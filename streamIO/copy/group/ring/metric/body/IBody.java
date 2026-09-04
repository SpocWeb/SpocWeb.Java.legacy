package streamIO.copy.group.ring.metric.body;

/**Body (M,+,-,*,/,0,1):
 * Defines the most basic Interface necessary for a Body.
 *
 * Design Decisions:
 * All Operations are already defined in IntegrityRing.
 * The only reason to distinguish between MetricIRing and MetricBody
 * is to make the Interfaces smaller and to have a better overview.
 * Eventually I'm gonna stop adding Functions directly to Body
 * and instead create them statically with Body Argument. */
public interface IBody
{
	/**Returns the largest (closest to positive infinity) value in Place,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer. 	 */
//	public MetricIRing FloorAt();

}
