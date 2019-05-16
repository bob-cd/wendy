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
            [wendy.conf :as conf]))

(defn respond
  [response]
  (json/generate-string (:message response)
                        {:pretty true}))

(defn can-we-build-it!
  []
  (let [{:keys [host port]} (:connection (conf/read-conf))
        url (format "http://%s:%d/api/can-we-build-it" host port)]
    (println (respond (-> (http/get url)
                          (:body)
                          (json/parse-string true))))))
