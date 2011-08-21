(ns jark.lein
  (:require leiningen.core)
  (:gen-class))

(defn set-pwd [dir]
  (System/setProperty "leiningen.original.pwd" dir))

(defn run-task [& args]
  (let [dir (first args)]
    (set-pwd dir)
    (println (System/getProperty "leiningen.original.pwd"))
    (apply leiningen.core/-main (rest args))))
  
