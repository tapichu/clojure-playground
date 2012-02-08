(defn divisible-by [res div n]
  (if (number? n)
    (if (= (rem n div) 0) res n)
    n))

(def by-3? (partial divisible-by "Fizz" 3))
(def by-5? (partial divisible-by "Buzz" 5))
(def by-15? (partial divisible-by "FizzBuzz" 15))

(defn fizz-buzz []
  (map by-3? (map by-5? (map by-15? (iterate inc 1)))))

(defn main []
  (println (apply str (interpose " " (time (take 100 (fizz-buzz)))))))

(main)
