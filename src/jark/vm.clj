(ns jark.vm
  (:gen-class)
  (:require [jark.utils.vm :as utils])
  (:use server.socket)
  (:require [clojure.tools.nrepl :as nrepl])
  (:import (threads SystemThreadList))
  (:import (java.lang.management RuntimeMXBean ManagementFactory))
  (:import (java.net ServerSocket))
  (:import (java.util Date))
  (:require jark.ns))

(defn gc []
  (let [before (utils/used-mem)]
    (loop [i 0]
      (utils/run-gc)
      (if (< i 4)
        (recur (inc i))))
    (str "Freed " (utils/mb (- before (utils/used-mem))) " MB of memory")))

(defn stats
  "Display current statistics of the JVM"
  []
  (let [mx     (ManagementFactory/getRuntimeMXBean)
        uptime (.getUptime mx)
        props {"Mem total"    (utils/to-mb (utils/total-mem))
               "Mem used"     (utils/to-mb (utils/used-mem))
               "Mem free"     (utils/to-mb (utils/free-mem))
               "Start time"   (.toString (Date. (.getStartTime mx)))
               "Uptime"       (str
                               (.toString (utils/mins uptime)) "m" " | "
                               (.toString (utils/secs uptime)) "s")}]
    props))

(defn uptime
  "Display uptime of the JVM"
  []
  (let [mx        (ManagementFactory/getRuntimeMXBean)
        uptime    (.getUptime mx)
        uptime-ms (str (.toString uptime) "ms")]
    (str uptime-ms " (" (utils/fmt-time uptime) ")")))

(defn threads
  "Display all running threads"
  []
  (let [stl (SystemThreadList.)]
    (map #(.getName %) (.getAllThreads stl))))

(defn pid []
  (or
    (first (.. java.lang.management.ManagementFactory (getRuntimeMXBean) (getName) (split "@")))
    (System/getProperty "pid")))


(defn -main [port]
  (create-repl-server (utils/random-port))
  (nrepl/start-server (Integer. port))
  (System/setSecurityManager nil))
