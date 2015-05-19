

(ns jogurt.core
  (:require [org.httpkit.server :as hs]
            [clojure.tools.nrepl.server :as nrepls]
            [cider.nrepl :as nreplc]))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!"})

(defn -main [& args]
  (nrepls/start-server :port 7888 :handler nreplc/cider-nrepl-handler)
  (hs/run-server app {:port 8080})
  (println "Server started..."))
