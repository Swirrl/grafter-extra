(ns grafter.extra.validation.pmd.dataset-test
  (:require [clojure.test :refer :all]
            [grafter.extra.repository :refer [with-repository-containing]]
            [grafter.extra.validation.pmd.dataset :refer :all]))

(defn includes? [strings string]
  (some (partial re-find (re-pattern string)) strings))

(deftest errors-test
  (let [expected (list "is not a pmd:Dataset"
                       "is missing a pmd:graph"
                       "is missing a dcterms:title"
                       "is missing a rdfs:label"
                       "is missing a reference area dimension"
                       "is missing reference area levels"
                       "has dimension component without a codelist, e.g. http://example.org/ns#comp1"
                       "codelist is empty, e.g. http://example.org/ns#codelist2"
                       "has codes missing labels, e.g. http://example.org/ns#unlabelled-code"
                       "has units without labels")
        invalid (with-repository-containing [r "./test/resources/cube-bad.ttl"]
                  (distinct
                   (concat
                    (doall (errors r "http://example.org/ns#d1"))
                    (doall (errors r "http://example.org/ns#d2")))))
        valid (with-repository-containing [r "./test/resources/outdoor-visits.nt"]
                (doall (errors r "http://statistics.gov.scot/data/outdoor-visits")))]
    (doseq [message expected]
      (is (includes? invalid message))
      (is (not (includes? valid message))))))

(deftest omissions-test
  (let [expected (list "is missing a dcterms:modified")
        missing (with-repository-containing [r "./test/resources/cube-bad.ttl"]
                  (doall (omissions r "http://example.org/ns#dataset-le3")))
        present (with-repository-containing [r "./test/resources/outdoor-visits.nt"]
                  (doall (omissions r "http://statistics.gov.scot/data/outdoor-visits")))]
    (doseq [message expected]
      (is (includes? missing message))
      (is (not (includes? present message))))))
