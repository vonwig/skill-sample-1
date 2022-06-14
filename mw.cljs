(ns mw
  (:require [promesa.core :as p]))

(defn clj-adapter-middleware
  "the handlers are pure js but this converts to clj maps"
  [handler]
  (fn [obj]
    (p/let [result (handler
                    (-> obj
                        (js->clj :keywordize-keys true)
                        (dissoc :logger :publish))
                    :logger (.-logger obj)
                    :transaction-publisher (fn [datoms]))]
      (set! (.-status obj) (clj->js (:atomist/status result)))
      obj)))
