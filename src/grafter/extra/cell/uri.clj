(ns grafter.extra.cell.uri
  (:require [clojure.string :as st]
            [grafter.extra.cell.string :refer [blank?]]))

(defn slugize-preserving-case [string]
  (-> string
      str
      (st/replace #"[^\w/]" "-")
      (st/replace #"-+" "-")
      (st/replace #"-$" "")))

(defn slugize [string]
  (-> string st/lower-case slugize-preserving-case))

(defn classize [string]
  "Join a sentence of strings capitalising the first letters of each
     e.g. \"date of birth\" => \"DateOfBirth\""
  (->> (st/split string #" ")
       (map st/capitalize)
       st/join))

(defn propertize [string]
  "Join a sentence of strings. First starts with a lower case character,
  and the rest start with upper case

     e.g. \"date of birth\" => \"dateOfBirth\""
  (let [classized (classize string)]
    (st/join (concat (st/lower-case (first classized))
                     (rest classized)))))

(defn remove-trailing-slashes [string]
  (st/replace string #"/+$" ""))

(defn hierarchize [& slugs]
  "Connect slugs interleaving with slashes:

     e.g [\"year\" 2015 \"month\" 8 \"day\" 5] => \"year/2015/month/08/day/05\"

  Strips any trailing slashes caused by nils"
  (let [non-blank-slugs (remove blank? (vec slugs))
        joined-slugs    (st/join "/" non-blank-slugs)]
    (remove-trailing-slashes joined-slugs)))

(defn compact-slugize-hierarchize [& strings]
  "Creates a slash-separated url from a collection of strings, dropping blanks"
  (->> strings
       (remove blank?)
       (map slugize)
       (apply hierarchize)))

(defn filenameize [uri]
  "Converts a graph-uri into a string suitable for using as a filename. Useful for
  serialising a sequence of quads into separate files for each graph."

  (-> uri
      (st/split #"graph\/")
      last
      (st/replace #"\/" "_")
      (st/replace #"\?" "_")
      (st/replace #"\&" "_")
      (st/replace #"\=" "-")))
