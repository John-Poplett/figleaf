;; Copyright (c) 2010-2020 John H. Poplett.
;;
;; Permission is hereby granted, free of charge, to any person obtaining
;; a copy of this software and associated documentation files (the
;; "Software"), to deal in the Software without restriction, including
;; without limitation the rights to use, copy, modify, merge, publish,
;; distribute, sublicense, and/or sell copies of the Software, and to
;; permit persons to whom the Software is furnished to do so, subject to
;; the following conditions:

;; The above copyright notice and this permission notice shall be
;; included in all copies or substantial portions of the Software.

;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
;; EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
;; MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
;; NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
;; LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
;; OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
;; WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
;;
(ns figleaf.test.core
  (:use [figleaf.demo.core])
  (:use [figleaf.demo.test])
  (:use [clojure.test])
  (:require [figleaf.core :as figleaf]))

;; (deftest replace-me ;; FIXME: write
;;   (is false "No tests have been written."))

(deftest standard-fn?-test
  (is (not (figleaf/standard-fn? (var standard-fn?-test)))))

(defn- test-fn []
  (println "wish you were here."))

(deftest instrument-function-test
  (let [test-value (atom 0)
        pre-post-fn (fn [& _] (swap! test-value inc))
        restore-fn (figleaf/instrument-function (var test-fn) pre-post-fn pre-post-fn)]
    (is (= 0 @test-value))
    (test-fn)
    (is (= 2 @test-value))
    (test-fn)
    (is (= 4 @test-value))
    (restore-fn)
    (test-fn)
    (is (= 4 @test-value))))

(deftest instrument-namespace-test
  (let [test-value (atom 0)
        pre-post-fn (fn [& _] (swap! test-value inc))
        restore-fns (figleaf/instrument-namespace 'figleaf.demo.core pre-post-fn pre-post-fn)]
    (is (= 0 @test-value))
    (foo)
    (is (= 2 @test-value))
    (foo)
    (is (= 4 @test-value))
    (doseq [restore-fn restore-fns]
      (restore-fn))
    (foo)
    (is (= 4 @test-value))))

(deftest with-instrument-namespace-test
  (let [test-value (atom 0)
        pre-post-fn (fn [& _] (swap! test-value inc))]
    (do
      (is (not (figleaf/instrumented? foo)))
      (figleaf/with-instrument-namespace figleaf.demo.core pre-post-fn pre-post-fn
        (do
          (is (figleaf/instrumented? foo))
          (is (= 0 @test-value))
          (foo)
          (is (= 2 @test-value))
          (foo)
          (is (= 4 @test-value))))
      (foo)
      (is (not (figleaf/instrumented? foo)))
      (is (= 4 @test-value)))))

(deftest with-instrument-namespace-exception-test
  (let [test-value (atom 0)
        pre-post-fn (fn [& _] (swap! test-value inc))]
    (do
      (is (not (figleaf/instrumented? bad-bad-foo)))
      (try
        (figleaf/with-instrument-namespace figleaf.demo.core pre-post-fn pre-post-fn
          (do
            (is (figleaf/instrumented? bad-bad-foo))
            (bad-bad-foo)))
        (catch Exception e (pre-post-fn)))
      (is (not (figleaf/instrumented? bad-bad-foo)))
      (is (= 2 @test-value)))))
