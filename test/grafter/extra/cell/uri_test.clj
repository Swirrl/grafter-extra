(ns grafter.extra.cell.uri-test
  (:require [clojure.test :refer :all]
            [grafter.extra.cell.uri :refer :all]))

(deftest slugize-test
  (testing "slugize"
    (is (= (slugize "/path/to/folder")
           "/path/to/folder"))))
