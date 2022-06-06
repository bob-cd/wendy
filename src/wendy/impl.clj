(ns wendy.impl)

(defn bail!
  [ex]
  (println (str "Error: " ex))
  (System/exit 1))

(defn try!
  [f]
  (try
    (let [resp (f)]
      (if (and (map? resp)
               (instance? Exception (:error resp)))
        (bail! (:error resp))
        resp))
    (catch Exception e
      (bail! e))))
