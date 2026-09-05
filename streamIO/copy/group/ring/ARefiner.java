package streamIO.copy.group.ring;

import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import function.IFunction;
import function.IMeasurAble;
import function.derive.ring.body.Cosinus;

/**
 * Framework for performing a Step to find a Solution (e.g. Root of Function f)
 *
 * Design Decisions:
 * This Class is for Determining an x Value that fulfills a certain Criterion,
 * like with Finding Roots or Minima.
 * For iterated stepping along a Function (Integration etc.) use AStepper.
 *
 * known Sublasses:
 * @see streamIO.copy.group.ring.SecantRefiner
 * @see streamIO.copy.group.ring.FixPtRefiner
 * @see streamIO.copy.group.ring.NewtonRefiner
 * @see streamIO.copy.group.ring.NewtonRefiner2
 *
 * similar Classes: 
 * @see streamIO.copy.group.ring.ARefiner
 *
 * AStepper is not inherited, since it uses IODE instead of Function
 * and also needs different Variable Names.
 * No use in defining an abstract Super Class just for xl, yl, dx.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 026045d1c455d858e489c47efb37d316f86e83038f4c02fe0789a22bd2247ca8
 * stale: false
 * tags: [code/ring_theory, code/ode_solver]
 * concepts: [Ring Algebra and ODE Solvers]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public abstract class ARefiner
implements IRefiner {

	/** Logger for Testing, modify Threshold for switching Logging */
	protected static Log L = new Log(ARefiner.class);

	////////////////////////////////////////////////////////////////////////////

	/**Initializing the Iteration
	 * by giving the Function and a Starting Point.	 */
	public void init(IIntRing x_, final IFunction f_) {
		f = f_;
		xl = (IIntRing) x_.copy();
		yl = (IIntRing) f_.Map(xl); }

	/**Empty Constructor.	 */
	public ARefiner()	{}

	/**Initializing Constructor for Iteration
	 * by giving the Function and a Starting Point.	 */
	public ARefiner(IIntRing x_, IFunction f_) {
		init(x_, f_); }

	/**Contains the last Step Size	 */
	public IIntRing dx;

	/**The last / left x Value 	 */
	public IIntRing xl;

	/**The last / left y Value	 */
	public IIntRing yl;

	/**The (a priori) Multiplicity of the Zero.
	 * Set by NewtonRefiner2, required for Correction by ...	 */
	public IIntRing multiplicity;

	/**Local Reference to the Function for which the Zero has to be determined.	 */
	protected IFunction f;

	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Counter for Testing	 */
	final static public IFunction TEST_FUNCTION = Cosinus.Cosinus;

	/** Fixpoint for the given Test Function 	 */
	final static public IIntRing TEST_FIX_POINT = new BodyDouble(0.7390851332151607);

	/** One Zeropoint for the given Test Function 	 */
	final static public IIntRing TEST_ZERO_POINT = new BodyDouble(IMeasurAble.PI_HALF);

	/** One Minimum for the given Test Function 	 */
	final static public IIntRing TEST_MIN_POINT = new BodyDouble(IMeasurAble.PI);

	/**Method to test all Implementations in this class.	 */
	protected static final void TEST_REFINER(final ARefiner refiner, final Object solution, final int maxIter) {	//RingFuncs only used for testing!
		for (int i = maxIter; --i >= 0;) { //
			refiner.refine(); 
			L.n("x=").l(refiner.xl).l("	y=").l(refiner.yl); 
			if (refiner.xl.equals(solution)) {
				return; }
		}
		Assert.FAIL("Maximum Number of Iterations exceeded:"+maxIter+" at x="+refiner.xl+" with y="+refiner.yl);
	}

}
