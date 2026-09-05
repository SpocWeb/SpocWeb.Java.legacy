package streamIO.copy.group.ring.metric.body.vector;	//Geometry; import Math.*;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;
import streamIO.copy.order.IOrder;
import function.IOrderAble;

/**All Methods of a Vector can also be applied to both points of the Line
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:53:50Z
 * digest: c9a41805b6c203d80dca35cab710386216221dd99af58f7fab1ef3f993df504a
 * stale: false
 * tags: [code/tensor, code/manifold_generation, code/interpolation]
 * concepts: [Vector/Matrix/Tensor and Manifold Interpolation]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class Line
	extends Tensor {		//This allows for all Matrix Operations.

	//////////////////////
	//	Constructors	//
	//////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Constructor, that takes the Startpoint and either the Width or the Endpoint	 */
	public Line (Tensor Start, Tensor StopWidth, boolean width) {
		super(Start.Carry, 1);
//		Tensor Stop;		//is copied on the Tensor Constructor
					 a[0]   = (Tensor)Start.copy();
		if (width)	{mWidth = (Tensor)StopWidth.copy(); validWidth = true;
					 a[1]   = (Tensor)Start.add(StopWidth);}
		else		{a[1]	= (Tensor)StopWidth.copy();}		//mWidth = (Tensor)Stop .subt (Start);}	//Don't calculate the Width automatically!
	}

	/**Constructor, that takes the Start- and Endpoint	 */
	public Line (Tensor Start, Tensor Stop) {
		this (Start, Stop, false);}

	/**Empty Constructor, defaults the Start- and Endpoint to zero Vectors.	 */
	public Line (IIntRing Element)	{
		this (new Tensor(Element, -1),
			  new Tensor(Element, -1), false); }

	/**Empty Constructor, defaults the Start- and Endpoint to zero Vectors.	 */
//	public Line ()	{ this (new Tensor(), new Tensor(), false); }


	//////////////////
	//	Operations	//
	//////////////////

	//	Retrieve the protected Properties:

	/**Starting Point of the Line	 */
	public Tensor getStart() { return (Tensor) a[0].copy(); }

	/**Ending Point of the Line	 */
	public Tensor getStop () { return (Tensor) a[1].copy(); }

	//////////////
	//	Corners	//
	//////////////

	/**Cache for Tensors with all Corners of the Hypercube with this Extent.	 */
	private Tensor[] Corners;

	/**Returns all Corners of the Hypercube with this Extent.
	 * There are 2^n Corners to this HyperCube,
	 * So for n = 2 it generates 4 Corners and for n = 3 8 Corners.	 */
	public Tensor[] getCorners() {	//Could be done recursively.
		if (Corners == null) {
			int Length = 1 << (((Tensor)a[0]).mDim);
			Corners = new Tensor[Length];
			while (--Length >= 0) {
				Corners[Length] = getCorner(Length); }
		}
		return Corners; }

	/**Returns the indicated Corner of the Hypercube with this Extent.
	 * There are 2^n Corners to this HyperCube, counted from 0.	 */
	public Tensor getCorner(int numCorner) {	//Could be done recursively.
		int i = ((Tensor)a[0]).mDim;
		numCorner <<= 1;	//to undo the first right shift
		IIntRing [] arr = new IIntRing [i];
		while (--i >= 0) {	//reuse the Elements of this Tensor
			arr[i] = ((Tensor) a[(numCorner >>= 1) & 1]).a[i]; }
		return new Tensor(arr);
//		Return.letGrad(, false, false);	//don't create Elements!
	}

	//////////////
	//	Width	//
	//////////////

	/**Flag for valid Cache	 */
	private boolean validWidth;

	/**Difference of Start and End Vectors 	 */
	private Tensor mWidth;

	/**Extent of the Line	 */
	protected Tensor getWidthAt() {
		if (! validWidth) {
			((Tensor) mWidth.copyAt (a[1])).subAt (a[0]);	//Width is recalculated	//mWidth = a[1].subt (a[0]); this is much slower!
			validWidth =true;
		}
		return mWidth; }

	/**Extent of the Line	 */
	public Tensor getWidth() {
		return (Tensor) getWidthAt().copy(); }

	//////////////
	//	Volume	//
	//////////////

	/**Volume of the (Hyper-)Cube with the two Points as opposite Corners	 */
	private IMetricIRing mVol;

	/**Volume of the (Hyper-)Cube surrounding the Line	 */
	public IMetricIRing getVolume() {
		if (mVol == null) {
			mVol = (IMetricIRing) getWidthAt().Prod(); }
		return (IMetricIRing) mVol.copy(); }

	//////////////
	//	Length	//
	//////////////

	/**Length of the Line. This is the euklidean Norm	 */
	private IMetricIRing mLength;

	/**Length of the Line. This is the euklidean Norm	 */
	public IMetricIRing Norm() {
		if (mLength == null) mLength = SqrNorm().SqRtAt();
		return (IMetricIRing) mLength.copy(); }

	//////////////////
	//	SqrLength	//
	//////////////////

	/**Square Length of the Line. This is the euklidean Norm	 */
	private IMetricIRing mSqrLength;

	/**Square Length of the Line. This is the euklidean Norm	 */
	public IMetricIRing SqrNorm() {
		if (mSqrLength == null) mSqrLength = getWidthAt().SqrNorm();
		return (IMetricIRing) mSqrLength.copy(); }

	//////////////////
	//	AbsLength	//
	//////////////////

	/**Returns the Sum of the Length of the (Hyper-)Cube's Edges
	 * with the two Points as opposite Corners.
	 * This is the AbsV - Norm, the fastest Norm */
	private IMetricIRing mAbsV;

	/**Returns the Sum of the Length of the (Hyper-)Cube's Edges
	 * with the two Points as opposite Corners.
	 * This is the AbsV - Norm, the fastest Norm */
	public IMetricIRing AbsV_Norm() {
		if (mAbsV == null) mAbsV = getWidthAt().AbsV_Norm();
		return (IMetricIRing) mAbsV.copy(); }

	//////////////////////
	//	Set Routines	//
	//////////////////////

	/**Signals to the Line Object, that Items dependent on the Width have changed	 */
	public void inValidate() {
		mVol = mAbsV = mLength = mSqrLength =  null; validWidth = false; }

	/**Ending of the Line	 */
	public void setStart (Tensor arg) {
		a[0].copyAt(arg); inValidate(); }

	/**Ending of the Line	 */
	public void setStop  (Tensor arg) {
		a[1].copyAt(arg); inValidate(); }

	/**Ending of the Line	 */
	public void setWidth (Tensor arg) {
		mWidth.copyAt(arg); validWidth = true;
		((Tensor) a[1]  .copyAt (a[0])). addAt (mWidth); //End Point is recalculated
		inValidate();
	}


	//Methods of the Hyper- Cube:

	//Testing

	//Use 'between' and not 'contains' to test, if a Point lies in this Line.

	/**Returns the Volume of the (Hyper-)Cube with the two Points as opposite Corners	 */
	public boolean contains(Line arg) {
		return ((IOrderAble)arg.a[0]).isBetween(a[0], a[1]) &&
			   ((IOrderAble)arg.a[1]).isBetween(a[0], a[1]); }

	/**Returns the Volume of the (Hyper-)Cube with the two Points as opposite Corners	 */
	public boolean intersects(Line arg) {
		return ((IOrderAble)arg.a[0]).isBetween(a[0], a[1]) ||
			   ((IOrderAble)arg.a[1]).isBetween(a[0], a[1]); }

	/**Returns the Volume of the (Hyper-)Cube with the two Points as opposite Corners	 */
	public boolean intersect(Line arg) {
		return ((IOrderAble)arg.a[0]).isBetween(a[0], a[1]) ||
			   ((IOrderAble)arg.a[1]).isBetween(a[0], a[1]); }

	//Operations:

	protected boolean bolOrdered;

	/**Returns whether {@link #orderAt()} has already been applied to this Line
	 * (i.e. Start holds the smaller and Stop the higher Coordinates).	 */
	public boolean ordered() { return bolOrdered; }

	/**Orders the Coordinates, so the smaller ones end up in Start
	 * and the higher ones end up in Stop.	 */
	public Line orderAt() {
		Tensor Max = (Tensor) ((IOrder)a[0]).Max  (a[1]);
		((IOrder)a[0]).MinAt(a[1]);
		a[1] = Max;	//now the Rectangle is ordered, this allows for Optimizations!
		bolOrdered = true;
		return this; }

	/**Returns the (Hyper-)Cube merged with the Point or Polygon 	 */
	public Line merge(Tensor arg) { return ((Line) copy()).mergeAt(arg); }

	/**Returns the (Hyper-)Cube merged with the Point or Polygon in Place
	 * After this, the Cube is ordered.	 */
	public Line mergeAt(Tensor arg) {
		if (! bolOrdered) orderAt();
		if (arg.a [0] instanceof Tensor) {
			for (int i = -1; ++i < arg.getDim();) {
				Tensor Item = (Tensor) arg.a [i];
				((Tensor) a[0]).MinAt(Item);
				((Tensor) a[1]).MaxAt(Item);
			}
		} else {
			((Tensor) a[0]).MinAt(arg);
			((Tensor) a[1]).MaxAt(arg);
		}
		inValidate();
		return this; }	//next merge would allow for only one MaxAt Operation, because this Rectangle is already ordered!

	//  Methods of a Line:

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
	public Tensor map(Tensor arg) { return mapAt((Tensor) arg.copy()); }

	/**Stretches each coordinate and adds a Vector to P in Place.
	 * This is an affine Mapping in each coordinate that fits a Box into another Box.
	 *
	 * Stretch is calculated as the Ratio of the Widths,
	 * Translate is the Difference of the starting Points.
	 *
	 * This can be used to do affine coordinate Transformation for simple Graphics
	 * or to generate uniformly distributed Data in a Hypercube
	 * from random Vectors in [0,1)^n.
	 */
	public Tensor mapAt(Tensor arg) { return (Tensor) arg.LinAt(getWidthAt(), a[0]); }

	/**Creates the Mapping of this Box to the L Box in Place.	 */
	public Line mapLineAt(Line L) { return (Line) invAt().mulAt(L); }

	/**Concatenates the two Mappings.
	 * An interesting Dilemma happened here.
	 * calculating Start, Stop  and Width takes one Subtraction more than
	 * calculating Start, Width and Stop.
	 * Still I calculate the first Version, since the Width is optional
	 * and would have to be calculated for the initial and End State!	 */
	public ISemiGroupM mulAt(Object arg) {
		if (! (arg instanceof Line)) return super.mulAt(arg);	//only Rotation!
		((Line) arg).mapAt	((Tensor) a[0]);	//transform the Start Vector
		((Line) arg).mapAt	((Tensor) a[1]);	//transform the Stop  Vector
		inValidate();
		return this; }

	/**Creates the inverse Mapping.	 */
	public IGroupM invAt() {
		getWidthAt().invAt();	//after this mWidth is valid, so use it directly!
		((Tensor) a[0]).mMulAt(mWidth).negAt();
		((Tensor) a[1].copyAt(a[0])).addAt(mWidth);//+
		inValidate();
		return this; }

	/**Multiplication: *
	 * This is the standard Delegation, because it has been changed in 'Tensor'  */
	public ISemiGroupM mul (Object arg) { return ((ISemiGroupM) copy()).mulAt(arg); }

	/**Inversion: 1/x
	 * This is the standard Delegation, because it has been changed in 'Tensor'  */
	public IGroupM inv() { return ((IGroupM) copy()).invAt(); }

	/**Division: /
	 * This is the standard Implementation, because it has been changed in 'Tensor'  */
	public IGroupM div(Object arg) { return ((IGroupM) copy()).divAt(arg); }

	/**Creates the Zero Mapping, uninteresting.	 */
	/**Setting to 0 in Place:	 */
//	public Group zeroAt() { return this; }

	/**Setting to 1 in Place:
	 * Creates the identical Mapping.	 */
	public IGroupM oneAt() {
		((Tensor) a[0]).oneAt();
		((Tensor) a[1]).zeroAt();
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
	public ICopyAble newInstance() { return new Line(Carry); }

}
