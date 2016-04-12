(ns grafter.extra.validation.pmd.dataset
  (:require [grafter.extra.validation :refer [presence-checker]]
            [grafter.extra.sparql :as sparql]))

(defn template [filename]
  (sparql/template (str "validation/pmd/" filename)))

(defn checker [filename dataset-uri message]
  (presence-checker ((template filename) {:dataset-uri dataset-uri})
                    message))

(defn check-for-pmd-dataset [dataset-uri]
  (checker "ds-a-pmd-dataset.sparql" dataset-uri "is not a pmd:Dataset"))

(defn check-for-pmd-graph [dataset-uri]
  (checker "ds-has-pmd-graph.sparql" dataset-uri  "is missing a pmd:graph"))

(defn check-for-title [dataset-uri]
  (checker "ds-has-title.sparql" dataset-uri  "is missing a dcterms:title"))

(defn check-for-modified [dataset-uri]
  (checker "ds-has-modified.sparql" dataset-uri "is missing a dcterms:modified"))

(defn check-for-refarea [dataset-uri]
  (checker "ds-has-refarea.sparql" dataset-uri "is missing a reference area dimension"))

(defn check-for-codelists [dataset-uri]
  (checker "ds-has-codelists.sparql" dataset-uri "is missing codelists"))

(defn errors [repo dataset-uri]
  (let [apply-check (fn [check] ((check dataset-uri) repo))
        checks (list check-for-pmd-dataset
                     check-for-pmd-graph
                     check-for-title
                     check-for-refarea
                     check-for-codelists)]
    (->> checks (map apply-check) (remove nil?))))

(defn omissions [repo dataset-uri]
  (let [apply-check (fn [check] ((check dataset-uri) repo))
        checks (list check-for-modified)]
    (->> checks (map apply-check) (remove nil?))))

