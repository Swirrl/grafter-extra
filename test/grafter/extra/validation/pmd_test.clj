(ns grafter.extra.validation.pmd-test
  (:require [clojure.test :refer :all]
            [grafter-2.rdf4j.io :refer [statements]]
            [grafter.extra.repository :refer [with-repository-containing]]
            [grafter.extra.validation.pmd :refer :all]))

(defn includes? [strings string]
  (some (partial re-find (re-pattern string)) strings))

(deftest errors-test
  (let [expected (list "cube vocabulary missing"
                       "graph containing more than one vocabulary, e.g. http://www.example.org/graph1")
        invalid (with-repository-containing [r (mapcat statements ["./test/resources/cube-bad.ttl"
                                                                   "./test/resources/graph-bad.trig"])]
                  (doall (errors r)))
        valid (with-repository-containing [r (mapcat statements ["./test/resources/sdmx-dimension.ttl"
                                                                 "./test/resources/graphs-good.trig"])]
                (doall (errors r)))]
    (doseq [message expected]
      (is (includes? invalid message))
      (is (not (includes? valid message))))))
