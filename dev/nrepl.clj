(ns ^{:clojure.tools.namespace.repl/load false} nrepl
  (:require [cider-nrepl.main]))

(def server (cider-nrepl.main/init ["cider.nrepl/cider-middleware"]))
