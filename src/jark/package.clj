(ns jark.package
  (:gen-class)
  (:use [cljr core clojars http])
  (:use [leiningen.deps :only (deps)])
  (:use [clojure.java.io :only (file copy)])
  (:refer-clojure :exclude [list find alias]))

(defn hash-of [f1 f2 x]
  (into {} (map #(vector (f1 %) (f2 %)) x)))

(defn hash-of-assoc [x]
  (hash-of first second x))

(defn get-clojars-list [] (http-get-text-seq *clojars-all-jars-url*))

(defn read-strings [response] (for [line response] (read-string line)))

(defn project-path []
  (str (get-cljr-home) (sep) project-clj))

(defn list
  "List all packages that are installed"
  []
  (let [dependencies (:dependencies (get-project))]
    (hash-of-assoc dependencies)))

(defn matches-library [library-name]
  #(= (first %) (symbol library-name)))

(defn not-matches-library [library-name]
  #(= (first %) (symbol library-name)))

(defn update-deps-project [f]
  (let [project (get-project)
        dependencies (:dependencies project)
        new-deps (f dependencies)
        updated-project (assoc project :dependencies new-deps)
        proj-str (project-clj-string updated-project {:dependencies new-deps})]
    (spit (project-path) proj-str)))

(defn install
  "Installs a given package name from available.the list of repositories
  If the version is not provided, the latest version is assumed"
  ([library-name]
   (let [version (get-latest-version library-name)]
     (if version
       (install library-name version)
       (println "Cannot find version of" library-name "on Clojars.org.\n"
                "If the library is in another repository, provide a version argument."))))

  ([library-name library-version]
   (println "Installing version " library-version " of " library-name "...")
   (println "updating " (project-path))
   (update-deps-project
     (fn [dependencies]
       (distinct (conj dependencies [(symbol library-name) library-version]))))
   (deps (get-project))
   (str "Installed library " library-name , " - ", library-version)))

(defn uninstall [library-name]
  (update-deps-project
    (fn [dependencies]
      (into [] (filter (not-matches-library library-name)
                       dependencies))))
  (str library-name " has been uninstalled"))

(defn get-dep [d]
  (apply merge (map #(hash-map (:tag %) (first (:content %))) d)))

(defn get-library [d]
  (let [dep (get-dep d)]
    (str (:groupId dep) "/" (:artifactId dep))))

(defn get-version [d]
  (let [dep (get-dep d)]
    (:version dep)))

(defn dependencies
  "Lists the dependencies for the given package name and version"
  [library-name version]
  (let [d (get-library-dependencies library-name version)]
    (hash-of get-library get-version d)))

(defn versions
  "Lists the available versions for the package on remote repositories"
  [library-name]
  (let [response (get-clojars-list)
        entries  (filter (matches-library library-name)
                         (read-strings response))]
    (hash-of-assoc entries)))

(defn search
  [term]
  (let [response (get-clojars-list)
        entries  (for [line response :when (.contains line term)]
                   (read-string line))]
    (hash-of-assoc entries)))

(defn latest-version [library-name]
  (let [response (get-clojars-list)]
    (second (last (filter (matches-library library-name)
                          (read-strings response))))))

(defn repo-list []
  (let [repos (merge leiningen.pom/default-repos
                     (:repositories (get-project)))]
    (hash-of-assoc repos)))

(defn write-repo-file [repo-map]
  (spit (project-path)
        (project-clj-string (get-project) {:repositories repo-map})))

(defn repo-add
  ([repo-name repo-url]
   (let [repo-map (assoc (get-repositories) repo-name repo-url)]
     (write-repo-file repo-map)
     (str repo-name " repository added"))))

(defn repo-remove
  ([repo-name]
   (let [repo-map (dissoc (get-repositories) repo-name)]
     (write-repo-file repo-map)
     (str repo-name " repository removed"))))
