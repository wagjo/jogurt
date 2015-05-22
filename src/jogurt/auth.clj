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

(ns jogurt.auth
  "Authentication methods."
  (:api dunaj))

;;;; SPI

(defprotocol IAuthEngine
  "A protocol for auth implementations."
  (-sign-in :- Any
    "Returns hiccup content for signing in."
    [this])
  (-callback :- {}
    "Returns ring response for signing callback."
    [this request :- {}]))

;;;; Public API

(defn sign-in :- Any
  "Returns sign-in content."
  [auth :- IAuthEngine]
  (-sign-in auth))

(defn callback :- {}
  "Returns callback response."
  [auth :- IAuthEngine, request :- {}]
  (-callback auth request))
