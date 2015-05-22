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

(ns jogurt.auth.openid
  "OpenID Connect authentication method."
  (:api dunaj)
  (:require [dunaj.format.clj :refer [pretty-clj]]
            [dunaj.host.array :as dha]
            [dunaj.resource :refer [IAcquirableFactory request!]]
            [hiccup.element :refer [link-to]]
            [hiccup.page :as hp]
            [ring.util.codec :as ruc]
            [ring.util.response :as rur]
            [ring.middleware.session :refer [session-response]]
            [clj-jwt.core :as jwt]
            [jogurt.util :as ju]
            [jogurt.util.cfg :refer [sget-in]]
            [jogurt.auth :refer [IAuthEngine]]))


(defn redirect-uri :- String
  "Returns a redirect uri string."
  [cfg :- {}]
  (let [hostname (sget-in cfg [:env :jogurt-hostname])
        protocol (sget-in cfg [:env :jogurt-protocol])
        port (sget-in cfg [:env :jogurt-port])
        path (sget-in cfg :callback-path)]
    (->str protocol "://" hostname ":" port "/" path)))

(deftype OpenIdAuthEngine [cfg]
  IAuthEngine
  (-sign-in [this]
    [:div
     [:div.jgsb
      (let [base-url "https://accounts.google.com/o/oauth2/auth"
            args {"client_id" (sget-in cfg [:env :jogurt-client-id])
                  "response_type" "code"
                  "scope" "openid email"
                  "redirect_uri" (redirect-uri cfg)
                  "state" (sget-in cfg [:anti-forgery])}
            url (->str base-url "?" (ju/make-query-string args))]
        (link-to url [:span "Sign in with Google"]))]])
  (-callback [this request]
    (let [code (get-in request [:params :code])
          state (get-in request [:params :state])
          ;; TODO: assert state equals with anti-forgery in cfg
          req
          {"code" code
           "client_id" (sget-in cfg [:env :jogurt-client-id])
           "client_secret" (sget-in cfg [:env :jogurt-client-secret])
           "redirect_uri" (redirect-uri cfg)
           "grant_type" "authorization_code"}
          resp (ju/post "https://www.googleapis.com/oauth2/v3/token"
                        req)
          jsresp (parse-whole json resp)
          token (jwt/str->jwt (get jsresp "id_token"))
          email (get-in token [:claims :email])
          user-id (get-in token [:claims :sub])]
      #_(let [pretty-clj (assoc pretty-clj :pretty-item-limit 0)]
        (hp/html5 [:div#jgfoot
                   [:div
                    [:p "User email is"] [:pre email]
                    [:p "User unique id is"] [:pre user-id]]
                   [:div [:p "User info is"] [:pre token]]
                   [:div
                    [:p "Auth response is"]
                    [:pre (str (print-one json jsresp))]]
                   [:div
                    [:p "Request is"]
                    [:pre (str (print-one pretty-clj request))]]
                   [:div
                    [:p "Config is"]
                    [:pre (str (print-one pretty-clj cfg))]]]))
      (-> (rur/redirect "/")
          (assoc-in [:session :user-id] user-id)
          (assoc-in [:session :user-email] email)))))

(defrecord OpenIdAuthEngineFactory [cfg]
  IAcquirableFactory
  (-acquire! [this] (->OpenIdAuthEngine cfg)))

(def auth-factory
  (->OpenIdAuthEngineFactory nil))
