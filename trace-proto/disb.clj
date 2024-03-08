#!/usr/bin/env bb
(ns disb
   (:require [clojure.java.io :as cj]  [clojure.edn :as edn])  )


(def rom (edn/read-string (slurp (cj/file "rom35.edn")) ) ) ;; rom[34]5.edn

(defn decode "hp45 instructions!" [wdv]
  ;;(printf "%d %o\n" wdv wdv)
  (cond
   (= (bit-and wdv 1) 1)           ;;decode block 1of8
   (let [n  (bit-shift-right (bit-and wdv 2) 1)
         pa (bit-shift-right (bit-and wdv 0x3fc) 2) ]
     (case n
       0 (printf "jsb @%03o\n" pa)
       1 (printf "go to @%03o\n" pa)) )

   (= (bit-and wdv 3) 2)           ;; 2of8
   (let [n   (bit-shift-right (bit-and wdv 0x3e0) 5)
         pa  (bit-shift-right (bit-and wdv 0x1c)  2)
         fx  (["p" "m" "x" "w" "wp" "ms" "xs" "s"] pa)
[first last]  ([[-1 -1] [3 12] [0 2] [0 13] [0 -1]  [3 13] [2 2] [13 13]]
                          pa )  ]
     (case n
          0 (printf "if b[%s] = 0\n" fx)
          1 (printf "0 -> b[%s]\n" fx)
          2 (printf "if a >= c[%s]\n" fx)
          3 (printf "if c[%s] >= 1\n" fx)
          4 (printf "b -> c[%s]\n" fx)
          5 (printf "0 - c -> c[%s]\n" fx)
          6 (printf "0 -> c[%s]\n" fx)
          7 (printf "0 - c - 1 -> c[%s]\n" fx)
          8 (printf "shift left a[%s]\n" fx)
          9 (printf "a -> b[%s]\n" fx)
         10 (printf "a - c -> c[%s]\n" fx)
         11 (printf "c - 1 -> c[%s]\n" fx)
         12 (printf "c -> a[%s]\n" fx)
         13 (printf "if c[%s] = 0\n" fx)
         14 (printf "a + c -> c[%s]\n" fx)
         15 (printf "c + 1 -> c[%s]\n" fx)
         16 (printf "if a >= b[%s]\n" fx)
         17 (printf "b exchange c[%s]\n" fx)
         18 (printf "shift right c[%s]\n" fx)
         19 (printf "if a[%s] >= 1\n" fx)
         20 (printf "shift right b[%s]\n" fx)
         21 (printf "c + c -> c[%s]\n" fx)
         22 (printf "shift right a[%s]\n" fx)
         23 (printf "0 -> a[%s]\n" fx)
         24 (printf "a - b -> a[%s]\n" fx)
         25 (printf "a exchange b[%s]\n" fx)
         26 (printf "a - c -> a[%s]\n" fx)
         27 (printf "a - 1 -> a[%s]\n" fx)
         28 (printf "a + b -> a[%s]\n" fx)
         29 (printf "a exchange c[%s]\n" fx)
         30 (printf "a + c -> a[%s]\n" fx)
         31 (printf "a + 1 -> a[%s]\n" fx) )   )

   
   (= (bit-and wdv 0xF) 4) ;; 3of8
   (let [n  (bit-shift-right (bit-and wdv 0x30)  4)
         pr (bit-shift-right (bit-and wdv 0x3c0) 6) ]
     (case n
         0 (printf "1 -> s%d\n" pr)    ;;sets(%d,1)
         1 (printf "if s%d = 0\n" pr)  ;;tests(%d)
         2 (printf "0 -> s%d\n" pr)    ;;sets(%d,0)  
         3 (printf "clear status\n" )  )   )  ;; clears

   
   (= (bit-and wdv 0xF) 12) ;; 4of8
   (let [n  (bit-shift-right (bit-and wdv 0x30)  4)
         pr (bit-shift-right (bit-and wdv 0x3c0) 6) ]
     (case n
         0 (printf "%d -> p\n" pr)   ;; setp(%d)
         1 (printf "p - 1 -> p\n" )  ;;decp
         2 (printf "if p # %d\n" pr) ;; testp(%d)
         3 (printf "p + 1 -> p\n" ) )  )  ;; incp

   
   (= (bit-and wdv 0x3f) 16)      ;; 5of8
   (let [n  (bit-shift-right (bit-and wdv 0x40) 6)
         pa (bit-shift-right (bit-and wdv 0x380) 7)  ]
     (case n
         0 (printf "select rom %d\n" pa)
         1 (printf "keys -> rom address\n")  )    )

   
   (= (bit-and wdv 0x3f) 24)       ;; 6of8
   (printf "load constant %d\n" (bit-shift-right (bit-and wdv 0x3c0) 6))

   
   (= (bit-and wdv 0x3ff) 0)       ;; 7of8
   (printf "no operation\n")
   
   (= (bit-and wdv 7) 0)           ;; 8of8
   (let [n  (bit-shift-right (bit-and wdv 0x3f8) 3)]
   	   (case n
   	   	     5 (printf "display toggle\n" ) ;;disptoggle
   	   	     6 (printf "return\n")     ;; ret
   	   	    21 (printf "c exchange m\n" )  ;; exchreg()BOGUS? TODO
   	   	    37 (printf "c -> stack\n") ;; cstack  pushC
                    53 (printf "stack -> a\n") ;; stacka popA
                    69 (printf "display off\n") ;; dispoff
                    78 (printf "c -> data address\n")
                    85 (printf "m -> c\n") ;;copyReg setreg(c,m, 0,13) TODO
                    94 (printf "c -> data\n")
                    95 (printf "data -> c\n")
                   101 (printf "down rotate\n") ;; downrot
                   117 (printf "clear registers\n" ) ;; clearregs clearAllRegs
   	   	   )   ) 
   ) ;; cond
  );;defn



(defn -main "" [& args]
  (doseq [i (range (count rom))]
;; ideally useful with new "minimal" trace facility!
    (printf "%1d.%03o  " (bit-shift-right i 8) (bit-and i 0377))
    (decode (rom i)))  )

(apply -main *command-line-args*)
