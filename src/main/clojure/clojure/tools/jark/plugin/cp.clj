(ns clojure.tools.jark.plugin.cp
  (:require [clojure.tools.jark.util.cp :as util.cp])
  (:use [clojure.string :only (split)])
  (:refer-clojure :exclude [list])
  (:import (java.net URL URLClassLoader))
  (:import (java.lang.reflect Method))
  (:import (java.io File))
  (:gen-class))

(defn ls
  "Lists all the entries in CLASSPATH"
  []
  (let [urls (seq (.getURLs (java.lang.ClassLoader/getSystemClassLoader)))]
    (map (memfn toString) urls))) 

(defn list
  "Lists all the entries in CLASSPATH"
  [] (ls))

(defn exists?
  "Checks if the given entry exists in CLASSPATH"
  [path]
  (not (empty? (filter #(. (str %) contains path) (ls)))))

(defn add
  "Adds an entry to CLASSPATH"
  ([] "Usage: jark cp add PATH(s)")
  ([#^String path]
     (cond
      (util.cp/jar? path) (util.cp/add path)
      (util.cp/dir? path) (let [jars (util.cp/list-jars path)]
                       (if (empty? jars)
                         (println "No jars found in directory")
                         (doseq [jar (util.cp/list-jars path)]
                           (if (exists? jar)
                             (println (str jar " already exists in classpath"))
                             (do (util.cp/add jar)
                                 (println (str "Added jar " path))))))
                       (util.cp/add path))
      :else "Not a valid path")))
