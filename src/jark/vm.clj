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

(defn gc
  "Run Garbage Collection on the JVM"
  []
  (let [before (utils/used-mem)]
    (loop [i 0]
      (utils/run-gc)
      (if (< i 4)
        (recur (inc i))))
    (str "Freed " (utils/mb (- before (utils/used-mem))) " MB of memory")))

(defn uptime
  "Display uptime of the JVM"
  []
  (let [mx        (ManagementFactory/getRuntimeMXBean)]
    (utils/uptime mx)))

(defn stat
  "Display JVM runtime stats"
  []
  (let [mx      (ManagementFactory/getRuntimeMXBean)
        gmxs    (ManagementFactory/getGarbageCollectorMXBeans)
        cmx     (ManagementFactory/getCompilationMXBean)
        omx     (ManagementFactory/getOperatingSystemMXBean)
        props {"Load Average"      (.getSystemLoadAverage omx)
               "Heap Mem Total"    (utils/to-mb (utils/total-mem))
               "Heap Mem Used"     (utils/to-mb (utils/used-mem))
               "Heap Mem Free"     (utils/to-mb (utils/free-mem))
               "GC Interval"       (map #(str (.getName %) ":" (.getCollectionTime %)) gmxs)
               "JIT Name"          (.getName cmx)
               "Processors"        (.getAvailableProcessors omx)
               "Uptime"            (utils/uptime mx)}]
    props))

(defn threads
  "Display all running threads"
  []
  (let [stl (SystemThreadList.)]
    (map #(.getName %) (.getAllThreads stl))))

(defn pid
  "Display the PID of the current JVM"
  []
  (or
   (first (.. java.lang.management.ManagementFactory
              (getRuntimeMXBean)
              (getName)
              (split "@")))
   (System/getProperty "pid")))

(defn info
  "Display JVM System information"
  []
  (sort (map #(.toString %) (System/getProperties))))

(defn version
  "Display JVM version"
  []
  (System/getProperty "java.vm.name"))

(defn pwd
  "Displays the present working directory of the JVM. Sets it if given a path"
  ([]     (. (java.io.File. ".") getCanonicalPath))
  ([path]
     "Oops. Cannot set the PWD, yet"))
