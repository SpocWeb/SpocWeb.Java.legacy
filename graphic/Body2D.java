package graphic;

import graphic.mvc.plane2D.MatrixShort;
import graphic.mvc.plane2D.VectorPolygon;

import java.awt.Color;

/**
 * A 2-dimensional mapping of a body made of polygon planes, either a
 * genuinely 2D body or the projection of a 3D body onto two dimensions.
 *
 * <p>2 dimensional Mapping of a Body consisting of Planes.
 * The Body can be a genuine 2 dimensional Body
 * or the Mapping of a 3 dimensional Body to two Dimensions.
 *
 * The Points are stored separately from the Borders, that define the Planes,
 * because of Storage Optimization and because they must be transformed only once.
 * Negative Indices in the Planes convert to 'null' in the Polygon,
 * which again results in the Breaking of the Line.
 * In the same way you could assume Lines as always closed
 * and mark then as open by using null as the last Point.
 * But this is the same as assuming Polygons as open
 * and close them by adding the same Point at the End.
 * I think the best is to leave this open and allow for a both,
 * programmatic and data driven Solution like done here and in Polygon2D.
 *
 * Conversion of a 3-dim Body to two Dimensions consists of simply converting the Points,
 * because the Polygons stay the same.
 *
 * Optimizations:
 * If all Polygons are convex,
 * the Test for the Orientation can be reduced to the first three Points.
 * Polygons with larger numbers are not guaranteed to be in a Plane anyway.
 *
 * Points that are infinitely large because of projective Geometry are not drawn,
 * because they are indicated by an x-Value of MaxInt.
 *
 * Because Points are stored in Objects anyway, the Polygons are stored directly,
 * instead of referencing an Index in the Array of Points.
 *
 * Tools for editing the Bodies are:
 * Enumbering the Points,
 * Enumbering the Polygons,
 * Coloring the Polygons dependent on their Orientation.
 * Drawing their Normals and the Point Normals
 *
 * All these tools should only be switched on demand by requesting a KeyPress.
 *
 * Surface Facettes are Planes within a Surface Plane.
 * They are completely embedded within their Parent Plane
 * Their Visibility is determined by their Parent Plane.
 * They are necessary for easily arranging Surface and textural Patterns
 * within a larger Surface without having to retest the Visibility
 * and with guaranteed later drawing than the Parent Plane.
 *
 * Design Decisions:
 * Planes are defined with the Indices of the Points,
 * not the Points of the Points Array directly.
 * This keeps referential Integrity.
 *
 * The Polygons are created on the fly.
 * Especially when the 3D Points are recalculated, brand new Point2D's are created.
 * These have to be put into the Polygons on calculation.
 * 
 * @deprecated use graphic.mvc.plane2D.VectorPolygon instead
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:58:15Z
 * digest: 5c06d308f3880806f404929266f1490900a082b5258b0fbc8d3fb43f2376f46b
 * stale: false
 * tags: [code/3d_geometry, code/polygon_calculation]
 * concepts: [3D-to-2D Body Projection]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public class Body2D
extends Polygon2D {

	//////////////////////
	//	Local Variables	//
	//////////////////////

	/**List of all Points of the Body, not sorted,
	 * but every Point should appear only once	 */
	//protected Point2D[] Points;	//already inherited from Polygon2D

	/**Sequence of the Planes.	*/
	protected int[] sequence;

	/**Sequence of the Planes.
	 * Maybe not necessary, because it is easy enough to reorder them,
	 * but I choose not to do that, because you can encode hidden Planes
	 * (negative Index) and other Information in the Sequence too.	 */
	public void setSequence(int[] Sequence) {
		this.sequence = Sequence; }

	/**Polygon Planes defining the Body, given by the Number of the Point	 */
	protected int[][] planes;

	/**Polygon Plane Points defining the Body, have to be built with 	 */
	protected Point2D[][] polygons;

	/**Colors of the Polygon Planes	 */
	protected int[] colors;

	/**These Surface Planes are defined by the 3-D-Models.
	 * They lie within a normal Body plane and their Visibility is the same
	 * as that of their home Polygon. 
	 * Nowadays they are replaced by Textures. 
	 */
	protected Point2D[][] surfacePlanes;

	/**Colors of the Surface Polygon Planes	 */
	protected int[] surfaceColors;	//Farben der Oberflaechen

	/**palette of Colors of the Polygon Planes.
	 * If this is null, the Colors are generated directly from the Colors List.	 */
	protected Color[] palette;

	/**Border Color of the Polygons.
	 * Borders are painted in this Color, if it is not null	 */
	public Color borderColor;

	/**
	 * Converts this body's plane polygons into a {@link VectorPolygon}.
	 *
	 * @param oriented whether the resulting matrices should track orientation
	 * @return the equivalent {@link VectorPolygon} representation
	 */
	public VectorPolygon getVectorPolygon(final boolean oriented) {
		VectorPolygon ret = new VectorPolygon(polygons.length); 
		for (int i = polygons.length; --i >= 0;) {
			ret.addItem(new MatrixShort(MatrixShort.getPolygon(polygons[i]), false, oriented));
		}
		return ret;
	}

	/**Creates the Polygons from the Planes.
	 * So both, the Polygons and the Planes are stored redundantly
	 * for easy Redraw and fast Recalculation with setLocation().
	 */
	public void initPlanes() {	//can also calculate the Bounds for this Polygon!
		int i = planes.length;
		this.polygons = new Point2D[i][];
		while (--i >= 0) {
			int[] Plane = planes[i];
			int Index, j = Plane.length;
			Point2D[] Poly = polygons[i] = new Point2D[j];
			while (--j >= 0) {	//Build the Polygons
				if ((Index = Plane[j]) >= 0) {
					Poly[j] = Points[Index];
//				} else { //Subtract Offset 1
//					Poly[j] = null; //null breaks the Polygon!
				}
			}
		}
	}

	/**Constructor taking all Values	 */
	public Body2D(Point2D[] Points, int[][] Planes) {
		super (Points);
		this.planes = Planes;
		initPlanes();
	}

	/**Constructor taking all Values	 */
	public Body2D(Point2D[] Points, Line2D Extent, int[][] Planes) {
		super (Points, Extent);
		this.planes = Planes;
		initPlanes();
	}

	/**Constructor taking all Values	 */
	public Body2D(Polygon2D Polygon_, int[][] Planes) {
		super (Polygon_.getPoints(), Polygon_.getExtent());
		this.planes = Planes;
		initPlanes();
	}

	//////////////
	//	Methods	//
	//////////////

	/**Draws a WireFrame Model of the Body
	 *
	 * Some Specifics have been taken out,
	 * like using an xa Value of MaxInt to indicate an infinite Point
	 * and therefore breaking the Polygon.
	 * This is now done by using 'null' for a Point2D Object.
	 *
	 * Since the Body is supposed to be closed when choosing full == false,
	 * you need to draw only half of the Lines,
	 * e.g. by drawing only lines where the Index of the 2nd Point
	 * is higher than the Index of the 1st Point.
	 * This has to be done with a special Drawing Routine.  */
	public void drawWire(IGraphShape g, boolean full, boolean closed) {
		if (full) { g.drawPolygons( this.polygons, closed);
		} else    { g.drawPolygons(Points, planes, closed); }
	}

	/**Prepares drawing a convex Body by setting the Index to negative Values
	 * for Planes with a wrong Orientation.
	 * This is an Optimization for a quick ReDraw
	 * by saving the Calculation of the Triangle Areas.	 */
	public void prepareKonvex() {
		Point2D[] Poly;
		boolean newInst;
		int i = planes.length;	//reverse Order
		if (newInst = (sequence == null)) sequence = new int[i];
		while (--i >= 0) {
			int Index = i; if (newInst) sequence[i] = i; else Index = sequence[i];
			if (Index >= 0) {  //{negativsetzen von Hide-Konvex, bei mehrmaligem Aufruf}
				Poly = this.polygons[Index];
			    if (((Poly.length > 2) && (Poly[0].AreaTriangle(Poly[1], Poly[2]) < 0))) {
					sequence[i] = -Index; }
			}
		}
	}

	/**Draws the Polygon with the Sequence given in 'Reihe'
	 * If the Index given there is negative, the Plane is a Surface Facette.
	 * Surface Facettes are completely embedded within a Facette, 
	 * their Visibility is solely dependent on the Visibility of the embedding Facette. 
	 *
	 * Only konvex Bodies with konvex Surfaces in the correct Orientation
	 * are painted properly, when 'oriented' is set to true!
	 * The easy Check for Visibility is the main Reason why Surface Polygons
	 * are introduced. 	 
	 */
	public void drawKonvex (final IGraphShape g, final boolean filled, final boolean oriented) {	// ,boolean hidden) {
		if (g == null) {
			return; }
		for (int i = planes.length;	--i >= 0; ) {//reverse Order
			int index = i; if (this.sequence != null) index = this.sequence[i];
			if (index >= 0) {  //{Nullsetzen von Hide-Konvex}
				final Point2D[] polygon = this.polygons[index];
				if (polygon == null) {
					continue; }
			    if ((! oriented) || ((polygon.length > 2) && (polygon[0].AreaTriangle(polygon[1], polygon[2]) > 0)))
				{	//1:	//For testing the Orientation, it is only necessary to test the first three Points!
					if (this.colors != null) { //otherwise paint monochome
						final int Farbe = this.colors[index];
						g.setColor(null != palette ? palette  [Farbe] : new Color(Farbe)); 
					}
					if (filled)	{ g.fillPolygon (polygon, borderColor);
					}   else    { g.drawPolygon (polygon, true); } //choose a wireframe Model, that's faster!
/*					if ((PPr.Grad) && (PW > 0)) //{negativer Grad => Oberflaechen-Facette}
					{ //{Oberflaechen-Facetten bearbeiten}
					   if (hidden) Farbe = this.SurfaceColors[PW];
					   Poly = this.SurfacePlanes[PW]; //GOTO 1
					}
*/				}
			}
		}
	}

}
