(ns jark.utils.ns
  (:gen-class)
  (:import (java.io File FileNotFoundException))
  (:require jark.cp)
  (:require jark.utils.pp)
  (:require clojure.set)
  (:use clojure.pprint)
  (:use clojure.tools.namespace))

; inlining from clojure.contrib.ns-utils
(defn ns-vars
  "Returns a sorted seq of symbols naming public vars in a namespace"
  [ns]
  (sort (map first (ns-publics ns))))

(defn namespaces []
  (find-namespaces-on-classpath))

(defn searched-namespaces []
  (let [nss (namespaces)]
    (if (> (count nss) 0)
      nss
      (map ns-name (all-ns)))))

(defn starting-with [module]
  (sort (filter #(. (str %) startsWith module) (searched-namespaces))))

(defn containing-str [module]
  (sort (filter #(. (str %) contains module) (searched-namespaces))))

(defn require-ns [n]
  (require (symbol n)))

(defn fun? [f]
  (instance? clojure.lang.IFn f))

(defn fns [n]
  (require-ns n)
  (let [namespace (symbol n)
        vars      (vec (ns-vars namespace))
        fns-list  (filter #(fun? %) vars)
        filtered-fns (filter (complement #(.startsWith % "-main")) (map #(.toString %) fns-list))]
    (sort filtered-fns)))

(defn fn-args [n f]
  (nthnext
   (flatten (:arglists (meta (eval (read-string (format "#'%s/%s" n f)))))) 0))

(defn fn-doc [n f]
  (:doc (meta (eval (read-string (format "#'%s/%s" n f))))))

(defn fn-usage [n f]
  (str "USAGE: " f " " (fn-args n f)))

(defn help
  ([n]
   (println n)
   (require-ns n)
   (into {} (map #(vector % (fn-doc n %)) (fns n))))

  ([n f]
   (println n "." f)
   (fn-usage n f)))

(defn about
  [n]
  (require-ns n)
  (println (let [p (into [] (fns n))]
                (cl-format true "~{~A ~}" p))))

(defn explicit-help [n f]
  (println "explicit help" n "." f)
  (if (= f "help")
    (help n)
    (help n f)))

(defn apply-fn [n f & args]
  (apply (resolve (symbol (str n "/" f))) args))

(defn load-module [module]
  (try
    (do (require-ns module)
      true)
    (catch FileNotFoundException e
      (do
        (println "jark: No such module" module)
        nil))))

(defn- resolve-cmd [module command]
  (resolve (symbol (str module "/" command))))

(defn dispatch-module-cmd
  ([printer module]
   (when (load-module module)
     (printer (help module))))

  ([printer module command & args]
   (if (or (= (first args) "help") (= command "help"))
     (explicit-help module command)
     (when (load-module module)
       (if-let [cmd (resolve-cmd module command)]
         (if-let [ret (apply cmd args)]
           (printer ret))
         (println module ": No such command" command))))))

