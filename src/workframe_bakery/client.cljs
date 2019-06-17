(ns workframe-bakery.client
  (:require
   [reagent.core :as r]
   [ajax.core :as http]))

(def treats (r/atom []))

(defn get-treats! []
  (http/GET "/treats"
            {:handler (fn [resp] (reset! treats (:treats resp)))
             :error-handler (fn [e] (.warn js/console "treats error" e))
             :response-format :json, :keywords? true}))

(defn main-app-component
  []
  [:h1 "Hello, world!"])

(defn reload
  []
  (r/render-component [main-app-component] (.getElementById js/document "app")))

(defn ^:export run
  []
  (enable-console-print!)
  (get-treats!)                                     ;
  (reload))
