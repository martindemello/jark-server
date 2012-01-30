(ns clojure.tools.jark.plugin.package
  (:gen-class)
  (:require [clojure.tools.jark.util.package :as util.package])
  (:refer-clojure :exclude [list find alias]))

(defn list
  "List all packages that are installed"
  []
  (util.package/list))

(defn install
  "Installs a given package name from available.the list of repositories
  If the version is not provided, the latest version is assumed"
  ([library-name]
     (util.package/install library-name))
     
  ([library-name library-version]
     (util.package/install library-name library-version)))

(defn uninstall [library-name]
  (util.package/uninstall library-name))
  
(defn latest-version [library-name]
  (util.package/latest-version library-name))

(defn deps
  "Lists the dependencies for the given package name and version"
  ([library-name]
     (util.package/dependencies library-name))
  ([library-name version]
     (util.package/dependencies library-name version)))

(defn versions
  "Lists the available versions for the package on remote repositories"
  [library-name]
  (util.package/versions library-name))

(defn search
  [term]
  (util.package/search term))
  
(defn repo-list []
  (util.package/repo-list))

(defn repo-add
  [repo-name repo-url]
  (util.package/repo-add repo-name repo-url))

(defn repo-remove
  [repo-name]
  (util.package/repo-remove repo-name))
