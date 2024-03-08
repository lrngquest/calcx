(ns calc.gui45u  ;; "retro" ver; via edits to  calc-35.core
  (:import (java.awt Color Dimension Font FontMetrics) 
           (javax.swing JPanel JFrame  JOptionPane)
           (java.awt.event  KeyListener KeyEvent  MouseListener MouseEvent))
  (:require [calc.mach2  :as mc])  (:require [calc.m45rom  :as r])
  (:gen-class) ) 


(declare keyinfo  ffn)   (def aadr (atom 0) ) ;;instr. seq. start addr

(def DISPLAY_HT 52)  (def WINDOW_WD 255)  (def WINDOW_HT 484)
(def colormap {:bck  (Color. 0x000000)  :wht  (Color. 0xffffff)
               :ltgr (Color. 0xa0a0a0)  :mdgr (Color. 0x585858)
               :dkgr (Color. 0x383838)  :gold (Color. 0xffd700)
               :blu  (Color. 0x4040ff)  :dkrd (Color. 0x500000)
               :brrd (Color. 0xff4000)  :vlgr (Color. 0xc0c8d8) } )

(def buttonwidth 30)  (def buttonheight 24)

(defn mkfnt [i] (if (< i 6) 6  (Font.  "Helvetica" Font/BOLD i) ) );;PLAIN
(def ftv (vec (map mkfnt (range 25) )) )

(def f-fm (atom {:key-font (ftv 0)  :kfm 0  :disp-font (ftv 5)  :dfm 0}) )

;; replace wi. init2
(defn init2 "fixed font sized" [ ^JFrame jfrm]
      (swap! f-fm assoc
             :key-font   (ftv 14)
             :kfm        (.getFontMetrics jfrm (ftv 14) )
             :disp-font  (ftv 24)
             :dfm        (.getFontMetrics jfrm (ftv 24) )   ) )


(defn draw-string "" [ ^sun.java2d.SunGraphics2D g  ^String s
                      [rx ry wd ht] fg bg  ^FontMetrics fm]
  (let [descent      (.getMaxDescent fm)
        sheight      (.getHeight     fm)
        swidth       (.stringWidth   fm  s)  ]
    (.setColor g (bg colormap) ) ;; was color[ fg]
    (.fillRect g rx ry wd ht)    
    (.setColor g (fg colormap) )  ;; 'int' required below, else we get  ratio.
    (.drawString g s  (int(+ rx (/ (- wd swidth) 2)))
                 (int (+ ry (/ (- (* 2 ht) sheight) 2) descent)) )    )   )

(defn draw-legend "" [^sun.java2d.SunGraphics2D g ^String lg rect ]
  ;; hardwire to have fewer params; as in  draw-string  assume font set
  (let [ [rx ry wd ht] rect]
    (when (> (count lg) 0)
      (.setColor g (:gold colormap)) ;; int below avoids illegal reflection!
      (.drawString g lg (int rx) (int(- ry 4))) ) )  )

(defn draw-calc "" [ ^sun.java2d.SunGraphics2D g]
  (.setColor g (:dkgr colormap) )
  (.fillRect g 0 DISPLAY_HT  WINDOW_WD (- WINDOW_HT DISPLAY_HT) )
  (doseq [i (range (count keyinfo))]
    (let [[rect kc lg krsa fg bg]  (keyinfo i)]
      (.setFont g (:key-font @f-fm))
      (draw-string g kc  rect fg bg (:kfm @f-fm))
      (.setFont g (ftv 12))
      (draw-legend g lg  rect)      )  )  )


(defn draw-disp "" [ ^sun.java2d.SunGraphics2D g s]
  (.setFont g (:disp-font @f-fm))
  (draw-string g s [0 0 WINDOW_WD DISPLAY_HT] :brrd :dkrd  (:dfm @f-fm) ))
  

(defn calc-panel [frame ]
  (proxy [JPanel  KeyListener MouseListener] []
    (paintComponent [ ^sun.java2d.SunGraphics2D g] 
      (draw-calc g)
      (draw-disp g  (mc/run-instr-seq @aadr r/rom) )    )

    (keyTyped [ ^KeyEvent e]
      (let [ ksa   (r/abmap (char (.getKeyChar e)))     ]       
        (when ksa  (swap! aadr  (constantly ksa))  (.repaint^JPanel this) ) )  )
   
    (keyPressed [e]) (keyReleased [e])

    
    (mouseClicked [ ^MouseEvent e]
      (let [x  (.getX e)        y  (.getY e) 
            kirow  (first (filter (partial ffn x y) keyinfo)) ]
        (when kirow
          (swap!  aadr  (constantly (kirow 3))) (.repaint ^JPanel this) )  ) )

    (mousePressed [e] )  (mouseReleased[e] ) (mouseEntered [e] )
    (mouseExited  [e] )
    
    (getPreferredSize []  (Dimension.  WINDOW_WD  WINDOW_HT ) )    ))


(defn calcfn [] 
  (let [frame (JFrame. "calcx")
        _             (init2 frame)
        ^JPanel panel (calc-panel frame )  ]
    (doto panel 
      (.setFocusable true)
      (.addKeyListener panel)  (.addMouseListener panel ) )
    (doto frame 
      (.add panel)
      (.pack)
      (.setDefaultCloseOperation (JFrame/EXIT_ON_CLOSE)) ;;added
      (.setVisible true))   ) ) 

(defn -main [] (calcfn) )

;;Swing gui app inspired by:
;; github.com/stuarthalloway/programming-clojure/
;;     blob/master/src/examples/atom_snake.clj
;;
;;and the Java applet code from
;; Jacques Laporte  and  David G. Hicks

;;Microcode "machine" based on:
;;  https://github.com/AshleyF/HP35


(def keyinfo [  ;; 2024Jan23 hp45 abbreviations ; color symb
[[ 14 100 30 24] "1/x"   "y^x"    (r/abmap \\)   :wht :mdgr ]
[[ 62 100 30 24] "ln"    "log"    (r/abmap \l)   :wht :mdgr ]
[[110 100 30 24] "e^x"   "10^x"   (r/abmap \^)   :wht :mdgr ]
[[158 100 30 24] "FIX"   "SCI"    (r/abmap \f)   :wht :mdgr ]
[[206 100 30 24] ""      ""       (r/abmap \')   :gold :gold ]

[[ 14 148 30 24] "x^2"   "sqrt x" (r/abmap \w)   :wht :mdgr ]
[[ 62 148 30 24] "->P"   "->R"    (r/abmap \p)   :wht :bck ]
[[110 148 30 24] "sin"   "aSIN"   (r/abmap \s)   :wht :bck ]
[[158 148 30 24] "cos"   "aCos"   (r/abmap \c)   :wht :bck ]
[[206 148 30 24] "tan"   "aTan"   (r/abmap \t)   :wht :bck ]

[[ 14 196 30 24] "x<>y"  "n!"     (r/abmap \a)   :wht :ltgr ]
[[ 62 196 30 24] "RDN"   "x,s"    (r/abmap \r)   :wht :ltgr ]
[[110 196 30 24] "STO"   "->D.MS" (r/abmap \[)   :wht :ltgr ]
[[158 196 30 24] "RCL"   "D.MS->" (r/abmap \])   :wht :ltgr ]
[[206 196 30 24] "%"     "delta %" (r/abmap \%)  :wht :mdgr ]

[[ 14 244 78 24] "ENTER" "DEG"    (r/abmap \space) :wht :ltgr ]
[[110 244 30 24] "CHS"   "RAD"    (r/abmap \z)   :wht :ltgr ]
[[158 244 30 24] "EEX"   "GRD"    (r/abmap \e)   :wht :ltgr ]
[[206 244 30 24] "CLX"   "CLEAR"  (r/abmap \;)   :wht :ltgr ]

[[ 14 292 24 24] "-"     ""       (r/abmap \-)   :wht :ltgr ]
[[ 63 292 37 24] "7"     "cm/in"  (r/abmap \7)   :bck :wht ]
[[131 292 37 24] "8"     "kg/lb"  (r/abmap \8)   :bck :wht ]
[[199 292 37 24] "9"     "lgr/gal" (r/abmap \9)  :bck :wht ]

[[ 14 340 24 24] "+"     ""       (r/abmap \+)   :wht :ltgr ]
[[ 63 340 37 24] "4"     ""       (r/abmap \4)   :bck :wht ]
[[131 340 37 24] "5"     ""       (r/abmap \5)   :bck :wht ]
[[199 340 37 24] "6"     ""       (r/abmap \6)   :bck :wht ]

[[ 14 388 24 24] "x"     ""       (r/abmap \*)   :wht :ltgr ]
[[ 63 388 37 24] "1"     ""       (r/abmap \1)   :bck :wht ]
[[131 388 37 24] "2"     ""       (r/abmap \2)   :bck :wht ] 
[[199 388 37 24] "3"     ""       (r/abmap \3)   :bck :wht ]

[[ 14 436 24 24] "/"     ""       (r/abmap \/)   :wht :ltgr ]
[[ 63 436 37 24] "0"     "LASTX"  (r/abmap \0)   :bck :wht ]
[[131 436 37 24] "."     "Pi"     (r/abmap \.)   :bck :wht ]
[[199 436 37 24] "SIG+"  "SIG-"   (r/abmap \o)   :bck :wht ]      ])


(defn ffn "point-in-rect filter fn" [x y kv]
  (let [[rx ry wd ht]  (kv 0) ] ;;get the rect, de-structure
    (and (>= x rx) (< x (+ rx wd)) (>= y ry) (< y (+ ry ht))) )  )

