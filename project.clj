(defproject jark "0.4"
  :description "JARK is a tool to manage classpaths and clojure namespaces on a persistent JVM"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [swank-clojure "1.3.0"]
                 [leiningen "1.1.0"]
                 [org.clojure/tools.nrepl "0.0.5"]]

  :aot [jark.vm jark.cp jark.ns jark.swank jark.pp jark.doc jark.package
        cljr.core cljr.clojars]) 
