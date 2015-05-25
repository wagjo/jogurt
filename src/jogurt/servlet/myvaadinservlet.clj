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

(ns jogurt.servlet.myvaadinservlet
  "Vaadin servlet."
  (:api dunaj)
  (:require [jogurt.servlet.vaadinui :as vui]
            [dunaj.resource :refer [IAcquirableFactory]])
  (:import [javax.servlet Servlet ServletConfig ServletRequest
            ServletResponse]
           [com.vaadin.server VaadinServlet])
  (:gen-class
   :extends com.vaadin.server.VaadinServlet
   :init myinit
   :constructors {[java.lang.Object] []}
   :name
   ^{com.vaadin.annotations.VaadinServletConfiguration
     {:ui jogurt.servlet.VaadinUI
      :productionMode false}}
   jogurt.servlet.MyVaadinServlet))

(defn -myinit
  [store]
  ;; TODO: Pass store to Vaadin UI more elegantly
  (reset! vui/store store)
  [[] nil])

(defrecord VaadinServletFactory [cfg store]
  IAcquirableFactory
  (-acquire! [this] (jogurt.servlet.MyVaadinServlet. store)))

(def servlet-factory
  (->VaadinServletFactory nil nil))
