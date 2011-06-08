(ns jark.package
  (:gen-class)
  (:use [cljr core clojars http])
  (:use [leiningen.deps :only (deps)])
  (:use [clojure.java.io :only (file copy)])
  (:refer-clojure :exclude [list find alias]))

(defn list
  "List all packages that are installed"
  []
  (let [dependencies (:dependencies (get-project))]
    (into {} (map #(vector (first %) (second %)) dependencies))))

(defn install
  "Installs a given package name from available.the list of repositories
   If the version is not provided, the latest version is assumed"
  ([library-name]
     (let [version (get-latest-version library-name)]
       (if version
	 (install library-name version)
	 (println "Cannot find version of" library-name "on Clojars.org.\r\n"
		  "If the library is in another repository, provide a version argument."))))
  ([library-name library-version]
    (let [project (get-project)
	  dependencies (:dependencies project)
	  updated-project (assoc project :dependencies
				 (conj dependencies
				       [(symbol library-name)
					library-version]))
	  proj-str (project-clj-string updated-project
				       {:dependencies (:dependencies updated-project)})]
      (println "Installing version " library-version " of " library-name "...")
      (spit (str (get-cljr-home) (sep) project-clj) proj-str)
      (deps (get-project)))
    (str "Installed library " library-name , " - ", library-version)))

(defn get-library [d]
  (let [dep (apply merge (map #(hash-map (:tag %) (first (:content %))) d))]
    (str (:groupId dep) "/" (:artifactId dep))))

(defn get-version [d]
  (let [dep (apply merge (map #(hash-map (:tag %) (first (:content %))) d))]
    (:version dep)))

(defn dependencies
  "Lists the dependecies for the given package name and version"
  [library-name version]
  (let [d (get-library-dependencies library-name version)]
    (into {} (map #(vector (get-library %) (get-version %)) d))))

(defn versions
  "Lists the available versions for the package on remote repositories"
  [library-name]
  (let [response (http-get-text-seq *clojars-all-jars-url*)
        entries  (filter #(= (first %) (symbol library-name))
                        (for [line response]
                          (read-string line)))]
    (into {} (map #(vector (first %) (second %)) entries))))

(defn search
  
  [term]
  (let [response (http-get-text-seq *clojars-all-jars-url*)
        entries  (for [line response :when (.contains line term)]
                   (read-string line))]
    (into {} (map #(vector (first %) (second %)) entries))))

(defn latest-version [library-name]
  (let [response (http-get-text-seq *clojars-all-jars-url*)
	lib-name (symbol library-name)]
    (second (last (filter #(= (first %) lib-name)
			  (for [line response]
			    (read-string line)))))))

(defn uninstall [library-name]
  (let [project (get-project)
	dependencies (:dependencies project)
	updated-project (assoc project :dependencies
			       (into [] (filter #(not= (symbol library-name)
						       (first %))
						dependencies)))
	proj-str (project-clj-string updated-project
				     {:dependencies (:dependencies updated-project)})]
    (spit (str (get-cljr-home) (sep) project-clj) proj-str)
    (str library-name " has been uninstalled")))

(defn repo-list []
  (let [repos (merge leiningen.pom/default-repos
		     (:repositories (get-project)))]
    (into {} (map #(vector (first %) (second %)) repos))))

(defn repo-add
  ([repo-name repo-url]
     (let [repo-map (assoc (get-repositories) repo-name repo-url)]
       (spit (file (str (get-cljr-home) (sep) project-clj))
	     (project-clj-string (get-project)
				 {:repositories repo-map}))
       (str repo-name " repository added"))))

(defn repo-remove
  ([repo-name]
     (let [repo-map (dissoc (get-repositories) repo-name)]
       (spit (file (str (get-cljr-home) (sep) project-clj))
	     (project-clj-string (get-project)
				 {:repositories repo-map}))
       (str repo-name " repository removed"))))
