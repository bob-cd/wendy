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

(ns wendy.cli
  (:require [cli-matic.core :as cli]
            [wendy.commands :as commands]))

(def config
  {:app      {:command     "wendy"
              :description "Bob's SO and the reference CLI."
              :version     "0.1.0"}

   :commands [{:command     "can-we-build-it"
               :description "Performs a health check on Bob."
               :runs        commands/can-we-build-it!}]})

(defn build-it!
  [args]
  (cli/run-cmd args config))
