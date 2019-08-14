(ns com.kirasystems.data
  "Sample data for testing"
  (:require [clj-time.core :as t]
            [com.kirasystems.util :as ku]))


(def events
  "Sample events data for sanity testing / benchmarking"
  [{:id    "a"
    :start (t/date-time 2019 1 1 13 00)
    :end   (t/date-time 2019 1 1 14 30)}
   {:id    "b"
    :start (t/date-time 2019 1 1 13 30)
    :end   (t/date-time 2019 1 1 13 45)}
   {:id    "c"
    :start (t/date-time 2019 1 1 13 30)
    :end   (t/date-time 2019 1 1 14 30)}
   {:id    "d"
    :start (t/date-time 2019 1 1 14 00)
    :end   (t/date-time 2019 1 1 18 00)}
   {:id    "e"
    :start (t/date-time 2019 1 1 18 00)
    :end   (t/date-time 2019 1 1 19 30)}
   {:id    "f"
    :start (t/date-time 2019 1 1 20 00)
    :end   (t/date-time 2019 1 1 21 00)}
   {:id    "g"
    :start (t/date-time 2019 1 1 19 00)
    :end   (t/date-time 2019 1 1 20 00)}
   {:id    "h"
    :start (t/date-time 2019 1 1 12 30)
    :end   (t/date-time 2019 1 1 17 30)}])


(def gen-events
  "Generates a lazy sequence of events"
  (repeatedly #(hash-map :id (ku/uuid)
                         :start (t/date-time 2019 1 1 (rand 24) 00)
                         :end (t/date-time 2019 1 1 (rand 24) 05))))
