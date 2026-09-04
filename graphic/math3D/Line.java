package graphic.math3D;

//import Maths.VectorFloat;
import math.vector.VectorFloat;

/**
 * The Line consists of a Starting Point and an Endpoint
 * thus it corresponds to a multidimensional Interval.
 * All Methods of a Vector can also be applied to both points of the Line
 * In that Respect the Line forms a 2-Row Tensor again.
 *
 * Design Decisions:
 * Since the Coordinates represent Points, they are stored in a Tensor.
 * That way a Transformation is automatically applied to all Points.
 *
 * The Points are shielded by get/set Methods,
 * so additional Information can be calculated each time they change,
 * like the Difference Vector, the Volume etc.
 *
 * With Respect to Rectangle Operations, I have not stored the State of Order
 * that is reached after merge Operations.
 *
 * Since Tensor now also implements Manifold Operations,
 * Line is inherited from Tensor, which also represents Polynoms,
 * since Line is a special Polynom and implements some special Operations.
 *
 * Line is created to make all operations with Lines and Boxes more transparent.
 *
 * Since all Operations are inherited from, instead of delegated to 'Tensor',
 * they still have to be re-implemented to invoke the 'recalc()' Method.
 *
 * The good thing about Objects ist that they can be encapsulated
 * and look for their Integrity themselves.
 *
 * Possible movements here are:
 * -changing either Start XOR Stop Vector: Width changes => recalculate
 * -Translating the Line => don't recalculate (corresponds to .add() / .subAt())
 * -changing Width => recalculate
 * -rotating the Line => don't recalculate
 * -general linear Operation => recalculate (corresponds to .mul())
 *
 * Design Decisions:
 * The Line is realized as a Matrix with two Coordinates rather than two Vectors.
 * This allows for the same Operations as with Tensors.
 *
 * Multiplication has been redefined to performing the Mapping.
 *
 * The Calculation of the other Properties is delayed and controlled by Flags
 * that indicate the Validity of the Values. If any Value is accessed
 * without valid Cache, then the Values are recalculated.
 * This speeds up dynamic Operations like merging and mapping!!!
 * This can be expressed much better in VB,
 * where the Flags and Caches would be local static Variables
 * of the Property Get Routines.
 *
 * The Politics are: Start and End Point are always calculated,
 *					 all the Rest only on demand.
 */
public class Line {		//This allows for all Matrix Operations.

	/**
     * References to the Start- (first) and End (second) Point.
     * Using an Array is slower (Range Check), but more flexible than a Member.
     */
	protected float[][] a = new float[2][];

	//////////////////////
	//	Constructors	//
	//////////////////////

	/**
	 * Constructor, that takes the Startpoint and either the Width or the Endpoint
	 * The Arrays are copied and not reused!
	 */
	public Line (float[] Start, float[] StopWidth, boolean width) {
//		super(Start.Carry, 1);
//		float[] Stop;		//is copied on the float[] Constructor
		a[0] = VectorFloat.COPY(Start);
		if (width) {
			mWidth = VectorFloat.COPY(StopWidth); validWidth = true;
			a[1]   = VectorFloat.ADD(Start, StopWidth);
		} else {
			a[1]   = VectorFloat.COPY(StopWidth); }		//mWidth = (float[])Stop .subt (Start);}	//Don't calculate the Width automatically!
	}

	/** Constructor, that takes the Start- and Endpoint	 */
	public Line (float[] Start, float[] Stop) {
		this (Start, Stop, false); }

	/**Empty Constructor, defaults the Start- and Endpoint to zero Vectors.	 */
/*	public Line ()	{
		this (new float[],
			  new float[], false); }
*/
	/**Empty Constructor, defaults the Start- and Endpoint to zero Vectors.	 */
//	public Line ()	{ this (new float[], new float[], false); }


	//////////////////
	//	Operations	//
	//////////////////

	//	Retrieve the protected Properties:

	/**Starting Point of the Line	 */
	public float[] getStart() { return VectorFloat.COPY(a[0]); }

	/**Ending Point of the Line	 */
	public float[] getStop () { return VectorFloat.COPY(a[1]); }

	//////////////
	//	Corners	//
	//////////////

	/**Cache for float[]s with all Corners of the Hypercube with this Extent.	 */
	private float[][] Corners;

	/**Returns all Corners of the Hypercube with this Extent.
	 * There are 2^n Corners to this HyperCube,
	 * So for n = 2 it generates 4 Corners and for n = 3: 8 Corners.
	 */
	public float[][] getCorners() {	//Could be done recursively.
		if (Corners == null) {
			int Length = 1 << a[0].length;
			Corners = new float[Length][];
			while (--Length >= 0)
				Corners[Length] = getCorner(Length);
		}
		return Corners; }

	/**Returns the indicated Corner of the Hypercube with this Extent.
	 * There are 2^n Corners to this HyperCube, counted from 0
	 * and indexed by the BitMap of the given Number.
	 */
	public float[] getCorner(int numCorner) {	//Could be done recursively.
		int i = a[0].length;
		numCorner <<= 1;	//to undo the first right shift
		float[] arr = new float[i];
		while (--i >= 0) {	//reuse the Elements of this float[]
			arr[i] = a[(numCorner >>= 1) & 1][i]; }
		return arr;
	}

	//////////////
	//	Width	//
	//////////////

	/**Flag for valid Cache	 */
	private boolean validWidth;

	/**Difference of Start and End Vectors 	 */
	private float[] mWidth = null;

	/**Extent of the Line	 */
	protected float[] getWidthAt() {
		if(!validWidth) {
			validWidth =true;
			return mWidth = VectorFloat.SUB(a[1], a[0]); } 	//Width is recalculated	//mWidth = a[1].subt (a[0]); this is much slower!
		return mWidth; }

	/**Extent of the Line	 */
	public float[] getWidth() {
		return VectorFloat.COPY(getWidthAt()); }

	//////////////
	//	Volume	//
	//////////////

	/** Volume of the (Hyper-)Cube with this Line as Diagonal
	 * i.e these two Points as opposite Corners
	 * Initialized to a negative Value to indicate
	 */
	private float mVol = -1;

	/**Volume of the (Hyper-)Cube surrounding the Line	 */
	public float getVolume() {
		if (mVol < 0) {
			mVol = (float) VectorFloat.PROD(getWidthAt()); }
		return mVol; }

	//////////////
	//	Length	//
	//////////////

	/**Length of the Line. This is the euklidean Norm	 */
	private float mLength = -1;

	/**Length of the Line. This is the euklidean Norm
	 * Initialized to a negative Value to indicate
	 */
	public float Norm() {
		if (mLength < 0) {
			mLength = (float) Math.sqrt(SqrNorm()); }
		return mLength; }

	//////////////////
	//	SqrLength	//
	//////////////////

	/**Square Length of the Line. This is the euklidean Norm
	 * Initialized to a negative Value to indicate
	 */
	private float mSqrLength = -1;

	/**Square Length of the Line. This is the euklidean Norm	 */
	public float SqrNorm() {
		if (mSqrLength < 0) {
			mSqrLength = (float) VectorFloat.NORM_SQR(getWidthAt()); }
		return mSqrLength; }

	//////////////////
	//	AbsLength	//
	//////////////////

	/**Returns the Sum of the Length of the (Hyper-)Cube's Edges
	 * with the two Points as opposite Corners.
	 * This is the AbsV - Norm, the fastest Norm
	 * Initialized to a negative Value to indicate
	 */
	private float mAbsV = -1;

	/**Returns the Sum of the Length of the (Hyper-)Cube's Edges
	 * with the two Points as opposite Corners.
	 * This is the AbsV - Norm, the fastest Norm */
	public float AbsV_Norm() {
		if (mAbsV < 0) {
			mAbsV = VectorFloat.NORM_ABS(getWidthAt()); }
		return mAbsV; }

	//////////////////////
	//	Set Routines	//
	//////////////////////

	/**Signals to the Line Object, that Items dependent on the Width have changed	 */
	public void inValidate() {
		mVol = mAbsV = mLength = mSqrLength = -1; validWidth = false; }

	/**Ending of the Line	 */
	public void setStart (float[] arg) {
		VectorFloat.COPY(arg, a[0]); inValidate(); }

	/**Ending of the Line	 */
	public void setStop  (float[] arg) {
		VectorFloat.COPY(arg, a[1]); inValidate(); }

	/**Ending of the Line	 */
	public void setWidth (float[] arg) {
		VectorFloat.COPY(arg, mWidth); validWidth = true;
		VectorFloat.add(a[1], a[0], mWidth); //End Point is recalculated
		inValidate();
	}


	//Methods of the Hyper- Cube:

	//Testing

	//Use 'between' and not 'contains' to test, if a Point lies in this Line.

	/** @return true when this (Hyper-)Cube intersects with the given HyperCube 	 */
	public boolean contains(Line arg) {
		return VectorFloat.BETWEEN(a[0], arg.a[0], a[1]) &&
			   VectorFloat.BETWEEN(a[0], arg.a[1], a[1]); }

	/** @return true when this (Hyper-)Cube intersects with the given HyperCube 	 */
	public boolean intersects(Line arg) {
		return VectorFloat.BETWEEN(a[0], arg.a[0], a[1]) ||
			   VectorFloat.BETWEEN(a[0], arg.a[1], a[1]); }

	//Operations:

	protected boolean bolOrdered;

	public boolean ordered() { return bolOrdered; }

	/**Orders the Coordinates, so the smaller ones end up in Start
	 * and the higher ones end up in Stop.	 */
	public Line orderAt() {
		bolOrdered = true;
		VectorFloat.ORDER_AT(a[0], a[1]); return this; }

	/**Returns the (Hyper-)Cube merged with the Point or Polygon 	 */
	public Line merge(float[] arg) { return ((Line) copy()).mergeAt(arg); }

	/**Returns the (Hyper-)Cube merged with the Point or Polygon in Place
	 * After this, the Cube is ordered.	 */
	public Line mergeAt(float[] arg) {
		if (! bolOrdered) {
			orderAt();}
		inValidate();
		VectorFloat.MIN_AT(a[0], arg);
		VectorFloat.MAX_AT(a[1], arg);
		return this; }	//next merge would allow for only one MaxAt Operation, because this Rectangle is already ordered!

	/**Returns the (Hyper-)Cube merged with the Point or Polygon in Place
	 * After this, the Cube is ordered.	 */
	public Line mergeAt(float[][] arg) {
		if (! bolOrdered) {
			orderAt();}
		inValidate();
		float[] Item;
		int i = arg.length;
		while (--i >= 0) {
			VectorFloat.MIN_AT(a[0], Item = arg[i]);
			VectorFloat.MAX_AT(a[1], Item);
		} return this; }

	/**
     * @return a Copy of this Line
     */
	public Line copy() {
		return new Line(a[0], a[1]); }

///////////////////////////////////////////////////////////////////////////////////
///   Mapping Methods of a Line:
///////////////////////////////////////////////////////////////////////////////////

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
	public float[] map(float[] arg) { return mapAt(VectorFloat.COPY(arg)); }

	/**Stretches each coordinate and adds a Vector to P in Place.
	 * This is an affine Mapping in each coordinate that fits the Unity Box
	 * into this Box.
	 *
	 * Stretch is calculated as the Ratio of the Widths,
	 * Translate is the Difference of the starting Points.
	 *
	 * This can be used to do affine coordinate Transformation for simple Graphics
	 * or to generate uniformly distributed Data in a Hypercube
	 * of random Vectors in [0,1)^n.
	 */
	public float[] mapAt(float[] arg) {
		return VectorFloat.LinAt(arg, getWidthAt(), a[0]); }

	/**
	 * Creates the Mapping of this Box to the L Box in Place.
	 * For this the original Range is first mapped back to the Unity Box
	 * and then into the Target Box.
	 */
	public Line mapToLineAt(Line L) { return (Line) invertAt().catAt(L); }

	/**Creates the inverse Mapping.	 */
	public Line invertAt() {
		VectorFloat.INV_AT(getWidthAt());	//after this mWidth is valid, so use it directly!
		VectorFloat.NEG_AT(
		VectorFloat.MUL_AT(a[0], mWidth));
		VectorFloat.add  (a[1], a[0], mWidth);//+
		inValidate();
		return this; }

	/**Concatenates the two Mappings.
	 * An interesting Dilemma happened here:
	 * calculating Start, Stop  and Width takes one Subtraction more than
	 * calculating Start, Width and Stop.
	 * Still I calculate the first Version, since the Width is optional
	 * and would have to be calculated for the initial and End State!	 */
	public Line catAt(Line arg) {
//		if (! (arg instanceof Line)) return super.mulAt(arg);	//only Rotation!
		((Line) arg).mapAt(a[0]);	//transform the Start Vector
		((Line) arg).mapAt(a[1]);	//transform the Stop  Vector
		inValidate();
		return this; }

	/**Multiplication: *
	 * This is the standard Delegation, because it has been changed in 'float[]'  */
//	public SemiGroupM cat (Object arg) { return ((SemiGroupM) copy()).mulAt(arg); }

	/**Inversion: 1/x
	 * This is the standard Delegation, because it has been changed in 'float[]'  */
//	public GroupM inv() { return ((GroupM) copy()).invAt(); }

	/**Division: /
	 * This is the standard Implementation, because it has been changed in 'float[]'  */
//	public GroupM div(Object arg) { return ((GroupM) copy()).divAt(arg); }

	/**Creates the Zero Mapping, uninteresting.	 */
	/**Setting to 0 in Place:	 */
	public Line zeroAt() {
		VectorFloat.ZERO_AT(a[0]);
		VectorFloat.ZERO_AT(a[1]);
		inValidate();
		return this; }

	/**Setting to 1 in Place:
	 * Creates the identical Mapping.	 */
	public Line oneAt() {
		VectorFloat. ONE_AT(a[0]);
		VectorFloat.ZERO_AT(a[1]);
		inValidate();
		return this; }

	//////////////////////
	//	Object Methods	//
	//////////////////////

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * NewInstance also clones the Types, but does not initialize them!
	 * When overriding, use newInstance on all Components.	 */
//	public copyAble newInstance() { return new Line(); }

}
