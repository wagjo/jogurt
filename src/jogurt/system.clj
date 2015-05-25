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

(ns jogurt.system
  (:api dunaj)
  (:require [dunaj.resource :as dr]
            [dunaj.uuid :as du]
            [jogurt.util :as ju]
            [jogurt.util.cfg :as juc :refer [sget-in]]
            [jogurt.server :refer [server-factory]]
            [jogurt.servlet.basic :refer [basic-servlet-factory]]
            [jogurt.ring.routes :refer [routes-factory]]
            [jogurt.servlet.myvaadinservlet :refer [servlet-factory]])
  (:import [jogurt.servlet MyVaadinServlet]))

(def cfg 
  (assoc (juc/cfg)
         :anti-forgery (du/random)))

(def default-auth-factory  "jogurt.auth.openid/auth-factory")

(def default-store-factory  "jogurt.store.memory/store-factory")

(def auth-factory
  (let [sym (sget-in cfg [:env :jogurt-auth] default-auth-factory)]
    @(ju/fetch-var sym)))

(def store-factory
  (let [sym (sget-in cfg [:env :jogurt-store] default-store-factory)]
    @(ju/fetch-var sym)))

(def jogurt-system
  (dr/system
   :cfg cfg
   :auth auth-factory
   :store store-factory
   :routes routes-factory
   :server server-factory
   :servlet servlet-factory))

(defonce jogurt (atom nil))

(defn shutdown!
  ([]
   (shutdown! jogurt))
  ([jref]
   (let [[sys scope] @jref]
     (when scope (release-scope! scope))
     (reset! jref nil))))

(defn launch!
  ([]
   (launch! jogurt))
  ([jref]
   (shutdown! jref)
   (reset! jref (grab-scope (start! jogurt-system)))))

;;;; Scratch

(scratch []

  []

  (launch!)

  (:servlet (first @jogurt))
  (:routes jogurt)
  jogurt

  (seq (map first (seq jogurt-system)))

  (shutdown!)

  (jogurt.store/row (get (first @jogurt) :store) :users "1")
  (jogurt.store/put! (get (first @jogurt) :store) :users "a"
                     {:foo "bar"})


  (jogurt.store/row (get (first @jogurt) :store) :users "109257541881760522726")

  (jogurt.store/row (get (first @jogurt) :store) :posts "e41f2229-421d-48ba-81d0-277cd9578edb")

)
