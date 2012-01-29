(ns jark.util.cp
  (:import (java.io File))
  (:import (java.net URL URLClassLoader))
  (:use clojure.java.classpath)
  (:import (java.lang.reflect Method)))

(defn file-exists? [file]
  (.exists (File. file)))

(defn add [file]
  (let [#^URL url   (.. (File. file) toURI toURL) 
        cls         (. (URLClassLoader. (into-array URL [])) getClass) 
        acls        (into-array Class [(. url getClass)]) 
        aobj        (into-array Object [url]) 
        #^Method m  (. cls getDeclaredMethod "addURL" acls)]
    (doto m
      (.setAccessible true) 
      (.invoke (ClassLoader/getSystemClassLoader) aobj))
    nil))

(defn file? [path]
  (.isFile (File. path)))

(defn dir? [path]
  (.isDirectory (File. path)))

(defn jar? [path]
  (and (file? path) (clojure.java.classpath/jar-file? (File. path))))

(defn list-jars [dir]
  (when (dir? dir)
    (filter #(jar-file? (File. %)) (map #(.toString %) (.listFiles (File. dir))))))

