(ns grafter.extra.cell.string-test
  (:require [clojure.test :refer :all]
            [grafter.extra.cell.string :refer :all]))

(deftest parseValue-test
  (testing "parseValue"
    (is (= (parseValue "123")
           123))
    (is (= (parseValue "1.23")
           1.23))
    (is (= (parseValue \a)
           nil))
    (is (= (parseValue "abc")
           nil))
    (is (= (parseValue \*)
           nil))
    (is (= (parseValue "*")
           nil))
    (is (= (parseValue "-4")
           -4))))
