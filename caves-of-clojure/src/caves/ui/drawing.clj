(ns caves.ui.drawing
  (:require [lanterna.screen :as s]))


; Constants -------------------------------------------------------------------
(def screen-size [80 24])


; Drawing ---------------------------------------------------------------------
(defn clear-screen [screen]
  (let [[cols rows] screen-size
        blank (apply str (repeat cols \space))]
    (doseq [row (range rows)]
      (s/put-string screen 0 row blank))))


(defmulti draw-ui
  (fn [ui game screen]
    (:kind ui)))

(defmethod draw-ui :start [ui game screen]
  (s/put-string screen 0 0 "Welcome to the Caves of Clojure!")
  (s/put-string screen 0 1 "Press any key to continue.")
  (s/put-string screen 0 2 "")
  (s/put-string screen 0 3 "Once in the game, you can use enter to win,")
  (s/put-string screen 0 4 "and backspace to lose."))

(defmethod draw-ui :win [ui game screen]
  (s/put-string screen 0 0 "Congratulations, you win!")
  (s/put-string screen 0 1 "Press escape to exit, anything else to restart."))

(defmethod draw-ui :lose [ui game screen]
  (s/put-string screen 0 0 "Sorry, better luck next time.")
  (s/put-string screen 0 1 "Press enter to exit, anything else to go."))

(defn get-viewport-coords [game vcols vrows]
  (let [location (:location game)
        [center-x center-y] location

        tiles (:tiles (:world game))

        map-rows (count tiles)
        map-cols (count (first tiles))

        start-x (max 0 (- center-x (int (/ vcols 2))))
        start-y (max 0 (- center-y (int (/ vrows 2))))

        end-x (min map-cols (+ start-x vcols))
        end-y (min map-rows (+ start-y vrows))

        start-x (- end-x vcols)
        start-y (- end-y vrows)]
    [start-x start-y end-x end-y]))

(defn draw-crosshairs [screen vcols vrows]
  (let [crosshair-x (int (/ vcols 2))
        crosshair-y (int (/ vrows 2))]
    (s/put-string screen crosshair-x crosshair-y "X" {:fg :red})
    (s/move-cursor screen crosshair-x crosshair-y)))

(defn draw-world [screen vrows vcols start-x start-y end-x end-y tiles]
  (doseq [[vrow-idx mrow-idx] (map vector
                                   (range 0 vrows)
                                   (range start-y end-y))
          :let [row-tiles (subvec (tiles mrow-idx) start-x end-x)]]
    (doseq [vcol-idx (range vcols)
            :let [{:keys [glyph color]} (row-tiles vcol-idx)]]
      (s/put-string screen vcol-idx vrow-idx glyph {:fg color}))))

(defmethod draw-ui :play [ui game screen]
  (let [world (:world game)
        tiles (:tiles world)
        [cols rows] screen-size
        vcols cols
        vrows (dec rows)
        [start-x start-y end-x end-y] (get-viewport-coords game vcols vrows)]
    (draw-world screen vrows vcols start-x start-y end-x end-y tiles)
    (draw-crosshairs screen vcols vrows)))


(defn draw-game [game screen]
  (clear-screen screen)
  (doseq [ui (:uis game)]
    (draw-ui ui game screen))
  (s/redraw screen))

