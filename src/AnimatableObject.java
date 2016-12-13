import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

import Jcg.graph.GeometricGraph_2;
import Jcg.graph.Node;
import Jcg.geometry.Point_2;
import processing.core.PApplet;

public class AnimatableObject extends GeometricGraph_2 {
  static final String outputFile = "../drawings/output.txt";
  private PApplet frame;
  private List<Node<Point_2>> nodes = new LinkedList<Node<Point_2>>();
  protected Map<Node<Point_2>, Point_2> initialPoint = new HashMap<Node<Point_2>, Point_2>();
  private int selectedNode = -1;
  
  public AnimatableObject(PApplet frame) {
    this.frame = frame;
  }
  
  /* Loading object from a file */
  public void load(String filename) {
    Scanner in;
    try {
      in = new Scanner(new FileReader(filename));
    } catch (FileNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      return;
    }
    int N = 0;
    try {N = in.nextInt();} catch(Exception e) {
      System.out.println(e.getCause());
    }
    int X, Y, D, index;
    // Retrieving the nodes.

    for (int i = 0; i < N; i++) {
      X = in.nextInt();
      Y = in.nextInt();
      nodes.add(new Node<Point_2>(new Point_2(X, Y)));
      nodes.get(i).neighbors = new ArrayList<Node<Point_2>>();
      nodes.get(i).tag = i;
    }
    
    //Retrieving the adjacency information.
    for (int i = 0; i < N; i++) {
      D = in.nextInt();
      Node<Point_2> nA = nodes.get(i);
      for (int j = 0; j < D; j++) {
        index = in.nextInt();
        Node<Point_2> nB = nodes.get(index);
        nA.addNeighbor(nB);
        nB.addNeighbor(nA);
      } 
    }
    
    for (int i = 0; i < N; i++) {
      this.addNode(nodes.get(i));
    }
    selectedNode = 0;
  }
  
  
  /*Saving the object into a file*/
  public void toFile() {
    PrintWriter out = null;
    try {
      out = new PrintWriter(new FileWriter(outputFile));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
    int N = this.listOfPoints().size();
    out.println(N);
    for (int i = 0; i < N; i++) {
      Point_2 p = this.listOfPoints().get(i);
      out.println(p.getX().intValue() + " " + p.getY().intValue()); 
    }
    
    for (int i = 0; i < N; i++) {
      Node<Point_2> node = this.vertices.get(i);
      int D = node.degree();
      out.println(D);
      for (Node<Point_2> neighbour: node.neighbors) {
        out.print(neighbour.getTag() + " ");
      }
      out.println(" ");
    }
    
    out.close();
  }
  
  /*Draw object*/
  public void drawObject() {
    List<Point_2[]> edges = this.computeEdges();
    Util.changeColor(frame, Util.DEFAULT);
    for (Point_2[] edge : edges) {
      this.frame.line(edge[0].getX().floatValue(), edge[0].getY().floatValue(), edge[1].getX().floatValue(), edge[1].getY().floatValue());
    }
    
    for (int i = 0; i < this.listOfPoints().size(); i++) {
      Point_2 p = this.listOfPoints().get(i);
      if (i == selectedNode) {
        Util.changeColor(this.frame, Util.RED);
      }
      this.frame.ellipse(p.getX().floatValue(), p.getY().floatValue(), 5, 5);
      
      if (i == selectedNode) {
        Util.changeColor(this.frame, Util.DEFAULT);
      }
    }
  }
  
  public int findPoint(Point_2 p) {
    for (int i = 0; i < this.listOfPoints().size(); i++) {
      Point_2 testPoint = this.listOfPoints().get(i);
      if (Util.closePoints(testPoint, p)) {
        return i;
      }
    }
    return -1;
  }
  
  public void movePoint(int index, Point_2 p) {
    this.nodes.get(index).setData(p);
  }
  
  public Point_2 getPoint(int index) {
    return this.listOfPoints().get(index);
  }
  
  public Point_2 getInitialPoint(int index) {
    Node<Point_2> node = this.nodes.get(index);
    return this.initialPoint.get(node);
  }
  
  public void selectPoint(Point_2 p) {
    int i = findPoint(p);
    if (i >= 0) selectedNode = i;
  }
  
  public void unselectPoint() {
    selectedNode = -1;
  }
  
  public void addPoint(Point_2 p) {
    int i = findPoint(p);
    if (i >= 0) {
      selectPoint(p);
    } else {
      if (selectedNode >= 0) {
        int n = this.nodes.size();
        nodes.add(new Node<Point_2>(p));
        nodes.get(n).neighbors = new ArrayList<Node<Point_2>>();
        nodes.get(n).tag = n;
        nodes.get(n).neighbors.add(nodes.get(selectedNode));
        nodes.get(selectedNode).neighbors.add(nodes.get(n));
        selectedNode = n;
        this.addNode(nodes.get(n));
      } else {
        int n = this.nodes.size();
        nodes.add(new Node<Point_2>(p));
        nodes.get(n).neighbors = new ArrayList<Node<Point_2>>();
        nodes.get(n).tag = 0;
        selectedNode = n;
        this.addNode(nodes.get(n));
      }      
    }
  }
  
  public void reset() {
    selectedNode = -1;
    this.nodes.clear();
    this.vertices.clear();
    this.initialPoint.clear();
  }
  
  public void bindPoints() {
    for (Node<Point_2> p : this.nodes) {
      this.initialPoint.put(p, p.getData());
    }
  }
}
