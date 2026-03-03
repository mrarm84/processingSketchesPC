;; Hello Fluxus - A simple test script
;; Just a rotating cube to verify installation
(clear)
(define (animate)
  (rotate (vector (time) (* 1.1 (time)) (* 1.2 (time))))
  (draw-cube))
(every-frame (animate))
