(ns caves.world)

(def world-size [160 50])

; Data Structures -------------------------------------------------------------
(defrecord World [tiles])
(defrecord Tile [kind glyph color])

(def tiles
  {:floor (new Tile :floor "." :white)
   :wall  (new Tile :wall  "#" :white)
   :bound (new Tile :bound "X" :black)})

; Utility Functions -----------------------------------------------------------
(defn get-tile [tiles x y]
  (get-in tiles [y x] (:bound tiles)))

; World -----------------------------------------------------------------------
(defn random-tiles []
  (let [[cols rows] world-size]
    (letfn [(random-tile []
              (tiles (rand-nth [:floor :wall])))
            (random-row []
              (vec (repeatedly cols random-tile)))]
      (vec (repeatedly rows random-row)))))

(defn random-world []
  (new World (random-tiles)))
