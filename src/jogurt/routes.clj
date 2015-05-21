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

(ns jogurt.routes
  "Web app routes"
  (:api dunaj)
  (:require [hiccup.core :as hc]
            [hiccup.page :as hp]
            [hiccup.element :as he]
            [jogurt.cfg :as jc]
            [jogurt.page.index :as jpi]
            [jogurt.auth :as ja]
            [dunaj.uuid :as du]
            [ring.util.response :as rur])
  (:use
   [compojure.route :only [files not-found resources]]
   [compojure.handler :only [site]]
   [compojure.core :only [defroutes GET POST DELETE ANY context]]))

(def cfg (assoc (jc/cfg) :anti-forgery (du/random)))

(def auth (ja/auth (ja/fetch-factory
                    (get-in cfg [:env :jogurt-auth]
                            "jogurt.auth.openid/auth-factory"))
                   cfg))

(defn sign-out
  [cfg request]
  (let [response (rur/redirect "/")]
    (assoc response :session nil)))

(defroutes app-routes*
  (GET "/" [] (partial jpi/index-page cfg auth))
  (GET "/authback" [] (partial ja/callback auth cfg))
  (GET "/signout" [] (partial sign-out cfg))
  (resources "/static/")
  (not-found "<p>Page not found.</p>"))

(def app-routes (site app-routes*))
