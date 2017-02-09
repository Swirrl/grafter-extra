(ns grafter.extra.sequence
  (:require [clojure.string :as string]))

(defn- sentence-builder 
  ([a] (str a))
  ([a b] (str a " and " b))
  ([a b & coll]
   (sentence-builder
    (string/join ", " (concat [a b] (butlast coll)))
    (last coll))))

(defn to-sentence [coll]
  (if (empty? coll) ""
    (apply sentence-builder coll)))
