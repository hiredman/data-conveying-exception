(ns dce.handle)

(defn- separate [f s] ; lifted from c.c.seq
  [(filter f s) (filter (complement f) s)])

(defn starts-with-fn [x]
  (fn [c] (and (coll? c) (= x (first c)))))

(def ^{:doc "While a handler is running, bound to the selector returned by the
  handler-case dispatch-fn for *condition*"} *selector*)

(defmacro handler-case
  "Executes body in a context where raised exceptions can be handled.

  dispatch-fn accepts a raised data-carrying Exception and returns a selector
  used to choose a handler. Commonly, dispatch-fn will be :type to dispatch
  on the condition's :type value.

  Handlers are forms within body:

    (handle key
      ...)

  If a data-carrying Exception is raised, executes the body of the
  first handler whose key satisfies (isa? selector key). If no
  handlers match, re-raises the condition.

  While a handler is running, *condition* is bound to the condition being
  handled and *selector* is bound to to the value returned by dispatch-fn
  that matched the handler's key."
  [dispatch-fn & body]
  (let [[handlers code] (separate (starts-with-fn 'handle) body)
        [catches code] (separate (starts-with-fn 'catch) code)]
    `(try
       ~@code
       (catch dce.Exception e#
         (binding [*selector* (~dispatch-fn e#)]
           (cond
            ~@(mapcat
               (fn [[_ key & body]]
                 `[(isa? *selector* ~key) (do ~@body)])
               handlers)
            :else (raise))))
       ~@catches)))
