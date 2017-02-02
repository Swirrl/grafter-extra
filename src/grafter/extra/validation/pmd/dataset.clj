(ns grafter.extra.validation.pmd.dataset
  (:require [grafter.extra.validation :refer [presence-checker
                                              absence-checker
                                              example-finder]]
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

(defn check-for-label [dataset-uri]
  (checker "ds-has-label.sparql" dataset-uri  "is missing a rdfs:label"))

(defn check-for-modified [dataset-uri]
  (checker "ds-has-modified.sparql" dataset-uri "is missing a dcterms:modified"))

(defn check-for-structure [dataset-uri]
  (checker "ds-has-structure.sparql" dataset-uri "is missing a qb:DataStructureDefinition"))

(defn check-for-refarea [dataset-uri]
  (checker "ds-has-refarea.sparql" dataset-uri "is missing a reference area dimension"))

(defn check-for-area-level [dataset-uri]
  (absence-checker ((template "ds-has-area-levels.sparql") {:dataset-uri dataset-uri})
                   "is missing reference area levels"))

(defn check-for-code-lists [dataset-uri]
  (absence-checker ((template "ds-has-dimensions-without-codelists.sparql") {:dataset-uri dataset-uri})
                   "is missing codelists"))

(defn check-for-code-labels [dataset-uri]
  (example-finder ((template "ds-codes-without-labels.sparql") {:dataset-uri dataset-uri})
                  "has codes missing labels"))

(defn check-for-measurement-unit-labels [dataset-uri]
  (absence-checker ((template "ds-units-without-labels.sparql") {:dataset-uri dataset-uri})
                   "has units without labels"))

(defn errors [repo dataset-uri]
  (let [apply-check (fn [check] ((check dataset-uri) repo))
        checks (list check-for-pmd-dataset
                     check-for-pmd-graph
                     check-for-title
                     check-for-label
                     check-for-structure
                     check-for-refarea
                     check-for-code-lists
                     check-for-code-labels
                     check-for-measurement-unit-labels
                     check-for-area-level)]
    (->> checks (map apply-check) (remove nil?))))

(defn omissions [repo dataset-uri]
  (let [apply-check (fn [check] ((check dataset-uri) repo))
        checks (list check-for-modified)]
    (->> checks (map apply-check) (remove nil?))))

