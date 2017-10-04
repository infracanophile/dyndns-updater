(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[adzerk/boot-cljs "1.7.228-2" :scope "test"]
                  [org.clojure/clojurescript "1.9.946"]
                  [org.clojure/clojure "1.9.0-beta1"]
                  [org.clojure/core.async "0.3.443"]
                  [cljs-node-io "0.5.0"]])

(require '[adzerk.boot-cljs :refer [cljs]])


(deftask dev
  "watch/compile"
  []
  (comp
   (watch)
   (cljs)
   (target)))


(deftask build
  "full build"
  []
  (comp (cljs :optimizations :simple)
        (target)))
