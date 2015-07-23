(ns grafter.extra.statements)

(defn statements-where [attr-map statements]
  "Filters a sequence of statements according to an attribute map

  e.g. To extract statements describing observations

  (statements-where {:p rdf:a, :o qb:Observation} quads)"

  (filter (fn [quad]
            (every? (fn [[part value]] (= (get quad part) value))
                    attr-map))
          statements))
