import java.awt.BorderLayout;
import java.awt.Panel;

import javax.swing.JFrame;

public class main {
  static String PATH;
 
  
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    String[] argument = null;
    if (args != null && args.length == 1) {
      argument = args;
    }
    DrawingApplet applet = new DrawingApplet(argument);
    JFrame frame = new JFrame("Harmonic Coordinates");
    frame.setSize(500, 700);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    //Creating panels
    Panel panel = new Panel(new BorderLayout());

    frame.add(panel);
    panel.add(applet, BorderLayout.CENTER);
    applet.init();
    frame.setVisible(true);
  }
}
  