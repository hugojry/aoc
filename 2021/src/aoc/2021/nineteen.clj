(ns aoc.2021.nineteen
  (:require [clojure.java.io :as io]
            [clojure.math.combinatorics :as combo]
            [clojure.set :as s]
            [clojure.string :as string]))

(defn parse-input [reader]
  (->> reader
       line-seq
       (partition-by string/blank?)
       (remove #(= '("") %))
       (map #(drop 1 %))
       (mapv (fn [lines]
               (set
                (map (fn [line]
                       (mapv #(Integer/parseInt %) (string/split line #",")))
                     lines))))))

(def test-scanners
  (parse-input (io/reader (io/file "nineteen_test.txt"))))

(def real-scanners
  (parse-input (io/reader (io/file "nineteen.txt"))))

(defn distance
  [a b]
  (reduce + (map (fn [a b] (* (- a b) (- a b))) a b)))

(defn distances
  [beacon beacons]
  (for [b beacons :when (not= b beacon)]
    (distance b beacon)))

(defn matching-beacon
  [distances scanner-distances]
  (some (fn [[beacon distances']]
          (when (= 11 (count (s/intersection distances distances')))
            beacon))
        scanner-distances))

(defn beacon-distances [scanner]
  (zipmap scanner (map #(set (distances % scanner)) scanner)))

(defn coordinate-mapping
  [scanner-a scanner-b]
  (let [beacon-distances-a (beacon-distances scanner-a)]
    (->> (beacon-distances scanner-b)
         (reduce
          (fn [m [coord distances]]
            (if-let [b (matching-beacon distances beacon-distances-a)]
              (assoc m b coord)
              m))
          {})
         not-empty)))

(def orientations
  (for [permutation (combo/permutations [0 1 2])
        polarities (combo/cartesian-product [1 -1] [1 -1] [1 -1])]
    [permutation polarities]))

(defn orientate
  [point [permutation polarities]]
  (mapv (fn [n p]
          (* p (nth point n)))
        permutation
        polarities))

(defn find-position [coordinates]
  (let [[[a1 b1] [a2 b2]] (take 2 coordinates)
        d1 (map - a1 a2)
        d2 (map - b1 b2)]
    (some (fn [orientation]
            (when (= d1 (orientate d2 orientation))
              {:orientation orientation
               :offset (map - a1 (orientate b1 orientation))}))
          orientations)))

(defn reorientate-scanner
  [scanner {:keys [orientation offset]}]
  (set (map #(mapv + (orientate % orientation) offset) scanner)))

(defn find-all-beacons [scanners]
  (let [scanner-0 (first scanners)]
    (loop [beacons scanner-0
           unresolved (disj (set scanners) scanner-0)
           resolved [scanner-0]
           offsets [[0 0 0]]]
      (if-not (seq unresolved)
        {:offsets offsets
         :beacons beacons}
        (let [scanner (peek resolved)
              matches (keep (fn [other]
                              (when-let [m (coordinate-mapping scanner other)]
                                [other m]))
                            unresolved)
              overlapping (map first matches)
              coordinate-mappings (map second matches)
              positions (map find-position coordinate-mappings)
              reorientated (map reorientate-scanner overlapping positions)]
          (recur (into beacons cat reorientated)
                 (apply disj unresolved overlapping)
                 (into (pop resolved) reorientated)
                 (into offsets (map :offset positions))))))))

(defn manhattan
  [a b]
  (reduce + (map (fn [x y] (Math/abs (- x y))) a b)))

(def beacons-and-offsets (find-all-beacons real-scanners))

(count (:beacons beacons-and-offsets))
(->> (combo/combinations (:offsets beacons-and-offsets) 2)
     (map #(apply manhattan %))
     (apply max))
