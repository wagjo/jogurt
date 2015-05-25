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

(ns jogurt.store-test
  "Data store"
  (:require
   [dunaj.resource :refer [grab-scope release-scope! acquire!]]
   [jogurt.store :as js]
   [jogurt.util.cfg :refer [sget-in] :as juc]
   [jogurt.util :as ju]
   [clojure.test :refer :all]))

(def default-store-factory  "jogurt.store.memory/store-factory")

(def cfg (juc/cfg))

;; set test store in profiles.clj under :test profile
(def test-store-factory
  (let [sym (sget-in cfg [:env :jogurt-store] default-store-factory)]
    @(ju/fetch-var sym)))

(deftest put-get-test
  (testing "Testing put and get to store"
    (let [[store scope] (grab-scope (acquire! test-store-factory))
          table "mytable"
          id "my-id"
          data {"foo" "bar"}
          want (assoc data (js/id-attr store) id)]
      (js/put! store table id data)
      (is (= want (js/row store table id)))
      (release-scope! scope))))
