#!/usr/bin/env lumo
(require '[cljs.nodejs :as nodejs])
(def https (nodejs/require "https"))

(def dns-url "https://freedns.afraid.org/dynamic/update.php?clh0R1dNNFlBUks1NkhYWVJSaE06MTU2MDQxMjQ=")
(def raw-data (atom nil))

(defn get-req [url]
  (.get https url
        (fn [res]
          (.on res "data"
               (fn [data]
                 (reset! raw-data (str @raw-data data))))
          (.on res "end"
               (fn [] (js/console.log @raw-data))))))

(get-req dns-url)
