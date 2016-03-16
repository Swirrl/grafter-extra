(ns grafter.extra.statements
  (:require [grafter.vocabularies.rdf :refer [rdf:a]]))

(defn statements-where [attr-map statements]
  "Filters a sequence of statements according to an attribute map

  e.g. To extract statements describing observations

  (statements-where {:p rdf:a, :o qb:Observation} quads)"

  (filter (fn [quad]
            (every? (fn [[part value]] (= (get quad part) value))
                    attr-map))
          statements))

(defn quad-is-a? [rdf-type]
  "Matches quads with `?s a <rdf-type>`"
  (fn [[s p o g]]
    (and (= p rdf:a)
         (= o rdf-type))))

(defn unique-uris-of-type [rdf-type statements]
  "Finds unique subjects where `?s a <rdf-type>`"
  (->> statements
       (filter (quad-is-a? rdf-type))
       (map :s)
       distinct))

(defn count-of-type [rdf-type statements]
  "Counts unique subjects where `?s a <rdf-type>`"
  (count (unique-uris-of-type rdf-type statements)))
