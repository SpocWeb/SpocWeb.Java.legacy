package structure; //

/**
  * A {@link Figure2D} defined by a Radius and Eccentricity, generalizing {@link Circle} to
  * unequal Rx and Ry.
  *
  * @see structure.Circle
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-25-2002, 08:19 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:15:20Z
  * digest: 1bab36edace006b9651b8ada1f273e75d84ab61a9181ff77f058e46752511117
  * stale: false
  * tags: [code/2d_geometry]
  * concepts: [2D Ellipse]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
public class Ellipse
extends Circle { //Figure2D {

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
/// #region : Variable 'Eccentricity' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds the Eccentricity of the Circle, i.e. SqRt(Ry / Rx)   */
protected double Eccentricity;

/** Returns the Eccentricity used to derive Rx and Ry from the shared Radius.
  * @return the Eccentricity of the Circle, i.e. SqRt(Ry / Rx)   */
public double getEccentricity() { return Eccentricity; }

/** Sets the Eccentricity of the Circle, i.e. SqRt(Ry / Rx)   */
public void setEccentricity(double Eccentricity_) { this.Eccentricity = Eccentricity_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'TurnAngle' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds the Turning Angle of the Ellipsis   */
protected double TurnAngle;

/** Returns the Angle by which the Ellipse's Axes are turned relative to the Coordinate System.
  * @return the Turning Angle of the Ellipsis  */
public double getTurnAngle() {
	return TurnAngle; }

/** Sets the Turning Angle of the Ellipsis  */
public void setTurnAngle(double TurnAngle_) {
	this.TurnAngle = TurnAngle_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Rx' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds Radius of the Ellipse in x Direction   */
//protected double Rx;

/** Derives the x-Radius from the inherited Radius and this Ellipse's Eccentricity.
  * @return Radius of the Ellipse in x Direction  */
public double getRx() { return R/Eccentricity; } //Rx; }

/** Sets Radius of the Ellipse in x Direction  */
//public void setRx(double Rx_) { this.Rx = Rx_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Ry' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds Radius of the Ellipse in y Direction   */
//protected double Ry;

/** Derives the y-Radius from the inherited Radius and this Ellipse's Eccentricity.
  * @return Radius of the Ellipse in y Direction  */
public double getRy() { return R*Eccentricity; } //Ry; }

/** Sets Radius of the Ellipse in y Direction  */
//public void setRy(double Ry_) { this.Ry = Ry_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected Ellipse() { }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface TODO: abstract Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface ISpatial: Implementation
////////////////////////////////////////////////////////////////////////////////

/** Does not paint anything yet; intended to exploit the Ellipse's fourfold Symmetry,
  * fewer than the eightfold Symmetry a {@link Circle} can use. */
public void draw() {
	//exploit the Symmetry to speed up Painting by 4 and not by 8 like with Circles.
}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Ellipse.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

