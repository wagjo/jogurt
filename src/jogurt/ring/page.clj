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

(ns jogurt.ring.page
  "Pages"
  (:api dunaj)
  (:require [hiccup.core :as hc]
            [hiccup.page :as hp]
            [hiccup.element :as he]
            [hiccup.form :as hf]
            [dunaj.format.edn :refer [pretty-edn]]
            [dunaj.format.clj :refer [pretty-clj]]
            [dunaj.time :refer [date-instant-factory]]
            [dunaj.uuid :as du]
            [jogurt.auth :refer [sign-in]]
            [jogurt.store :as js]
            [jogurt.render :refer [purge-cache! render-post]]
            [dunaj.format.asciidoc :as dfa]
            [dunaj.string :as ds]
            [ring.util.response :as rur]
            [dunaj.state.var :refer [reset-root!]]))

(defn sign-out
  "Returns sign out hiccup div"
  [cfg pid]
  (seq [(when pid
          [:div.jghnew (he/link-to (->str "/edit/?post-id=" pid)
                                   "Edit current post")])
        [:div.jghnew (he/link-to "/new" "Write new post")]
        [:div.jgsob (he/link-to "/signout" [:span "Sign out"])]]))

(defn fetch-posts
  "Returns seq of n posts from store, sorted by time descending."
  [store n]
  (seq (take n (js/sorted store :posts nil nil :date))))

(defn get-post
  [store pid]
  (js/row store :posts pid))

(defn author-name
  "Returns author name by hers id."
  [store id]
  (:name (js/row store :users id)))

(def date-format (java.text.SimpleDateFormat. "yyyy-MM-dd"))

(defn post-date
  [timestamp]
  (.format date-format (instant date-instant-factory timestamp)))

(defn page-template
  ([cfg auth store request content]
   (page-template cfg auth store request content nil))
  ([cfg auth store request content pid]
   (let [pretty-clj (assoc pretty-clj :pretty-item-limit 0)
          head [:head 
                (hp/include-css "/static/dd.css"
                                "//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css"
                                "/static/coderay-asciidoctor.css"
                                "/static/style.css")
                [:title "Jogurt"]]
          header [:div#jghead
                  [:div (he/link-to "/" (he/image "/static/logo.png"))]
                  [:div (he/link-to "/" [:span.jghtitle "Jogurt blog"])]
                  [:div#jgsignup 
                   (if (get-in request [:session :user-id]) 
                     (sign-out cfg pid)
                     (sign-in auth))]]
          content [:div#jgcontent content]
          footer [:div#jgfoot
                  [:p "Copyright 2015, Jozef Wagner"]
                  #_[:div
                     [:p "Request is"]
                     [:pre (str (print-one pretty-clj request))]]
                  #_[:div
                     [:p "Config is"]
                     [:pre (str (print-one pretty-clj cfg))]]]
          body [:body [:div#jgpage header content footer]]]
      (hp/html5 head body))))

(defn post-page
  "Returns index page in hiccup syntax"
  [cfg auth store request]
  (let [id-attr (js/id-attr store)
        pid (:pid (:params request))
        content [[:p.desc (he/link-to "/" "Back to home")]
                 (let [post (get-post store pid)]
                   [:div.jgpost
                    [:h2.jgtitle
                     (he/link-to
                      (->str "/post/" (get post id-attr))
                      (->str (:title post)))]
                    [:p.jgbody (render-post
                                (get post id-attr)
                                (:body post))]
                    [:p.jgmeta
                     (->str "Created at "
                            (post-date (:date post))
                            " by "
                            (author-name store(:author post)))]])]]
    (page-template cfg auth store request (seq content) pid)))

(defn new-page
  "Returns index page in hiccup syntax"
  [cfg auth store request]
  (let [id-attr (js/id-attr store)
        content
        [[:p.desc (he/link-to "/" "Back to home")]
         [:div.jgpost
          [:h2 "Create new post"]
          (hf/form-to 
           [:post "/submitpost"]
           [:div (hf/label :ptitle "Title:")]
           [:div (hf/text-field :ptitle "Enter title here")]
           [:div (hf/label :pbody 
                           "Body: (asciidoc syntax is supported)")]
           [:div (hf/text-area :pbody "Enter body here")]
           [:div (hf/submit-button {:id "pbut"} "send")])]]]
    (page-template cfg auth store request (seq content))))

(defn index-page
  "Returns index page in hiccup syntax"
  [cfg auth store request]
  (let [id-attr (js/id-attr store)
        content [[:p.desc "Latest posts:"]
                 (for [post (fetch-posts store 20)]
                   [:div.jgpost
                    [:h2.jgtitle
                     (he/link-to
                      (->str "/post/" (get post id-attr))
                      (->str (:title post)))]
                    [:p.jgbody (render-post
                                (get post id-attr)
                                (:body post))]
                    [:p.jgmeta
                     (->str "Created at "
                            (post-date (:date post))
                            " by "
                            (author-name store(:author post)))]])]]
    (page-template cfg auth store request (seq content))))

(defn new-post
  "Returns index page in hiccup syntax"
  [cfg auth store request]
  (let [id-attr (js/id-attr store)
        ptitle (:ptitle (:params request))
        pbody (:pbody (:params request))
        uid (:user-id (:session request))
        uemail (:user-email (:session request))
        user (js/row store :users uid)
        uname (str (slice uemail 0 (ds/index-of uemail \@)))
        pid (canonical (du/random))
        ret (if (nil? uid)
              (rur/redirect "/")
              (do
                (when (empty? user)
                  (js/put! store :users uid {:email uemail :name uname}))
                (js/put! store :posts pid
                         {:title ptitle :body pbody
                          :author uid
                          :date (canonical
                                 (instant date-instant-factory (now)))})
                (rur/redirect (->str "/post/" pid))))]
    (sleep 500)
    (pp! uid uemail)
    ret))
