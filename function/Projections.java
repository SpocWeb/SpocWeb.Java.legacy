package function;

import math.vector.VectorDouble;
import function.byref.ByRefDouble;
import function.vector.AFloatVectorField;
import function.vector.IFloatVectorField;

/**Contains the Definitions of some 2-dimensional Coordinate Mappings
 * from a Sphere to a Plane.
 * All Coordinates are assumed to be in Radians.
 * @see BodyTensor.Projections for the corresponding Operations with Tensor Objects.
 */
public class Projections
extends AFunction
implements IFloatVectorField
//, ICoordMapper
{

////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////

	/**Selected Projection 	 */
	protected int Projection;

////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/**Copy Constructor taking a Projection	 */
	public Projections(int Projection_) { this.Projection = Projection_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface IFloatVectorField: Implementation
////////////////////////////////////////////////////////////////////////////

	/**
	 * Maps the Vectors to Vector Values
	 * @param v The Position to evaluate
	 * @param out The Value to be returned
	 * The Contract is that if out is null,
	 * the VectorField creates a new double[][] to return it's Values.
	 */
	public double[][] map(double[][] v, double[][] out) {
		return AFloatVectorField.Map(this, v, out); }

	/**
	 * Maps the Vectors to Vector Values
	 * @param v The Positions to evaluate
	 * @param out The Values to be returned
	 * The Contract is that if out is null,
	 * the VectorField creates a new float[][] to return it's Values.
	 */
	public float[][] map(float[][] v, float[][] out) {
		return AFloatVectorField.Map(this, v, out); }

	/**Actual Function implemented	 */
	public float[] map(float[] arg, float[] out) {
		double[] tmp = { arg[0], arg[1]};
//		System.arraycopy(arg, 0, tmp, 0, 2); //compiles, but Runtime Error
		switch (Projection)	{
			case Cyl_Netz:			tmp = Cyl_NetzAt		(tmp); break;
			case Hour2Rad:			tmp = Hour2RadAt		(tmp); break;
			case Deg2Rad:			tmp = Deg2RadAt			(tmp); break;
			case Cyl_Azimuthal:		tmp = Cyl_AzimuthalAt	(tmp); break;
			case Cyl_Gnomonisch:	tmp = Cyl_GnomonischAt	(tmp); break;
			case Cyl_Mercator:		tmp = Cyl_MercatorAt	(tmp); break;
			case Cyl_Orthograph:	tmp = Cyl_OrthographAt	(tmp); break;
			case Cyl_Stereograph:	tmp = Cyl_StereographAt (tmp); break;
			case Polar_Azimuthal:	tmp = Polar_Azimuthal	(tmp); break;
			case Polar_Gnomonisch:	tmp = Polar_Gnomonisch	(tmp); break;
			case Polar_Netz:		tmp = Polar_Netz		(tmp); break;
			case Polar_Orthograph:	tmp = Polar_Orthograph	(tmp); break;
			case Polar_Stereograph:	tmp = Polar_Stereograph (tmp); break;
			case Sinusoidal:		tmp = Sinusoidal		(tmp); break;
			case Albers:			tmp = AlbersAt			(tmp); break;
			case Mollweide:			tmp = MollweideAt		(tmp); break;
			default: throw new IllegalArgumentException(); //
		}
		arg[0] = (float) tmp[0];
		arg[1] = (float) tmp[1];
		return arg; }

	/** Actual Function implemented	 */
	public double[] map(double[] arg, double[] out) {
		switch (Projection)	{
			case Hour2Rad:			return Hour2RadAt		(arg);
			case Deg2Rad:			return Deg2RadAt		(arg);
			case Cyl_Azimuthal:		return Cyl_AzimuthalAt	(arg);
			case Cyl_Gnomonisch:	return Cyl_GnomonischAt	(arg);
			case Cyl_Mercator:		return Cyl_MercatorAt	(arg);
			case Cyl_Netz:			return Cyl_NetzAt		(arg);
			case Cyl_Orthograph:	return Cyl_OrthographAt	(arg);
			case Cyl_Stereograph:	return Cyl_StereographAt(arg);
			case Polar_Azimuthal:	return Polar_Azimuthal	(arg);
			case Polar_Gnomonisch:	return Polar_Gnomonisch	(arg);
			case Polar_Netz:		return Polar_Netz		(arg);
			case Polar_Orthograph:	return Polar_Orthograph	(arg);
			case Polar_Stereograph:	return Polar_Stereograph(arg);
			case Sinusoidal:		return Sinusoidal		(arg);
			case Albers:			return AlbersAt			(arg);
			case Mollweide:			return MollweideAt		(arg);
			default: throw new IllegalArgumentException(); // return arg; //return the Vector unchanged
		}
	}

////////////////////////////////////////////////////////////////////////////
/// #region : Interface IFunction: Implementation
////////////////////////////////////////////////////////////////////////////

	/**Actual Function implemented	 */
	public Object Map(Object arg) {
		if (arg instanceof float[]) {
			return map((float []) arg, null); }
			return map((double[]) arg, null); }

	//////////////////////
	//	Class Constants	//
	//////////////////////

	/** Radians for a single Degree in a 360 Degree System  */
	final public static double Grad = Math.PI/180;

	/** Radians for a single Hour in a 12 Hour System  */
	final public static double Hour = Math.PI/6;

///////////////////////////////////////////////////////////////////////////////////
/// Enumeration Constants for the selected Transformation
///////////////////////////////////////////////////////////////////////////////////

	final public static int Cyl_Netz			=  0;
	final public static int Hour2Rad			=  1;
	final public static int Deg2Rad				=  2;
	final public static int Cyl_Azimuthal		=  3;
	final public static int Cyl_Gnomonisch		=  4;
	final public static int Cyl_Mercator		=  5;
	final public static int Cyl_Orthograph		=  6;
	final public static int Cyl_Stereograph		=  7;
	final public static int Polar_Netz			=  8;
	final public static int Polar_Azimuthal		=  9;
	final public static int Polar_Gnomonisch	= 10;
	final public static int Polar_Orthograph	= 11;
	final public static int Polar_Stereograph	= 12;
	final public static int Sinusoidal			= 13;
	final public static int Albers				= 14; //??
	final public static int Mollweide			= 15; //??

	//////////////////////
	//	Class Methods	//
	//////////////////////

	/**Conversion of Degrees to Radians,
	 * necessary for the subsequent Conversions if the Data is given in Degrees.
	 */
	public static double[] Deg2RadAt (double[] V) {
		return VectorDouble.MUL_AT(V, Grad); } //TDouble.Grad);}

	/**Conversion of Hours to Radians,
	 * necessary for the subsequent Conversions if the Data is given in Hours.
	 */
	public static double[] Hour2RadAt (double[] V) {
		return VectorDouble.MUL_AT(V, Hour); }

	/**Projektion des Nordpols in die Mitte einer Polar-Darstellung
	 * mit äquidistantem Breiten- Raster
	 * x' = Cos(x) * (Pi/2-y)
	 * y' = Sin(x) * (Pi/2-y)
	 */
	public static double[] Polar_Netz (double[] V) {
		double[] Result = new double[2];	//copies the Structure!
		ByRefDouble.COS_SIN(V[0], Result);
		return VectorDouble.MUL_AT(Result, IMeasurAble.PI_HALF-V[1]); }

	/**Projektion des Nordpols in die Mitte einer Polar-Darstellung
	 * mit in Blickrichtung parallel projiziertem Breiten- Raster
	 * x' = Cos(x) * Cos(y)
	 * y' = Sin(x) * Cos(y)
	 */
	public static double[] Polar_Orthograph (double[] V) {
		double[] Result = new double[2];	//copies the Structure!
		ByRefDouble.COS_SIN(V[0], Result);
		return VectorDouble.MUL_AT(Result, Math.cos(V[1])); }

	/**
	 * Stereographische polare Projektion aus dem Nordpol heraus
	 * x' = Cos(x) * Cos(y)/(1-Sin(y))
	 * y' = Sin(x) * Cos(y)/(1-Sin(y))
	 */
	public static double[] Polar_Stereograph (double[] V) {
		double[] Result = new double[2];	//copies the Structure!
		double[] c_s    = new double[2];
		ByRefDouble.COS_SIN(V[0], Result);
		ByRefDouble.COS_SIN(V[1], c_s);
		return VectorDouble.MUL_AT(Result, c_s[0]/(1-c_s[1])); }	// *cos/(1-sin)

	/**
	 * Stereographische polare Projektion aus dem Mittelpunkt heraus
	 * x' = Cos(x) / tan(y)
	 * y' = Sin(x) / tan(y)
	 */
	public static double[] Polar_Gnomonisch  (double[] V) {
		double[] Result = new double[2];	//copies the Structure!
		ByRefDouble.COS_SIN(V[0], Result);
		return VectorDouble.MUL_AT(Result, 1/Math.tan(V[1])); }

	/**
	 * Projektion des Nordpols in die Mitte einer Polar-Darstellung
	 * mit flächenerhaltendem Breiten- Raster nach Lambert
	 * x' = Cos(x) * sin((y-Pi/2)/2)
	 * y' = Sin(x) * sin((y-Pi/2)/2)
	 */
	public static double[] Polar_Azimuthal (double[] V) {
		double[] Result = new double[2];	//copies the Structure!
		ByRefDouble.COS_SIN(V[0], Result);
		return VectorDouble.MUL_AT(Result, Math.sin((V[1]-IMeasurAble.PI_HALF)/2)); }

	/** Anderes ProjektionsZentrum => Verzerrung */
	final static public double ProjZentrum = 0.3;

	/** stereographische Projektion von der Kugel auf den Zylinder */
	public double[] Mercator(double[] V) {
		double[] c_s = new double[2];
		ByRefDouble.COS_SIN (V [2], c_s);
		V[2] = c_s[1]/(ProjZentrum + c_s[0]);
		return V; }

	/**
	 * Stereographic Mercator Projection onto a Cylinder.
	 * Leaves Angles correct.
	 * x' = x
	 * y' = Log(tan((y+Pi/2)/2))
	 */
	public static double[] Cyl_MercatorAt (double[] V) {
		V[1] = Math.log(Math.abs(Math.tan((V[1] + IMeasurAble.PI_HALF)/2)));
		return V; }

	/**Orthographic Projection onto a Cylinder.
	 * Leaves ?? correct
	 * x' = x
	 * y' = sin(y)
	 */
	public static double[] Cyl_OrthographAt (double[] V) {
		V[1] = Math.sin(V[1]); return V; }

	/**This is the identical Mapping	 */
	public static double[] Cyl_NetzAt (double[] V) { return V; }

	/**
	 * stereographische Projektion von der Kugel auf den Zylinder
	 * x' = x
	 * y' = sin(y)/(1+cos(y))
	 */
	public static double[] Cyl_StereographAt (double[] V) {
		double[] c_s = new double[2];	//copies the Structure!
		ByRefDouble.COS_SIN(V[1], c_s);
		V[1] = c_s[1]/(1+c_s[0]);
		return V; }

	/**
	 * stereographische Projektion von der Kugel auf den Zylinder
	 * x' = x
	 * y' = tan(y)
	 */
	public static double[] Cyl_GnomonischAt (double[] V) {
		V[1] = Math.tan(V[1]); return V; }

	/**
	 * Azimutale Projektion
	 * x' = x
	 * y' = sin(y/2)
	 */
	public static double[] Cyl_AzimuthalAt (double[] V) {
		V[1] = Math.sin(V[1]/2); return V; }

	/**
	 * Azimutale Projektion
	 * x' = x*cos(y)
	 * y' = y
	 */
	public static double[] Sinusoidal (double[] V) {
		V[0] *= Math.cos(V[1]); return V; }

	/**
	 * Mollweide Projektion
	 * x' = ?
	 * y' = ?
	 */
	public static double[] MollweideAt(double[] V) {
		double dx;
		double V0 = V[0]; //Cache
		double sx = Math.sin(V[1])*IMeasurAble.PI;
		double fa = sx/2;	//guter Startwert
		//fa = Loesung der Gleichung fa + sin(fa) = Pi*Sin V[1]
		do { //	Nullstellensuche ueber das Newton-Verfahren
			///	fa = c-Sin (fa); //	Banach-Iteration zwar konvergent,aber schlecht
			ByRefDouble.COS_SIN(fa, V);
			fa -= (dx = fa-sx+V[1]/(1+V[0])); //	Newton viel besser
		} while (Math.abs(dx) > IMeasurAble.DOUBLE_ACCURACY);
		ByRefDouble.COS_SIN(fa/2, V);
		V[0] *= V0/IMeasurAble.PI_HALF;
		return V; }

	/**
     * Values precalculated in Albers_Init
     */
	protected double An;
	protected double Ac;
	protected double Ar0;

	/**Initialization of the Albers Projection with two Widths
	 * that define the Range to be used:
	 */
	protected void Albers_Init (double[] V) {
		double s0 = Math.sin(V[0]);
		double s1 = Math.sin(V[1]);
		An = (s0 + s1)/2;
		Ac = (s0 * s1)+1; //	= Sqr (c1) + 2*n*s0;
		Ar0= Math.sqrt(Ac)/An;
	}

	/**Albers Projection with two Widths
	 * that define the Range to be used:
	 * x' = ?
	 * y' = ?
	 */
	public double[] AlbersAt(double[] V) {
		double[] Result = new double[2];	//copies the Structure!
		double scale = Math.sqrt(2*(Ac-An*Math.sin(V[1])))/An;
		ByRefDouble.COS_SIN(V[0]*An, V); //x and y swapped!
		VectorDouble.MUL_AT(Result, scale);
		V[1] -= Ar0;
		return V; }

/*	public double[] PolarDrehung (VAR W : Vektor2;VAR D : Matrix3);
	 VAR ZV,EV : Vektor3;
	     i : Integer;
	{
		Cos_Sin (W [1],ZV [1],ZV [2]);
		Cos_Sin (W [2],fa    ,ZV [3]);
		ZV [1] = ZV [1]*fa;
		ZV [2] = ZV [2]*fa;
		RZ1 = @EV;
		RZ2 = @D;
		FOR i = 1 TO 3 DO {
		  RZ1^ = Verkuerzung (@ZV,RZ2,SizeOf (Real),SizeOf (Real),3);
		  INC (RZ2,3);
		  INC (RZ1);
		 }
		W [1] = ArcTg (EV [1],EV [2]);
		W [2] = ArcTg (SqRt (Eins-Sqr (EV [3])),EV [3]); //	ArcSin(ZV [3]);
	}
*/
}
