(ns grafter.extra.datasets
  (:require [clojure.string :as st]
            [grafter.sequences :as seqs]
            [grafter.tabular :refer [make-dataset column-names]]))

(defn row-set [dataset]
  (-> dataset :rows set))

(defn datasets-match? [dataset-a dataset-b]
  (let [row-set-a (row-set dataset-a)
        row-set-b (row-set dataset-b)]
    (= row-set-a row-set-b)))

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
