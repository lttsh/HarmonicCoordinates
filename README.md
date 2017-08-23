# INF555 -Articulating objects with Harmonic Coordinates (2D) #

This is an 2D implementation of the [Harmonic Coordinates for Character Articulation](http://dl.acm.org/citation.cfm?id=1276466) by Joshi et. al
Harmonic coordinates provide an intuitive and real-time method for character articulation, the implementation, done using Java and Java Applet is a demonstration of this method in the 2D Case.

This was a paired-project for the final project of the [Digital Representation and Analysis of Shapes](http://www.enseignement.polytechnique.fr/informatique/INF555/)
(INF555) class taught at Ecole polytechnique. 

## Description of the Applet ##

![Image](https://user-images.githubusercontent.com/13089230/29629130-95a458ce-87ec-11e7-9ea0-d1f95a93c43c.png)


The applet has 6 buttons.
* Switch mode button that allows the user to switch between the different modes of the application.
  * `CURVE_EDIT`: The user can define a drawing that will be the 2D character to be animated. 
  * `CAGE_EDIT`: The user can define and modify the cage that is used in the Harmonic Coordinates articulation method.
  * `COORDINATE_VISUALISATION`: The user can visualize the values of the harmonic coordinates associated to a selected point of the cage.
  * `TEST`: The user can visualize the sum of the harmonic coordinates. Sum should always be 1 and this mode is just a sanity check.
  * `ANIMATE`: The user can animate the 2D character by moving the point of the cage.
* New object button: 
  * in `CURVE_EDIT` mode, reinitializes the 2D character.
  * in `CAGE_EDIT` mode, reinitializes the cage.
* Value of harmonic coordinate: Once the cage is defined, this computes the harmonic coordinates for all the vertices of the cage.
* +/- buttons: Changes the selected vertex of the cage (drawn in red), in `COORDINATE_VISUALISATION` mode, allows the user to switch the selected point to visualize the coordinates.
* Save current drawing: encodes the current 2D character to a file `drawings/output.txt` for later use.

## How to use ##
The application was exported to the `demo.jar` file. To launch:
` java -jar demo.jar optional/path/to/encoded/2D-character/file`
The argument is optional and is a commodity enable loading complex drawings. If no argument is given of if the path leads to a non-existent file,,
the application will just begin with an empty curve.

### `CURVE_EDIT` usage ###
* Left-Clicking on an already defined point selects it. (The selected point is in red)
* Left-Clicking and dragging an already defined point moves it. 
* Left-Clicking on an empty space, adds a new point and draws a segment between the new point and the currently selected point.
* Right-Clicking on a selected point deselects it, which allows the drawing of non-connected shapes.

### `CAGE_EDIT` usage ###
The cage is a closed polygon whose points are defined by the user. 
* Left-clicking and dragging an already defined point moves it.
* Left-clicking on an empty space adds a new point linked to the first defined point and the last point defined.

### `ANIMATE` usage ###
Left click and drag the points of the cage to see the 2D drawing be deformed.
  
## Demos ##

### Visualization of coordinates ###
Dark blue indicates coordinates of higher values, white indicates coordinates is 0.


![visualization](https://user-images.githubusercontent.com/13089230/29629475-dc24c99a-87ed-11e7-92ec-4ec33b528775.gif)

