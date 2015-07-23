(ns grafter.extra.statements-test
  (:require [clojure.test :refer :all]
            [grafter.rdf.protocols :refer [->Quad]]
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
