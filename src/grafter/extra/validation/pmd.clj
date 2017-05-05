(ns grafter.extra.validation.pmd
  (:require [clojure.java.io :as io]
            [grafter.extra.validation :refer [example-finder presence-checker]]))

(defn query-resource [filename]
  (->> filename (str "validation/pmd/") io/resource))

(defn check-for-cube-vocab [repo]
  ((presence-checker (slurp (query-resource "has-cube-vocabulary.sparql")) "cube vocabulary missing") repo))

(defn check-for-graph-with-multiple-vocabs [repo]
  ((example-finder (slurp (query-resource "graph-with-multiple-vocabularies.sparql")) "graph containing more than one vocabulary") repo))

(defn errors [repo]
  (let [checks (list check-for-cube-vocab
                     check-for-graph-with-multiple-vocabs)]
    (->> checks (map #(% repo)) (remove nil?))))
