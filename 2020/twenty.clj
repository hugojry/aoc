(require '[clojure.string :as string]
         '[clojure.set :refer [rename-keys]])

(defn map-vals
  [f m]
  (zipmap (keys m) (map f (vals m))))

(defn parse-tiles
  [lines]
  (loop [lines lines, tiles {}]
    (if (seq lines)
      (let [line (first lines)
            tile-id-s (peek (string/split line #" "))
            tile-id (Integer/parseInt (subs tile-id-s 0 (dec (count tile-id-s))))]
        (recur (drop 12 lines)
               (assoc tiles tile-id (mapv vec (take 10 (rest lines))))))
      tiles)))

(defn pixels->int
  [pixels]
  (-> (apply str pixels)
      (string/replace \. \0)
      (string/replace \# \1)
      (Integer/parseInt 2)))

(defn edges
  [tile]
  (map-vals pixels->int {:north (first tile)
                         :east (mapv peek tile)
                         :south (peek tile)
                         :west (mapv first tile)}))

(defn rotate-clockwise
  [edges]
  (rename-keys edges {:north :east
                      :east :south
                      :south :west
                      :west :north}))

(defn reverse-int
  [x]
  (Integer/parseInt (apply str (reverse (Integer/toString x 2))) 2))

(defn vertical-mirror
  [edges]
  (-> (rename-keys edges {:east :west :west :east})
      (update :south reverse-int)
      (update :north reverse-int)))

(defn horizontal-mirror
  [edges]
  (-> (rename-keys edges {:north :south :south :north})
      (update :east reverse-int)
      (update :west reverse-int)))

(defn orientations
  [edges]
  (->> [edges
        (horizontal-mirror edges)
        (vertical-mirror edges)
        (vertical-mirror (horizontal-mirror edges))]
       (map #(cons % (take 3 (iterate rotate-clockwise %))))
       (into #{} cat)))

(orientations (edges (val (first (parse-tiles *input*)))))
