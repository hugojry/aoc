(ns aoc.fourteen
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn new-position
  [length width position velocity iterations]
  (let [[x y] (map + (map #(* iterations %) velocity) position)]
    (list (mod x width) (mod y length))))

(defn safety-factory
  [length width position-frequencies]
  (let [mid-length (quot length 2)
        mid-width (quot width 2)]
    (->> position-frequencies
         (reduce (fn [quadrant-counts [[y x] n]]
                   (if-some [idx (cond
                                   (and (< x mid-length) (< y mid-width)) 0
                                   (and (< x mid-length) (> y mid-width)) 1
                                   (and (> x mid-length) (< y mid-width)) 2
                                   (and (> x mid-length) (> y mid-width)) 3)]
                     (update quadrant-counts idx + n)
                     quadrant-counts))
                 [0 0 0 0])
         (reduce *))))

(defn part-1
  [length width robots]
  (let [positions (map (fn [{:keys [velocity position]}]
                         (new-position length width position velocity 100))
                       robots)
        freqs (frequencies positions)]
    (safety-factory length width freqs)))

(defn visualize
  [length width positions]
  (doseq [i (range length)
          j (range width)]
    (if (positions [j i])
      (print "o")
      (print " "))
    (when (= j (dec width))
      (println)))
  (println))

(defn iterate-position
  [position length width velocity]
  (new-position length width position velocity 1))

(defn part-2
  [robots length width]
  (let [iterations (iterate
                     (fn [robots]
                       (for [robot robots]
                         (update robot :position iterate-position length width (:velocity robot))))
                     robots)]
    (doseq [robots (map-indexed vector (take 100000 iterations))]
      (visualize length width (set (map :position robots))))))

;; Part two was achieved by redirecting the output of the part-2 function to a
;; file then grepping for a long sequence of o's, which only occurs in the
;; christmas tree picture. Then take the line number for the match and divide
;; it by 104 to get the iteration number.

(comment

  (def robots (map (fn [line]
                     (let [parts (string/split line #" ")
                           [p v] (map #(map parse-long (string/split (subs % 2) #",")) parts)]
                       {:position p,
                        :velocity v}))
                   (string/split (slurp (io/resource "fourteen.txt")) #"\n")))

  (part-1 103 101 robots))
