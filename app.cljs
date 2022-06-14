(ns app
  (:require ["@atomist/skill-entry-point$default" :as api]
            [promesa.core :as p]
            [mw]))

(defn handler 
  "sample cljs handler
     params
       m - clj map representing the skill payload (insert schema url)
       options - :logger and :publisher :transaction-publisher will always be present
               - logger is a preconfigured @atomist/skills-logging instance (needs docs)
               - transaction-publisher will transact new datoms
     returns
       must return a promise - add a map with an :atomist/status key to automatically send status"
  [m & {:keys [logger transaction-publisher]}]
  (.info logger (pr-str m))
  #_(transaction-publisher [])
  ;; resolve a promise and add status
  (p/resolved {:atomist/status {:code 0 :reason "completed successfully"}}))

;; start the http listener with handler
(api/start (mw/clj-adapter-middleware handler))
