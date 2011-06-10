(ns dce.Exception
  (:gen-class :extends RuntimeException
              :implements [clojure.lang.ILookup]
              :state data
              :init init
              :constructors {[java.util.Map]
                             [String Throwable]}))

(defn -init
  ([{:keys [message cause] :as data}]
     [[message cause] data]))

(defn -valAt
  ([self key]
     (.valAt (.data self) key))
  ([self key not-found]
     (.valAt (.data self) key not-found)))

(defn -toString [e]
  (.toString (.data e)))
