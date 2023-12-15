(ns aoc.2023.four
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as string]
            [clojure.math.numeric-tower :refer [expt]]))

(def input (map (fn [line]
                  (let [[card-id card] (string/split line #": +")
                        [winning-numbers numbers] (string/split card #" \| +")]
                    {:card (read-string (second (string/split card-id #" +")))
                     :winning-numbers (map read-string (string/split winning-numbers #" +"))
                     :numbers (map read-string (string/split numbers #" +"))}))
                (string/split (slurp (io/resource "four.txt")) #"\n")))

(defn number-of-winning-numbers [game]
  (count (set/intersection (set (:winning-numbers game)) (set (:numbers game)))))

(defn part-one-score [games]
  (->> games
       (map (fn [game]
              (let [n (number-of-winning-numbers game)]
                (if (zero? n) 0 (expt 2 (dec n))))))
       (reduce +)))

(defn part-two-score [games]
  (let [card-counts (mapv (fn [game] [(number-of-winning-numbers game) 1]) games)]
    (->> (reduce (fn [card-counts i]
                   (let [n (get-in card-counts [i 1])]
                     (reduce (fn [card-counts j]
                               (update-in card-counts [(+ i j 1) 1] + n))
                             card-counts
                             (range (get-in card-counts [i 0])))))
                 card-counts
                 (range (count card-counts)))
         (map second)
         (reduce +))))

(comment

  (part-one-score input)
  (part-two-score input))
