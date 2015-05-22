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

(ns jogurt.auth.fake
  "Fake authentication method."
  (:api dunaj)
  (:require
   [dunaj.resource :refer [IAcquirableFactory]]
   [jogurt.cfg :refer [sget]]
   [jogurt.auth :refer [IAuthEngine IAuthEngineFactory]]
   [hiccup.element :refer [link-to]]
   [ring.middleware.session :refer [session-response]]
   [ring.util.response :refer [redirect]]))


(deftype FakeAuthEngine [cfg]
  IAuthEngine
  (-sign-in [this]
    (let [label [:span "Sign in with fake credentials"]
          path (sget cfg :callback-path "authback")]
      [:div [:div.jgsb (link-to path label)]]))
  (-callback [this request]
    (let [fake-user-id (sget cfg :fake-user-id "1")]
      (-> (redirect "/")
          (assoc-in [:session :user-id] fake-user-id)))))

(defrecord FakeAuthEngineFactory [cfg]
  IAcquirableFactory
  (-acquire! [this] (->FakeAuthEngine cfg)))

(def auth-factory
  (->FakeAuthEngineFactory nil))
