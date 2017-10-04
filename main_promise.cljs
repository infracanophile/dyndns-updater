#!/usr/bin/env lumo
(require '[cljs.nodejs :as nodejs])
(def https (nodejs/require "https"))
(def fs (nodejs/require "fs"))

(def config
  (-> (.parse js/JSON (.readFileSync fs "config.json"))
      (js->clj :keywordize-keys true)))

(defn promise [executor]
  (new js/Promise executor))

(defn get-req [url]
  (promise
   (fn [resolve]
     (.get https url
           (fn [res]
             (-> res .-statusCode resolve))))))

(-> (get-req (:dns-url config))
    (.then println))
