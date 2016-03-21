(ns grafter.extra.validation.cube-test
  (:require [clojure.test :refer :all]
            [grafter.rdf.repository :refer [repo]]
            [grafter.extra.repository :refer [with-repository-containing]]
            [grafter.extra.validation.cube :refer :all]))

(deftest assert-well-formed-test
  (testing "Nothing thrown with well-formed cubes"
    (with-repository-containing [r "./test/resources/cube.ttl"]
      (assert-well-formed r)))

  (testing "Errors thrown with badly-formed cube"
    (with-repository-containing [r "./test/resources/cube-bad.ttl"]
      (is (thrown-with-msg? AssertionError
                            #"Malformed Cube"
                            (assert-well-formed r))))))
