(ns aoc.input
  (:require [clojure.string :as string]))

(defn make-grid
  [s & {:keys [sep]}]
  (let [lines (string/split s #"\n")]
    (if sep
      (map #(string/split % (re-pattern sep)) lines)
      (map seq lines))))
