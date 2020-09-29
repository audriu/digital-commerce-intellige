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

(defn get-all-images-from-nested-map
  "This solution is vulnerable to stackoverflow. I will reimplement if I have enough time."
  [all-data]
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

(def cash-spent (atom 0))
(defn accumulate-price [item limit]
  (swap! cash-spent #(+ % (get item "price")))
  (>= limit @cash-spent))

(defn fifth-exercise
  "This implementation will choose cheapest items from every brand until there are money left.
  Ths could use any sort of greedy algorithm having enough time/need to implement."
  [goods]
  (let [cash-available 250
        purchases (->> goods
                       (group-by #(get % "brandName"))
                       (fmap #(sort-by (fn [item] (get "price " item)) %))
                       (fmap first)
                       (map val)
                       (map #(update % "price" (fn [param1] (Float/parseFloat param1))))
                       (sort-by #(get % "price"))
                       (take-while #(accumulate-price % cash-available))
                       (map #(select-keys % ["itemId" "brandName" "price"]))
                       (map vals))]
    (write-csv "fifth.csv" ["itemId" "brandName" "price"] purchases)))

(defn -main [& _]
  (let [initial-data (json/read-str (slurp "lipstick.json"))]
    (first-exercise (get-in initial-data ["mods" "listItems"]))
    (second-exercise (get-in initial-data ["mods" "listItems"]))
    (third-exercise (get-in initial-data ["mods" "listItems"]))
    (fourth-exercise initial-data)
    (fifth-exercise (get-in initial-data ["mods" "listItems"]))))
