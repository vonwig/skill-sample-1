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
  (logger :info (pr-str payload))

  ;; there's also a transaction function for pushing data to the service
  ;; this skill might execute different transactions depending on the payload (webhook or subscription)
  (p/then
   (p/resolved true) 
   (fn [_] 
     ;; promise should return a status message
     {:atomist/status {:state :completed :reason "completed successfully"}})))

;; start the http listener with handler
(api/start (mw/clj-adapter-middleware handler))
