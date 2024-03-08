# Calcx

An extension and backport of the Clojure project [calc35](https://github.com/lrngquest/calc35).
The micro machine has been extended with 10 "storage" locations and
three instructions required for HP-45, then backported to Java.

A new Java GUI was implemented.

## Credits

The GUI code is based on example `v1ch08/MouseTest/MouseTest.java` from "Core Java Volume 1 - Fundamentals Eighth Edition".

 Core Java. Volume 1, Fundamentals/ Cay S. Horstmann, Gary Cornell - 8th ed.
 Copyright 2008 Sun Microsystems, Inc.
 (There is no specific copyright notice in individual example files.)
 

The instruction decoder logic is re-based on `HP1973-Source/HP1973.py` at:

 ```
 https://sarahkmarr.com/retrohp1973.html
 ```

 `HP1973-Source/HP1973.py`
 by Sarah Libman
 version 1.00.01
 ideally, downloaded from sarahkmarr.com
 made available under a "you know, just be nice about it: use it, play
 with it, tweak it, but don't pretend you wrote it, don't host it
 elsewhere and don't try to make money from it" licence


Hat tip to https://Dillinger.io for markdown edit/preview.

## Usage

The default is HP-45 microcode and GUI:

 ```
 $ java -jar calcx.jar
 ```
or, for HP-35 microcode and GUI

 ```
 $ java -jar calcx.jar m35
 ```

A window with the calculator GUI will appear. Operate by "clicking" on the key-caps
of the GUI and observing the results on the display portion.
Exit the calculator app by closing its window.

Try an approximation to PI and compare result to internally provided value:

 ```
103993 ENTER
33102 /
FIX 9
GOLD .
-
 ```

### Limitations
Unlike `calc35` no single-character keyboard shortcuts are supported.

### Bugs
None known.

## License

Other than noted above:

Copyright © 2019-2024 L. E. Vandergriff

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
 ```
"Freely you have received, freely give." Mt. 10:8
