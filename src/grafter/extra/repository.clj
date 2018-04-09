(ns grafter.extra.repository
  (:require [grafter.rdf :refer [add statements]]
            [grafter.rdf4j.repository :refer [shutdown sail-repo ->connection]]))

(defmacro with-repository [repo-binding & body]
  `(let ~repo-binding
     (try
       ~@body
       (finally (shutdown (first ~repo-binding))))))

(defn load-contents [repository contents]
  (let [is-file? string?
        data (if (is-file? contents) (statements contents) contents)]
    (with-open [connection (->connection repository)]
      (add connection data))))

(defmacro with-repository-containing [[repo-binding repo-contents] & body]
  `(let [~repo-binding (sail-repo)]
     (try
       (load-contents ~repo-binding ~repo-contents)
       ~@body
       (finally (shutdown ~repo-binding)))))
