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

(ns wendy.effects
  (:require [clojure.java.io :as io]
            [clj-http.lite.client :as http]
            [cheshire.core :as json])
  (:import (java.nio.file Files)))

(defmacro unsafe!
  [& body]
  `(try
     ~@body
     (catch Exception e# e#)))

(defn failed?
  [expr]
  (instance? Exception expr))

(defn fail-with
  [reason & status]
  (conj
   {:failed? true
    :reason  reason}
   (when (not-empty status)
     {:status (first status)})))

(defn parse-error
  [message]
  (if (:body message)
    (json/generate-string (:body message))
    message))

(defn request
  ([url]
   (request url :get {}))
  ([url method]
   (request url method {}))
  ([url method opts]
   (let [req-fn   (case method
                    :get
                    http/get
                    :post
                    http/post)
         response (unsafe! (req-fn url opts))]
     (if (failed? response)
       (fail-with (if (ex-data response)
                    (parse-error (ex-data response))
                    (ex-message response))
                  (:status (ex-data response)))
       (-> response
           (:body)
           (json/parse-string true))))))

(defn file-from
  [path]
  (let [file (io/as-file path)]
    (if (Files/isReadable (.toPath file))
      {:file file}
      (fail-with (str "Missing or unreadble file: " path)))))

(comment
  (failed? (unsafe! (/ 5 0)))

  (fail-with "shizzz" 403)

  (request "https://httpbin.org/get")

  (request "https://httpbin.org/bet")

  (file-from "deps.edn")

  (file-from "lulz.edn")

  (request "http://localhost:7777/api/pipeline/stop/dev/test/1"
           :post))
