(ns app
  (:require [deps]
            [log]
            [api]
            [promesa.core :as p]))

(defn handler [m]
  (log/info m)
  ;; resolve a promise and add status
  (p/resolved (assoc m :atomist/status {:code 0 :reason "completed successfully"})))

(api/start handler) 
