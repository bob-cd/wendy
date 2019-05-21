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
  (:require [cheshire.core :as json]
            [wendy.commands :as commands])
  (:import (java.io File)
           (net.sourceforge.argparse4j ArgumentParsers)
           (net.sourceforge.argparse4j.inf ArgumentParser
                                           Namespace)))

(defn configured-parser
  []
  (let [parser          (-> (ArgumentParsers/newFor "wendy")
                            (.build)
                            (.defaultHelp true)
                            (.description "bob's reference CLI and his SO"))
        subparsers      (-> parser
                            (.addSubparsers)
                            (.title "things i can make bob do")
                            (.metavar "COMMANDS")
                            (.dest "command"))
        _               (-> subparsers
                            (.addParser "can-we-build-it" true)
                            (.help "perform a health check on bob"))
        pipeline-parser (-> subparsers
                            (.addParser "pipeline" true)
                            (.help "pipeline lifecycle commands")
                            (.addSubparsers)
                            (.title "pipeline lifecycle")
                            (.metavar "COMMANDS")
                            (.dest "lifecycle-cmd"))
        create-parser   (-> pipeline-parser
                            (.addParser "create" true)
                            (.help "create a pipeline"))
        _               (-> create-parser
                            (.addArgument (into-array String ["-c" "--config"]))
                            (.required false)
                            (.setDefault (str (System/getProperty "user.dir")
                                              File/separator
                                              "build.toml"))
                            (.help "path to the build file"))
        start-parser    (-> pipeline-parser
                            (.addParser "start" true)
                            (.help "start a pipeline"))
        _               (-> start-parser
                            (.addArgument (into-array String ["-g" "--group"]))
                            (.help "group of the pipeline"))
        _               (-> start-parser
                            (.addArgument (into-array String ["-n" "--name"]))
                            (.help "name of the pipeline"))]
    parser))

(defn dispatch
  [^Namespace options]
  (case (.get options "command")
    "can-we-build-it"
    (commands/can-we-build-it!)
    "pipeline"
    (case (.get options "lifecycle-cmd")
      "create"
      (commands/pipeline-create! (.get options "config"))
      "start"
      (commands/pipeline-start! (.get options "group")
                                (.get options "name")))))

(defn error-out
  [^String message]
  (.println System/err message)
  (System/exit 1))

(defn build-it!
  [args]
  (let [parser   ^ArgumentParser (configured-parser)
        response (dispatch (.parseArgsOrFail parser (into-array String args)))]
    (if (:failed? response)
      (error-out (:reason response))
      (println (json/generate-string (:message response))))))

(comment
  (configured-parser)

  (try
    (let [options (into-array String ["pipeline" "--help"])]
      (.parseArgs (configured-parser) options))
    (catch Exception _)))
