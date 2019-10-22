(ns wendy.utils)

(defn map-to-query-str
  [m]
  (->> m
       (filter #(some? (second %)))
       (map (fn [[param value]]
              (let [param-name (name param)
                    value-encoded (str (java.net.URLEncoder/encode value))]
                (str param-name "=" value-encoded) )))
       (clojure.string/join "&")
       (#(if (not-empty %) (str "?" %) ""))))
