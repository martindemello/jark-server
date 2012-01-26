(ns jark.swank
  (:gen-class)
  (:use swank.swank))

(defn start
  "Start a swank repl"
  ([] (start "0.0.0.0" "4005"))
  ([host port]
     (let [port (Integer. port)]
       (ignore-protocol-version nil)
       (start-repl port :host host)
       nil)))
