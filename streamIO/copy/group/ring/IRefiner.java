package streamIO.copy.group.ring;

/**
 * Title: IFloatRefiner<p>
 * Description:
 * Interface for performing a single Step 
 * to find a special Point (Zero, FixPoint or Extremum) in Function f 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see refiner.IDoubleRefiner
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 0abcfba82c57a06f8ebc7374e1a2e5f11768ecee947361b1459dab264a2fc426
 * stale: false
 * tags: [code/ring_theory, code/ode_solver]
 * concepts: [Ring Algebra and ODE Solvers]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface IRefiner {

	/**Performs one Step to refine the previous Result	 */
	IIntRing refine();

}
