(ns aoc.2023.one
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(def input (string/split (slurp (io/resource "one.txt")) #"\n"))

(defn calibration [s]
  (read-string (str (first s) (last s))))

(def matches ["on" "tw" "thre" "four" "fiv" "six" "seve" "eigh" "nin"])

(defn match-to-digit [s]
  (let [idx (.indexOf matches s)]
    (if (neg? idx) s (str (inc idx)))))

(def part-two-re #"[1-9]|on(?=e)|tw(?=o)|thre(?=e)|four|fiv(?=e)|six|seve(?=n)|eigh(?=t)|nin(?=e)")

(defn solve [in re]
  (->> in
       (map #(calibration (map match-to-digit (re-seq re %))))
       (reduce +)))

(comment
  (solve input #"[0-9]")
  (solve input part-two-re))
