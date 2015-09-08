(defproject grafter/extra "0.1.2-SNAPSHOT"
  :description "A collection of extra transformations and tools for developing Grafter pipelines"
  :url "http://grafter.org/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [grafter/grafter "0.5.1"]
                 [grafter/vocabularies "0.1.3"]]
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]])
