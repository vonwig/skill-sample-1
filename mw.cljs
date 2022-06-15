(ns mw
  (:require [promesa.core :as p]
            ["@atomist/skill-entry-point$default" :as api]))

(defn clj-adapter-middleware
  "the handlers are pure js but this converts to clj maps"
  [handler]
  (fn [obj]
    (p/let [result (handler
                    (-> obj
                        (js->clj :keywordize-keys true)
                        (dissoc :logger :publish))
                    :logger (.-logger obj)
                    :transact (fn [datoms] (-> datoms 
                                               pr-str 
                                               api/entitiesPayload 
                                               ((.-publish obj)))))]
      (set! (.-status obj) (clj->js (:atomist/status result)))
      obj)))
