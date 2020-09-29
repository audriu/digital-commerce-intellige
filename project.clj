(defproject exercise "0.1.0-SNAPSHOT"
  :description "Clojure programming exercise"
  :dependencies [[org.clojure/algo.generic "0.1.3"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/data.csv "1.0.0"]
                 [org.clojure/data.json "1.0.0"]]
  :main ^:skip-aot exercise.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
