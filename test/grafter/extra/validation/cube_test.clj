(ns grafter.extra.validation.cube-test
  (:require [clojure.test :refer :all]
            [grafter.extra.repository :refer [with-repository-containing
                                              load-contents]]
            [grafter.extra.validation.cube :refer :all]))

(deftest assert-well-formed-test
  (testing "Nothing thrown with well-formed cubes"
    (with-repository-containing [r "./test/resources/cube.ttl"]
      (load-contents r "./test/resources/sdmx-dimension.ttl")
      (normalise-cube r)
      (assert-well-formed r)))

  (testing "Errors thrown with badly-formed cube"
    (with-repository-containing [r "./test/resources/cube-bad.ttl"]
      (normalise-cube r)
      (is (thrown-with-msg? AssertionError
                            #"Malformed Cube"
                            (assert-well-formed r))))))

(deftest errors-test
  (testing "Nothing thrown with well-formed cubes"
    (with-repository-containing [r "./test/resources/cube.ttl"]
      (load-contents r "./test/resources/sdmx-dimension.ttl")
      (normalise-cube r)
      (is (empty? (errors r))))))

(testing "Errors thrown with badly-formed cube"
  (with-repository-containing [r "./test/resources/cube-bad.ttl"]
    (normalise-cube r)
    (is (= '("Malformed Cube. Every dimension declared in a qb:DataStructureDefinition must have a declared rdfs:range. , e.g. http://example.org/ns#dim1"
             "Malformed Cube. Every qb:Observation has a value for each dimension declared in its associated qb:DataStructureDefinition., e.g. http://example.org/ns#dim1"
             "Malformed Cube. Every qb:Observation has exactly one associated qb:DataSet., e.g. http://example.org/ns#o1"))
        (errors r))))
