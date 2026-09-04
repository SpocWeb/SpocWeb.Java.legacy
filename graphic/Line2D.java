package graphic;

/**
 * Line with two Point2D Coordinates with Integer Values
 * Can calculate the following Values:
 * -Height
 * -Width
 * -Center of the Line / Rectangle / Ellipsis
 * -Length of the Line / Diagonal
 * -Volume of the Rectangle
 *
 * Used as a Bounding Rectange in @see Polygon2D
 * Used to represent
 * -straight Lines
 * -Rectangles
 * -Ellipses
 * -rounded Rectangles
 */
public class Line2D {

	//Local Variables:

	/**The Starting Point2D.	  */
	protected Point2D Start;

	/**The Ending Point2D.	  */
	protected Point2D Stop;

	/**The Width of the Line.	  */
	protected Point2D Width;

	/**The Center (middle Point) of the Line.	  */
	protected Point2D Center;

	/**Indiates that the Coordinates are ordered,
	 * so the smaller ones end up in Start
	 * and the higher ones end up in Stop.
	 * This makes finding the HyperCube faster.	 */
	protected boolean bolOrdered;

	/**Initializing Constructor giving the Start
	 * and either End Point or the Extent of the Line.
	 * @param Start_ the Start Point of the Line
	 * @param P2 either End Point or Width of the Line
	 * @param width when true P2 is considered as the Width
	 */
	public Line2D (final Point2D Start_, final Point2D P2, final boolean width) { 
		this.Start = new Point2D(Start_);
		if (width) {
			Width = P2   .getLocation();
			Stop  = Start.getLocation().addAt (Width);
		} else {
			Stop  = P2   .getLocation();
			Width = Stop .getLocation().subAt(Start);
		}
		Center= Stop.getLocation().middleAt(Start);
	}

	/**
	 * Initializing Constructor giving the Points
	 * @param Start_ the Start Point of the Line
	 * @param Stop_  the End   Point of the Line
	 */
	public Line2D (Point2D Start, Point2D Stop) { this (Start, Stop, false); }

	/**Starting Point2D of the Line	 */
	public Point2D getStart() { return Start.getLocation(); }

	/**Ending Point2D of the Line	 */
	public Point2D getStop () { return Stop .getLocation(); }

	/**Extent of the Line	 */
	public Point2D getWidth() { return Width.getLocation(); }

	/**Center of the Line	 */
	public Point2D getCenter(){return Center.getLocation(); }

	/**Ending of the Line
	 * Setting this leaves the End Point2D unmodified */
	public void setStart (Point2D arg) {
		Start .subAt(arg);
		Width .addAt(Start);
		Center.subAt(Start.halfAt());
		Start.setLocation(arg);
	}

	/**Ending of the Line
	 * Setting this leaves the Start Point2D unmodified */
	public void setStop	(Point2D arg) {
		Stop  .subAt(arg);
		Width .subAt(Start);
		Center.addAt(Stop.halfAt());
		Stop.setLocation(arg);
	}

	/**Extent of the Line
	 * Setting this leaves the Start Point2D unmodified */
	public void setWidth (Point2D arg) {
		Width .subAt(arg);
		Stop  .subAt(Width);
		Center.subAt(Stop.halfAt());
		Width.setLocation(arg);
	}

	/**Middle of the Line.
	 * Setting this	moves the Line */
	public void setCenter (Point2D arg) {
		Center.subAt(arg);
		Start .addAt(Center);
		Stop  .addAt(Center);
		Center.setLocation(arg);
	}


	//Testing

	//Use 'between' and not 'contains' to test, if a Point2D lies in this Line.

	/**Returns true, if the Point2D arg is within the Box
	 * with the two Points as opposite Corners	 */
	public boolean contains(Point2D arg) {
		return arg.contained(Start, Stop);}

	/**Returns true, if the Line arg is within the Box
	 * with the two Points as opposite Corners	 */
	public boolean contains(Line2D arg) {
		return arg.getStart().contained(Start, Stop) &&
			   arg.getStop ().contained(Start, Stop); }

	/**Returns true, if two Lines intersect  */
/*	public boolean intersects(Line2D arg) {
		return ((orderAble)arg.a[0]).between(a[0], a[1]) ||
			   ((orderAble)arg.a[1]).between(a[0], a[1]); }

	/**Returns the Intersection Point2D of the two Lines	 */
/*	public boolean intersect(Line2D arg) {
		return ((orderAble)arg.a[0]).between(a[0], a[1]) ||
			   ((orderAble)arg.a[1]).between(a[0], a[1]); }

	//Operations:

	protected boolean bolOrdered;

	public boolean ordered(){return bolOrdered;}

	/**Orders the Coordinates, so the smaller ones end up in Start
	 * and the higher ones end up in Stop.	 */
	public Line2D orderAt() {
		int tmp;
		if (! bolOrdered) {
			return this; }
		if (Start.getX() > Stop.getX()) { tmp = Start.getX(); Start.setX(Stop.getX()); Stop.setX(tmp); }
		if (Start.getY() > Stop.getY()) { tmp = Start.getY(); Start.setY(Stop.getY()); Stop.setY(tmp); }
		bolOrdered = true;
		return this; }

	/**Returns the (Hyper-)Cube merged with the Point2D or Polygon 	 */
//	public Line2D merge(Point2D arg) {return ((Line2D) copy()).addPointAt(arg);}

	/**Returns the (Hyper-)Cube merged with the Point2D or Polygon in Place
	 * After this, the Cube is ordered.
	 */
	public Line2D mergeAt(final Point2D arg) {
		if (arg == null) {
			return this; }
		if (! bolOrdered) orderAt();
		if (arg.getX() > Stop .getX()) Stop .setX(arg.getX());
		if (arg.getY() > Stop .getY()) Stop .setY(arg.getY());
		if (arg.getX() < Start.getX()) Start.setX(arg.getX());
		if (arg.getY() < Start.getY()) Start.setY(arg.getY());

		Width .setLocation(Stop);Width .subAt  (Start);
		Center.setLocation(Stop);Center.middleAt(Start);
		return this; }	//next merge would allow for only one MaxAt Operation, because this Rectangle is already ordered!

	/**Returns the Volume of the (Hyper-)Cube with the two Points as opposite Corners	 */
	public int BoxVolume() { return Width.getX()*Width.getY(); }

	/**Returns the Sum of the Length of the (Hyper-)Cube's Edges with the two Points as opposite Corners
	 * This is the AbsV - Norm, the fastest Norm */
	public int AbsV_Norm() { return Math.abs(Width.getX()) + Math.abs(Width.getY()); }

	//Methods of a Line2D:

	/**Returns the Length of the Line.
	 * This is the euklidean Norm	 */
	public int SqrNorm() { return Width.getX()*Width.getX() + Width.getY()*Width.getY(); }

	/**Multiplication: *
	 * This is the standard Implementation, because it has been changed in 'Point2D'  */
//	public SemiGroupM mul (Object arg) { return ((SemiGroupM) copy()).mulAt(arg); }

	/**Stretches each coordinate and adds a Vector to P.
	 * This is an affine Mapping in each coordinate that fits a Box into another Box.
	 *
	 * Stretch is calculated as the Ratio of the Widths,
	 * Translate is the Difference of the starting Points.
	 *
	 * This can be used to do affine coordinate Transformation for simple Graphics
	 * or to generate uniformly distributed Data in a Hypercube
	 * from random Vectors in [0,1)^n.
	 */
/*	public Point2D Lin(Point2D arg) {
		return LinAt((Point2D) arg.copy()); }

	/**Stretches each coordinate individually and adds a Vector to P in Place.
	 * This is an affine Mapping in each coordinate that fits a Box into another Box.
	 *
	 * Stretch is calculated as the Ratio of the Widths,
	 * Translate is the Difference of the starting Points.
	 *
	 * This can be used to do affine coordinate Transformation for simple Graphics
	 * or to generate uniformly distributed Data in a Hypercube
	 * from random Vectors in [0,1)^n.
	 */
	public Point2D LinAt(Point2D arg) {
		arg.setX(arg.getX() * Width.setX(Width.getX() + Start.getX()));
		arg.setY(arg.getY() * Width.setY(Width.getY() + Start.getY()));
		return arg; }

	/**Concatenates the two Mappings.	 */
	public Line2D mulAt(Line2D arg) {
		arg.LinAt (Start);
		Width.setX(Width.getX() * arg.Width.getX());		//*	...transform mWidth and ...
		Width.setY(Width.getY() * arg.Width.getY());		//*	...transform mWidth and ...
		Stop.addAt(Width);	//+	...a[1] separately, although the Benefit of LinAt is lost.
		Center.setLocation(Stop);Center.middleAt(Start);
		return this; }

}
