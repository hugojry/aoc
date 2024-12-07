(ns aoc.input
  (:require [clojure.string :as string]))

(defn make-grid
  [readable & {:keys [sep]}]
  (let [lines (string/split (slurp readable) #"\n")]
    (if sep
      (map #(string/split % (re-pattern sep)) lines)
      (map seq lines))))
