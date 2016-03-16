(ns grafter.extra.validation.cube-test
  (:require [clojure.test :refer :all]
            [grafter.rdf.repository :refer [repo]]
            [grafter.extra.repository :refer [with-repository-containing]]
            [grafter.extra.validation.cube :refer :all]))

(deftest unique-dataset-test
  (testing "can be executed"
    (with-repository-containing [r "./test/resources/cube.ttl"]
      (is (true? (unique-dataset? r))))))
