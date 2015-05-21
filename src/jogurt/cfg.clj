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

(ns jogurt.cfg
  "Manage project's configuration."
  (:api dunaj)
  (:require [environ.core :as ec]))

(defn cfg :- {}
  "Returns config map. Merges contents of config.edn on a classpath
   with environment variables, that are fetched with environ and
   put under :env key."
  []
  (with-scope
    (let [conf (parse-whole edn (slurp "cp:config.edn"))
          env ec/env]
      (assoc conf :env env))))

(defn nget-in :- (Maybe Number)
  "Like get-in, but parses value with edn reader and asserts that
  it is a number. Does not parse default value. Allows nil values."
  ([cfg :- {} ks :- []]
   (nget-in cfg ks nil))
  ([cfg :- {} ks :- [] default-value :- (Maybe Number)]
   (when-let [v (get-in cfg ks default-value)]
     (cond
       (identical? v default-value) v
       (number? v) v
       :else
       (let [pv (parse-whole edn v)] 
         (when-not (number? pv)
           (throw (illegal-argument "cannot parse value to number")))
         pv)))))
