(ns jark.plugin
  (:gen-class)
  (:refer-clojure :exclude [list find alias load])
  (:require jark.util.ns)
  (:import (java.io File FileNotFoundException))
  (:require jark.cp))

(defn list
  "List all loaded plugins"
  []
  (let [ns-strings       (map #(.toString %) (jark.util.ns/namespaces))
        jark-namespaces  (filter #(.startsWith % "jark") ns-strings)
        names            (sort (map #((clojure.string/split % #"\.") 1) jark-namespaces))
        ns-exceptions    ["util" "server" "plugin"]]
    (seq (clojure.set/difference (set names) (set ns-exceptions)))))

(defn load
  "Load a plugin"
  [plugin]
  (let [basename (.getParentFile (File. plugin))]
    (jark.cp/add (.toString basename)))
  (load-file plugin)
  (require 'jark.foo))
