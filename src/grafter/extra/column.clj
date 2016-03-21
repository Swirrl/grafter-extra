(ns grafter.extra.column
  (:require [grafter.tabular :refer [apply-columns]]
            [grafter.sequences :refer [integers-from]]))

(defn add-id-column [dataset column-name]
  (apply-columns dataset {column-name (fn [_] (integers-from 0))}))
