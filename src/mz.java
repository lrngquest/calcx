import java.util.HashMap;

public class mz {  // flex gui to support  HP45  or  HP35

    public static  int[] rom, ad;
    public static  String[][] kc_lgnd;
    public static  int[][] clrsx;
    public static  HashMap<Character,Integer> abmap;


    public static void init( String ms) {
        if( ms.equals ("m45"))  {
            rom = m45.rom;  abmap = m45.abmap;
            ad = m45.ad;  kc_lgnd = m45.kc_lgnd;  clrsx = m45.clrsx;
        } else if ( ms.equals("m35")) {
            rom = m35.rom;  abmap = m35.abmap;
            ad = m35.ad;  kc_lgnd = m35.kc_lgnd;  clrsx = m35.clrsx;
        }
    }
}
