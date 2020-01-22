(ns grafter.extra.repository-test
  (:require [clojure.test :refer :all]
            [grafter.extra.repository :refer :all]
            [grafter-2.rdf4j.repository :refer [query ->connection]]
            [grafter-2.rdf.protocols :refer [->Quad add]]
            [grafter-2.rdf4j.repository :as repo])
  (:import [java.net URI]))

(def test-quad
  (apply ->Quad (map #(URI. %) '("http://s" "http://p" "http://o" "http://g"))))

(deftest with-repository-test
  (testing "binds a repository"
    (with-repository [test-repo (repo/sail-repo)]
      (with-open [connection (->connection test-repo)]
        (let [input test-quad]
          (add connection input)
          (let [output (query connection "SELECT * WHERE { ?s ?p ?o }")]
            (is (= 1
                   (count output)))))))))

(deftest with-repository-containing-test
  (testing "binds an in-memory repository, adding data to it"
    (letfn [(repo-loaded-with? [data]
              (with-repository-containing [test-repo data]
                (with-open [connection (->connection test-repo)]
                  (let [anything-there? "ASK { ?s ?p ?o }"]
                    (query connection anything-there?)))))]

      (testing "from a sequence of statements"
        (let [data (list test-quad)]
          (is (repo-loaded-with? data))))

      (testing "from a file"
        (let [data "./test/resources/example.ttl"]
          (is (repo-loaded-with? data)))))))
