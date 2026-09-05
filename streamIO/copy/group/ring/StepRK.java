package streamIO.copy.group.ring;

import math.vector.VectorDouble;
import function.vector.IBinaryOpFloat;

/**
 *Integrates the given ODE in (x,y) using the Runge-Kutta Formula. Step() performs a single Step with fixed width.
 * Run () performs as many Steps as necessary to reach a certain x Value.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:13:44Z
 * digest: b4ccf01736b87137e5ebdaf37f7d509035dba9557a88ef04bdd5b40b705f667e
 * stale: false
 * tags: [code/ring_theory, code/ode_solver]
 * concepts: [Ring Algebra and ODE Solvers]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class StepRK
extends AStepper {

    ////////////////////////////////////////////////////////////////////////////
    /// #region : Variables
    ////////////////////////////////////////////////////////////////////////////

    /** Local Helper Vectors, contains intermediate Results Reference kept here to keep the Memory allocated */
    protected IIntRing w, v1, v2, v3, v4;

    /** Cache for the scalar Function Value at the first Point Used in Quality Control to save second Evaluation */
    protected double vd;

    /** Cache for the last Result when not replacing */
    protected double yLast;

    /** Local Helper Vectors, contains intermediate Results Reference kept here to keep the Memory allocated */
    protected double[] wd, v1d, v2d, v3d, v4d;

    ////////////////////////////////////////////////////////////////////////////
    /// #region : Constructors, calling each other using this()/super() (not in Interfaces)
    ////////////////////////////////////////////////////////////////////////////
	
    /**Initializes this Stepper and allocates the generic IIntRing Helper Vectors w/v1..v4.	 */
    public void Init(IIntRing y) { //Allocate Space to save iterative Allocation and Destruction!
        super.Init(y);
        //		int Grad = y.getDim();		//Create the Storage for the Helper Vectors:
        w = (IIntRing)y.newInstance(); //new Tensor(x, Grad);
        v1 = (IIntRing)y.newInstance();
        v2 = (IIntRing)y.newInstance();
        v3 = (IIntRing)y.newInstance();
        v4 = (IIntRing)y.newInstance();
    }

    /**Initializes this Stepper and allocates the primitive double[] Helper Vectors wd/v1d..v4d.	 */
    public void Init(double[] y) { //Allocate Space to save iterative Allocation and Destruction!
        super.Init(y);
        //		int Grad = y.getDim();		//Create the Storage for the Helper Vectors:
		wd  = new double[y.length];
        v1d = new double[y.length];
        v2d = new double[y.length];
        v3d = new double[y.length];
        v4d = new double[y.length];
    }

    /** Constructor with all necessary Parameters */
    public StepRK(final IIntRing stepSize_, final IIntRing x, final IIntRing y, final IODE f) {
        super(stepSize_, x, y, f);
    }

    /** Constructor with all necessary Parameters */
    public StepRK(final double stepSize_, final double x, final double y, final IBinaryOpFloat f) {
        super(stepSize_, x, y, f);
    }

    /** Constructor with all necessary Parameters */
    public StepRK(final double stepSize_, final double x, final double[] y, final IBinaryOpFloat f) {
        super(stepSize_, x, y, f);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// #region : Interface IODE: Implementation
    ////////////////////////////////////////////////////////////////////////////

    /**
     *Performs one Runge Kutta Step with given Width h. The starting Point (x, y) is modified to the new Point.
     * V1 retains the Derivative in this Point, so it's calculation can be saved when resetting to the old Point (x, y)
     * @return the Value at the given Point. TODO:
     */
    public IIntRing step(final IIntRing h) { //{R_K_S nur wegen der Schrittweiten-Kontrolle !}
        //V1 and V4 keep their Values for another Calculation
        IIntRing x1 = x;
        if (!replace) x1 = (IIntRing)x1.copy();
		IIntRing h2 = h .half ();
        IIntRing h6 = h2.third();
        if (derive) { f.Funktion(x1, y, v1); }
        x1.addAt(h2); //{V1 = F (x,y)}	//Derivative doesn't have to be calculated each time!
        w.copyAt(y); w.addProdAt(h2, v1); f.Funktion(x1, w, v2); //{V2 = F (x+h/2,y+h/2*V1}
        w.copyAt(y); w.addProdAt(h2, v2); f.Funktion(x1, w, v3); x1.addAt(h2); //{V3 = F (x+h/2,y+h/2*V2}
        w.copyAt(y); w.addProdAt(h, v3); f.Funktion(x1, w, v4); //{V4 = F (x+h  ,y+h  *V3}
        v3.addAt(v2).dblAt();
        v3.addAt(v1).addAt(v4);
        v3.LinAt(h6, y);
        if (replace) { IIntRing yTmp = y; y = v3; v3 = yTmp; return y; }
        //y.addProdAt (H6, V3);	//{y += h/6*(V1 + 2*(V2+V3) + V4)}
        return v3;
    }
    ////////////////////////////////////////////////////////////////////////////
    /// #region : Interface IOdeFloat: Implementation
    ////////////////////////////////////////////////////////////////////////////

    /**
     *Performs one Runge Kutta Step with given Width h. The starting Point (x_, y_) is modified to the new Point if 'replace'
     * is set. V1_ retains the Derivative in this Point,
     * so it's calculation can be saved when resetting to the old Point (x, y)
     * @return the taken StepSize.
     */
    public double step(final double h) { //{R_K_S nur wegen der Schrittweiten-Kontrolle !}
        if (yv == null) {
            return stepScalar(h); }
		 return stepVector(h); }

    /**
     *Performs one Runge Kutta Step with given Width h. The starting Point (x_, y_) is modified to the new Point if 'replace'
     * is set. V1_ retains the Derivative in this Point,
     * so it's calculation can be saved when resetting to the old Point (x, y)
     * @return the taken StepSize.
     */
    public double stepScalar(final double h) { //{R_K_S nur wegen der Schrittweiten-Kontrolle !}
        double x1 = xd;
        double h2 = h * 0.5;
        double h6 = h / 6;
        double V2_, V3_, V4_;
        if (derive) {
            vd = fd.Funktion(x1, yd);
        }
        x1 += h2; //Derivative doesn't have to be calculated each time!
        V2_ = fd.Funktion(x1, yd + h2 * vd);
        V3_ = fd.Funktion(x1, yd + h2 * V2_); x1 += h2;
        V4_ = fd.Funktion(x1, yd + h * V3_); V3_ += V2_;
        yLast = yd + h6 * (vd + V3_ + V3_ + V4_);
        if (replace) { yd = yLast; xd = x1; }
        return h;
    }

    /**
     *Performs one Runge Kutta Step with given Width h. The starting Point (x, y) is modified to the new Point.
     * V1 retains the Derivative in this Point, so it's calculation can be saved when resetting to the old Point (x, y)
     * @return the Value at the given Point. TODO:
     */
    public double stepVector(final double h) //{R_K_S nur wegen der Schrittweiten-Kontrolle !}
    { //V1 and V4 keep their Values for another Calculation
        double x1 = xd;
        double h2 = h * 0.5;
        double h6 = h / 6;
        if (derive) { fd.Funktion(x1, yv, v1d); }
        x1 += h2; //{V1 = F (x,y)}	//Derivative doesn't have to be calculated each time!
        VectorDouble.ADD_PROD(wd, yv, v1d, h2); fd.Funktion(x1, wd, v2d); //{V2 = F (x+h/2,y+h/2*V1}
        VectorDouble.ADD_PROD(wd, yv, v2d, h2); fd.Funktion(x1, wd, v3d); x1 += h2; //{V3 = F (x+h/2,y+h/2*V2}
		VectorDouble.ADD_PROD(wd, yv, v3d, h ); fd.Funktion(x1, wd, v4d); //{V4 = F (x+h  ,y+h  *V3}
        VectorDouble.ADD_AT(v3d, v2d);
        VectorDouble.MUL_AT(v3d, 2);
        VectorDouble.ADD_AT(v3d, v1d);
        VectorDouble.ADD_AT(v3d, v4d);
		VectorDouble.LIN_AT(v3d, h6, yv);
		if (replace) { xd = x1; double[] yTmp = yv; yv = v3d; v3d = yTmp; }
//        yLast = y_ + h6 * (v1 + V3_ + V3_ + V4_);
//        if (replace) { y_ = yLast; x_ = x1; }
		return h;
    }
    ////////////////////////////////////////////////////////////////////////////
    /// #region : static Testing and main() Methods (not in Interfaces)
    ////////////////////////////////////////////////////////////////////////////

    /** Method to test all Implementations in this class. */
    public static void testIt() {
		//super.testIt();	//not easily possible to call static super Method, only using Reflection 
		AStepper.testFloat  (new StepRK(0, 0, 0, null), 0.01); //for explicitness qualified by AStepper
		AStepper.testVector (new StepRK(0, 0, new double[0], null), 0.005); //for explicitness qualified by AStepper
		if (null != streamIO.copy.ACopyAble.testInstance) {
			IIntRing xl = (IIntRing)streamIO.copy.ACopyAble.testInstance.copy();
		 	AStepper.testIntRing(new StepRK(xl, xl, xl, null)); //for explicitness qualified by AStepper
		}
    }

	/**
     *The main entry point for the application.
     * @param args Array of parameters passed to the application via the command line.
     */
    public static void main(final String[] args) { //throws java.io.IOException {
        testIt();
    }
    
}
