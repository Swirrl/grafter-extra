(ns grafter.extra.datasets
  (:require [clojure.string :as st]
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
