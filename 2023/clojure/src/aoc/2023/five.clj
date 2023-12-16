(ns aoc.2023.five
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn map-fn [ranges]
  (fn [source-value]
    (or (some (fn [[dest-start source-start n]]
                (let [offset (- source-value source-start)]
                  (when (< -1 offset n)
                    (+ dest-start offset))))
              ranges)
        source-value)))

(defn parse-map [strings]
  (cons
   (map-fn (map #(map read-string (string/split % #" ")) (rest strings)))
   (string/split (first (string/split (first strings) #" ")) #"-to-")))

(let [[seeds & maps]
      (->> (string/split (slurp (io/resource "five.txt")) #"\n\n")
           (map #(string/split % #"\n")))]
  (def seeds (map read-string (string/split (second (string/split (first seeds) #": ")) #" ")))
  (def graph (reduce (fn [graph [f & path]]
                       (assoc-in graph path f))
                     {}
                     (map parse-map maps))))

(defn walk
  [name value graph]
  ;; Assume only 1 destination
  (let [[destination f] (first (get graph name))]
    (if (= "location" destination)
      (f value)
      (recur destination (f value) graph))))

(defn solve
  [graph seeds]
  (apply min (map #(walk "seed" % graph) seeds)))

(comment

  (solve graph seeds)
  (partition-all 2 seeds)
  (run! #(walk "seed" % graph) (range (first seeds) (+ (first seeds) (int 1e6)))))
