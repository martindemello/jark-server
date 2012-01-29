(ns jark.vm
  (:gen-class)
  (:require jark.util.vm)
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
  (let [before (jark.util.vm/used-mem)]
    (loop [i 0]
      (jark.util.vm/run-gc)
      (if (< i 4)
        (recur (inc i))))
    (str "Freed " (jark.util.vm/mb (- before (jark.util.vm/used-mem))) " MB of memory")))

(defn uptime
  "Display uptime of the JVM"
  []
  (let [mx        (ManagementFactory/getRuntimeMXBean)]
    (jark.util.vm/uptime mx)))

(defn stat
  "Display JVM runtime stats"
  []
  (let [mx      (ManagementFactory/getRuntimeMXBean)
        gmxs    (ManagementFactory/getGarbageCollectorMXBeans)
        cmx     (ManagementFactory/getCompilationMXBean)
        omx     (ManagementFactory/getOperatingSystemMXBean)
        props {"Load Average"      (.getSystemLoadAverage omx)
               "Heap Mem Total"    (jark.util.vm/to-mb (jark.util.vm/total-mem))
               "Heap Mem Used"     (jark.util.vm/to-mb (jark.util.vm/used-mem))
               "Heap Mem Free"     (jark.util.vm/to-mb (jark.util.vm/free-mem))
               "GC Interval"       (map #(str (.getName %) ":" (.getCollectionTime %)) gmxs)
               "JIT Name"          (.getName cmx)
               "Processors"        (.getAvailableProcessors omx)
               "Uptime"            (jark.util.vm/uptime mx)}]
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
