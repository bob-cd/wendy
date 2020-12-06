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
  (:require [wendy.utils :as u]
            [wendy.effects :as e]))

(defn extract-params [params opts]
  (let [opts (->> (map (fn [opt]
                         (let [option (keyword (:option opt))
                               in     (keyword (:in opt))]
                           {option in}))
                      opts)
                  (into {}))
        path-params (->> (for [[k v] params
                               :let [o (get opts k)]
                               :when (= o :path)]
                          {k v})
                         (into {}))
        query-params (->> (for [[k v] params
                                :let [o (get opts k)]
                                :when (= o :query)]
                           {k v})
                          (into {}))]
    [path-params query-params]))

(defn cli-request [{:keys [body headers method uri params opts]}]
  (let [[path-params query-params] (extract-params params opts)
        transformed-uri (-> uri
                            (u/interpolate-path path-params)
                            (str (u/map-to-query-str query-params)))
        request-args {:body body
                      :headers (merge
                                 {"Accept" "application/json"}
                                 headers)
                      :method method
                      :uri transformed-uri}
        response (e/request request-args)]
    (println response)
    0))
