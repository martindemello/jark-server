(ns jark.server
  (:gen-class)
  (:require jark.util.vm)
  (:require jark.util.ns)
  (:require jark.util.pp)
  (:use server.socket)
  (:import (java.net ServerSocket))
  (:require [clojure.tools.nrepl :as nrepl])
  (:use clojure.data.json)
  (:require jark.vm))

(defn version [] "0.4.0")

(defn random-port []
  (let [s     (new ServerSocket 0)
        port  (.getLocalPort s)]
    (.close s)
    port))

(def dispatch
     (partial jark.util.ns/dispatch-module-cmd jark.util.pp/pp-form))

(def cli-json
     (partial jark.util.ns/dispatch-module-cmd json-str))

(defn active-ip-address
  []
  (last (jark.util.vm/local-addresses)))

(defn info []
  {"PID"  (jark.vm/pid)
   "Host" (active-ip-address)
   "Port" 9000 })

(defn remote-server? [] true)

(defn find-latest-version
  [])

(defn -main [port]
  (create-repl-server (random-port))
  (nrepl/start-server (Integer. port))
  (System/setSecurityManager nil))
