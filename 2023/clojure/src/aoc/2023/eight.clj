(ns aoc.2023.eight
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.math.numeric-tower :refer [lcm]]))

(let [[path _ & graph-lines] (line-seq (io/reader (io/resource "eight.txt")))]
  (def path (map (fn [c] (case c \R 1 0)) (vec path)))
  (def graph (->> graph-lines
                  (map (fn [line]
                         (let [[name pair] (string/split line #" = ")]
                           [name (string/split
                                  (string/replace pair #"[()]" "")
                                  #", ")])))
                  (into {}))))

(defn cycle-length
  [start path graph]
  (->> (cycle path)
       (reductions #(get-in graph [%1 %2]) start)
       (take-nth (count path))
       (map-indexed vector)
       (filter #(= \Z (.charAt (second %) 2)))
       (take 2)
       (map first)
       reverse
       (apply -)))

(comment

  (reduce (fn [[n current] i]
            (let [next (get-in graph [current i])]
              (if (= "ZZZ" next)
                (reduced n)
                [(inc n) next])))
          [1 "AAA"]
          (cycle path))

  (->> (keys graph)
       (filter #(= \A (.charAt % 2)))
       (map (fn [s] (* (count path) (cycle-length s path graph))))
       (reduce lcm)))
