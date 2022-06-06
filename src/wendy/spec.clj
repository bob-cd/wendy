(ns wendy.spec
  (:require
    [camel-snake-kebab.core :as csk]
    [clj-yaml.core :as yaml]
    [clojure.java.io :as io]
    [clojure.string :as str]
    [org.httpkit.client :as http]
    [wendy.client :as client]
    [wendy.impl :as impl]))

(defn fetch-schema
  [url]
  (-> (impl/try! #(deref (http/request {:url url :method :get})))
      (:body)
      (io/reader)
      (yaml/parse-stream :keywords false)))

(defn deref-path
  [schema path-ref]
  (->> (str/split path-ref #"/")
       (drop 1)
       (get-in schema)))

(defn coerce-type
  [schema-type]
  (case schema-type
    "string"  :string
    "integer" :int))

(defn param->arg
  [schema param]
  {:option  (param "name")
   :as      (param "description")
   :in      (param "in")
   :default (when (param "required")
              :present)
   :type    (coerce-type (if-let [schema-ref (get-in param ["schema" "$ref"])]
                           (deref-path schema schema-ref)
                           (get-in param ["schema" "type"])))})

(defn make-opts
  [schema params request-body]
  (let [args (map #(param->arg schema %) params)]
    (if request-body
      (conj args
            {:option  "f"
             :as      (request-body "description")
             :default :present
             :in      :body
             :type    :slurp})
      args)))

(defn path-data->op
  [url schema [method op]]
  (when-not (get-in op ["x-cli" "disabled"])
    (let [opts (make-opts schema (op "parameters") (op "requestBody"))]
      {:method      method
       :command     (csk/->kebab-case (get op "operationId"))
       :description (op "description")
       :runs        #(client/request url (keyword method) % opts)
       :opts        opts})))

(defn path->ops
  [url schema [path path-data]]
  (->> path-data
       (map #(path-data->op (str url path) schema %))
       (filter some?)))

(defn get-ops
  [url schema]
  (->> (schema "paths")
       (mapcat #(path->ops url schema %))))
