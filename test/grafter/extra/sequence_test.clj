(ns grafter.extra.sequence-test
  (:require [clojure.test :refer :all]
            [grafter.extra.sequence :refer :all]))

(deftest to-sentence-test
  (testing "creates a sentence from a sequence"
    (is (= "" (to-sentence [])))
    (is (= "one" (to-sentence '("one"))))
    (is (= "one and two" (to-sentence '("one" "two"))))
    (is (= "one, two and three" (to-sentence '("one" "two" "three"))))
    (is (= "one, two, three and four" (to-sentence '("one" "two" "three" "four"))))))
