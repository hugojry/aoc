(ns aoc.one
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(let [lines (line-seq (io/reader (io/resource "one.txt")))
      pairs (map #(map (fn [x] (Integer/parseInt x)) (string/split % #"   ")) lines)
      first-list (sort (map first pairs))
      second-list (sort (map second pairs))
      freqs (frequencies second-list)]
  (reduce + (map (comp abs -) first-list second-list))
  (reduce + (map #(* % (get freqs % 0)) first-list)))
