import java.awt.Color;

import processing.core.PApplet;
import Jama.Matrix;
import Jcg.geometry.Point_2;


public class Util {
  final static Color DEFAULT = new Color(255, 255, 255);
  final static Color RED = new Color(255, 0, 0);
  final static Color BLUE = new Color(0, 0, 255);
  final static Color GREEN = new Color(0, 155, 0);
  final static Color PURPLE = new Color(250, 12, 255);
  
  public static double epsilon = 20d;
  public static double cage_precision = 0.1d;
  
  public static boolean closePoints(Point_2 p1, Point_2 p2) {
    return ((Double) p1.squareDistance(p2) < epsilon);
  }
  
  public static void changeColor(PApplet frame, Color c) {
    frame.fill(c.getRGB());
    frame.stroke(c.getRGB());
  }
  
  public static boolean segmentsIntersect(Point_2 A, Point_2 B, Point_2 P, Point_2 Q) {
    double[][] coeffA = {{B.x - A.x, P.x - Q.x}, {B.y - A.y, P.y - Q.y}};
    Matrix matrixA = new Matrix(coeffA);
    double[][] coeffB = {{P.x - A.x}, {P.y - A.y}};
    Matrix matrixB = new Matrix(coeffB);
    if (matrixA.det() != 0) {
      Matrix result = matrixA.solve(matrixB);
      if(result.get(0, 0)>= 0 && result.get(0, 0) <=1 && result.get(1, 0) <=1 && result.get(1, 0) >=0) {
        return true;
      }
    }
    return false;
  }
}
