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
            [clojure.java.io :as io])
  (:import (java.io File
                    PushbackReader)))

(def ^:private defaults {:connection {:host "127.0.0.1"
                                      :port 7777}})

(defn read-conf
  []
  (let [path (str (System/getProperty "user.home")
                  (File/separator)
                  ".wendy.edn")]
    (with-open [rdr (io/reader path)]
      (merge defaults
             (edn/read (PushbackReader. rdr))))))
