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
   [org.httpkit.server :as hs]
   [jogurt.util.cfg :refer [nget-in sget-in]]))

(defreleasable Server
  [stop-fn]
  IReleasable
  (-release! [this] (stop-fn)))

(defrecord ServerFactory [cfg routes]
  IAcquirableFactory
  (-acquire! [this]
    (let [port (nget-in cfg [:env :jogurt-port] 8080)
          ip (sget-in cfg [:env :jogurt-ip] "0.0.0.0")
          stop-fn (hs/run-server routes {:port port :ip ip})]
      (->Server stop-fn))))

(def server-factory
  (->ServerFactory nil nil))
