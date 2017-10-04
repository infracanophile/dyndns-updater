(ns ddu.main
  (:require [cljs.nodejs :as nodejs]
            [cljs.core.async :refer [chan put! <! close!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

;; Use print/println instead of console.log
(nodejs/enable-util-print!)

(def https (nodejs/require "https"))
(def fs (nodejs/require "fs"))

(def config
  (-> (.parse js/JSON (.readFileSync fs "config.json"))
      (js->clj :keywordize-keys true)))


(defn get-req
  "Create a channel. Start connecting to the url using callbacks to put data on
  the channel and close the channel when request is complete.
  Return the channel"
  [url]
  (let [response-ch (chan)]
    (.get https url
          (fn [res]
            (.on res "data"
                 (fn [data]
                   (put! response-ch data)))
            (.on res "end"
                 (fn [] (close! response-ch)))))
    response-ch))


(defn update-dyndns
  "Make the request. Read responses off the channel, combining any chunks
  When the channel closes, print the full response. We could just print as we
  get them but that could interleave text in the console if the response is
  long and something else is printing (stderr for example) (we know it won't be
  but still good practice)"
  []
  (let [response-ch (get-req (:dns-url config))]
    ;; Could use go-loop but that isn't a recur target in cljs, some old bug?
    (go
      (loop [response ""]
        (if-let [data (<! response-ch)]
          (recur (str response data))
          (println response))))))


(defn main []
  ;; outrageous hack to get clojurescript/node to work propertly with node
  ;; Bug ASYNC-110 is not fixed
  (set! js/goog.global js/global)
  (update-dyndns))
