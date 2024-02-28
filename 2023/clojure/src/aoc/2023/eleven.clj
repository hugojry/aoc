(ns aoc.2023.eleven
  (:require [clojure.java.io :as io]
            [clojure.math.combinatorics :refer [combinations]]))

(let [lines (line-seq (io/reader (io/resource "eleven.txt")))]
  (def lines (into [] (map vec) lines))
  (def width (count (first lines)))
  (def height (count lines)))

(defn no-galaxies? [cs]
  (every? #(= \. %) cs))

(defn empty-rows [lines]
  (keep (fn [[i line]]
          (when (no-galaxies? line) i))
        (map-indexed list lines)))

(defn transpose [lines]
  (let [height (count lines)]
    (for [i (range (count (first lines)))]
      (for [j (range height)]
        (nth (nth lines j) i)))))

(defn insert
  [xs x i]
  (concat (take i xs) (list x) (drop i xs)))

(defn extra-count
  [x n extras]
  (+ x (* n (count (filter #(< % x) extras)))))

(defn solve
  [lines n]
  (let [rows (empty-rows lines)
        cols (empty-rows (transpose lines))
        galaxies (->> (for [i (range (count lines))
                            j (range (count (first lines)))]
                        [[i j] (get-in lines [i j])])
                      (keep (fn [[coord c]] (when (= \# c) coord)))
                      (map (fn [[x y]]
                             [(extra-count x n rows) (extra-count y n cols)])))]
    (->> (combinations galaxies 2)
         (map (fn [[[a b] [x y]]]
                (+ (abs (- a x)) (abs (- b y)))))
         (reduce +))))

(comment

  (solve lines 1)
  (solve lines (dec 1000000)))
