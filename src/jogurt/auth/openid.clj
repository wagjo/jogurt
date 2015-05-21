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
  (:require [jogurt.auth :refer [IAuthEngine IAuthEngineFactory]]
            [hiccup.element :refer [link-to]]
            [hiccup.page :as hp]
            [ring.util.codec :as ruc]
            [dunaj.format.clj :refer [pretty-clj]]
            [dunaj.host.array :as dha]
            [clj-jwt.core :as jwt]
            [ring.middleware.session :refer [session-response]]
            [ring.util.response :as rur]))

(defn make-query-string
  "Transforms a map into url params"
  [m]
  (->> (for [[k v] m]
         (->str k "=" (java.net.URLEncoder/encode v "UTF-8")))
       (interpose "&")
       (apply ->str)))

(defn redirect-uri
  [cfg path]
  (let [hostname (get-in cfg [:env :jogurt-hostname])
        protocol (get-in cfg [:env :jogurt-protocol])
        port (get-in cfg [:env :jogurt-port])]
    (->str protocol "://" hostname ":" port "/" path)))

(defrecord OpenIdAuthEngine []
  IAuthEngine
  (-sign-in [this cfg]
    [:div
     [:div.jgsb
      (let [base-url "https://accounts.google.com/o/oauth2/auth"
            anti-forgery (->str (get-in cfg [:anti-forgery]))
            args {"client_id" (get-in cfg [:env :jogurt-client-id])
                  "response_type" "code"
                  "scope" "openid email"
                  "redirect_uri" (redirect-uri cfg "authback")
                  "state" anti-forgery}]       
        (link-to (->str base-url "?" (make-query-string args))
                 [:span "Sign in with Google"]))]])
  (-callback [this cfg request]
    (let [state (get-in request [:params :state])
          ;; TODO assert state equals with anti-forgery in cfg
          code (get-in request [:params :code])
          hr (http "https://www.googleapis.com/oauth2/v3/token"
                   :request-method :post)
          client-secret (get-in cfg [:env :jogurt-client-secret])
          anti-forgery (->str (get-in cfg [:anti-forgery]))
          req {"code" code
               "client_id" (get-in cfg [:env :jogurt-client-id])
               "client_secret" client-secret
               "redirect_uri" (redirect-uri cfg "authback")
               "grant_type" "authorization_code"}
          _ (pr! (make-query-string req))
          resp (with-io-scope 
                 (let [r (acquire! hr)]
                   (write! r (print utf-8 (make-query-string req)))
                   (str (parse utf-8 (read! r)))))
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

(def auth-factory
  (reify IAuthEngineFactory
    (-auth [this cfg] (->OpenIdAuthEngine))))
