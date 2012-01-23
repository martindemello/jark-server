(ns jark.ns
  (:gen-class)
  (:use clojure.contrib.pprint)
  (:use clojure.contrib.ns-utils)
  (:use clojure.contrib.find-namespaces)
  (:refer-clojure :exclude [list find alias])
  (:import (java.io File FileNotFoundException))
  (:require jark.cp)
  (:require jark.pp)
  (:use clojure.contrib.json))

(defn- namespaces []
  (find-namespaces-on-classpath))

(defn- searched-namespaces []
  (let [nss (namespaces)]
    (if (> (count nss) 0)
      nss
      (map ns-name (all-ns)))))

(defn- starting-with [module]
  (sort (filter #(. (str %) startsWith module) (searched-namespaces))))

(defn- containing-str [module]
  (sort (filter #(. (str %) contains module) (searched-namespaces))))

(defn list
  "List all namespaces in the classpath. Optionally takes a namespace prefix"
  ([]
   (sort (namespaces)))
  ([module]
   (starting-with (str module "."))))

(defn find
  "Find all namespaces containing the given name"
  [module]
  (containing-str module))

(defn load-clj
  "Loads the given clj file, and adds relative classpath"
  [file]
  (let [basename (.getParentFile (File. file))]
    (jark.cp/add (.toString basename)))
  (load-file file))

(defn require-ns [n]
  (require (symbol n)))

(defn fun? [f]
  (instance? clojure.lang.IFn f))

(defn fns [n]
  (require-ns n)
  (let [namespace (symbol n)
        vars      (vec (ns-vars namespace))
        fns-list  (filter #(fun? %) vars)]
    (sort fns-list)))

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

(defn dispatch-module-cmd
  ([printer module]
   (try
     (require-ns module)
     (printer (help module))
     (catch FileNotFoundException e (println "jark: No such module" module))))

  ([printer module command & args]
   (if (or (= (first args) "help") (= command "help"))
     (explicit-help module command)
     (try
       (do
         (require-ns module)
         (let [ret (apply (resolve (symbol (str module "/" command))) args)]
           (when ret (printer ret))))
       (catch FileNotFoundException e (println "jark: No such module" module))
       (catch IllegalArgumentException e (help module command))
       (catch NullPointerException e (println module ": No such command" command))))))

(def dispatch
  (partial dispatch-module-cmd jark.pp/pp-form))

(def cli-json
  (partial dispatch-module-cmd json-str))

(defn run
  "Runs the class/ns containing a -main function"
  [main-ns & args]
  (require-ns main-ns)
  (apply (resolve (symbol (str main-ns "/-main"))) args))
