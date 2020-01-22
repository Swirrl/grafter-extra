(defproject grafter/extra "0.3.1-SNAPSHOT"
  :description "A collection of extra transformations and tools for developing Grafter pipelines"
  :url "http://grafter.org/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [grafter/grafter "2.1.7"]
                 [selmer "1.0.4"]]
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]])
