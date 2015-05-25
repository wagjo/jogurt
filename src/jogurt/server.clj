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
  "Web app server."
  (:require 
   [dunaj.resource :refer [IAcquirableFactory IReleasable]]
   [dunaj.resource.helper :refer [defreleasable]]
   [immutant.web :as web]
   [jogurt.util.cfg :refer [nget-in sget-in]]))

(defreleasable Server
  [stop-handler]
  IReleasable
  (-release! [this] (web/stop stop-handler)))

(defrecord ServerFactory [cfg routes servlet store]
  IAcquirableFactory
  (-acquire! [this]
    (let [port (nget-in cfg [:env :jogurt-port] 8080)
          ip (sget-in cfg [:env :jogurt-ip] "0.0.0.0")
          ring-handler
          (web/run routes {:port port :host ip :path "/"})
          stop-handler
          (web/run servlet (assoc ring-handler
                                  :port port
                                  :host ip
                                  :path "/edit"))]
      (->Server stop-handler))))

(def server-factory
  (->ServerFactory nil nil nil nil))
