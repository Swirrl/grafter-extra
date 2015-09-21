(ns grafter.extra.sparql
  (:require [grafter.sequences :refer [alphabetical-column-names]]
            [clojure.string :as string]))

(defn to-var [string]
  (str "?" string))

(defn to-label [var]
  (str var "_label"))

(defn alphabetical-vars-for [coll]
  (->> (alphabetical-column-names)
       (take (count coll))
       (map to-var)))

(defn bgp
  ([predicate object]
   (str predicate " " object "; "))
  ([subject predicate object]
   (str subject " " predicate " " object " .")))

(def join-space
  (partial string/join " "))

(def join-newline
  (partial string/join "\n"))

(defn sparql-uri [uri]
  (str "<" uri ">"))
