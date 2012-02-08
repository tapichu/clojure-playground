(defn divisible-by? [result div num] (when (= (rem num div) 0) result))
(def by-3? (partial divisible-by? "Fizz" 3))
(def by-5? (partial divisible-by? "Buzz" 5))

(defn fizz-buzz [n]
  (let [res (str (by-3? n) (by-5? n))]
    (if (= res "") n res)))

(defn fizz-buzz-seq []
  (map fizz-buzz (iterate inc 1)))

(defn main []
  ; (nth (fizz-buzz-seq) 1000000)
  (println (apply str (interpose " " (time (take 100 (fizz-buzz-seq)))))))

(main)
