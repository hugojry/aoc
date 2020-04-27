(ns aoc.twenty
  (:require [aoc.maze :as maze]
            [clojure.zip :as z]))

(defn- children
  [maze visited node]
  (->> (maze/next-positions (:position node))
       (remove (fn [p] (or (contains? #{\# nil} (get-in maze p))
                           (visited p))))
       (map (fn [p]
              {:position p
               :depth (inc (:depth node))
               :contents (get-in maze p)}))))

(defn portal-exit
  [maze entrance]
  (first (filter #(Character/isUpperCase (get-in maze %))
                 (maze/next-positions entrance))))

(defn bfs
  [maze start]
  (loop [queue (conj (clojure.lang.PersistentQueue/EMPTY)
                     {:position start
                      :contents \.
                      :depth 0})
         visited #{}
         portals {}]
    (if-some [{:keys [position contents depth] :as node} (first queue)]
      (if (= contents \.)
        (recur (into (pop queue) (children maze visited node))
               (conj visited position)
               portals)
        (recur (pop queue)
               (conj visited position)
               (if (> depth 1)
                 (assoc portals [position (portal-exit maze position)] depth)
                 portals)))
      portals)))

(comment

(require '[clojure.string :as string])

(def test-maze-str
  (string/replace "         A           
         A           
  #######.#########  
  #######.........#  
  #######.#######.#  
  #######.#######.#  
  #######.#######.#  
  #####  B    ###.#  
BC...##  C    ###.#  
  ##.##       ###.#  
  ##...DE  F  ###.#  
  #####    G  ###.#  
  #########.#####.#  
DE..#######...###.#  
  #.#########.###.#  
FG..#########.....#  
  ###########.#####  
             Z       
             Z       " #" " "#"))

(def maze (maze/str->maze test-maze-str))

(def start [2 9])

(bfs maze start)

)
