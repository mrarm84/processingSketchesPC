;; Simple Fluxus Animation
;; Run this with: fluxus_bin\Fluxus\bin\fluxus.exe -s main.scm

(clear)

(define (draw)
  ;; Set a background color (dark grey)
  (blur 0.1) ;; Add some motion blur effect
  
  ;; Pulse color based on time
  (colour (vector (+ 0.5 (* 0.5 (sin (time)))) 
                  (+ 0.5 (* 0.5 (cos (* 1.1 (time))))) 
                  0.8))
  
  ;; Rotate the whole scene
  (rotate (vector (* 20 (time)) (* 30 (time)) 0))
  
  ;; Draw a sphere that deforms slightly
  (with-state
    (scale (vector (+ 1 (* 0.2 (sin (* 5 (time))))) 1 1))
    (draw-sphere))
    
  ;; Draw some surrounding cubes
  (for ([i (in-range 0 10)])
    (with-state
      (rotate (vector 0 (* i 36) 0))
      (translate (vector 3 0 0))
      (rotate (vector (* 100 (time)) 0 0))
      (draw-cube))))

;; Register the draw function to be called every frame
(every-frame (draw))
