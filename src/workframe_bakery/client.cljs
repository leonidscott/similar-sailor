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
  [id quantity]
  (let [index (->> (get-item-by-id @cart id)
                   (.indexOf @cart))]
    (if (> index -1)
      (swap! cart update-in [index :quantity] + quantity)
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

(defn button
  [value on-click-fn & args]
  [:input {:type "button"
           :value value
           :on-click #(on-click-fn args)}])

(defn treat
  [{:keys [id name price] {bulk-price :totalPrice bulk-quantity :amount} :bulkPricing}]
  (if bulk-price
    [:li name
     ", Individual Price: " (button (str "$" price) #(add-to-cart id 1))
     ", Bulk Price: (" bulk-quantity ") " (button (str "$" bulk-price) #(add-to-cart id bulk-quantity))]
    [:li name
     ", Individual Price: " (button (str "$" price) #(add-to-cart id 1))]))

(defn treat-list []
  [:ul
   (for [item @treats]
     ^{:key (:id item)} (treat item))])

(defn cart-item
  [{:keys [id quantity]}]
  [:li (-> (get-item-by-id @treats id) :name)
   ": Quantity: " quantity
   ", Price: $" (price id)])

(defn display-cart []
  [:ul
   (for [item @cart]
     ^{:key (:id item)} (cart-item item))])

(defn total-price []
  [:h3 "Total: $"
       (->> (map #(price (:id %)) @cart)
            (reduce +))])

(defn checkout-clear-buttons []
  [:li
   (button "Checkout" #(reset! cart []))
   (button "Clear" #(reset! cart []))])

(defn main-app-component
  []
  [:div
   [:h1 "Bakery"]
   (treat-list)
   [:h2 "Cart"]
   (display-cart)
   (total-price)
   (checkout-clear-buttons)])

(defn reload
  []
  (get-treats!)
  (r/render-component [main-app-component] (.getElementById js/document "app")))

(defn ^:export run
  []
  (enable-console-print!)
  (get-treats!)                                     ;
  (reload))
