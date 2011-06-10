(ns dce.test.exception
  (:use [clojure.test]))

(deftest test-constructing
  (is (instance? dce.Exception (dce.Exception. {}))))

(deftest test-get
  (is (:data-conveying (dce.Exception. {:data-conveying true}))))

(deftest test-str
  (is (= (str {:data-conveying true})
         (str (dce.Exception. {:data-conveying true})))))
