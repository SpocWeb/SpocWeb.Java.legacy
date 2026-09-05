package graphic.math3D;

//import Stream.Copy.Group.Ring.IODE;
//import Functions.IFunction;
import function.vector.IBinaryOpFloat;
import function.vector.IFloatVectorField;
import function.vector.OdeHeight;
import function.vector.StepConstant;
import graphic.IGraphShape;
import graphic.Point2D;
import math.vector.VectorDouble;
import streamIO.copy.group.ring.AStepper;
import streamIO.copy.group.ring.StepRK;

/**
 * Draws the HyperPlane with constant Potential recursively.
 * This is used for drawing the full Equipotential Plane.
 *
 * TODO: eliminate the hardcoded Stepper Routine StepRKQ
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 3f2710a7e9387a18578a10f248dce64d8ab0b67ed73823334fac3030559072fb
 * stale: false
 * tags: [code/3d_rendering, code/chart_rendering]
 * concepts: [Hyperplane Plotter]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 *		 and the hardcoded ODE Height  */
public class HyperPlanePlotter
	extends ASpatial {

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**Local Reference to the Force Field in Form of an ODE	 */
	protected IBinaryOpFloat force;

	/**Local Reference to the Force Field (time independent	 */
	protected IFloatVectorField Field;

	/**Initial Step Size for the ODE Stepper	 */
	protected float Step;

	/**Maximum Number of Steps in each Dimension	 */
	public int MaxSteps = 24;

	/**Graphics Context to paint to	 */
	protected IGraphShape g2D;

	/**Reference to the Coordinate System for Conversion	 */
	protected ICoordMapper CD;

////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/**Constructor, taking a Force Field in Form of a (time dependant) ODE	 */
	public HyperPlanePlotter(IGraphShape g2D, ICoordMapper CD, IBinaryOpFloat Force, float Step) {
		this.CD		= CD;
		this.g2D	= g2D;
		this.force	= Force;
		this.Step	= Step;
	}

	/**Constructor, taking a Force Field in Form of a (time independant) Field	 */
	public HyperPlanePlotter(IGraphShape g2D, ICoordMapper CD, IFloatVectorField Field, float Step) {
		this.CD		= CD;
		this.g2D	= g2D;
		this.Field	= Field;
		this.Step	= Step;
	}

////////////////////////////////////////////////////////////////////////////
/// #region : Interface ISpatial: Implementation
////////////////////////////////////////////////////////////////////////////

	/**Draw Routine of ISpatial	 */
	public void moveTo (float[] V, Object S) {
		processDim(VectorDouble.COPY(V), V.length-1); }

	/**Recursive Hyper Line Drawer.
	 * At every Stop Point the Stepper branches off to another Dimension.	 */
	public void processDim(double[] V, int k) {
		int dim = V.length;
		int i = k+1; if (i >= dim) i-= dim;
		int j = i+1; if (j >= dim) j-= dim;
		AStepper Stepper;
		if (Field == null)	//Time dependent Version
			Stepper = new StepRK(Step, Step, VectorDouble.COPY(V), new OdeHeight(force, i, j));
		else				//Time independent Version
			Stepper = new StepConstant(Field,
				new StepRK(Step, Step, VectorDouble.COPY(V), null), 0, 2);	//because StepConstant uses it
		Point2D yPNew, yP = CD.mapPt(V);
		//g2D.setPixel(yP); //TODO: only to set the Start Point
		int n= -1;
		while (++n < MaxSteps) {	//Run until the maximum number of Steps
			//or a goal or a minimum Step Size is reached
			//or the Starting point is reached again with an accuracy that depends on the largest Distance reached.
			//or the drawing Area is left
			//or a Key is pressed
			//or a flag is set
			Stepper.stepFloat();
			yPNew = CD.mapPt(Stepper.yv);
			g2D.drawLine(yP, yPNew); yP = yPNew;
			if (k > 0) {
				processDim(Stepper.yv, k-1); }
		}
	}

}
