(defproject org.wagjo/jogurt "0.1.0-SNAPSHOT"
  :description "Yet another blog"
  :url "https://github.com/wagjo/jogurt"
  :scm {:name "git"
        :url "https://github.com/wagjo/jogurt"}
  :source-paths ["src"]
  :resource-paths ["res"]
  :java-source-paths ["src/jvm"]
  :main ^:skip-aot jogurt.core
  :aot [jogurt.servlet.vaadinui jogurt.servlet.myvaadinservlet]
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
   [org.immutant/web "2.0.1" :exclusions [org.clojure/clojure]]
   [com.cemerick/rummage "1.0.1" :exclusions [org.clojure/clojure]]
   [ring/ring-codec "1.0.0" :exclusions [org.clojure/clojure]]
   [clj-jwt "0.0.13" :exclusions [org.clojure/clojure]]
   [environ "1.0.0" :exclusions [org.clojure/clojure]]
   [hiccup "1.0.5" :exclusions [org.clojure/clojure]]
   [compojure "1.3.4" :exclusions [org.clojure/clojure]]
   [ring/ring-servlet "1.3.2" :exclusions [org.clojure/clojure]]
   [com.vaadin/vaadin-server "7.4.6"]
   [com.vaadin/vaadin-themes "7.4.6"]
   [com.vaadin/vaadin-widgets "7.4.6"]
   [com.vaadin/vaadin-client-compiled "7.4.6"]
   [com.vaadin/vaadin-client "7.4.6"]
   [org.clojure/tools.reader "0.9.2"]]
  :plugins [[lein-environ "1.0.0"]]
  :jvm-opts ^:replace ["-Xms1G" "-Xmx1G"
                       "-XX:-UseConcMarkSweepGC" "-server"])
