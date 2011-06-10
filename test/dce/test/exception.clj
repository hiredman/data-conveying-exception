(ns dce.test.exception
  (:use [clojure.test]
        [dce.handle]
        [dce.Exception :only [toss]]))

(def e (dce.Exception. {:a :bob :b :alice}))

(deftest test-constructing
  (is (instance? dce.Exception e)))

(deftest test-get
  (is (:a e)))

(deftest test-str
  (is (= "{:a :bob, :b :alice}" (str e))))

(deftest test-destructure
  (is (= [:bob :alice]
         (let [{:keys [a b]} e]
           [a b]))))

(deftest test-catching
  (is
   (try
     (when true
       (throw e))
     (catch Exception x
       (:a x)))))

(deftest test-keys-vals
  (is (= [:a :b] (keys e)))
  (is (= [:bob :alice] (vals e))))

(deftest test-assoc-dissoc
  (is (= :charlie (:c (assoc e :c :charlie))))
  (is (= [:b] (keys (dissoc e :a)))))

(deftest test-count
  (is (= 2 (count e))))

(deftest test-ifn
  (is (= :bob (e :a))))

(deftest test-handler-case
  (is (= :lots (handler-case :type
                 (toss :message "duuuude" :type :funky :funkiness :lots)
                 (handle :non-funky _)
                 (handle :funky {:keys [funkiness]}
                         funkiness)))))

(defn funky? [])

(deftest test-try+
  (is (= :lots (try+
                (toss :message "duuuude" :funky true :funkiness :lots)
                (catch :funky {:keys [funkiness]}
                  funkiness)
                (catch :non-funky _
                  :bummer-dude)
                (catch funky? x)
                (catch Exception _)))))
