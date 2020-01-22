(ns grafter.extra.statements-test
  (:require [clojure.test :refer :all]
            [grafter-2.rdf.protocols :refer [->Quad]]
            [grafter.extra.statements :refer :all]
            [grafter.vocabularies.rdf :refer :all]))

(deftest statements-where-test
  (testing "Filters sequences to the right number of results"
    (let [quads (map (partial apply ->Quad) [["http://example.com/taste" rdf:a rdf:Property "http://example.com/graph"]
                                             ["http://example.com/taste" rdfs:subPropertyOf "http://example.com/sense" "http://example.com/graph"]
                                             ["http://example.com/smell" rdf:a rdf:Property "http://example.com/graph"]
                                             ["http://example.com/smell" rdfs:subPropertyOf "http://example.com/sense" "http://example.com/graph"]])]
      (is (= 2
             (count (statements-where {:p rdf:a, :o rdf:Property} quads))))

      (is (= 2
             (count (statements-where {:s "http://example.com/taste"} quads))))

      (is (= 4
             (count (statements-where {:c "http://example.com/graph"} quads))))

      (is (= 0
             (count (statements-where {:s "http://example.com/sight"} quads)))))))

(def eg-uri (partial str "http://eg.net/"))

(defn- make-quads [quad-slugs]
  (->> quad-slugs
       (map (fn [slugs] (map eg-uri slugs)))
       (map (partial apply ->Quad))))

(deftest bijection-test
  (testing "Translate quads"
    (let [quad-a (->Quad (eg-uri "s") (eg-uri "p") (eg-uri "o") (eg-uri "c"))
          quad-b (->Quad (eg-uri "S") (eg-uri "p") (eg-uri "o") (eg-uri "C"))]
      (is (= quad-a
             ((bijection {:s {(eg-uri "S") (eg-uri "s")}
                          :c {(eg-uri "C") (eg-uri "c")}}) quad-b))))))

(deftest isomorphism-test
  (let [sequence-a (make-quads [["s1" "p1" "o1" "c1"]
                                ["s2" "p2" "o2" "c2"]])
        sequence-b (make-quads [["XX" "p1" "o1" "c1"]
                                ["s2" "p2" "o2" "c2"]])
        a->b {:s {(eg-uri "XX") (eg-uri "s1")}}]
    (testing "matches self"
      (is (isomorphic? sequence-a sequence-a)))
    (testing "matches reverse"
      (is (isomorphic? sequence-a (reverse sequence-a))))
    (testing "doesn't match different sequence"
      (is (not (isomorphic? sequence-a sequence-b))))
    (testing "matches with bijection"
      (is (isomorphic? sequence-a sequence-b a->b)))))
