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

(defn strip-non-value-chars [^java.lang.String string]
  (.replaceAll string "[^\\.0123456789-]" ""))

(defmulti parseValue class)

(defmethod parseValue nil [x]
  nil)

(defmethod parseValue java.lang.Character [x]
  (parseValue (.toString x)))

(defmethod parseValue java.lang.String [x]
  (let [cleaned (strip-non-value-chars x)]
    (try
      (Integer/parseInt cleaned)
      (catch NumberFormatException e
        (try
          (BigInteger. cleaned)
          (catch NumberFormatException e
            (try
              (BigDecimal. cleaned)
              (catch NumberFormatException e
                nil))))))))

(defmethod parseValue :default [x]
  x)
