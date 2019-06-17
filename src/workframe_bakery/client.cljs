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

(defn price
  [id]
  (let [{price :price
         {bulk-price :totalPrice bulk-quantity :amount} :bulkPricing} (get-item-by-id @treats id)
        {:keys [quantity] :or {quantity 0}} (get-item-by-id @cart id)]
    (if bulk-quantity                           ;preventing division by nil
      (+ (* (-> (/ quantity bulk-quantity) int) ;price = (quantity/bulk-quantity) bulk-price + (quantity % bulk-quantity) price
            bulk-price)
         (* (mod quantity bulk-quantity)
            price))
      (* quantity price))))

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
