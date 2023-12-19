(ns aoc.2023.seven
  (:require [clojure.java.io :as io]
            [clojure.math.combinatorics :as combo]
            [clojure.string :as string]))

(def input (map #(-> (string/split % #" ")
                     (update 0 vec)
                     (update 1 read-string))
                (string/split (slurp (io/resource "seven.txt")) #"\n")))

(defn classify [hand]
  (let [freqs (frequencies hand)]
    (case (apply max (vals freqs))
      1 0
      5 6
      4 5
      3 (if (= 2 (count freqs)) 4 3)
      2 (if (= 3 (count freqs)) 2 1))))

(defn card-strength-1 [char]
  (case char
    \2 0 \3 1 \4 2 \5 3 \6 4 \7 5 \8 6 \9 7 \T 8 \J 9 \Q 10 \K 11 \A 12))

(defn comp-1
  [a b]
  (let [diff (- (classify a) (classify b))]
    (if (zero? diff)
      (loop [c a, d b]
        (let [diff (- (card-strength-1 (first c))
                      (card-strength-1 (first d)))]
          (if (zero? diff)
            (recur (rest c) (rest d))
            diff)))
      diff)))

(def hand-chars [\2 \3 \4 \5 \6 \7 \8 \9 \T \J \Q \K \A])

;; They aren't combinations but I don't know what they right name is
(defn combinations [n]
  (apply combo/cartesian-product (repeat n hand-chars)))

;; Again, not permutations, but it sounds right
(defn hand-permutations [hand]
  (let [positions (->> (map-indexed vector hand)
                       (reduce (fn [v [i c]]
                                 (if (= \J c)
                                   (conj v i)
                                   v))
                               []))]
    (for [combo (combinations (count positions))]
      (reduce (fn [hand [i c]]
                (assoc hand i c))
              hand
              (map vector positions combo)))))

(defn classify-joker [hand]
  (let [n (reduce (fn [n c] (if (= \J c) (inc n) n)) 0 hand)]
    (case n
      5 6
      4 6
      (apply max (map classify (hand-permutations hand))))))

(defn card-strength-2 [char]
  (case char
    \2 2 \3 3 \4 4 \5 5 \6 6 \7 7 \8 8 \9 9 \T 10 \J 1 \Q 11 \K 12 \A 13))

(defn comp-2
  [a b]
  (let [diff (- (classify-joker a) (classify-joker b))]
    (if (zero? diff)
      (loop [c a, d b]
        (let [diff (- (card-strength-2 (first c))
                      (card-strength-2 (first d)))]
          (if (zero? diff)
            (recur (rest c) (rest d))
            diff)))
      diff)))

(comment

  (reduce + (map * (rest (range)) (map second (sort-by first comp-2 input)))))
