package graphic.math2D;

/**
 * This Class encapsulates affine Mapping between two 1D Coordinate Systems.
 * It also converts float Numbers to integer Numbers.
 * This is used separately for the x and y Axis of 2D Systems.
 * Here only scalar Types are used for Performance Reasons
 * The affine Mapping is also defined coordinate-wise in Vector.Line()
 * This Mapping is neither linear, nor does it reflect
 * Rotation or Shearing.
 * 
 * TODO: compare the Raster Methods in
 * @see graphic.math2D.LinCoordMap 
 * with those from 
 * @see graphic.math2D.Raster
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 3fda585440159ab2a2346f7794433d083a2714fd82e5efd42343cb7318bbe6a5
 * stale: false
 * tags: [code/coordinate_transform]
 * concepts: [Linear Coordinate Mapping]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class LinCoordMap {

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////

	/**Criterion for the proposed Raster to have around 10 Items	 */
	public static int NormRasterItems = 10;

	/**Natural Logarithm of 10, useful to calculate an Integer Multiple of 10	 */
	final static public double Ln10 = Math.log(10.0);

	/**Selection of Rasters.	 */
	final static public double[] Raster = { 0.1, 0.2, 0.25, 0.333333333333333, 0.5 };

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////

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

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////
	//	internal Parameters for the Mapping	//
	//////////////////////////////////////////

	/** Scale Factor. 	 */
	private float Scale;

	/** Translation Offset	 */
	private float Offset;

	/**Inverse of the Scale Factor
	 * This is also the Step Size for the fine Raster.
	 */
	private float Step;

	/** Length of the (Integer) Target/Value Range	 */
	private int iLength;

	/** Length of the Original Definition Range	 */
	private float Length;

	/** Starting Point for the Raster */
	private float Start;

	////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////

	/**Returns the Step Size necessary to progress one Digit.  */
	public float getStep() {
		return Step;
	}

	/**Returns the Scale Factor: Scale == 1/Step
	 * Useful for relative Calculations, e.g with Vectors.  */
	public float getScale() {
		return Scale;
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**Constructor that takes the Original Range and the Target Range	 */
	public LinCoordMap(final double Min, final double Max, final int iMin, final int iMax) {
		this((float) Min, (float) Max, iMin, iMax);
	}

	/**Constructor that takes the Original Range and the Target Range	 */
	public LinCoordMap(final float Min, final float Max, final int iMin, final int iMax) {
		Start = Min;
		Length = Max - Min;
		iLength = iMax - iMin;
		Scale = iLength / Length; //Multiplication is faster than Division.
		Step = 1.0f / Scale;
		Offset = iMin * Step - Min; //Speeds up Calculation to sum up both Translation and to save 1 Subtraction
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Scales the Coordinates by the given Factor
	 * also changes the Offset so the Center stays focused.	 */
	public void scaleAt(final double factor) {
		Scale *= factor;
		Length*= factor;
		Step  /= factor;
	}

	/** Move the Coordinates by the given Distance in the Target/Value Range	 */
	public void moveAt(final int dx) {
		Offset += dx/Scale; }

	/** Move the Coordinates by the given Distance in the Definition Range	 */
	public void moveAt(final double dx) {
		Offset += dx; }

	/**Calculates the Map to the Target Range	 */
	public int map(final float x) {
		return (int) ((x + Offset) * Scale);
	} //doing the Subtraction first prevents Risk of over/underflow!

	/**Calculates the relative Map to the relative Target Range.
	 * doing this repeatedly leads to Problems with Accuracy!
	 */
	public int scale(final float x) {
		return (int) (x * Scale);
	} 

	/**Calculates the relative Map to the relative Target Range.
	 * doing this repeatedly leads to Problems with Accuracy!
	 */
	public int scale(final double dx) {
		return (int) (dx * Scale);
	} 

	/**Calculates the Map to the Target Range	 */
	public int map(final double x) {
		return (int) ((x + Offset) * Scale);
	} //doing the Subtraction first prevents Risk of over/underflow!

	/**Maps the x-Coordinates to the X Target Range	 */
	public int[] map(final float[] x) {
		int[] V = new int[x.length];
		int i = x.length;
		while (--i >= 0) {
			V[i] = (int) ((x[i] + Offset) * Scale);
		} //map(x[i]); }
		return V;
	}

	/**Maps the x-Coordinates to the X Target Range	 */
	public int[] map(final double[] x) {
		int[] V = new int[x.length];
		int i = x.length;
		while (--i >= 0) {
			V[i] = (int) ((x[i] + Offset) * Scale);
		} //map(x[i]); }
		return V;
	}

	/** Calculates the inverse Map to the Original Range	 */
	public float unMap(final int x) {
		return (x * Step - Offset);
	} //same as .../Scale;} //no risk of Overflow here.

	/**Creates the Array with the Points of the Raster by the Origin and the Width	 */
	public float[] createFineRaster() {
		return createRaster(Start, Step, iLength);
	}

}
