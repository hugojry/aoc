(ns aoc.three
  (:require [clojure.java.io :as io]))

(defn part-1 [program]
  (->> program
       (re-seq #"mul\((\d+),(\d+)\)")
       (map (fn [[_ a b]] (* (parse-long a) (parse-long b))))
       (reduce +)))

(defn part-2 [program]
  (reduce
    (fn [[do-or-dont sum] tok]
      (case (first tok)
        "do()" [true sum]
        "don't()" [false sum]
        (let [[_ a b] tok]
          (if do-or-dont
            [do-or-dont (+ sum (* (parse-long a) (parse-long b)))]
            [do-or-dont sum]))))
    [true 0]
    (re-seq #"mul\((\d+),(\d+)\)|do\(\)|don't\(\)" program)))

(comment
  (def input (slurp (io/resource "three.txt")))

  (re-seq #"ab|bc|cd" "abcd")

  (part-1 input) 
  (part-2 input))
