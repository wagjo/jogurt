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

(ns jogurt.store.sdb
  "SimpleDB storage."
  (:api dunaj)
  (:require
   [dunaj.resource :refer [IAcquirableFactory]]
   [cemerick.rummage :as rum]
   [cemerick.rummage.encoding :as enc]
   [jogurt.util.cfg :refer [sget-in]]
   [jogurt.store :as js :refer [IStoreEngine -id]]))

(def max-length 700)

(defn encode-value
  [val]
  (set (map (fn [i v] (->str (print "%04d" i) (str v)))
            (unpacked (indexed (partition-all max-length val))))))

(defn decode-value
  [set]
  (str (mapcat #(section % 4) (sort set))))

(defn encode-map
  [m]
  (zipmap (keys m)
          (map #(if (< (count %) max-length) % (encode-value %))
               (vals m))))

(defn decode-map
  [m]
  (zipmap (keys m)
          (map #(if (set? %) (decode-value %) %)
               (vals m))))

(deftype SimpleDBStoreEngine [cfg client sdbc]
  IStoreEngine
  (-init! [this tables]
    (doseq [table tables]
      (rum/create-domain client (name table))))
  (-id [this] :cemerick.rummage/id)
  (-sorted [this table attr val sort]
    (let [r (let [table (name table)]
              (if (nil? sort)
                (cond
                  (nil? attr)
                  (rum/query sdbc `{select * from ~table})
                  :else
                  (rum/query sdbc
                             `{select * from ~table where (= ~attr ~val)}))
                (cond
                  (nil? attr)
                  (rum/query sdbc `{select * from ~table
                                    where (not-null ~sort)
                                    order-by [~sort desc]})
                  :else
                  (rum/query sdbc
                             `{select * from ~table where
                               (and (= ~attr ~val)
                                    (not-null ~sort))
                               order-by [~sort desc]}))))]
      (seq (map decode-map r))))
  (-put! [this table id kv-map]
    (let [kv-map (encode-map kv-map)]
      (rum/put-attrs sdbc (name table) (assoc kv-map (-id this) id))))
  (-delete! [this table id]
    (rum/delete-attrs sdbc (name table) id)))

(defrecord SimpleDBStoreEngineFactory [cfg]
  IAcquirableFactory
  (-acquire! [this]
    (let [aws-id (sget-in cfg [:env :jogurt-aws-id])
          aws-secret-key (sget-in cfg [:env :jogurt-aws-secret-key])
          client (rum/create-client aws-id aws-secret-key)
          sdbc (assoc enc/keyword-strings :client client)]
      (->SimpleDBStoreEngine cfg client sdbc))))

(def store-factory
  (->SimpleDBStoreEngineFactory nil))


;;;; Scratch

(scratch [[jogurt.util.cfg :as juc]]

  (def cfg (juc/cfg))

  (def client
    (let [aws-id (sget-in cfg [:env :jogurt-aws-id])
          aws-secret-key (sget-in cfg [:env :jogurt-aws-secret-key])]
      (rum/create-client aws-id aws-secret-key)))

  (def sdbc (assoc enc/keyword-strings :client client))

  (rum/create-domain client "foo")
  (rum/delete-domain client "posts")

  

  (rum/put-attrs sdbc "foo"
                 {:cemerick.rummage/id "1"
                  :param1 "val1"
                  :param2 "val2"})

  (rum/put-attrs sdbc "foo"
                 {:cemerick.rummage/id "2"
                  :param1 "val21"
                  :param2 (encode-value "lorem ipsum dolor sit")})

  (:param2 (rum/get-attrs sdbc "foo" "2"))

  (rum/query sdbc `{select * from "foo"})

  (rum/query sdbc `{select * from "foo"
                    where (= "param1" "val21")})

  (rum/delete-attrs sdbc "foo" "1")

  (rum/list-domains client)

  client

  ()


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
