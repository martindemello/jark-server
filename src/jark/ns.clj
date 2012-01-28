(ns jark.ns
  (:gen-class)
  (:require [jark.utils.ns :as utils])
  (:refer-clojure :exclude [list find alias load])
  (:import (java.io File FileNotFoundException))
  (:require jark.cp))

(defn list
  "List all namespaces in the classpath. Optionally takes a namespace prefix"
  ([]
   (sort (utils/namespaces)))
  ([module]
   (utils/starting-with (str module "."))))

(defn find
  "Find all namespaces containing the given name"
  [module]
  (utils/containing-str module))

(defn load
  "Loads the given clj file, and adds relative classpath"
  [file]
  (let [basename (.getParentFile (File. file))]
    (jark.cp/add (.toString basename)))
  (load-file file))
