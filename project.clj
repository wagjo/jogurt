(defproject org.wagjo/jogurt "0.1.0-SNAPSHOT"
  :description "Yet another blog"
  :url "https://github.com/wagjo/jogurt"
  :scm {:name "git"
        :url "https://github.com/wagjo/jogurt"}
  :source-paths ["src"]
  :resource-paths ["res"]
  :java-source-paths ["src/jvm"]
  :main ^:skip-aot jogurt.core
;  :global-vars {*warn-on-reflection* true}
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}
  :profiles {:dev {}}
  :dependencies
  [[org.dunaj/dunaj "0.4.1-SNAPSHOT"]
   [org.asciidoctor/asciidoctorj "1.5.2"]
   [org.clojure/tools.nrepl "0.2.10"]
   [cider/cider-nrepl "0.8.2"]
   [javax.servlet/servlet-api "2.5"]
   [com.cemerick/rummage "1.0.1" :exclusions [org.clojure/clojure]]
   [ring/ring-codec "1.0.0" :exclusions [org.clojure/clojure]]
   [clj-jwt "0.0.13" :exclusions [org.clojure/clojure]]
   [environ "1.0.0" :exclusions [org.clojure/clojure]]
   [http-kit "2.1.18" :exclusions [org.clojure/clojure]]
   [hiccup "1.0.5" :exclusions [org.clojure/clojure]]
   [compojure "1.3.4" :exclusions [org.clojure/clojure]]
   [org.clojure/tools.reader "0.9.2"]]
  :plugins [[lein-environ "1.0.0"]]
  :jvm-opts ^:replace ["-Xms1G" "-Xmx1G"
                       "-XX:-UseConcMarkSweepGC" "-server"])
