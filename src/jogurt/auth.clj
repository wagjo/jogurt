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
  (:api dunaj)
  (:require [dunaj.lib :refer [require!]]))

(defprotocol IAuthEngine
  "A protocol for auth implementers."
  (-sign-in
    "Returns hiccup content for signing in."
    [this cfg])
  (-callback
    "Returns ring response for signing callback."
    [this cfg request]))

(defprotocol IAuthEngineFactory
  "A factory protocol for instantiating auth engines."
  (-auth [this cfg]))

(defn auth :- IAuthEngine
  "Returns auth engine from a given auth factory and configuration."
  [factory :- IAuthEngineFactory cfg]
  (-auth factory cfg))

(defn fetch-factory :- IAuthEngineFactory
  "Returns auth factory from a given fully qualified string symbol."
  [sym]
  (let [afs (symbol (name sym))]
    (require! (symbol (namespace afs)))
    (eval afs)))

(defn sign-in
  "Returns sign in content."
  [auth cfg]
  (-sign-in auth cfg))

(defn callback
  "Returns callback response."
  [auth cfg request]
  (let [resp (-callback auth cfg request)]
    (pp! resp)
    resp))
