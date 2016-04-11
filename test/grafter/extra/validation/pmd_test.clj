(ns grafter.extra.validation.pmd-test
  (:require [clojure.test :refer :all]
            [grafter.extra.repository :refer [with-repository-containing]]
            [grafter.extra.validation.pmd :refer :all]))

(defn includes? [strings string]
  (some (partial re-find (re-pattern string)) strings))

(deftest errors-test
  (let [expected (list "cube vocabulary missing")
        invalid (with-repository-containing [r "./test/resources/cube-bad.ttl"]
                  (doall (errors r)))
        valid (with-repository-containing [r "./test/resources/sdmx-dimension.ttl"]
                (doall (errors r)))]
    (doseq [message expected]
      (is (includes? invalid message))
      (is (not (includes? valid message))))))

