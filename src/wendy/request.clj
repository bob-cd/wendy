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

(ns wendy.request
  (:require [java-http-clj.core :as http]
            [clj-yaml.core :as yaml]
            [wendy.conf :as conf]
            [wendy.utils :as u]))

(defn extract-params
  [params opts]
  (let [opts (->> (map (fn [opt]
                         (let [option (keyword (:option opt))
                               in     (keyword (:in opt))]
                           {option in}))
                       opts)
                  (into {}))]
    (reduce (fn [p-map param]
              (let [[k v] param
                    path  (case (get opts k)
                            :path  [:path-params k]
                            :query [:query-params k]
                            :body  [:body-param k]
                            nil)]
                (if path
                  (assoc-in p-map path v)
                  p-map)))
            {:path-params  {}
             :query-params {}
             :body-param   {}}
            params)))

(defn request
  [args-map]
  (let [{:keys [host port]} (:connection (conf/read-conf))
        defaults-map        {:uri (str "http://" host ":" port (:uri args-map))}
        request-map         (merge args-map
                                   defaults-map)]
    (http/send request-map)))

(defn retrieve-configuration
  []
  (-> (request {:uri     "/api.yaml"
                :headers {"Accept"          "application/yaml"
                          "Accept-Encoding" ["gzip" "deflate"]}})
      (:body)
      (yaml/parse-string :keywords false)
      (get "paths")))

(defn api-request
  [{:keys [headers method uri params opts]}]
  (let [{:keys [path-params query-params body-param]} (extract-params params opts)
        transformed-uri                               (-> uri
                                                          (u/interpolate-path path-params)
                                                          (str (u/map-to-query-str query-params)))
        request-args                                  {:body    (:data body-param)
                                                       :headers (merge
                                                                  {"Accept"       "application/json"
                                                                   "content-type" "application/json"}
                                                                  headers)
                                                       :method  method
                                                       :uri     transformed-uri}
        response                                      (request request-args)]
    (if (or (= (:status response) 200)
            (= (:status response) 202))
      (println (:body response))
      (println response))
    0))
