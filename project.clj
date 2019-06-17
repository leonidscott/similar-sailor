(defproject workframe-bakery "0.1.0-SNAPSHOT"

  :dependencies [[cljsjs/bootstrap "3.3.6-1"]
                 [compojure "1.6.0"]
                 [environ "1.1.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.908"]
                 [org.immutant/web "2.1.9"]
                 [org.webjars/bootstrap "3.3.6"]
                 [reagent "0.7.0"]
                 [ring "1.6.2"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-defaults "0.3.1" :exclusions [javax.servlet/servlet-api]]
                 [clojure-io "1.0.0-SNAPSHOT"]
                 [org.clojure/data.json "0.2.6"]
                 [cljs-ajax "0.3.14"]]

  :plugins       [[lein-cljsbuild "1.1.7"]
                  [lein-environ "1.1.0"]
                  [lein-figwheel "0.5.13"]]

  :main          workframe-bakery.server

  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :app :compiler :output-dir]
                                    [:cljsbuild :builds :app :compiler :output-to]]

  :figwheel      {:css-dirs ["resources/public/css"]}

  :cljsbuild     {:builds
                  {:app
                   {:source-paths ["src"]
                    :figwheel     {:on-jsload workframe-bakery.client/reload}
                    :compiler     {:main          workframe-bakery.client
                                   :output-to     "resources/public/cljs/app.js"
                                   :output-dir    "resources/public/cljs/target"
                                   :asset-path    "cljs/target"
                                   :source-map    true
                                   :optimizations :none
                                   :pretty-print  true}}}}

  :profiles      {:dev     {:env          {:dev? true}
                            :source-paths ["env/dev/src"]
                            :dependencies [[figwheel-sidecar "0.5.13"]
                                           [org.clojure/tools.nrepl "0.2.13"]
                                           [com.cemerick/piggieback "0.2.2"]]
                            :figwheel     {:nrepl-port       7000
                                           :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                            :cljsbuild    {:builds {:app
                                                    {:source-paths ["src" "env/dev/src"]}}}}

                  :uberjar {:source-paths ["env/prod/src"]
                            :aot          :all
                            :hooks        [leiningen.cljsbuild]
                            :omit-source  true
                            :cljsbuild    {:jar    true
                                           :builds {:app
                                                    {:compiler ^:replace {:source-paths ["src" "env/prod/src"]
                                                                          :output-to     "resources/public/cljs/app.js"
                                                                          :optimizations :advanced
                                                                          :pretty-print  false}}}}}}

  :aliases       {"uberjar" ["do" ["clean"] ["uberjar"]]}

  )
