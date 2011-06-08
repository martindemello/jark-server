(ns jark.pp
  (:use clojure.contrib.pprint)
  (:gen-class))

(defn pp-plist [p]
  (cl-format true "~{~20A  ~A~%~}" p))

(defn pp-map [m]
  (let [p (mapcat #(vector (key %) (val %)) m)]
    (pp-plist p)))

(defn pp-list [xs]
  (doseq [i xs]
    (println i)))

(defmulti pp-form class)

(defmethod pp-form clojure.lang.PersistentArrayMap [m] (pp-map m))
  
(defmethod pp-form String [s] (println s))

(defmethod pp-form clojure.lang.IPersistentVector [c] (pp-list c))

(defmethod pp-form java.util.Collection [xs] (pp-list xs))

(defmethod pp-form clojure.lang.LazySeq [c] (pp-list c))

(defmethod pp-form :default [s]
           (println (type s))
           (println 2))
(prefer-method pp-form clojure.lang.IPersistentVector java.util.Collection)

(prefer-method pp-form clojure.lang.LazySeq java.util.Collection)
