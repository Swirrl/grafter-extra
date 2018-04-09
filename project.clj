(defproject grafter/extra "0.2.2-rdf4j-SNAPSHOT"
  :description "A collection of extra transformations and tools for developing Grafter pipelines"
  :url "http://grafter.org/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [grafter/grafter "0.11.0-drafter-rdf4j"]
                 [grafter/grafter.tabular "0.9.0"]
                 [org.openrdf.sesame/sesame-runtime "2.8.9"]
                 [selmer "1.0.4"]]
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]])
