import java.awt.Font;
import java.awt.Color;

public class mx {

    public static int[][] krects = new int[][] {  // rx,ry, wd, ht (pixels)
{14,100,30,24},{62,100,30,24}, {110,100,30,24},{158,100,30,24}, {206,100,30,24},
{14,148,30,24},{62,148,30,24}, {110,148,30,24},{158,148,30,24}, {206,148,30,24},
{14,196,30,24},{62,196,30,24}, {110,196,30,24},{158,196,30,24}, {206,196,30,24},

{14,244,78,24},{110,244,30,24}, {158,244,30,24}, {206,244,30,24},
{14,292,24,24}, {63,292,37,24}, {131,292,37,24}, {199,292,37,24},
{14,340,24,24}, {63,340,37,24}, {131,340,37,24}, {199,340,37,24},
{14,388,24,24}, {63,388,37,24}, {131,388,37,24}, {199,388,37,24},
{14,436,24,24}, {63,436,37,24}, {131,436,37,24}, {199,436,37,24} };


    public static Color[] colors = {  // see  sim35.java
            new Color(0x00,0x00,0x00), new Color(0xff,0xff,0xff),   //Blk,Wht  
            new Color(0xa0,0xa0,0xa0), new Color(0x58,0x58,0x58),   //LtGr,MdGr 
            new Color(0x38,0x38,0x38), new Color(0xff,0xd7,0x00),   //DkGr,Gold 
            new Color(0x40,0x40,0xff), new Color(0x50,0x00,0x00),   //Blu,DkRd 
            new Color(0xff,0x40,0x00), new Color(0xc0,0xc8,0xd8) }; //BrRd,VltGr


    //Need to put others here:  keyfont, keyfm  -- results of init2()

    public static Font keyfont = new Font( "Helvetica", Font.BOLD, 14);
    public static Font dspfont = new Font( "Helvetica", Font.BOLD, 24);
    public static Font lgdfont = new Font( "Helvetica", Font.BOLD, 12);

    public static  int WINDOW_WD = 255,  WINDOW_HT = 484, DISPLAY_HT = 52;
}
