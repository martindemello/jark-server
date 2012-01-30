(ns clojure.tools.jark.plugin.ns
  (:gen-class)
  (:require [clojure.tools.jark.util.ns :as util.ns])
  (:require [clojure.tools.jark.util.np :as util.cp])
  (:refer-clojure :exclude [list find alias load])
  (:import (java.io File FileNotFoundException)))


(defn list
  "List all namespaces in the classpath. Optionally takes a namespace prefix"
  ([]
   (sort (util.ns/namespaces)))
  ([module]
   (util.ns/starting-with (str module "."))))

(defn find
  "Find all namespaces containing the given name"
  ([] "Usage: jark ns find PATTERN")
  ([module]
     (util.ns/containing-str module)))

(defn load
  "Loads the given clj file, and adds relative classpath"
  ([] "Usage: jark ns load FILE")
  ([file]
     (let [basename (.getParentFile (File. file))]
       (util.cp/add (.toString basename)))
     (load-file file)))
