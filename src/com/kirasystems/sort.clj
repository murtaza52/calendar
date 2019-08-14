(ns com.kirasystems.sort)


(defn by-time-and-op
  "Sort time by ascending order. If the time is equal then sort by the op, such that the :start ops are after :end ops."
  [[_ op1 t1] [_ op2 t2]]
  (let [c (compare t1 t2)]
    (if (= c 0)
      (compare op1 op2) ;; if the start time and end time are overlapping then start op should come after the end.
      c)))

(defn by-op
  "Sorts by the op such that :start comes before :end"
  [[_ op1][_ op2]]
  (compare op1 op2))

