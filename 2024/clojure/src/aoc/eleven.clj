(ns aoc.eleven)

(declare stone-tree)

(defn- stone-tree-inner
  [stone n]
  (cond
    (zero? n) 1
    (zero? stone) (stone-tree 1 (dec n))
    :else (let [log (count (str stone))]
            (if (even? log)
              (+ (stone-tree (parse-long (subs (str stone) 0 (/ log 2))) (dec n))
                 (stone-tree (parse-long (subs (str stone) (/ log 2))) (dec n)))
              (stone-tree (* stone 2024) (dec n))))))

(def stone-tree (memoize stone-tree-inner))

(comment

  (def stones [1117 0 8 21078 2389032 142881 93 385])

  (reduce + (map #(stone-tree % 25) stones))
  (reduce + (map #(stone-tree % 75) stones)))
