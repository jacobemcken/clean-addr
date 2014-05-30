(ns clean-addr.core
  (:require [clojure.string :as str]
            [clojure-csv.core :refer [parse-csv write-csv]])
  (:gen-class))

(defn next-empty-key
  "Find next empty key in m(ap) using the order provided by k(ey)s"
  [m & ks]
  (some #(when (nil? (get m %)) %) ks))

(defn get-bucket
  "Identify which key to save the data into"
  [next-empty-key data]
  (cond
   (= next-empty-key :no) :no
   (and (= next-empty-key :floor) (re-matches #"[a-z]" data)) :no
   (and (some #{next-empty-key} [:no :floor]) (re-matches #"kl|st|\d+" data)) :floor
   (and (some #{next-empty-key} [:floor :door]) (re-matches #"\d+|th|tv|mf" data)) :door))

(defn arrange
  "Assoc data into addr(ess) map"
  [addr data]
  (let [bucket (get-bucket (next-empty-key addr :no :floor :door) data)]
    (if (nil? bucket)
      addr
      (update-in addr [bucket] #(str % data)))))

(defn addr->map
  [addr]
  (let [[_ street other] (re-find (re-matcher #"^([^\d]+)(.*)$" addr))]
    (when-not (empty? street)
      (reduce arrange
              {:street (if (empty? street) "" (clojure.string/trim street))}
              (map #(-> %
                        clojure.string/lower-case
                        clojure.string/trim)
                   (clojure.string/split other #"\s+"))))))

(defn append-addr-cols
  [col-no row]
  (let [addr-string (-> (nth row col-no)
                        (str/replace #",|\." " ")
                        (str/replace #"(\d+)(\D+)" "$1 $2"))
        addr-map (addr->map addr-string)]
    (concat row (map #(get addr-map % "") [:street :no :floor :door]))))

(defn -main
  "Split addresse found in specified column number in file-name and 
append the new columns to the end of the csv file."
  ([file-in col-no]
     (-main file-in col-no nil))
  ([file-in col-no file-out]
     (let [[headers & rows] (parse-csv (slurp file-in :encoding "ISO-8859-1") :delimiter \;)]
       (spit (or file-out (clojure.string/replace file-in #".csv$" "_split.csv"))
             (write-csv (cons (concat headers ["Vej" "Nr." "Etage" "Dør"])
                              (reduce #(conj %1 (append-addr-cols (Integer. col-no) %2)) [] rows)))))))
