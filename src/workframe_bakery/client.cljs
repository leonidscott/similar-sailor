(ns workframe-bakery.client
  (:require
   [reagent.core :as r]
   [ajax.core :as http]))

(def treats (r/atom []))

(def cart (r/atom []))

(defn get-treats! []
  (http/GET "/treats"
            {:handler (fn [resp] (reset! treats (:treats resp)))
             :error-handler (fn [e] (.warn js/console "treats error" e))
             :response-format :json, :keywords? true}))

(defn get-item-by-id
  "returns first map with matching id when datastucture is:
  [{:id ~ ...} {:id~ ....} ...]
  ASSUMPTION FOR CORRECT BEHAVOIR: id's in maps are unique"
  [vec-of-maps id]
  (-> (filter #(= id (:id %)) vec-of-maps)
      first))

(defn add-to-cart
  [id]
  (let [index (->> (get-item-by-id @cart id)
                   (.indexOf @cart))]
    (if (> index -1)
      (swap! cart update-in [index :quantity] inc)
      (swap! cart conj {:id id :quantity 1}))))

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
