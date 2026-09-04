package structure; //

/**
  * Title: Circle<p>
  * Description:
  * The Circle needs fewer Parameters than the Ellipse,
  * thus you could technically derive Ellipse from Circle
  * and possibly redefining R to be Rx.
  * On the other Hand, Ellipse and Circle have the same get/set Interface,
  * except that the R Property of Circle is not defined in Ellipse.
  * Certainly Circles are a special Case (math. Subset) of Ellipses 
  * and should not be modeled separately except for Optimizations
  * e.g. in drawing!
  * Here the Optimization can only be realized
  * by hiding the Internals of the Ellipsis
  * and to possibly offer an additional Circle Interface.
  * So the only Difference are different Implementations
  * for the Accessor and Drawing Methods.
  * Since there are set() Methods for Rx and Ry, Encapsulation is broken though!
  * If the Optimization is not drastic
  * one should consider not defining a separate Circle Class.
  * If you model an Ellipse by Radius and Eccentricity,
  * Ellipse has one Parameter more than Circle! 
  * The Formulae for the Area stays the same A=R²*Pi
  * General Ellipses have three Parameters: 
  * Rx = R*e, Ry = R/e or e, and the Inclination phi. 
  *
  * A Solution for immutable Circles is to defines them as Subclasses 
  * with a reduced Set of Constructors to force e=1 (and phi=0).
  * 
  * Known SubClasses:
  * @see Ellipse
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-25-2002, 08:19 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Circle
extends Figure2D { //Ellipse { //

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'R' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds the Radius of the Circle   */
protected double R;

/** @return the Radius of the Circle  */
public double getR() { return R; }

/** Sets the Radius of the Circle  */
public void setR(double R_) { this.R = R_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'StartAngle' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds the Start Angle of the Ellipsis   */
protected double StartAngle;

/** @return the Start Angle of the Ellipsis  */
public double getStartAngle() {
	return StartAngle; }

/** Sets the Start Angle of the Ellipsis  */
public void setStartAngle(double StartAngle_) {
	this.StartAngle = StartAngle_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'StopAngle' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds the Stop Angle of the Ellipsis   */
protected Object StopAngle;

/** @return the Stop Angle of the Ellipsis  */
public Object getStopAngle() {
	return StopAngle; }

/** Sets the Stop Angle of the Ellipsis  */
public void setStopAngle(Object StopAngle_) {
	this.StopAngle = StopAngle_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

/** Empty Constructor	 */
protected Circle() { }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface draw: Implementation
////////////////////////////////////////////////////////////////////////////////

/** Also holds for Ellipse! */
public double Area() { return R*R*Math.PI; }

public void draw() {
	//exploit the Symmetry to speed up Painting by 8 and not by 4 like with Ellipses.
}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Circle.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

