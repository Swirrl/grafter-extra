(ns grafter.extra.validation.cube
  (:require
   [grafter.rdf.repository :refer [repo query ->connection]]))

(defn- ask [repository ask-query]
  (with-open [connection (->connection repository)]
    (let [result (query connection ask-query)]
      result)))

(defn cube-resource [filename]
  (slurp (str "./resources/validation/cube/" filename)))

(defn query-resource [filename]
  (str (cube-resource "prefixes.sparql")
       (cube-resource filename)))

(defn integrity-constraint [filename]
  "Returns a function that tests that a condition holds for the data in a
   given repository.

   Whereas the source ASK queries from the data-cube specification return
   true if the constraint is violated, these predicate functions will return
   true if the constraint holds (i.e. when the data is valid).

   Well-formed cube specification: https://www.w3.org/TR/vocab-data-cube/#wf"
  
  (fn [repository]
    (false? (ask repository (query-resource filename)))))

(def unique-dataset?
  "Every qb:Observation has exactly one associated qb:DataSet."
  (integrity-constraint "unique-dataset.sparql"))

(def unique-dsd?
  "Every qb:DataSet has exactly one associated qb:DataStructureDefinition."
  (integrity-constraint "unique-dsd.sparql"))
