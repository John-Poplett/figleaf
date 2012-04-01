(ns figleaf.demo.test
  (:require [clojure.test :as test]
            [figleaf.demo.core :as demo]
            [figleaf.core :as figleaf]))

(test/deftest add-one-test []
  (test/is (= 1 (demo/add-one 0))))

;;(defn run-tests []
;;  (figleaf/run-tests figleaf.demo.core figleaf.demo.test))
