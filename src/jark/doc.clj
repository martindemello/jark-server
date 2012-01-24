(ns jark.doc
  (:refer-clojure :exclude [bytes])
  (:use clojure.contrib.json)
  (:use clojure.contrib.http.agent)
  (:require [cd-client.core :as cd]))

(defn- search-format [w x]
  (let [format-string (apply str ["~" w "A~A\n"])]
  (clojure.pprint/cl-format nil format-string (first x) (second x))))

(defn- pp-search [res]
  (let [vs (map #(vector (:name %) (:ns %)) res)
        w  (apply max (map #(count (first %)) vs))
        lines (map #(search-format (+ 2 w) %) vs)]
    (print (apply str lines))))

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
