import java.util.Arrays;
import java.util.HashMap;  //modify mach1.java  ala  mach2.clj
import java.io.*;


public class mach2 {  // backport  mach1.clj  2022 July 2..25 
    public static  HashMap<String,int[]> ms = new HashMap<>(10); //machine state
    public static  HashMap<String,Integer> mk = new HashMap<>(10);  //ditto
    static {
        ms.put("a", new int[14]);   ms.put("b", new int[14]);
        ms.put("c", new int[14]);   ms.put("d", new int[14]);
        ms.put("e", new int[14]);   ms.put("f", new int[14]);
        ms.put("m", new int[14]);   ms.put("s", new int[12]);
        ms.put("z", new int[14]); // convenience ??
        ms.put("t", new int[14]); // temporary
        mk.put("p",0);  mk.put("pc",0);  mk.put("ret", 0);  mk.put("offset",0);
        mk.put("carry",0);  mk.put("prev-carry",0);  mk.put("disp",0);
        mk.put("da",0); mk.put("upTr",1); mk.put("cy",0); mk.put("lastkey",0); }

    private static int [][]Ram = new int[10][14];

    static void keyrom() { mk.put("pc", mk.get("lastkey"));  mk.put("upTr", 1);}
    static void disptoggle()  { mk.put( "disp", (1 - mk.get("disp")) ); }
    static void dispoff() { mk.put( "disp", 0); } 
    static void go_to( int addr) {
        if( mk.get( "prev-carry") == 0)   mk.put( "pc", addr); }
    static void jsb( int addr) {
        mk.put( "ret", mk.get( "pc"));   mk.put( "pc", addr); }
    static void retn() { mk.put("pc", mk.get("ret")); }
    static void sets(int num, int val) {
        int[]s = ms.get("s");  s[num] = val;  ms.put("s", s);  }
    static void tests( int num) {
        mk.put("carry", (ms.get("s"))[num] ); if (num == 0)  mk.put("upTr", 0);}
    static void setp(  int val)  { mk.put("p", val); }
    static void testp( int num) { mk.put( "carry", (mk.get("p")== num) ?1 :0); }
    static void incp()  { mk.put("p", (mk.get("p") + 1) & 15);  }
    static void decp()  { mk.put("p", (mk.get("p") - 1) & 15);  }
    static void setrom( int num) { mk.put("offset", num << 8); } // 8 x 256 roms

    static void stacka() {
        int[]aa= new int[14], dd= new int[14],  ee= new int[14];
        System.arraycopy( ms.get("d"),0, aa,0, 14);
        System.arraycopy( ms.get("e"),0, dd,0, 14);
        System.arraycopy( ms.get("f"),0, ee,0, 14);
        ms.put("a",aa);  ms.put("d",dd);  ms.put("e",ee);    }

    static void downrot() {
      int[] cc= ms.get("d"), dd=ms.get("e"), ee=  ms.get("f"), ff=  ms.get("c");
        ms.put("c",cc);  ms.put("d",dd);  ms.put("e",ee);  ms.put("f",ff);     }

    static void clearregs() {
      ms.put("a",new int[14]); ms.put("b",new int[14]); ms.put("c",new int[14]);
      ms.put("d",new int[14]); ms.put("e",new int[14]); ms.put("f",new int[14]);
        ms.put( "m", new int[14] );    }

    static void clears() {  ms.put( "s", new int[12] );  }

    static void cstack() {
        int[]ff= new int[14], ee= new int[14],  dd= new int[14];
        System.arraycopy( ms.get("e"),0, ff,0, 14);
        System.arraycopy( ms.get("d"),0, ee,0, 14);
        System.arraycopy( ms.get("c"),0, dd,0, 14);
        ms.put("f",ff);  ms.put("e",ee);  ms.put("d",dd);    }

    static int field( int val)  { return val == -1  ? mk.get("p")  :val;  }

    static void ifregzero( String rkaa, int first, int last) {
        int[]  rtaa =  ms.get( rkaa);   int lv = field( last), vcy = 0;
        for( int i = field( first); i <= lv; i++)  vcy |= (rtaa[i] != 0)  ?1 :0;
        mk.put( "carry", vcy);     }


    static int[] digitAdd( int aa, int bb, int cin) {
        int[] sumCo = new int[]{ aa + bb + cin,  0};
        if (sumCo[0] > 9)  { sumCo[0] -= 10; sumCo[1] = 1;}   return sumCo;   }

    static void add( String rkdd, String rkaa, String rkbb, //add6
                      int first, int last, int ci) {
        int[] rtdd = ms.get( rkdd),  rtaa = ms.get( rkaa),  rtbb= ms.get( rkbb);
        int ciCo = ci,  i = field( first), lv = field( last);
        int[] dgtDco = new int[2];
        while( i <= lv) {
            dgtDco = digitAdd( rtaa[i], rtbb[i], ciCo);
            rtdd[ i]= dgtDco[ 0];  ciCo = dgtDco[ 1];   i++;
        }  mk.put("carry", dgtDco[1]);  ms.put( rkdd, rtdd);   }

    static void add( String rkdd, String rkaa, String rkbb, //add5
                      int fv, int lv) {  add( rkdd,rkaa,rkbb,fv,lv, 0);  }
 

   static int[] digitSub( int aa, int bb, int cin) {
       int[] diffCo = new int[]{ (aa - bb) - cin,  0};
       if (diffCo[0] < 0)  { diffCo[0] += 10; diffCo[1] = 1;}  return diffCo;  }

    static void sub( String rkdd, String rkaa, String rkbb,
                      int first, int last, int ci) {
        int[] rtdd = ms.get( rkdd), rtaa =  ms.get( rkaa), rtbb= ms.get( rkbb);
        int ciCo = ci,  i = field( first), lv = field( last);
        int[] dgtDco = new int[2];
        while( i <= lv) {
            dgtDco = digitSub( rtaa[i], rtbb[i], ciCo);  // s/Add/Sub/ !!
            rtdd[ i]= dgtDco[ 0];  ciCo = dgtDco[ 1];   i++;
        }  mk.put("carry", dgtDco[1]);  ms.put( rkdd, rtdd);  }

    static void sub( String rkdd, String rkaa, String rkbb,
                      int fv, int lv) {  sub( rkdd,rkaa,rkbb,fv,lv, 0);  }

         // opcodes implemented wi. (above)  add,sub  and optional carry-in
    static void increg( String rky, int first, int last)
       { add( rky, rky, "z", first, last,  1); }
    static void decreg( String rky, int first, int last)
       { sub( rky, rky, "z", first, last,  1); }
    static void negc( int first, int last)
       { sub( "c", "z", "c", first, last); }
    static void negsubc( int first, int last)
       { sub( "c", "z", "c", first, last, 1); }
    static void regsgte( String rkaa, String rkbb, int first, int last)
       { sub( "t", rkaa, rkbb, first, last); } //only for effect setting "carry"


    static void setreg( String rkaa, String rkbb, int first, int last) {
        int[] rtaa = ms.get( rkaa),  rtbb =  ms.get( rkbb);
        int fv = field( first),  lv = field( last);
        System.arraycopy( rtbb,fv,  rtaa,fv,  lv - fv +1);  ms.put(rkaa,rtaa); }

    static void exchreg( String rkaa, String rkbb, int first, int last) {
        int[] rtaa = ms.get( rkaa),  rtbb = ms.get( rkbb),   stsh = new int[14];
        int fv = field( first),  lv = field( last);
        System.arraycopy( rtbb, fv,  stsh, fv,  lv - fv +1);
        System.arraycopy( rtaa,fv,  rtbb,fv,  lv - fv +1);  ms.put(rkbb,rtbb);
        System.arraycopy( stsh,fv,  rtaa,fv,  lv - fv +1);  ms.put(rkaa,rtaa); }

    static void shiftr( String rkaa, int first, int last) {
        int[] rtaa = ms.get( rkaa),  aamdl = new int[14];
        int fv = field( first),  lv = field( last);
        System.arraycopy( rtaa, fv+1,  aamdl, fv,  lv - fv);
        aamdl[ lv] = 0;
        System.arraycopy( aamdl,fv,  rtaa,fv,  lv - fv+1); ms.put(rkaa,rtaa); }

    static void shiftl( String rkaa, int first, int last)  {
        int[] rtaa = ms.get( rkaa),  aamdl = new int[14];
        int fv = field( first),  lv = field( last);
        System.arraycopy( rtaa, fv,  aamdl, fv + 1,  lv - fv );
        aamdl[ fv] = 0;
        System.arraycopy( aamdl,fv, rtaa,fv, lv -fv+1 );  ms.put(rkaa,rtaa);  }

    static void zeroreg( String rky, int first, int last)  {
        setreg( rky, "z", first, last); }

    static void regsgte1( String rkaa, int first, int last) {
        int[]  rtaa = ms.get( rkaa);   int lv = field( last),  vcy = 1;
        for( int i = field( first); i <= lv; i++)  vcy &= (rtaa[i] == 0)  ?1 :0;
        mk.put( "carry", vcy);    }

    static void loadconst( int num) {
        int op = mk.get("p");  int[]c = ms.get( "c");
        if( op < 14)  {  c[ op] = num;  ms.put("c", c); }
        mk.put( "p", (--op) & 15);    }

    // 3 new machine instrs for hp45
    private static void cToDataAddr() { mk.put("da", (ms.get("c"))[12] ); }
    private static void cToData() {
        int[] rtaa = new int[14];
        System.arraycopy( ms.get("c"),0, rtaa,0, 14);
        Ram[ mk.get("da")] = rtaa;    }
    private static void dataToC() {
        int[] rtaa = new int[14];
        System.arraycopy( Ram[ mk.get("da")], 0, rtaa,0, 14);
        ms.put("c", rtaa);    }

    static void noop() {};

    private static int[][] flar = new int[][] {
        {-1,-1}, {3, 12}, {0, 2}, {0, 13},  {0,-1}, {3, 13}, {2, 2}, {13, 13}};

    public static void decodEx( int wdv) {  // ala Sarah Marr
        if(  (wdv & 1) == 1) {                    //block 1of8
            int pa = (wdv & 0x3fc) >> 2;
            switch( (wdv & 2) >> 1) {
            case 0:  jsb(   pa); break;
            case 1:  go_to( pa); break;  }
        } else if( (wdv & 3) == 2) {              // block 2of8
            int pa = (wdv & 0x1c) >> 2,  first= flar[pa][0],  last= flar[pa][1];
            switch( (wdv & 0x3e0) >> 5) {
            case  0:  ifregzero( "b", first, last);    break;  // if  b[f] = 0
            case  1:  zeroreg( "b", first, last);      break;  // 0 -> b[f]
            case  2:  regsgte( "a", "c", first,last);  break;  // if a >= c[f]
            case  3:  regsgte1( "c", first, last);     break;  // if c[f] >= 1
            case  4:  setreg( "c", "b", first, last);  break;  // b -> c[f]
            case  5:  negc( first, last);              break;  // 0 - c -> c[f]
            case  6:  zeroreg( "c", first, last);      break;  // 0 -> c[f]
            case  7:  negsubc( first, last);           break;// 0 - c -1 -> c[f]
            case  8:  shiftl( "a", first, last);       break; // shift left a[f]
            case  9:  setreg( "b", "a", first, last);  break;  // a -> b[f]
            case 10:  sub( "c", "a", "c", first,last); break;  // a - c -> c[f]
            case 11:  decreg( "c", first, last);       break;  // c - 1 -> c[f]
            case 12:  setreg( "a", "c", first, last);  break;  // c -> a[f]
            case 13:  ifregzero( "c", first, last);    break;  // if c[f] = 0
            case 14:  add( "c", "a", "c", first,last); break;  // a + c -> c[f]
            case 15:  increg( "c", first, last);       break;  // c + 1 -> c[f]
            case 16:  regsgte( "a", "b", first, last); break;  // if a >= b[f]
            case 17:  exchreg( "b", "c", first, last); break;  // b exch c[f]
            case 18:  shiftr( "c", first, last);       break;// shift right c[f]
            case 19:  regsgte1( "a", first, last);     break;  // if a[f] >= 1
            case 20:  shiftr( "b", first, last);       break;// shift right b[f]
            case 21:  add( "c", "c", "c", first,last); break;  // c + c -> c[f]
            case 22:  shiftr( "a", first, last);       break;// shift right a[f]
            case 23:  zeroreg( "a", first, last);      break;  // 0 -> a[f]
            case 24:  sub( "a", "a", "b", first,last); break;  // a - b -> a[f]
            case 25:  exchreg( "a", "b", first, last); break;  // a exch b[f]
            case 26:  sub( "a", "a", "c", first,last); break;  // a - c -> a[f]
            case 27:  decreg( "a", first, last);       break;  // a - 1 -> a[f]
            case 28:  add( "a","a","b", first, last);  break;  // a + b -> a[f]
            case 29:  exchreg( "a","c", first, last);  break;  // a exch c{f]
            case 30:  add( "a","a","c", first, last);  break;  // a + c -> a[f]
            case 31:  increg( "a", first, last);       break; } //a + 1 ->a[f]
        } else if( (wdv & 0xF) == 4) {            // block 3of8
            int pr = (wdv & 0x3c0) >> 6;
            switch ( (wdv & 0x30) >> 4) {
            case 0:  sets(  pr,1);  break;   // "1 -> s%d"
            case 1:  tests( pr);    break;   // pr==0 ==> key_input "if s%d = 0"
            case 2:  sets(  pr,0);  break;   // 0 -> s%d"
            case 3:  clears();      break; } // "clear status"
        } else if( (wdv & 0xF) ==12) {            // block 4of8
            int pr = (wdv & 0x3c0) >> 6;
            switch( (wdv & 0x30) >> 4) {
            case 0:  setp( pr);    break;   // "%d -> p"
            case 1:  decp();       break;   // "p - 1 -> p"
            case 2:  testp( pr);   break;   // "if p # %d"
            case 3:  incp();       break; } // "p + 1 -> p"
        } else if( (wdv & 0x3f) ==16) {           // block 5of8
            int pa = (wdv & 0x380) >> 7;
            switch( (wdv & 0x40) >> 6) {
            case 0:  setrom( pa);  break;
            case 1:  keyrom();     break;    }
        } else if( (wdv & 0x3f) == 24) {          // block 6of8
            loadconst( (wdv & 0x3c0) >> 6);
        } else if( (wdv & 0x3ff) == 0)    noop(); // block 7of8
        else if ( (wdv & 7) == 0) {               // block 8of8
            switch( (wdv & 0x3f8) >> 3) {
            case   5:  disptoggle();  break;
            case   6:  retn();        break;
            case  21:  exchreg( "c", "m", 0,13);  break;
            case  37:  cstack();      break;
            case  53:  stacka();      break;
            case  69:  dispoff();     break;
            case  78:  cToDataAddr(); break;
            case  85:  setreg( "c", "m", 0,13);   break;
            case  94:  cToData();     break;
            case  95:  dataToC();     break;
            case 101:  downrot();     break;
            case 117:  clearregs();   break;   }
        }
        mk.put("cy", mk.get("cy") + 1);    }


    public static String disp() {
        String dstr = new String();   int[] a = ms.get("a"),  b = ms.get("b");
        for( int i=13; i>=0; i--) {
            if( b[i] >= 8)             dstr += " ";
            else if( i==2 || i==13)    dstr += ( a[i] >= 8) ?"-" :" " ;
            else                       dstr += Integer.toString( a[i]);

            if( b[i]==2 )  dstr += ".";
        }
        return dstr;    }

 
    public static void run_instrs( int[] rom) {
        while( true) {
 int pc= mk.get("pc"),  upTr= mk.get("upTr"),  ra= mk.get("offset") + pc;

            mk.put("prev-carry", mk.get("carry"));   mk.put("carry", 0);
            mk.put("pc", ++pc & 255);
 
            decodEx( rom[ ra]);  // run a single instruction

            if( upTr == 1  &&  (mk.get("upTr")) == 0)  break;  //At checkpt
        }    }


    public static String run_instr_seq( int ksra, int[] mrom) {
        mk.put("lastkey", ksra); int[] s= ms.get("s");  s[0]=1;  ms.put("s", s);
        run_instrs( mrom);  // resume from checkpt; run key seq. to checkpt
        return disp();    }

 
    public static void main( String[] args) {  // cli:  allows rom choice !
        String lastdisp = "";

        int[] xrom;   HashMap<Character,Integer> xabm;
        String Arg = (args.length == 0) ?"m45" :args[0] ; 
        if( Arg.equals ("m45")) { xrom = m45.rom;  xabm = m45.abmap; }
        else                    { xrom = m35.rom;  xabm = m35.abmap; }
        System.out.printf( "xrom.length %d\n", xrom.length);

        mach2.run_instrs( xrom ); // power on seq.
        System.out.println( mach2.disp());

        try (
     BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while( true) {
                String vch = br.readLine();
                if( vch == null)  break;

                for( int i=0; i<vch.length(); i++)
                    lastdisp= mach2.run_instr_seq(
                                      xabm.get( vch.charAt(i)),  xrom );
                System.out.printf("%s\n", lastdisp);
            }
            } catch( Exception e) {}
    }
}
