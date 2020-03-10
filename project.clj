(defproject figleaf "1.0.1"
  :description "figleaf, a code coverage library for Clojure"
  :aot [figleaf.demo.core]
  :main figleaf.core
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :repositories [["project" {:url "file:repo" :username "" :password "" ::checksum :ignore}]])

