(ns aoc.seven
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn concat-op [a b] (parse-long (str a b)))

(def part-1-ops [+ *])
(def part-2-ops [+ * concat-op])

(defn parts
  [equations ops]
  (let [solve (fn solve [[total operands]]
                (if (seq operands)
                  (mapcat #(solve [(% total (first operands)) (rest operands)]) ops)
                  [total]))]
    (filter (fn [[target operands]]
              (first (filter #(= target %) (solve [0 operands]))))
            equations)))

(comment
  
  (def equations (map (fn [line]
                        (let [[x xs] (string/split line #": ")]
                          [(parse-long x) (map parse-long (string/split xs #" "))]))
                      (string/split (slurp (io/resource "seven.txt")) #"\n")))
  
  (reduce + (map first (parts equations part-1-ops)))
  (reduce + (map first (parts equations part-2-ops))))
