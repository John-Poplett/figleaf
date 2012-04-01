(ns figleaf.core
  (:use [clojure.set :only [difference]])
  (:require [clojure.test :as test]))

(defn foo [x] (* x x))

(defn standard-fn? [func]
  (and (.isBound func) (fn? (deref func))
           (not (:macro (meta func))) (not (:test (meta func)))))

(defn do-instrument-function [var-name pre post]
  "Wrap given func with pre- and post-function calls. Return a let-over-lambda
expression to restore to original."
  (do
    (alter-var-root var-name
                    (fn [function]
                      (with-meta
                        (fn [& args]
                          (if pre (pre (str var-name) args))
                          (let [result (apply function args)]
                            (if post (post (str var-name) args))
                            result))
                        (assoc (meta function)
                          :figleaf/original function))))
    #(alter-var-root var-name (fn [function] (:figleaf/original (meta function))))))

(defmacro instrument-function [function pre post]
  `(do-instrument-function (var ~function) ~pre ~post))

(defmacro with-instrument-namespace [ns pre post & body]
  "Wrap each function of the given package with pre and post function
calls. Call code specified in the body and restore the functions on exit."
  `(loop [symbols# (vals (ns-publics '~ns)) restore-list# nil]
     (if (nil? (first symbols#))
       (let [result# ~@body]
         (doseq [restore-fn# restore-list#]
           (restore-fn#))
         result#)
       (let [func# (first symbols#)]
         (if (and (.isBound func#) (fn? (deref func#))
                  (not (:macro (meta func#))) (not (:test (meta func#))))
           (recur (rest symbols#) (cons (do-instrument-function func# ~pre ~post) restore-list#))
           (recur (rest symbols#) restore-list#))))))

(let [funcall-counter (atom {})
      target-ns (atom 'user)]
  (defn all []
    (filter standard-fn? (vals (ns-publics @target-ns))))
;;    (loop for name being the external-symbol of package when (fboundp name) collect name))
  (defn tested []
    "Return a list of tested functions"
    (keys @funcall-counter))
  ;;  (loop for name being the hash-keys in funcall-counter collect name))
  (defn untested []
    "Return a list of untested functions."
    (difference (into #{} (all)) (into #{} (tested))))
  (defn increment-funcall-count
    "Increment count of times function is called by one. May arity2 into arity2."
    ([func-name _]
       (increment-funcall-count func-name))
    ([func-name]
    (let [current-count (get @funcall-counter func-name 0)]
      (swap! funcall-counter #(assoc % func-name (inc current-count))))))
  (defn funcall-count []
    "Return count of function calls."
    (reduce + (vals @funcall-counter)))
  (defn tested-function-count []
    "Return count of functions tested."
    (reduce + (map #(if (> %1 0) 1 0) (vals @funcall-counter))))
  (defn namespace-function-count []
    (count (all)))
  (defn reset-function-count []
    (swap! funcall-counter (fn [_] {})))
  (defn set-namespace [namespace-under-test]
    (swap! target-ns (fn [_] namespace-under-test)))
  (defmacro run-tests [namespace-under-test unit-test-namespace]
    `(do
      (set-namespace '~namespace-under-test)
      (reset-function-count)
      (with-instrument-namespace ~namespace-under-test increment-funcall-count nil
        (test/run-tests '~unit-test-namespace))
      (printf "CODE COVERAGE: Functions %d, Tested %d, Ratio %2.0f%%\n" (namespace-function-count)
              (tested-function-count) (/ (funcall-count) (namespace-function-count) 0.01))))
  )
