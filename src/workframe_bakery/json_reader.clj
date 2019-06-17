(ns workframe-bakery.json-reader
  (:require
   [clojure.java.io :as io]))

(defn slurp-json
  [file-name]
  (-> file-name
      io/resource
      slurp))
