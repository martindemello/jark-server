(ns cljr.clojars
  (:require [clojure.string :as s])
  (:use [cljr core http]
	[leiningen.deps :only (deps)]))


(def *clojars-repo-url* "http://clojars.org/repo")
(def *clojars-all-jars-url* (str *clojars-repo-url* "/all-jars.clj"))
(def *clojars-all-poms-url* (str *clojars-repo-url* "/all-poms.txt"))

(defn get-latest-version [library-name]
  (let [response (http-get-text-seq *clojars-all-jars-url*)
	lib-name (symbol library-name)]
    (second (last (filter #(= (first %) lib-name)
			  (for [line response]
			    (read-string line)))))))

(defn get-pom-dir
  ([library-name version]
     (let [id-str (if (.contains library-name "/")
		    (str "./" library-name "/" version "/")
		    (str "./" library-name "/" library-name "/" version "/"))]
       id-str)))


(defn get-pom-locations
  ([library-name version]
     (let [response (http-get-text-seq *clojars-all-poms-url*)
	   pom-dir (get-pom-dir library-name version)]
       (for [line response :when (.startsWith line pom-dir)]
	 line))))


(defn get-latest-pom-location
  ([library-name version]
     (last (get-pom-locations library-name version))))


(defn to-clojars-url
  ([file-location]
     (str *clojars-repo-url* "/" file-location)))


(defn get-latest-pom-file
  ([library-name version]
     (http-get-text-seq (to-clojars-url (get-latest-pom-location library-name version)))))


(defn extract-description-text [xml]
  (when-let [desc (re-find (re-pattern (str "<description>(.*)</description>")) xml)]
    (second desc)))

(defn description-text [xml-seq]
  (-> (apply str
	     (for [line xml-seq
		   :when (.contains line "<description>")]
	       line))
      (extract-description-text)))

(defn get-library-dependencies
  [library-name version]
  (let [pom-xml (clojure.xml/parse
		 (java.io.ByteArrayInputStream.
		  (.getBytes (apply str (get-latest-pom-file library-name version)))))
	deps-xml (filter #(= (:tag %) :dependencies) (:content pom-xml))
	deps-seq (partition 3 (for [x (xml-seq deps-xml)
				    :when (or (= (:tag x) :artifactId)
					      (= (:tag x) :groupId)
					      (= (:tag x) :version))] 
				(hash-map (:tag x) (first (:content x)))))]
    (into #{} (for [v deps-seq] (apply merge v)))
    (map :content (:content (first deps-xml)))))
