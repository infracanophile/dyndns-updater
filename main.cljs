#!/usr/bin/env lumo
(require '[cljs.nodejs :as nodejs])
(def https (nodejs/require "https"))
(def fs (nodejs/require "fs"))

(def config
  (-> (.parse js/JSON (.readFileSync fs "config.json"))
      (js->clj)))

(defn get-req [url]
  (js/Promise.
   (fn [resolve reject]
     (.get https url
           (fn [res]
             (if (<= 200 (.-statusCode res) 299)
               (resolve nil)
               (reject nil)))))))

(defn update-service
  [url]
  (get-req url))

(let [args (js->clj js/process.argv)
      target (when (> 3 (count args)))]
  (if (= target "--all")
    (doseq [[service-name url] config]
      (println "updating" (pr-str service-name))
      (-> (update-service url)
          (.then
           (fn [_] (println service-name "updated successfully"))
           (fn [_] (println "ERROR" service-name)))))
    (if-let [service-url (get config target)]
      (do
        (println "updating!")
        (update-service service-url))
      (do
        (println "ERROR: either provide a service to update")
        (println "\tknown services:" (-> config keys pr-str))))))
