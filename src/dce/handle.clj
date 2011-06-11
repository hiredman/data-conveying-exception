(ns dce.handle)

(def ^{:doc "While a handler is running, bound to the selector returned by the
  handler-case dispatch-fn for the exception."} *selector*)

(defn- separate [f s] ; lifted from c.c.seq
  [(filter f s) (filter (complement f) s)])

(defn starts-with-fn [x]
  (fn [c] (and (coll? c) (= x (first c)))))

(defmacro handler-case
  "Executes body in a context where raised exceptions can be handled.

  dispatch-fn accepts a raised data-conveying Exception and returns a selector
  used to choose a handler. Commonly, dispatch-fn will be :type to dispatch
  on the condition's :type value.

  Handlers are forms within body:

    (handle key ex
      ...)

  If a data-conveying Exception is raised, executes the body of the
  first handler whose key satisfies (isa? selector key). If no
  handlers match, re-raises the condition.

  While a handler is running, *condition* is bound to the condition being
  handled and *selector* is bound to to the value returned by dispatch-fn
  that matched the handler's key."
  [dispatch-fn & body]
  (let [[handlers code] (separate (starts-with-fn 'handle) body)
        [catches code] (separate (starts-with-fn 'catch) code)
        exception (gensym)]
    `(try
       ~@code
       (catch dce.Exception ~exception
         (binding [*selector* (~dispatch-fn ~exception)]
           (cond
            ~@(mapcat
               (fn [[_ key local & body]]
                 `[(isa? *selector* ~key) (let [~local ~exception] ~@body)])
               handlers)
            :else (throw ~exception))))
       ~@catches)))

(defn- body-form? [x]
  (or (not (seq? x)) (not= 'catch (first x))))

(defn- class-catch? [x]
  (and (symbol? x) (class? (resolve x))))

(defmacro try+
  [& body]
  (let [[tried catches] (partition-by body-form? body)
        exception (gensym)]
    `(try
       ~@tried
       (catch Throwable ~exception
         (cond
          ~@(mapcat
             (fn [[_ pred local & body]]
               (let [pred (if (class-catch? pred)
                            `(fn [x#] (instance? ~pred x#))
                            pred)]
                 `[(~pred ~exception) (let [~local ~exception] ~@body)]))
             catches)
          :else (throw ~exception))))))
