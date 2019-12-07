(ns aoc.intcode)

(def ^:dynamic *input*)

(defn op-code
  [x]
  (mod x 100))

(defn param-modes
  [x n]
  (loop [n n
         modes (quot x 100)
         params []]
    (if (zero? n)
      params
      (recur (dec n)
             (quot modes 10)
             (conj params (mod modes 10))))))

(def op-code->param-pattern
  {1 [:value :value :pointer]
   2 [:value :value :pointer]
   3 [:pointer]
   4 [:value]
   5 [:value :value]
   6 [:value :value]
   7 [:value :value :pointer]
   8 [:value :value :pointer]})

(defn derefenced-params
  [x memory pc]
  (let [op-code (op-code x)
        pattern (op-code->param-pattern op-code)
        modes (param-modes x (count pattern))]
    (loop [pc (inc pc)
           type-modes (map vector pattern modes)
           params []]
      (if-some [[type mode] (first type-modes)]
        (recur (inc pc)
               (rest type-modes)
               (if (= type :pointer)
                 (conj params (nth memory pc))
                 (if (zero? mode)
                   (conj params (nth memory (nth memory pc)))
                   (conj params (nth memory pc)))))
        params))))

(defn add
  [memory a b location]
  (assoc memory location (+ a b)))

(defn mult
  [memory a b location]
  (assoc memory location (* a b)))

(defn input
  [memory location]
  (assoc memory location *input*))

(defn output
  [memory location]
  (println location)
  memory)

(defn jump-if-true
  [pc x jump]
  (if (not (zero? x))
    jump
    (+ pc 3)))

(defn jump-if-false
  [pc x jump]
  (if (zero? x)
    jump
    (+ pc 3)))

(defn less-than
  [memory a b location]
  (assoc memory location (if (< a b) 1 0)))

(defn equals
  [memory a b location]
  (assoc memory location (if (= a b) 1 0)))

(defn interpret
  [memory pc]
  (let [instruction (nth memory pc)
        params (derefenced-params instruction memory pc)]
    (case (op-code instruction)
      1 (recur (apply add memory params) (+ pc 4))
      2 (recur (apply mult memory params) (+ pc 4))
      3 (recur (apply input memory params) (+ pc 2))
      4 (recur (apply output memory params) (+ pc 2))
      5 (recur memory (apply jump-if-true pc params))
      6 (recur memory (apply jump-if-false pc params))
      7 (recur (apply less-than memory params) (+ pc 4))
      8 (recur (apply equals memory params) (+ pc 4))
      99 memory)))
