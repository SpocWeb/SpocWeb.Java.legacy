package streamIO.copy.group.ring;

import math.vector.VectorDouble;
import function.vector.IBinaryOpFloat;

/**Integrates the ODE in (x,y) by using the MidPoint Rule.
 * This is exactly the Trapezoidal Rule used to integrate normal function:
 *
 *
 *
 * Step() performs a single Step with fixed width.
 * Run () performs as many Steps as necessary to reach a certain x Value.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:13:31Z
 * digest: 648bef55b270b3a2ea64f9217f40f52bfea59f76d5b8efa2a037caa4bae00c75
 * stale: false
 * tags: [code/ring_theory, code/ode_solver]
 * concepts: [Ring Algebra and ODE Solvers]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class StepMP
extends AStepper {

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**Local Helper Vectors, contains intermediate Results	 */
	protected IIntRing W, V1, V2;

	/**Local Helper Vectors, contains intermediate Results	 */
	protected double[] W_, V1_, V2_;

	/** Number of Middle Points with one MidPoint Call	 */
	public int numSteps = 1;

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Initializes this Stepper and allocates the generic IIntRing Helper Vectors W/V1/V2.	 */
	public void Init(IIntRing Step_, IIntRing x_, IIntRing y_, IODE f_) {
		//Allocate Space to save iterative Allocation and Destruction!
		super.Init(Step_, x_, y_, f_);		//Create the Storage for the Helper Vectors:
		W	= (IIntRing) y.newInstance();
		V1	= (IIntRing) y.newInstance();
		V2	= (IIntRing) y.newInstance();
	}

	/**Initializes this Stepper and allocates the primitive double[] Helper Vectors W_/V1_/V2_.	 */
	public void Init(double Step_, double x_, double[] y_, IBinaryOpFloat f_) {
		//Allocate Space to save iterative Allocation and Destruction!
		super.Init(Step_, x_, y_, f_);		//Create the Storage for the Helper Vectors:
		W_  = new double[y_.length];
		V1_ = new double[y_.length];
		V2_ = new double[y_.length];
	}

	/** Constructor with all necessary Parameters  */
	public StepMP(double Step_, double x, double y, IBinaryOpFloat f) {
		super(Step_, x, y, f); }

	/** Constructor with all necessary Parameters  */
	public StepMP(double Step_, double x, double[] y, IBinaryOpFloat f) {
		super(Step_, x, y, f); }

	/** Constructor with all necessary Parameters  */
	public StepMP(IIntRing Step_, IIntRing x, IIntRing y, IODE f) {
		super(Step_, x, y, f); }

	/**Performs numStep Midpoint Steps with given Width h.
	 * The starting Point (x, y) is modified to the new Point.
	 * The good Thing about the Midpoint Rule is that all changes are simply added up!
	 * The bad thing is that, unlike with normal Integration,
	 * you cannot reuse the previous Points.
	 */
	public IIntRing step(IIntRing h)	//{R_K_S nur wegen der Schrittweiten-Kontrolle !}
	{	//V1 and V4 keep their Values for another Calculation
		//h = (IIntRing) h.div(numSteps);
		int n = numSteps;
		IIntRing x1 = x; if (!replace) x1 = (IIntRing)	x1.copy();
		IIntRing y1 = y; if (!replace) y1 = (IIntRing)	y1.copy();
		IIntRing h2 = (IIntRing) h.dbl();
		W.copyAt (y1);  //possibly save one Evaluation
		if (derive) {            f.Funktion (x1, W, V1); } x1.addAt(h2);	//{V1 = F (x	,y		 )}	//Derivative doesn't have to be calculated each time!
			W.addProdAt(h , V1); f.Funktion (x1, W, V2);				//{V2 = F (x+h/2,y+h/2*V1)}
		while (--n > 0) {
			IIntRing tmp = y1; y1 = W; W = tmp;	//swap W and y
			W.addProdAt(h2, V2); f.Funktion(x1, W, V2);    x1.addAt(h);
		}
			W.addProdAt(h , V2).addAt(y1);
			W.halfAt();
		if (replace) {
			IIntRing yTmp = y; y = W; W = yTmp; x1.subAt(h); return y; }
		return W; }

	/**Performs numStep Midpoint Steps with given Width h.
	 * The starting Point (x, y) is modified to the new Point.
	 * The good Thing about the Midpoint Rule is that all changes are simply added up!
	 * The bad thing is that, unlike with normal Integration,
	 * you cannot reuse the previous Points. 	 */
	public double stepVector(double h)	//{R_K_S nur wegen der Schrittweiten-Kontrolle !}
	{	//V1 and V4 keep their Values for another Calculation
		//h = (IIntRing) h.div(numSteps);
		int n = numSteps;
		double   x1 = xd;
		double[] y1 = yv ; 
		if (!replace) { 
			y1 = VectorDouble.COPY(y1); }
		double h2 = h + h; //possibly save one Evaluation
		VectorDouble.COPY (y1, W_);
		if (derive) {                            fd.Funktion (x1, W_, V1_); } x1 += h2; 	//{V1 = F (x	,y		 )}	//Derivative doesn't have to be calculated each time!
			VectorDouble.ADD_PROD_AT(W_, h , V1_); fd.Funktion (x1, W_, V2_);				//{V2 = F (x+h/2,y+h/2*V1)}
		while (--n > 0) {
			double[] tmp = y1; y1 = W_; W_ = tmp;	//swap W and y
			VectorDouble.ADD_PROD_AT(W_, h2, V2_); fd.Funktion(x1, W_, V2_);    x1 += h;
		}
			VectorDouble.ADD_PROD_AT(W_, h , V2_);
			VectorDouble.ADD_AT(W_, y1);
			VectorDouble.MUL_AT(W_, 0.5);
		if (replace) {
			yv = W_; xd = x1 - h; }
		return h; }

	private double dy_;

	/**Performs numStep Midpoint Steps with given Width h.
	 * The starting Point (x, y) is modified to the new Point.
	 * The good Thing about the Midpoint Rule is that all changes are simply added up!
	 * The bad thing is that, unlike with normal Integration,
	 * you cannot reuse the previous Points. 	 */
	public double stepScalar(final double h)	//{R_K_S nur wegen der Schrittweiten-Kontrolle !}
	{	//V1 and V4 keep their Values for another Calculation
		//h = (IIntRing) h.div(numSteps);
		int n = numSteps;
		double x1 = xd;
		double y1 = yd;
		double W_ = y1;
		double V2_;
		double h2 = h + h; //possibly save one Evaluation
		if (derive) {      dy_ = fd.Funktion (x1, W_); } x1 += h2; 	//{V1 = F (x	,y		 )}	//Derivative doesn't have to be calculated each time!
			W_ += h * dy_; V2_ = fd.Funktion (x1, W_);				//{V2 = F (x+h/2,y+h/2*V1)}
		while (--n > 0) {
			double tmp = y1; y1 = W_; W_ = tmp;	//swap W and y
			W_ += h2 * V2_; V2_ = fd.Funktion(x1, W_);   x1 += h;
		}
			W_ += h * V2_ + y1;
			W_ *= 0.5;
		if (replace) {
			yd = W_; xd = x1 - h; }
		return h; }

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {//Testing single Step of MultiPoint Method
		System.out.println("Testing StepMP (MultiPoint):");
		IIntRing xl = (IIntRing) streamIO.copy.ACopyAble.testInstance.copy();
	//	super.testIt();	//not possible
		AStepper.testIntRing(new StepMP(xl, xl, xl, null));
	}
}
