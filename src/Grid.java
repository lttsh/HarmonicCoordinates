import java.util.LinkedList;
import java.util.Stack;

import Jama.Matrix;
import Jcg.geometry.Point_2;

public class Grid {
  static enum LABELS {UNDEFINED, BOUNDARY, EXTERIOR, INTERIOR};
  private Cage cage;
  private int n;
  protected static int GRID_STEP = 2; 
  protected LABELS[][] nature;
  protected double[][][] harmonicValues;
  protected int maxX, minX, maxY, minY;
  protected int heightOfGrid, widthOfGrid;
  static final double CHANGE_THRESHOLD = 0.000000001;
  
  public Grid(Cage cage) {
    this.cage = cage;
    this.cage.computeGridCoordinates();
    minX = (int)this.cage.minX - 2 * GRID_STEP;
    minY = (int)this.cage.minY - 2 * GRID_STEP ;
    maxX = (int)this.cage.maxX + 2 * GRID_STEP ;
    maxY = (int)this.cage.maxY + 2 * GRID_STEP ;
    heightOfGrid = (maxY - minY) / GRID_STEP;
    widthOfGrid = (maxX - minX) / GRID_STEP;
    
    this.nature = new LABELS[widthOfGrid][heightOfGrid];
    for(int i =0; i< widthOfGrid;i++){
      for(int j = 0; j< heightOfGrid;j++){
        nature[i][j] = LABELS.UNDEFINED;
      }
    }
    
    int N = this.cage.points.size();
    this.harmonicValues = new double[N][widthOfGrid][heightOfGrid];
    for(int k = 0; k < N; k++) {
      for(int i = 0; i < widthOfGrid; i++) {
        for(int j = 0;j < heightOfGrid; j++) {
          harmonicValues[k][i][j]=0.; // on initialise à 0
        }
      }
    }   
  }
  
  public void labelGrid(){
    if (this.cage.points.size() < 3) return;
    System.out.println("Computing fill...");
    Stack<Point_2> next = new Stack<Point_2>(); // pile des cases encore à traiter
    Point_2 origin = new Point_2(minX,minY);
    next.add(origin); 
    // tout d'abord on a juste le point 0,0 dans la pile, on va rajouter tous ses voisins
    while(!next.isEmpty()){
      Point_2 currentPoint = next.pop();
      int x = (int) currentPoint.x.doubleValue() , y = (int) currentPoint.y.doubleValue();
      Point_2 d = new Point_2(x + GRID_STEP, y) , g = new Point_2(x - GRID_STEP, y); // voisins droite/gauche
      Point_2 h = new Point_2(x,y - GRID_STEP) , b = new Point_2(x,y + GRID_STEP); // voisins haut/bas
      if(isCorrect(g)){ // isCorrect = le point est dans le cadre et n'a pas été visité précédemment
        labelPoint(g,next); //cf labelPoint
      }
      if(isCorrect(h)){
        labelPoint(h,next);
      }
      if(isCorrect(b)){
        labelPoint(b,next);
      }
      if(isCorrect(d)){
        labelPoint(d,next);
      }
    }
    
    for (int i = 0; i < widthOfGrid; i++) {
      for (int j = 0; j < heightOfGrid; j++) {
        if (nature[i][j] == LABELS.UNDEFINED) {
          nature[i][j] = LABELS.INTERIOR;
        }
      }
    }
    
    System.out.println("done");
  }
  
  public Point_2 computeNewPosition(Point_2 p) {
    int x = p.getX().intValue();
    int y = p.getY().intValue();
    int i = (int) ((x - this.cage.grid.minX) / Grid.GRID_STEP);
    int j = (int) ((y - this.cage.grid.minY) / Grid.GRID_STEP);
    if (i< 0 || i >= widthOfGrid || j < 0 || j >= heightOfGrid) return p;
    if (this.nature[i][j] == LABELS.EXTERIOR) return p; 
    Point_2 newPosition = new Point_2 (0, 0);
    for (int k = 0; k < this.cage.points.size(); k++) {
      System.out.println("Influence of vertex " + k + "on point: " + p.toString() + " "+ this.harmonicValues[k][i][j]);
        newPosition.x += this.harmonicValues[k][i][j] * this.cage.points.get(k).x;
        newPosition.y += this.harmonicValues[k][i][j] * this.cage.points.get(k).y;
    }
    if (p.distanceFrom(newPosition).doubleValue() < 2* GRID_STEP) return p;
    return newPosition;
  }
  
  public void computeHarmonicsSequential() {
    System.out.println("Computing the harmonic coordinates.");
    long startTime = System.currentTimeMillis();
        
    for (int i = 0; i < this.cage.points.size(); i++) {
      computeHarmonic(i);
    }
    
    testAffineInvariance(widthOfGrid / 2, heightOfGrid / 2);
    testAffineInvariance(this.cage.getPoint(0));
    testNewPosition(new Point_2(this.cage.object.getPoint(0)));
    
    testAffineInvariance(this.cage.getPoint(1));
    testNewPosition(new Point_2(this.cage.object.getPoint(1)));
    
    testAffineInvariance(this.cage.getPoint(1));
    testNewPosition(new Point_2(this.cage.object.getPoint(2)));
    long endTime = System.currentTimeMillis();
    System.out.println("Sequential Computation of harmonic coordinates for "+ this.cage.points.size() + 
        " vertices in a cage of size: "+ widthOfGrid+ ", " + heightOfGrid+ " took: " + 
        (endTime - startTime) + " ms.");
    
  }
  public void computeHarmonics() {
    System.out.println("Computing the harmonic coordinates.");
    long startTime = System.currentTimeMillis();
    Thread[] threads = new Thread[this.cage.points.size()];
    for (int i = 0;i < this.cage.points.size(); i++) {
      final int index = i;
      threads[i] = new Thread(new Runnable() {
        public void run() {
          computeHarmonic(index);
        }});
     threads[i].start();
    }
    
    for (int i = 0; i < this.cage.points.size(); i++) {
      try {
        threads[i].join();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
    testAffineInvariance(widthOfGrid / 2, heightOfGrid / 2);
    testAffineInvariance(this.cage.getPoint(0));
    testNewPosition(new Point_2(this.cage.object.getPoint(0)));
    
    testAffineInvariance(this.cage.getPoint(1));
    testNewPosition(new Point_2(this.cage.object.getPoint(1)));
    
    testAffineInvariance(this.cage.getPoint(1));
    testNewPosition(new Point_2(this.cage.object.getPoint(2)));
    long endTime = System.currentTimeMillis();
    System.out.println("Computation of harmonic coordinates for "+ this.cage.points.size() + 
        " vertices in a cage of size: "+ widthOfGrid+ ", " + heightOfGrid+ " took: " + 
        (endTime - startTime) + " ms.");
  }
  
  private boolean testAffineInvariance(Point_2 p) {
    int i = (int) ((p.x.doubleValue() - this.cage.grid.minX) / Grid.GRID_STEP);
    int j = (int) ((p.y.doubleValue() - this.cage.grid.minY) / Grid.GRID_STEP);
    return testAffineInvariance(i, j);
  }
  
  private boolean testAffineInvariance(int i, int j) {
    double sum = 0;
    for (int k = 0; k < this.cage.points.size(); k++) {
      sum+= this.harmonicValues[k][i][j];
    }
    System.out.println(sum);
    return (Math.abs(sum - 1) < 0.01);
  }
  
  private void testNewPosition(Point_2 p) {
    Point_2 newPosition = this.computeNewPosition(p);
    System.out.println(newPosition.distanceFrom(p));
    System.out.println(newPosition);
    System.out.println(p); 
  }
  
  /* COMPUTING HARMONIC COORDINATES */ 
  //on applique Laplace
  
  private void computeHarmonic(int i) {
    computeEdgeValues(i);
    computeInteriorValues(i);
  }
  //on applique Laplace sur les contours
  
  private void computeEdgeValues(int i) {
    int nbrVertex = this.cage.points.size();
    if(this.cage.points.size() == 0) return;
    int ibefore = (i - 1 + nbrVertex) % nbrVertex, iafter = (i + 1) % nbrVertex;
    Point_2 p = this.cage.getPoint(i);
    Point_2 pBefore = this.cage.getPoint(ibefore) , pAfter = this.cage.getPoint(iafter);
    
    double distanceBefore = (Double) p.distanceFrom(pBefore).doubleValue();
    double distanceAfter=(Double)p.distanceFrom(pAfter).doubleValue();
    
    for(int x = 0; x < widthOfGrid; x++) {
      for(int y = 0; y < heightOfGrid; y++) {
        Point_2 temp = new Point_2(
            x * Grid.GRID_STEP + this.cage.grid.minX,
            y * Grid.GRID_STEP + this.cage.grid.minY);
        if(temp.equals(p)) {
          this.harmonicValues[i][x][y] = 1.;
        }
        else if(this.cage.grid.belongsToEdge(temp, ibefore)) {
          this.harmonicValues[i][x][y] = (Double) pBefore.distanceFrom(temp).doubleValue() / distanceBefore;
        }
        else if(this.cage.grid.belongsToEdge(temp, i)) {
          this.harmonicValues[i][x][y] = (Double) pAfter.distanceFrom(temp).doubleValue() / distanceAfter;
        }
      }
    } 
  }
  
  //on applique Laplace dans la cage
  
  private void computeInteriorValues(int i) {
    double[][] copy = new double[widthOfGrid][heightOfGrid]; //on va stocker une copie
    for(int x = 0; x < widthOfGrid; x++) {
      for(int y = 0; y < heightOfGrid; y++) {
        copy[x][y]=this.harmonicValues[i][x][y];
      }
    }
    int count = 0;
    double averageChange = 0;
    do {
      int n = 0; //nbr of changed pixels
      double change = 0.;
      for(int x = 0; x < widthOfGrid; x++) {
        for(int y = 0; y < heightOfGrid; y++) {
          //si le point est à l'interieur on le change
          if (this.cage.grid.nature[x][y] == Grid.LABELS.INTERIOR) {
            copy[x][y] = neighborMean(i,x,y); //moyenne des voisins
            change += Math.abs(copy[x][y] - this.harmonicValues[i][x][y]);
            n++;
          }
        }
      }
      
      for(int x = 0; x < widthOfGrid; x++) {
        for(int y = 0; y < heightOfGrid; y++) {
          this.harmonicValues[i][x][y] = copy[x][y]; //on remplace les valeurs des harmonic
        }
      }
      
      if(n != 0) averageChange = change / (double) n;
      count++;
    } while(averageChange > CHANGE_THRESHOLD);
  }
  
  private double neighborMean(int i, int x, int y) {
    return (harmonicValues[i][x - 1][y] + harmonicValues[i][x + 1][y] +
        harmonicValues[i][x][y - 1] + harmonicValues[i][x][y + 1]) * 0.25;
  }
  
  /* LABELING THE GRID ELEMENTS */ 
  
  private boolean isCorrect(Point_2 p){
    int X = ((int) p.x.doubleValue() - minX)/GRID_STEP; int Y = ((int) p.y.doubleValue() - minY) / GRID_STEP;
    if(X < 0 || X >= widthOfGrid || Y < 0 || Y >= heightOfGrid || nature[X][Y] != LABELS.UNDEFINED) return false;
    return true;
  }
  
  private void labelPoint(Point_2 p, Stack<Point_2> next){
    int X = ((int) p.x.doubleValue() - minX) / GRID_STEP; int Y = ((int) p.y.doubleValue() - minY) / GRID_STEP;
    if(this.belongsToCage(p)){ //si le point est sur le bord
      nature[X][Y] = LABELS.BOUNDARY; // on le note bord et on ne le rajoute pas dans la pile
    }
    else{
      nature[X][Y] = LABELS.EXTERIOR; //sinon c'est qu'il est à l'extérieur
      next.add(p); //on l'ajoute à la pile
    }
  }

  private boolean belongsToEdge(Point_2 p, int i) {
    int N = this.cage.points.size();
    Point_2 A = this.cage.points.get(i % N);
    Point_2 B = this.cage.points.get((i + 1) % N);
    Point_2[] square= new Point_2[4];
    square[0] = p;
    square[1] = new Point_2(p.x  + GRID_STEP, p.y);
    square[2] = new Point_2(p.x + GRID_STEP, p.y + GRID_STEP);
    square[3] = new Point_2(p.x, p.y + GRID_STEP);
    for (int j = 0; j < 4; j ++) {
      if (Util.segmentsIntersect(A, B, square[j % 4], square[ (j+1) % 4])) {
        return true;
      }
    }
    return false;
  }
    
  private boolean belongsToCage(Point_2 p) {
    for (int i = 0; i < this.cage.points.size()+1; i++) {
      if (belongsToEdge(p, i)) return true;
    }
    return false;
  }
}
