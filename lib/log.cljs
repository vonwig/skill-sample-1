(ns log
  (:require ["@atomist/skill-logging/lib/logging$default" :as skill-logging]
            [promesa.core :as p]))

(def atomist-logger (atom nil))

(defn create-logger [config]
  (let [skill-logger ((. skill-logging -createLogger) (clj->js config) (clj->js (select-keys config [:name :skill])))]
    (swap! atomist-logger (constantly skill-logger))))

(defn close-logger []
  (if @atomist-logger
    (.close @atomist-logger)
    (p/resolved true)))

(defn info [s]
  (if @atomist-logger
    (.info @atomist-logger s)
    (println s)))
