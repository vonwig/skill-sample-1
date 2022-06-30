(ns app
  (:require ["@atomist/skill-entry-point$default" :as api]
            [promesa.core :as p]
            [mw]))

(defn handler
  "sample cljs handler
     params
       m - clj map representing the skill payload (insert schema url)
       options - :logger and :transact will always be present
               - logger is a preconfigured @atomist/skills-logging instance (needs docs)
               - transact will transact new datoms
     returns
       must return a promise - add a map with an :atomist/status key to automatically send status"
  [payload & {:keys [logger transact]}]
  ;; there'll always be a logger which can send messages to the go.atomist.com console
  (.info logger (pr-str payload))

  ;; there's also a transaction function for pushing data to the service
  ;; this skill might execute different transactions depending on the payload (webhook or subscription)
  (p/then
   (cond
     (= "webhook_raw_payload" (:type payload))
     (transact [{:schema/entity-type :vonwig.testing/observation
                 :vonwig.testing.observation/id (str (random-uuid))
                 :vonwig.testing.observation/seen-by-subscriber false
                 :vonwig.testing.observation/webhook-value (-> payload :webhook :body)}])

     (= "on_observation.edn" (-> payload :subscription :name))
     (let [[[id webhook-value]] (-> payload :subscription :result)]
       (.info logger (str "observing " webhook-value))
       (transact [{:schema/entity-type :vonwig.testing/observation
                   :vonwig.testing.observation/id id
                   :vonwig.testing.observation/seen-by-subscriber true}]))
     :else
    ;; unrecognized
     (p/resolved true))
   (fn [_] 
     ;; promise should return a status message
     {:atomist/status {:code 0 :reason "completed successfully"}} )))
;; start the http listener with handler
(api/start (mw/clj-adapter-middleware handler))
