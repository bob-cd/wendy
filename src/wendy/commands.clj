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

(ns wendy.commands
  (:require [cheshire.core :as json]
            [wendy.conf :as conf]
            [wendy.build-conf :as build]
            [wendy.effects :as effects]))

(defn bob-url
  []
  (let [{:keys [host port]} (:connection (conf/read-conf))]
    (format "http://%s:%d/api" host port)))

(defn can-we-build-it!
  []
  (let [url (str (bob-url) "/" "can-we-build-it")]
    (effects/request url)))

(defn pipeline-create!
  [path]
  (let [result (build/read-file path)]
    (if (:failed? result)
      result
      (let [conf        result
            requests    (for [group    (build/groups-in conf)
                              pipeline (build/pipelines-of conf group)]
                          {:url    (format "%s/pipeline/%s/%s"
                                           (bob-url)
                                           group
                                           pipeline)
                           :params (build/pipeline-config-of conf group pipeline)})
            responses   (map #(effects/request (:url %)
                                               :post
                                               {:content-type :json
                                                :accept       :json
                                                :body         (:params %)})
                             requests)
            successful? (every? #(= (:message %) "Ok") responses)]
        (if (not successful?)
          (effects/fail-with (->> responses
                                  (filter #(not= (:message %) "Ok"))
                                  (clojure.string/join "\n")))
          {:message "Ok"})))))

(defn pipeline-start!
  [group name]
  (let [url (format "%s/pipeline/start/%s/%s"
                    (bob-url)
                    group
                    name)]
    (effects/request url :post)))

(defn pipeline-status!
  [group name number]
  (let [url (format "%s/pipeline/status/%s/%s/%s"
                    (bob-url)
                    group
                    name
                    number)]
    (effects/request url)))

(defn pipeline-stop!
  [group name number]
  (let [url (format "%s/pipeline/stop/%s/%s/%s"
                    (bob-url)
                    group
                    name
                    number)]
    (effects/request url :post)))

(defn pipeline-logs!
  [group name number offset lines]
  (let [url (format "%s/pipeline/logs/%s/%s/%s/%s/%s"
                    (bob-url)
                    group
                    name
                    number
                    offset
                    lines)]
    (effects/request url)))

(defn pipeline-delete!
  [group name]
  (let [url (format "%s/pipeline/%s/%s"
                    (bob-url)
                    group
                    name)]
    (effects/request url :delete)))

(defn gc!
  [all?]
  (let [url (format "%s/gc%s"
                    (bob-url)
                    (if all? "/all" ""))]
    (effects/request url :post)))

(defn external-resource-register!
  [name resource-url]
  (let [url (format "%s/external-resource/%s"
                    (bob-url)
                    name)]
    (effects/request url
                     :post
                     {:content-type :json
                      :accept       :json
                      :body         (json/generate-string {:url resource-url})})))

(defn external-resource-list!
  []
  (let [url (format "%s/external-resources"
                    (bob-url))]
    (effects/request url)))

(defn external-resource-delete!
  [name]
  (let [url (format "%s/external-resource/%s"
                    (bob-url)
                    name)]
    (effects/request url :delete)))

(defn artifact-store-register!
  [name resource-url]
  (let [url (format "%s/artifact-store/%s"
                    (bob-url)
                    name)]
    (effects/request url
                     :post
                     {:content-type :json
                      :accept       :json
                      :body         (json/generate-string {:url resource-url})})))

(defn artifact-store-show!
  []
  (let [url (format "%s/artifact-store"
                    (bob-url))]
    (effects/request url)))

(defn artifact-store-delete!
  [name]
  (let [url (format "%s/artifact-store/%s"
                    (bob-url)
                    name)]
    (effects/request url :delete)))

(comment
  (bob-url)

  (can-we-build-it!)

  (pipeline-create! "docs/build.toml")

  (pipeline-start! "dev" "test")

  (pipeline-status! "dev" "test" "1")

  (pipeline-logs! "dev" "test" "1" "0" "100")

  (pipeline-delete! "dev" "test")

  (gc! true)

  (external-resource-register! "git"
                               "http://localhost:8000")

  (external-resource-list!)

  (external-resource-delete! "git")

  (artifact-store-register! "local"
                            "http://localhost:8001")

  (artifact-store-show!)

  (artifact-store-delete! "local"))
