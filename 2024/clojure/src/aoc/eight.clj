(ns aoc.eight
  (:require [clojure.java.io :as io]
            [aoc.input :refer [make-grid]]))

(defn- combos [xs]
  (reduce into [] (for [xs' (take-while seq (iterate rest xs))]
                    (let [head (first xs')]
                      (for [x (rest xs')]
                        [head x])))))

(defn- make-frequency-map [grid]
  (reduce (fn [m [coord c]]
            (update m c (fnil conj []) coord))
          {}
          (for [[i row] (map-indexed vector grid)
                [j c] (map-indexed vector row)
                :when (not= \. c)]
            [[i j] c])))

(defn- in-bounds?
  [length width [i j]]
  (and (< -1 i length) (< -1 j width)))

(defn- antinodes-part-1
  [length width [a b]]
  (let [d (map - b a)]
    (filter #(in-bounds? length width %) [(map - a d) (map + b d)])))

(defn- extrapolate
  [length width f start]
  (take-while #(in-bounds? length width %) (iterate f start)))

(defn antinodes-part-2
  [length width [a b]]
  (let [d (map - b a)]
    (concat (extrapolate length width #(map - % d) a)
            (extrapolate length width #(map + % d) b))))

(defn parts
  [grid f]
  (let [length (count grid)
        width (count (first grid))]
    (->> (vals (make-frequency-map grid))
         (mapcat combos)
         set
         (mapcat #(f length width %))
         set
         count)))

(comment
  (def grid (make-grid (slurp (io/resource "eight.txt"))))
  (parts grid antinodes-part-1)
  (parts grid antinodes-part-2))
