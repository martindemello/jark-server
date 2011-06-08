(ns jark.swank
  (:gen-class)
  (:use swank.swank))

(defn start
  "Start a swank repl"
  [host port]
  (let [port (Integer. port)]
    (ignore-protocol-version nil)
    (start-repl port :host host)
    nil))
