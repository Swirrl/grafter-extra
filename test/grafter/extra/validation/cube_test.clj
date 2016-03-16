(ns grafter.extra.validation.cube-test
  (:require [clojure.test :refer :all]
            [grafter.rdf.repository :refer [repo]]
            [grafter.extra.repository :refer [with-repository-containing]]
            [grafter.extra.validation.cube :refer :all]))

(deftest well-formed-cube-test
  (testing "can be executed"
    (with-repository-containing [r "./test/resources/cube.ttl"]
      (is (true? (well-formed-cube? r))))))
