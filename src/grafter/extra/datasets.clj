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

(defn row-bind [& datasets]
  { :pre [(apply = (map column-names datasets))]}
  "Combine datasets by appending rows. Column names must match."
  (let [headers (column-names (first datasets))
        metas (apply merge (map meta datasets))
        rows (mapcat :rows datasets)]
    (with-meta
      (make-dataset rows headers)
      metas)))

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
        trim-in-row (fn [row] (->> row (apply concat) (map trim-if-string) (apply hash-map)))
        original-meta (meta dataset)]
    (-> (make-dataset (->> dataset :rows (map trim-in-row))
                      (map trim-if-string (column-names dataset)))
        (with-meta original-meta))))

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
  (let [original-meta (meta dataset)]
    (-> (make-dataset (-> dataset :rows row-transformer)
                      (column-names dataset))
        (with-meta original-meta))))

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

(defn matrix-product [dataset-a dataset-b]
  "Creates a dataset that is the product of two datasets (i.e. merge without a common key)"
  (let [columns-a (column-names dataset-a)
        columns-b (column-names dataset-b)
        columns-common (filter (set columns-a) columns-b)]
    (if (empty? columns-common)
      (let [headers (concat columns-a columns-b)]
        (make-dataset (mapcat
                       (fn [row-a] (map
                                    (fn [row-b] (merge row-a row-b))
                                    (:rows dataset-b)))
                       (:rows dataset-a))
                      headers))
      (throw (RuntimeException. (str "Can't calculate matrix-product for incompatible datasets - common columns: " (st/join ", " columns-common)))))))

(defn cast-dataset [dataset id-variables column-variables value-variable aggregate-fn]
  "Cast dataset/ create pivot table. Reverses melt."
  (letfn [(row-key [r] "create a key to identify the row"
            (map r id-variables))
          (collect-vals [or nr] "pivot creating headers for column-variable values"
            (let [value (nr value-variable)]
              (if (nil? or) ;; use new row to create or update old-hash results 
                (into {} (for [[k v] nr]
                           (if (contains? (set column-variables) k) ;; pivot on column-variable
                             {v (vector value)}
                             (if (contains? (set id-variables) k) ;; duplicate id
                                 {k v}))))
                (reduce-kv (fn [acc k v] (if (contains? (set column-variables) k) ;; pivot on column-variable or return accumulator
                                         (update acc v (fn [vs] (conj (or vs []) value)))
                                         acc))
                           or nr))))
          (aggregate-vals [m] "collapse values for each new header to single aggregate"
            (into {} (for [[k v] m] {k (if (vector? v) (aggregate-fn v) v)})))
          (collect-cols [oc r] "gather new headers"
            (clojure.set/union oc (set (map r column-variables))))]
    (loop [rows-seq (:rows dataset)
           rows-hash {}
           columns #{}]
      (if (seq rows-seq)
        (let [row (first rows-seq)]
          (recur (rest rows-seq) (update rows-hash (row-key row) collect-vals row) (collect-cols columns row)))
        (make-dataset (->> rows-hash vals (map aggregate-vals))
                      (into id-variables columns))))))
