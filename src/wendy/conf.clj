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

(ns wendy.conf
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clj-yaml.core :as yaml]
            [wendy.request :as r])
  (:import (java.io File
                    PushbackReader)))

(def ^:private defaults
  {:connection {:host "127.0.0.1"
                :port 7777}})

(defn with-home-dir
  [path]
  (str (System/getProperty "user.home")
       (File/separator)
       path))

(defn read-conf
  []
  (let [path          (with-home-dir ".wendy.edn")
        external-conf (when (.exists (io/file path))
                        (with-open [rdr (io/reader path)]
                          (edn/read (PushbackReader. rdr))))]
    (merge defaults external-conf)))

(defn retrieve-configuration
  [connection]
  (let [cached-api (with-home-dir ".bob.api.yaml")
        data       (if (.exists (io/file cached-api))
                     (slurp cached-api)
                     (let [{:keys [body status]} (r/request {:uri     "/api.yaml"
                                                             :headers {"Accept"          "application/yaml"
                                                                       "Accept-Encoding" ["gzip" "deflate"]}}
                                                            connection)]
                       (when (>= status 400)
                         (throw (Exception. (str "Error fetching api spec: " body))))
                       (spit cached-api body)
                       body))
        parsed     (yaml/parse-string data :keywords false)]
    (get parsed "paths")))

(comment
  (with-home-dir ".bob.api.yaml")

  (read-conf)

  (-> (read-conf)
      :connection
      retrieve-configuration))
