(ns wendy.main
  (:require [wendy.cli :as cli]))

(defn -main
  [& _args]
  (let [bob-url (or (System/getenv "BOB_URL") "http://localhost:7777")]
    (cli/run bob-url *command-line-args*)))
