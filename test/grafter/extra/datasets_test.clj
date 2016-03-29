(ns grafter.extra.datasets-test
  (:require [clojure.test :refer :all]
            [grafter.tabular :refer [make-dataset move-first-row-to-header column-names]]
            [grafter.extra.datasets :refer :all]
            [grafter.extra.cell.string :refer [blank?]]))

(deftest datasets-match-test
  (testing "Compares rows regardless of order"
    (let [dataset-a (make-dataset [[1] [2] [3]] [:a])
          dataset-b (make-dataset [[1] [3] [2]] [:a])
          dataset-c (make-dataset [[0] [0] [0]] [:a])]
      (is (datasets-match? dataset-a dataset-b))
      (is (not (datasets-match? dataset-a dataset-c))))))

(deftest column-bind-test
  (testing "Binds columns together"
    (let [a (make-dataset [[1 "apple"] [2 "banana"] [3 "orange"]] [:number :fruit])
          b (make-dataset [["one"] ["two"] ["three"]] [:name])]
      (is (datasets-match? (column-bind a b)
                           (make-dataset [[1 "apple" "one"] [2 "banana" "two"] [3 "orange" "three"]] [:number :fruit :name])))))
  (testing "Fails if column lengths differ"
    (let [short (make-dataset [[1]] [:number])
          long (make-dataset [["one"] ["two"]] [:name])]
      (is (thrown? java.lang.RuntimeException
                   (column-bind short long))))))

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

(deftest row-transformers-test
  (testing "Transforms rows"
    (let [original (with-meta (make-dataset [[1 2 3] [1 2 3] [11 12 13]] [:a :b :c]) {:foo :bar})
          transformed (transform-rows original distinct)
          expected (with-meta (make-dataset [[1 2 3] [11 12 13]] [:a :b :c]) {:foo :bar})]
      (testing "replaces rows"
          (is (= transformed expected)))
      (testing "maintains metadata"
        (is (= (meta transformed) (meta original)))))))

(deftest drop-where-test
  (testing "Drop where"

    (testing "on single columns"
      (let [dataset-full (make-dataset [[1 "one"] [2 "two"] [3 "three"]] [:a :b])
            dataset-filtered (drop-where dataset-full odd? :a)
            dataset-expected (make-dataset [[2 "two"]] [:a :b])]
        (is (= dataset-filtered
               dataset-expected))
        (is (= (column-names dataset-filtered)
               [:a :b]))))

    (testing "on multiple columns"
      (let [dataset-full (make-dataset [["" "Leonardo"] ["Raphael" ""] ["Donatello" "Michelangeolo"]] [:a :b])
            dataset-filtered (drop-where dataset-full blank? [:a :b])
            dataset-expected (make-dataset [["Donatello" "Michelangeolo"]] [:a :b])]
        (is (= dataset-filtered
               dataset-expected))
        (is (= (column-names dataset-filtered)
               [:a :b]))))))
