package streamIO.copy.group.ring;

/**Interface for a Stepper Routine, either for Integration or for ODEs
 * e.g. Euler Sums or Runge Kutta etc.
 * This can be used for DGLs of higher Degree
 * by just using a Tensor as y Value and defining one Dimension per Degree.
 * Of course you can also integrate several independent DGLs in one.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 33c58da311af1032e9bb1ba07335add56ef5a5e996131c5442db1479c6e11001
 * stale: false
 * tags: [code/ring_theory, code/ode_solver]
 * concepts: [Ring Algebra and ODE Solvers]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface IStepper {

	/**Initializes the Stepper to new Coordinates and a new Differential Equation
	 * The single Constructor of the Stepper should have the same signature!
	 * This is rather ensured by inheriting from AStepper.	 */
//	public void init(IIntRing stepSize, IIntRing x, IIntRing y, IODE  f);

	/**
	 * Performs the next Step with the given Step Size, which is subject to change (e.g. by Quality Control).
	 * @return the taken StepSize, also as a proposal for the next StepSize.
	 */
	IIntRing step(IIntRing stepSize);

	/**
	 * Performs the next Step with the current Step Size, which is subject to change (e.g. by Quality Control).
	 * @return the taken StepSize, also as a proposal for the next StepSize.
	 */
	IIntRing step();

	/**
	 * Performs the next Step with the current Step Size, which is subject to change (e.g. by Quality Control).
	 * @return the Result Vector at the Position.
	 */
	IIntRing run (IIntRing target);

}
