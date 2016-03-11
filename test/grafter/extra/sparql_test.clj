(ns grafter.extra.sparql-test
  (:require [clojure.test :refer :all]
            [grafter.extra.sparql :refer :all]))

(deftest alphabetical-vars-for-test
  (is (= '("?a" "?b" "?c" "?d")
         (alphabetical-vars-for (range 4)))))


(deftest object-list-test
  (testing "long object"
    (is (= "obj"
           (object-list "obj"))))
  (testing "list of objects"
    (is (= "obj-a, obj-b, obj-c"
           (object-list ["obj-a" "obj-b" "obj-c"])))))

(deftest verb-object-list-test
  (testing "predicate with object"
    (is (= "prop obj;"
           (verb-object-list ["prop" "obj"]))))
  (testing "predicate with list of objects"
    (is (= "prop obj-a, obj-b, obj-c;"
           (verb-object-list ["prop" ["obj-a" "obj-b" "obj-c"]])))))

(deftest property-list-test
  (is (= ["prop1 obj1;"
          "prop2 obj2;"]
         (property-list [["prop1" "obj1"]
                         ["prop2" "obj2"]]))))

(deftest triples-same-subject-test
  (is (= ["subj"
          "  prop1 obj1;"
          "  prop2 obj2-a, obj2-b;"
          "  ."]
         (triples-same-subject "subj"
                               [["prop1" "obj1"]
                                ["prop2" ["obj2-a" "obj2-b"]]]))))

(defn long-str [& strings] (clojure.string/join "\n" strings))

(deftest group-graph-pattern-test
  (is (= (long-str
          "{"
          "  s"
          "    p1 o1;"
          "    p2 o2;"
          "    ."
          "}") ;; "{\n  s\n    p1 o1;\n    p2 o2;\n    .\n}"
         (group-graph-pattern
          "s"
          [["p1" "o1"]
           ["p2" "o2"]]))))
