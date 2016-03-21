(ns grafter.extra.column-test
  (:require [clojure.test :refer :all]
            [grafter.tabular :refer [test-dataset column-names]]
            [grafter.extra.column :refer :all]))

(deftest add-id-column-test
  (let [d (-> (test-dataset 3 3)
              (add-id-column :id))]
    (testing "adds an column"
      (is (some #(= % :id) (column-names d))))
    (testing "adds an ascending id"
      (is (= (->> d :rows (map :id))
             [0 1 2])))))
