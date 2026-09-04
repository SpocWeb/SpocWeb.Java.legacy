package streamIO.copy.group.ring;

/**Interface for a Stepper Routine, either for Integration or for ODEs
 * e.g. Euler Sums or Runge Kutta etc.
 * This can be used for DGLs of higher Degree
 * by just using a Tensor as y Value and defining one Dimension per Degree.
 * Of course you can also integrate several independent DGLs in one.
 *
 * Design Decisions:
 * since some ODEs depends critically on the Starting Values
 * and the Float Processors work with double Accuracy (and more!) internally
 * and double Parameters only fill up the Stack,
 * all these Routines use double Numbers.
 */
public interface IFloatStepper {

	/**Initializes the Stepper to new Coordinates and a new Differential Equation
	 * The single Constructor of the Stepper should have the same signature!
	 * This is rather ensured by inheriting from AStepper.	 */
//	public void init(double StepSize, double x, double y, IOdeFloat f);

	/**
	 * Performs the next Step with the given Step Size, which is subject to change (e.g. by Quality Control).
	 * @return the taken StepSize, also as a proposal for the next StepSize.
	 */
	double step(double stepSize);

	/**
	 * Performs the next Step with the current Step Size, which is subject to change (e.g. by Quality Control).
	 * @return the taken StepSize, also as a proposal for the next StepSize.
	 */
	double stepFloat();

	/**
	 * Performs the next Step with the current Step Size, which is subject to change (e.g. by Quality Control).
	 * @return the taken StepSize, also as a proposal for the next StepSize.
	 */
	double run (double target);

}
