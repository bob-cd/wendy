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
  (:require [camel-snake-kebab.core :as csk]
            [wendy.conf :as conf]
            [wendy.request :as r]))

(def extra-subcommands
  [{:command     "purge-cache"
    :description "Purges the cache to force Wendy to build options again"
    :runs        conf/purge-cache}])

(defn extract-opts
  [parameters]
  (let [param-in          (get parameters "in")
        param-name        (get parameters "name")
        param-description (get parameters "description")
        param-type        (let [t (keyword (get-in parameters ["schema" "type"]))]
                            (if (= t :integer)
                              :int
                              t))
        required          (when (true? (get parameters "required"))
                            {:default :present})]
    (when (or (= param-in "query") (= param-in "path"))
      (merge {:as     param-description
              :option param-name
              :type   param-type
              :in     param-in}
             required))))

(defn extract-body-opt
  [path-item opts]
  (if-let [body-opt (get path-item "requestBody")]
    (let [param-in          "body"
          param-name        "data"
          param-description (get body-opt "description")
          param-type        :slurp]
      (merge opts
             {:as      param-description
              :option  param-name
              :type    param-type
              :in      param-in
              :default :present}))
    opts))

(defn get-command
  [operation]
  (let [cli-tags (get operation "x-cli")]
    (when-not (true? (get cli-tags "disabled"))
      (get cli-tags "name" (csk/->kebab-case (get operation "operationId"))))))

(defn extract-subcommand
  [path path-item connection]
  (let [method    (key path-item)
        operation (val path-item)
        command   (get-command operation)]
    (when command
      (let [description (get operation "summary")
            opts        (->> (get operation "parameters")
                             (map extract-opts)
                             (extract-body-opt operation))
            runs        #(r/api-request {:params %
                                         :opts   opts
                                         :method method
                                         :uri    path}
                                        connection)
            subcommand  {:method      method
                         :command     command
                         :path        path
                         :description description
                         :runs        runs}]
        (if (empty? opts)
          subcommand
          (assoc subcommand :opts opts))))))

(defn transform-configuration
  [conf connection]
  (let [head      {:command     "wendy"
                   :description "Bob's SO and the reference CLI"
                   :version     "0.1.0"}
        transform (fn [path]
                    (->> (val path)
                         (map #(extract-subcommand (key path) % connection))
                         (filter some?)))]
    (assoc head
           :subcommands
           (concat extra-subcommands
                   (-> (map transform conf)
                       flatten)))))

(comment
  (get-command {"operationId" "GetApiSpec"
                "x-cli"       {"disabled" true}})

  (get-command {"operationId" "PipelineCreate"
                "x-cli"       {"name" "p-create"}})

  (get-command {"operationId" "GetMetrics"})

  (map (fn [[k v]]
         (str (name k) "=" v))
       {:foo "bar"
        :baz "meh"})

  (->> (map extract-opts
            '({"name"        "group"
               "required"    false
               "in"          "query"
               "description" "The group of the pipeline"
               "schema"      {"type" "string"}}
              {"name"        "name"
               "required"    false
               "in"          "query"
               "description" "The name of the pipeline"
               "schema"      {"type" "string"}}
              {"name"        "status"
               "required"    false
               "in"          "query"
               "description" "The status of the pipeline"
               "schema"      {"type" "string"}}))
       (cons nil)))
