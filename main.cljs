#!/usr/bin/env lumo
(require '[cljs.nodejs :as nodejs])
(def https (nodejs/require "https"))
(def fs (nodejs/require "fs"))

(def config
  (-> (.parse js/JSON (.readFileSync fs "config.json"))
      (js->clj :keywordize-keys true)))

(def raw-data (atom nil))

(defn get-req [url]
  (.get https url
        (fn [res]
          (.on res "data"
               (fn [data]
                 (reset! raw-data (str @raw-data data))))
          (.on res "end"
               (fn [] (js/console.log @raw-data))))))

(get-req (:dns-url config))
