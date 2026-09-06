package streamIO.object.enumer.container;

/**Implements a Priority Queue with fixed Length using an Array
 * TODO: implement this, see the PQueue in Package ???
 * <!-- docstate
 * tags: [code/container, code/hash_table, code/container_iteration]
 * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
 * facets: {layer: utility, status: legacy, complexity: high}
 * digest: 183fb1860e18bb6502f7e98d0f03ef558b56441d2cc0c7b97817771c0695a645
 * stale: false
 * -->
 */
public abstract class PQueueArr
extends AContainer {

    /**Storage for the Items in the Queue
     */
	protected int a[];

	/**Initializing Constructor allocating Space for the Queue	 */
	public PQueueArr (int n) {
        a = new int[n]; }

}
