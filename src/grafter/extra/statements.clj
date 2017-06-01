(ns grafter.extra.statements
  (:require [grafter.vocabularies.rdf :refer [rdf:a]]
            [grafter.rdf.protocols :refer [->Quad]]))

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

(defn bijection [{sm :s, pm :p, om :o, cm :c :as mapping}]
  "For transforming statements using a hash-map which specifies a mapping
   for part of the quad e.g.
   { :s { (->uri \"http://example/a\") (->uri \"http://example/A\") } }"
  (letfn [(map-with [m v] (if m (m v v) v))]
    (fn [[s p o c]]
      (->Quad (map-with sm s)
              (map-with pm p)
              (map-with om o)
              (map-with cm c)))))

(defn map-bijection [mapping statements]
  "Maps a sequence of statements into another sequence of the same length according to a bijection function."
  (map (bijection mapping) statements))

(defn isomorphic?
  "True if sequences are graph-isomorphic
   https://www.w3.org/TR/rdf11-concepts/#graph-isomorphism

   Arguments statements-a and statements-b are sequence of statements (Quads or Triples).

   An optional third argument specifies a mapping to translate nodes before comparison."
  ([statements-a statements-b]
   (= (set statements-a)
      (set statements-b)))
  ([statements-a statements-b bijection-mapping]
   (isomorphic? statements-a (map-bijection bijection-mapping statements-b))))
