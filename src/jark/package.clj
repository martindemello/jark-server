(ns jark.package
  (:gen-class)
  (:require jark.util.package)
  (:refer-clojure :exclude [list find alias]))

(defn list
  "List all packages that are installed"
  []
  (jark.util.package/list))

(defn install
  "Installs a given package name from available.the list of repositories
  If the version is not provided, the latest version is assumed"
  ([library-name]
     (jark.util.package/install library-name))
     
  ([library-name library-version]
     (jark.util.package/install library-name library-version)))

(defn uninstall [library-name]
  (jark.util.package/uninstall library-name))
  
(defn latest-version [library-name]
  (jark.util.package/latest-version library-name))

(defn deps
  "Lists the dependencies for the given package name and version"
  ([library-name]
     (jark.util.package/dependencies library-name))
  ([library-name version]
     (jark.util.package/dependencies library-name version)))

(defn versions
  "Lists the available versions for the package on remote repositories"
  [library-name]
  (jark.util.package/versions library-name))

(defn search
  [term]
  (jark.util.package/search term))
  
(defn repo-list []
  (jark.util.package/repo-list))

(defn repo-add
  [repo-name repo-url]
  (jark.util.package/repo-add repo-name repo-url))

(defn repo-remove
  [repo-name]
  (jark.util.package/repo-remove repo-name))
