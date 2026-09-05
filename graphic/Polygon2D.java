package graphic;

/**
 * Adds the (calculated) extent of the resulting polygon to a plain array of
 * points.
 *
 * <p>Design decision: negative indices in the source planes convert to
 * {@code null} in the polygon, which breaks the line at that point - the
 * same effect as marking an otherwise-closed line open by using {@code null}
 * as its last point, or a polygon open by repeating its first point at the
 * end. Both a programmatic and a data-driven solution are supported here and
 * in {@code Polygon2D}, deliberately left open rather than picking one.
 *
 * @deprecated use graphic.mvc.plane2D.MatrixShort instead
 * @see graphic.mvc.plane2D.MatrixShort Point2D is not extensible
 * @see graphic.mvc.plane2D.VectorPolygon storing a whole polyhedron in short[][][]
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:52:12Z
 * digest: b6973bac9df07c7d1dba7e43af60b16ccee108baa80bc65374a7e07e5ca253fd
 * stale: false
 * tags: [code/2d_geometry, code/polygon_calculation]
 * concepts: [2D Polygon]
 * facets: {layer: domain, status: broken, complexity: medium}
 * -->
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

	/**
	 * Returns this polygon's bounding extent, computing and caching it on
	 * first access.
	 *
	 * @return the Extent of the Polygon
	 */
	public Line2D getExtent() {
		if (Extent == null) {	//calculate it first
			Point2D Point;
			Extent = new Line2D(Points[0], Points[0]);
			int Length = Points.length;
			// TODO: LOGIC: this.Points is overwritten with a fresh all-null array
			// BEFORE the loop below reads "Points[Length]", so every iteration sees
			// null and mergeAt() never runs beyond the first point set above; the
			// resulting Extent only ever covers Points[0], and the polygon's real
			// point data is destroyed for every subsequent getPoints() call.
			this.Points = new Point2D[Length];
			while (--Length >= 0) { //Create Copies of these Points, because this prevents from fiddling arount with them
				if ((Point = Points[Length]) != null) {
					Extent.mergeAt(Point); } }
		} return Extent; }

}
