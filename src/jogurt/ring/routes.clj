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

(ns jogurt.ring.routes
  "Web app routes"
  (:api dunaj)
  (:require
   [dunaj.resource :refer [IAcquirableFactory]]
   [hiccup.core :as hc]
   [hiccup.page :as hp]
   [hiccup.element :as he]
   [jogurt.util.cfg :as jc]
   [jogurt.ring.page :as jp]
   [jogurt.auth :as ja]
   [jogurt.store :as js]
   [dunaj.uuid :as du]
   [ring.util.response :as rur]
   [jogurt.session.global :as jsg]
   [compojure.core :as cc
    :refer [defroutes GET POST DELETE ANY context]])
  (:use
   [compojure.route :only [files not-found resources]]
   [compojure.handler :only [site]]))

(defn sign-out
  [request]
  ;; TODO: guard against CSRF attack so that sign out cannot be
  ;; automated
  (assoc (rur/redirect "/") :session nil))

(defn make-routes
  [cfg auth store]
  (cc/routes
   (GET "/" [] (partial jp/index-page cfg auth store))
   (GET "/post/:pid" [] (partial jp/post-page cfg auth store))
   (GET "/new" [] (partial jp/new-page cfg auth store))
   (POST "/submitpost" [] (partial jp/new-post cfg auth store))
   (GET "/authback" [] (partial ja/callback auth))
   (GET "/signout" [] sign-out)
   (resources "/static/")
   (not-found "<p>Page not found.</p>")))

(defrecord RoutesFactory [cfg auth store]
  IAcquirableFactory
  (-acquire! [this]
    (let [schema (get cfg :schema [:users :posts])]
      (apply js/init! store schema))
    (site (make-routes cfg auth store)
          {:session {:store (jsg/memory-store)}})))

(def routes-factory
  (->RoutesFactory nil nil nil))
