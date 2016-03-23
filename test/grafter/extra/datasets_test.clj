(ns grafter.extra.datasets-test
  (:require [clojure.test :refer :all]
            [grafter.tabular :refer [make-dataset move-first-row-to-header]]
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

(deftest revert-header-to-first-row-test
  (let [raw (make-dataset [["food" "price"] ["borek" 1.9] ["pizza" 2.5]])
        interpreted (make-dataset raw move-first-row-to-header)
        re-rawed (revert-header-to-first-row interpreted)]
    (testing "Allows for round-tripping via headers"
      (datasets-match? raw re-rawed))))

(deftest select-columns-test
  (testing "Select columns with a set"
    (let [dataset (make-dataset [[1 2 3] [4 5 6] [7 8 9]] [:x :y :z])]
      (is (datasets-match? (make-dataset [[1 3] [4 6] [7 9]] [:x :z])
                           (select-columns dataset #{:x :z}))))))
