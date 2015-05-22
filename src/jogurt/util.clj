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

(ns jogurt.util
  "Miscellaneous utilities."
  (:api dunaj)
  (:require [dunaj.state.var :refer [find-var Var]]
            [dunaj.resource :refer [request!]]
            [dunaj.lib :refer [require!]]))

(defn fetch-var :- Var
  "Returns var from a given fully qualified string symbol, loading
  its namespace if needed."
  [sym :- (U String Symbol)]
  (let [afs (if (string? sym) (symbol sym) sym)]
    (require! (symbol (namespace afs)))
    (find-var afs)))

(defn make-query-string :- String
  "Transforms a map into url params"
  [m :- {}]
  (->> (for [[k v] m]
         (->str k "=" (java.net.URLEncoder/encode v "UTF-8")))
       (interpose "&")
       (apply ->str)))

(defn post :- String
  "Makes a POST request to url with given query-map used to construct
  the body of POST request. Waits for and returns response as string.
  Has hardcoded UTF-8 for coding."
  [url :- String, query-map :- {}]
  (-> (http url :request-method :post)
      acquire!
      (format utf-8)
      (request! (make-query-string query-map))
      str
      with-io-scope))
