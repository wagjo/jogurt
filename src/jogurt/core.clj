(ns jogurt.core
  (:require [org.httpkit.server :as hs]))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!"})

(defn -main [& args]
  (hs/run-server app {:port 8080}))
