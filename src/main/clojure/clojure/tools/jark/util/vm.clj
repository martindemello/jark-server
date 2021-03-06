(ns clojure.tools.jark.util.vm
  (:gen-class)
  (:use server.socket)
  (:require [clojure.tools.nrepl :as nrepl])
  (:import (java.lang.management RuntimeMXBean ManagementFactory))
  (:import (java.net ServerSocket NetworkInterface))
  (:import (java.util Date)))

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

(defn to-mb [x] (str (mb x) " MB"))

(defn divmod [m n] [(quot m n) (rem m n)])

(defn fmt-time [ms]
  (let [[r ms] (divmod ms 1000)
        [r s ] (divmod  r 60)
        [r m ] (divmod  r 60)
        [d h ] (divmod  r 24)]
    (str d "d " h "h " m "m " s "." ms "s")))

(defn local-addresses []
  (->> (java.net.NetworkInterface/getNetworkInterfaces)
       enumeration-seq
       (map bean)
       (filter (complement :loopback))
       (mapcat :interfaceAddresses)
       (map #(.. % (getAddress) (getHostAddress)))))

(defn uptime [mx]
  (let [uptime    (.getUptime mx)
        uptime-ms (str (.toString uptime) "ms")]
    (str uptime-ms " (" (fmt-time uptime) ")")))

(defn pid
  "Display the PID of the current JVM"
  []
  (or
   (first (.. java.lang.management.ManagementFactory
              (getRuntimeMXBean)
              (getName)
              (split "@")))
   (System/getProperty "pid")))
