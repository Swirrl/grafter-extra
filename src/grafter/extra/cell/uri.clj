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
  (->> (st/split string #" ")
       (map st/capitalize)
       st/join))

(defn propertize [string]
  (let [classized (classize string)]
    (st/join (concat (st/lower-case (first classized))
                     (rest classized)))))

(defn remove-trailing-slashes [string]
  (st/replace string #"/+$" ""))

(defn hierarchize [& slugs]
  (let [non-blank-slugs (remove blank? (vec slugs))
        joined-slugs    (st/join "/" non-blank-slugs)]
    (remove-trailing-slashes joined-slugs)))

(defn compact-slugize-hierarchize [& parts]
  (->> parts
       (remove blank?)
       (map slugize)
       (apply hierarchize)))

(defn filenameize [uri]
  (-> uri
      (st/split #"graph\/")
      last
      (st/replace #"\/" "_")
      (st/replace #"\?" "_")
      (st/replace #"\&" "_")
      (st/replace #"\=" "-")))
