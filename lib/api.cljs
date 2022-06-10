(ns api
  (:require ["express$default" :as express]
            ["body-parser$default" :as body-parser]
            ["google-auth-library$default" :as google-auth-library]
            [log]
            [http]
            [promesa.core :as p]
            [clojure.string :as string]
            [goog.string :refer [format]]))

(defn encode [s] (.toString (js/Buffer. s) "base64"))
(defn decode [s] (.toString (.from js/Buffer s "base64")))
(defn json-payload [m]
  (encode (.stringify js/JSON (clj->js m))))

(defn resolve-payload [pub-sub-event]
  (let [json ((js/require "json-bigint") #js {:alwaysParseAsBig false :useNativeBigInt true})
        original-parse (.-parse js/JSON) ]
    (set! (.-parse js/JSON) (fn [text reviver]
                              (try
                                (.parse json text reviver)
                                (catch Error _
                                  (original-parse text reviver)))))
    (set! (.-stringify js/JSON) (.-stringify json))
    (.parse js/JSON (decode (.-data pub-sub-event)))))

;; testing
(comment
  (.stringify js/JSON (resolve-payload #js {:data (json-payload {})})))
;; -------

(defn publish 
  [{:keys [data orderingKey]}]
  (let [url (format "https://pubsub.googleapis.com/v1/projects/%s/topics/%s:publish" "atomist-skill-production" (.. js/process -env -ATOMIST_TOPIC))]
    (p/chain
     (.getAccessToken (new (.-GoogleAuth google-auth-library) #js {:scopes "https://www.googleapis.com/auth/cloud-platform"}))
     (fn [token]
       (http/response->json
        (http/post url {:body (.stringify js/JSON (clj->js {:messages [{:data (json-payload data) :orderingKey orderingKey}]}))
                        :headers {:content-type "application/json"
                                  :authorization (format "Bearer %s" token)}})))
     (fn [json] json))))

(defn start [handler]
  (let [app (express)]
    (log/info "listen for requests")
    (.use app (.json body-parser #js {:limit "1024mb"}))
    (.post app "/" (fn [req resp]
                     (let [payload (-> (resolve-payload (.. req -body -message))
                                       (js->clj :keywordize-keys true)
                                       (assoc
                                        :trace-id (-> (.get req "x-cloud-trace-context")
                                                      (string/split #"/")
                                                      first)
                                        :event-id (.. req -body -message -messageId)))
                           topic-publisher (fn [obj]
                                             (publish {:data obj
                                                       :orderingKey (:correlation_id payload)}))]
                       (log/create-logger (merge
                                           {:skillId (-> payload :skill :id)
                                            :workspaceId (-> payload :team_id)
                                            :correlationId (:correlation_id payload)
                                            :eventId (or (:event-id payload) "unknown")
                                            :name (or (:operation payload) "default")
                                            :skill (format "%s/%s@%s"
                                                           (-> payload :skill :namespace)
                                                           (-> payload :skill :name)
                                                           (-> payload :skill :version))}
                                           (when (:trace-id payload) {:traceId (:trace-id payload)})))
                       (.catch
                        (.then
                         (handler (assoc payload :publisher topic-publisher))
                         (fn [m]
                           (topic-publisher
                            {:status (:atomist/status m)
                             :content_type "application/x-atomist-status+json"})
                           (.sendStatus resp 201)))
                        (fn [err]
                          (topic-publisher
                           {:status {:code 1 :reason (str "handler failure " err)}
                            :content_type "application/x-atomist-status+json"})
                          (.sendStatus resp 201)))
                       (log/close-logger))))
    (.listen app (or (.. js/process -env -PORT) 8080))))

