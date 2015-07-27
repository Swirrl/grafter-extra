(ns grafter.extra.datasets)

(defn row-set [dataset]
  (-> dataset :rows set))

(defn datasets-match? [dataset-a dataset-b]
  (let [row-set-a (row-set dataset-a)
        row-set-b (row-set dataset-b)]
    (= row-set-a row-set-b)))
