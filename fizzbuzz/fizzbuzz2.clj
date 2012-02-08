(defn by-3? [num] (= (mod num 3) 0))
(defn by-5? [num] (= (mod num 5) 0))

(defn fizz-buzz [num]
  (let [divisible (list (by-3? num) (by-5? num))]
    (case divisible
      ((true true)) "FizzBuzz"
      ((true false)) "Fizz"
      ((false true)) "Buzz"
      num)))

(defn fizz-buzz-seq []
  (map fizz-buzz (iterate inc 1)))

(defn main []
  (println (apply str (interpose " " (time (take 100 (fizz-buzz-seq)))))))

(main)
