package graphic.math2D;

import function.byref.ByRefFloat;
import graphic.math3D.ISpatial;
import graphs.MatrixGraph;
import graphs.SparseGraph;

import java.security.InvalidAlgorithmParameterException;

import math.matrix.MatrixFloat;
import math.vector.VectorFloat;

/**This Class defines Routines 
 * that display the Results of numerical Calculations with 2D Graphics. 
 * It therefore needs to reference both Packages Math and Graphics2D.
 *
 * It calculates real Vectors into Polygons or Pairs of x and y Vectors.
 * These Vectors can then be used in the actual Display Routines,
 * so the conversion is saved.
 *
 * The Mapping takes place between the Clipping Area of the Graphics2D Object
 * and the Data Area. 
 * 
 * TODO: compare the Raster Methods in
 * @see graphic.math2D.LinCoordMap 
 * with those from 
 * @see graphic.math2D.Raster
 */
public class Raster {

	/**Criterion for the proposed Raster to have around 10 Items	 */
	public static int NormRasterItems = 10;

	/**Natural Logarithm of 10, useful to calculate an Integer Multiple of 10	 */
	final static public double Ln10 = Math.log(10.0);

	/**Selection of Rasters.	 */
	final static public double[] Raster = { 0.1, 0.2, 0.25, 0.333333333333333, 0.5 };

	/**Proposes a Raster together with the starting Point.
	 * Criterion is the number of Raster Items that should be around NormRasterItems */
	public static float[] proposeRaster(float Min, float Max) {
		float Length = Max - Min;
		float absLength = Math.abs(Length) / NormRasterItems; //Don't care for Rounding Errors
		float Dimension = (float) Math.exp(Ln10 * (1.0 + Math.floor(Math.log(absLength) / Ln10)));
		float raster;
		int Num, i = 0;
		do {
			raster = (float) (Dimension * Raster[i++]);
			Num = (int) (Length / raster);
		} while ((Num > NormRasterItems) && (i <= Raster.length));
		float start = Min - (Min % raster) - raster;
		Num++; //added a single Point at the left border!
		return createRaster(start, raster, Num);
	}

	/**Proposes the Position of the Origin as 0 if possible, otherwise near 0	 */
	public static float proposeOrigin(float Min, float Max) {
		if ((Min > 0) ^ (Max > 0))
			return 0;
		if (Min >= 0)
			return Min;
		return Max;
	}

	/**Creates the Array with the Points of the Raster by the Origin and the Width	 */
	public static float[] createRaster(float start, float Raster, int Num) {
		float[] raster = new float[Num];
		raster[0] = start;
		int i = 0;
		while (++i < Num) {
			raster[i] = (start += Raster);
		}
		return raster;
	}

	///////////////////////////////////////////////////////////////////////////////////
	// Methods to generate a Graphic from the Distances in a Graph
	// Moved to VectorFloat
	///////////////////////////////////////////////////////////////////////////////////

	/** @return a Proposal for the nDim Coordinates of the Nodes  */
	//	public float[][] generateGraph() { return generateGraph(this); }

	/** Generates a graphic Representation of the Nodes of this connected Component
	  * from an SparseMatrix Object based on Triangulation.
	  * Vermeide Überschneidungen (bei N Knoten mit ca. 4N Verbindungen ein 4N*N Problem!)
	  * Näherungslösung: verringere Überschneidungen (die in > 2D sowieso vorkommen)
	  * Nimm je 3 zusammenhängende Knoten und ermittle die geometrische Lösung.
	  * Ist die Lösung
	  * * Eindeutig, dann Koordinaten festlegen
	  * * zweideutig, dann überspringen!
	  * * Unmöglich, z.B. wenn zwei Punkte schon festliegen,
	  *   setze den dritten (verlängere die Abstände gleichmäßig) derart,
	  *   dass er nicht genau auf der Linie sitzt
	  *
	  * @return the 2D Coordinates of the Nodes
	  * The first  Node is as the Origin (0,0)
	  * The second Node is at (0,Weight)
	  */
	/*	public double[][] generateGraph(int StartNode) {
			ListEdge t = Nodes[StartNode];
			double[][] ret = new double[Nodes.length][];//[2]; //don't fill the Vectors yet!
			double[] P1 = ret[StartNode] = new double[2]; //{ 0, 0 }
			double[] P2 = ret[t .  Node] = new double[2]; P2[1] = t.Weight; //{ 0, t.Weight }
			try {
				generateGraph(StartNode, P1, t.Node, P2, t.Weight, ret, false);
			} catch (InvalidAlgorithmParameterException x) {
				x.printStackTrace();
				return null; }
			//need a Routine to calculate a 2D Triangle from the Lengths
			//find a third Coordinate meeting both..
			//if none found, go back and find a second one recursively...
			return ret; }
	
		/** Recursively generate the Coordinates, to be able to go back and restart!
		  * from an SparseMatrix Object based on Triangulation.
		  */
	/*	protected void generateGraph(int node1, double[] P1, int node2, double[] P2, double dist0, double[][] ret, boolean left)
			throws InvalidAlgorithmParameterException {
			double[] P0;
			double[] dist = new double[3];
			dist[0] = dist0;
			int node0 = Nodes.length;
			while (--node0 >= 0) {
				if (ret[node0] != null) {
					continue; }
				if (Double.isInfinite(dist[1] = directDistance(node0, node1))) {
					continue; }
				if (Double.isInfinite(dist[2] = directDistance(node0, node2))) {
					continue; }
				//yet unknown Point, but connected to both node1 and node2:
				P0 = ret[node0] = TriangleP0(P1, P2, dist, left = !left, 1.5);
				//go on searching for at both new edges, prevents getting the graph too full
				generateGraph(node0, P0, node1, P1, dist[2], ret, left);
				generateGraph(node0, P0, node2, P2, dist[1], ret, left);
			}
		}
	
		/**
		  * Calculates (an Approximation for) a Triangle given the Distances and two Points.
		  * Usually there are two or four Solutions symmetric to the Line P1-P2.
		  * when four Solutions are possible, null is returned,
		  * otherwise either the left or right Solution is chosen.
		  * When no Solution is possible and elongate > 1,
		  * dist[1] and dist[2] are elongated so that dist[1] + dist[2] = dist[0] * elongate
		  * @param elongate only elongating when elongate > 1
		  * @param dist the Distances opposite to the Points
		  * @return the third Point P0 given two Points P1, P2 and the Distances between all.
		  */
	public static double[] TriangleP0(double[] P1, double[] P2, double[] dist, boolean left, double elongate)
		throws InvalidAlgorithmParameterException {
		double[] d0 = { P1[0] - P2[0], P1[1] - P2[1] };
		double d = Math.sqrt(d0[0] * d0[0] + d0[1] * d0[1]) / dist[0]; //Normalize dist[0]
		dist[1] *= d;
		dist[2] *= d;
		double cos1 = calcCosAlpha(dist[0], dist[1], dist[2], elongate);
		double sin1 = Math.sqrt(1 - cos1 * cos1);
		if (left) {
			sin1 = -sin1;
		}
		//		double[] angles = TriAngles(dist, elongate);
		//Calculate the Position P0 from a single Angle:
		double[] ret = { P1[0], P1[1] };
		ret[0] += d0[0] * cos1 + d0[1] * sin1;
		ret[1] += d0[1] * cos1 - d0[0] * sin1;
		return ret;
	}

	/** Uses the Cosinus Equation a*a = b*b+c*c-2b*c*cos(alpha)
	 * to calculate the Cos of the Angle alpha
	 * @param elongate only elongating when elongate > 1
	 * @return the Angle alpha opposite to the Side a
	 */
	protected static final double calcCosAlpha(double a, double b, double c, double elongate)
		throws InvalidAlgorithmParameterException {
		double cos0;
		while (true) {
			cos0 = (a * a - b * b - c * c) / (2 * b * c); //
			//could be tested earlier using the Triangle Inequation?!?
			if (Math.abs(cos0) <= 1) {
				break;
			}
			if (elongate < 1.0) {
				throw new InvalidAlgorithmParameterException();
			}
			//a+b >= c and the same rotating => e*(a+b) = c
			double factor = elongate * a / (b + c);
			b *= factor; //
			c *= factor; //
		}
		return cos0;
	}

	/**
	  * Calculates (an Approximation for) the Angles of a Triangle
	  * given the Distances and two Points.
	  * Usually there are two or four Solutions symmetric to the Line P1-P2.
	  * when four Solutions are possible, null is returned,
	  * otherwise either the left or right Solution is chosen.
	  * When no Solution is possible and elongate > 1,
	  * dist[1] and dist[2] are elongated so that dist[1] + dist[2] = dist[0] * elongate
	  * @param elongate only elongating when elongate > 1
	  * @return the angles of the Triangle given by these Distances
	  */
	public static double[] TriAngles(double[] dist, double elongate) throws InvalidAlgorithmParameterException {
		double[] ret = new double[3];
		ret[0] = calcAlpha(dist[0], dist[1], dist[2], elongate);
		ret[1] = calcAlpha(dist[1], dist[2], dist[0], elongate);
		ret[2] = Math.PI - ret[0] - ret[1];
		return ret;
	}

	/** Uses the Cosinus Equation a*a = b*b+c*c-2b*c*cos(alpha)
	  * @param elongate only elongating when elongate > 1
	  * @return the Angle alpha opposite to the Side a
	  */
	protected static final double calcAlpha(double a, double b, double c, double elongate)
		throws InvalidAlgorithmParameterException {
		return function.derive.ring.body.ArcCos.ARC_COS(calcCosAlpha(a, b, c, elongate));
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	final static public float[][] testGenerateGraph() {
		//to be able to generate a Graph, at least Triangles have to be defined!
		//so too sparse Graphs are not completely drawn!
		float[][] dist = { { 0, 1, 1.4142f, 1 }, {
				1, 0, 1, 1.4142f }, {
				1.4142f, 1, 0, 1 }, {
				1, 1.4142f, 1, 0 }
		};
		//
		MatrixGraph adjM = new MatrixGraph(dist, false);
		//		SparseMatrix   adjL = new SparseMatrix  (dist, false, 10);
		SparseGraph adjL = new SparseGraph(adjM, 0, 10);
		float[][] gr = adjL.generateGraph(2); //3);
		int i = dist.length;
		while (--i >= 0) {
			int j = dist.length;
			while (--j >= 0) {
				System.out.println(Math.sqrt(VectorFloat.DIST_SQR(gr[i], gr[j])));
			}
		}
		return gr;
	}

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Raster.class.getName());
		testGenerateGraph();
		System.in.read();
		System.in.read();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws java.io.IOException {
		testIt(args);
	}

	//////////////////////////////////////////////////////////////////////////////////////
	/// Recursive Plotting
	//////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Calculates the Index of the Standpoint within this Raster
	 * Using a linear Search here instead of Bisection.
	 * If the Order is reversed, the Return Value is negative.
	 * @param x the Raster, a list of Positions for each Dimension
	 * @param SP standPoint, the Point of View (POV) in the current Dimension
	 * @return the Index of the SP in the Raster 
	 * negative if the Raster has a descending Order. 
	 */
	final static public int positionInRaster(final float[] x, final float sPnt) {
		int i =0, iLast = x.length-1;
		final float last  = x[iLast];
		final float first = x[0];
		final boolean ab = (last < first);
		if ((last < sPnt ) != ab) { return iLast; } else
		if ((sPnt < first) != ab) { return 0;     } else
			while ((++i <= iLast) && ((sPnt > x[i]) != ab));
		if (ab) {
			return -i; }
			return  i; 
	}

	/**Determines, if the Range that the ViewPoint falls in is painted twice.
	 * This may be necessary, e.g. with the Voxel Plot,
	 * because the Plot from the other side may leave remainders	 */
	//public boolean rePlotIntersection = false;

	/**Helper Method to paint rastered Data with a Sequence in Direction to the ViewPoint
	 * The Algorithm is the same as in 'float[].float[](intFunction)
	 * @param figure The Drawing Routine performed at each Position
	 * @param pos the current Position, filled from the Raster
	 * @param raster The list of Positions to process in each Dimension
	 * @param values optional, a nested Array of Values to be plotted.
	 * @param starts Flag indicating for each Dimension whether Rastering has just started.
	 * @param ab Flag whether the Raster Data is descending
	 * @param middle
	 * @param index Positions in all Dimensions for the ISpatial Routine
	 * @param dim current Dimension being processed during Recursion.
	 */
	protected static final void recurseOrdered(ISpatial figure, float[] pos, float[][] raster, Object[] values, boolean[] starts, boolean[] ab, int[] middle, int[] index, int dim) {
		//Loop ascending; i is used to save accessing Index[dim] all the time.
		int step = +1;	//Loop ascending
		int i = index[dim] = 0; figure.reSet(dim); starts [dim] = true;	//all these Flags correspond!
		Object sNew = null;
		do {
			i -= step; do //if (rePlotIntersection) Middle[dim]+= Step; do //one or two extra Steps!
			{	//Plotting in both Directions with the same Code:
				i += step;
				pos[dim] = raster[dim][i];
				if (values != null) sNew = values[i];
				if (dim > 0) {
					if ((sNew == null) || (sNew instanceof Object[])) {
						recurseOrdered (figure, pos, raster, (Object[]) sNew, starts, ab, middle, index, dim-1);
					} else {
						recurseOrdered(figure, pos, raster, (float[])sNew, starts, ab, middle, index, dim-1);
					}
				} else {
					figure.moveTo(pos, sNew, index); }	//here Copy the float[] before, because it may be transformed in Place
				starts [dim] = false;
				index [dim]+= step;
			} while ((i != middle[dim]));
			i = index[dim] = raster[dim].length-1; figure.reSet(-dim-1); starts [dim] = true;	//all these Flags correspond!
			step = -step;		//Loop descending
		} while (step < 0);
	}

	/**Helper Method to paint rastered Data with a Sequence in Direction to the ViewPoint
	 * The Algorithm is the same as in 'float[].float[](intFunction)
	 * @param Figure The Drawing Routine performed at each Position
	 * @param V the current Position, filled from the Raster
	 * @param Raster The list of Positions to process in each Dimension
	 * @param S optional, a nested Array of Values to be plotted.
	 * @param Start Flag indicating for each Dimension whether Rastering has just started.
	 * @param ab Flag whether the Raster Data is descending
	 * @param Middle
	 * @param Index Positions in all Dimensions for the ISpatial Routine
	 * @param dim current Dimension being processed during Recursion.
	 */
	protected static final void recurseOrdered(final ISpatial figure, float[] pos, float[][] raster, float[] values, boolean[] starts, boolean[] ab, int[] middle, int[] index, int dim) {
		int Step = +1;	//Loop ascending
		int i = index[dim] = 0; figure.reSet(dim); starts [dim] = true;	//all these Flags correspond!
		ByRefFloat sNew = new ByRefFloat();
		do {
			i -= Step; do //if (rePlotIntersection) Middle[dim]+= Step; do //one or two extra Steps!
			{	//Plotting in both Directions with the same Code:
				i += Step;
				pos[dim] = raster[dim][i];
				if (values != null) {
					sNew.Value = values[i]; }
				figure.moveTo(pos, sNew, index); 	//here Copy the float[] before, because it may be transformed in Place
				starts [dim] = false;
				index [dim]+= Step;
			} while ((i != middle[dim]));
			i = index[dim] = raster[dim].length-1; figure.reSet(-dim-1); starts [dim] = true;	//all these Flags correspond!
			Step = -Step;		//Loop descending
		} while (Step < 0);
	}

	/**Paints a Raster of Points on the Raster contained in the Vectors of R;
	 *
	 * The Difference to the Plot Routine is
	 * that this Routine always paints in the Direction to the ViewPoint,
	 * so the nearer Points hide the ones far out.
	 *
	 * the real Plotting is done in 'PlotRecursion',
	 * only the Preprocessing is done here:
	 * Calculating the Indices of the Positions closest to the ViewPoint
	 * in all Dimensions and storing them in the Vector Middle[].
	 * At the same Time ab[] is filled with the Directions of the Data.
	 * The Algorithm is the same as in
	 * @param Figure The Drawing Routine performed at each Position
	 * @param ViewPoint Position from which the Data is seen
	 * @param raster the Raster, a list of Positions to process in each Dimension
	 * @param values optional, a nested Array of Values to be plotted.
	 */
	final static public void rasterOrdered(float[] viewPoint, ISpatial Figure, double[][] raster, Object[] values) {
		rasterOrdered(viewPoint, Figure, MatrixFloat.COPY(raster), values); }

	/**Paints a Raster of Points on the Raster contained in the Vectors of R;
	 *
	 * The Difference to the Plot Routine is
	 * that this Routine always paints in the Direction to the ViewPoint,
	 * so the nearer Points hide the ones far out.
	 *
	 * the real Plotting is done in 'PlotRecursion',
	 * only the Preprocessing is done here:
	 * Calculating the Indices of the Positions closest to the ViewPoint
	 * in all Dimensions and storing them in the Vector Middle[].
	 * At the same Time ab[] is filled with the Directions of the Data.
	 * The Algorithm is the same as in
	 * @param Figure The Drawing Routine performed at each Position
	 * @param ViewPoint Position from which the Data is seen
	 * @param R the Raster, a list of Positions to process in each Dimension
	 * @param S optional, a nested Array of Values to be plotted.
	 */
	final static public void rasterOrdered(float[] standPoint, ISpatial figure, float[][] raster, Object[] values) {
		int Mid;
		boolean	[] desc  = new boolean	[raster.length];
		boolean	[] Start = new boolean	[raster.length];
		int		[] middle= new int		[raster.length];
		int		[] index = new int		[raster.length];
		int i = -1;
		while (++i < middle.length) {	//This has to be done once, so it is done here!
			Mid = positionInRaster(raster[i], standPoint[i]);
			middle[i] = Math.abs(Mid);
			desc[i] = (Mid < 0);
		}
		float[] V = new float[raster.length+1]; //for the eventual z Coordinate! TODO: makes Problems for the Vector Plot!!!
		recurseOrdered(figure, V, raster, values, Start, desc, middle, index, raster.length-1); }

	/**Paints a Raster of Points on the Raster contained in the Vectors of R;
	 *
	 * The Difference to the Plot Routine is
	 * that it paints in the Direction to the ViewPoint,
	 * so the nearer Points hide the ones far out.
	 * @param Figure The Drawing Routine performed at each Position
	 * @param R the Raster, a List of Positions to process in each Dimension
	 * @param S optional, a nested Array of Values to be plotted.
	 */
//	public void rasterOrdered(ISpatial figure, double[][] R, Object[] S) {
//		Raster.rasterOrdered (P.getStart(), figure, MatrixFloat.copy(R), S); }
	
	/**Paints a Raster of Points on the Raster contained in the Vectors of R;
	 *
	 * The Difference to the Plot Routine is
	 * that it paints in the Direction to the ViewPoint,
	 * so the nearer Points hide the ones far out.
	 * @param Figure The Drawing Routine performed at each Position
	 * @param R the Raster, a List of Positions to process in each Dimension
	 * @param S optional, a nested Array of Values to be plotted.
	 */
//	final public void rasterOrdered(final ISpatial figure, final float[][] R, final Object[] S) {
//		Raster.rasterOrdered(P.getStart(), figure, R, S);}	//Could be static, if the ViewPoint would be supplied!
	
	/////////////////////////////////////////////////////////////////////////////////////
		
	/**Paints a Raster of Points on the Raster contained in the Vectors of R;
	 * The Algorithm is the same as in 'float[].float[](IFunction)'
	 * @param figure The Drawing Routine performed at each Position
	 * @param values optionally contains additional Data to be plotted as a nested Array.
	 * @param raster used for building up the Vectors during Recursion
	 */
	public static void rasterFigure(final ISpatial figure, final float[][] raster, final Object[] values) {
		boolean	[] Start = new boolean	[raster.length];
		int		[] Index = new int		[raster.length];
		float[] V = new float[raster.length];
		rasterFigure(figure, V, raster, values, Start, Index, raster.length-1); }

	/**Recursive Helper Method to process rastered Data in arbitrary Dimensions.
	 * The Figure is drawn ...
	 * @param Figure The Drawing Routine performed at each Position
	 * @param position current Position, filled from the Raster
	 * @param Raster The list of Positions to process in each Dimension
	 * @param values an optional nested Array of Values to be plotted.
	 * @param Start Flag indicating for each Dimension whether Rastering has just started.
	 * @param Index Positions in all Dimensions for the ISpatial Routine
	 * @param dim current Dimension being processed during Recursion.
	 */
	public static void rasterFigure(final ISpatial figure, final float[] position, final float[][] raster, final Object[] values, final boolean[] starts, final int[] index, final int dim)
	{	//never put the BreakPoint on the first Line of a procedure,
		//because if it is programmed recursively, it may run through the whole Procedure in the next Step
		//Loop ascending; i is used to save accessing Index[dim] all the time.
		int Length = raster[dim].length;
		figure.reSet(dim); starts [dim] = true;	//all these Flags correspond!
		Object sNew = null;
		index [dim] = -1;
		while (++index[dim] < Length) { //plot in order!
			starts[dim] = false;
			position[dim] = raster[dim][index [dim]];
			if (values != null) sNew = values[index [dim]];
			if (dim > 0) {
				rasterFigure(figure, position, raster, (Object[]) sNew, starts, index, dim-1);
			} else { //last Dimension
				figure.moveTo(position, sNew, index); }	//here Copy the float[] before, because it may be transformed in Place
		}
	}

}