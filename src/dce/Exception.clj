(ns dce.Exception
  (:gen-class :extends RuntimeException
              :state payload
              :init init
              :constructors {[String Throwable clojure.lang.Keyword
                              java.util.Map]
                             [String Throwable]
                             [String clojure.lang.Keyword java.util.Map]
                             [String]
                             [clojure.lang.Keyword java.util.Map]
                             []}))

(defn -init
  ([message cause type data]
     [[message cause] (vary-meta data assoc :type type)])
  ([message type data]
     [[message] (vary-meta data assoc :type type)])
  ([type data]
     [[] (vary-meta data assoc :type type)]))

(defn -toString [e]
  (print-str (.getMessage e) (.payload e)))
