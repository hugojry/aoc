(ns aoc.five
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.set :refer [intersection]])
  (:import (clojure.lang PersistentQueue)))

(defn correct-order?
  [graph pages]
  (loop [pages pages]
    (if-some [page (first pages)]
      (if (some (get graph page #{}) (rest pages))
        false
        (recur (rest pages)))
      true)))

(defn part-1
  [graph updates]
  (->> updates
       (filter #(correct-order? graph %))
       (map #(nth % (quot (count %) 2)))
       (reduce +)))

(defn- zeroth-degree [degrees]
  (->> degrees
       (filter #(zero? (val %)))
       (map key)))

(defn- topological-sort [vertices graph]
  (let [degrees (reduce (fn [degrees' v]
                          (reduce #(update %1 %2 inc) degrees' (graph v)))
                        (zipmap vertices (repeat 0))
                        vertices)]
    (loop [q (into PersistentQueue/EMPTY (zeroth-degree degrees))
           degrees degrees
           order (list)]
      (if-some [vert (peek q)]
        (let [degrees' (reduce #(update %1 %2 (fnil dec 0))
                               (dissoc degrees vert)
                               (graph vert))]
          (recur (into (pop q) (zeroth-degree degrees'))
                 degrees'
                 (cons vert order)))
        order))))

(defn part-2
  [graph updates]
  (->> (filter #(not (correct-order? graph %)) updates)
       (map (fn [pages]
              (topological-sort
                pages
                (->> (select-keys graph pages)
                     (map (fn [[k vs]]
                            [k (intersection (set pages) vs)]))
                     (into {})))))
       (map #(nth % (quot (count %) 2)))
       (reduce +)))

(comment

  (let [[deps updates] (string/split (slurp (io/resource "five.txt")) #"\n\n")
        graph (->> (string/split deps #"\n")
                   (map (fn [line]
                          (map parse-long (string/split line #"\|"))))
                   (reduce (fn [m [a b]]
                             (update m b (fnil conj #{}) a))
                           {}))
        updates (->> (string/split updates #"\n")
                     (map (fn [line] (map parse-long (string/split line #",")))))]
    (def graph graph)
    (def updates updates))

  (part-1 graph updates)
  (part-2 graph updates))
