package streamIO.copy.group.ring;

import math.vector.VectorDouble;
import streamIO.Assert;
import streamIO.IIStreamIn;
import streamIO.Log;
import function.ICountAble;
import function.IFloatFunction;
import function.byref.ByRefDouble;
import function.vector.IBinaryOpFloat;
import function.vector.IFloatVectorField;

/**
 * Abstract Stepper Routine for Integration of ODEs and Functions.
 * Step() performs a single Step and controls it's width.
 * Run () performs as many Steps as necessary to reach a certain x Value.
 * The Result is always in the public (x,y) Values.
 *
 * Design Decisions:
 * The Reason why not another Class is responsible for Multistep Integration
 * is that the run() Method looks exactly the same for each Stepper,
 * except for the difference between variable and fixed Step Size.
 * This is not true for Determining an x Value that fulfills a certain Criterion,
 * like with Finding Roots or Minima!
 * For that ARefiner and it's descendants are used!
 */
public abstract class AStepper
implements IStepper, IFloatStepper, IIStreamIn { //IStreamIn_Float {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(AStepper.class, -0);
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**Determines calculation of 1st Derivative in the Step
	 * When using iterated Calls, you can save one Function Evaluation 	 */
	protected boolean derive = true;
	
	/**Determines replacement of Coordinates (x,y) by the new ones 	 */
	protected boolean replace = true;
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables for Object Calculations
	////////////////////////////////////////////////////////////////////////////
	
	/**Actual Position x, can be changed arbitrarily	 */
	public IIntRing x;
	
	/**Actual Position y, can be changed arbitrarily	 */
	public IIntRing y;
	
	/**Local Reference to the Differential Equation. Should not be changed arbitrarily.	 */
	public IODE f;
	
	/**proposed Default StepSize of the next Step, can be changed arbitrarily	 */
	public IIntRing stepSize;
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables for float Point Calculations
	////////////////////////////////////////////////////////////////////////////
	
	/**Local Reference to the Differential Equation. Should not be changed arbitrarily.	 */
	public IBinaryOpFloat fd;
	
	/**proposed Default StepSize of the next Step, can be changed arbitrarily	 */
	public double stepSizeDbl;
	
	/**Actual Position x, can be changed arbitrarily	 */
	public double xd;
	
	/**Actual Position y, can be changed arbitrarily	 */
	public double yd;
	
	/**Actual Position y, can be changed arbitrarily	 */
	public double[] yv;
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor, only used by StepConstant!	 */
	protected AStepper() { }
	
	/**Single Constructor for all Steppers	 */
	public AStepper(IIntRing StepSize, IIntRing x, IIntRing y, IODE  f) {
		Init (StepSize, x, y, f);}
	
	/**Single Constructor for all Steppers	 */
	public AStepper(double StepSize, double x, double y, IBinaryOpFloat f) {
		Init (StepSize, x, y, f);}
	
	/**Single Constructor for all Steppers	 */
	public AStepper(double StepSize, double x, double[] y, IBinaryOpFloat f) {
		Init (StepSize, x, y, f);}

	/**Initializes the Stepper to new Coordinates and a new Differential Equation
	 * The single Constructor of the Stepper should have the same signature!	 */
	public void Init(IIntRing StepSize, IIntRing x, IIntRing y, IODE  f) {
		this.f = f; Init(StepSize, x, y); }

	/**Initializes the Stepper to new Coordinates and a new Differential Equation
	 * The single Constructor of the Stepper should have the same signature!	 */
	public void Init(IIntRing y, IODE  f) {
		this.f = f; Init(y); }

	/**Initializes the Stepper to new Coordinates	 */
	public void Init(IIntRing StepSize, IIntRing x, IIntRing y) {
		this.x = (IIntRing) x.copy(); Init(StepSize, y);}

	/**Initializes the Stepper to new Coordinates	 */
	public void Init(IIntRing StepSize, IIntRing y) {
		//this.StepSize.copyAt(StepSize);
		if (StepSize != null) this.stepSize = (IIntRing) StepSize.copy();
		Init(y);
	}

	/**Initializes the Stepper to new y Coordinates (used for time-independent ODEs)	 */
	public void Init(IIntRing y) { this.y = (IIntRing) y.copy(); }


	/**Initializes the Stepper to new Coordinates and a new Differential Equation
	 * The single Constructor of the Stepper should have the same signature!	 */
	public void Init(double StepSize, double x, double y, IBinaryOpFloat f) {
		this.fd = f; Init(StepSize, x, y); }

	/**Initializes the Stepper to new Coordinates and a new Differential Equation
	 * The single Constructor of the Stepper should have the same signature!	 */
	public void Init(double y, IBinaryOpFloat f) {
		this.fd = f; Init(y); }

	/**Initializes the Stepper to new Coordinates	 */
	public void Init(double StepSize, double x, double y) {
		this.xd = x; Init(StepSize, y);}

	/**Initializes the Stepper to new Coordinates	 */
	public void Init(double StepSize, double y) {
		this.stepSizeDbl = StepSize;
		Init(y);
	}

	/**Initializes the Stepper to new y Coordinates (used for time-independent ODEs)	 */
	public void Init(double y) { this.yd = y; }
	
	/**Initializes the Stepper to new Coordinates and a new Differential Equation
	 * The single Constructor of the Stepper should have the same signature!	 */
	public void Init(double StepSize, double x, double[] y, IBinaryOpFloat f) {
		this.fd = f; Init(StepSize, x, y); }
	
	/**Initializes the Stepper to new Coordinates and a new Differential Equation
	 * The single Constructor of the Stepper should have the same signature!	 */
	public void Init(double[] y, IBinaryOpFloat f) {
		this.fd = f; Init(y); }
	
	/**Initializes the Stepper to new Coordinates	 */
	public void Init(double StepSize, double x, double[] y) {
		this.xd = x; Init(StepSize, y);}
	
	/**Initializes the Stepper to new Coordinates	 */
	public void Init(double StepSize, double[] y) {
		this.stepSizeDbl = StepSize;
		Init(y);
	}
	
	/**
	 * Initializes the Stepper to new y Coordinates (used for time-independent ODEs)
	 * Using an (expensive) Copy to leave the Start Vector unchanged!
	 */
	public void Init(double[] y) { this.yv = VectorDouble.COPY(y); }
	
	/**
	 * Performs the next Step with the given Step Size, which is subject to change (e.g. by Quality Control).
	 * @return the taken StepSize.
	 */
	public IIntRing step() { return step(stepSize); }
	
	/**
	 * Performs several Step with the given Step Size, which is subject to change (e.g. by Quality Control).
	 * @return the taken StepSize.
	 */
	public IIntRing run (final IIntRing Target) { return stride (Target); }
	
	/**Performs several Steps with a fixed Step Size up to the Target	 */
	protected IIntRing stride(final IIntRing target) {
		//Instead of a Metric (not defined yet!), use the exact Distance
		IIntRing dist = (IIntRing) target.sub(x);
		int numSteps = ((ICountAble)dist.div(stepSize)).getInt();
		if (numSteps < 0) {numSteps = -numSteps; stepSize.negAt();}
		while (--numSteps > 0) {
			step(stepSize); }
		return step((IIntRing) target.sub(x));	//last Step is calculated again
	}	//because of rounding Errors and also variable Step Size.
	
	/**
	 * Performs the next Step with the given Step Size, which is subject to change (e.g. by Quality Control).
	 * @return the taken StepSize.
	 */
	public double stepFloat() { return step(stepSizeDbl); }
	
	/**
	 * Performs several Steps with the given Step Size, which is subject to change (e.g. by Quality Control).
	 * @return the last taken StepSize.
	 */
	public double run (final double target) { return stride (target); }
	
	/**Performs several Steps with a fixed Step Size up to the Target	 */
	protected double stride(final double target) {
		//Instead of a Metric (not defined yet!), use the exact Distance
		final double dist = target - xd;
		int numSteps = (int) (dist / stepSizeDbl);
		if (numSteps < 0) { 
			numSteps = -numSteps; stepSizeDbl = -stepSizeDbl; }
		while (--numSteps > 0) {
			step(stepSize); }
		return step(target - xd); 	//last Step is calculated again
	}	//because of rounding Errors and also variable Step Size.
	
	/**Performs one Runge Kutta Step with given Width h.
	 * The starting Point (x_, y_) is modified to the new Point if 'replace' is set.
	 * V1_ retains the Derivative in this Point,
	 * so it's calculation can be saved when resetting to the old Point (x, y)
	 * @return the taken StepSize.
	 */
	public double step(double h) {	//{R_K_S nur wegen der Schrittweiten-Kontrolle !}
		if (yv == null) {
			return stepScalar(h); }
			return stepVector(h); }
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IStreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** @return the (minimum) Number of Items left (in the Buffer),
	  * i.e. the minimum Number of times to call nextItem().
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number.
	  *
	  * Nearly equivalent is currItem != null
	  * (when the Container does not contain null Entries, like e.g. HashTables)
	  */
	public long availAble() { return 100; } //return a reasonably high Number
	
	/**
	 * @see streamIO.IIStreamIn#isValid()
	 */
	public boolean isValid() { return true; }
	
	/** @return the next (Parent) Object of this one.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	  * This is less explicit, but much faster because Exception Handling can be extremely slow.
	  * Alternatively this Method can block until new Data is available,
	  * but this should always have a TimeOut to avoid DeadLocks.
	  */
	public Object nextItem() {
		stepFloat();
		return yv; }
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IStepper: abstract Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Performs the next Step with the given Step Size, which is subject to change (e.g. by Quality Control).
	 * @return the taken StepSize.
	 */
	public abstract IIntRing step(IIntRing StepSize);
	
	/**Performs one Runge Kutta Step with given Width h.
	 * The starting Point (x_, y_) is modified to the new Point if 'replace' is set.
	 * V1_ retains the Derivative in this Point,
	 * so it's calculation can be saved when resetting to the old Point (x, y)
	 * @return the taken StepSize.
	 */
	public abstract double stepScalar(double h);
	
	/**Performs one Runge Kutta Step with given Width h.
	 * The starting Point (x, y) is modified to the new Point.
	 * V1 retains the Derivative in this Point,
	 * so it's calculation can be saved when resetting to the old Point (x, y)
	 * @return the Value at the given Point. 
	 */
	public abstract double stepVector(double h);
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.
	 * Relies on testStepper set to a concrete Implementation.
	 * Tests the Integration of ODEs
	 * @param testStepper Instance of the Stepper to be tested
	 */
	public static void testIntRing(AStepper testStepper) {	//Testing single Step of MultiPoint Method
		L.n("Using AStepper to test...:");
		IIntRing xl = (IIntRing) streamIO.copy.ACopyAble.testInstance.copy();
		IIntRing xr = (IIntRing) streamIO.copy.ACopyAble.testInstance.copy();
		IIntRing y0 = (IIntRing) streamIO.copy.ACopyAble.testInstance.copy();
		xl.copyAt(new Double(1));
		xr.copyAt(new Double(2));
		y0.copyAt(new Double(0));
		L.n("First Testing on simple Functions:");

		//Code commented out to reduce Dependency between Metric and RingFuncs
		
		L.n("Integrating y' = 0 from 1 to 2 with a single Step. Should be exact!");
		L.n("Startpoint: (" + xl + "," + y0 + ")");
//TODO		testStepper.Init((IIntRing) xl.copy(), xl, y0, new RingFuncs.OdeConst());
		testStepper.step();	//use given Step Size
		L.n("Integral Values: (" + testStepper.x + "," + testStepper.y +")");
		L.n("Expected: (2, 0)");
		
		L.n("Integrating y' = 1 from 1 to 2 with a single Step. Should be exact!");
		L.n("Startpoint: (" + xl + "," + y0 + ")");
//TODO		testStepper.Init((IIntRing) xl.copy(), xl, y0, new RingFuncs.OdeLinear());
		testStepper.step();	//use given Step Size
		L.n("Integral Values: (" + testStepper.x + "," + testStepper.y +")");
		L.n("Expected: (2, 1)");

		L.n("Integrating y' = x from 1 to 2 with a single Step. Should be exact!");
		L.n("Startpoint: (" + xl + "," + y0 + ")");
//TODO		testStepper.Init((IIntRing) xl.copy(), xl, y0, new RingFuncs.OdeSquare());
		testStepper.step();	//use given Step Size
		L.n("Integral Values: (" + testStepper.x + "," + testStepper.y +")");
		L.n("Expected: (2, 1.5)");

		L.n("Now Testing it on ODEs: ");

		L.n("Integrating y' = y from 1 to 2 with a single Step. Not exact!");
		L.n("Startpoint: (" + xl + "," + xl + ")");
//TODO		testStepper.Init((IIntRing) xl.copy(), xl, xl, new RingFuncs.Function2ODE(Identity.Identity));
		testStepper.step();	//use given Step Size
		testStepper.run (xr);	//use given Step Size
		L.n("Integral Values: (" + testStepper.x + "," + testStepper.y +")");
		L.n("Expected: (2, 2.718281828459)");

		L.n("Integrating y' = y directly from 1 to 2 with a single Step. No longer exact!");
		L.n("Startpoint: (" + xl + "," + xl + ")");
//TODO		testStepper.Init((IIntRing) xl.copy(), xl, xl, new RingFuncs.OdeExp());
		testStepper.step();	//use given Step Size
		testStepper.run (xr);	//use given Step Size
		L.n("Integral Values: (" + testStepper.x + "," + testStepper.y +")");
		L.n("Expected: (2, 2.718281828459)");

	}

	/**Method to test all Implementations in this class.
	 * Relies on testStepper set to a concrete Implementation.
	 * Tests the Integration of ODEs
	 * @param testStepper Instance of the Stepper to be tested
	 */
	public static void testFloat(final AStepper testStepper, final double accuracy) {	//Testing single Step of MultiPoint Method
		L.n("Using AStepper to test Class:").l(testStepper.getClass());
		final double stepSize = 1; 
		final double y0 = 1;
		final double x0 = 1;
		final double x1 = 2;
		final double x2 = 3;
		//Code commented out to reduce Dependency between Metric and RingFuncs

		L.n();
		L.n("First Testing on simple Functions:");
		L.n("Integrating y' = 0  <=> y = 0 from 1 to 2 with a single Step. Should be exact!");
		L.n("Startpoint: (" + x0 + "," + y0 + ")");
		testStepper.Init(stepSize, x0, y0, new function.derive.ring.OdeConst());
		testStep(testStepper, x1, y0);
		testStep(testStepper, x2, y0);

		L.n();
		L.n("Integrating y' = 1 <=> y = x from 1 to 2 with a single Step. Should be exact!");
		L.n("Startpoint: (" + x0 + "," + y0 + ")");
		testStepper.Init(stepSize, x0, y0, new function.derive.ring.OdeLinear());
		testStep(testStepper, x1, y0+1);
		testStep(testStepper, x2, y0+2);

		L.n();
		L.n("Integrating y' = x <=> y = x^2/2-0.5 from 1 to 2 with a single Step. Should be exact!");
		L.n("Startpoint: (" + x0 + "," + y0 + ")");
		testStepper.Init(stepSize, x0, y0, new function.derive.ring.OdeSquare());
		testStep(testStepper, x1, y0+1.5);
		testStep(testStepper, x2, y0+4  );
		
		L.n();
		L.n("Now Testing it on real ODEs (no longer exact!): ");
		L.n("Integrating y' = y <=> y = exp(x) from 1 to 2 with a single Step. Not exact!");
		L.n("Startpoint: (" + x0 + "," + x0 + ")");
		testStepper.Init(stepSize, x0, y0, new function.derive.ring.Function2ODE((IFloatFunction) function.derive.Identity.IDENTITY));
		testStep(testStepper, x1, y0*Math.exp(x1-x0), accuracy);
		testStep(testStepper, x2, y0*Math.exp(x2-x0), Math.sqrt(accuracy));
	}

	private static void testStep(final AStepper testStepper, final double x1, final double y1) {
		testStep(testStepper, x1, y1, ByRefDouble.DOUBLE_ACCURACY); }

	private static void testStep(final AStepper testStepper, final double x1, final double y1, final double accuracy) {
		testStepper.run(x1);	//use given Step Size
		L.n("Integral Values: (").l(testStepper.xd).l(testStepper.yd).l(")");
		Assert.EQUALS(x1, testStepper.xd); //high Accuracy! 
		Assert.EQUALS(y1, testStepper.yd, accuracy, accuracy);
	}

	/**Method to test all Implementations in this class.
	 * Relies on testStepper set to a concrete Implementation.
	 * Tests the Integration of ODEs
	 * @param testStepper Instance of the Stepper to be tested
	 */
	public static void testVector(final AStepper testStepper, final double accuracy) {	//Testing single Step of MultiPoint Method
		L.n("Using AStepper to test Class:").l(testStepper.getClass());
		final double stepSize = 1; 
		final double x0 = 1;
		final double x1 = 2;
		final double x2 = 3;
		final double[] y0 = {1};
		//Code commented out to reduce Dependency between Metric and RingFuncs
		L.n("First Testing on simple Functions (i.e. Integration):");

		L.n();
		L.n("Integrating y' = 0  <=> y = 0 from 1 to 2 with a single Step. Should be exact!");
		L.n("Startpoint: (" + x0 + "," + y0[0] + ")");
		testStepper.Init(stepSize, x0, y0, new function.derive.ring.OdeConst());
		testVectorStep(testStepper, x1, y0[0]);
		testVectorStep(testStepper, x2, y0[0]);

		L.n();
		L.n("Integrating y' = 1 <=> y = x from 1 to 2 with a single Step. Should be exact!");
		L.n("Startpoint: (" + x0 + "," + y0[0] + ")");
		testStepper.Init(stepSize, x0, y0, new function.derive.ring.OdeLinear());
		testVectorStep(testStepper, x1, y0[0]+1);
		testVectorStep(testStepper, x2, y0[0]+2);

		L.n();
		L.n("Integrating y' = x <=> y = x^2/2 from 1 to 2 with a single Step. Should be exact!");
		L.n("Startpoint: (" + x0 + "," + y0[0] + ")");
		testStepper.Init(stepSize, x0, y0, new function.derive.ring.OdeSquare());
		testVectorStep(testStepper, x1, y0[0]+1.5);
		testVectorStep(testStepper, x2, y0[0]+4  );

		L.n();
		L.n("Now Testing it on real ODEs: ");
		y0[0] = x0;
		L.n("Integrating y' = y <=> y = exp(x) from 1 to 2 with a single Step. Not exact!");
		L.n("Startpoint: (" + x0 + "," + y0[0] + ")");
		testStepper.Init(stepSize, x0, y0, new function.derive.ring.Function2ODE((IFloatVectorField) function.derive.Identity.IDENTITY));
		testVectorStep(testStepper, x1, y0[0]*Math.exp(x1-x0), accuracy);
		testVectorStep(testStepper, x2, y0[0]*Math.exp(x2-x0), Math.sqrt(accuracy));
	}
	
	private static void testVectorStep(final AStepper testStepper, final double x1, final double y1) {
		testVectorStep(testStepper, x1, y1, ByRefDouble.DOUBLE_ACCURACY); }
	
	private static void testVectorStep(final AStepper testStepper, final double x1, final double y1, final double accuracy) {
		testStepper.run(x1);	//use given Step Size
		L.n("Integral Values: (" + testStepper.xd + "," + testStepper.yv[0] +")");
		Assert.EQUALS(testStepper.xd   , x1);
		Assert.EQUALS(testStepper.yv[0], y1, accuracy, accuracy);
	}

	/**Method to test all Implementations in this class.	 */
/*	public static void testIt() {
		L.n("Testing single Step of Runge-Kutta Method: StepRK:");
//		super.testIt();	//not possible to call static Method
		testFloat  (new StepRK(0, 0,  0 , null)); //
		testVector (new StepRK(0, 0, {0}, null)); //
		IIntRing xl = (IIntRing) Stream.Copy.ACopyAble.testInstance.copy();
		testIntRing(new StepRK(xl, xl, xl, null)); //for explicitness qualified by AStepper
	}
*/
}
