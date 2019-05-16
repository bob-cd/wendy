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
  (:require [clojure.tools.cli :as cli]
            [clojure.string :as string]
            [wendy.commands :as commands]))

(defn- usage
  [options-summary]
  (->> ["wendy 0.1.0 - Bob's SO and the reference CLI."
        ""
        "Usage: wendy [options] action [action-options]"
        ""
        "Options:"
        options-summary
        ""
        "Actions:"
        "  can-we-build-it    Perform a health check on Bob."]
       (string/join \newline)))

(defn- error-msg
  [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(def ^:private options
  [["-h" "--help"]])

(def ^:private commands #{"can-we-build-it"})

(defn- validate-args
  [args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args options)]
    (cond
      (:help options)
      {:exit-message (usage summary) :ok? true}
      errors
      {:exit-message (error-msg errors)}
      (and (= 1 (count arguments))
           (commands (first arguments)))
      {:action (first arguments) :options options}
      :else
      {:exit-message (usage summary)})))

(defn exit
  [status msg]
  (println msg)
  (System/exit status))

(defn build-it!
  [args]
  (let [{:keys [action options exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (case action
        "can-we-build-it" (commands/can-we-build-it!)))))
