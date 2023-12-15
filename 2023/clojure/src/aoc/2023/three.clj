(ns aoc.2023.three
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(def input (string/split (slurp (io/resource "three.txt")) #"\n"))

(defn enumerate [xs]
  (map-indexed list xs))

(defn all-matches [matcher]
  ((fn step []
     (lazy-seq
      (when (.find matcher)
        (cons {:start (.start matcher)
               :end (.end matcher)
               :number (read-string (.group matcher))}
              (step)))))))

(defn numbers-in-line [line]
  (all-matches (re-matcher #"\d+" line)))

(def symbols (for [[i line] (enumerate input)
                    [j c] (enumerate line)
                    :when (not (or (<= 48 (int c) 57) (= c \.)))]
                [i j]))

(reduce + (for [[row line] (enumerate input)
                match (numbers-in-line line)
                :when (some (fn [[sym-row sym-col]]
                              (and (<= (dec row) sym-row (inc row))
                                   (<= (dec (:start match)) sym-col (:end match))))
                            symbols)]
            (:number match)))

(def gears (for [[i line] (enumerate input)
                 [j c] (enumerate line)
                 :when (= c \*)]
             [i j]))

(->> (for [[row line] (enumerate input)
           match (numbers-in-line line)]
       (map (fn [gear] [gear (:number match)])
            (filter (fn [[sym-row sym-col]]
                      (and (<= (dec row) sym-row (inc row))
                           (<= (dec (:start match)) sym-col (:end match))))
                    gears)))
     (sequence cat)
     (group-by first)
     vals
     (map #(map second %))
     (filter #(= 2 (count %)))
     (map #(reduce * %))
     (reduce +))
