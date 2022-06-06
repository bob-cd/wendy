(ns wendy.cli
  (:require
    [cli-matic.core :as cli]
    [wendy.spec :as spec]))

(def base-config
  {:command     "wendy"
   :description "Bob's SO, build manager and planner"
   :version     "0.1.0"})

(defn run
  [bob-url args]
  (let [schema      (spec/fetch-schema (str bob-url "/api.yaml"))
        subcommands (spec/get-ops bob-url schema)]
    (cli/run-cmd args (assoc base-config :subcommands subcommands))))
