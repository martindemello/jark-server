(ns jark.vm
  (:gen-class)
  (:use clojure.contrib.server-socket)
  (:require [clojure.tools.nrepl :as nrepl])
  (:use clojure.contrib.pprint)
  (:import (jark SystemThreadList))
  (:import (java.lang.management RuntimeMXBean ManagementFactory))
  (:import (java.net ServerSocket))
  (:import (java.util Date))
  (:require jark.ns))


(defn used-mem []
  (let [rt (. Runtime getRuntime)]
    (- (. rt totalMemory) (. rt freeMemory))))

(defn free-mem []
  (let [rt (. Runtime getRuntime)]
    (. rt freeMemory)))

(defn total-mem []
  (let [rt (. Runtime getRuntime)]
    (. rt totalMemory)))

(defn run-gc []
  (let [rt (. Runtime getRuntime)]
    (loop [m1 (used-mem)
	   m2 1000000000000
	   i 0]
	(. rt runFinalization)
	(. rt gc)
	(. Thread yield)
	(if (and (< i 500)
          (< m1 m2))
   (recur (used-mem) m1 (inc i))))))

(defn mb [bytes]
  (int (/ bytes (* 1024.0 1024.0))))

(defn mins [ms]
  (int (/ ms 60000.0)))

(defn secs [ms]
  (int (/ ms 1000.0)))

(defn gc []
  (let [before (used-mem)]
    (loop [i 0]
      (run-gc)
      (if (< i 4)
        (recur (inc i))))
    (str "Freed " (mb (- before (used-mem))) " MB of memory")))

(defn- to-mb [x] (str (mb x) " MB"))

(defn divmod [m n] [(quot m n) (rem m n)])

(defn- fmt-time [ms]
  (let [[r ms] (divmod ms 1000)
        [r s ] (divmod  r 60)
        [r m ] (divmod  r 60)
        [d h ] (divmod  r 24)]
    (str d "d " h "h " m "m " s "." ms "s")))

(defn stats
  "Display current statistics of the JVM"
  []
  (let [mx     (ManagementFactory/getRuntimeMXBean)
        uptime (.getUptime mx)
        props {"Mem total"    (to-mb total-mem)
               "Mem used"     (to-mb used-mem)
               "Mem free"     (to-mb free-mem)
               "Start time"   (.toString (Date. (.getStartTime mx)))
               "Uptime"       (str
                               (.toString (mins uptime)) "m" " | "
                               (.toString (secs uptime)) "s")}]
    props))

(defn uptime
  "Display uptime of the JVM"
  []
  (let [mx        (ManagementFactory/getRuntimeMXBean)
        uptime    (.getUptime mx)
        uptime-ms (str (.toString uptime) "ms")]
    (str uptime-ms " (" (fmt-time uptime) ")")))

(defn stop []
  (. System (exit 0)))

(defn threads
  "Display all running threads"
  []
  (let [stl (SystemThreadList.)]
    (map #(.getName %) (.getAllThreads stl))))

(defn random-port []
  (let [s     (new ServerSocket 0)
        port  (.getLocalPort s)]
    (.close s)
    port))

(defn get-pid []
  (or
    (first (.. java.lang.management.ManagementFactory (getRuntimeMXBean) (getName) (split "@")))
    (System/getProperty "pid")))

(defn -main [port]
  (create-repl-server (random-port))
  (nrepl/start-server (Integer. port))
  (System/setSecurityManager nil))
