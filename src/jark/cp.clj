(ns jark.cp
  (:require [jark.util.cp :as cp])
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
  "Adds an entry to CLASSPATH, dynamically"
  ([] "Usage: jark cp add PATH(s)")
  ([#^String path]
     (cond
      (cp/jar? path) (cp/add path)
      (cp/dir? path) (let [jars (cp/list-jars path)]
                       (if (empty? jars)
                         (println "No jars found in directory")
                         (doseq [jar (cp/list-jars path)]
                           (if (exists? jar)
                             (println (str jar " already exists in classpath"))
                             (do (cp/add jar)
                                 (println (str "Added jar " path))))))
                       (cp/add path))
      :else "Not a valid path")))
