package graphic;

/**
 * This Class adds the (calculated) Extent of the resulting Polygon
 * to the simmple Array of Points.
 * @see graphic.mvc.plane2D.MatrixShort, because Point2D is not extensible  
 *
 * Design Decisions:
 * Negative Indices in the Planes convert to 'null' in the Polygon,
 * which again results in the Breaking of the Line.
 * In the same way you could assume Lines as always closed
 * and mark then as open by using null as the last Point.
 * But this is the same as assuming Polygons as open
 * and close them by adding the same Point at the End.
 * I think the best is to leave this open and allow for a both,
 * programmatic and data driven Solution like done here and in Polygon2D.
 * 
 * @deprecated use graphic.mvc.plane2D.MatrixShort instead
 * @see graphic.mvc.plane2D.VectorPolygon storing a whole polyhedron in short[][][] 
 */
public class Polygon2D {

	/**The Points of the Polygon	 */
	protected Point2D[] Points;

	/**The Extent of the Polygon	 */
	private Line2D Extent;

	//////////////////////
	//	Constructors	//
	//////////////////////

	/**Initializing Constructor,
	 * saves calculating the Extent, because this can be calculated
	 * as the Mapping of the 2D original Extent
	 * or the Extent of the mapped 3D Extent.
	 * Uses the Points directly, creates no copy!	 */
	public Polygon2D(Point2D[] Points, Line2D Extent) {
		this.Points = Points;
		this.Extent = Extent;
	}

	/**Initializing Constructor,
	 * calculates the Extent
	 * and creates a deepCopy of the Points.
	 */
	public Polygon2D(Point2D[] Points) {
		int Length = Points.length;
//		this.Extent = null; //calculated on Demand!
		this.Points = new Point2D[Length];
		while (--Length >= 0) //Create Copies of these Points, because this prevents from fiddling arount with them
			this.Points[Length] = Points[Length].getLocation();
	}


	//////////////
	//	Methods	//
	//////////////

	/**Draws the Polygon, either closed or open.
	 * Needs a Graphics Context for that	 */
	public void draw(IGraphShape g, boolean closed) {
		g.drawPolygon(Points, closed); }

	/**Marks the Points of the Polygon with the Marker from the Marker Class.
	 * Needs a Graphics Context for that	 */
	public void mark(Marker mrk, boolean connect, boolean closed) {
		mrk.P2_Plot(this.Points, null, 0, 0, connect, closed, false, false); }

	/**Returns a Copy of all the Points
	 * TODO: create the Copy.
	 */
	public Point2D[] getPoints() { return Points; }

	/** @return the Extent of the Polygon	 */
	public Line2D getExtent() {
		if (Extent == null) {	//calculate it first
			Point2D Point;
			Extent = new Line2D(Points[0], Points[0]);
			int Length = Points.length;
			this.Points = new Point2D[Length];
			while (--Length >= 0) { //Create Copies of these Points, because this prevents from fiddling arount with them
				if ((Point = Points[Length]) != null) {
					Extent.mergeAt(Point); } }
		} return Extent; }

}
