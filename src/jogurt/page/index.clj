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

(ns jogurt.page.index
  "Index page"
  (:api dunaj)
  (:require [hiccup.core :as hc]
            [hiccup.page :as hp]
            [hiccup.element :as he]
            [dunaj.format.edn :refer [pretty-edn]]
            [dunaj.format.clj :refer [pretty-clj]]
            [jogurt.auth :refer [sign-in]]))

(defn sign-out
  "Returns sign out hiccup div"
  [cfg]
  [:div [:div.jgsob (he/link-to "signout" [:span "Sign out"])]])

(defn index-page
  "Returns index page in hiccup syntax"
  [cfg auth request]
  (let [pretty-clj (assoc pretty-clj :pretty-item-limit 0)
        head [:head 
              (hp/include-css "static/style.css")
              [:title "Jogurt"]]
        header [:div#jghead
                [:div (he/link-to "/" (he/image "static/logo.png"))]
                [:div (he/link-to "/" [:span.jgtitle "Jogurt blog"])]
                [:div#jgsignup 
                 (if (get-in request [:session :user-id]) 
                   (sign-out cfg)
                   (sign-in auth cfg))]]
        content [:div#jgcontent
                 [:p "Foo bar"]]
        footer [:div#jgfoot
                [:div
                 [:p "Request is"]
                 [:pre (str (print-one pretty-clj request))]]
                [:div
                 [:p "Config is"]
                 [:pre (str (print-one pretty-clj cfg))]]]
        body [:body [:div#jgpage header content footer]]]
    (hp/html5 head body)))
