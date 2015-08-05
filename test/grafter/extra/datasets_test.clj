(ns grafter.extra.datasets-test
  (:require [clojure.test :refer :all]
            [grafter.tabular :refer [make-dataset]]
            [grafter.extra.datasets :refer :all]))

(deftest datasets-match-test
  (testing "Compares rows regardless of order"
    (let [dataset-a (make-dataset [[1] [2] [3]] [:a])
          dataset-b (make-dataset [[1] [3] [2]] [:a])
          dataset-c (make-dataset [[0] [0] [0]] [:a])]
      (is (datasets-match? dataset-a dataset-b))
      (is (not (datasets-match? dataset-a dataset-c))))))

(deftest trim-all-strings-test
  (testing "Cleans whitespace"
    (let [dirty    (make-dataset [[" left" "right " " both "]] [" really" "dirty " " whitespace "])
          expected (make-dataset [[ "left" "right"   "both" ]] [ "really" "dirty"   "whitespace" ])
          cleaned  (trim-all-strings dirty)]
      (is (= cleaned expected)))))
