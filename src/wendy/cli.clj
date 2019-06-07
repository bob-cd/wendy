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
                                           Namespace)
           (net.sourceforge.argparse4j.impl Arguments)))

;; TODO: Maybe a macro to convert a cute little map to this mess?
(defn configured-parser
  []
  (let [parser                   (-> (ArgumentParsers/newFor "wendy")
                                     (.build)
                                     (.defaultHelp true)
                                     (.version "${prog} 0.1.0")
                                     (.description "bob's reference CLI and his SO"))
        _                        (-> parser
                                     (.addArgument (into-array String ["--version"]))
                                     (.action (Arguments/version))
                                     (.help "show the version"))
        subparsers               (-> parser
                                     (.addSubparsers)
                                     (.title "things i can make bob do")
                                     (.metavar "COMMANDS")
                                     (.dest "command"))
        _                        (-> subparsers
                                     (.addParser "can-we-build-it" true)
                                     (.help "perform a health check on bob"))
        pipeline-parser          (-> subparsers
                                     (.addParser "pipeline" true)
                                     (.help "pipeline lifecycle commands")
                                     (.addSubparsers)
                                     (.title "pipeline lifecycle")
                                     (.metavar "COMMANDS")
                                     (.dest "lifecycle-cmd"))
        create-parser            (-> pipeline-parser
                                     (.addParser "create" true)
                                     (.help "create a pipeline"))
        _                        (-> create-parser
                                     (.addArgument (into-array String ["-c" "--config"]))
                                     (.required false)
                                     (.setDefault (str (System/getProperty "user.dir")
                                                       File/separator
                                                       "build.toml"))
                                     (.help "path to the build file"))
        start-parser             (-> pipeline-parser
                                     (.addParser "start" true)
                                     (.help "start a pipeline"))
        _                        (-> start-parser
                                     (.addArgument (into-array String ["-g" "--group"]))
                                     (.required true)
                                     (.help "group of the pipeline"))
        _                        (-> start-parser
                                     (.addArgument (into-array String ["-n" "--name"]))
                                     (.required true)
                                     (.help "name of the pipeline"))
        status-parser            (-> pipeline-parser
                                     (.addParser "status" true)
                                     (.help "status of a pipeline"))
        _                        (-> status-parser
                                     (.addArgument (into-array String ["-g" "--group"]))
                                     (.required true)
                                     (.help "group of the pipeline"))
        _                        (-> status-parser
                                     (.addArgument (into-array String ["-n" "--name"]))
                                     (.required true)
                                     (.help "name of the pipeline"))
        _                        (-> status-parser
                                     (.addArgument (into-array String ["-num" "--number"]))
                                     (.required true)
                                     (.help "the run number of the pipeline"))
        stop-parser              (-> pipeline-parser
                                     (.addParser "stop" true)
                                     (.help "status of a pipeline"))
        _                        (-> stop-parser
                                     (.addArgument (into-array String ["-g" "--group"]))
                                     (.required true)
                                     (.help "group of the pipeline"))
        _                        (-> stop-parser
                                     (.addArgument (into-array String ["-n" "--name"]))
                                     (.required true)
                                     (.help "name of the pipeline"))
        _                        (-> stop-parser
                                     (.addArgument (into-array String ["-num" "--number"]))
                                     (.required true)
                                     (.help "the run number of the pipeline"))
        logs-parser              (-> pipeline-parser
                                     (.addParser "logs" true)
                                     (.help "logs of a pipeline"))
        _                        (-> logs-parser
                                     (.addArgument (into-array String ["-g" "--group"]))
                                     (.required true)
                                     (.help "group of the pipeline"))
        _                        (-> logs-parser
                                     (.addArgument (into-array String ["-n" "--name"]))
                                     (.required true)
                                     (.help "name of the pipeline"))
        _                        (-> logs-parser
                                     (.addArgument (into-array String ["-num" "--number"]))
                                     (.required true)
                                     (.help "the run number of the pipeline"))
        _                        (-> logs-parser
                                     (.addArgument (into-array String ["-o" "--offset"]))
                                     (.required true)
                                     (.help "the line offset from the beginning of the logs"))
        _                        (-> logs-parser
                                     (.addArgument (into-array String ["-l" "--lines"]))
                                     (.required true)
                                     (.help "the number of lines from the offset"))
        delete-parser            (-> pipeline-parser
                                     (.addParser "delete" true)
                                     (.help "delete a pipeline"))
        _                        (-> delete-parser
                                     (.addArgument (into-array String ["-g" "--group"]))
                                     (.required true)
                                     (.help "group of the pipeline"))
        _                        (-> delete-parser
                                     (.addArgument (into-array String ["-n" "--name"]))
                                     (.required true)
                                     (.help "name of the pipeline"))
        gc-parser                (-> subparsers
                                     (.addParser "gc")
                                     (.help "trigger garbage collection on bob"))
        _                        (-> gc-parser
                                     (.addArgument (into-array String ["-a" "--all"]))
                                     (.action (Arguments/storeTrue))
                                     (.help "trigger full GC resulting in build history loss"))
        external-resource-parser (-> subparsers
                                     (.addParser "external-resource")
                                     (.help "external resource commands")
                                     (.addSubparsers)
                                     (.title "external resource")
                                     (.metavar "COMMANDS")
                                     (.dest "external-resource-command"))
        register-parser          (-> external-resource-parser
                                     (.addParser "register" true)
                                     (.help "register an external resource"))
        _                        (-> register-parser
                                     (.addArgument (into-array String ["-n" "--name"]))
                                     (.required true)
                                     (.help "name of the resource"))
        _                        (-> register-parser
                                     (.addArgument (into-array String ["-u" "--url"]))
                                     (.required true)
                                     (.help "url of the resource provider"))
        _                        (-> external-resource-parser
                                     (.addParser "list" true)
                                     (.help "list all registered external resources"))
        delete-parser            (-> external-resource-parser
                                     (.addParser "delete" true)
                                     (.help "delete an external resource"))
        _                        (-> delete-parser
                                     (.addArgument (into-array String ["-n" "--name"]))
                                     (.required true)
                                     (.help "name of the resource"))
        artifact-store-parser    (-> subparsers
                                     (.addParser "artifact-store")
                                     (.help "artifact store commands")
                                     (.addSubparsers)
                                     (.title "artifact store")
                                     (.metavar "COMMANDS")
                                     (.dest "artifact-store-command"))
        artifact-register-parser (-> artifact-store-parser
                                     (.addParser "register" true)
                                     (.help "register an artifact store"))
        _                        (-> artifact-register-parser
                                     (.addArgument (into-array String ["-n" "--name"]))
                                     (.required true)
                                     (.help "name of the artifact store"))
        _                        (-> artifact-register-parser
                                     (.addArgument (into-array String ["-u" "--url"]))
                                     (.required true)
                                     (.help "url of the artifact store"))
        _                        (-> artifact-store-parser
                                     (.addParser "show" true)
                                     (.help "show the registered artifact store"))
        artifact-delete-parser   (-> artifact-store-parser
                                     (.addParser "delete" true)
                                     (.help "delete the registered artifact store"))
        _                        (-> artifact-delete-parser
                                     (.addArgument (into-array String ["-n" "--name"]))
                                     (.required true)
                                     (.help "name of the artifact store"))]
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
                                (.get options "name"))
      "status"
      (commands/pipeline-status! (.get options "group")
                                 (.get options "name")
                                 (.get options "number"))
      "stop"
      (commands/pipeline-stop! (.get options "group")
                               (.get options "name")
                               (.get options "number"))
      "logs"
      (commands/pipeline-logs! (.get options "group")
                               (.get options "name")
                               (.get options "number")
                               (.get options "offset")
                               (.get options "lines"))
      "delete"
      (commands/pipeline-delete! (.get options "group")
                                 (.get options "name")))
    "gc"
    (commands/gc! (.get options "all"))
    "external-resource"
    (case (.get options "external-resource-command")
      "register"
      (commands/external-resource-register! (.get options "name")
                                            (.get options "url"))
      "list"
      (commands/external-resource-list!)
      "delete"
      (commands/external-resource-delete! (.get options "name")))
    "artifact-store"
    (case (.get options "artifact-store-command")
      "register"
      (commands/artifact-store-register! (.get options "name")
                                         (.get options "url"))
      "show"
      (commands/artifact-store-show!)
      "delete"
      (commands/artifact-store-delete! (.get options "name")))))

(defn error-out
  [message]
  (.println System/err (str message))
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
    (let [options (into-array String ["pipeline" "status" "--help"])]
      (.parseArgs (configured-parser) options))
    (catch Exception _))

  (-> (configured-parser)
      (.parseArgs (into-array String ["gc" "--all"])))

  (-> (configured-parser)
      (.parseArgs (into-array String ["external-resource" "list"])))

  (build-it! ["pipeline" "status" "-g" "dev" "-n" "test" "-num" "2"]))
