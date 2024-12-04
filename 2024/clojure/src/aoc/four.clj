(ns aoc.four
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn letter-map [input]
  (into {} (for [[i row] (map-indexed vector input)
                 [j c] (map-indexed vector row)
                 :when (#{\X \M \A \S} c)]
             [[i j] c])))

(defn part-1 [letter-map]
  (let [fs (for [[dx dy] [[0 1] [0 -1] [1 0] [-1 0] [-1 1] [1 1] [-1 -1] [1 -1]]]
             (fn [start]
               (take 4 (iterate (fn [[x y]] [(+ x dx) (+ y dy)]) start))))]
    (->> letter-map
         (filter #(= \X (val %)))
         (map key)
         (mapcat (fn [coord] (map #(apply str (map letter-map (% coord))) fs)))
         (filter #(= "XMAS" %))
         count)))

(defn part-2 [letter-map]
  (let [f (fn [[x y]]
            [[(dec x) (dec y)]
             [(dec x) (inc y)]
             [(inc x) (inc y)]
             [(inc x) (dec y)]])]
    (->> letter-map
         (filter #(= \A (val %)))
         (map #(apply str (map letter-map (f (key %)))))
         (filter #{"MMSS" "SSMM" "MSSM" "SMMS"})
         count)))

(comment
  (def input (letter-map (map seq (string/split (slurp (io/resource "four.txt")) #"\n")))) 

  (part-1 input)
  (part-2 input)
  )
