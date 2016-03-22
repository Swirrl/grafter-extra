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

(def assert-slice-key-consistent
  (constraint-asserter
   "slice-key-consistent.sparql"
   "Every qb:componentProperty on a qb:SliceKey must also be declared as a qb:component of the associated qb:DataStructureDefinition."))

(def assert-unique-slice-structure
  (constraint-asserter
   "unique-slice-structure.sparql"
   "Each qb:Slice must have exactly one associated qb:sliceStructure."))

(def assert-slice-dimensions-complete
  (constraint-asserter
   "slice-dimensions-complete.sparql"
   "Every qb:Slice must have a value for every dimension declared in its qb:sliceStructure."))

(def assert-all-dimensions-required
  (constraint-asserter
   "all-dimensions-required.sparql"
   "Every qb:Observation has a value for each dimension declared in its associated qb:DataStructureDefinition."))

(def assert-no-duplicate-observations
  (constraint-asserter
   "no-duplicate-observations.sparql"
   " No two qb:Observations in the same qb:DataSet may have the same value for all dimensions."))

(def assert-required-attributes
  (constraint-asserter
   "required-attributes.sparql"
   "Every qb:Observation has a value for each declared attribute that is marked as required."))

(def assert-all-measures-present
  (constraint-asserter
   "all-measures-present.sparql"
   "In a qb:DataSet which does not use a Measure dimension then each individual qb:Observation must have a value for every declared measure."))

(def assert-measures-dimension-consistent
  (constraint-asserter
   "measures-dimension-consistent.sparql"
   "In a qb:DataSet which uses a Measure dimension then each qb:Observation must have a value for the measure corresponding to its given qb:measureType."))

(def assert-single-measure-on-measure-dimension-observation
  (constraint-asserter
   "single-measure-on-measure-dimension-observation.sparql"
   "In a qb:DataSet which uses a Measure dimension then each qb:Observation must only have a value for one measure (by IC-15 this will be the measure corresponding to its qb:measureType)."))

(def assert-all-measures-present-in-measures-dimension-cube
  (constraint-asserter
   "all-measures-present-in-measures-dimension-cube.sparql"
   "In a qb:DataSet which uses a Measure dimension then if there is a Observation for some combination of non-measure dimensions then there must be other Observations with the same non-measure dimension values for each of the declared measures."))

(def assert-consistent-dataset-links
  (constraint-asserter
   "consistent-dataset-links.sparql"
   "If a qb:DataSet D has a qb:slice S, and S has an qb:observation O, then the qb:dataSet corresponding to O must be D."))

(def assert-codes-from-codelist-concept-scheme
  (constraint-asserter
   "codes-from-codelist-concept-scheme.sparql"
   "If a dimension property has a qb:codeList, then the value of the dimension property on every qb:Observation must be in the code list."))

(def assert-codes-from-codelist-collection
  (constraint-asserter
   "codes-from-codelist-collection.sparql"
   "If a dimension property has a qb:codeList, then the value of the dimension property on every qb:Observation must be in the code list."))

;; not yet implmented tests for "Codes from hierarchy" and
;; "Codes from hierarchy (inverse)" as the ask queries need to be parametised

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
                          assert-slice-key-declared
                          assert-slice-key-consistent
                          assert-unique-slice-structure
                          assert-slice-dimensions-complete
                          assert-all-dimensions-required
                          assert-no-duplicate-observations
                          assert-all-measures-present
                          assert-measures-dimension-consistent
                          assert-single-measure-on-measure-dimension-observation
                          assert-all-measures-present-in-measures-dimension-cube
                          assert-consistent-dataset-links
                          assert-codes-from-codelist-concept-scheme
                          assert-codes-from-codelist-collection)]
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
