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

(ns wendy.utils
  (:require [clojure.string :as s])
  (:import [java.net URLEncoder]
           [java.util.regex Pattern]))

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
                  (re-pattern (format "\\{(%s.*?)\\}"
                                      (-> param name Pattern/quote)))
                  value)
       (dissoc path-map param)))))

(defn map-to-query-str
  [m]
  (->> m
       (filter #(some? (second %)))
       (map (fn [[param value]]
              (let [param-name    (name param)
                    value-encoded (str (URLEncoder/encode value))]
                (str param-name "=" value-encoded))))
       (clojure.string/join "&")
       (#(if (not-empty %) (str "?" %) ""))))
