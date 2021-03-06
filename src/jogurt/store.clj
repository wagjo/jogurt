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

(ns jogurt.store
  "Data store"
  (:api dunaj :exclude [get put!])
  (:require [dunaj.lib :refer [require!]]
            [dunaj.coll.util :refer [select-keys]]))

;;;; SPI

(defprotocol IStoreEngine
  "A protocol for store implementers."
  (-init!
    "Makes sire tables with given names are present in storage."
    [this tables])
  (-id
    "Returns name of attr that represents primary key."
    [this])
  (-sorted
    "Returns rows that have val under given attr,
    sorted by sort attr. Must accept nil sort or attr."
    [this table attr val sort])
  (-put!
    "Puts attributes into given table, under id"
    [this table id kv-map])
  (-delete!
    "Deletes given row."
    [this table id]))

;;;; Public API

(defn init!
  "Initializes database. Makes sure given tables are present"
  [store & tables]
  (-init! store tables)
  (require! 'dunaj.user)
  (with-bindings [#'clojure.core/*ns* (clojure.core/the-ns 'dunaj.user)]
    (let [seed (eval (with-scope
                       (parse-whole edn (slurp "cp:default.edn"))))
          tosend (select-keys seed tables)]
      (doseq [[table rowmap] (seq tosend)]
        (doseq [[id attrmap] (seq rowmap)]
          (-put! store table id attrmap))))))

(defn id-attr
  "Returns name of attr that represents primary key."
  [store]
  (-id store))

(defn get
  "Returns rows that have val under given attr."
  [store table attr val]
  (-sorted store table attr val nil))

(defn sorted
  "Returns rows that have val under given attr, sorted by sort attr."
  [store table attr val sort]
  (-sorted store table attr val sort))

(defn row
  "Returns row from table with a given id."
  [store table id]
  (first (get store table (id-attr store) id)))

(defn put!
  "Puts attribute map into given table, under id"
  [store table id kv-map]
  (-put! store table id kv-map))

(defn delete!
  "Deletes given row."
  [store table id]
  (-delete! store table id))
