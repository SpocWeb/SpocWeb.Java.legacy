package streamIO;


/** This is the Interface containing the Method to create an Iterator for a Container.
  * All Containers implement the StreamOut Interface,
  * but this can be used only for a single Client.
  * Being able to inadvertedly serve multiple Clients
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:24Z
  * digest: 74a7163d227c0c4c3eb64ea97e32e0e7f8ab0c8cd787271bb09a326fdf93b0ff
  * stale: false
  * tags: [code/iterator]
  * concepts: [Iterable Interface]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  * introduces Problems with Concurrency.  */
public interface IIterAble {

	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator     */
	IIStreamIn Iterator();

}
