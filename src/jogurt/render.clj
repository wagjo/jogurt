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

(ns jogurt.render
  "Post renderer"
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
            [dunaj.format.asciidoc :as dfa]
            [dunaj.string :as ds]
            [ring.util.response :as rur]
            [dunaj.state.var :refer [reset-root!]]))

(def default-header
  {:type :header
   :under "_"
   :equal "="
   :title "placeholder"
   :icons :font
   :linkcss true
   :stylesheet "/dd.css"
   :coderay-linenums-mode "table"
   :coderay-css "class"
   :source-highlighter "coderay"})

(defn parse-adoc
  [content]
  (let [x (concat [(assoc default-header
                          :sectlinks false)]
                  [content])
        ad (str (print dfa/asciidoc (vec x)))]
    #_(with-scope (spit! "temp.ad" ad [:create :truncate]))
    (str (print (assoc dfa/convert :embedded? true) ad))))

(def ad-cache (atom {}))

(defn purge-cache!
  ([]
   (reset! ad-cache nil))
  ([pid]
   (alter! ad-cache dissoc pid)))

(defn render-post
  [id content]
  (loop [pc (get @ad-cache id)]
    (if pc
      pc
      (do (alter! ad-cache assoc id (parse-adoc content))
          (recur (get @ad-cache id))))))
