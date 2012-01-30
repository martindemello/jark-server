(defproject jark "0.4-SNAPSHOT"
  :description "Tool to interact with a persistent JVM"
  :dependencies [[org.clojure/clojure "1.3.0"]

                 ; contrib replacements
                 [org.clojure/java.classpath "0.1.0"]
                 [org.clojure/data.json "0.1.1"]
                 [org.clojure/tools.namespace "0.1.0"]
                 [clj-http "0.2.7"]
                 [server-socket "1.0.0"]

                 [swank-clojure "1.4.0"]
                 [org.clojure/tools.nrepl "0.0.5"]]

  :source-path "src/main/clojure"
  :java-source-path "src/main/java"
  :java-options {:debug "true"}
  :aot [clojure.tools.jark.server
        clojure.tools.jark.plugin])
