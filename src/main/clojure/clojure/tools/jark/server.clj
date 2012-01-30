(ns clojure.tools.jark.server
  (:gen-class)
  (:require [clojure.tools.jark.util.vm :as util.vm])
  (:require [clojure.tools.jark.util.ns :as util.ns])
  (:require [clojure.tools.jark.util.pp :as util.pp])
  (:use server.socket)
  (:import (java.net ServerSocket))
  (:require [clojure.tools.nrepl :as nrepl])
  (:use clojure.data.json))

(defn version [] "0.4.0")

(defn random-port []
  (let [s     (new ServerSocket 0)
        port  (.getLocalPort s)]
    (.close s)
    port))

(def dispatch
     (partial util.ns/dispatch-module-cmd util.pp/pp-form))

(def cli-json
     (partial util.ns/dispatch-module-cmd json-str))

(defn active-ip-address
  []
  (last (util.vm/local-addresses)))

(defn info []
  {"PID"  (util.vm/pid)
   "Host" (active-ip-address)
   "Port" 9000 })

(defn remote-server? [] true)

(defn find-latest-version
  [])

(defn -main [port]
  (create-repl-server (random-port))
  (nrepl/start-server (Integer. port))
  (System/setSecurityManager nil))
