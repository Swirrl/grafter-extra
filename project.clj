(defproject grafter/extra "0.1.5-grafter080-SNAPSHOT"
  :description "A collection of extra transformations and tools for developing Grafter pipelines"
  :url "http://grafter.org/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [grafter/grafter "0.8.3-SNAPSHOT"]
                 [grafter/vocabularies "0.2.0-SNAPSHOT"]
                 [selmer "1.0.4"]]
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]])
