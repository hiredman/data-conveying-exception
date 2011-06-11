# Data-Carrying Exception

An exception for carrying data. Based on clojure.contrib.condition.

Differs from c.c.condition in a few ways:

* the exception is a descendant of RuntimeException rather than direct Throwable.
* the exception may be treated as a Clojure map itself.

Also introduces new awesomeness in the form of try+ and throw+. throw+
takes an Exception, a map, or varargs, and constructs a data-conveying
Exception in the latter two cases. try+ can destructure data-conveying
exceptions in catches.

## Usage

    (defn asplode [problem type]
      (dce.Exception/throw+ :message (str "Oh no! " problem) :failure true))
      
    (try+
      (when-not (success?)
        (asplode "failed!"))
      (catch :failure e
        (log/warn e "stuff failed, dude: " (:message e)))
      (catch :catastrophic-failure {:keys [exit-code]}
        (System/exit exit-code))
      (catch java.io.IOException e
        (log/info "whatever; who cares.")))

## License

Copyright (C) 2011 Kevin Downey, Stephen Gilardi, and Phil Hagelberg

Distributed under the Eclipse Public License, the same as Clojure.
