(ns ddu.main
  (:require [cljs.nodejs :as nodejs]
            [cljs.core.async :refer [chan put! <! close!]]
            [cljs-node-io.core :refer [slurp]]
            [cljs.reader :refer [read-string]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def https (nodejs/require "https"))
(def process (nodejs/require "process"))

(def default-config-file "config.edn")


(defn load-config
  [config-file]
  (-> config-file slurp read-string))


(defn cmd-line-args
  "Nodejs's process.argv property is [nodepath scriptpath & arguments]
  So we use the .- operator to access an object property
  drop the first two and take the first of the remaining seq
  will be either an arg or nil"
  []
  (->> (.-argv process) (drop 2) first))


(defn get-req
  "Create a channel. Start connecting to the url using callbacks to put data on
  the channel and close the channel when request is complete.
  Return the channel"
  [url]
  (let [response-ch (chan)]
    (.get https url
          (fn [res]
            (doto res
              (.on "data" #(put! response-ch %))
              (.on "end" #(close! response-ch)))))
    response-ch))


(defn update-dyndns
  "Make the request. Read responses off the channel, combining any chunks
  When the channel closes, print the full response. We could just print as we
  get them but that could interleave text in the console if the response is
  long and something else is printing (stderr for example) (we know it won't be
  but still good practice)"
  [{:keys [dns-url]}]
  (let [response-ch (get-req dns-url)]
    ;; Could use go-loop but that isn't a recur target in cljs, some old bug?
    (go
      (loop [response ""]
        (if-let [data (<! response-ch)]
          (recur (str response data))
          (println response))))))


(defn main
  []
  ;; outrageous hack to get cljs and core.async to cooperate with node
  ;; Bug ASYNC-110 is *not* fixed
  (set! js/goog.global js/global)
  ;; Use print/println instead of console.log
  (nodejs/enable-util-print!)
  (let [config-file (or (cmd-line-args) default-config-file)
        config (load-config config-file)]
    (update-dyndns config)))
