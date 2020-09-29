(ns exercise.core-test
  (:require [clojure.test :refer :all]
            [exercise.core :refer :all]))

(deftest get-file-from-url-test
  (testing "getting last part from the URL"
    (is (= (get-file-from-url "http://www.example.com/index.html") "index.html"))
    (is (= (get-file-from-url "https://th-test-11.slatic.net/p/8a54cf4ea029b3de8ad0df2fb170a646.jpg") "8a54cf4ea029b3de8ad0df2fb170a646.jpg"))
    (is (= (get-file-from-url
            "https://th-test-11.slatic.net/p/5/sivanna-colors-matte-stay-lipstick-kiss-me-sivanna-6504-73262549-bf3c30c410443aed84afe28e2df0bf6f-catalog_233.jpg")
           "sivanna-colors-matte-stay-lipstick-kiss-me-sivanna-6504-73262549-bf3c30c410443aed84afe28e2df0bf6f-catalog_233.jpg"))))

(deftest average-test
  (testing "calculating averages"
    (is (= (average [1 2 3]) 2))
    (is (= (average []) 0))
    (is (= (average [-1 -2 -3]) -2))
    (is (= (average [0]) 0))
    (is (= (average [1 2 -3]) 0))))

(deftest get-all-images-from-nested-map-test
  (testing "extracting images from a collection"
    (is (= (get-all-images-from-nested-map {}) []))
    (is (= (get-all-images-from-nested-map {:some 3 "image" 55 :other {"image" 123}}) [55 123]))
    (is (= (get-all-images-from-nested-map {:some 3 "image" 6 :z {"image" 7} :vec [{"image" 8} {"image" 10} {:image 77}]}) (6 7 8 10)))))
