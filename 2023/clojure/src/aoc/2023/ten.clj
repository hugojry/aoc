(ns aoc.2023.ten
  (:require [clojure.java.io :as io]))

(let [lines (line-seq (io/reader (io/resource "ten.txt")))]
  (def pipes (into [] cat (map vec lines)))
  (def width (count (first lines)))
  (def height (count lines))
  (def start (ffirst (filter #(= \S (second %)) (map-indexed vector pipes)))))

(defn right [x] [:right (inc x)])
(defn left [x] [:left (dec x)])
(defn down [x] [:down (+ x width)])
(defn up [x] [:up (- x width)])

(defn next-pipe
  [d x]
  (let [pipe (get pipes x)]
    (case d
      :down  (case pipe \| (down x)  \J (left x)  \L (right x) nil)
      :up    (case pipe \| (up x)    \F (right x) \7 (left x)  nil)
      :left  (case pipe \- (left x)  \F (down x)  \L (up x)    nil)
      :right (case pipe \- (right x) \J (up x)    \7 (down x)  nil))))

(defn pipe-seq
  [d x]
  (lazy-seq
   (let [[d' x'] (next-pipe d x)]
     (when (get pipes x')
       (cons x (pipe-seq d' x'))))))

(defn route []
  (let [[d x] (cond
                (next-pipe :right (inc start)) [:right (inc start)]
                (next-pipe :left  (dec start)) [:left (dec start)]
                (next-pipe :down  (+ start width)) [:down (+ start width)])]
    (cons start (pipe-seq d x))))

(let [r (set (route))]
  (def pipes-2 (vec (map-indexed (fn [i pipe]
                                   (cond
                                     (= start i) \F
                                     (r i) pipe
                                     :else \.))
                                 pipes))))

(defn all-sides [idx]
  (mapv (fn [x] [nil x]) [(inc idx) (dec idx) (+ idx width) (- idx width)]))

(defn get-successors
  [d x pipe]
  (case pipe
    \. (all-sides x)
    \- (case d
         :left  [(left x) (down x)]
         :right [(right x) (up x)]
         nil)
    \| (case d
         :up    [(up x) (left x)]
         :down  [(down x) (right x)]
         nil)
    \F (case d
         :up    [(right x) (left x) (up x)]
         :left  [(down x)]
         nil)
    \7 (case d
         :right [(down x) (up x) (right x)]
         :up    [(left x)]
         nil)
    \L (case d
         :left  [(up x) (down x) (left x)]
         :down  [(right x)]
         nil)
    \J (case d
         :down  [(left x) (down x) (right x)]
         :right [(up x)]
         nil)
    nil))

(defn bfs []
  (loop [queue (conj (clojure.lang.PersistentQueue/EMPTY) [:up start])
         visited #{[:up start]}
         positions-visited #{}]
    (if-some [[d x] (peek queue)]
      (if-some [pipe (get pipes-2 x)]
        (let [successors (get-successors d x pipe)]
          (recur (into (pop queue)
                       (comp (filter some?) (remove visited))
                       successors)
                 (reduce conj visited successors)
                 (->> (map second successors)
                      (filter #(some? (get pipes-2 %)))
                      (reduce conj positions-visited))))
        (recur (pop queue) visited positions-visited))
      positions-visited)))

(comment

  (/ (count (route)) 2)

  (get-successors :up start \F)

  (vals (sort (group-by #(quot % 10) (sort (bfs)))))

  (- (* width height) (count (bfs))))
