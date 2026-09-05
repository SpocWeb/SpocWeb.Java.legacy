package graphic.math3D;

import function.AFunction;
import function.IFunction;
import graphic.Line2D;
import graphic.Point2D;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import math.matrix.MatrixFloat;
import math.vector.VectorFloat;
import math.vector.VectorInt;
import streamIO.object.parser.jdbc.ResultSetSep;
import tools.Parsing;

/** Polygon in 2 or 3 Dimensions. 
 * modeled by a 2Dim float[][] Array, 
 * which could be replaced by a MatrixFloat to allow for dynamic addition of Vectors. 
 * Defines Calculation and Cacheing of Tangents, Normals, BiNormals as well as 
 * Extrusion and Rotation to form 3D Bodies with 2D Surfaces from a 1D Line. 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:43:41Z
 * digest: 11ba61c60c8c82bc2a3606a6356021ceda036422994bcb90e3cd46f5d2df14b8
 * stale: false
 * tags: [code/3d_geometry, code/polygon_calculation]
 * concepts: [3D Polygon]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public class Polygon3D {

	/** Token in the *.PNT File to indicate that the Extent is given too */
	public static String TokenExtent = "Extent";

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * rotates a planar 2D CrossSection in 3D Space and translates it to P
	 * used in Tube
	 */
	public static float[][] rotateXSection(float[][] XSection, float[] x, float[] y, float[] P) {
		int Length = XSection.length;
		float[][] ret = new float[Length][];
		while (--Length >= 0) {
			float[] Pt = XSection[Length];
			ret[Length] = VectorFloat.ADD_AT(VectorFloat.BiLin(x, Pt[0], y, Pt[1]), P); }
		return ret; }

	/**
	 * Computes the consecutive-Point Differences of the given Points, normalizing each in place.
	 * @return the normalized Tangents (and calculates them, if needed)
	 * This is the normal Differentiation Operation!
	 * It can deal with periodic Border Conditions, resulting in the Null Vector for the first Norm.
	 */
	final static public float[][] diff(final float[][] Points, final float[] Lengths, final boolean periodic) {
		int Length = Points.length;
		float[] tmp1, tmp2; //for periodic References. 
		tmp2 = periodic ? Points[0] : Points[--Length]; //if not periodic, one Difference less!
		float[][] diffs = new float[Length] [];
		while (--Length >= 0) { //== Points[i+1].subt(Points[i]);
			tmp1 = tmp2; 
			Lengths[Length] = (float) //unfortunately relies...
				VectorFloat.NORMALIZE_AT(diffs[Length] = //...on Evaluation Sequence!!!
				VectorFloat.SUB(tmp1, tmp2 = Points[Length])); 
		}
		return diffs; }

	//////////////////////
	//	Local Variables	//
	//////////////////////
	
	/** Determines, whether the Polygon is periodic, i.e. closed */
	public boolean periodic;

	////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** Caches the Extent of the Polygon */
	protected Line extent;

	/** @return and calculates Extent of the Polygon in 2 or 3 Dimensions. */
	protected Line getExtentAt() {
		if ((points == null) || (points.length < 2)) {
			return null; }
		if (extent == null) {
			extent = new Line(points[0], points[1]);
			extent.orderAt();
			int numPoints = points.length;
			while (--numPoints > 1) {
				extent.mergeAt(points[numPoints]); }
		} return extent; }

	/** Returns a copy of this Polygon's bounding Extent, calculating it first if not yet cached.
	 * @return the Extent of the Polygon */
	public Line getExtent() { return (Line)getExtentAt().copy(); }

	///////////////////////////////////////////////////////////////////////////////////

	/** Cache for the List of all Points of the Polygon */
	protected float[][] points;

	/** Returns the Number of the Points in this Polygon */
	public int getNumPoints() { return points.length; }

	/** Returns the Points of this Polygon */
	public float[][] getPointsAt() { return points; } //should be protected!

	///////////////////////////////////////////////////////////////////////////////////

	/** Local Buffer to store the Lengths of the Tangents */
	protected float[] lengths;

	/** Returns the Lengths of the Tangents */
	public float[] getLengths() { getTangents(); return lengths; }

	///////////////////////////////////////////////////////////////////////////////////

	/** Local Buffer to store the Tangents */
	protected float[][] tangents;

	/** Returns the Tangents (and calculates them, if needed) */
	public float[][] getTangents() { //this is the normal Differentiation Operation
		if (tangents == null) {
			lengths = new float[points.length];
			tangents = diff(points, lengths, periodic); }
		return tangents; }

	///////////////////////////////////////////////////////////////////////////////////

	/** Local Buffer to store the Torsions */
	protected float[] bendings;

	/** Returns the Lengths of the Torsions */
	public float[] getBendings() { getNormals(); return bendings; }

	///////////////////////////////////////////////////////////////////////////////////

	/** Local Buffer to store the Normals */
	protected float[][] normals;

	/** Returns the Normals (and calculates them, if needed) */
	public float[][] getNormals() { //The Normals have to be shifted by 1
		if (normals == null) {
			bendings = new float[points.length];
			normals = diff(getTangents(), bendings, periodic); }
		return normals; }

	///////////////////////////////////////////////////////////////////////////////////

	/** Local Buffer to store the Torsion Vectors */
	protected float[] torsions;

	/** Returns the Lengths of the Torsion Vectors */
	public float[] getTorsions() { getBiNormals(); return torsions; }

	///////////////////////////////////////////////////////////////////////////////////

	/** Local Buffer to store the BiNormals */
	protected float[][] biNormals;

	/** Returns the Cross Product of the Tangents and Normals at each Point, calculating and caching it if needed.
	 * @return the BiNormals (and calculates them, if needed) */
	public float[][] getBiNormals() { //The BiNormals have to be shifted by 1
		if (biNormals == null) { //create the Cross Product
			float[][] Tangents = getTangents(); //1 Point  less, except when periodic
			float[][] Normals = getNormals(); //2 Points less, except when periodic
			int Length = Normals.length;
			biNormals = new float[Length][];
			torsions = new float[Length];
			while (--Length >= 0) {
				torsions[Length] = (float)
					VectorFloat.NORMALIZE_AT  (biNormals[Length] =
					MatrixFloat.MUL_CROSS(Tangents [Length], Normals[Length])); }
		}
		return biNormals; }

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////


	/**
	 * Constructor, that takes the Points as a List of Points (Rows of a float[][]).
	 * Creates a full (deep) Copy of the Points.
	 */
	public Polygon3D(final float[][] Points_) { this(Points_, true); }

	/**
	 * Constructor, that takes the Points either as Rows or as Columns of a float[][].
	 * Creates a full (deep) Copy of the Points.
	 * Transposes the Points, when they are given as a Set of Coordinate Lists.
	 */
	public Polygon3D(final float[][] Points_, final boolean ListOfPoints) {
//		this.Points = Points;
//		int numPoints = Points_.length;
		if (ListOfPoints) { //leave the Order as it is...
			this.points = MatrixFloat.COPY(Points_);
		} else { //transpose the Points
			this.points = MatrixFloat.TRP(Points_);
		}
	}

	/** Constructor, that takes the Points as a List of float[]s. Creates a shallow Copy of the Points and the Extent. */
	public Polygon3D(final float[][] Points, final Line Extent) {
		this.points = Points;
		this.extent = Extent;
	}

	/**
	 * Constructor, that takes a ResultSet containing the Points.
	 * @param RS the ResultSet containing the Points.
	 * @param Cols the Column Numbers containing the Coordinates
	 * @param numPoints the maximum Number of Points to read
	 * @param readExtent expects the Extent in the first two Points.
	 * The Extent is either calculated or read.
	 */
	public Polygon3D(final String FilePath) throws FileNotFoundException, IOException, SQLException {
		final ResultSetSep rs = new ResultSetSep(FilePath); 
		init(rs);
		rs.close(); //close the rs before gc to close the File
	}

	/**
	 * Constructor, that takes a ResultSet containing the Points.
	 * @param RS the ResultSet containing the Points.
	 * @param Cols the Column Numbers containing the Coordinates
	 * @param numPoints the maximum Number of Points to read
	 * @param readExtent expects the Extent in the first two Points.
	 * The Extent is either calculated or read.
	 */
	public Polygon3D(final ResultSetSep rs) throws java.sql.SQLException {
		init(rs);
	}

	/**
	 * Constructor, that takes a ResultSet containing the Points.
	 * @param RS the ResultSet containing the Points.
	 * @param Cols the Column Numbers containing the Coordinates
	 * @param numPoints the maximum Number of Points to read
	 * @param readExtent expects the Extent in the first two Points.
	 * The Extent is either calculated or read.
	 */
	public Polygon3D(final ResultSet RS, final int numPoints) throws java.sql.SQLException {
		init(RS, numPoints, -1, null); }

	/**
	 * Constructor, that takes a ResultSet containing the Points.
	 * @param RS the ResultSet containing the Points.
	 * @param Cols the Column Numbers containing the Coordinates
	 * @param numPoints the maximum Number of Points to read
	 * @param readExtent expects the Extent in the first two Points.
	 * The Extent is either calculated or read.
	 */
	public Polygon3D(ResultSet RS, int numPoints, int[] Cols) throws java.sql.SQLException {
		init(RS, numPoints, -1, Cols); }

	/** Empty Constructor for the Superclass to initialize itself.	 */
//	protected Polygon3D() { }

	/**
	 * Initializer
	 * @param rs
	 * @throws SQLException
	 */
	private void init(final ResultSetSep rs) throws SQLException {
		try {
			final int numPointsEstimate = (int) rs.getMaxNumRowsLeft() >> 1; //for single Character Coordinates
			init(rs, numPointsEstimate);
		} catch (IOException x) {
			throw new SQLException(x.toString()); 
		}
	}

	protected void init(final ResultSet rs, final int numPointsEstimate) throws SQLException {
		final ResultSetMetaData rsMeta = rs.getMetaData();
		final int numCols   = rsMeta.getColumnCount(); //always constant!
		final int[] Cols = VectorInt.IDENTITY(numCols);
		init(rs, numPointsEstimate, numCols, Cols); 
	}

	/**
	 * Initializer, that takes a ResultSet containing the Points.
	 * @param RS the ResultSet containing the Points.
	 * @param numPointsEstimate, the maximum Number of Points to read, -1 if unknown
	 * @param dim the Number of Dimensions for the Points; if -1 dynamically evaluated!
	 * @param Cols the Column Numbers containing the Coordinates
	 * @param readExtent expects the Extent in the first two Points.
	 * The Extent is either calculated or read.
	 */
	protected void init(final ResultSet rs, final int numPointsEstimate, 
		final int dim, final int[] Cols) throws java.sql.SQLException { //Simple Parsing is enough, no nested Structures
		int extCount = -1;
		float[][] extent = new float[2][]; //Cols.length];
		//ext[0] = VectorFloat.readVector(rs, ext[0], Cols);
		//ext[1] = VectorFloat.readVector(rs, ext[1], Cols);
		//if (dim >= 0) //saves dynamic Re-Allocation!
		//	this.points = new float[Math.abs(numPointsEstimate)][dim];
		//else 
			this.points = new float[Math.abs(numPointsEstimate)][];
		int len = -1;
		while (++len < numPointsEstimate) { //keep the Order!!!
			if  (len >= points.length) {  //enlarge the Array
				final float[][] tmp = new float[len + len + 1][];
				System.arraycopy(points, 0, tmp, 0, len);
				points = tmp; }
			final float[] point; 
			if (null == (point = VectorFloat.READ_VECTOR(rs, points[len], Cols))) 
				break; 
			if (point.length <= 0) {
				--len; continue; }
			final String comment = rs.getString(3); 
			if ("Minimum".equals(comment) || 
				"Maximum".equals(comment)) { //Extension: Minimum or Maximum
				points[len] = new float[point.length]; 
				--len; extent[++extCount] = point; 
			} else 
				points[len] = point; 
		}
		if (len < points.length) { //if there were no new Points
			float[][] tmp = new float[len][];  //shrink the Array
			System.arraycopy(points, 0, tmp, 0, len);
			points = tmp; }
		if (extent[0] != null)
			this.extent = new Line(extent[0], extent[1]); 
	}

	/**
	 * Constructor, that takes a String Description of the Points like the one in the *.Pol Files.
	 * The Extent is either calculated or read.
	 */
	public Polygon3D(java.io.StreamTokenizer ST, boolean withExtent) throws java.io.IOException { //Simple Parsing is enough, nested Structures
		// are delimited by different Characters.
		int numPoints = (int) Parsing.nextNumber(ST, true); //Number	of Points
		int Dim = (int) Parsing.nextNumber(ST, true); //Dimension of Points
		if((Dim <= 0) || (numPoints == Dim)) {
			Dim  = 3; } //Default!
		if (withExtent) {
			extent = new Line(
				Parsing.parseList2float(ST, Dim, true), //Minimum Extent
				Parsing.parseList2float(ST, Dim, true)); //Maximum Extent
		} //read until the next Number	//no Extent!
		//		ACopyAble.nextToken(ST, ST.TT_EOL, 4);	//Skip the next 5 EOLs
		points = new float[numPoints][]; //Dim];
		int j = -1;
		while (++j < numPoints) { //Skip up to the End of the Line
			points[j] = Parsing.parseList2float(ST, Dim, true); }
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Scales the Points of the Polygon
	 * Used to prevent direct access to the Coordinates
	 */
	public void scalePoints(float Factor) {
		MatrixFloat.MUL_AT(points, Factor); }

	/** Calculating the 2D Extent from the 3D Extent is faster, 
	 * because only the 8 Corners are used and not all the Points! */
	public Line2D getExtent2D(final ICoordMapper CD) {
		final Line extent = getExtentAt();
		if (extent == null) {
			return null; }
		final float[][] corners = extent.getCorners(); //Create ALL Corners
		//of the multidimensional Extent: 2^D Points and start with the first two Points
		int length = corners.length; //for calculating the mapped Extent
		Point2D p0 = null; 
		while (--length >= 0) {
			if (null != (p0 = CD.mapPt(corners[length]))) {
				break; }
		}
		Point2D p1 = null; 
		while (--length >= 0) {
			if (null != (p1 = CD.mapPt(corners[length]))) {
				break; }
		}
		if (p1 == null) { //prevent Null Pointer Exception, no Extent, at most a single Point! 
			return null; }
		Line2D Extent = new Line2D(p0, p1);
		while (--length > 1) { //and loop through the Rest.
			Extent.mergeAt(CD.mapPt(corners[length])); }
		return Extent; }

	/**Calculates the 2D Polygon from this 3D Polygon, using the given Coordinate System 
	 * @deprecated 
	 */
	public graphic.Polygon2D getPolygon2D(final ICoordMapper CD) {
		return new graphic.Polygon2D(CD.mapPt(points), getExtent2D(CD)); }

	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Object: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Writes the Data out in the *.POL Format
	 * This requires the float[]s to be printed without Prefix
	 * and with CRLF as Suffix.
	 */
	public String toString() {
		String SepBuffer = Parsing.SeparatorLength; //buffer the Separator
		Parsing.SeparatorLength = null; //Don't give out the Dimensions for each Point
		int Dim = points[0].length; //because they are (should be) all the same.
		StringBuffer Buffer = new StringBuffer((points.length + 2) * //+ Min/Max
			((Dim) * 21 //max. 21 Positions = 18 +Sign + Dot +Space
			+ 3) //CRLF and Space
			+ 100); //extra Characters
		Buffer.append(points.length + " EckPunkte (x,y,z), \n"); // + Name
		Buffer.append(Dim + " Dimensionen \n"); // + Name
		Buffer.append(extent.toString());
		//		Buffer.append(Extent.a[0] + " Minimum \n");
		//		Buffer.append(Extent.a[1] + " Maximum \n");
		Buffer.append(Parsing.toString(points));
		Parsing.SeparatorLength = SepBuffer;
		return Buffer.toString(); }

	/**
	 * Creates the Tangents and Normals
	 * Shifts these (if periodic) or creates artificial closing Vectors
	 * for the Routines Thickened() and Tube().
	 */
	protected void prepareNormals() {
		getNormals (); //2 Points less
//		getTangents(); //unnecessary after getNormals...
		int Length = points.length;
		int len2 = Length - 1;
		float[][] tmp = new float[Length] [];
		if (!periodic) { //otherwise the Tangents are already o.k.
			System.arraycopy(tangents, 0, tmp, 0, Length - 1); //1 Point  less
			tangents = tmp; tmp = new float[Length] [];
			tangents[len2] = tangents[--len2];
		} //only the Normals have to be shifted by 1
		System.arraycopy(normals, 0, tmp, 1, len2); //shift by 1!
		if (periodic) { //reuse the last Normal
			tmp[0] = normals[Length - 1];
		} else { //create the last 2 Normals as orthogonal to the last 2 Tangents
			float[] tng1 = tangents[0];
			float[] tng2 = tangents[Length - 2]; //Lengths are 1 => don't normalize anymore
			VectorFloat.NORMALIZE_AT(tmp[0] = VectorFloat.SUB_PART(tmp[1], tng1)); //tng1.SqrNorm());
			VectorFloat.NORMALIZE_AT(tmp[Length - 1] = VectorFloat.SUB_PART(tmp[Length - 2], tng2)); //tng2.SqrNorm());
		}
		normals = tmp;
	}

	/**
	 * Creates a (topologically) planar Ribbon from this (2D or 3D) Polygon
	 * by widening it in the Direction of it's Normals.
	 * Useful for creating solid Figures from lined Characters in 2D
	 * but also for 3D Lines to simulate something like a thick Ribbon.
	 *
	 * All the Normal Vectors are created and multiplied with the Thickness.
	 * A Set of separate Planes is created, because with a large Polygon it would not be possible
	 * to create closed (periodic) Figures like Circles etc!
	 * Instead of connecting them by a Border that contains the original Polygon
	 * as the first Side and the new Points in reversed Order.
	 * This relies on the Polygon making a Turn on every Point, so the Normals never become 0!
	 */
	public Body3D Ribbon(final float Thickness) {
		prepareNormals(); //just take the Difference between consecutive Points
		int Length = points.length; //as the Direction, i.e. the Differential
		int len2 = Length;
		if (!periodic) len2--;
		int[][] Planes = new int[len2][4]; //create a new Trapez for each line
		float[][] xPoints = new float[Length << 1][];
		System.arraycopy(points, 0, xPoints, 0, Length); //shallow-copy the current Points, see below
		int curr = Length;
		int prev = 0;
		while (--curr >= 0) {
			float[] tmp = normals[curr];
//			xPoints[curr] = Points[curr].copy();//deep- copy of the current Points in here!, see above
			float Prod = (float)	 VectorFloat.MAP(tmp, tangents[curr]);
			xPoints[curr + Length] = VectorFloat.   addProd(points[curr], tmp, Thickness / (float)Math.sqrt(1 - Prod * Prod));
			if (curr < len2) //depends on whether the Polygon is closed
			{ //with a closed Polygon it is not wanted to connect the first time.
				int[] Plane = Planes[curr];
				Plane[0] = prev;
				Plane[1] = curr;
				Plane[2] = curr + Length;
				Plane[3] = prev + Length;
			}
			prev = curr;
		}
		return new Body3D(xPoints, Planes, false); }

	/**
	 * Creates a 3D Tube along this (2D or 3D) Polygon
	 * with the given closed (2D) Polygon as the Cross Section Plane.
	 * This XSection can be formed / warped by 'formXSection' (if it is != null),
	 * which is called after each Step along this Polygon.
	 * but the Mapping must retain the Number of Points.
	 * Very similar to 'thicken'ing the Line.
	 * This Routine relies on the Polygon making a Turn on every Point!
	 * Design Decisions:
	 * It would have been sufficient to make formXSection an Operation,
	 * but this is more flexible, allowing to exchange the whole XSection,
	 * e.g. by Samples from a cached Series of morphed XSections.
	 */
	public Body3D Tube(float[][] XSection, final IFunction formXSection) {
		prepareNormals();
		getBiNormals(); //additionally...
		int Length = points.length;
		int BLength = XSection.length;
		int len2 = Length;
		if (!periodic) len2--;
		int[][] Planes = new int[len2 * BLength][4];
		float[][] xPoints = new float[Length * (BLength + 1)][];
		int currPlane = 0;
		int curr = Length; //the last  Polygon Point
//		int prev = 0;				//the first Polygon Point, for closing, not used here
		int curr0 = xPoints.length; //the last  Point in the last  Circle
		int prev0 = curr0 - BLength; //the first Point in the last  Circle, for closing
		int prev1 = Length; //the first Point in the first Circle, for closing
		int curr1 = prev1 + BLength; //the last  Point in the first Circle
		System.arraycopy(points, 0, xPoints, 0, Length); //shallow-copy the current Points, see below
		while (--curr >= 0) //Process solutions are faster, but harder to debug!
		{ //Sometimes it is easier to construct a running solution where the Planes are constructed in a Process,
			int numPts = XSection.length; //but a closed Expression for all Polygon Planes is easier to program correctly!
			float[][] Ring = rotateXSection(XSection, normals[curr], biNormals[curr], points[curr]);
			System.arraycopy(Ring, 0, xPoints, Length * (curr + 1), numPts); //add this Ring to the other Points
			if ((formXSection != null) && (curr > 0)) {
				XSection = (float[][]) formXSection.Map(XSection);
			} //form the XSection to a new Shape, not needed on the last Iteration
			//			xPoints[curr		 ] = (float[]) Points[curr].copy();//deep- copy of the current Points in here!, see above
			if (curr < len2) //depends on whether the Polygon is closed
			{ //with a closed Polygon it is not wanted to connect the first time.
				int BLen = BLength; //The XSection is assumed to be always closed.
				while (--BLen >= 0) //Create the Polygons
				{ //for each Point of the XSection, a Plane is created.
					int[] Plane = Planes[currPlane++]; //All Planes contain 4 Points
					Plane[0] = prev1;
					Plane[1] = --curr1;
					Plane[2] = --curr0;
					Plane[3] = prev0;
					prev0 = curr0;
					prev1 = curr1;
				}
			}
			//			prev  = curr ;	//not used here!
			prev1 = prev0; //curr0 is just counted down, only prev and curr1 have to be set
			prev0 = curr0 - BLength;
			curr1 = curr0 + BLength;
		}
		return new Body3D(xPoints, Planes, false);
	}

	/**
	 * Creates a 3D Tube from this Polygon and the given Set of Cross Section Planes.
	 * These Planes must be Polygons with all the same Number of Points.
	 * If the Number of Planes doesn't match the Number of Points in this Polygon,
	 * they are used in wrap around Order.
	 */
	public Body3D Tube(final float[][][] XSections) {
		return Tube(XSections[0], new EnumFormXSection(XSections)); }

	/**
	 * Creates a 3D Rotation Body around the y Axis with Cross Sections
	 * from this (closed) Polygon (in 2D).
	 * This is the special Case of creating a Tube
	 * around a straight Line with Stops at the y Values of the XSection
	 * and with constant XSection Scaling in x/z Direction
	 * given by the x Coordinate to allow e.g. Helices and Ammonites.
	 * @param Origin	of the new Polygon
	 * @param Direction Vectors
	 * @param XSection List of Cross Sections to apply
	 * @param Radius
	 */
	public Body3D Rotation(final float[] Origin, final float[] Direction, final float[][] XSection, final float Radius) {
		int Length = XSection.length;
		float[][] TubePoints = new float[Length][];
		while (--Length >= 0) { //new Polygon at Origin with Directions scaling the Points.
			TubePoints[Length] = VectorFloat.addProd(Origin, Direction, points[Length][1]); }
		Polygon3D Tube = new Polygon3D(TubePoints);
		return Tube.Tube(XSection, new RotateFormXSection(points, XSection)); }

}


/**
 * Helper IFunction Class to help drawing Rotation Bodies
 * Used only in Rotation().
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:43:41Z
 * digest: 4d05f74052c8e55377cbac74453a9fdf6432748f27703d9be18e6464afa64401
 * stale: false
 * tags: [code/3d_geometry]
 * concepts: [Rotated-Form Cross-Section]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
class RotateFormXSection extends AFunction {

	/** Counter for the Calls of 'Function' */
	int counter;

	/** Cache for the XSection to be used in consecutive Calls of 'Function' */
	float[][] XSection;

	/** Cache for the Shape to be used in consecutive Calls of 'Function' */
	float[][] Points;

	/** Initializing Constructor */
	public RotateFormXSection(float[][] Points, float[][] XSection) {
		//		counter = 0;	//not necessary!
		this.XSection = XSection;
		this.Points = Points;
	}

	/** returns the next XSection, as a scaled Copy of the original XSection. */
	public Object Map(Object arg) {
		float[][] arg_ = (float[][]) arg;
		int Length = arg_.length;
		float Radius = Points[++counter][0];
		while (--Length >= 0) {
			VectorFloat.MUL(arg_[Length], XSection[Length], Radius); }
		return arg; }

}


/**
 * Helper IFunction Class to help constructing Rotation Bodies with arbitrary XSections.
 * by returning the next Shape (must all have the same Number of Points!)
 * with each Map() Call.
 * Used in Wrap around Order, if more Map() Calls happen.
 * Used in Tube() exclusively
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:43:41Z
 * digest: 6b21327e01ec8cedc56f32a7fe5b0f6c8716bbfed32c82f24b81424b864b018f
 * stale: false
 * tags: [code/3d_geometry]
 * concepts: [Cross-Section Form Enumeration]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
class EnumFormXSection extends AFunction {

	/** Counter for the Calls of 'Function' */
	int counter; // = 0; //not necessary!

	/** Cache for the XSection to be used in consecutive Calls of 'Function' */
	float[][][] XSections;

	/** Initializing Constructor */
	public EnumFormXSection(float[][][] XSections) {
		this.XSections = XSections;
	}

	/** returns the next XSection from the List. */
	public Object Map(Object arg) {
		if (++counter >= XSections.length) {
			counter = 0; }
		return XSections[counter]; }

}
