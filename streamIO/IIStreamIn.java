package streamIO;

/**
  * IStreamIn.java
  * Interface for Classes that inherently allows to build (singly) linked Structures
  * like Linked Lists, Trees and Set Representations.
  *
  * Any StreamIn can also be used as a Multiplexer (on a Get Request Basis)
  * by just connecting several Processes, Threads etc. to it
  *
  * Design Decisions:
  * Since this is usually used for querying recursive Structures,
  * the Parent is assumed to be an IStreamIn too, instead of Object,
  * which reduces casting.
  * This is the same Design Conflict as between Association and ListItem.
  *
  * When Collections cannot contain 'null',
  * 	you can test the return Values for EOI and SOI,
  * 	This is also possible with RMI, because null is transferred by RMI.
  * If the Collection contains 'null', you have to test for both
  *  'null' AND (available() <= 0), which is nearly as fast due to shortCut Evaluation:
  *
  * while ((EOI != (currItem = Iter.nextItem())) || (Iter.availAble() >= 0)) {
  * while ((EOI != (currItem = Iter.nextItem())) || Iter.isValid()) {
  *
  * or better with an only locally defined Loop Variable:
  * for(Object curr; EOI != (curr = Iter.nextItem()) || Iter.isValid();) {
  *
  * if ((EOI == findFirst(Item)) && !isValid()) {
  * if ((EOI == findFirst(Item)) && (this.availAble() < 0)) {
  *
  * For testing whether you have reached the End AFTER the Iteration
  * you just use the following Test:
  *
  *
  * Similarly it is faster and doesn't hurt to quickly test for Identity first,
  * before testing for Equality:
  *
  * if ((a == b) || (a.equals(b)))
  *
  * The Advantage of this Interface to the Java 1.2 Iterator is:
  * available() or hasMore() needn't be called separately,
  * when the Collection doesn't contain 'null's, which makes it faster
  * and prevents the Effect of the Container changing between calling hasMore()
  * and nextItem() by a different Thread thus saving to synchronize this Access.
  *
  * Linked Structures are most flexible, but not effective in many ways;
  * rather use Arrays, especially for primitive Types:
  * Mostly they are only accessible sequentially (slow)
  * The Memory is distributed on the Heap which results in Cache-Failures
  *
  * Created on 3. M�rz 2001, 10:48
  *
  * @author  Matthias Heuer
  * @version
  * @stereotype enumeration
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:24Z
  * digest: 1836aebb8f07dcb37d1f97f30a96938c57ff803af9ee1d3faeca81cc0e7979fd
  * stale: false
  * tags: [code/iterator]
  * concepts: [Item Input Stream Interface]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public interface IIStreamIn
extends IFactory {
    
	////////////////////////////////////////////////////////////////////////////
	//	static Members
	////////////////////////////////////////////////////////////////////////////
    
	/**Object to indicate the End of any Enumerator
	 * Default Value for nextItem() and currItem() when at the End of Iteration
	 * This allows to add 'null' to Containers,
	 * although the hashCode Operation fails on these,
	 * so you cannot use them for Set Operations.
	 *
	 * 'null' has the Advantage of being castAble to any other Type
	 * and thus save an assignment command.
	 * while new Object() actually create an Object, this is not castable
	 * to any other Type!
	 */
	final static public Object EOI = null; //"-=!EOI!=-"; //null; //new Object(); //
	
	/**Object to indicate the Start of any Enumerator
	 * Default Value for currItem() before any Call of nextItem()
	 * A possible Problem is that these Elements have no Identity like 'null'
	 * which can be used across virtual Machines e.g. for RMI Calls.
	 * For this a Separate Singleton Class should be created.
	 * A new Object() can also not be cast to any other Type!
	 */
	final static public Object SOI = null; //"-=!SOI!=-"; //null; //new Object(); //
	
	////////////////////////////////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Reports whether this Stream is still valid, i.e. no more Items have been retrieved via {@link #nextItem()} than the Stream actually contains.
	 * @return true if the streamIO is still valid,
	 * i.e. not more Items were retrieved than there were in this streamIO. 
	 * 
	 * It should be used to test Streams with no unique End Result for their End like in
	 * for(int    curr; EOF != (curr = nextInt ()) && isValid();) { ... }
	 * for(Object curr; EOI != (curr = nextItem()) && isValid();) { ... }
	 * 
	 * This Function changes to false when nextItem() was called 
	 * AFTER the last Item was retrieved from the streamIO. 
	 * Thus it is not equivalent to hasNext() == !isEmpty() 
	 * which change on retrieving the last Item from the streamIO, 
	 * one nextItem() Call earlier. 
	 */
	public boolean isValid();
	
	/** @return the (minimum) Number of Items left (in the Buffer),
	  * i.e. the minimum Number of times to call nextItem().
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number of calls to nextItem().
	  * This can be used to dimension Buffers for Processing.
	  * Since Streams are not required to return this Number accurately,
	  * it was moved into the StreamIn Interface.
	  *
	  * Nearly equivalent is currItem != null
	  * (when the Container does not contain null Entries, like e.g. HashTables)
	  *
	  * To make the Test easier for Clients, the isEmpty() Method has been added,
	  * which is equivalent to available() &lt; 0
	  */
	//public long availAble();
	
	/** @return true if the streamIO is empty, i.e. no more Items are left in this streamIO
	 * AFTER the nextItem() Method has been called.
	 * equivalent to '0 > available()'
	 * This Method was created to make the Test for the End more OO
	 * and less dependent on the actual Value of availAble().
	 */
	//public boolean isEmpty();
	
}
