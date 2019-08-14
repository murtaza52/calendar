(ns com.kirasystems.calendar-test
  (:require [clojure.test :refer :all]
            [com.kirasystems.calendar :as kc]
            [com.kirasystems.specs :as ks]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [clj-time.core :as t]
            [com.kirasystems.data :as kd]))

(defn run-instrumented-tests
  []
  (stest/instrument)
  (kc/get-overlaps-2 kd/events) ;; simulate external calls
  (kc/get-overlaps-2 (gen/sample (s/gen ::ks/event) 50)) ;; simulate conformance to :arg specs based on generated data.
  (stest/unstrument))

(def run-generative-tests #(stest/summarize-results (stest/check)))

(deftest testing-overlaps
  (testing "gives overlaps"
    (is (= #{#{"a" "b"} #{"b" "c"} #{"c" "d"} #{"b" "d"}} (kc/get-overlaps-2 [{:id    "a"
                                                                               :start (t/date-time 2019 1 1 13 00)
                                                                               :end   (t/date-time 2019 1 1 14 30)}
                                                                              {:id    "b"
                                                                               :start (t/date-time 2019 1 1 12 00)
                                                                               :end   (t/date-time 2019 1 1 13 30)}
                                                                              {:id    "c"
                                                                               :start (t/date-time 2019 1 1 11 00)
                                                                               :end   (t/date-time 2019 1 1 12 30)}
                                                                              {:id    "d"
                                                                               :start (t/date-time 2019 1 1 10 00)
                                                                               :end   (t/date-time 2019 1 1 13 00)}]))))
  (testing "does not overlap"
    (is (= #{} (kc/get-overlaps-2 [{:id    "a"
                                    :start (t/date-time 2019 1 1 13 00)
                                    :end   (t/date-time 2019 1 1 14 00)}
                                   {:id    "b"
                                    :start (t/date-time 2019 1 1 12 00)
                                    :end   (t/date-time 2019 1 1 13 00)}
                                   {:id    "c"
                                    :start (t/date-time 2019 1 1 14 00)
                                    :end   (t/date-time 2019 1 1 15 00)}])))))

(defn run-all
  []
  (run-instrumented-tests)
  (run-generative-tests)
  (run-tests 'com.kirasystems.calendar-test))

(comment
  (run-all))
