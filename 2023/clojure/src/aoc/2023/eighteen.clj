(ns aoc.2023.eighteen
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn parse-input [input-string]
  (map (fn [s]
         (update (string/split s #" ") 1 #(Integer/parseInt %)))
       (string/split input-string #"\n")))

(defn shoelace [vertices]
  (/ (->> (first vertices)
          (conj vertices)
          (partition 2 1)
          (map (fn [[[x1 y1] [x2 y2]]]
                 (* (- y1 y2) (+ x1 x2))))
          (reduce +))
     2))

(defn corner-type
  [d1 d2]
  (case (str d1 d2)
    ("UR" "DL" "RD" "LU") :outer
    ("UL" "DR" "RU" "LD") :inner))

(defn next-vertex
  [vertex direction n]
  (case direction
    "R" (update vertex 1 + n)
    "L" (update vertex 1 - n)
    "D" (update vertex 0 + n)
    "U" (update vertex 0 - n)))

(defn corner-types [steps]
  (->> (cons (last steps) steps)
       (map first)
       (partition 2 1)
       (map #(apply corner-type %))))

(defn part-1 [steps]
  (->> (map #(take 2 %) steps)
       (interleave (corner-types steps))
       (partition 3 2)
       (reduce (fn [vertices [c1 [direction n] c2]]
                 (conj vertices (next-vertex (peek vertices)
                                             direction
                                             (condp = [c1 c2]
                                               [:outer :outer] (inc n)
                                               [:inner :inner] (dec n)
                                               n))))
               [[0, 0]])
       shoelace))

(defn part-2 [steps]
  (part-1 (map (comp (fn [code]
                       [(case (nth code 7) \0 "R" \1 "D" \2 "L" \3 "U")
                        (Long/parseLong (subs code 2 7) 16)])
                     last)
               steps)))
