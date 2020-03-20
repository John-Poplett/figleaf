(defproject figleaf "1.0.2-SNAPSHOT"
  :author "John Poplett <https://www.johnpoplett.com>"
  :description "figleaf, a code coverage library for Clojure"
  :url "https://github.com/John-Poplett/figleaf"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "Same as Clojure"}
  :min-lein-version "2.3.3"
  :aot [figleaf.demo.core]
  :main figleaf.core
  :dependencies [[org.clojure/clojure "1.10.1"]])

