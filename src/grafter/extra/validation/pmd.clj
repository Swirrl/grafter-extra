(ns grafter.extra.validation.pmd
  (:require [clojure.java.io :as io]
            [grafter.extra.validation :refer [presence-checker]]
            [grafter.extra.sparql :as sparql]))

(defn query-resource [filename]
  (->> filename (str "validation/pmd/") io/resource))

(defn check-for-cube-vocab [repo]
  ((presence-checker (slurp (query-resource "has-cube-vocabulary.sparql")) "cube vocabulary missing") repo))

(defn errors [repo]
  (let [checks (list check-for-cube-vocab)]
    (->> checks (map #(% repo)) (remove nil?))))
