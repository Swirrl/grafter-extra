(ns grafter.extra.cell.string
  (:require [clojure.string :as st]))

(defn blank? [string]
  "Checks for nil or empty strings"
  (or (nil? string)
      (= "" string)))

(defn first-sentence [string]
  "Takes the first sentence from a paragraph"
  (if string
    (first (st/split string #"\. "))))

(defmulti parseValue class)
(defmethod parseValue nil                 [x] nil)
(defmethod parseValue java.lang.Character [x] (Character/getNumericValue x))
(defmethod parseValue java.lang.String    [x] (if (= "" x)
                                                nil
                                                (if (.contains x ".")
                                                  (Double/parseDouble x)
                                                  (Integer/parseInt x))))
(defmethod parseValue :default            [x] x)
