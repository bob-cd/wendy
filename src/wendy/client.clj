(ns wendy.client
  (:require
    [cheshire.core :as json]
    [clojure.string :as str]
    [org.httpkit.client :as http]
    [wendy.impl :as impl])
  (:import
    [java.util.regex Pattern]))

(defn args-lookup
  [args]
  (->> args
       (map #(vector (keyword (:option %))
                     (:in %)))
       (into {})))

(defn interpolate-path
  [path value-map]
  (let [[param value] (first value-map)]
    (if (nil? param)
      path
      (recur (str/replace path
                          (re-pattern (format "\\{([%s].*?)\\}"
                                              (-> param
                                                  name
                                                  Pattern/quote)))
                          (str value))
             (dissoc value-map param)))))

(defn make-query-params
  [supplied-args lookup]
  (->> supplied-args
       (filter #(= "query" (lookup (first %))))
       (into {})))

(defn request
  [url method supplied-args required-args]
  (let [lookup                 (args-lookup required-args)
        call                   #(deref (http/request {:url          (interpolate-path url supplied-args)
                                                      :method       method
                                                      :headers      {"Content-Type" "application/json"}
                                                      :query-params (make-query-params supplied-args lookup)
                                                      :body         (supplied-args :f)}))
        {:keys [headers body]} (impl/try! call)
        result                 (if (str/includes? (headers :content-type) "application/json")
                                 (try
                                   (-> body
                                       (json/parse-string)
                                       (get "message"))
                                   (catch Exception _
                                     body))
                                 body)]
    (println result)))

(comment
  (deref (http/request {:url "http://localhost:7777/cctray.xml" :method :get}))

  (make-query-params {:a 10 :b 12} {:a "path" :b "query"})

  (args-lookup [{:ignore 42 :option "foo" :in "path"}
                {:also-ignore 66 :option "bar" :in "query"}]))
