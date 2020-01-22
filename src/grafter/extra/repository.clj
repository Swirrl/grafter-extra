(ns grafter.extra.repository
  (:require [grafter-2.rdf.protocols :refer [add]]
            [grafter-2.rdf4j.io :as gio]
            [grafter-2.rdf4j.repository :as repo]))

(defmacro with-repository [repo-binding & body]
  `(let ~repo-binding
     (try
       ~@body
       (finally (repo/shutdown (first ~repo-binding))))))

(defn load-contents [repository contents]
  (let [is-file? string?
        data (if (is-file? contents) (gio/statements contents) contents)]
    (with-open [connection (repo/->connection repository)]
      (add connection data))))

(defmacro with-repository-containing [[repo-binding repo-contents] & body]
  `(let [~repo-binding (repo/sail-repo)]
     (try
       (load-contents ~repo-binding ~repo-contents)
       ~@body
       (finally (repo/shutdown ~repo-binding)))))
