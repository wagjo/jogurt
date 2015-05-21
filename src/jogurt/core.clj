(ns jogurt.core
  (:require [clojure.tools.nrepl.server :as nrepls]
            [cider.nrepl :as nreplc]))

(defn -main [& args]
  (nrepls/start-server :port 7888 :handler nreplc/cider-nrepl-handler)
  #_(hs/run-server (site #'all-routes) {:port 8080})
  (println "Server started..."))
