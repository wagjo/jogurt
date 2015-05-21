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
  (:require [jogurt.auth :refer [IAuthEngine IAuthEngineFactory]]
            [hiccup.element :refer [link-to]]
            [ring.middleware.session :refer [session-response]]
            [ring.util.response :as rur]))

(defrecord FakeAuthEngine []
  IAuthEngine
  (-sign-in [this cfg]
    [:div
     [:div.jgsb
      (link-to "authback"
               [:span "Sign in with fake credentials"])]])
  (-callback [this cfg request]
    (-> (rur/redirect "/")
     (assoc-in [:session :user-id] 1))))

(def auth-factory
  (reify IAuthEngineFactory
    (-auth [this cfg] (->FakeAuthEngine))))