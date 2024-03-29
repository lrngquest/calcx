(ns calc.mach2
  (:gen-class))

(def ms (atom {:a [0 0 0  0 0 0  0 0 0  0 0 0  0 0 ]
               :b [0 0 0  0 0 0  0 0 0  0 0 0  0 0 ]
               :c [0 0 0  0 0 0  0 0 0  0 0 0  0 0 ]
               :d [0 0 0  0 0 0  0 0 0  0 0 0  0 0 ]
               :e [0 0 0  0 0 0  0 0 0  0 0 0  0 0 ]
               :f [0 0 0  0 0 0  0 0 0  0 0 0  0 0 ]
               :m [0 0 0  0 0 0  0 0 0  0 0 0  0 0 ]
               :s [0 0 0  0 0 0  0 0 0  0 0 0] ;; 12              
               :z [0 0 0  0 0 0  0 0 0  0 0 0  0 0] ;;constant all-zeroes
               :t [0 0 0  0 0 0  0 0 0  0 0 0  0 0]
               :p 0
               :pc 0         :ret 0    :offset 0
               :carry 0      :prev-carry 0
               :disp 0       :upTr 1  :cy 0   :lastkey 0
               :da 0
               :ram [
                     [0 0 0  0 0 0  0 0 0  0 0 0  0 0] ;; 0
                     [0 0 0  0 0 0  0 0 0  0 0 0  0 0]
                     [0 0 0  0 0 0  0 0 0  0 0 0  0 0]
                     [0 0 0  0 0 0  0 0 0  0 0 0  0 0]
                     [0 0 0  0 0 0  0 0 0  0 0 0  0 0]
                     [0 0 0  0 0 0  0 0 0  0 0 0  0 0]
                     [0 0 0  0 0 0  0 0 0  0 0 0  0 0]
                     [0 0 0  0 0 0  0 0 0  0 0 0  0 0]
                     [0 0 0  0 0 0  0 0 0  0 0 0  0 0] ;; 8
                     [0 0 0  0 0 0  0 0 0  0 0 0  0 0] ]
               })  )

(defn keyrom ""     []     (swap! ms assoc :pc (:lastkey @ms) :upTr 1)  )
(defn disptoggle "" []     (swap! ms assoc :disp  (- 1 (:disp @ms))  )  )
(defn dispoff ""    []     (swap! ms assoc :disp 0) )

(defn goto ""   [addr]
  (when (= 0 (:prev-carry @ms))  (swap! ms assoc  :pc  addr) )  )

(defn jsb   ""  [addr]     (swap! ms assoc  :ret (:pc @ms)  :pc addr) )
(defn retn  ""  []         (swap! ms assoc  :pc  (:ret @ms) ) )
(defn sets  ""  [num val]  (swap! ms assoc :s  (assoc (:s @ms)  num val) )  )

(defn tests  "" [num]  ;; :ksft unused; simplify
  (swap! ms assoc :carry ((:s @ms) num)  :upTr (if (= 0 num)  0  (:upTr @ms))) )

(defn setp   "" [val] (swap! ms assoc :p val))
(defn testp  "" [num] (swap! ms assoc :carry (if (= (:p @ms) num) 1 0) ) )
(defn incp   "" []    (swap! ms assoc :p (bit-and 15 (inc (:p @ms)))  ) )
(defn decp   "" []    (swap! ms assoc :p (bit-and 15 (dec (:p @ms)))  ) )
   ;; generalize to allow 8 roms -- i.e. rom# * 256
(defn setrom "" [num] (swap! ms assoc :offset (bit-shift-left num 8) ) )

(defn stacka "" []    (swap! ms assoc  :a (:d @ms)  :d (:e @ms)  :e (:f @ms))  )

(defn downrot "" []
  (swap! ms assoc  :c (:d @ms)  :d (:e @ms)  :e (:f @ms)  :f (:c @ms))   )

(defn clearregs "" []
  (let [z  (:z @ms)]
    (swap! ms assoc  :a z  :b z  :c z  :d z  :e z  :f z  :m z) )  )

(defn clears "" [] (swap! ms assoc  :s [0 0 0 0  0 0 0 0  0 0 0 0]) )
(defn cstack "" [] (swap! ms assoc  :f (:e @ms)  :e (:d @ms)  :d (:c @ms))  )


(defn field "internal fn" [val]   (if (= val -1)  (:p @ms)  val)  )
(defn nonz  "internal fn" [digit] (if (not= 0 digit) 1  0)  )

(defn ifregzero "if b[f] = 0" [rkaa first last]
  (let [lv   (field last)    fv  (field first)    rtaa  (rkaa @ms)
        rgsv (if (< lv 13)  (subvec rtaa fv (inc lv))  (subvec rtaa fv) )   ]
      (swap! ms assoc  :carry (reduce bit-or 0 (map nonz rgsv) )) )  )


(defn digitAdd "inner-fn  sum,cout" [aa bb cin]
  (let [res  (+ aa bb cin)]   (if (> res 9)  [(- res 10)  1]  [res  0] ))  )

(defn digitSub "inner-fn  dif,cout" [aa bb cin]
  (let [res  (- aa bb cin)]   (if (< res 0)  [(+ res 10)  1]  [res  0] ))  )

(defn arith3 "arith outer fn " [ opFn  rkdd rkaa rkbb first last ci] ;;backport!
  (let [lv  (field last)    rtaa (rkaa @ms)  rtbb (rkbb @ms)
        [vd mc]  (loop [rtdd  (rkdd @ms)    ci-co  ci    i (field first) ]
                   (if (<= i lv)
                     (let [[dgt dco]   (opFn (rtaa i) (rtbb i) ci-co) ]
                       (recur  (assoc rtdd i dgt)   dco   (inc i) ))
                     [rtdd  ci-co] )  )   ]  ;; else: exit
    (swap! ms assoc  rkdd vd  :carry mc )  )  )


(defn add "arity overloaded fn"
  ([rkdd rkaa rkbb first last]
     (add rkdd rkaa rkbb first last  0) )  ;; delegate to 6-param version
  ([rkdd rkaa rkbb first last ci]
     (arith3 digitAdd rkdd rkaa rkbb first last  ci) )    )

(defn sub "arity overloaded fn"
  ([rkdd rkaa rkbb first last]
     (sub rkdd rkaa rkbb first last  0) )
  ([rkdd rkaa rkbb first last ci]
     (arith3 digitSub rkdd rkaa rkbb first last  ci) )    )


;; opcodes implemented wi. (above)  add,sub  and optional carry-in
(defn increg "c15,31"  [rky first last]  (add rky rky :z first last  1) )
(defn decreg "c11,27"  [rky first last]  (sub rky rky :z first last  1) )
(defn negc   "c5"          [first last]  (sub :c :z :c first last) )
(defn negsubc "c7"         [first last]  (sub :c :z :c first last 1))
(defn regsgte "" [rkaa rkbb first last]  (sub :t rkaa rkbb first last) );;"c2,16


;; opcodes implemented wi.  subvec  to avoid iteration
(defn setreg "" [rkaa rkbb first last]
  (let [rtaa (rkaa @ms)  rtbb  (rkbb @ms)   lv (field last)  fv (field first)
        rslft  (subvec rtaa 0 fv)           ;; i.e. 0..fv-1  might be []
        rmddl  (subvec rtbb fv (inc lv))    ;; i.e. fv..lv
        rsrgt  (subvec rtaa (inc lv))    ]  ;; i.e. lv..13   might be []
    (swap! ms assoc  rkaa (vec (concat  rslft rmddl rsrgt)))  )  )

(defn exchreg "" [rkaa rkbb first last]
  (let [rtaa (rkaa @ms)  rtbb  (rkbb @ms)   lv (field last)  fv (field first)
        aalft  (subvec rtaa 0 fv)           bblft  (subvec rtbb 0 fv)
        aamdl  (subvec rtaa fv (inc lv))    bbmdl  (subvec rtbb fv (inc lv))
        aargt  (subvec rtaa (inc lv))       bbrgt  (subvec rtbb (inc lv))     ]
    (swap! ms assoc  rkaa (vec (concat aalft bbmdl aargt))
                     rkbb (vec (concat bblft aamdl bbrgt)))  )  )

(defn shiftr "" [rkaa first last]
  (let [rtaa (rkaa @ms)  fv (field first)  fvp1 (inc fv)  lvp1 (inc(field last))
        aalft  (subvec rtaa 0    fv)       ;;ndx    0..m-1  unchg, c/b []
        aamdl  (subvec rtaa fvp1 lvp1)     ;;ndx  m+1..n
        aargt  (subvec rtaa lvp1   )    ]  ;;ndx  n+1..13   unchg, c/b []
    (swap! ms assoc  rkaa (vec (concat aalft aamdl [0] aargt))) )  )

(defn shiftl "" [rkaa first last]
  (let [rtaa  (rkaa @ms)   fv  (field first)  lv  (field last)  lvp1 (inc lv)
        aalft  (subvec rtaa  0  fv)     ;;ndx    0..fv-1   c/b []
        aamdl  (subvec rtaa fv  lv)     ;;ndx   fv..lv-1
        aargh  (subvec rtaa lvp1  )  ]  ;;ndx fv+1..13     c/b []
     (swap! ms assoc  rkaa (vec (concat aalft [0] aamdl aargh))) )  )


(defn zeroreg "" [rky first last] (setreg rky :z first last))

(defn isz "internal fn" [digit] (if (= 0 digit) 1  0) )

(defn regsgte1 "if a[f] >= 1" [rkaa first last]
  (let [lv   (field last)    fv (field first)    rtaa  (rkaa @ms)
        rgsv (if (< lv 13)  (subvec rtaa fv (inc lv))  (subvec rtaa fv) )    ]
    (swap! ms assoc  :carry (reduce bit-and 1 (map isz rgsv)))    )  )

(defn loadconst "no-op reqd. if p >= 14 !" [num]
  (let [op  (:p @ms)    oc  (:c @ms)  ]
    (swap! ms assoc :c (if (< op 14) (assoc  oc op num)  oc)
           :p (bit-and 15 (dec op)) ) )  )

;; add 3 instrs for hp45
(defn cToDataAdr "" [] (swap! ms assoc :da  ((:c @ms) 12) )  )

(defn cToData "" []      ;; pres-val----v    ndx---v  nu-val-v
  (swap! ms assoc :ram  (assoc (:ram @ms)  (:da @ms)  (:c @ms)) ) )

(defn dataToC "" [] (swap! ms assoc :c ((:ram @ms) (:da @ms)) )  )


(defn decodEx "decode and execute instruction" [wdv]
  (cond  ;; chain of clauses for 8 decode "blocks"     
     (= (bit-and wdv 1) 1)           ;; block 1of8
     (let [n  (bit-shift-right (bit-and wdv 2) 1)
           pa (bit-shift-right (bit-and wdv 0x3fc) 2) ]
     (case n
       0 (jsb pa)
       1 (goto pa)  ) )

     (= (bit-and wdv 3) 2)           ;; block 2of8
     (let [n   (bit-shift-right (bit-and wdv 0x3e0) 5)
           pa  (bit-shift-right (bit-and wdv 0x1c)  2)
           [first last]  ([[-1 -1] [3 12] [0 2] [0 13]
                           [0 -1]  [3 13] [2 2] [13 13]]  pa)  ]
       (case n
         0  (ifregzero :b first last)  ;; if b[f] = 0  (f: p m x w wp ms xs s)
         1  (zeroreg :b first last)    ;; 0 -> b[f]
         2  (regsgte :a :c first last) ;; if a >= c[f]
         3  (regsgte1 :c first last)   ;; if c[f] >= 1
         4  (setreg :c :b first last ) ;; b -> c[f]
         5  (negc first last)          ;; 0 - c -> c[f]
         6  (zeroreg :c first last)    ;; 0 -> c[f]
         7  (negsubc first last)       ;; 0 - c -1 -> c[f]
         8  (shiftl :a first last)     ;; shift left a[f]
         9  (setreg :b :a first last)  ;; a -> b[f]
         10 (sub :c :a :c first last)  ;; a - c -> c[f]
         11 (decreg :c first last)     ;; c - 1 -> c[f]
         12 (setreg :a :c first last)  ;; c -> a[f]
         13 (ifregzero :c first last)  ;; if c[f] = 0
         14 (add :c :a :c first last)  ;; a + c -> c[f]
         15 (increg :c first last)     ;; c + 1 -> c[f]
         16 (regsgte :a :b first last) ;; if a >= b[f]
         17 (exchreg :b :c first last) ;; b exch c[f]
         18 (shiftr :c first last)     ;; shift right c[f]
         19 (regsgte1 :a first last)   ;; if a[f] >= 1
         20 (shiftr :b first last)     ;; shift right b[f]
         21 (add :c :c :c first last)  ;; c + c -> c[f]
         22 (shiftr :a first last)     ;; shift right a[f]
         23 (zeroreg :a first last)    ;; 0 -> a[f]
         24 (sub :a :a :b first last)  ;; a - b -> a[f]
         25 (exchreg :a :b first last) ;; a exch b[f]
         26 (sub :a :a :c first last)  ;; a - c -> a[f]
         27 (decreg :a first last)     ;; a - 1 -> a[f]
         28 (add :a :a :b first last)  ;; a + b -> a[f]
         29 (exchreg :a :c first last) ;; a exch c[f]
         30 (add :a :a :c first last)  ;; a + c -> a[f]
         31 (increg :a first last) ) ) ;; a + 1 -> a[f]   ;let for block 2of8

     (= (bit-and wdv 0xF) 4)         ;; block 3of8
     (let [n  (bit-shift-right (bit-and wdv 0x30)  4)
           pr (bit-shift-right (bit-and wdv 0x3c0) 6) ]
       (case n
         0 (sets pr 1)       ;;  "1 -> s%d"
         1 (tests pr)        ;; pr==0 ==> key_input "if s%d = 0\n"
         2 (sets pr 0)       ;;  "0 -> s%d\n"
         3 (clears )  )   )  ;; "clear status\n"

     (= (bit-and wdv 0xF) 12)        ;; block 4of8
     (let [n  (bit-shift-right (bit-and wdv 0x30)  4)
           pr (bit-shift-right (bit-and wdv 0x3c0) 6) ]
       (case n
         0 (setp pr)  ;; "%d -> p\n"
         1 (decp )    ;; "p - 1 -> p\n"
         2 (testp pr) ;; "if p # %d\n"
         3 (incp ) )  )  ;; "p + 1 -> p\n"

     (= (bit-and wdv 0x3f) 16)       ;; block 5of8
     (let [n  (bit-shift-right (bit-and wdv 0x40) 6)
           pa (bit-shift-right (bit-and wdv 0x380) 7)  ]
       (case n
         0 (setrom pa) ; "select rom %d\n"
         1 (keyrom ) )    )

     (= (bit-and wdv 0x3f) 24)       ;; block 6of8
     (loadconst (bit-shift-right (bit-and wdv 0x3c0) 6))

     (= (bit-and wdv 0x3ff) 0)  0    ;; block 7of8  A no-operation

     (= (bit-and wdv 7) 0)           ;; block 8of8
     (let [n  (bit-shift-right (bit-and wdv 0x3f8) 3)]
       (case n
         5   (disptoggle )
         6   (retn )
         21  (exchreg :c :m 0 13) ; "c exchange m"
         37  (cstack )    ;"c -> stack"
         53  (stacka )
         69  (dispoff )
         78  (cToDataAdr) ; "c -> data address"
         85  (setreg :c :m 0 13) ; "m ->c"
         94  (cToData)    ; "c -> data"
         95  (dataToC)    ; "data -> c"
         101 (downrot )
         117 (clearregs ) )   )     
     ) ;; cond

  (swap! ms assoc :cy (inc (:cy @ms)) ) ;; optional to count cycles
  )


(defn disp "" []
  (loop [i 13   dstr ""]
    (if (>= i 0)
      (recur
       (dec i)
       (let [ai ((:a @ms)i)   bi ((:b @ms)i)
             d1  (cond
                  (>= bi 8)              (str dstr " ")
                  (or (= i 2) (= i 13))  (str dstr (if (>= ai 8) "-" " "))
                  :else                  (str dstr ai))
             d2  (if (= bi 2)  (str d1 ".")  d1)  ]
         d2)  )
      
      dstr) )   )



(defn run-instrs "run from  offset+pc until wait-loop" [rom ]
  (loop []
    (let [pc (:pc @ms)  upTr (:upTr @ms)  ra (+ (:offset @ms) pc)]
      
      (swap! ms assoc :prev-carry (:carry @ms)  :carry 0
             :pc (bit-and (inc pc) 255) )
      
      (decodEx (rom  ra) )
                      ;; wait-loop detected ---v  ==> suspend at checkpoint
      (if (and (= upTr 1) (= 0 (:upTr @ms)))   0   (recur )) )  )  )


;; from  disb.clj  access rom via edn
;; (def rom (edn/read-string (slurp (cj/file "rom45.edn")) ) ) ;; rom[34]5.edn

(defn run-instr-seq "" [ksra rom]
  (swap! ms assoc  :lastkey  ksra   :s  (assoc (:s @ms)  0 1) )
  (run-instrs rom)
  (disp )  )

