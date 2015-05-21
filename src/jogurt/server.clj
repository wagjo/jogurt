;; Copyright (C) 2015, Jozef Wagner. All rights reserved.
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be
;; found in the file epl-v10.html at the root of this distribution.
;;
;; By using this software in any fashion, you are agreeing to be bound
;; by the terms of this license.
;;
;; You must not remove this notice, or any other, from this software.

(ns jogurt.server
  "Web app server"
  (:require [org.httpkit.server :as hs]
            [jogurt.routes :as jr]
            [jogurt.cfg :refer [nget-in]]))


(defn start!
  "Starts web server. Returns function of zero args that shuts down
  the server when invoked.
  Uses [:env :jogurt-port] for port number, defaults to 8080."
  [cfg]
  (let [port (nget-in cfg [:env :jogurt-port] 8080)
        ip (get-in cfg [:env :jogurt-ip] "0.0.0.0")]
    (hs/run-server #'jr/app-routes {:port port :ip ip})))


;;;; Scratch

(comment
  
  (def s (start! (jogurt.cfg/cfg)))

  (keys (get-in (jogurt.cfg/cfg) [:env :jogurt-client-id]))

  (s)

)
