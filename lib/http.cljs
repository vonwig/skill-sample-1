(ns http
  (:require ["node-fetch$default" :as node-fetch]
            [promesa.core :as p]))

(def https (js/require "https"))
(def http (js/require "http"))
(def request-init (.-RequestInit node-fetch))
(def response (.-Response node-fetch))
(def https-agent (new (.-Agent https) #js {:keepAlive true}))
(def http-agent (new (.-Agent http) #js {:keepAlive true}))

(defn prepare-url [url parameters] url)

(defn request [url {:keys [parameters] :as options}]
  (node-fetch (prepare-url url parameters) (-> options
                                               (assoc :agent (fn [parsed-url]
                                                               (case (.-protocol parsed-url) "http:" http-agent https-agent)))
                                               (clj->js))))

(defn post [url options]
  (request url (assoc options :method "POST")))

(defn response->json [r]
  (try
    (p/catch
     (p/then 
       (p/then r (fn [response] (.json response))) 
       (fn [json] (println "json " json) json))
     (fn [err] (println "no json in r" r err)))
    (catch Error err 
      (println "unable to set up response handler" err))))
