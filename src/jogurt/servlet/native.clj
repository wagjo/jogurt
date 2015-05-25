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

(ns jogurt.servlet.native
  "Native servlet."
  (:api dunaj)
  (:import [javax.servlet Servlet ServletConfig ServletRequest
            ServletResponse])
  (:gen-class
   :implements [javax.servlet.Servlet]
   :name jogurt.servlet.Native))

(defn -init
  [^Servlet this ^ServletConfig config])

(defn -destroy
  [^Servlet this])

(defn -service
  [^Servlet this ^ServletRequest request ^ServletResponse response])
