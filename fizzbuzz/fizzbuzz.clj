(defn by-3? [num] (= (mod num 3) 0))
(defn by-5? [num] (= (mod num 5) 0))

(defn by-3-or-5 [num]
  (let [divisible (list (by-3? num) (by-5? num))]
    (case divisible
      ((true true)) "FizzBuzz"
      ((true false)) "Fizz"
      ((false true)) "Buzz"
      num)))

(defn fizz-buzz [nums]
  (map by-3-or-5 nums))

(defn main []
  (println (apply str (interpose " " (time (fizz-buzz (range 1 101)))))))

(main)
