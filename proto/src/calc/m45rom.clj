(ns calc.m45rom)

;; abbreviations map and rom  --  HP45

(def abmap { ;;   used by CLI  and also in GUI.
 \\ 06,      \l 04,   \^ 03,   \f 02,   \' 00,   ;; 1/x    ln  e^x FIX gold
 \w 056,     \p 054,  \s 053,  \c 052,  \t 050,  ;; x^2   ->p  SIN COS TAN  
 \a 016,     \r 014,  \[ 013,  \] 012,  \% 010,  ;; x<>y  RDN  STO RCL % (or??)
 \space 074, \z 073,  \e 072,  \; 070,           ;; ENTER CHS  EEX CLX
 \- 066,     \7 064,  \8 063,  \9 062,           ;; -    7     8    9 
 \+ 026,     \4 024,  \5 023,  \6 022,           ;; +    4     5    6
 \* 036,     \1 034,  \2 033,  \3 032,           ;; *    1     2    3
 \/ 046,     \0 044,  \. 043,  \o 042  }  )      ;; /    0     .    SIG+


(def rom [
 173 784 297 814 998 314 890 910 746 905 905 746 805 403 532 327
 541 942 398 11 656 541 942 528 144 212 99 296 206 624 0 760
 942 633 793 649 793 766 760 446 183 894 181 83 144 942 532 971
 254 969 84 179 660 423 235 144 398 144 942 398 506 283 426 283
 206 12 344 780 458 943 660 471 942 227 202 12 344 842 618 819
 1023 297 909 46 818 814 270 917 452 805 206 216 408 780 633 537
 503 490 482 527 206 140 280 942 7 144 452 276 463 686 478 614
 459 254 665 68 87 144 532 427 541 68 660 235 975 942 400 362
 362 452 354 660 635 651 676 452 292 388 680 446 571 260 510 583
 420 206 490 84 771 276 903 484 404 523 391 683 660 651 708 100
 46 663 708 100 144 144 708 254 100 164 46 1018 1018 506 506 74
 715 942 934 422 731 942 550 74 763 654 1002 14 763 735 144 1023
 404 903 484 276 523 511 424 296 48 708 144 144 550 654 1002 823
 750 396 48 724 867 686 452 627 144 780 847 724 999 708 468 815
 740 48 46 818 654 652 910 28 300 923 48 206 482 660 223 423
 84 507 942 206 354 624 206 752 503 212 963 660 1019 528 656 272
 574 975 814 193 760 942 193 760 942 596 51 942 340 107 126 71
 548 222 665 752 661 609 181 760 16 942 665 660 875 750 994 294
 934 362 658 442 135 722 490 151 718 654 752 558 283 558 268 891
 752 942 418 215 174 398 138 815 39 16 340 119 254 958 79 658
 894 255 510 818 466 814 302 850 259 942 760 590 958 814 274 752
 1022 1022 175 206 42 726 713 866 760 268 657 396 621 524 621 140
 536 652 621 569 621 817 270 621 142 813 817 686 660 471 596 471
 942 254 617 817 686 16 817 686 686 597 686 941 817 652 625 569
 524 629 140 536 396 625 268 625 625 814 590 844 344 1011 396 536
 408 344 152 280 600 84 875 48 750 994 16 272 270 662 558 647
 510 782 643 910 272 272 330 272 482 846 675 974 270 28 594 44
 679 215 482 790 715 918 278 28 44 719 215 28 918 879 16 378
 378 746 862 638 795 272 518 811 254 814 782 272 206 716 472 536
 344 216 600 536 88 408 216 344 16 48 16 906 891 354 510 44
 751 938 746 98 923 718 590 554 202 780 699 272 658 658 382 947
 466 786 562 142 894 955 958 760 942 30 11 270 946 752 658 382
 784 830 1022 598 274 75 424 665 398 532 267 750 838 3 718 382
 3 510 302 601 866 71 818 926 7 460 437 524 629 588 625 1017
 652 625 501 716 625 893 625 741 625 985 942 334 26 191 334 814
 28 270 108 195 942 446 227 230 490 716 789 596 27 340 595 985
 669 595 985 945 741 716 621 893 652 621 501 588 621 1017 524 621
 621 621 396 754 844 558 942 408 571 148 379 1002 634 779 790 359
 918 270 362 371 718 210 938 446 435 814 782 238 718 558 206 358
 148 475 280 486 487 408 108 471 590 590 148 595 48 460 216 216
 24 536 344 24 600 939 601 994 302 382 539 722 942 278 942 894
 547 814 994 817 144 722 894 599 766 910 48 144 718 558 643 910
 382 639 942 278 942 439 204 458 350 687 190 806 750 812 791 102
 731 84 3 656 879 0 562 934 144 548 408 600 216 88 280 472
 88 923 998 403 910 354 787 718 60 876 791 490 766 780 46 610
 859 270 362 622 831 206 298 910 638 799 934 398 46 780 491 588
 216 88 24 88 472 600 536 24 344 344 216 887 942 302 390 698
 379 506 718 490 971 415 206 780 152 216 24 152 344 519 332 507
 528 0 299 359 363 0 275 272 528 784 411 419 808 511 424 296
 507 255 1006 1006 1006 107 528 541 270 331 1006 1006 1006 48 528 208
 548 528 131 204 48 528 151 324 401 267 307 159 528 0 389 291
 0 16 1006 1006 1006 75 528 0 665 519 751 775 46 912 296 669
 519 389 612 199 389 750 994 295 528 528 612 95 401 580 324 199
 270 1006 270 168 938 168 459 0 0 548 580 389 132 31 541 323
 196 676 784 784 68 391 580 423 612 132 541 469 397 596 455 985
 507 752 468 255 511 12 610 1023 459 541 469 528 0 0 942 669
 452 533 981 859 254 676 547 718 414 548 595 506 516 340 587 490
 559 40 20 563 36 28 812 599 552 532 583 270 356 660 127 528
 0 210 370 218 906 671 206 52 398 780 298 394 442 711 170 378
 647 938 276 39 810 42 533 812 543 266 763 260 724 115 718 946
 795 718 276 531 946 250 398 442 815 218 170 844 278 362 638 947
 630 819 202 533 726 414 812 895 142 494 76 274 60 418 879 942
 236 919 202 388 795 404 931 28 658 793 28 490 2 939 708 726
 934 276 847 677 519 758 468 999 296 452 206 366 190 510 558 48
 0 531 371 803 355 784 389 363 611 267 548 67 131 16 389 1023
 389 55 523 523 523 656 0 419 727 0 523 523 523 0 0 443
 385 656 835 459 455 0 463 324 377 263 303 159 591 675 389 263
 0 0 1006 1006 451 656 0 431 389 983 1006 255 0 0 878 12
 315 400 385 548 808 296 362 362 532 655 647 377 400 400 270 60
 876 315 168 930 168 400 0 0 324 400 541 400 676 400 68 391
 196 644 784 784 784 644 196 399 388 260 471 420 260 471 388 467
 1006 784 784 420 292 148 511 676 400 676 656 0 559 0 400 389
 424 487 676 535 644 612 400 424 296 942 48 596 579 385 942 487
 405 752 571 377 424 196 676 311 385 808 296 665 275 0 0 656
 208 228 663 228 144 144 254 16 377 196 292 446 699 260 808 545
 614 215 210 482 213 100 541 398 657 752 813 401 657 760 669 890
 890 890 791 378 965 398 261 967 389 132 23 206 624 0 760 48
 596 847 639 385 87 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 16 0 0 0 656 0 0 0 0 0 0 0 0 48 784
 784 482 482 482 482 482 334 624 0 760 276 1023 942 532 671 667
 400 0 0 0 0 915 260 676 657 9 749 181 398 13 143 0
 0 0 267 749 201 17 749 206 482 398 5 749 67 206 624 0
 760 48 808 296 398 48 548 612 661 0 0 0 0 0 0 0
 0 400 676 292 13 398 657 5 126 3 661 9 942 665 296 5
 942 206 482 665 424 661 261 296 13 398 5 645 292 468 387 296
 398 17 296 398 13 507 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 749 942 400 0 0 0 881 596 471 400 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 371 228 663 228 144 144 254 16 0 0 0 0 0 0 0 0
 210 370 218 272 707 210 370 218 906 751 206 398 780 298 394 442
 787 170 378 727 950 760 942 752 48 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 276 903 404 667
 671 404 663 659 206 382 140 152 168 206 750 780 354 624 942 296
 752 942 482 482 947 507 206 408 939 0 0 0 0 0 0 48
 755 31 52 511 506 505 977 206 52 324 398 680 942 103 726 1002
 59 844 826 314 28 172 183 750 874 46 516 76 314 390 278 610
 71 548 908 394 122 59 28 172 171 95 874 155 75 890 83 290
 28 754 394 918 235 726 1022 1002 532 239 60 726 54 890 634 263
 95 1018 814 994 994 278 890 279 754 882 726 814 532 343 938 42
 442 339 170 378 938 340 1019 515 0 0 0 0 0 0 0 0
 0 0 0 723 659 398 206 354 624 942 398 1007 0 0 0 0
 0 0 519 473 52 144 468 487 296 206 48 0 676 401 400 101
 400 270 270 473 890 543 499 890 567 152 344 280 507 890 615 280
 344 216 344 600 152 216 472 362 505 216 472 536 344 280 88 88
 472 536 280 507 12 270 60 812 663 766 942 624 164 760 942 740
 46 84 1007 407 302 942 206 624 142 942 752 403 126 31 122 31
 106 787 28 815 28 236 803 19 362 769 272 114 31 938 716 426
 855 362 106 19 270 942 750 994 174 811 942 590 510 780 310 974
 895 846 270 974 911 1022 814 985 716 985 142 50 654 814 918 887
 942 554 490 809 548 272 2 1003 658 1002 48 660 1019 528 400 0
 23 651 23 23 23 55 23 71 23 671 23 23 23 659 23 600
 591 663 195 127 280 503 23 536 591 675 491 499 88 503 23 344
 591 159 579 839 24 503 23 39 23 183 23 23 23 191 23 223
 408 591 63 95 472 503 23 1015 750 53 467 571 459 400 596 247
 52 126 1019 362 362 442 1019 295 582 490 291 942 680 88 216 70
 1019 408 652 66 1019 524 722 460 408 460 66 1019 168 332 722 722
 722 722 814 882 746 524 866 716 994 994 894 814 36 165 165 20
 563 667 644 659 10 547 42 31 0 0 216 591 152 591 400 20
 7 532 435 552 548 780 206 208 810 874 810 663 516 135 660 459
 676 624 603 624 660 623 942 398 582 752 667 760 780 418 643 206
 942 278 181 189 189 189 189 221 680 552 40 660 511 76 1010 511
 396 994 651 460 994 66 739 655 754 588 994 55 652 994 66 775
 663 210 754 716 994 811 750 780 994 671 70 823 39 750 716 994
 103 680 210 140 10 867 280 871 408 168 206 780 46 624 558 760
 98 915 941 752 558 482 887 710 942 941 507 434 1015 942 270 524
 274 396 274 274 274 780 1002 610 1011 874 262 991 942 48 206 301 ]  )

