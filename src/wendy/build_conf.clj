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

(ns wendy.build-conf
  (:import (java.io StringWriter)
           (com.electronwill.nightconfig.core.file FileConfig)
           (com.electronwill.nightconfig.toml TomlFormat)
           (com.electronwill.nightconfig.core Config
                                              Config$Entry)
           (com.electronwill.nightconfig.json MinimalJsonWriter)))

;; TODO: Really? This complicated?!

(defn toml->json
  [^Config conf]
  (let [json-writer   (MinimalJsonWriter.)
        string-writer (StringWriter.)
        _             (.write json-writer conf string-writer)]
    (str string-writer)))

(defn read-file
  [^String path]
  (let [toml (FileConfig/of path (TomlFormat/instance))
        _    (.load toml)]
    toml))

(defn groups-in
  [^Config conf]
  (into #{} (keys (.valueMap conf))))

(defn pipelines-of
  [^Config conf ^String group]
  (->> ^Config (.get conf group)
       (.entrySet)
       (map #(.getKey ^Config$Entry %))
       (into #{})))

(defn pipeline-config-of
  [^Config conf ^String group ^String pipeline]
  (-> conf
      ^Config (.get group)
      (.get pipeline)
      (toml->json)))
