(ns mversioner.core
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.util.response :refer [response]]
            [clojure.string :as str]
            [immuconf.config :as immuconf])
  (:import com.google.api.client.json.jackson2.JacksonFactory
           com.google.api.services.compute.Compute
           com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
           com.google.api.client.googleapis.auth.oauth2.GoogleCredential
           com.google.api.services.compute.Compute$Builder
           )
  (:gen-class))

(def config (immuconf.config/load "resources/config.edn" "/etc/mversioner/config.edn"))

(def JSON_FACTORY (JacksonFactory/getDefaultInstance))
(def httpTransport (GoogleNetHttpTransport/newTrustedTransport))
(def credential (GoogleCredential/getApplicationDefault))

(def compute (.build (.setApplicationName (Compute$Builder. httpTransport JSON_FACTORY credential) "mversioner")))
(def mproject (.execute (.get (.projects compute) (immuconf.config/get config :project))))



(defn meta-item-to-service [mi]
  {:service (first (str/split (get mi "key") #"_")) :version (get mi "value")})


(defn list-versions [params]
  (let [
        items (into () (.getItems (.getCommonInstanceMetadata mproject)))
        versions (map
                   meta-item-to-service
                   (filter (fn [x] (str/ends-with? (get x "key") "_version")) items))
        ]
    {:body {:versions versions }}
    ))

(defroutes main-routes
  (GET "/" [] "m'versioner")
  (POST "/versions/list" 
        {params :params} 
        (list-versions params))
  (route/not-found "not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-json-params)
      (wrap-json-response)))
