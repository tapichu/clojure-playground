(ns caves.ui.drawing
  (:use [caves.utils :only [map2d shear]])
  (:require [lanterna.screen :as s]))


; Definitions -----------------------------------------------------------------
(defmulti draw-ui
  (fn [ui game screen]
    (:kind ui)))


; Start -----------------------------------------------------------------------
(defmethod draw-ui :start [ui game screen]
  (s/put-sheet screen 0 0
               ["Welcome to the Caves of Clojure!"
                ""
                "Press any key to continue."]))

; Win -------------------------------------------------------------------------
(defmethod draw-ui :win [ui game screen]
  (s/put-sheet screen 0 0
               ["Congratulations, you win!"
                "Press escape to exit, anything else to restart."]))

; Lose ------------------------------------------------------------------------
(defmethod draw-ui :lose [ui game screen]
  (s/put-sheet screen 0 0
               ["Sorry, better luck next time."
                "Press escape to exit, anything else to restart."]))

; Play ------------------------------------------------------------------------
;
; The Play UI draws the world. This is tricky, but hopefully not too bad.
;
; Imagine a 10 by 4 world with a 3 by 2 "viewport":
;
;  0123456789
; 0...OVV....
; 1...VVV....
; 2...VVV....
; 3..........
; 4..........
;
; The V is the viewport, and the O is the "viewport origin", which would be
; [3 0] in terms of the map's coordinates.

(defn get-viewport-coords
  "Find the top-left coordinates of the viewport in the overall map, centering on the player."
  [game player-location vcols vrows]
  (let [[center-x center-y] player-location

        tiles (:tiles (:world game))

        map-rows (count tiles)
        map-cols (count (first tiles))

        start-x (max 0 (- center-x (int (/ vcols 2))))
        start-y (max 0 (- center-y (int (/ vrows 2))))

        end-x (min map-cols (+ start-x vcols))
        end-y (min map-rows (+ start-y vrows))

        start-x (- end-x vcols)
        start-y (- end-y vrows)]
    [start-x start-y]))

(defn get-viewport-coords-of
  "Get the viewport coordinates for the given real coords, given the viewport origin."
  [origin coords]
  (map - coords origin))


(defn draw-hud [screen game [ox oy]]
  (let [hud-row (dec (second (s/get-size screen)))
        [x y] (get-in game [:world :entities :player :location])
        info (str "player loc: [" x "-" y "]")
        info (str info " viewport origin: [" ox "-" oy "]")]
    (s/put-string screen 0 hud-row info)))


(defn draw-entity [screen origin {:keys [location glyph color]}]
  (let [[x y] (get-viewport-coords-of origin location)]
    (s/put-string screen x y glyph {:fg color})))


(defn draw-world [screen vrows vcols [ox oy] tiles]
  (letfn [(render-tile [tile]
            [(:glyph tile) {:fg (:color tile)}])]
    (let [tiles (shear tiles ox oy vcols vrows)
          sheet (map2d render-tile tiles)]
      (s/put-sheet screen 0 0 sheet))))


(defn highlight-player [screen origin player]
  (let [[x y] (get-viewport-coords-of origin (:location player))]
    (s/move-cursor screen x y)))


(defmethod draw-ui :play [ui game screen]
  (let [world (:world game)
        {:keys [tiles entities]} world
        player (:player entities)
        [cols rows] (s/get-size screen)
        vcols cols
        vrows (dec rows)
        origin (get-viewport-coords game (:location player) vcols vrows)]
    (draw-world screen vrows vcols origin tiles)
    (doseq [entity (vals entities)]
      (draw-entity screen origin entity))
    (draw-hud screen game origin)
    (highlight-player screen origin player)))


; Entire Game -----------------------------------------------------------------
(defn draw-game [game screen]
  (s/clear screen)
  (doseq [ui (:uis game)]
    (draw-ui ui game screen))
  (s/redraw screen))
