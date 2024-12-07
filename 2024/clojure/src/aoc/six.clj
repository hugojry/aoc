(ns aoc.six
  (:require [aoc.input :refer [make-grid]]
            [clojure.java.io :as io]))

(defn make-lab-map [input]
  (reduce (fn [m [i j]]
            (-> m
                (update-in [:rows i] (fnil conj []) j)
                (update-in [:cols j] (fnil conj []) i)))
          {}
          (for [[i row] (map-indexed vector input)
                [j c] (map-indexed vector row)
                :when (= \# c)]
            [i j])))

(defn next-obstacle
  [lab-map [i j] direction]
  (let [obstacle
        (case direction
          :up    (last (filter #(< % i) (get-in lab-map [:cols j])))
          :down  (first (filter #(< i %) (get-in lab-map [:cols j])))
          :left  (last (filter #(< % j) (get-in lab-map [:rows i])))
          :right (first (filter #(< j %) (get-in lab-map [:rows i]))))]
    (when obstacle
      (case direction
        :up    [[(inc obstacle) j] :right]
        :down  [[(dec obstacle) j] :left]
        :left  [[i (inc obstacle)] :up]
        :right [[i (dec obstacle)] :down]))))

(defn part-1 [grid]
  (let [lab-map (make-lab-map grid)
        start (first (for [[i row] (map-indexed vector grid)
                           [j c] (map-indexed vector row)
                           :when (= \^ c)]
                       [i j]))
        route (->> [start :up]
                   (iterate #(apply next-obstacle lab-map %))
                   (take-while some?)
                   (apply concat))]
    (->> (concat route [nil])
         (partition 3 2)
         (map (fn [[[i j] dir [i' j']]]
                (cond
                  (= i i') [i (if (< j j') (range j (inc j')) (range j' (inc j)))]
                  (= j j') [(if (< i i') (range i (inc i')) (range i' (inc i))) j]
                  :else (case dir
                          :up    [(range i -1 -1) j]
                          :down  [(range i (count grid)) j]
                          :left  [i (range j -1 -1)]
                          :right [i (range j (count (first grid)))]))))
         (mapcat (fn [range]
                   (if (seq? (first range))
                     (for [i (first range)]
                       [i (second range)])
                     (for [j (second range)]
                       [(first range) j]))))
         set)))

(defn cycles?
  [lab-map start]
  (loop [path (->> [start :up]
                   (iterate #(apply next-obstacle lab-map %))
                   (take-while some?))
         visited? #{}]
    (when-some [coords (first path)]
      (or (visited? coords)
          (recur (rest path) (conj visited? coords))))))

(defn part-2 [grid]
  (let [positions (part-1 grid)
        lab-map (make-lab-map grid)
        start (first (for [[i row] (map-indexed vector grid)
                           [j c] (map-indexed vector row)
                           :when (= \^ c)]
                       [i j]))
        positions (disj positions start)]
    (count
      (filter
        (fn [[i j]]
          (cycles? (-> lab-map
                       (update-in [:rows i] (comp sort (fnil conj [])) j)
                       (update-in [:cols j] (comp sort (fnil conj [])) i))
                   start))
        positions))))

(comment

  (def grid (make-grid (io/resource "six.txt")))

  (get-in (make-lab-map grid) [:rows 44])
  
  (count (part-1 grid))
  (part-2 grid))
