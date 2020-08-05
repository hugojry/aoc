(ns aoc.twenty
  (:require [aoc.maze :as maze]))

(defn- upper? [ch]
  (if ch (Character/isUpperCase ch) false))

(defn- exit-square
  [maze p]
  (let [ps (maze/next-positions p)]
    (if-some [p (first (filter #(= (get-in maze %) \.) ps))]
      p
      (if-some [p (first (filter #(upper? (get-in maze %)) ps))]
        (recur (assoc-in maze p nil) p)
        (throw (Exception.))))))

(defn- other-portal-letter
  [maze p]
  (->> (maze/next-positions p)
       (keep #(get-in maze %))
       (filter upper?)
       first))

(defn- portal
  [maze p]
  [(get-in maze p) (other-portal-letter maze p)])

(defn ->portal-fn
  [maze]
  (into {} (for [x (range (count maze))
                 y (range (count (first maze)))
                 :let [p [x y]]
                 :when (upper? (get-in maze p))]
             [p (portal maze p)])))

(defn- exit?
  [maze portal p]
  (= [(get-in maze p) (other-portal-letter maze p)] portal))

(defn- portal-exit
  [maze entrance]
  (let [portal (portal maze entrance)
        maze* (assoc-in maze entrance nil)]
    (first (for [x (range (count maze))
                 y (range (count (first maze)))
                 :let [p [x y]]
                 :when (exit? maze* portal p)]
             (exit-square maze p)))))

(defn ->portal-exit-fn
  [maze]
  (into {} (for [x (range (count maze))
                 y (range (count (first maze)))
                 :let [p [x y]]
                 :when (upper? (get-in maze p))]
             [p (portal-exit maze p)])))

(defn outer-portal?
  [maze [x y]]
  (let [length (count maze)
        width (count (first maze))]
    (or (contains? #{0 1 (dec length) (- length 2)} x)
        (contains? #{0 1 (dec width) (- width 2)} y))))

(defn children
  [maze portal-fn portal-exit-fn visited path]
  (let [{:keys [position depth]} (peek path)]
    (into
     []
     (comp (remove
            (fn [p]
              (or (contains? #{\# nil} (get-in maze p))
                  (contains? #{[\A \A] [\Z \Z]} (portal maze p))
                  (and (zero? depth) (outer-portal? maze p))
                  (visited p))))
           (map
            (fn [p]
              (let [ch (get-in maze p)]
                (if (upper? ch)
                  (conj path (let [portal (portal-fn p)]
                               {:portal portal
                                :position (portal-exit-fn p)
                                :depth ((if (outer-portal? maze p)
                                          dec inc)
                                        depth)}))
                  (conj path {:position p :depth depth}))))))
     (maze/next-positions position))))

(defn- end-reachable?
  [maze position depth]
  (and (zero? depth)
       (some #{[\Z \Z]} (map #(portal maze %) (maze/next-positions position)))))

(defn bfs
  [maze children-fn start]
  (loop [queue (conj (clojure.lang.PersistentQueue/EMPTY)
                     [{:position start
                       :depth 0}])
         visited {}]
    (when-some [path (first queue)]
      (let [{:keys [position depth]} (peek path)]
        (if (end-reachable? maze position depth)
          path
          (recur (into (pop queue) (children-fn (get visited depth #{}) path))
                 (update visited depth (fnil conj #{}) position)))))))

(comment

(require '[clojure.string :as string])

(def test-1 "         A           
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
             Z       ")

(def test-2 "                   A               
                   A               
  #################.#############  
  #.#...#...................#.#.#  
  #.#.#.###.###.###.#########.#.#  
  #.#.#.......#...#.....#.#.#...#  
  #.#########.###.#####.#.#.###.#  
  #.............#.#.....#.......#  
  ###.###########.###.#####.#.#.#  
  #.....#        A   C    #.#.#.#  
  #######        S   P    #####.#  
  #.#...#                 #......VT
  #.#.#.#                 #.#####  
  #...#.#               YN....#.#  
  #.###.#                 #####.#  
DI....#.#                 #.....#  
  #####.#                 #.###.#  
ZZ......#               QG....#..AS
  ###.###                 #######  
JO..#.#.#                 #.....#  
  #.#.#.#                 ###.#.#  
  #...#..DI             BU....#..LF
  #####.#                 #.#####  
YN......#               VT..#....QG
  #.###.#                 #.###.#  
  #.#...#                 #.....#  
  ###.###    J L     J    #.#.###  
  #.....#    O F     P    #.#...#  
  #.###.#####.#.#####.#####.###.#  
  #...#.#.#...#.....#.....#.#...#  
  #.#####.###.###.#.#.#########.#  
  #...#.#.....#...#.#.#.#.....#.#  
  #.###.#####.###.###.#.#.#######  
  #.#.........#...#.............#  
  #########.###.###.#############  
           B   J   C               
           U   P   P               ")

(def test-3 "             Z L X W       C                 
             Z P Q B       K                 
  ###########.#.#.#.#######.###############  
  #...#.......#.#.......#.#.......#.#.#...#  
  ###.#.#.#.#.#.#.#.###.#.#.#######.#.#.###  
  #.#...#.#.#...#.#.#...#...#...#.#.......#  
  #.###.#######.###.###.#.###.###.#.#######  
  #...#.......#.#...#...#.............#...#  
  #.#########.#######.#.#######.#######.###  
  #...#.#    F       R I       Z    #.#.#.#  
  #.###.#    D       E C       H    #.#.#.#  
  #.#...#                           #...#.#  
  #.###.#                           #.###.#  
  #.#....OA                       WB..#.#..ZH
  #.###.#                           #.#.#.#  
CJ......#                           #.....#  
  #######                           #######  
  #.#....CK                         #......IC
  #.###.#                           #.###.#  
  #.....#                           #...#.#  
  ###.###                           #.#.#.#  
XF....#.#                         RF..#.#.#  
  #####.#                           #######  
  #......CJ                       NM..#...#  
  ###.#.#                           #.###.#  
RE....#.#                           #......RF
  ###.###        X   X       L      #.#.#.#  
  #.....#        F   Q       P      #.#.#.#  
  ###.###########.###.#######.#########.###  
  #.....#...#.....#.......#...#.....#.#...#  
  #####.#.###.#######.#######.###.###.#.#.#  
  #.......#.......#.#.#.#.#...#...#...#.#.#  
  #####.###.#####.#.#.#.#.###.###.#.###.###  
  #.......#.....#.#...#...............#...#  
  #############.#.#.###.###################  
               A O F   N                     
               A A D   M                     ")

(def input (slurp "twenty.txt"))

(defn ->maze [s]
  (maze/str->maze (string/replace s #" " "#")))

(let [maze (->maze test-2)]
  (bfs maze
       (partial children
                maze
                (->portal-fn maze)
                (->portal-exit-fn maze))
       [2 19]))

)
