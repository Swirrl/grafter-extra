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

(defn constraint-asserter [sparql-fname message]
  "Returns a function for testing that a condition holds for the data in a given
   repository, throwing an AssertionError if it is violated.

   Well-formed cube specification: https://www.w3.org/TR/vocab-data-cube/#wf"
  
  (fn [repository]
    (if (ask repository (query-resource sparql-fname))
      (throw (AssertionError. (str "Malformed Cube. Constraint violated: " message))))))

(def assert-unique-dataset
  (constraint-asserter
   "unique-dataset.sparql"
   "Every qb:Observation has exactly one associated qb:DataSet."))

(def assert-unique-dsd
  (constraint-asserter
   "unique-dsd.sparql"
   "Every qb:DataSet has exactly one associated qb:DataStructureDefinition."))

(defn assert-well-formed [repository]
  "Asserts each cube integrity constraints in sequence, throwing at first
   violation.

   This is not a sufficient test for cube validity: an empty repository will
   succeed (as it possesses no badly-formed cubes)."
  (doseq [assertion (list assert-unique-dataset
                        assert-unique-dsd)]
    (assertion repository)))
