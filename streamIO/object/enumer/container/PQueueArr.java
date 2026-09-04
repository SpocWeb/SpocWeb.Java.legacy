package streamIO.object.enumer.container;

/**Implements a Priority Queue with fixed Length using an Array
 * TODO: implement this, see the PQueue in Package ???
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
