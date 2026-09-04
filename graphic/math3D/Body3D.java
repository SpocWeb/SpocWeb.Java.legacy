package graphic.math3D;

import graphic.Body2D;
import graphic.Point2D;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.sql.ResultSet;

import math.matrix.MatrixFloat;
import math.matrix.MatrixInt;
import math.vector.VectorFloat;
import math.vector.VectorInt;
import streamIO.Log;
import streamIO.object.parser.jdbc.ResultSetSep;
import tools.Parsing;

/**
 * 2  or 3 dimensional Mapping of a Body consisting of Planes.
 * The Body can be a genuine 2 dimensional Body
 * or the Mapping of a 3 dimensional Body to two Dimensions.
 * 
 * Data that is stored is: 
 * The Points as Array of float[][] stemming from Polygon3D  
 * The Polygons as Array of int[][] pointing to the Points
 * The Color of each Polygon if shaded flat (could also be the 0th Polygon Point) 
 * 
 * Data that is calculated and cached is: 
 * The Mids of the Polygons
 * The Normals of the Polygons 
 * The Normals of the Points 
 * 
 * Missing is...
 * Skeletal  Info (Ms3dJoint)
 * Animation Info (Ms3dKeyFrame)
 * Textural  Info (Ms3dTextureMap, put into separate File against Redundancy)
 * Texture   Storage (Ms3dTexture)
 * 
 * The Points are stored separately from the Borders, that define the Planes,
 * because of Storage Optimization and because they must be transformed only once.
 *
 * Conversion of a 3-dim Body to two Dimensions consists of simply converting the Points,
 * because the Polygons stay the same.
 * 
 * Optimizations:
 * If all Polygons are convex,
 * the Test for the Orientation can be reduced to the first three Points.
 * Polygons higher than Triangles are not guaranteed to be in a Plane anyway.
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
 * So far the 3D Graphics is contained within is Object for easy calculation.
 * Maybe later separate the Graphics from the abstract Methods.
 *
 * This prevents some Optimizations like copying and mapping in the same loop.
 * On the other hand, one of these Operations can be skipped at all,
 * if both Entities are properly separated!
 */
public class Body3D
extends Polygon3D {

	/** Logger to be used for Output 	 */
	final static public Log L = new Log(Body3D.class, 1); 

	/////////////////////////////////////////////////////////////////////////////////////

	/** Position of the Color Column in *.PLN Files     */
	public static String TokenColorColumn  = "ColorColumn";

	/** Position of the #Point Column in *.PLN Files     */
	public static String TokenPointColumn  = "PointColumn";

	/** Offset of the Point Values in *.PLN Files     */
	public static String TokenPointOffset = "PointOffset";

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////

	/** Surface Planes are better emulated by Textures!	 */
	//protected float[][][] surfacePlanes3D;

	/** Polygon Plane Colors, given by the Position in the palette	 */
	protected int[] colors;

	////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** Polygon Planes defining the Body, given by the Number of the Points	 */
	protected int[][] planes;

	/**Returns the Planes of this Body
	 * should be protected!	 */
	public int[][] getPlanesAt() { return planes; }

	/**Maximum Number of Points per Plane	 */
	private int maxPointsPerPlane;

	/**Returns the maximum Number of Points per Plane	 */
	public int getMaxPointsPerPlane() {
		if (maxPointsPerPlane <= 0) 	//calculate the Maximum of all Point Numbers
			maxPointsPerPlane = MatrixInt.MAX_LENGTH(planes);
		return maxPointsPerPlane; }

	/**List of all the middle Points of the Planes	 */
	private float[][] mids;

	/**Returns the middle Points of the Planes
	 * to determine the Painting Sequence
	 */
	public float[][] getMidsAt() { 	//should be protected!
		if (mids == null) {
			mids = MatrixFloat.GET_MID_POINTS(points, planes);
		}
		return mids; }

	/**Returns the AbsV Distances of the Mids to the given StandPoint 
	 * as Representatives for the Plane Distances. 
	 */
	public float[] getPlaneDistances(final float[] standPoint) {
		return MatrixFloat.ABSV_DIST(standPoint, getMidsAt()); }

	/**List of all the Normal Vectors of the Planes	 */
	private float[][] planeNormals;

	/**Returns the Vectors orthogonal to the Planes (only for 3Dim Tensors!)	 
	protected float[][] getPlaneNorms() {
		return (float[]) getPlaneNormsAt().copy(); }

	/**Returns the Vectors orthogonal to the Planes (only for 3Dim Tensors!)	 */
	public float[][] getPlaneNormals() { 	//should be protected!
		if (planeNormals == null) {
			planeNormals = MatrixFloat.PLANE_NORMALS(points, planes);
		}
		return planeNormals; }

	/**List of all the Normal Vectors of the Planes	 */
	private float[][] pointNormals;

	/**Returns the Vectors orthogonal to the Points (only for 3Dim Tensors!)	 */
	public float[][] getPointNormals() {	//should be protected!
		if (pointNormals == null) {
			return pointNormals; }
		pointNormals = MatrixFloat.POINT_NORMALS(points.length, planes, planeNormals);
		return pointNormals; }

	/////////////////////////////////////////////////////////////////////////////////////
	//	Constructors	
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor that takes a Matrix with the Points as Rows.
	 * @param decreasePointIndex
	 */
	public Body3D(float[][] Points, int[][] Planes, boolean decreasePointIndex) {
		super(Points);
		this.planes = Planes;
		if (decreasePointIndex) {
			this.planes = MatrixInt.ADD(Planes, -1); }
	}

	/**Constructor, that takes all Characteristics of another Body3D and reuses them	 */
	public Body3D(float[][] Points, Line Extent, int[][] Planes, int MaxPointsPerPlane, boolean decreasePointIndex) {
		super(Points, Extent);
//		int j = Points.length;
		this.points = Points;
		this.planes = Planes;
		this.maxPointsPerPlane = MaxPointsPerPlane;
		if (decreasePointIndex) {
			this.planes = MatrixInt.ADD(Planes, -1); }
	}

	/**
	 * Constructor, that takes a ResultSet containing the Points.
	 * @param PathWoExtension the Path and FileName without Suffixes for the Points and the Planes.
	 * @param Cols the Column Numbers containing the Coordinates
	 * @param numPoints the maximum Number of Points to read
	 * @param readExtent expects the Extent in the first two Points.
	 * The Extent is either calculated or read.
	 * @param PointCols the Column Numbers containing the Point Numbers
	 * @param numPlanes the maximum Number of Planes to read
	 * @param PointOffset the Offset of the Point Numbers (must start at 0!)
	 */
/*	public Body3D(String PathWoExtension,
		boolean readExtent,
		int PointOffset)
		throws FileNotFoundException, IOException, java.sql.SQLException { //Simple Parsing is enough, no nested Structures
		this(new ResultSetSep(PathWoExtension + ".PNT"), Cols, numPoints, readExtent,
			 new ResultSetSep(PathWoExtension + ".PLN"), PointCols, numPlanes, PointOffset); }
*/

	/**
	 * Constructor, that takes a ResultSet containing the Points.
	 * @param PathWoExtension the Path and FileName without Suffixes for the Points and the Planes.
	 * @param Cols the Column Numbers containing the Coordinates
	 * @param numPoints the maximum Number of Points to read
	 * @param readExtent expects the Extent in the first two Points.
	 * The Extent is either calculated or read.
	 * @param PointCols the Column Numbers containing the Point Numbers
	 * @param numPlanes the maximum Number of Planes to read
	 * @param PointOffset the Offset of the Point Numbers (must start at 0!)
	 */
	public Body3D(String PathWoExtension,
		int[] PointCols, int numPoints, 
		int[] PlaneCols, int numPlanes
		) throws FileNotFoundException, IOException, java.sql.SQLException { //Simple Parsing is enough, no nested Structures
		super(new ResultSetSep(PathWoExtension + ".PNT"), numPoints, PointCols);
		final ResultSetSep RSPlanes = new ResultSetSep(PathWoExtension + ".PLN");
		if (numPlanes   <  0) 
			numPlanes    = (int) RSPlanes.getMaxNumRowsLeft() >> 1; 
//		int numPointCols = RSPlanes.getNumCols(); //total Number of Columns
		int PointColumn  = -1;
		int ColorColumn  = RSPlanes.findColumn("Color");
		if (ColorColumn >= 0) { //Colors of the Planes
			colors = new int[numPlanes]; }
		int PointOffset = RSPlanes.findColumn("Sequence");
		int PlaneColOffset = 0;
		init(RSPlanes, numPlanes, PointColumn, PlaneColOffset, PointCols,
		PointOffset, ColorColumn); }


	/**
	 * Constructor, that takes the Path 
	 * @param PathWoExtension the Path and FileName without Suffixes for the Points and the Planes.
	 */
	public Body3D(final String PathWoExtension)
		throws FileNotFoundException, IOException, java.sql.SQLException { //Simple Parsing is enough, no nested Structures
		super(PathWoExtension + ".PNT");
		final ResultSetSep rsPlanes = new ResultSetSep(PathWoExtension + ".PLN");
		int PlaneColOffset = 0;
		int numPlanes    = (int) rsPlanes.getMaxNumRowsLeft() >> 1;
//		int numPointCols = rsPlanes.getNumCols(); //total Number of Columns
		int PointColumn  = -1;
		if (PointColumn >= 0) { //Colors of the Planes
			PlaneColOffset = PointColumn+1; }
		int ColorColumn  = rsPlanes.findColumn("Color");
		if (ColorColumn >= 0) { //Colors of the Planes
			colors = new int[numPlanes]; }
		int PointOffset = rsPlanes.findColumn("Sequence");
		init(rsPlanes, numPlanes, PointColumn, PlaneColOffset, null, PointOffset, ColorColumn); 
		rsPlanes.close(); 
	}

	/**
	 * Constructor, that takes a ResultSet containing the Points.
	 * @param RSPoints the ResultSet containing the Points.
	 * @param Cols the Column Numbers containing the Coordinates
	 * @param numPoints the maximum Number of Points to read
	 * @param readExtent expects the Extent in the first two Points.
	 * The Extent is either calculated or read.
	 * @param RSPlanes the ResultSet containing the Planes.
	 * @param PointCols the Column Numbers containing the Point Numbers
	 * @param numPlanes the maximum Number of Planes to read
	 * @param PointOffset the Offset of the Point Numbers (must start at 0!)
	 */
	public Body3D(
		ResultSet RSPoints, int numPoints, 
		ResultSet RSPlanes, int numPlanes, int PointColumn, int PlaneColOffset, int PointOffset, int ColorColumn)
		throws java.sql.SQLException { //Simple Parsing is enough, no nested Structures
		super(RSPoints, numPoints, null);
		init(RSPlanes, numPlanes, PointColumn, PlaneColOffset, null, PointOffset, ColorColumn); }

	/**
	 * Constructor, that takes a ResultSet containing the Points.
	 * @param RSPoints the ResultSet containing the Points.
	 * @param Cols the Column Numbers containing the Coordinates
	 * @param numPoints the maximum Number of Points to read
	 * @param readExtent expects the Extent in the first two Points.
	 * The Extent is either calculated or read.
	 * @param RSPlanes the ResultSet containing the Planes.
	 * @param PointCols the Column Numbers containing the Point Numbers
	 * @param numPlanes the maximum Number of Planes to read
	 * @param PointOffset the Offset of the Point Numbers (must start at 0!)
	 */
	public Body3D(
		ResultSet RSPoints, int numPoints, int[] PointCols, 
		ResultSet RSPlanes, int numPlanes, int[] PlaneCols, int PointColumn, int PlaneColOffset, int PointOffset, int ColorColumn)
		throws java.sql.SQLException { //Simple Parsing is enough, no nested Structures
		super(RSPoints, numPoints, PointCols);
		init (RSPlanes, numPlanes, PointColumn, PlaneColOffset, PlaneCols, PointOffset, ColorColumn); }

	/** Empty Constructor for the Superclass to initialize itself.	 */
//	protected Body3D() { }

	/**Copy Operation that takes a String Description of Points AND Planes in one
	 * within a streamIO, like the one in the *.Pol Files.
	 * givenNumber = false means that the Number of Points in a Polygon
	 * is only given implicitly. This happens when LengthSeparator == null.  */
	public Body3D(java.io.StreamTokenizer ST, boolean givenExtent, boolean givenNumber, int decreasePointIndex)
		throws java.io.IOException
	{	//Simple Parsing is enough, nested Structures are delimited by different Characters.
		super(ST, givenExtent);	//Processed the Points, now Processing the Planes...
		int NumPoints = (int) Parsing.nextNumber(ST, StreamTokenizer.TT_EOL, 1, !givenNumber);	//maximum Number of Points in a Plane, can be larger than the actual max. Number
		if (givenNumber) //if 'const' appears behind NumPoints,
		{	//this signals that all Planes have the same number of Points.
			givenNumber = !((ST.nextToken() == StreamTokenizer.TT_WORD) && ("const".equals(ST.sval)));	//String-Vergleich nur über Equals!!!
			while (ST.nextToken() != StreamTokenizer.TT_EOL);
			maxPointsPerPlane = NumPoints;
		}
		int numPlanes = (int) Parsing.nextNumber(ST, StreamTokenizer.TT_EOL, 6, true);	//Skip Description;
		this.planes	= new int [numPlanes][];
		int j = -1;
		while (++j < numPlanes) {	//Leave the Color out for now...
			if (givenNumber) NumPoints = (int) Parsing.nextNumber(ST, true);
			this.planes [j] = Parsing.parseList2int(ST, NumPoints, true);
		}
		if (decreasePointIndex != 0) {
			this.planes = MatrixInt.ADD(planes, decreasePointIndex); }
	}

	/**
	 * Initializer, that takes a ResultSet containing the Points.
	 * @param rsPlanes the ResultSet containing the Planes.
	 * @param numPlanesEstimate the maximum Number of Planes to read, -1 if unknown!
	 * @param numPointColumn
	 *        if positive: the Column containing the Number of Points
	 *        if negative: the Number of Points is constantly this!
	 * @param planeColumnOffset The Column Offset to start from when PointCols == null
	 * @param pointColumns the List of Columns to read the Point Numbers, when null, consecutive starting at PointOffset
	 *        if null, the Number of Points is determined dynamically for each Plane/Row!
	 * @param pointOffset the Offset subtracted from the Point Numbers (must start at 0!)
	 * @param colorColumn 
	 * 		  if positive: the Column of the Color Numbers
	 *        if negative: the Color of the Planes is constantly this!
	 * 		
	 */
	protected void init(final ResultSet rsPlanes, final int numPlanesEstimate,
		final int numPointColumn, final int planeColumnOffset,
		final int[] pointColumns,
		final int pointOffset, final int colorColumn )
		throws java.sql.SQLException {
		final MatrixInt matrix = new MatrixInt(numPlanesEstimate);
		matrix.read(rsPlanes, numPlanesEstimate, planeColumnOffset, -1);
		if (pointOffset != 0) //some Graphs start counting at 0, some at 1
			matrix.addAt(-pointOffset); 
		this.planes = matrix.getItems(1); //Colors have to be read separately
		/*
		if (numPointColumn < -1) {
			this.planes = new int[Math.abs(numPlanesEstimate)][-numPointColumn];
		} else {
			this.planes = new int[Math.abs(numPlanesEstimate)][];
		}
		final VectorInt vector = new VectorInt(10); //TODO: hardcoded Capacity
		int len = -1;
		while (++len < numPlanesEstimate) { //keep the Order!!!
			int[] plane;
			if  (len >= planes.length) { //enlarge the Array
				final int[][] tmp = new int[len + len + 1][];
				System.arraycopy(planes, 0, tmp, 0, len);
				planes = tmp; }
			final VectorInt row; 
			if (null == (row = vector.read(rsPlanes, planeColumnOffset))) {
				break; }
			plane = planes[len] = row.getItems(false);
			if((colors != null)   &&    (0 <= colorColumn)) { //read the Color
				colors[len] = rsPlanes.getInt(colorColumn); }
			if (pointOffset != 0) {
				VectorInt.ADD_AT(plane, -pointOffset); }
		}
		if (len < planes.length) { //if there were no new Points
			final int[][] tmp = new int[len][]; //shrink the Array
			System.arraycopy(planes, 0, tmp, 0, len);
			planes = tmp; 
		}
		*/
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Calculates the 2D Body, using the given Coordinate System.
	 * Also calculates the Sequence of Planes by the Distance to the Viewer.
	 *
	 * The 2D Extent is not only the mapped 3D Extent!
	 * It can be calculated from the mapped Points
	 *		   or estimated from the Extent of the mapped 3D Extent,
	 * which is done here. 	 */
	public Body2D getBody2D(ICoordMapper CD) {
		final Point2D[] points2D = CD.mapPt(points); 
		return new Body2D (points2D, getExtent2D(CD), planes); }

	/**Returns the Sequence resulting from the Distances of the Mids.
	 * TODO: Needs a Reference to the Array Package!  	 */
/*	public int[] PlaneSequence(float[] StandPoint) {
		Array.Array PlaneDistances = new Array.Array(PlaneDistances(StandPoint));
		Array.Index Index = new Array.Index(PlaneDistances);
		return Index.createIntArray(true); } //get the Rank leads to an ordered Access
*/
	//TODO: There are many ways to draw a 3D Body:
	//only some can be covered by the Body2D!

	/**Returns the mapped Mids of this Body's Planes	 */
	public Point2D[] getMids(final ICoordMapper CD) {
		final float[][] Mids = getMidsAt(); 
//		int Length = Mids.length;
		final Point2D[] MidPoints = CD.mapPt(Mids);
//							 new Point2D[Length];
//		while (--Length > 1)
//			MidPoints[Length] = CD.map(Mids[Length]);
		return MidPoints; }

	/**Writes the Data out in the *.POL Format
	 * This requires the Tensors to be printed without Starter and with CRLF as Ender.	 */
	public String toString() {
//		int Dim = Points[0].length;
		StringBuffer Buffer = new StringBuffer(super.toString());
		Buffer.ensureCapacity(Buffer.length()	+ planes.length * 4	//max 4 Characters per Number
												* (getMaxPointsPerPlane() + 2)	//max Number of Points
												+ 100);	//extra Characters
		Buffer.append("\n" + maxPointsPerPlane + " max.#Ecken/Flaeche \n");
		Buffer.append("\nFormat: \n#Ecken   Farb-Nr.\nReihenfolge der Ecken\n\n\n");
		Buffer.append(planes.length + " Flaechen\n\n");
		Buffer.append(Parsing.toString(planes));
		return Buffer.toString(); }

	/** writes the Data of this Object to a File with the given Name */
	public void stream(final String filePath) throws IOException {
		streamVertices(filePath+".PNT");
		streamFacets(filePath+".PLN");
	}
	
	/** writes the Data of this Object to a Stream */
	public void streamVertices(final String filePath) throws IOException {
		streamVertices(new File(filePath));
	}
	
	/** writes the Data of this Object to a Stream */
	public void streamVertices(final File ps) throws IOException {
		FileOutputStream fos = new FileOutputStream(ps); 
		streamVertices(fos);
		fos.close(); 
	}
	
	/** writes the Data of this Object to a Stream */
	public void streamVertices(final OutputStream ps) {
		streamVertices(new PrintStream(ps));
	}
	
	/** writes the Data of this Object to a Stream */
	public void streamVertices(final PrintStream ps) {
		for (int i = -1; ++i < points.length; ) {
			VectorFloat.STREAM(points[i], ps, '\t'); ps.println(); 
		}
	}
	
	/** writes the Data of this Object to a Stream */
	public void streamFacets(final String filePath) throws IOException {
		streamFacets(new File(filePath));
	}
	
	/** writes the Data of this Object to a Stream */
	public void streamFacets(final File ps) throws IOException {
		FileOutputStream fos = new FileOutputStream(ps); 
		streamFacets(fos);
		fos.close(); 
	}
	
	/** writes the Data of this Object to a Stream */
	public void streamFacets(final OutputStream ps) {
		streamFacets(new PrintStream(ps));
	}
	
	/** writes the Data of this Object to a Stream */
	public void streamFacets(final PrintStream ps) {
		for (int i = -1; ++i < planes.length; ) {
			VectorInt.STREAM(planes[i], ps, '\t'); ps.println(); 
		}
	}
	
	/**Creates an extruded 3D Body from a planar Body (in 2D)
	 * useful for creating solid Figures from Characters.
	 * relies on the Body having only 2 Dimensions
	 * and being closed and right-oriented.
	 */
	public Body3D extrude(final float Height)	{
		int LengthPt  = points.length;
		int LengthPt2 = LengthPt;
		int LengthPl  = planes.length;
		int LengthPl2 = LengthPl;
		int numLines = 0;	//Sum up the Number of Lines = num of side Planes.
		while (--LengthPl2 >= 0) numLines += planes[LengthPl2].length;	//assume closed Lines here!
		float[][] xPoints = new float[ LengthPt << 1][3];
		int  [][] xPlanes = new int  [(LengthPl << 1) + numLines][];
		float[] T2;
//		System.arraycopy (Points, 0, xPoints, 0, Length);	//Copy the original Points
		while (--LengthPt2 >= 0) {
			VectorFloat.COPY(     points[LengthPt2]          , xPoints[LengthPt2 ]);
			VectorFloat.COPY(points[LengthPt2], T2 = xPoints[LengthPt2 + LengthPt]);
			T2[2] = Height;	//re-use the old Coordinates
		}
		System.arraycopy (planes, 0, xPlanes, 0, LengthPl);	//Copy the original Planes
		int numPlanes = LengthPl << 1; //start with the new Planes after the last opposite Plane and count up.
		LengthPl2 = LengthPl;
		while (--LengthPl2 >= 0)	//for all Planes...
		{	//Create the opposite Planes:
			//Copy the Planes, increment them by a fixed Offset and change their Orientation
			int[] xPlane = xPlanes[LengthPl2 + LengthPl] = VectorInt.REVERSE(planes[LengthPl2], LengthPt);
			int LenPl = xPlane.length;
			int curr, prev  = xPlane[0];
			while (--LenPl >= 0)	//Create all the side Planes
			{	//Create the side Planes at the End, for each Line one!
				int[] SidePlane = new int[4];	//always have 4 Points
				SidePlane[0] = prev;
				SidePlane[1] = curr = xPlane[LenPl];
				SidePlane[2] = curr-LengthPt;
				SidePlane[3] = prev-LengthPt;
				xPlanes[numPlanes++] = SidePlane;
				prev = curr;
			}
		}
		final Line Extent = getExtent();
		float[] xMin = new float[3]; VectorFloat.COPY(Extent.a[0], 0, 2, xMin);
		float[] xMax = new float[3]; VectorFloat.COPY(Extent.a[1], 0, 2, xMin);
		if (Height > 0) {
			xMax[2] = Height;
		} else {
			xMin[2] = Height; }
		return new Body3D(xPoints, new Line(xMin, xMax), xPlanes, Math.max(getMaxPointsPerPlane(), 4), false); }

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	private static final String DEFAULT_GRAPHICS = "../../Databases/POLYEDER/Helicopter";
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws Exception {
		L.n("Testing " + Body3D.class.getName());
		main(new String[] { DEFAULT_GRAPHICS }); 
	}
		
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		if (args.length == 0) 
			testIt(); 
		final Body3D body3DG = new Body3D(args[0]);
		L.n("Number of Planes:"+body3DG.planes.length);
	}
		
}
