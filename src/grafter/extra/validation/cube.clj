(ns grafter.extra.validation.cube
  (:require
   [clojure.java.io :as io]
   [grafter.rdf.repository :refer [repo query ->connection]]))

(defn- ask [repository ask-query]
  (with-open [connection (->connection repository)]
    (let [result (query connection ask-query)]
      result)))

(defn cube-resource [filename]
  (->> filename (str "validation/cube/") io/resource slurp))

(defn query-resource [filename]
  (str (cube-resource "prefixes.sparql")
       (cube-resource filename)))

(defn integrity-constraint [filename]
  "Returns a function that tests that a condition holds for the data in a
   given repository.

   Whereas the ASK queries from the data-cube specification return true if
   the constraint is violated, these predicate functions will return true if
   the constraint holds (i.e. when the data is valid).

   Well-formed cube specification: https://www.w3.org/TR/vocab-data-cube/#wf"
  
  (fn [repository]
    (false? (ask repository (query-resource filename)))))

(def unique-dataset?
  "Every qb:Observation has exactly one associated qb:DataSet."
  (integrity-constraint "unique-dataset.sparql"))

(def unique-dsd?
  "Every qb:DataSet has exactly one associated qb:DataStructureDefinition."
  (integrity-constraint "unique-dsd.sparql"))

(def all-constraints
  (list unique-dataset?
        unique-dsd?))

(defn well-formed-cube? [repository]
  "Returns true only if no integrity constraints are broken. This is not a
   sufficient test for cube validity: an empty repository will succeed (as it
   possesses no badly-formed cubes)."
  (every? true? ((apply juxt all-constraints) repository)))
