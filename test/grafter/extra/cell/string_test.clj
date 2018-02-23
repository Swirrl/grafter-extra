(ns grafter.extra.cell.string-test
  (:require [clojure.test :refer :all]
            [grafter.extra.cell.string :refer :all]))

(deftest parseValue-test
  (testing "parseValue"
    (is (= (parseValue "123")
           123))
    (is (= (parseValue "1.23")
           1.23M))
    (is (= (parseValue "2256672315")
           2256672315N))
    (is (= (parseValue "123123123123123123123123123123123.123123131231231231231231231231231312312312313123")
           123123123123123123123123123123123.123123131231231231231231231231231312312312313123M))
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
