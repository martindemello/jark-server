(ns clojure.tools.jark.plugin
  (:gen-class)
  (:refer-clojure :exclude [list find alias load])
  (:require [clojure.tools.jark.util.ns :as util.ns])
  (:import (java.io File FileNotFoundException))
  (:require clojure.string)
  (:require [clojure.tools.jark.util.cp :as util.cp]))

(defn list
  "List all loaded plugins"
  []
  (let [ns-strings       (map #(.toString %) (util.ns/namespaces))
        jark-namespaces  (filter #(.startsWith % "clojure.tools.jark.plugin") ns-strings)
        names            (sort (map #((clojure.string/split % #"\.") 1) jark-namespaces))]
    names))
        
(defn load
  "Load a plugin"
  [plugin]
  (let [basename (.getParentFile (File. plugin))]
    (util.cp/add (.toString basename)))
  (load-file plugin))
