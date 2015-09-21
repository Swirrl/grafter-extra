(ns grafter.extra.sparql-test
  (:require [clojure.test :refer :all]
            [grafter.extra.sparql :refer :all]))

(deftest alphabetical-vars-for-test
  (is (= '("?a" "?b" "?c" "?d")
         (alphabetical-vars-for (range 4)))))

