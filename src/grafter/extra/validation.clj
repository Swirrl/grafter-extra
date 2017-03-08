(ns grafter.extra.validation
  (:require [grafter.rdf.repository :refer [->connection query]]))

(defn- ask [repository ask-query]
  (with-open [connection (->connection repository)]
    (let [result (query connection ask-query)]
      result)))

(defn constraint-asserter [invalid? ask-query message]
  "Returns a function for asserting that a condition, specified by the ask-query,
   holds for the data in a given repository. Throws an AssertionError if the response
   is invalid."
  
  (fn [repository]
    (if (invalid? (ask repository ask-query))
      (throw (AssertionError. (str "Constraint violated: " message))))))

(defn absence-asserter [ask-query message]
  "Throws an AssertionError if matching data is found (i.e. the ASK returns true)"
  (constraint-asserter true? ask-query message))

(defn presence-asserter [ask-query message]
  "Throws an AssertionError unless matching data is found (i.e. the ASK returns false)"
  (constraint-asserter false? ask-query message))


(defn constraint-checker [invalid? ask-query message]
  "Returns a function for checking that a condition, specified by the ask-query,
   hold for the data in a given repository. Returns the message if the response
   is invalid."

  (fn [repository]
    (if (invalid? (ask repository ask-query))
      message)))

(defn presence-checker [ask-query message]
  "Returns the message unless matching data is found"
  (constraint-checker false? ask-query message))

(defn absence-checker [ask-query message]
  "Returns the message if matching data is found"
  (constraint-checker true? ask-query message))


(defn example-finder [example-query message]
  (fn [repository]
    (with-open [connection (->connection repository)]
      (let [result (query connection example-query)]
        (if (seq result)
          (-> result
              last
              (get "example")
              ((partial str message ", e.g. "))))))))
