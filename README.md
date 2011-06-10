# Data-Carrying Exception

An exception for carrying data. Based on clojure.contrib.condition.

Differs from c.c.condition in a few ways:

* the exception is a descendant of RuntimeException rather than direct Throwable
* the exception may be treated as a Clojure map itself (rather than its metadata)
* catch clauses may be interspersed with handle causes.

## Usage

    (defn asplode [problem type]
      (dce.Exception/toss :message (str "Oh no! " problem") :type type))
      
    (handler-case :type
      (when-not (success?)
        (asplode "failed!" :failure))
      (handle :failure e
        (log/warn e "stuff failed, dude: " (:message e)))
      (handle :catastrophic-failure e
        (System/exit (:exit-code e))))

## License

Copyright (C) 2011 Kevin Downey, Stephen Gilardi, and Phil Hagelberg

Distributed under the Eclipse Public License, the same as Clojure.
