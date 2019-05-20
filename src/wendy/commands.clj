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
            [clj-http.lite.client :as http]
            [wendy.conf :as conf]
            [wendy.build-conf :as build]))

;; TODO: HANDLE ERRORS!!

(defn bob-url
  []
  (let [{:keys [host port]} (:connection (conf/read-conf))]
    (format "http://%s:%d/api" host port)))

(defn respond
  [response]
  (json/generate-string (:message response)
                        {:pretty true}))

(defn can-we-build-it!
  []
  (let [url (str (bob-url) "/" "can-we-build-it")]
    (-> (http/get url)
        (:body)
        (json/parse-string true)
        (respond))))

(defn pipeline-create!
  [path]
  (let [conf        (build/read-file path)
        requests    (for [group    (build/groups-in conf)
                          pipeline (build/pipelines-of conf group)]
                      {:url    (format "%s/pipeline/%s/%s"
                                       (bob-url)
                                       group
                                       pipeline)
                       :params (build/pipeline-config-of conf group pipeline)})
        successful? (->> requests
                         (map #(http/post (:url %)
                                          {:content-type :json
                                           :accept       :json
                                           :body         (:params %)}))
                         (map #(json/parse-string (:body %) true))
                         (map :message)
                         (every? #(= % "Ok")))]
    (respond {:message (if successful? "Ok" "Failed")})))

(defn pipeline-start!
  [group name]
  (let [url (format "%s/pipeline/start/%s/%s"
                    (bob-url)
                    group
                    name)]
    (-> (http/post url)
        (:body)
        (json/parse-string true)
        (respond))))
