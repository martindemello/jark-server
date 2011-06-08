(ns jark.cp
  (:use [clojure.string :only (split)])
  (:import (java.net URL URLClassLoader))
  (:import (java.lang.reflect Method))
  (:import (java.io File))
  (:use clojure.contrib.classpath)
  (:gen-class))

(defn ls
  "Lists all the entries in CLASSPATH"
  []
  (let [urls (seq (.getURLs (java.lang.ClassLoader/getSystemClassLoader)))]
    (map (memfn toString) urls))) 
  
(defn add
  "Adds an entry to CLASSPATH, dynamically"
  [#^String jarpath] 
 (let [#^URL url   (.. (File. jarpath) toURI toURL) 
       cls         (. (URLClassLoader. (into-array URL [])) getClass) 
       acls        (into-array Class [(. url getClass)]) 
       aobj        (into-array Object [url]) 
       #^Method m  (. cls getDeclaredMethod "addURL" acls)]
   (doto m
     (.setAccessible true) 
     (.invoke (ClassLoader/getSystemClassLoader) aobj))
   nil))

(defn exists?
  "Checks if the given entry exists in CLASSPATH"
  [path]
  (some #(= path %) (ls)))
