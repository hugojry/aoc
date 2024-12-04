(ns aoc.two
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn part-1-pred [report]
  (let [differences (map #(apply - %) (partition 2 1 report))]
    (and (or (every? neg? differences)
             (every? pos? differences))
         (< (apply max (map abs differences)) 4))))

(defn remove-at
  [v i]
  (into (subvec v 0 i) (subvec v (inc i))))

(defn part-2-pred [report]
  (or (part-1-pred report)
      (let [v (vec report)]
        (some part-1-pred (for [i (range (count v))]
                            (remove-at v i))))))

(comment

  (def reports (->> (io/resource "two.txt")
                    io/reader
                    line-seq
                    (map #(map parse-long (string/split % #" ")))))

  (count (filter part-1-pred reports))
  (count (filter part-2-pred reports)))
