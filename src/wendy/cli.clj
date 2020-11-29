;   This file is part of Wendy.
;
;   Wendy is free software: you can redistribute it and/or modify
;   it under the terms of the GNU General Public License as published by
;   the Free Software Foundation, either version 3 of the License, or
;   (at your option) any later version.
;
;   Wendy is distributed in the hope that it will be useful,
;   but WITHOUT ANY WARRANTY; without even the implied warranty of
;   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
;   GNU General Public License for more details.
;
;   You should have received a copy of the GNU General Public License
;   along with Wendy. If not, see <http://www.gnu.org/licenses/>.

(ns wendy.cli
  (:require [cheshire.core :as json]
            [clj-yaml.core :as yaml]
            [java-http-clj.core :as http]
            [camel-snake-kebab.core :as csk]
            [cli-matic.core :as cli]))

(defn invoke [& args]
  (println args))

(defn retrieve-configuration []
  (-> (http/get "http://localhost:7777/api.yaml" {:headers {"Accept" "application/yaml"
                                                            "Accept-Encoding" ["gzip" "deflate"]}})
      (:body)
      (yaml/parse-string :keywords false)
      (get "paths")))

(defn transform-configuration [conf]
  (let [head {:command "wendy"
              :description "Bob's SO and the reference CLI."
              :version "1"}
        extract-opts (fn [parameters]
                       (let [in (get parameters "in")
                             name (get parameters "name")
                             description (get parameters "description")
                             type (keyword (get-in parameters ["schema" "type"]))]
                         (when (or (= in "query") (= in "path"))
                           {:as description
                            :option name
                            :type type})))
        extract (fn [path path-item]
                  (let [method (key path-item)
                        operation (val path-item)
                        command (-> (get operation "operationId")
                                    (csk/->kebab-case))
                        description (get operation "summary")]
                    (if-let [opts (get operation "parameters")]
                      {:method method :command command :path path :description description :runs invoke :opts (mapv extract-opts opts)}
                      {:method method :command command :path path :description description :runs invoke})))
        transform (fn [path]
                    (let [p (key path)]
                      (into {} (map #(extract p %) (val path)))))]
    (assoc head :subcommands (mapv transform conf))))

(defn run [args]
  (cli/run-cmd args (transform-configuration (retrieve-configuration))))

(comment
  (clojure.pprint/pprint (get (yaml/parse-string (:body (http/get "http://localhost:7777/api.yaml" {:headers {"Accept" "application/yaml"
                                                                                                              "Accept-Encoding" ["gzip" "deflate"]}}))
                                                :keywords false)
                              "paths"))
  (clojure.pprint/pprint (transform-configuration (retrieve-configuration))))
