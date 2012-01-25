(ns jark.doc
  (:refer-clojure :exclude [bytes])
  (:use clojure.data.json)
  (:use jark.pp)
  (:require [cd-client.core :as cd]))

(defn- pp-search [res]
  (pp-table 2 (map #(vector (:name %) (:ns %)) res)))

(defn search [& args]
  (pp-search (apply cd/search args)))

(defn examples
  ([function]      (examples "clojure.core" function))
  ([nspc function] (cd/pr-examples nspc function)))

(defn comments
  ([function]      (comments "clojure.core" function))
  ([nspc function] (cd/pr-comments nspc function)))

(defn see-also
  ([function]      (see-also "clojure.core" function))
  ([nspc function] (map :name (cd/see-also nspc function))))
