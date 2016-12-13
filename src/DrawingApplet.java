import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Jcg.geometry.Point_2;

import processing.core.*; 


public class DrawingApplet extends PApplet implements ActionListener {
/* Static variables */
  static enum Mode {CAGE_EDIT, CURVE_EDIT, COORDINATE_VISUALISATION, TEST, ANIMATE};
  static boolean DEBUG_MODE = true;
  static String SWITCH_MODE = "Switch to Mode: ";
  private static Mode[] MODES = {Mode.CURVE_EDIT, Mode.CAGE_EDIT, Mode.COORDINATE_VISUALISATION, Mode.TEST, Mode.ANIMATE};
  final static int NUMBER_MODES = MODES.length;
  final static String filename = "../drawings/nico2.txt";
  
  /* Variables for window */ 
  private Button newCage, harmonicCoord, plusButton, minusButton, toggleMode, saveDrawing;
  private int currentMode = 0;
  private int currentIndex = 0;
  private Cage cage;
  private AnimatableObject object;
  
  /* Variables for moving points */ 
  private float xOffset = 0.0f;
  private float yOffset = 0.0f;
  private int indexOfMovingPoint = -1;
  private boolean locked = false;
  
  public void setup() {
    initButton();
    object = new AnimatableObject(this);
    cage = new Cage(this, object);
    object.load(filename);
  }
  
  public void initButton() {
    toggleMode = new Button(SWITCH_MODE + MODES[(currentMode + 1) % NUMBER_MODES].toString());
    add(toggleMode);
    toggleMode.addActionListener(this);
    
    newCage = new Button("New object");
    add(newCage);
    newCage.addActionListener(this);
    
    harmonicCoord = new Button("Value of harmonic coordinate");
    add(harmonicCoord);
    harmonicCoord.addActionListener(this);
    
    plusButton = new Button("+");
    add(plusButton);
    plusButton.addActionListener(this);
    
    minusButton = new Button("-");
    add(minusButton);
    minusButton.addActionListener(this);
    
    saveDrawing = new Button("Save current drawing.");
    add(saveDrawing);
    saveDrawing.addActionListener(this);
  }
  
  public void draw() {
    background(0);
    size(500, 700);
    Util.changeColor(this, Util.DEFAULT);
    this.cage.drawCage(currentIndex);
    if (MODES[currentMode] == Mode.CAGE_EDIT) {
      this.cage.colorGrid(); 
    }
    if (MODES[currentMode] == Mode.TEST) {
      this.cage.colorLaplace();
    }
    if (MODES[currentMode] == Mode.COORDINATE_VISUALISATION) {
      this.cage.colorLaplace(currentIndex);
    }
    this.object.drawObject();
  }

  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == newCage) {
      if (MODES[currentMode] == Mode.CAGE_EDIT) {
        System.out.println("New cage.");
        this.cage.reset();
        currentIndex = 0;
      } else if (MODES[currentMode] == Mode.CURVE_EDIT) {
        this.object.reset();
      }
    } else if (event.getSource() == harmonicCoord) {
      this.cage.computeHarmonicCoordinates();
    } else if (event.getSource() == plusButton) {
      currentIndex = (currentIndex + 1) % this.cage.points.size();
    } else if (event.getSource() == minusButton) {
        currentIndex = (currentIndex - 1 + this.cage.points.size()) % this.cage.points.size();
    }  else if (event.getSource() == toggleMode) {
      currentMode = (currentMode + 1) % NUMBER_MODES;
      toggleMode.setLabel(SWITCH_MODE + MODES[(currentMode + 1) % NUMBER_MODES].toString());
      if (MODES[currentMode] == Mode.ANIMATE) {
        this.object.bindPoints();
      }
    } else if (event.getSource() == saveDrawing) {
      if (MODES[currentMode] == Mode.CURVE_EDIT) {
        this.object.toFile();
      } else if (MODES[currentMode] == Mode.CAGE_EDIT) {
        this.cage.toFile();
      }
    }
  }

  public void mouseClicked() {
    Point_2 p = new Point_2();
    p.x = (double) mouseX;
    p.y = (double) mouseY;
    switch (MODES[currentMode]) {
      case CAGE_EDIT:
        if (mouseButton == LEFT) {
          this.cage.addPoint(p);
        } else if (mouseButton == RIGHT) {
          this.cage.removePoint(p);
        }
      break;
      case CURVE_EDIT:
        if (mouseButton == LEFT) {
          this.object.addPoint(p);
        } if (mouseButton == RIGHT) {
          this.object.unselectPoint();
        }
      break;
      case TEST:
        if (mouseButton == LEFT) {
        }
      break;
    }
  }

  public void mousePressed() {
    Point_2 p = new Point_2();
    p.x = (double) mouseX;
    p.y = (double) mouseY;
    if(MODES[currentMode] == Mode.CURVE_EDIT) {
      indexOfMovingPoint = this.object.findPoint(p);
    } else {
      indexOfMovingPoint = cage.findPoint(p); 
    }
  if (indexOfMovingPoint > -1) {
    if (DEBUG_MODE) System.out.println("Locked");
    if(MODES[currentMode] == Mode.ANIMATE) currentIndex = indexOfMovingPoint;
     locked = true;
     if(MODES[currentMode] == Mode.CURVE_EDIT) {
       xOffset = mouseX - (float)(double)object.getPoint(indexOfMovingPoint).x;
       yOffset = mouseY - (float)(double)object.getPoint(indexOfMovingPoint).y;
     } else if (MODES[currentMode] == Mode.CAGE_EDIT) {
      xOffset = mouseX - (float)(double)cage.getPoint(indexOfMovingPoint).x;
      yOffset = mouseY - (float)(double)cage.getPoint(indexOfMovingPoint).y;
     } else if (MODES[currentMode] == Mode.ANIMATE) {
       xOffset = mouseX - (float)(double)cage.getPoint(indexOfMovingPoint).x;
       yOffset = mouseY - (float)(double)cage.getPoint(indexOfMovingPoint).y; 
     }
    }
  }   
  
  public void mouseReleased() {
    locked = false;
    if (DEBUG_MODE) System.out.println("Unlock");
    if (MODES[currentMode] == Mode.ANIMATE) {
      Point_2 q = new Point_2(mouseX - xOffset, mouseY - yOffset);
      this.cage.moveBoundObject();
      //this.cage.computeHarmonicCoordinates();
    }
    indexOfMovingPoint = -1;
  }
  
  public void mouseDragged() {
    if (locked) {
      Point_2 q = new Point_2(mouseX - xOffset, mouseY - yOffset);
      if(MODES[currentMode] == Mode.CURVE_EDIT) {
        this.object.movePoint(indexOfMovingPoint, q);
      } else if (MODES[currentMode] == Mode.CAGE_EDIT) {
        this.cage.movePoint(indexOfMovingPoint, q);
      } else if (MODES[currentMode] == Mode.ANIMATE) {
        this.cage.movePoint(indexOfMovingPoint, q);
        this.cage.moveBoundObject();
      }
    }
  }
  
}