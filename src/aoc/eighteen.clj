(ns aoc.eighteen
  (:require [clojure.string :as string]
            [clojure.set :as set]))

(defn neighbors
  [[x y]]
  [[(inc x) y]
   [(dec x) y]
   [x (inc y)]
   [x (dec y)]])

(defn traversable?
  [maze-keys c]
  (or (= c \.) (< 96 (int c)) (contains? maze-keys c)))

(defn supercedes?
  [{:keys [maze-keys-1 steps-1]} {:keys [maze-keys-2 steps-2]}]
  (or (set/subset? maze-keys-2 maze-keys-1)
      (and (= maze-keys-1 maze-keys-2) (<= steps-1 steps-2))))

(defn prunable?
  [potential-winners path-state]
  (loop [potential-winners (seq potential-winners)]
    (if-some [potential-winner (first potential-winners)]
      (if (supercedes? potential-winner path-state)
        true
        (recur (rest potential-winners)))
      false)))

(defn new-potential-winners
  [potential-winners path-state]
  (let [new-potential-winners
        (remove #(supercedes? path-state %) potential-winners)]
    (conj new-potential-winners path-state)))

(defn get-2d
  [maze [x y]]
  (get (get maze y) x))

(defn search
  [maze starting-position all-keys]
  (let [initial-state {:position starting-position
                       :maze-keys #{}
                       :steps 0}]
    (loop [queue (conj (clojure.lang.PersistentQueue/EMPTY) initial-state)
           visited {}]
      (let [{:keys [position maze-keys steps] :as path-state} (peek queue)]
        (println path-state)
        (if (prunable? (get visited position) path-state)
          (recur (pop queue) visited)
          (let [next-positions (->> (neighbors position)
                                    (filter (fn [pos]
                                              (if-some [c (get-2d maze pos)]
                                                (traversable? maze-keys c)))))
                maze-key (get-2d maze position)
                new-maze-keys (if (< 96 (int maze-key) 123)
                                (conj maze-keys maze-key)
                                maze-keys)
                new-state (assoc path-state :maze-keys new-maze-keys :steps (inc steps))]
            ; (println (:position (peek queue)))
            (if (= new-maze-keys all-keys)
              path-state
              (recur (->> (map #(assoc new-state :position %) next-positions)
                          (into (pop queue)))
                     (update visited
                             position
                             new-potential-winners
                             path-state)))))))))

(comment

  (defn str->maze
    [s]
    (mapv (comp vec string/trim) (string/split s #"\n")))

  (defn all-keys
    [maze]
    (->> (into [] cat maze)
         (remove #{\. \#})
         (map (comp first string/lower-case str))
         set))

  (defn starting-position
    [maze]
    (let [coords (for [y (range (count maze))
                       x (range (count (first maze)))]
                   [x y])]
      (loop [coords coords]
        (if-some [coord (first coords)]
          (if (= (get-2d maze coord) \@)
            coord
            (recur (rest coords)))
          nil))))

  (def maze (str->maze (slurp "eighteen.txt")))
  (def test-maze-1 (str->maze "########################
#...............b.C.D.f#
#.######################
#.......a.B.c.d.A.e.F.g#
########################"))

  (search test-maze-1 [6 3] (all-keys test-maze-1))

  )
