(ns mw
  (:refer-clojure :exclude [pr-str])
  (:require [promesa.core :as p]
            ["@atomist/skill-entry-point$default" :as api]))

;; this is a pr-str that is js/BigInt aware
(def pr-str (comp api/prStr (fn [m] (clj->js m :keyword-fn #(str (namespace %) "/" (name %))))))

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
                                               ((partial api/entitiesPayload (.-correlation_id obj)))
                                               ((.-publish obj)))))]
      (set! (.-status obj) (clj->js (:atomist/status result)))
      obj)))
