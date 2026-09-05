package function.vector;

/*
import GroupM.*;
import Ring.*;
import BaseCopy.*;
import ByRef.*;
*/
import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.AStepper;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IODE;
import streamIO.copy.group.ring.metric.body.vector.Tensor;
import streamIO.copy.groupM.IGroupM;
import function.IFunction;
import function.IMeasurAble;

/**Stepper Routine, that integrates the Field from the given Starting Points on
 * along the given Dimensions by keeping the Potential constant
 * (by moving orthogonal to the Force Vector).
 * This Algorithm is very effective, but only for time- independent Fields,
 * because a 1-dimensional ODE is used to integrate along the Lines of same Potential.
 * It works for arbitrary Dimensions and handles even very complicated cases with
 * singular Points by switching automatically between the Integration Dimensions.
 * It uses an Instance of HeightOde for this, so this class is also defined here.
 *
 * The other, more generic way is to just keep all up to two Dimensions constant,
 * by just zeroing out the Derivatives. This is done in "VectorFuncs.OdeHeight"
 *
 * Design Decisions:
 * derived from absStepper, because that is used in all Graphics Routines
 * (because it allows for accessing the y Coordinate)
 * and because it contains some optimizations.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: cffdcdce18a10fa7714999acb4c3d3bb0f9e7efde84136330b99b2ad72af5fd2
 * stale: false
 * tags: [code/differential_integration, code/vector_math]
 * concepts: [ODE Integration]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class StepConstant
	extends AStepper {

	/** Local Reference to the Helper ODE defining the Potential	 */
	protected HeightOde HelpODE;

	/** Local Reference to the used Stepper Routine	 */
	protected AStepper Stepper;

	//The original dimX and dimY Settings have to be kept,
	//because otherwise the StepSize setting makes no sense.

	/** X-Dimension along which the ODE is integrated along	 */
	public int dimX;

	/** Y-Dimension which the ODE is integrated in.	 */
	public int dimY;

	/**Initializing Constructor, works for arbitrary dimensional Vectors,
	 * because it uses a generic Coordinate Mapping Routine
	 * The Stepper Routine is chosen at will, but the ODE is masked by a Helper ODE.
	 * Thus the ODE can be set to null at instantiating the Stepper.
	 */
	public StepConstant (IFloatVectorField Fktn, AStepper Stepper, int dimX, int dimY) {
		this.dimX = dimX;
		this.dimY = dimY;
		this.Stepper = Stepper;
		this.HelpODE = new HeightOde(Fktn);
		double[] tmp = Stepper.yv;
		Stepper.Init(Stepper.xd, HelpODE);	//Use the same type for x and y!
		Init (tmp);	//Use the Tensor Definition from the Stepper
	}

	/**Initializing Constructor, works for arbitrary dimensional Vectors,
	 * because it uses a generic Coordinate Mapping Routine
	 * The Stepper Routine is chosen at will, but the ODE is masked by a Helper ODE.
	 * Thus the ODE can be set to null at instantiating the Stepper.
	 */
	public StepConstant (//IIntRing StepSize, IIntRing x, IIntRing y, intDgl f,
						 IFunction Fktn, AStepper Stepper, int dimX, int dimY) {
		//Have to use the empty Constructor, because the called Init Routine calls my Init Routines!
		this.dimX = dimX;
		this.dimY = dimY;
		this.Stepper = Stepper;
		this.HelpODE = new HeightOde(Fktn);
		IIntRing tmp = Stepper.y;
		Stepper.Init ((IIntRing) Stepper.x.newInstance(), HelpODE);	//Use the same type for x and y!
		Init (Stepper.stepSize, tmp);	//Use the Tensor Definition from the Stepper
	}

	/**Initializes the Stepper to new Coordinates	 */
	public void Init(IIntRing StepSize, IIntRing y) {
		if (StepSize != null) {
			HelpODE.dimX = this.dimX;	//Without resetting the Dimensions
			HelpODE.dimY = this.dimY;	//changing the Step Size makes no sense!
			this.stepSize = (IIntRing) (Stepper.stepSize.copyAt(StepSize));
		}
		Init(y);
	}

	/**(Re-)Sets the Coordinates, done once before a Sequence of Steps
	 * to initialize the Coordinates x and y.	 */
	public void Init(IIntRing y) {	//use the exact same Tensor, because 'this.y' is evaluated!
		this.y = HelpODE.Y = (Tensor) y.copy();	//TODO: Hand over the Vector for Reconstruction, better use a Copy?!
		Stepper.x.copyAt(HelpODE.Y.getAt(HelpODE.dimX));
		Stepper.y.copyAt(HelpODE.Y.getAt(HelpODE.dimY));
	}

	/**Actual Step for plotting the ODE	 */
	public IIntRing step (IIntRing Size){
		Stepper.step(Size);	//changes x and y Coordinates...
		((ICopyAble)HelpODE.Y.getAt(HelpODE.dimX)).copyAt(Stepper.x); //write them back to the original Vector
		((ICopyAble)HelpODE.Y.getAt(HelpODE.dimY)).copyAt(Stepper.y);
		boolean swap;	//for economizing the Check
		double Derive = ((IMeasurAble) HelpODE.lastDerivative).getFloat();
		if (swap = (Derive < -2.0)) Stepper.stepSize.negAt(); else	//negate the Step Size
			swap = (Derive > +2.0);	//because otherwise it would step back!
		if (swap){	//Derivative is kept between -2 and +2 for exact Integration!
			int tmp = HelpODE.dimX; HelpODE.dimX = HelpODE.dimY; HelpODE.dimY = tmp;
			IIntRing tmpC = Stepper.x; Stepper.x = Stepper.y; Stepper.y = tmpC;}
		return y;	//== HelpODE.Y;	//both are the same Tensor, see Init!
	}

	/**Actual Step for plotting the ODE	 */
	public double stepScalar(double Size) { throw new AbstractMethodError(); }

	/**Actual Step for plotting the ODE	 */
	public double stepVector(double Size){
		Stepper.step(Size);	//changes x and y Coordinates...
		((ICopyAble)HelpODE.Y.getAt(HelpODE.dimX)).copyAt(Stepper.x); //write them back to the original Vector
		((ICopyAble)HelpODE.Y.getAt(HelpODE.dimY)).copyAt(Stepper.y);
		boolean swap;	//for economizing the Check
		double Derive = ((IMeasurAble) HelpODE.lastDerivative).getFloat();
		if (swap = (Derive < -2.0)) Stepper.stepSize.negAt(); else	//negate the Step Size
			swap = (Derive > +2.0);	//because otherwise it would step back!
		if (swap){	//Derivative is kept between -2 and +2 for exact Integration!
			int tmp = HelpODE.dimX; HelpODE.dimX = HelpODE.dimY; HelpODE.dimY = tmp;
			IIntRing tmpC = Stepper.x; Stepper.x = Stepper.y; Stepper.y = tmpC;}
		return Size; }	//== HelpODE.Y;	//both are the same Tensor, see Init!

}

/**Helper ODE that converts a time independent Force Field into an ODE
 * that can be integrated by a 1-dim. Stepper Routine, which is very effective.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 5995d3234fcecd9053f6231f71dd0743fd648f9038c8c43c728f1b356f0a54b7
 * stale: false
 * tags: [code/differential_integration, code/vector_math]
 * concepts: [ODE Integration]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 * It is only used in conjunction with the 'StepConstant' Stepper.	 */
class HeightOde
extends AOdeFloat 
implements IODE {

	/**X-Dimension along which the ODE is integrated along	 */
	public int dimX;

	/**Y-Dimension which the ODE is integrated in.	 */
	public int dimY;

	/**Local Reference to the static Vector Function	 */
	protected IFunction Fktn;

	/**Local Reference to the static Vector Function	 */
	protected IFloatVectorField VFn;

	/**Contains the absolute Value of the last Derivative.
	 * This is used by the Plotter above (or a TODO: Stepper) to swap Dimensions,
	 * when the Derivative becomes larger than 1
	 * (or 2 to smoothen swapping between Dimensions)
	 */
	public IIntRing lastDerivative;

	/**Contains the absolute Value of the last Derivative.
	 * This is used by the Plotter above (or a TODO: Stepper) to swap Dimensions,
	 * when the Derivative becomes larger than 1
	 * (or 2 to smoothen swapping between Dimensions)
	 */
	public double lastDerivative_;

	/**This Tensor contains all the Coordinates necessary for the Original ODE.
	 * The Selected Dimensions are exchanged to process actual Values.
	 * The Tensor is public, because it is updated externally. 	 */
	public Tensor Y;

	/**This Array contains all the Coordinates necessary for the Original ODE.
	 * The Selected Dimensions are exchanged to process actual Values.
	 * The Tensor is public, because it is updated externally.
	 */
	public double[] Y_;

	/**This Array contains all the Coordinates necessary for the Original ODE.
	 * The Selected Dimensions are exchanged to process actual Values.
	 * The Tensor is public, because it is updated externally.
	 */
	public double[] DYDX;

	/**Initializing Constructor: takes the Force Field ODE
	 * (usually not Time dependant)
	 */
	public HeightOde(IFunction Fktn_) { this.Fktn = Fktn_; }

	/**Initializing Constructor: takes the Force Field ODE
	 * (usually not Time dependant)
	 */
	public HeightOde(IFloatVectorField VFn_) { this.VFn = VFn_; }

	/**Changes the Force Field ODE into a Height ODE by creating a Vector as Derivative,
	 * that is orthogonal to the Force Field Vector.
	 * All coordinates except for the 0 and 1 are kept exactly the same.
	 * Thus you would have to swap Coordinates before and after Calculation.
	 */
	public void Funktion(IIntRing x, IIntRing y, IIntRing dydx) {
		((ICopyAble) Y.getAt(dimX)).copyAt(x);
		((ICopyAble) Y.getAt(dimY)).copyAt(y);
		Tensor dYdX = (Tensor) Fktn.Map(Y);	//Invert and change Sign for Equipotentiality.
		lastDerivative = (IIntRing) dydx.copyAt(((IGroupM)dYdX.getAt(dimX)).div(dYdX.getAt(dimY)));
		lastDerivative.negAt();
	}

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @param dydx The Derivative at (x,y)
	 */
	public void Funktion(double x, double[] y, double[] dydx) { throw new AbstractMethodError(); }

	/**
	 * Changes the Force Field ODE into a Height ODE by creating a Vector as Derivative,
	 * that is orthogonal to the Force Field Vector.
	 * All coordinates except for the 0 and 1 are kept exactly the same.
	 * Thus you would have to swap Coordinates before and after Calculation.
	 *
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return the 1st Derivative at (x,y) of this Function
	 */
	public double Funktion(double x, double y) {
		Y_[dimX] = x;
		Y_[dimY] = y;
		VFn.map(Y_, DYDX); 	//Invert and change Sign for Equipotentiality.
		return lastDerivative_ = -DYDX[dimX]/DYDX[dimY]; }

}
