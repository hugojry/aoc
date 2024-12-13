(ns aoc.twelve
  (:require [clojure.java.io :as io]
            [aoc.input :refer [make-grid]]))

(defn adjacent [[i j]]
  [[(dec i) j] [(inc i) j] [i (dec j)] [i (inc j)]])

(defn find-region
  [garden-map start]
  (let [plant (garden-map start)]
    (loop [garden-map (dissoc garden-map start)
           region [start]
           positions [start]]
      (if-some [pos (peek positions)]
        (let [neighbors (filter #(= plant (garden-map %)) (adjacent pos))]
          (recur (apply dissoc garden-map neighbors)
                 (into region neighbors)
                 (into (pop positions) neighbors)))
        (assoc garden-map start region)))))

(defn make-regions [garden-map]
  (map set (vals (reduce #(if (contains? %2 %1) (find-region %2 %1) %2)
                         garden-map
                         (keys garden-map)))))

(defn perimeter [region]
  (reduce + (map #(count (remove region (adjacent %))) region)))

(defn outer-corners
  [region [i j]]
  (->> [[[(dec i) j] [i (dec j)]]
        [[(dec i) j] [i (inc j)]]
        [[(inc i) j] [i (dec j)]]
        [[(inc i) j] [i (inc j)]]]
       (filter (fn [[a b]] (and (not (region a)) (not (region b)))))
       count))

(defn inner-corners
  [region [i j]]
  (->> [[[(dec i) j] [i (dec j)] [(dec i) (dec j)]]
        [[(dec i) j] [i (inc j)] [(dec i) (inc j)]]
        [[(inc i) j] [i (dec j)] [(inc i) (dec j)]]
        [[(inc i) j] [i (inc j)] [(inc i) (inc j)]]]
       (filter (fn [[a b c]] (and (region a) (region b) (not (region c)))))
       count))

(defn corners [region]
  (reduce + (map #(+ (outer-corners region %) (inner-corners region %)) region)))

(comment
  (def garden-map (into {} (for [[i row] (map-indexed vector (make-grid (io/resource "twelve.txt")))
                                 [j c] (map-indexed vector row)]
                              [[i j] c])))
  (def regions (make-regions garden-map))
  (reduce + (map #(* (count %) (perimeter %)) regions))
  (reduce + (map #(* (count %) (corners %)) regions)))
