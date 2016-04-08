(ns grafter.extra.validation.pmd
  (:require [grafter.extra.validation :refer [presence-checker]]
            [grafter.extra.sparql :as sparql]))

(defn add-error [errors error]
  (conj errors error))

(defn template [filename]
  (sparql/template (str "validation/pmd/" filename)))

(defn check-for-pmd-dataset [dataset-uri]
  (presence-checker ((template "ds-a-pmd-dataset.sparql") {:dataset-uri dataset-uri})
                    "is not a pmd:Dataset"))

(defn errors [repo dataset-uri]
  (let [apply-check (fn [check] ((check dataset-uri) repo))
        checks (list check-for-pmd-dataset)]
    (->> checks (map apply-check) (remove nil?))))


;;(defn add-omission [omissions omission])

;;(defn omissions [repo dataset-uri])

