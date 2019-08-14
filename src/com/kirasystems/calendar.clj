(ns com.kirasystems.calendar
  (:require [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [com.kirasystems.util :as ku]
            [clojure.core.reducers :as r]
            [com.kirasystems.sort :as ks]
            [clojure.spec.alpha :as s]
            [com.kirasystems.specs :as kspecs]
            [com.kirasystems.data :as kd]))


;;;;
;;;; first approach
;;;;


(defn split-event
  "Split an event into its intervals. Given a {:id 1 :start 123 :end 345} -> [[1 :start 123] [1 :end 345]]"
  [{:keys [id start end]}]
  [[id :start (tc/to-long start)]
   [id :end (tc/to-long end)]])

(s/fdef split-event
  :args (s/cat :events ::kspecs/event)
  :ret ::kspecs/split-events-with-time)

(defn split-events
  "Given an events collection split each event. Given a [{:id 1 :start 123 :end 345}] -> [[1 :start 123] [1 :end 345]]"
  [events]
  (into [] (r/foldcat (r/mapcat split-event events))))

(s/fdef split-events
  :args (s/cat :events ::kspecs/events)
  :ret ::kspecs/split-events-with-time)

(def sort-events
  "Give a collection of split events, sorts events based on the time (ascending) and operation (:end > :start)."
  (partial sort ks/by-time-and-op))

(s/fdef sort-events
  :args (s/cat :split-events ::kspecs/split-events-with-time)
  :ret ::kspecs/split-events-with-time)

;;;;
;;;; second approach - sort data using primitves
;;;;


(defn events->time-map
  "Given an events collection returns a hash-map in which the key's are the msecs, and values are an array of split intervals."
  [events]
  (r/fold
   (r/monoid merge (constantly {}))
   (fn
     ([] {})
     ([acc {:keys [id start end]}]
      (let [start (tc/to-long start)
            end   (tc/to-long end)]
        (merge acc
               {start (conj (get acc start []) [id :start])
                end   (conj (get acc end []) [id :end])}))))
   events))


(s/fdef events->time-map
  :args (s/cat :events ::kspecs/events)
  :ret  (s/keys :opt-un [::kspecs/msecs->events]))


(def sort-event-time
  "Given a msecs->events hash-map, returns a hash-map with :msecs->events and :sorted-msecs keys.
  Value of the :msecs->events key represents the hash-map passed in, and the :sorted-msecs represents the sorted time values."
  #(hash-map :msecs->events % :sorted-msecs (sort (keys %))))

(s/fdef sort-event-time
  :args (s/cat :msecs->events ::kspecs/msecs->events)
  :ret (s/keys :opt-un [::kspecs/msecs->events ::kspecs/sorted-msecs]))


(defn sorted-time->sorted-events
  "Given sorted msecs, and a msecs->events hash-map, sort the events."
  [{:keys [msecs->events sorted-msecs]}]
  (into [] (r/foldcat (r/mapcat (fn [msecs]
                                  (if (next (msecs->events msecs))
                                    (sort ks/by-op (msecs->events msecs))
                                    (msecs->events msecs)))
                                sorted-msecs))))

(s/fdef sorted-time->sorted-events
  :args (s/cat :args (s/keys :req-un [::kspecs/msecs->events ::kspecs/sorted-msecs]))
  :ret ::kspecs/split-events-without-time)


(defn determine-overlaps
  "Given sorted split events, returns a set of overlaps."
  [sorted-events]
  (set (:overlaps
        (loop [{:keys [starts overlaps] :as acc} (transient {:starts #{} :overlaps []})
               [[id event] & r] sorted-events]
          (case event
            :start (assoc! acc
                           :starts (conj starts id)
                           :overlaps (concat overlaps
                                             (map #(hash-set % id) starts)))
            :end (assoc! acc
                         :starts (disj starts id))
            nil)
          (if (seq r)
            (recur acc r)
            (persistent! acc))))))


(s/fdef determine-overlaps
  :args (s/cat :split-events (s/or :without-time ::kspecs/split-events-without-time
                                   :with-time ::kspecs/split-events-with-time))
  :ret ::kspecs/overlaps)


(defn get-overlaps
  "Given a collection of events, returns a set of overlaps. Each overlap is a set of event ids."
  [events]
  (->> events
       split-events
       sort-events
       determine-overlaps))

(s/fdef get-overlaps
  :args (s/cat :events ::kspecs/events)
  :ret ::kspecs/overlaps)


(defn get-overlaps-2
  "Given a collection of events, returns a set of overlaps. Each overlap is a set of event ids.
  Sorting is done on primitives for better performance."
  [events]
  {:pre [(s/valid? ::kspecs/events events)]
   :post [(s/valid? ::kspecs/overlaps %)]}
  (->> events
       events->time-map
       sort-event-time
       sorted-time->sorted-events
       determine-overlaps))

(s/fdef get-overlaps-2
  :args (s/cat :events ::kspecs/events)
  :ret ::kspecs/overlaps)


(comment (get-overlaps-2 kd/events))
