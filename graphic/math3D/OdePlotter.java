package graphic.math3D;

import graphic.IGraphShape;
import graphic.Point2D;

import java.awt.Rectangle;

import math.vector.VectorDouble;
import streamIO.copy.group.ring.AStepper;

/**Integrates the ODE from the given Starting Points on.
 * Works for arbitrary Dimensions.
 * Implements ISpatial to be called from Rastering Routines.
 * This draws the ODE Trajectories for the Rastering Points.
 * Only the GUI is used as the Storage for the calculated Data.
 *
 * Design Decisions:
 * Most Variables kept in package, so they can be accessed by PlotOde2 and PlotOdeRect
 */
public class OdePlotter
	extends ASpatial {

	/** External Flag to stop Integrating asynchronously	 */
	public boolean stop;

	/**Graphics Context to paint to	 */
	final IGraphShape g2D;

	public boolean bothWays = true;

	/**Reference to the Coordinate System for Conversion	 */
	final ICoordMapper CD;

	/**Local Reference to the ODE Stepper 	 */
	final AStepper Stepper;

	/**Local Reference to the Rectangle, can be null	 */
	Rectangle Rect;

	/**Maximum number of Integration Steps to prevent infinite Loops 	 */
	public int MaxSteps = 20;

	/** (initial) Step Size for integrating the ODE	 */ 
	public double stepSize = 0.1; 

	/**Initializing Constructor, works for both 2D and 3D Vectors,
	 * because it uses a generic Coordinate Mapping Routine
	 * The Rectangle can be null, it won't be checked then.	 */
	public OdePlotter(IGraphShape g2D, ICoordMapper CD, AStepper Stepper, Rectangle Rect) {
		this.CD		 = CD;
		this.g2D	 = g2D;
		this.Stepper = Stepper;
		if (Rect != null) {
			Rect = Rect.getBounds();
			Rect.x -= Rect.width ;
			Rect.y -= Rect.height;
			Rect.height += Rect.height << 1;	//Size is three times as large
			Rect.width  += Rect.width  << 1;
			this.Rect = Rect;
		}
	}

	/**Starting Point	 */
	protected Point2D yP;

	/**Starts integrating the ODE from V as the Starting Point.
	 * S is ignored and can be null	 */
	public void moveTo(final float[] V, final Object S) {
		yP = CD.mapPt(V); drawPrep(stepSize, VectorDouble.COPY(V)); }

	/**(Re-)Sets the Coordinates	 */
	public void drawPrep(final double StepSize, final double[] V) {
		boolean turned = false;
		do{
			Stepper.Init(StepSize, V);	//In Init, some processing might take place,
			g2D.moveTo(yP.getLocation());	//like in StepConstant.Init()!
			drawLoop();
			if (bothWays) {
				//if (StepSize == 0) { //ran into Singularity
				Stepper.stepSizeDbl = -StepSize; }
		} while (bothWays && (turned = !turned));
	}

	/**Actual Loop for plotting the ODE
	 * Separating the Loop from the draw(V,S) Routine
	 * saves the Step of Mapping V again
	 * (when starting in opposite Direction from the same Point)	 */
	public void drawLoop() {
		Point2D currPoint;
		int i = -1; //Run until...
		do {
			//or a goal or a minimum Step Size is reached
			//or the Starting point is reached again with an accuracy that depends on the largest Distance reached.
			//
			//or a Key is pressed
			//or a flag is set
			Stepper.stepFloat(); currPoint = CD.mapPt(Stepper.yv); 
			g2D.drawLine(currPoint);
		} while ((++i < this.MaxSteps) && //the maximum number of Steps
			   (! stop) && //or the drawing Area is left
			   ((Rect != null) && Rect.contains(currPoint.x, currPoint.y)));
	}

}
