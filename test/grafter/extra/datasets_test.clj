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

(deftest ensure-presence-of-test
  (let [hubbard (make-dataset [[:cupboard nil] [:baker :bread]] [:place :contents])]
    (testing "Throws if column is missing"
      (is (thrown-with-msg? RuntimeException #"Column :dog-status is missing"
                   (ensure-presence-of hubbard :dog-status))))
    (testing "Throws if column has blanks"
      (is (thrown? RuntimeException #"no value for column ':contents'"
                   (ensure-presence-of hubbard :contents))))
    (testing "Passes when column is present and complete"
      (ensure-presence-of hubbard :place))))
