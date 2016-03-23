(ns grafter.extra.validation
  (:require [grafter.rdf.repository :refer [->connection query]]))

(defn- ask [repository ask-query]
  (with-open [connection (->connection repository)]
    (let [result (query connection ask-query)]
      result)))

(defn constraint-asserter [ask-query message]
  "Returns a function for testing that a condition, specified by the ask-query,
   holds for the data in a given repository.
   
   If the condition is violated (the ASK returns false) then an AssertionError
   is thrown with the explanatory message."
  
  (fn [repository]
    (if (ask repository ask-query)
      (throw (AssertionError. (str "Constraint violated: " message))))))
