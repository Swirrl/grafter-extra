(ns grafter.extra.repository-test
  (:require [clojure.test :refer :all]
            [grafter.extra.repository :refer [with-repository]]
            [grafter.rdf.repository :refer [repo query]]
            [grafter.rdf.protocols :refer [->Quad add]]))

(deftest with-repository-test
  (testing "binds a repository"
    (with-repository [test-repo (repo)]
      (let [input (->Quad "http://s" "http://p" "http://o" "http://g")]
        (add test-repo input)
        (let [output (query test-repo "SELECT * WHERE { ?s ?p ?o }")]
          (is (= 1
                 (count output))))))))
