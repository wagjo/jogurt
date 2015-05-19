(defproject org.wagjo/jogurt "0.1.0-SNAPSHOT"
  :description "Yet another blog"
  :url "https://github.com/wagjo/jogurt"
  :scm {:name "git"
        :url "https://github.com/wagjo/jogurt"}
  :source-paths ["src"]
  :resource-paths ["res"]
  :java-source-paths ["src/jvm"]
  :main jogurt.core
;;  :global-vars {*warn-on-reflection* true}
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}
  :dependencies
  [[org.dunaj/dunaj "0.4.0"]
   [org.clojure/tools.nrepl "0.2.10"]
   [cider/cider-nrepl "0.8.2"]
   [http-kit "2.1.18"]]
  :jvm-opts ^:replace ["-Xms1G" "-Xmx1G"
                       "-XX:-UseConcMarkSweepGC" "-server"])
