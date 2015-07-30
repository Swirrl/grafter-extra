(ns grafter.extra.repository-test
  (:require [clojure.test :refer :all]
            [grafter.extra.repository :refer [with-repository]]
            [grafter.rdf.repository :refer [repo query ->connection]]
            [grafter.rdf.protocols :refer [->Quad add]])
  (:import [org.openrdf.repository RepositoryConnection]))

(deftest with-repository-test
  (testing "binds a repository"
    (with-repository [test-repo (repo)]
      (with-open [connection (->connection test-repo)]
        (let [input (->Quad "http://s" "http://p" "http://o" "http://g")]
          (add connection input)
          (let [output (query connection "SELECT * WHERE { ?s ?p ?o }")]
            (is (= 1
                   (count output)))))))))
