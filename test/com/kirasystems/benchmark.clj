(ns com.kirasystems.benchmark
  (:require [com.kirasystems.calendar :as kc]
            [com.kirasystems.specs :as ks]
            [clojure.spec.alpha :as s]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [criterium.core :as cc :refer [bench quick-bench]]
            [com.kirasystems.sort :as ksort]
            [com.kirasystems.data :as kd]))


;;;;
;;;; Benchmarking comparison using date object vs data converted to a nummeric primitive
;;;;



(comment
  (quick-bench (t/after? (t/date-time 2019 1 1 19 00)
                         (t/date-time 2019 1 1 18 00))))

;; Execution time mean : 264.312045 ns



(comment
  (quick-bench (> 1546369200000
                  1546365600000)))

;; Execution time mean : 21.595515 ns


;;;;
;;;; Benchmarking sort using object vs primitive
;;;;


(comment
  (let [data (kc/split-events kd/events)]
    (bench (kc/sort-events data))))

;; Execution time mean : 12.031737 µs



(comment
  (let [data (mapv #(nth % 2) (kc/split-events kd/events))]
    (bench (sort data)))) ;; primitive sorting is 35% faster !

;; Execution time mean : 4.463240 µs


;;;;
;;;; Benchmark reduce vs transient version
;;;;


(comment
  (let [sorted-data (kc/sort-events (kc/split-events kd/events))]
    (bench (kc/determine-overlaps sorted-data)))) ;; an earlier version which used core/reduce

;; Execution time mean : 20.924783 µs


(comment
  (let [sorted-data (kc/sort-events (kc/split-events kd/events))]
    (bench (kc/determine-overlaps sorted-data))))

;; Execution time mean : 18.440397 µs



;;;;
;;;; Benchmarking with a larger sample size
;;;;


(comment
  (let [data (into [] (take 10000 kd/gen-events))]
    (bench (kc/get-overlaps data))))

;; data size - 1000
;; Execution time mean : 13.695729 ms

;; data size - 5000
;; Execution time mean : 61.370762 ms

;; data size - 10000
;; Execution time mean : 111.244914 ms



(comment
  (let [data (into [] (take 10000 kd/gen-events))]
    (bench (kc/get-overlaps-2 data))))

;; data size - 1000
;; Execution time mean : 5.815242 ms

;; data size - 5000
;; Execution time mean : 10.326383 ms

;; data size - 10,000
;; Execution time mean : 15.286281 ms
