import java.awt.*;
import java.awt.event.*;  // Mouse{Event,Listener}
import javax.swing.*;

// Ala  Cay Horstmann  v1ch08/MouseTest/MouseTest.java   "Core Java 8"


public class calcx {

    public static void main( String[] args) {
        mz.init( (args.length == 0) ?"m45" :args[0]);

        EventQueue.invokeLater( new Runnable() {
            public void run() {
               CalcxFrame frame = new CalcxFrame();
               frame.pack();
               frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
               frame.setVisible( true);
            }
         } );
    }
}


class CalcxFrame extends JFrame {

   public CalcxFrame() {
       setTitle( "calc X");

       CalcxComponent component = new CalcxComponent();
       add( component);  // to frame
       setFocusable( true); // helpful?
   }
}


// A component with mouse operations for  calculator "keys"

class CalcxComponent extends JComponent {

    FontMetrics keyfm = getFontMetrics( mx.keyfont),
        dspfm = getFontMetrics( mx.dspfont);

    public Dimension getPreferredSize() {  // override
        return new Dimension( mx.WINDOW_WD, mx.WINDOW_HT);
    }  // stackoverflow.com  10866762

    private void draw_string( Graphics2D g, String s,
                                   int[] xywh, int fg, int bg, FontMetrics fm) {
        int descent= fm.getMaxDescent(), sheight= fm.getHeight(),
            swidth= fm.stringWidth( s),
            rx= xywh[0], ry= xywh[1], wd= xywh[2], ht= xywh[3];
        g.setColor( mx.colors[ bg] );
        g.fillRect( rx, ry, wd, ht);
        g.setColor( mx.colors[ fg] );
        g.drawString( s, rx + ( wd-swidth)/2, ry + ( 2*ht-sheight)/2 + descent);
    }

    private void draw_legend( Graphics2D g, String lg, int[] rect) {
        if( lg.length() > 0) {  // when string not eq ""
            g.setFont( mx.lgdfont);  g.setColor( mx.colors[5]); // 5::Gold
            g.drawString( lg, rect[0], rect[1] - 4);  // rx ry, resp.
            g.setFont( mx.keyfont);  // restore as expected in  draw_calc loop
        }
    }
 
    private void draw_calc( Graphics2D g) {
        g.setColor( mx.colors[4] );  // 4::DkGr
        g.fillRect( 0, mx.DISPLAY_HT, mx.WINDOW_WD,  mx.WINDOW_HT - mx.DISPLAY_HT);
        g.setFont( mx.keyfont );
        for( int M=0; M < mx.krects.length; M++) {
            int[] fb = mz.clrsx[ M];  String kc = mz.kc_lgnd[M][0];
            draw_string( g, kc, mx.krects[M], fb[0], fb[1], keyfm);
            draw_legend( g, mz.kc_lgnd[M][1], mx.krects[M] );
        }
    }
    
    private void draw_disp( Graphics2D g, String s) {
        g.setFont( mx.dspfont);
        draw_string( g, s, dsp_rect, 8,7, dspfm);  // 8::BrRd  7::DkRd
    }

    public CalcxComponent() {
        setFocusable( true);  // unclear if this works!
        addMouseListener( new MouseHandler());
        //addKeyListener( new KeyHandler()); // ??
    }

   public void paintComponent( Graphics g) {
       Graphics2D g2 = (Graphics2D) g;
       draw_calc( g2);
       draw_disp( g2, mach2.run_instr_seq( aadr, mz.rom) );
   }

    // class vars decl;  Add as needed.
    int aadr;  int[] dsp_rect = new int[] {0,0, mx.WINDOW_WD, mx.DISPLAY_HT};

   private class MouseHandler extends MouseAdapter {

       private int find_key( int x, int y) {  // point in a key rectangle ?
           for( int M = 0; M < mx.krects.length; M++) {
               int[] kr= mx.krects[M];
               int rx= kr[0], ry= kr[1], wd= kr[2], ht= kr[3];

               if( (x >= rx) && (x < (rx+wd)) && (y >= ry) && (y < (ry + ht)) )
                   return M;
           }
           return -1; // ==> not found
       }

       public void mouseClicked( MouseEvent event) {
           int M = find_key( event.getX(), event.getY());
           if( M >= 0)  { aadr = mz.ad[ M];  repaint();  }
       }
   }

} // End CalcxComponent
