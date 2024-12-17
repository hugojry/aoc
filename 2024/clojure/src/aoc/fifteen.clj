(ns aoc.fifteen
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [aoc.input :refer [make-grid]]))

(defn shift-fn [move]
  (let [args (case move
               :up [0 dec]
               :down [0 inc]
               :left [1 dec]
               :right [1 inc])]
    (fn [pos] (apply update pos args))))

(defn moved-blocks
  [warehouse position shift]
  (loop [layers [[[position position]]]]
    (let [layer (peek layers)
          s (set (mapcat (fn [block]
                           (remove #(= block %) (map #(warehouse (shift %)) block)))
                         layer))]
      (cond
        (s \#) nil
        (= s #{nil}) (mapcat identity layers)
        :else (recur (conj layers (disj s nil)))))))

(defn update-warehouse
  [warehouse position move]
  (let [shift (shift-fn move)
        moved (moved-blocks warehouse position shift)]
    (if-not moved
      warehouse
      (assoc (reduce (fn [w block]
                       (let [[k1 k2 :as new-block] (mapv shift block)]
                         (assoc w k1 new-block k2 new-block)))
                     (apply dissoc
                            (dissoc warehouse position)
                            (mapcat identity moved))
                     moved)
             (shift position) \@))))

(defn parts
  [warehouse moves]
  (let [rf (fn [warehouse' move]
             (let [position (key (first (filter #(= \@ (val %)) warehouse')))]
               (update-warehouse warehouse' position move)))]
    (->> moves
         (reduce rf warehouse)
         vals
         (filter vector?)
         distinct
         (map first)
         (map (fn [[i j]] (+ j (* i 100))))
         (reduce +))))

(comment

  (let [[warehouse moves] (string/split (slurp (io/resource "fifteen.txt")) #"\n\n")]
    (def moves (map #(case % \^ :up \v :down \> :right \< :left) (remove #(= \newline %) moves)))

    (def part-1-warehouse
      (into {} (for [[i row] (map-indexed vector (make-grid warehouse))
                     [j c] (map-indexed vector row)
                     :when (not= c \.)]
                 [[i j] (if (= \O c) [[i j]] c)])))

    (def part-2-warehouse
      (into {} cat (for [[i row] (map-indexed vector (make-grid warehouse))
                         [j c] (map-indexed vector row)
                         :when (not= c \.)
                         :let [j (* 2 j)
                               k1 [i j]
                               k2 [i (inc j)]]]
                     (cond
                       (= \O c) (let [block [k1 k2]]
                                  [[k1 block] [k2 block]])
                       (= \@ c) [[k1 \@]]
                       :else [[k1 c] [k2 c]])))))

  (parts part-1-warehouse moves)
  (parts part-2-warehouse moves))
