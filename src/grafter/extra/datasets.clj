(ns grafter.extra.datasets
  (:require [clojure.string :as st]
            [grafter.sequences :as seqs]
            [grafter.tabular :refer [make-dataset column-names columns]]
            [incanter.core :refer [nrow]]))

(defn row-set [dataset]
  (-> dataset :rows set))

(defn datasets-match? [dataset-a dataset-b]
  (let [row-set-a (row-set dataset-a)
        row-set-b (row-set dataset-b)]
    (= row-set-a row-set-b)))

(defn- all-identical? [coll]
  (apply = (partition 2 1 coll)))

(defn row-bind [& datasets]
  "Combine datasets by appending rows. Column names must match."
  { :pre [(all-identical? (map column-names datasets))]}
  (let [headers (column-names (first datasets))
        rows    (mapcat :rows datasets)]
    (with-meta
      (make-dataset rows headers)
      (apply merge (map meta datasets)))))

(defn column-bind [& datasets]
  "Combine datasets by appending columns. Row lengths must match."
  (let [column-lengths (->> datasets (map nrow) distinct)]
    (if (not= 1 (count column-lengths))
      (throw (RuntimeException. (str "Can't column-bind datasets with different column-lengths (i.e. row counts): " (st/join ", " column-lengths))))
      (let [all-columns (mapcat column-names datasets)
            merged-rows (->> datasets
                             (map :rows)
                             (apply interleave)
                             (partition (count datasets))
                             (map (partial apply merge)))]
        (make-dataset merged-rows all-columns)))))

(defn trim-all-strings [dataset]
  "Removes leading and trailing whitespace from every string in the dataset.
   Includes column headers as well as cell values."
  (let [trim-if-string (fn [value] (if (string? value) (st/trim value) value))
        trim-in-row (fn [row] (->> row (apply concat) (map trim-if-string) (apply hash-map)))]
    (-> (make-dataset (->> dataset :rows (map trim-in-row))
                      (map trim-if-string (column-names dataset)))
        (with-meta (meta dataset)))))

(defn ensure-presence-of [dataset column]
  (if (some #(= column %) (column-names dataset))
    (let [na-rows (->> dataset
                       :rows
                       (filter #(nil? (% column))))]
      (if (not-empty na-rows)
        (throw (RuntimeException. (str "Some rows have no value for column '" column "' e.g. " (first na-rows)))))
      dataset)
    (throw (RuntimeException. (str "Column " column " is missing from the dataset column headers.")))))

(defn revert-header-to-first-row [dataset]
  "Not for use with make-dataset. For modifying datasets that are fed into
   pipelines that already include a (make-dataset move-first-row-to-header) step."
  (let [old-rows (-> dataset :rows)
        new-rows (cons (-> old-rows first keys) (map vals old-rows))
        headers (take (-> old-rows first count) (seqs/alphabetical-column-names))]
    [headers new-rows]))

(defn select-columns [dataset selector-fn]
  "Select columns using a function"
  (let [selected-columns (filter selector-fn (column-names dataset))]
    (if (empty? selected-columns)
      (throw (RuntimeException. "No columns selected"))
      (columns dataset selected-columns))))

(defn transform-rows [dataset row-transformer]
  (-> (make-dataset (-> dataset :rows row-transformer)
                    (column-names dataset))
      (with-meta (meta dataset))))

(defn unique-rows [dataset]
  "Eagerly de-duplicates the dataset. Useful for building smaller files of triples.
  If you want to stay lazy then you could let the triplestore de-dupe."
  (transform-rows dataset distinct))

(defn drop-where [dataset pred column-or-columns]
  (if (sequential? column-or-columns)
    (letfn [(drop-where-reverse [pred column dataset] (drop-where dataset pred column))]
      ((apply comp (for [column column-or-columns]
                     (partial drop-where-reverse pred column))) dataset))
    (transform-rows dataset (fn [rows] (remove #(pred (% column-or-columns)) rows)))))

(defn take-where [dataset pred column]
  (transform-rows dataset (fn [rows] (filter #(pred (% column)) rows))))

(defn mapcat-rows [dataset f]
  (transform-rows dataset (fn [rows] (mapcat f rows))))
