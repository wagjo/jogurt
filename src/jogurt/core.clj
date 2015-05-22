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

(ns jogurt.core
  (:require [clojure.tools.nrepl.server :as nrepls]
            [cider.nrepl :as nreplc]
            [jogurt.system :as jsys]))

(defn -main [& args]
  (nrepls/start-server :port 7888 :handler nreplc/cider-nrepl-handler)
  (jsys/launch!)
  (println "Server started. Running indefinitely..."))
