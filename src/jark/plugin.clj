(ns jark.plugin
  (:gen-class)
  (:refer-clojure :exclude [list find alias load])
  (:require jark.utils.ns))

(defn list
  "List all loaded plugins"
  []
  (let [ns-strings       (map #(.toString %) (jark.utils.ns/namespaces))
        jark-namespaces  (filter #(.startsWith % "jark") ns-strings)
        names            (sort (map #((clojure.string/split % #"\.") 1) jark-namespaces))
        ns-exceptions    ["utils" "server" "plugin"]]
    (seq (clojure.set/difference (set names) (set ns-exceptions)))))

(defn load
  "Load a plugin"
  [path])
