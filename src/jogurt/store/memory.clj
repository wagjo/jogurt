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

(ns jogurt.store.memory
  "Memory storage."
  (:api dunaj)
  (:require
   [dunaj.resource :refer [IAcquirableFactory]]
   [jogurt.store :as js :refer [IStoreEngine]]))

(deftype MemoryStoreEngine [cfg db-ref]
  IStoreEngine
  (-init! [this tables]
    (let [ifn (fn [db] (merge (into {} (map #(pair % {}) tables)) db))]
      (alter! db-ref ifn)))
  (-id [this] ::id)
  (-sorted [this table attr val sort]
    (let [rows (seq (get @db-ref table))
          ff (fn [[id attrs]]
               (cond (nil? attr) true
                     (identical? attr ::id) (= val id)
                     :else (= val (get attrs attr))))
          frows (filter ff rows)
          srows (if sort
                  (reverse (sort-by #(get (second %) sort) frows))
                  frows)]
      (vec (map (fn [[id attrs]] (assoc attrs ::id id)) srows))))
  (-put! [this table id kv-map]
    (let [rf #(merge % kv-map)
          uf #(update % id rf)]
      (alter! db-ref #(update % table uf))))
  (-delete! [this table id]
    (let [uf #(dissoc % id)]
      (alter! db-ref #(update % table uf)))))

(defrecord MemoryStoreEngineFactory [cfg]
  IAcquirableFactory
  (-acquire! [this] (->MemoryStoreEngine cfg (atom {}))))

(def store-factory
  (->MemoryStoreEngineFactory nil))


;;;; Scratch

(comment


  (def ss (grab-scope (acquire! store-factory)))

  (def s (first ss))

  (js/id-attr s)
  (js/init! s :users :posts)
  (js/row s :users 1)
  (js/row s :users 2)
  (js/row s :users 3)
  (js/put! s :users "1" {:name "Jozo"})
  (js/put! s :users 2 {:name "Samko"})
  (js/delete! s :users "1")

  {:users {"1" {:name "Fake User" :email "fake.user@example.com"}}
   :posts {"1" {:title "Sample post"
                :body "== Sample post\n\nBla bla"
                :author "1"
                :date (canonical (now))}}}
  

)
