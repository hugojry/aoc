(ns aoc.2023.two
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(def input (->> (string/split (slurp (io/resource "two.txt")) #"\n")
                (map (fn [s]
                       (->> (string/split (second (string/split s #": ")) #"; ")
                            (map (fn [set]
                                   (map #(update (string/split % #" ") 0 read-string)
                                        (string/split set #", ")))))))))

(defn rgb [game]
  (reduce (fn [maxes set]
            (reduce (fn [m [n color]] (update m color max n)) maxes set))
          {"red" 0, "green" 0, "blue" 0}
          game))

(defn part-one [games]
  (->> games
       (map-indexed (fn [i game]
                      (let [maxes (rgb game)]
                        (if (and (<= (maxes "red") 12)
                                 (<= (maxes "green") 13)
                                 (<= (maxes "blue") 14))
                          (inc i) 0))))
       (reduce +)))

(defn part-two [input]
  (->> input
       (map (fn [game]
              (let [maxes (rgb game)]
                (reduce * (vals maxes)))))
       (reduce +)))

(comment

  (part-one input)
  (part-two input))
