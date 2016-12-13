import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import processing.core.PApplet;
import Jama.Matrix;
import Jcg.geometry.Point_2;
import Jcg.graph.Node;

public class Cage {
  
  protected PApplet frame;
  protected LinkedList<Point_2> points;
  protected float maxX, maxY, minX, minY;
  protected Grid grid;
  protected AnimatableObject object;
  
  public Cage(PApplet frame, AnimatableObject object) {
    this.frame = frame;
    this.points = new LinkedList<Point_2>();
    this.grid = new Grid(this);
    this.object = object;
    maxX = Float.MIN_VALUE;
    maxY = Float.MIN_VALUE;
    minX = Float.MAX_VALUE;
    minY = Float.MAX_VALUE;
  }
  
  
  public void reset() {
    this.points.clear();
    this.grid = new Grid(this);
    maxX = Float.MIN_VALUE;
    maxY = Float.MIN_VALUE;
    minX = Float.MAX_VALUE;
    minY = Float.MAX_VALUE;
  }
  
  public void moveBoundObject() {
    for (int i = 0; i < this.object.vertices.size(); i++) {
      this.object.movePoint(i, this.grid.computeNewPosition(this.object.getInitialPoint(i)));
    }
  }
  
  public void drawCage(int currentIndex) {
    int N = this.points.size();
    for (int i = 0; i <N; i++) {
      Point_2 p1 = this.points.get(i % N);
      Point_2 p2 = this.points.get((i + 1) % N);
      if (currentIndex == i) {
        Util.changeColor(this.frame, Util.RED);
      }
      this.frame.ellipse((float)(double)p1.x, (float)(double)p1.y, 10, 10);
      Util.changeColor(frame, Util.DEFAULT);
      this.frame.line((float)(double)p1.x, (float)(double)p1.y, (float)(double)p2.x, (float)(double)p2.y);
    }
  }
  
  public void computeHarmonicCoordinates() {
    this.grid = new Grid(this);
    this.grid.labelGrid();
    this.grid.computeHarmonics();
    //this.grid.computeHarmonicsSequential();
  }
  
  public void colorGrid() {
    if (this.grid == null) return;
    for (int i = 0; i < grid.widthOfGrid; i++) {
      for (int j = 0; j < grid.heightOfGrid; j++) {
        switch(this.grid.nature[i][j]) {
          case EXTERIOR:
            Util.changeColor(this.frame, Util.RED);
            this.frame.ellipse(i * Grid.GRID_STEP + this.grid.minX,j * Grid.GRID_STEP + this.grid.minY, 2, 2);
            Util.changeColor(this.frame, Util.DEFAULT);
            break;
          case BOUNDARY:
            Util.changeColor(this.frame, Util.BLUE);
            this.frame.ellipse(i * Grid.GRID_STEP + this.grid.minX,j * Grid.GRID_STEP + this.grid.minY, 2, 2);
            Util.changeColor(this.frame, Util.DEFAULT);
            break;
          case INTERIOR:
            Util.changeColor(this.frame, Util.GREEN);
            this.frame.ellipse(i * Grid.GRID_STEP + this.grid.minX,j * Grid.GRID_STEP + this.grid.minY, 2, 2);
            Util.changeColor(this.frame, Util.DEFAULT);
        }
      }
    }
  }
 
  public void colorLaplace(int k) {
    if (this.points.size() == 0) return;
      for (int i = 0; i < this.grid.widthOfGrid; i++) {
        for (int j = 0; j < this.grid.heightOfGrid; j++) {
          if(this.grid.nature[i][j] == Grid.LABELS.INTERIOR){
          int v = (int) ((1.-Math.pow(this.grid.harmonicValues[k][i][j],0.45))*255.);
          v = Math.min(v, 255);
          v = Math.max(v, 0);
          Color c = new Color(v,v,255);
          Util.changeColor(frame, c);
            this.frame.ellipse(i * Grid.GRID_STEP + this.grid.minX, j * Grid.GRID_STEP + this.grid.minY, 2, 2);
            Util.changeColor(this.frame, Util.DEFAULT);
          }
        }
      }
    }
  
  public void colorLaplace() {
    if (this.points.size() == 0) return;
    for (int i = 0; i < this.grid.widthOfGrid; i++) {
      for (int j = 0; j < this.grid.heightOfGrid; j++) {
        if(this.grid.nature[i][j] == Grid.LABELS.INTERIOR){
          double result = 0; 
          for (int k = 0; k < this.points.size(); k ++) {
            result += this.grid.harmonicValues[k][i][j];
          }
        int v = (int) (Math.max(0, 1 - Math.pow(result, 0.45)) * 255);
        Color c = new Color(v,v,255);
        Util.changeColor(frame, c);
          this.frame.ellipse(i * Grid.GRID_STEP + this.grid.minX, j * Grid.GRID_STEP + this.grid.minY, 2, 2);
          Util.changeColor(this.frame, Util.DEFAULT);
        }
      }
    }
  }
  
  public void addPoint(Point_2 p) {
    if (findPoint(p) == -1) {
      if (DrawingApplet.DEBUG_MODE) {
        System.out.println("Adding point: "+ p.toString());
      }
      this.points.add(p);
    }
  }
  
  public void removePoint(Point_2 p) {
    int index = findPoint(p);
    if (index >= 0 && index < this.points.size()) {
      removePoint(index);
      this.computeHarmonicCoordinates();
    }
  }
  
  private void removePoint(int index) {
    this.points.remove(index);
  }
  
  public int findPoint(Point_2 p) {
    for (int i = 0; i < this.points.size(); i++) {
      Point_2 testPoint = this.points.get(i);
      if (Util.closePoints(testPoint, p)) {
        return i;
      }
    }
    return -1;
  }
  
  public void movePoint(int index, Point_2 p) {
    this.points.set(index, p);
  }
  
  public Point_2 getPoint(int index) {
    return this.points.get(index);
  }
  
  public void computeGridCoordinates() {
    for (Point_2 p : this.points) {
      float x = (float)(double)p.x;
      float y = (float)(double)p.y;
      maxX = (x > maxX) ? x : maxX;
      maxY = (y > maxY) ? y : maxY;
      minX = (x < minX) ? x : minX;
      minY = (y < minY) ? y : minY;
    }
  }
  
  public void toFile() {
    PrintWriter out = null;
    try {
      out = new PrintWriter(new FileWriter(AnimatableObject.outputFile));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
    int N = this.points.size();
    out.println(N);
    for (int i = 0; i < N; i++) {
      Point_2 p = this.points.get(i);
      out.println(p.getX().intValue() + " " + p.getY().intValue()); 
    }
    out.println(2);
    out.println(N - 1 + " " + 1);
    for (int i = 1; i < N; i++) {
      out.println(2);
      out.print(((i + 1) % N) + " " + (i - 1) % N);
      out.println(" ");
    }
    out.close();
  }
}
