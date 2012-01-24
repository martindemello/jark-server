(ns jark.pp
  (:use clojure.contrib.pprint)
  (:gen-class))

(defn pp-plist [p]
  (cl-format true "~{~20A  ~A~%~}" p))

(defn- make-row-format [cols padding]
  (apply str (conj (map #(str "~" (+ padding %) "A") cols) "\n")))

(defn- format-row [format-string row]
  (apply clojure.pprint/cl-format (apply conj [nil format-string] row)))

(defn pp-table [padding table]
  (let [widths (reduce (partial map max)
                       (map (partial map count) table))
        format-string (make-row-format widths padding)
        lines (map #(format-row format-string %) table)]
    (print (apply str lines))))

(defn pp-map [m]
  (let [p (mapcat #(vector (key %) (val %)) m)]
    (pp-plist p)))

(defn pp-list [xs]
  (doseq [i xs]
    (println i)))

(defmulti pp-form class)

(defmethod pp-form clojure.lang.PersistentArrayMap [m] (pp-map m))

(defmethod pp-form clojure.lang.PersistentHashMap [m] (pp-map m))
  
(defmethod pp-form String [s] (println s))

(defmethod pp-form clojure.lang.IPersistentVector [c] (pp-list c))

(defmethod pp-form java.util.Collection [xs] (pp-list xs))

(defmethod pp-form clojure.lang.LazySeq [c] (pp-list c))

(defmethod pp-form :default [s]
           (println (type s))
           (println 2))
(prefer-method pp-form clojure.lang.IPersistentVector java.util.Collection)

(prefer-method pp-form clojure.lang.LazySeq java.util.Collection)
