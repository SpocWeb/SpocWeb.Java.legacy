package streamIO;


/** This is the Interface containing the Method to create an Iterator for a Container.
  * All Containers implement the StreamOut Interface,
  * but this can be used only for a single Client.
  * Being able to inadvertedly serve multiple Clients
  * introduces Problems with Concurrency.  */
public interface IIterAble {

	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator     */
	IIStreamIn Iterator();

}
