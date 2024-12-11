(ns aoc.nine
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn make-disk-map [xs]
  (let [rf (fn [files [x file?]]
             (update (if (and file? (not (zero? x)))
                       (conj files [(first files) x])
                       files)
                     0 + x))]
    (vec (map-indexed
           #(apply vector %1 %2)
           (subvec (reduce rf [0] (map vector xs (cycle [true false]))) 1)))))

(defn update-disk-map
  [disk-map i j]
  (if (<= j i)
    (if (= 1 (get-in disk-map [0 2]))
      (subvec disk-map 1)
      (update-in disk-map [0 2] dec))
    (let [last-idx (dec (count disk-map))]
      (if (= 1 (get-in disk-map [last-idx 2]))
        (pop disk-map)
        (update-in disk-map [last-idx 2] dec)))))

(defn part-1 [disk-map]
  (loop [i 0
         checksum 0
         disk-map disk-map]
    (if-some [[disk-id j] (first disk-map)]
      (recur (inc i)
             (+ checksum (* (if (<= j i)
                              disk-id
                              (first (peek disk-map)))
                            i))
             (update-disk-map disk-map i j))
      checksum)))

(defn- gap
  [f1 f2]
  (- (second f2) (second f1) (nth f1 2)))

(defn part-2-disk-map [disk-map]
  (reduce (fn [dm [_ limit len :as file]]
            (let [[j [[_ k len2]]] (->> (filter #(<= (nth % 1) limit) dm)
                                        (partition 2 1)
                                        (map-indexed vector)
                                        (filter (fn [[_ [f1 f2]]]
                                                  (<= len (gap f1 f2))))
                                        first)]
              (if j
                (filterv #(not= file %)
                         (-> (subvec dm 0 (inc j))
                             (conj (assoc file 1 (+ k len2)))
                             (into (subvec dm (inc j)))))
                dm)))
          disk-map
          (reverse disk-map)))

(defn part-2 [disk-map]
  (let [packed-disk-map (part-2-disk-map disk-map)]
    (reduce + (map (fn [[disk-id i len]]
                     (reduce + (map #(* disk-id %) (range i (+ i len)))))
                   packed-disk-map))))

(comment

  (def disk-map (->> (io/resource "nine.txt")
                     slurp
                     string/trim
                     (map #(- (int %) 48))
                     make-disk-map))
  
  (part-1 disk-map)
  (part-2 disk-map))
