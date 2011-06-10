(ns dce.Exception
  (:gen-class :extends RuntimeException
              :implements [clojure.lang.IPersistentMap
                           clojure.lang.MapEquivalence
                           clojure.lang.IFn
                           java.util.Map]
              :state state
              :init init
              :constructors {[java.util.Map]
                             [String Throwable]}))

(defn -init
  ([{:keys [message cause] :as state}]
     [[message cause] state]))

(defn -toString [self]
  (.toString (.state self)))

;; ILookup

(defn -valAt
  ([self key]
     (.valAt (.state self) key))
  ([self key not-found]
     (.valAt (.state self) key not-found)))

;; Seqable

(defn -seq [self]
  (.seq (.state self)))

;; Associative

(defn -containsKey [self key]
  (.containsKey (.state self)))

(defn -entryAt [self key]
  (.entryAt (.state self)))

(defn -assoc [self key val]
  (dce.Exception. (assoc (.state self) key val)))

;; IPersistentMap

(defn -without [self key]
  (dissoc (.state self) key))

;; IPersistentCollection

(defn -cons [self o]
  (dce.Exception. (.cons (.state self) o)))

(defn -empty [self]
  (dce.Exception. {}))

(defn -equiv [self o]
  (.equiv (.state self) (.state o)))

;; Iterable

(defn -iterator [self]
  (.iterator (.state self)))

;; IFn

(def -invoke -valAt)

;; Counted

(defn -count [self]
  (count (.state self)))

(defn toss [& {:as args}]
  (throw (dce.Exception. args)))
