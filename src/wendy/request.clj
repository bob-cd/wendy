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
            [jsonista.core :as j]
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
  [args-map {:keys [host port]}]
  (let [defaults-map {:uri (str "http://" host ":" port (:uri args-map))}
        request-map  (merge args-map
                            defaults-map)]
    (http/send request-map)))

(defn api-request
  [{:keys [headers method uri params opts]} connection]
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
        {:keys [body status headers]}                 (request request-args connection)
        response                                      (if (= (get headers "content-type") "application/json")
                                                        (:message (j/read-value body j/keyword-keys-object-mapper))
                                                        body)]
    (println response)
    (if (>= status 400)
      1
      0)))
