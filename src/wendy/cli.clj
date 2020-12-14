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
  (:require [wendy.request :as r]
            [wendy.effects :as e]
            [cheshire.core :as json]
            [camel-snake-kebab.core :as csk]
            [cli-matic.core :as cli]))

(defn invoke [args]
  (r/api-request args))

(defn extract-opts [parameters]
  (let [param-in (get parameters "in")
        param-name (get parameters "name")
        param-description (get parameters "description")
        param-type (keyword (get-in parameters ["schema" "type"]))
        required (when (true? (get parameters "required"))
                   {:default :present})]
    (when (or (= param-in "query") (= param-in "path"))
      (merge {:as param-description :option param-name :type param-type :in param-in}
             required))))

(defn extract-body-opt [path-item opts]
  (if-let [body-opt (get path-item "requestBody")]
    (let [param-in "body"
          param-name "data"
          param-description (get body-opt "description")
          param-type :slurp]
      (merge opts {:as param-description :option param-name :type param-type :in param-in :default :present}))
    opts))

(defn extract-subcommand [path path-item]
  (let [method      (key path-item)
        operation   (val path-item)
        command     (-> (get operation "operationId")
                        (csk/->kebab-case))
        description (get operation "summary")
        opts        (->> (get operation "parameters")
                         (map extract-opts)
                         (extract-body-opt operation))
        runs        (fn [params]
                      (invoke {:params params
                               :opts opts
                               :method method
                               :uri path}))
        subcommand {:method method :command command :path path :description description :runs runs}]
    (if (empty? opts)
      subcommand
      (assoc subcommand :opts opts))))

(defn transform-configuration [conf]
  (let [head {:command "wendy"
              :description "Bob's SO and the reference CLI."
              :version "1"}
        transform (fn [path]
                    (let [p (key path)]
                      (map #(extract-subcommand p %) (val path))))]
    (assoc head :subcommands (-> (map transform conf)
                                 (flatten)))))

(defn run [args]
  (cli/run-cmd args (transform-configuration (e/retrieve-configuration))))

(comment
  (clojure.pprint/pprint (transform-configuration (e/retrieve-configuration)))

  (map (fn [[k v]]
         (str (name k) "=" v))
       {:foo "bar"
        :baz "meh"})
  (join-query-params "foo" {:foo "bar"
                            :baz "meh"})

  (->> (map extract-opts
        '({"name" "group",
           "required" false,
           "in" "query",
           "description" "The group of the pipeline",
           "schema" {"type" "string"}}
          {"name" "name",
           "required" false,
           "in" "query",
           "description" "The name of the pipeline",
           "schema" {"type" "string"}}
          {"name" "status",
           "required" false,
           "in" "query",
           "description" "The status of the pipeline",
           "schema" {"type" "string"}}))
       (cons nil))
  (run '("health-check")))
