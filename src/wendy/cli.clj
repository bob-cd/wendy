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
  (:require [wendy.commands :as commands])
  (:import (net.sourceforge.argparse4j ArgumentParsers)
           (net.sourceforge.argparse4j.inf ArgumentParser Namespace)))

(defn- configured-parser
  []
  (let [parser     (-> (ArgumentParsers/newFor "wendy")
                       (.build)
                       (.defaultHelp true)
                       (.description "Bob's reference CLI and his SO."))
        subparsers (-> parser
                       (.addSubparsers)
                       (.description "Things I can make Bob do.")
                       (.metavar "COMMANDS")
                       (.dest "command"))
        _          (-> subparsers
                       (.addParser "can-we-build-it")
                       (.help "Perform a health check on Bob."))]
    parser))

(defn dispatch
  [^Namespace options]
  (case (.get options "command")
    "can-we-build-it" (commands/can-we-build-it!)))

(defn build-it!
  [args]
  (let [parser   ^ArgumentParser (configured-parser)
        response (dispatch (.parseArgsOrFail parser (into-array String args)))]
    (println response)))
