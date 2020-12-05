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
  (:require [wendy.conf :as conf]
            [java-http-clj.core :as http]
            [clojure.string :as s])
  (:import (java.util.regex Pattern)))

(defn interpolate-path
  "Replaces all occurrences of {k1}, {k2} ... with the value map provided.

  Example:
  given a/path/{id}/on/{not-this}/root/{id} and {:id hello}
  results in: a/path/hello/{not-this}/root/hello."
  [path path-map]
  (let [[param value] (first path-map)]
    (if (nil? param)
      path
      (recur
       (s/replace path
                  (re-pattern (format "\\{([%s].*?)\\}"
                                      (-> param name Pattern/quote)))
                  value)
       (dissoc path-map param)))))

(defn join-query-params
  "Joins a path with its query parameters.

  Example:
  given a/my/path and {:foo bar}
  results in: a/my/path&foo=bar"
  [path query-map]
  (let [query-list   (map (fn [[k v]] (str (name k) "=" v))
                          query-map)
        query-string (s/join "?" query-list)]
    (if (empty? query-map)
      path
      (str path "&" query-string))))

(defn extract-params [params opts]
  (let [opts (into {} (map (fn [opt]
                             (let [option (keyword (:option opt))
                                   in     (keyword (:in opt))]
                               {option in}))
                          opts))
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
  (let [{:keys [host port]} (:connection (conf/read-conf))
        [path-params query-params] (extract-params params opts)
        transformed-uri (-> uri
                            (interpolate-path path-params)
                            (join-query-params query-params))
        request {:body body
                 :headers (merge
                            {"Accept" "application/json"}
                            headers)
                 :method method
                 :uri (str "http://" host ":" port transformed-uri)}
        response (http/send request)]
    (println response)
    0))
