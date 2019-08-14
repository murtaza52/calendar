(ns com.kirasystems.specs
  "Defines specs for the calendar domain"
  (:require [clojure.spec.alpha :as s]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]))


(s/def ::start (s/inst-in (t/date-time 2000 1 1 00 00) (t/date-time 2030 1 1 00 00)))

(s/def ::end (s/inst-in (t/date-time 2000 1 1 00 00) (t/date-time 2030 1 1 00 00)))

(s/def ::id (s/and string? #(not (clojure.string/blank? %))))

(s/def ::event-keys (s/keys :req-un [::id ::start ::end]))

(s/def ::event (s/and ::event-keys
                      #(< (tc/to-long (:start %)) (tc/to-long (:end %)))))

(s/def ::events (s/every ::event))

(s/def ::op #{:start :end})

(s/def ::msecs integer?)

(s/def ::msecs-coll (s/every ::msecs))

(s/def ::split-event-with-time (s/cat :id ::id
                                      :op ::op
                                      :msecs ::msecs))

(s/def ::split-event-without-time (s/cat :id ::id
                                         :op ::op))

(s/def ::split-events-with-time (s/every ::split-event-with-time))

(s/def ::split-events-without-time (s/every ::split-event-without-time))

(s/def ::msecs->events (s/every-kv ::msecs ::split-events-without-time))

(s/def ::sorted-msecs ::msecs-coll)

(s/def ::all (s/every-kv (s/or ::msecs #{:sorted-msecs}) (s/or ::msecs-coll ::split-events-without-time)))

(s/def ::overlap (s/every ::id))

(s/def ::overlaps (s/every ::overlap))
