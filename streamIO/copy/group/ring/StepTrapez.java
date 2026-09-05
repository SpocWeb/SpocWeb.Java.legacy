package streamIO.copy.group.ring;

import math.vector.VectorDouble;
import streamIO.copy.group.IGroup;
import function.IFloatFunction;
import function.IFunction;
import function.vector.IFloatVectorFunction;

/**Integrates the Function f(x) using the Trapez (MidPoint) Rule.
 * Uses the same Parent as StepMP, which integrates the ODE
 * with the full MidPoint Rule, because both work the same.
 *
 * Step() performs a single Step with fixed width and 'numSteps' middle Points.
 * Run () performs as many Steps as necessary to reach a certain x Value.
 * The Result is always in the public (x,y) Values.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:13:57Z
 * digest: 80048007eaea110228f4d600d82308040b23ec22bb9d2d57160ba5528a870a22
 * stale: false
 * tags: [code/ring_theory, code/ode_solver]
 * concepts: [Ring Algebra and ODE Solvers]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class StepTrapez
extends AStepper {

////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////

	/**Use a Function Reference instead of the ODE Reference of AStepper.
	 * This is due to the similar Methods both the Midpoint Rule (for ODE)
	 * and the Trapez Rule (for Functions)	 */
	protected IFunction Fn;

	protected IFloatVectorFunction VFn;

	protected IFloatFunction SFn;

	/**Local Helper Vectors, contains intermediate Results	 */
	protected IIntRing y2, y3, x2, x3;

	/**Local Helper Vectors, contains intermediate Results	 */
	protected double[] Y2, Y3, Y4;

	/** Number of Middle Points with one MidPoint Call	 */
	public int numSteps = 1;

////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/**Initializes the Stepper to new Values:
	 * Stepsize, Start-X, Start-Y and Function f	 */
	public void Init(IIntRing Step_, IIntRing x_, IIntRing y_, IFunction f)	{ //, IODE f_) {
		//Allocate Space to save iterative Allocation and Destruction!
		super.Init(Step_, x_, y_, null);//f_);		//Create the Storage for the Helper Vectors:
		x2 = (IIntRing) x.newInstance();
		x3 = (IIntRing) x.newInstance();
		y2 = (IIntRing) y.newInstance();
		y3 = (IIntRing) y.newInstance();
		Fn = f;
	}

	/**Initializes this Stepper and allocates the primitive double[] Helper Vectors Y2/Y3/Y4.	 */
	public void Init(double Step_, double x_, double[] y_, IFloatVectorFunction f_) {
		//Allocate Space to save iterative Allocation and Destruction!
		super.Init(Step_, x_, y_, null);		//Create the Storage for the Helper Vectors:
		Y2 = new double[y_.length];
		Y3 = new double[y_.length];
		Y4 = new double[y_.length];
		VFn = f_;
	}

	/** Constructor with all necessary Parameters  */
	public StepTrapez(double Step_, double x, double y, IFloatFunction f) {
		super(Step_, x, y, null);
		this.SFn = f; }

	/** Constructor with all necessary Parameters  */
	public StepTrapez(double Step_, double x, double[] y, IFloatVectorFunction f) {
		super(Step_, x, y, null);
		this.VFn = f; }

	/** Constructor with all necessary Parameters.
	 * It takes a Function instead of an ODE,   */
	public StepTrapez(IIntRing Step_, IIntRing x, IIntRing y, IFunction f) {
        super(Step_, x, y, null); Fn = f; }

	/**Empty Constructor, used for testing.	 */
	protected StepTrapez() { }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface IStepper: Implementation
////////////////////////////////////////////////////////////////////////////

	/**Performs numStep Midpoint Steps with given Width h.
	 * The starting Point (x) is modified to the new Point.
	 * The good Thing about the Midpoint Rule is that all changes are simply added up,
	 * without using complicated correction Factors or calculating new y Start Values.
	 * Additionally you can reuse the previous Points (x,y).	 */
	public IIntRing step(IIntRing h) {	//{R_K_S nur wegen der Schrittweiten-Kontrolle !}
		int n = numSteps;
		x2.zeroAt();	//better against rounding Errors
		y2.zeroAt();	//to start with an empy Vector!
		if (derive)	{          y3 =(IIntRing)Fn.Map(x);                             x2.addAt(h); }	//{V1 = F(x)}	//Function doesn't have to be calculated each time!
		while (--n >= 0) {     y2.addAt (Fn.Map (((IGroup)x3.copyAt(x)).addAt(x2))); x2.addAt(h); }
		if (derive) {          y3.addAt (Fn.Map (((IGroup)x3.copyAt(x)).addAt(x2))); }
		y2.addAt(y3.halfAt()); y2.mulAt(h);
		if (replace) { x.copyAt(x3); return (IIntRing) y.addAt(y2); }
		return y2; }

	/**Performs numStep Midpoint Steps with given Width h.
	 * The starting Point (x) is modified to the new Point.
	 * The good Thing about the Midpoint Rule is that all changes are simply added up,
	 * without using complicated correction Factors or calculating new y Start Values.
	 * Additionally you can reuse the previous Points (x,y).	 */
	public double stepVector(double h) {	//{R_K_S nur wegen der Schrittweiten-Kontrolle !}
		int n = numSteps;
		double x2 = 0;	//better against rounding Errors
		VectorDouble.ZERO_AT(Y2);	//to start with an empy Vector!
		if (derive) {
			VFn.map(xd, Y3); x2 += h; }	//{V1 = F(x)}	//Function doesn't have to be calculated each time!
		while (--n >= 0) {
			VFn.map(xd + x2, Y4); VectorDouble.ADD_AT(Y2, Y4);  x2 += h; }
		if (derive) {
			VFn.map(xd + x2, Y4); VectorDouble.ADD_AT(Y3, Y4);
		VectorDouble.MUL_AT(Y3, 0.5); }
		VectorDouble.ADD_AT(Y2, Y3);
		VectorDouble.MUL_AT(Y2, h);
		if (replace) { xd += x2; VectorDouble.ADD_AT(yv, Y2); }
		return h; }

	/**
     * Cache for the previous Values for first and last Point
     * to be reused over the same Interval
     */
	protected double y3_;

	/**Performs numStep Midpoint Steps with given Width h.
	 * The starting Point (x) is modified to the new Point.
	 * The good Thing about the Midpoint Rule is that all changes are simply added up,
	 * without using complicated correction Factors or calculating new y Start Values.
	 * Additionally you can reuse the previous Points (x,y).	 */
	public double stepScalar(double h) {	//{R_K_S nur wegen der Schrittweiten-Kontrolle !}
		int n = numSteps;
		double x2 = 0;	//better against rounding Errors
		double Y2 = 0;	//to start with an empy Vector!
		if (derive) {
			y3_ = SFn.Map(xd     ); x2 += h; }	//{V1 = F(x)}	//Function doesn't have to be calculated each time!
		while (--n >= 0) {
			Y2 += SFn.Map(xd + x2); x2 += h; }
		if (derive) {
			y3_+= SFn.Map(xd + x2); y3_*= 0.5; }
		Y2 += y3_; Y2 *= h;
		if (replace) {
			xd += x2; yd += Y2; }
		return h; }

////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/**Local Instance for Testing.	 */
	protected static StepTrapez TZ = new StepTrapez();

	/**Method to test all Implementations in this class.	 */
	public static void testIt()	{	//Testing single Step of Trapez Method
		System.out.println("Testing StepTrapez:");
		IIntRing xl = (IIntRing) streamIO.copy.ACopyAble.testInstance.copy();
		IIntRing yl = (IIntRing) streamIO.copy.ACopyAble.testInstance.copy();
		xl.copyAt(new Double(1));
		yl.copyAt(new Double(0));

		//Code commented out to reduce Dependency between Metric and RingFuncs

		System.out.println("Integrating y = x from 1 to 2 with a single Step. Should be exact!");
		System.out.println("Startpoint: (" + xl + "," + yl + ")");
//TODO		TZ.Init((IIntRing) xl.copy(), xl, yl, Identity.Identity);	//fSquare());	//IdentityCopy());
		TZ.step();	//use given Step Size
		System.out.println("Integral Values: (" + TZ.x + "," + TZ.y +")");
		System.out.println("Expected: (2, 1.5)");

		System.out.println("Integrating y = x^2  from 1 to 2 with a single Step. No longer exact!");
		System.out.println("Startpoint: (" + xl + "," + yl + ")");
//TODO		TZ.Init ((IIntRing) xl.copy(), xl, yl, RingFuncs.Square.Square);	//IdentityCopy());
		TZ.step();	//use given Step Size
		System.out.println("Integral Values: (" + TZ.x + "," + TZ.y +")");
		System.out.println("Expected: (2, 2.333333333)");
	}

}
