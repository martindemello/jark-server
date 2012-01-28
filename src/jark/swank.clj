(ns jark.swank
  (:gen-class)
  (:use swank.swank))

(defn start
  "Start a Swank REPL sever"
  ([] (start "0.0.0.0" "4005"))
  ([host port]
     (let [port (Integer. port)]
       (ignore-protocol-version nil)
       (start-repl port :host host)
       nil)))

(defn stop
  "Stop Swank REPL server"
  []
  "Not implemented yet")
