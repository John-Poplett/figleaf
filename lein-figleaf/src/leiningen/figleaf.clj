(ns leiningen.figleaf
  (:use [leiningen.compile :only [eval-in-project]]))

(defn- require-form [ns-under-test unit-test-ns]
  `(try
     (require 'figleaf.core)
     (require '~ns-under-test)
     (require '~unit-test-ns)
     (catch Throwable e#
       (.printStackTrace e#)
       (System/exit 1))))

(defn- run-form [ns-under-test unit-test-ns]
  `(figleaf.core/run-tests ~ns-under-test ~unit-test-ns))

(defn figleaf [project ns-under-test unit-test-ns]
  "Run clojure.test and report code coverage. Takes two arguments,
the namespace under test and the namespace of the corresponding unit tests."
  (let [ns-under-test-as-symbol (symbol ns-under-test)
	unit-test-ns-as-symbol (symbol unit-test-ns)]
    (eval-in-project
     project
     (run-form ns-under-test-as-symbol unit-test-ns-as-symbol)
     nil
     nil
     (require-form ns-under-test-as-symbol unit-test-ns-as-symbol))))
