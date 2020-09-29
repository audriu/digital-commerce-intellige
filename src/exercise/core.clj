(ns exercise.core
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [clojure.data.csv :as csv]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.set :refer [union]])
  (:gen-class))

(defn write-csv [filename header data]
  (with-open [writer (io/writer filename)]
    (csv/write-csv writer (into [header] data))))

(defn average
  [numbers]
  (if (empty? numbers)
    0
    (/ (reduce + numbers) (count numbers))))

(defn first-exercise [list-items]
  (let [data (->> list-items
                  (map #(select-keys % ["productUrl" "price" "originalPrice" "skus"]))
                  (map #(update % "skus" count))
                  (map vals))]
    (write-csv "first.csv" ["productUrl" "price" "originalPrice" "numberOfSKUs"] data)))

(defn second-exercise [list-items]
  (let [data (->> list-items
                  (filter #(and
                            (< 2 (count (get % "skus")))
                            (= "OEM" (get % "brandName"))))
                  (map #(get % "price"))
                  (map #(Float/parseFloat %))
                  average)]
    (write-csv "second.csv" ["averagePrice"] [[data]])))

(defn third-exercise [list-items]
  (let [data (->> list-items
                  (group-by #(get % "brandName"))
                  (fmap count)
                  json/write-str)]
    (spit "third.json" data)))

(defn get-all-images-from-nested-map [all-data]
  (if (empty? all-data)
    '()
    (let [head (first all-data)]
      (concat '()
              (cond
                (map? head) (get-all-images-from-nested-map head)
                (and (coll? head) (coll? (second head))) (get-all-images-from-nested-map (second head))
                (and (coll? head) (= "image" (first head))) (conj '() (last head))
                :else '())
              (get-all-images-from-nested-map (rest all-data))))))

(defn get-file-from-url [url]
  (let [last-index (clojure.string/last-index-of url "/")]
    (if (nil? last-index)
      url
      (subs url (inc last-index)))))

(defn fourth-exercise [data]
  (let [images (->> data
                    get-all-images-from-nested-map
                    (map get-file-from-url)
                    distinct
                    (map vector))]
    (write-csv "fourth.csv" ["images"] images)))

(defn -main [& _]
  (let [initial-data (json/read-str (slurp "lipstick.json"))]
    (first-exercise (get-in initial-data ["mods" "listItems"]))
    (second-exercise (get-in initial-data ["mods" "listItems"]))
    (third-exercise (get-in initial-data ["mods" "listItems"]))
    (fourth-exercise initial-data)))
