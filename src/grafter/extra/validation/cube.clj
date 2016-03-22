(ns grafter.extra.validation.cube
  (:require
   [clojure.java.io :as io]
   [grafter.rdf.repository :refer [repo ->connection query]]
   [grafter.rdf.protocols :refer [update!]]))


;; Well-formed cubes
;; https://www.w3.org/TR/vocab-data-cube/#wf

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

(def assert-dsd-includes-measures
  (constraint-asserter
   "dsd-includes-measures.sparql"
   "Every qb:DataStructureDefinition must include at least one declared measure."))

(def assert-dimensions-have-range
  (constraint-asserter
   "dimensions-have-range.sparql"
   "Every dimension declared in a qb:DataStructureDefinition must have a declared rdfs:range. "))

(def assert-concept-dimensions-have-codelists
  (constraint-asserter
   "concept-dimensions-have-codelists.sparql"
   "Every dimension with range skos:Concept must have a qb:codeList."))

(def assert-only-attributes-optional
  (constraint-asserter
   "only-attributes-optional.sparql"
   "The only components of a qb:DataStructureDefinition that may be marked as optional, using qb:componentRequired are attributes."))

(def assert-slice-key-declared
  (constraint-asserter
   "slice-key-declared.sparql"
   "Every qb:SliceKey must be associated with a qb:DataStructureDefinition."))

(defn assert-well-formed [repository]
  "Asserts each cube integrity constraints in sequence, throwing at first
   violation.

   This is not a sufficient test for cube validity: an empty repository will
   succeed (as it possesses no badly-formed cubes)."
  (doseq [assertion (list assert-unique-dataset
                          assert-unique-dsd
                          assert-dsd-includes-measures
                          assert-dimensions-have-range
                          assert-concept-dimensions-have-codelists
                          assert-only-attributes-optional
                          assert-slice-key-declared)]
    (assertion repository)))


;; Abbreviated and Normalised data cubes
;; https://www.w3.org/TR/vocab-data-cube/#h2_normalize

(defn- update [repository update-query]
  (with-open [connection (->connection repository)]
    (let [result (update! connection update-query)]
      result)))

(defn normalise-cube [repository]
  (doseq [update-qry ["type-and-property-closure.sparql"
                      "push-down-attachment-levels.sparql"]]
    (update repository (cube-resource update-qry))))
