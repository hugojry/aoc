#!/usr/bin/env bb

(require '[clojure.string :as string])

(defn bin-print
  [x]
  (println (Integer/toString x 2)))

(defn insertions
  [mask]
  (mapcat (fn [x]
            (loop [from 0
                   locations []]
              (if-let [index (string/index-of mask x from)]
                (recur (inc index)
                       (conj locations [x index]))
                locations)))
          [\0 \1]))

(defn bit-clear
  [x n]
  (bit-and x (bit-not (bit-shift-left 1 n))))

(defn insert
  [x index]
  (if (= \1 x)
    #(bit-set % index)
    #(bit-clear % index)))

(defn compile-mask
  [mask]
  (->> (insertions (string/reverse mask))
       (map #(apply insert %))
       (apply comp)))

(defn parse-mask
  [line]
  (peek (string/split line #" ")))

(defn parse-assignment
  [line]
  {:address (Integer/parseInt (subs (first (string/split line #"]")) 4))
   :value (Integer/parseInt (peek (string/split line #" ")))})

(defn interpret
  [instructions]
  (loop [instructions instructions
         mask nil
         memory {}]
    (let [instruction (first instructions)]
      (cond
        (nil? instruction) memory

        (string/starts-with? instruction "mask")
        (recur (rest instructions)
               (compile-mask (parse-mask instruction))
               memory)

        :else
        (let [{:keys [address value]} (parse-assignment instruction)]
          (recur (rest instructions)
                 mask
                 (assoc memory address (mask value))))))))

(reduce + (vals (interpret *input*)))
