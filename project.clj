(defproject jark "0.4-SNAPSHOT"
  :description "JARK is a tool to manage classpaths and clojure namespaces on a persistent JVM"
  :dependencies [[org.clojure/clojure "1.3.0"]

                 ; contrib replacements
                 [org.clojure/java.classpath "0.1.0"]
                 [org.clojure/data.json "0.1.1"]
                 [org.clojure/tools.namespace "0.1.0"]
                 [clj-http "0.2.7"]
                 [server-socket "1.0.0"]

                 [swank-clojure "1.3.0"]
                 [jark/leiningen "2.0.0-SNAPSHOT"]
                 [org.clojure/tools.nrepl "0.0.5"]
                 [org.thnetos/cd-client "0.3.1"]
                 [recon "0.3.0"]]

  :java-source-path "src/jark"
  :aot [ leiningen.pom jark.vm jark.cp jark.ns jark.swank jark.pp jark.doc jark.package
        cljr.core cljr.clojars
        jark.lein])
