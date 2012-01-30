(ns clojure.tools.jark.plugin.vm
  (:gen-class)
  (:require [clojure.tools.jark.util.vm :as util.vm])
  (:use server.socket)
  (:require [clojure.tools.nrepl :as nrepl])
  (:import (threads SystemThreadList))
  (:import (java.lang.management RuntimeMXBean ManagementFactory))
  (:import (java.net ServerSocket))
  (:import (java.util Date)))

(defn gc
  "Run Garbage Collection on the JVM"
  []
  (let [before (util.vm/used-mem)]
    (loop [i 0]
      (util.vm/run-gc)
      (if (< i 4)
        (recur (inc i))))
    (str "Freed " (util.vm/mb (- before (util.vm/used-mem))) " MB of memory")))

(defn uptime
  "Display uptime of the JVM"
  []
  (let [mx        (ManagementFactory/getRuntimeMXBean)]
    (util.vm/uptime mx)))

(defn stat
  "Display JVM runtime stats"
  []
  (let [mx      (ManagementFactory/getRuntimeMXBean)
        gmxs    (ManagementFactory/getGarbageCollectorMXBeans)
        cmx     (ManagementFactory/getCompilationMXBean)
        omx     (ManagementFactory/getOperatingSystemMXBean)
        props {"Load Average"      (.getSystemLoadAverage omx)
               "Heap Mem Total"    (util.vm/to-mb (util.vm/total-mem))
               "Heap Mem Used"     (util.vm/to-mb (util.vm/used-mem))
               "Heap Mem Free"     (util.vm/to-mb (util.vm/free-mem))
               "GC Interval"       (map #(str (.getName %) ":" (.getCollectionTime %)) gmxs)
               "JIT Name"          (.getName cmx)
               "Processors"        (.getAvailableProcessors omx)
               "Uptime"            (util.vm/uptime mx)}]
    props))

(defn threads
  "Display all running threads"
  []
  (let [stl (SystemThreadList.)]
    (map #(.getName %) (.getAllThreads stl))))

(defn pid
  "Display the PID of the current JVM"
  []
  (util.vm/pid))

(defn info
  "Display JVM System information"
  []
  (sort (map #(.toString %) (System/getProperties))))

(defn version
  "Display JVM version"
  []
  (System/getProperty "java.vm.name"))

(defn pwd
  "Display the present working directory of the JVM. Set it if given a path"
  ([]     (. (java.io.File. ".") getCanonicalPath))
  ([path]
     "Oops. Cannot set the PWD, yet"))
