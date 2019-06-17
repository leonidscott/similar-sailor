(ns workframe-bakery.json-reader
  (:require
   [clojure.java.io :as io]
   [clojure.data.json :as json]))

(defn- slurp-json
  [file-name]
  (-> file-name
      io/resource
      slurp))

(defn- json-edn
  [file-string]
  (json/read-str file-string
                 :key-fn keyword))

(defn get-edn
  [file-name]
  (json-edn (slurp-json file-name)))

(defn unmarshal-treats
  [treats]
  (-> treats
      first
      (nth 1)))
