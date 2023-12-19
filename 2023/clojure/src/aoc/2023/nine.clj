(ns aoc.2023.nine
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(def input
  (map #(map read-string (string/split % #" "))
       (string/split (slurp (io/resource "nine.txt")) #"\n")))

(defn differences [xs]
  (map - (rest xs) xs))

(defn successive-differences [xs]
  (take-while #(not (every? zero? %)) (iterate differences xs)))

(defn extrapolate [xs]
  (reduce + (map last (reverse (successive-differences xs)))))

(defn backxtrapolate [xs]
  (reduce #(- %2 %1) (map first (reverse (successive-differences xs)))))

(comment

  (reduce + (map extrapolate input))
  (reduce + (map backxtrapolate input)))
