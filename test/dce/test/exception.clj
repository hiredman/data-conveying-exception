(ns dce.test.exception
  (:use [clojure.test]
        [dce.handle]
        [dce.Exception :only [throw+]]))

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
                 (throw+ :message "duuuude" :type :funky :funkiness :lots)
                 (handle :non-funky _)
                 (handle :funky {:keys [funkiness]}
                         funkiness)))))

(defn funky? [{:keys [funkiness]}]
  (= :most-excellent funkiness))

(defmacro mega-try [body]
  `(try+
    ~body
    (catch :funky {key# :key}
      key#)
    (catch :non-funky _#
      :bummer-dude)
    (catch funky? x#
      :external-pred)
    (catch :locals e#
      e#)
    (catch Exception _#
      :exception)))

(deftest test-try+
  (is (= :destructured (mega-try
                        (throw+ :message "duuuude" :key :destructured :funky true))))
  (is (= :exception (mega-try
                     (throw+ (Exception. "whoops")))))
  (is (= :external-pred (mega-try
                         (throw+ :funkiness :most-excellent))))
  (is (thrown? Throwable (mega-try (throw+ (Throwable. "hi"))))))

(deftest test-locals
  (is (= :not-variable ('a-local (:locals (mega-try
                                           (let [a-local :not-variable]
                                             (throw+ {}))))))))
