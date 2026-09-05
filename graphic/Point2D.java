package graphic;

//import streamIO.Log;

/**
 * The <code>Point2D</code> class represents a location in a
 * two-dimensional (<i>x</i>,&nbsp;<i>y</i>) Integer coordinate space.
 *
 * An Alternative would have been to use an int[], 
 * but that would be less explicit, but more flexible,  
 * because you could use @see MatrixInt instead of @see graphic.VectorPoint2D. 
 * Contains some integer arithmetics useful for 2D Graphics
 * 
 * By making this Class final e.g. the Access Methods are sped up by Inlining
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:04:32Z
 * digest: e971f2468efb8a14dc45335d10b10c5239414526cb334745c7f61fe303331bd3
 * stale: false
 * tags: [code/2d_geometry, code/point_normal_calculation]
 * concepts: [Integer 2D Point]
 * facets: {layer: domain, status: broken, complexity: low}
 * -->
 */
final public class Point2D {

	/** streamIO for Logging */
	//public static Log L = new Log(1); 

	//static { L.logStack = true;	}

	/////////////////////////////////////////////////////////////////////////////////
	//local Variables
	/////////////////////////////////////////////////////////////////////////////////

	/**The <i>x</i> coordinate.	  */
	public int x; //use short

	/** Replaces the x coordinate, returning the new value. */
	public int setX(final int x) {
		//L.n("setX("+x+")");
		return this.x = x;
	}

	/** Returns the x coordinate. */
	public int getX() {
		return x;
	}

	/**The <i>y</i> coordinate.	  */
	public int y; //use short

	/** Replaces the y coordinate, returning the new value. */
	public int setY(final int y) {
		//L.n("setX("+x+")");
		return this.y = y;
	}

	/** Returns the y coordinate. */
	public int getY() {
		return y;
	}

	/** Returns this point's coordinates as a new {@code short[2]}. */
	public short[] getCoords() {
		return getCoords(null);
	}

	/**
	 * Writes this point's coordinates into {@code ret}, or a new
	 * {@code short[2]} when {@code ret} is null or too small.
	 */
	public short[] getCoords(short[] ret) {
		if ((ret == null) || (ret.length < 2)) 
			ret = new short[2]; 
		ret[0] = (short) x; 
		ret[1] = (short) y; 
		return ret;
	}
	

	/////////////////////////////////////////////////////////////////////////////////
	// arithmetic Methods
	/////////////////////////////////////////////////////////////////////////////////

	/**Swaps the coordinates 	 */
	public Point2D swapColsAt() {
		int tmp = x;
		x = y;
		y = tmp;
		return this;
	}

	/**Makes the coordinates non-negative	 */
	public Point2D absVAt() {
		if (x < 0)
			x = -x;
		if (y < 0)
			y = -y;
		return this;
	}

	/**Adds the Coordinates of P to this Point2D	 */
	public Point2D addAt(final Point2D P) {
		//L.n("addAt(" + P + ")");
		x += P.x;
		y += P.y;
		return this;
	}

	/**Adds the Coordinates of P to this Point2D	 */
	public Point2D addAt(final int increment) {
		//L.n("addAt(" + increment + ")");
		x += increment;
		y += increment;
		return this;
	}

	/**Adds the Coordinates of P to this Point2D	 */
	public Point2D addAt(final int dx, final int dy) {
		//L.n("addAt(" + dx + "," + dy + ")");
		x += dx;
		y += dy;
		return this;
	}

	/**Adds the Coordinates of P to this Point2D	 */
	public Point2D addAt(final int[] increment) {
		//L.n("addAt(" + increment[0] + "," + increment[1] + ")");
		x += increment[0];
		y += increment[1];
		return this;
	}

	/** Sets each coordinate to the smaller of itself and P's matching coordinate. */
	public Point2D MinAt(final Point2D P) {
		if (x > P.x)
			x = P.x;
		// TODO: LOGIC: compares "y > P.x" and assigns "y = P.x" instead of
		// using P.y; the y coordinate is min'd against P's x coordinate, not
		// P's y coordinate, corrupting every bounding-box computation that
		// relies on this method whenever P.x != P.y.
		if (y > P.x)
			y = P.x;
		return this;
	}

	/**Sets the Maximum Coordinates of P in this Point2D	 */
	public Point2D MaxAt(final Point2D P) {
		if (x < P.x)
			x = P.x;
		// TODO: LOGIC: compares "y < P.x" and assigns "y = P.x" instead of
		// using P.y; same P.x/P.y mixup as MinAt() above, corrupting every
		// bounding-box computation that relies on this method whenever
		// P.x != P.y.
		if (y < P.x)
			y = P.x;
		return this;
	}

	/** Returns the larger of this point's two coordinates. */
	public int MaxVal() {
		if (x < y)
			return y;
		return x;
	}

	/**Adds the Coordinates of P to this Point2D	 */
	public Point2D copy() {
		return new Point2D(this);
	}

	/**
	 * Returns the sum of this point's two coordinates.
	 *
	 * @return the Sum of the Coordinates of this Point2D
	 */
	public int Sum() {
		return x + y;
	}

	/**Divides the Coordinates of this Point2D by two	 */
	public Point2D halfAt() {
		x >>= 1;
		y >>= 1;
		return this;
	}

	/**Divides the Coordinates of this Point2D by two	 */
	public Point2D AbsVAt() {
		if (x < 0)
			x = -x;
		if (y < 0)
			y = -y;
		return this;
	}

	/**Divides the Coordinates of this Point2D by two	 */
	public Point2D NegAt() {
		x = -x;
		y = -y;
		return this;
	}

	/**Divides the Coordinates of this Point2D by two	 */
	public Point2D divAt(int i) {
		x /= i;
		y /= i;
		return this;
	}

	/**Divides the Coordinates of this Point2D by two	 */
	public Point2D mulAt(int i) {
		x *= i;
		y *= i;
		return this;
	}

	/**Subtracts the Coordinates of P to this Point2D	 */
	public Point2D subAt(Point2D P) {
		x -= P.x;
		y -= P.y;
		return this;
	}

	/**Subtracts the Coordinates of P to this Point2D	 */
	public Point2D copyAt(Point2D P) {
		//L.n("copyAt(").l(P).l(")");
		x = P.x;
		y = P.y;
		return this;
	}

	/**Subtracts the Coordinates of P to this Point2D	 */
	public Point2D copyAt(final int x_, final int y_) {
		//L.n("copyAt(").l(x_).l(",").l(y_).l(")");
		this.x = x_;
		this.y = y_;
		return this;
	}

	/**Subtracts the Coordinates of P to this Point2D	 */
	public Point2D copyAt(final int[] vals) {
		x = vals[0];
		y = vals[1];
		return this;
	}

	/**Subtracts the Coordinates of P to this Point2D	 */
	public Point2D subAt(final int[] decrement) {
		x -= decrement[0];
		y -= decrement[1];
		return this;
	}

	/**Calculates the Middle Coordinates of P and this Point2D	 */
	public Point2D middleAt(final Point2D P) {
		return addAt(P).halfAt();
	}

	/**Calculates the Middle Coordinates of P and this Point2D	 */
	public Point2D middle(final Point2D P) {
		return new Point2D((x + P.x) >> 1, (y + P.y) >> 1);
	}

	/////////////////////////////////////////////////////////////////////////////////
	//	Constructors
	/////////////////////////////////////////////////////////////////////////////////

	/**Constructs and initializes a point at the origin
	 * (0,&nbsp;0) of the coordinate space.	  */
	public Point2D() {
		//L.n("Point2D()");
		// this.x = 0;	//initialization not necessary,
		// this.y = 0;	//done by the Java System
	}

	/**Constructs and initializes a point with the same location
	 * as the specified <code>Point2D</code> object.
	 * @param	   p a point.
	 */
	public Point2D(final Point2D p) {
		//L.n("Point2D(" + p + ")");
		if (p == null) {
			return; }
		this.x = p.x;
		this.y = p.y;
	}

	/**Constructs and initializes a point at the specified
	 * (<i>x</i>,&nbsp;<i>y</i>) location in the coordinate space.
	 * @param       x   the <i>x</i> coordinate.
	 * @param       y   the <i>y</i> coordinate.
	 */
	public Point2D(final int x_, final int y_) {
		this.x = x_;
		this.y = y_;
		//L.n("Point2D(").l(x).l(",").l(y).l(")");
	}

	/**Constructs and initializes a point at the specified
	 * (<i>x</i>,&nbsp;<i>y</i>) location in the coordinate space.
	 * @param       xy   the <i>x</i> and <i>y</i> coordinate.
	 */
	public Point2D(final int[] xy) {
		this.x = xy[0];
		this.y = xy[1];
		//L.n("Point2D(").l(x).l(",").l(y).l(")");
	}

	/////////////////////////////////////////////////////////////////////////////////
	///
	/////////////////////////////////////////////////////////////////////////////////

	/**Used to identify the clicked Object.
	 * called on the Mouse Button down Event 
	 * 
	 * @param x Coordinate of the Point to search for
	 * @param y Coordinate of the Point to search for
	 * @param maxDist maximum Distance in Maximum Norm
	 * @return true if the given Coordinates are closer than the given Distance.
	 */
	public boolean isNeighbour(int x_, int y_, int maxDist) {
		//return (Math.abs(x - point.x) + Math.abs(y - point.y) <= PointRadius) {
		return (
			(Math.abs(x_ - this.x) <= maxDist)
				&& (Math.abs(y_ - this.y) <= maxDist));
	}

	/**
	 * Returns the location of this point.
	 * This method is included for completeness, to parallel the
	 * <code>getLocation</code> method of <code>Component</code>.
	 * Actually it is a copy() or clone() Method.
	 * @return      a copy of this point, at the same location.
	 * @see         java.awt.Component#getLocation
	 * @see         java.awt.Point2D#setLocation(java.awt.Point2D)
	 * @see         java.awt.Point2D#setLocation(int, int)
	 */
	public Point2D getLocation() {
		return new Point2D(x, y);
	}

	/**Sets the location of the point to the specificed location.
	 * This method is included for completeness, to parallel the
	 * <code>setLocation</code> method of <code>Component</code>.
	 * @param       p  a point, the new location for this point.
	 * @see         java.awt.Component#setLocation(java.awt.Point2D)
	 * @see         java.awt.Point2D#getLocation
	 * @since       JDK1.1
	 */
	public void setLocation(Point2D p) {
		setLocation(p.x, p.y);
	}

	/**Changes the point to have the specificed location.
	 * <p>
	 * This method is included for completeness, to parallel the
	 * <code>setLocation</code> method of <code>Component</code>.
	 * Its behavior is identical with <code>move(int,&nbsp;int)</code>.
	 * @param       x  the <i>x</i> coordinate of the new location.
	 * @param       y  the <i>y</i> coordinate of the new location.
	 * @see         java.awt.Component#setLocation(int, int)
	 * @see         java.awt.Point2D#getLocation
	 * @see         java.awt.Point2D#move(int, int)
	 */
	public void setLocation(int x, int y) {
		move(x, y);
	}

	/**Moves this point to the specificed location in the
	 * (<i>x</i>,&nbsp;<i>y</i>) coordinate plane. This method
	 * is identical with <code>setLocation(int,&nbsp;int)</code>.
	 * @param       x  the <i>x</i> coordinate of the new location.
	 * @param       y  the <i>y</i> coordinate of the new location.
	 * @see         java.awt.Component#setLocation(int, int)
	 */
	public void move(final int x_, final int y_) {
		this.x = x_;
		this.y = y_;
	}

	/**Translates this point, at location (<i>x</i>,&nbsp;<i>y</i>),
	 * by <code>dx</code> along the <i>x</i> axis and <code>dy</code>
	 * along the <i>y</i> axis so that it now represents the point
	 * (<code>x</code>&nbsp;<code>+</code>&nbsp;<code>dx</code>,
	 * <code>y</code>&nbsp;<code>+</code>&nbsp;<code>dy</code>).
	 * @param       dx   the distance to move this point
	 *                            along the <i>x</i> axis.
	 * @param       dy    the distance to move this point
	 *                            along the <i>y</i> axis.
	 */
	public void translate(final int x_, final int y_) {
		this.x += x_;
		this.y += y_;
	}

	/**Returns the hashcode for this point.
	 * @return      a hash code for this point.
	 */
	public int hashCode() {
		return x ^ ((y << 5) - y);
	} //(y*31); }

	/**Determines whether two points are equal. Two instances of
	 * <code>Point2D</code> are equal if the values of their
	 * <code>x</code> and <code>y</code> member fields, representing
	 * their position in the coordinate space, are the same.
	 * @param      obj   an object to be compared with this point.
	 * @return     <code>true</code> if the object to be compared is
	 *                     an instance of <code>Point2D</code> and has
	 *                     the same values; <code>false</code> otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Point2D) {
			Point2D pt = (Point2D) obj;
			return (x == pt.x) && (y == pt.y);
		}
		return false;
	}

	/**Returns a representation of this point and its location
	 * in the (<i>x</i>,&nbsp;<i>y</i>) coordinate space as a string.
	 * @return    a string representation of this point,
	 *                 including the values of its member fields.
	 * @since     JDK1.0
	 */
	public String toString() {
		return getClass().getName() + "[x=" + x + ",y=" + y + "]";
	}

	/////////////////////////////////////////////////////////////////////////////////
	//	Geometric Routines	
	/////////////////////////////////////////////////////////////////////////////////

	/**Returns true, if the Point2D arg is within the Box
	 * with the two Points as opposite Corners	 */
	public boolean contained(Point2D P1, Point2D P2) {
		return ((x < P1.x) ^ (x < P2.x)) && ((y < P1.y) ^ (y < P2.y));
	}

	/**Returns the double Area of the Triangle defined by these three Points.	 */
	public int AreaTriangle(Point2D P1, Point2D P2) {
		return ((P2.y - y) * (P1.x - x)) - ((P2.x - x) * (P1.y - y));
	}

}
