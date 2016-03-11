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

(defn join-comma [& args]
  (string/join ", " args))

(defn join-space [& args]
  (string/join " " args))

(defn join-newline [& args]
  (string/join "\n" args))

(defn prefix-space [arg]
  (str "  " arg))

(defn indent [arg]
  (map prefix-space arg))

(defn sparql-uri [uri]
  (str "<" uri ">"))

(def to-uri sparql-uri)

(defn object-list [obj-or-list]
  (if (or (vector? obj-or-list) (seq? obj-or-list))
    (apply join-comma obj-or-list)
    obj-or-list))

(defn verb-object-list [[verb obj-list]]
  (str (join-space verb (object-list obj-list)) ";"))

(defn property-list [prop-list]
  (map verb-object-list prop-list))

(defn triples-same-subject [subj prop-list]
  (concat [subj]
          (indent (property-list prop-list))
          ["  ."]))

(defn group-graph-pattern [subj prop-list]
  (apply join-newline
         (concat ["{"]
                 (indent (triples-same-subject subj prop-list))
                 ["}"])))

(def ggp group-graph-pattern)
