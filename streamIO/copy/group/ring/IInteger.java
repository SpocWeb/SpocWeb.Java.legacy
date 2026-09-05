package streamIO.copy.group.ring;

/**Defines the most basic Operations inc() and dec(), usually for Integer Types.
 * Used when a 1 Element is added to an additive Group, e.g. in the Integrity Ring
 * Usually implemented together with the 'countable' Interface
 * and complemented with the 'integer' Interface.
 * Also used for sequential Access of Data Structures.
 * Since it modifies the State it cannot be used for Const etc.
 * but it allows a minimal Interface for Counters (Increment and Decrement)
 * like e.g. for Versioning (but the availability of dec() defies this)
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 2a4385bce6dfb1700a29c1b87c92f879e35cad64b337342c9e961d27ff76d10a
 * stale: false
 * tags: [code/ring_theory, code/ode_solver]
 * concepts: [Ring Algebra and ODE Solvers]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface IInteger {

	/**Setting to Zero: 0	 */	integer ZeroAt();
	/**Increment: x++	 */	integer inc();
	/**Decrement: x--	 */	integer dec();
}
