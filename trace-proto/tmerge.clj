#!/usr/bin/env bb
(ns tmerge)

;;  diff-23/src/l_diff/core.clj  file->vec
(defn file->vec [fn]  ;;read contents of file one line at a time
  (with-open [rdr  (clojure.java.io/reader fn)]
    (reduce conj [] (line-seq rdr)) ) )


(defn conv-addr "rom.page-loc to linear-address" [a]
  (let [[rom pgloc]  (clojure.string/split a  #"\.")  ]
    (+ (bit-shift-left (read-string rom) 8) (read-string (str "0" pgloc)) ) ) )


(defn ptlbl "" []
  (let [rom-lines  (file->vec "x35.txt") ;;s/b var !        
        ]
    
    (with-open [r (clojure.java.io/reader "t3p.txt")] ;; e.g. t2py.txt
      (doseq [line (line-seq r)]
        (if (nil? (clojure.string/index-of line "(display)"))
          (let [lin-adr   (conv-addr (subs line 0 5))  ]
            (println (rom-lines lin-adr))
            (when (> (count line) 5)  (println (subs line 5))  )
            )
          
          (println line)  ;just print display and continue          
          )
        ))
    ))

(defn -main "" [& args]
  (ptlbl)
  )

(apply -main *command-line-args*)

