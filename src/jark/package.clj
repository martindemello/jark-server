(ns jark.package
  (:gen-class)
  (:require [jark.utils.package :as utils])
  (:refer-clojure :exclude [list find alias]))

(defn list
  "List all packages that are installed"
  []
  (utils/list))

(defn install
  "Installs a given package name from available.the list of repositories
  If the version is not provided, the latest version is assumed"
  ([library-name]
     (utils/install library-name))
     
  ([library-name library-version]
     (utils/install library-name library-version)))

(defn uninstall [library-name]
  (utils/uninstall library-name))
  
(defn latest-version [library-name]
  (utils/latest-version library-name))

(defn deps
  "Lists the dependencies for the given package name and version"
  ([library-name]
     (utils/dependencies library-name))
  ([library-name version]
     (utils/dependencies library-name version)))

(defn versions
  "Lists the available versions for the package on remote repositories"
  [library-name]
  (utils/versions library-name))

(defn search
  [term]
  (utils/search term))
  
(defn repo-list []
  (utils/repo-list))

(defn repo-add
  [repo-name repo-url]
  (utils/repo-add repo-name repo-url))

(defn repo-remove
  [repo-name]
  (utils/repo-remove repo-name))
