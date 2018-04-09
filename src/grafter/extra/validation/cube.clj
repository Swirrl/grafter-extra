(ns grafter.extra.validation.cube
  (:require [clojure.java.io :as io]
            [grafter.extra.validation :refer [absence-asserter example-finder]]
            [grafter.rdf.protocols :refer [update!]]
            [grafter.rdf4j.repository :refer [->connection]]))

(defn cube-resource [filename]
  (->> filename (str "validation/cube/") io/resource slurp))

(defn ask-query [filename]
  (str (cube-resource "prefixes.sparql")
       (cube-resource (str "ask/" filename))))

(defn select-query [filename]
  (str (cube-resource "prefixes.sparql")
       (cube-resource (str "select/" filename))))


;; Abbreviated and Normalised data cubes
;; https://www.w3.org/TR/vocab-data-cube/#h2_normalize
(defn normalise-cube [repository]
  (with-open [connection (->connection repository)]
    (doseq [update-query ["normalise/type-and-property-closure.sparql"
                          "normalise/push-down-attachment-levels.sparql"]]
      (update! connection (cube-resource update-query)))))


;; Well-formed cubes
;; https://www.w3.org/TR/vocab-data-cube/#wf
(def query->message
  {"unique-dataset.sparql"
   "Every qb:Observation has exactly one associated qb:DataSet."

   "unique-dsd.sparql"
   "Every qb:DataSet has exactly one associated qb:DataStructureDefinition."

   "dsd-includes-measures.sparql"
   "Every qb:DataStructureDefinition must include at least one declared measure."

   "dimensions-have-range.sparql"
   "Every dimension declared in a qb:DataStructureDefinition must have a declared rdfs:range. "

   "concept-dimensions-have-codelists.sparql"
   "Every dimension with range skos:Concept must have a qb:codeList."

   "only-attributes-optional.sparql"
   "The only components of a qb:DataStructureDefinition that may be marked as optional, using qb:componentRequired are attributes."

   "slice-key-declared.sparql"
   "Every qb:SliceKey must be associated with a qb:DataStructureDefinition."

   "slice-key-consistent.sparql"
   "Every qb:componentProperty on a qb:SliceKey must also be declared as a qb:component of the associated qb:DataStructureDefinition."

   "unique-slice-structure.sparql"
   "Each qb:Slice must have exactly one associated qb:sliceStructure."

   "slice-dimensions-complete.sparql"
   "Every qb:Slice must have a value for every dimension declared in its qb:sliceStructure."

   "all-dimensions-required.sparql"
   "Every qb:Observation has a value for each dimension declared in its associated qb:DataStructureDefinition."

   "no-duplicate-observations.sparql"
   " No two qb:Observations in the same qb:DataSet may have the same value for all dimensions."

   "required-attributes.sparql"
   "Every qb:Observation has a value for each declared attribute that is marked as required."

   "all-measures-present.sparql"
   "In a qb:DataSet which does not use a Measure dimension then each individual qb:Observation must have a value for every declared measure."

   "measures-dimension-consistent.sparql"
   "In a qb:DataSet which uses a Measure dimension then each qb:Observation must have a value for the measure corresponding to its given qb:measureType."

   "single-measure-on-measure-dimension-observation.sparql"
   "In a qb:DataSet which uses a Measure dimension then each qb:Observation must only have a value for one measure (by IC-15 this will be the measure corresponding to its qb:measureType)."

   "all-measures-present-in-measures-dimension-cube.sparql"
   "In a qb:DataSet which uses a Measure dimension then if there is a Observation for some combination of non-measure dimensions then there must be other Observations with the same non-measure dimension values for each of the declared measures."

   "consistent-dataset-links.sparql"
   "If a qb:DataSet D has a qb:slice S, and S has an qb:observation O, then the qb:dataSet corresponding to O must be D."

   "codes-from-codelist-concept-scheme.sparql"
   "If a dimension property has a qb:codeList, then the value of the dimension property on every qb:Observation must be in the code list."

   "codes-from-codelist-collection.sparql"
   "If a dimension property has a qb:codeList, then the value of the dimension property on every qb:Observation must be in the code list."})

;; not yet implmented tests for "Codes from hierarchy" and
;; "Codes from hierarchy (inverse)" as the ask queries need to be parametised



(defn cube-asserter [filename message]
  (absence-asserter (ask-query filename)
                    (str "Malformed Cube. " message)))

(defn cube-example-finder [filename message]
  (example-finder (select-query filename) (str "Malformed Cube. " message)))

(defn assert-well-formed [repository]
  "Asserts each cube integrity constraints in sequence, throwing at first
   violation.

   This is not a sufficient test for cube validity: an empty repository will
   succeed (as it possesses no badly-formed cubes).

   Well-formed cube specification: https://www.w3.org/TR/vocab-data-cube/#wf."
  (doseq [[qry msg] query->message]
    ((cube-asserter qry msg) repository)))

(defn errors [repository]
  "Validate that any cube resources are well-formed returning an error message for
   each constraint that fails."
  (doall
   (remove nil?
           (for [[qry msg] query->message]
             ((cube-example-finder qry msg) repository)))))
