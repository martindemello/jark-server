(ns jark.cp
  (:use [clojure.string :only (split)])
  (:refer-clojure :exclude [list])
  (:import (java.net URL URLClassLoader))
  (:import (java.lang.reflect Method))
  (:import (java.io File))
  (:use clojure.java.classpath)
  (:gen-class))

(defn ls
  "Lists all the entries in CLASSPATH"
  []
  (let [urls (seq (.getURLs (java.lang.ClassLoader/getSystemClassLoader)))]
    (map (memfn toString) urls))) 

(defn list [] (ls))

(defn add
  "Adds an entry to CLASSPATH, dynamically"
  ([] "Usage: jark cp add PATH(s)")
  ([#^String jarpath] 
     (let [#^URL url   (.. (File. jarpath) toURI toURL) 
           cls         (. (URLClassLoader. (into-array URL [])) getClass) 
           acls        (into-array Class [(. url getClass)]) 
           aobj        (into-array Object [url]) 
           #^Method m  (. cls getDeclaredMethod "addURL" acls)]
       (doto m
         (.setAccessible true) 
         (.invoke (ClassLoader/getSystemClassLoader) aobj))
       nil)))
