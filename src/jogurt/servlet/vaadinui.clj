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

(ns jogurt.servlet.vaadinui
  "Hello world vaadin servlet."
  (:api dunaj)
  (:require
   [dunaj.resource :refer [IAcquirableFactory]]
   [jogurt.session.global :as jsg]
   [jogurt.store :as js]
   [jogurt.render :refer [purge-cache!]]
   [ring.util.servlet :as rus])
  (:import
   [com.vaadin.ui UI VerticalLayout Label TextField Button
    TextArea Button$ClickListener Notification Notification$Type]
   [com.vaadin.shared.ui.label ContentMode]
   [com.vaadin.server VaadinRequest])
  (:gen-class
   :extends com.vaadin.ui.UI
   :name ^{com.vaadin.annotations.Theme "valo"}
   jogurt.servlet.VaadinUI))


(defn get-ring-session
  [vaadin-request]
  (some #(and (= "ring-session" (.getName %)) (.getValue %))
        (seq (.getCookies vaadin-request))))

(defonce store (atom nil))

(defn get-post-title
  [store pid]
  (:title (js/row store :posts pid)))

(defn get-post-body
  [store pid]
  (:body (js/row store :posts pid)))

(defn get-post-id
  [vaadin-request]
  (.getParameter vaadin-request "post-id"))

(defn get-uid
  [vaadin-request]
  (get-in @jsg/global-session [(get-ring-session vaadin-request)
                               :user-id]))

(defn get-email
  [vaadin-request]
  (get-in @jsg/global-session [(get-ring-session vaadin-request)
                               :user-email]))

(defn save-post
  [store pid title body]
  (js/put! store :posts pid {:title title :body body}))

(defn -init [^com.vaadin.ui.UI this ^VaadinRequest request]
  (let [view (VerticalLayout.)
        uid (get-uid request)
        pid (get-post-id request)]
    (cond (nil? uid)
          (do (.addComponent view (Label. "Not logged in"))
              (.setContent this view))
          (nil? pid)
          (do (.addComponent view (Label. "No post specified"))
              (.setContent this view))
          :else
          (let [title (get-post-title @store pid)
                body (get-post-body @store pid)
                tf (TextField. "Title:" title)
                tb (TextArea.
                    "Body (asciidoctor syntax supported):" body)
                b (Button. "Edit")
                clickListener
                (proxy [Button$ClickListener] []
                  (buttonClick [event] 
                    (purge-cache! pid)
                    (save-post
                     @store pid (.getValue tf) (.getValue tb))
                    (Notification/show
                     "Post Saved"
                     "Your post has been saved to the database"
                     Notification$Type/WARNING_MESSAGE))) ]
            (.addComponent view (Label. "Edit your post"))
            (.addListener b clickListener)
            (.setSizeFull this)
            (.setColumns tb 50)
            (.setRows tb 25)
            (.addComponent view tf)
            (.addComponent view tb)
            (.addComponent view b)
            (.addComponent
             view (Label. (->str "<a target=\"_blank\" href=\"/post/"
                                 pid
                                 "\">View in blog</a>")
                          ContentMode/HTML))
            (.setContent this view)))))
