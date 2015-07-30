(ns grafter.extra.repository
  (:require [grafter.rdf.repository :refer [shutdown]]))

(defmacro with-repository [repo-binding & body]
  `(let ~repo-binding
     (try
       ~@body
       (finally (shutdown (first ~repo-binding))))))
