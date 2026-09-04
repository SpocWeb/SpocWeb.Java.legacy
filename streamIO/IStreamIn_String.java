package streamIO;

/**
  * IStreamIn_String.java
  * Interface for Classes that inherently allows to build (singly) linked Structures
  * like Linked Lists, Trees and Set Representations.
  *
  * Any StreamStringIn can also be used as a Multiplexer (on a Get Request Basis)
  * by just connecting several Processes, Threads etc. to it
  *
  * Design Decisions:
  * Since this is usually used for querying recursive Structures,
  * the Parent is assumed to be an IStreamIn_String too, instead of Object,
  * which reduces casting.
  * This is the same Design Conflict as between Association and ListItem.
  *
  * When Collections cannot contain 'null',
  * 	you can test the return Values for EOI and SOI,
  * 	This is also possible with RMI, because 'null' is transferred correctly by RMI.
  * If the Collection contains 'null's, you have to test for both 
  *  'null' AND (isValid()), which is nearly as fast due to shortCut Evaluation:
  *
  * Testing for 'null' or EOI as special Values is faster 
  * than a Testing Function like hasNext(), because Variables are used directly. 
  *
  * while ((EOI  != (currItem = Iter.nextItem())) || Iter.isValid()) {
  *
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
  * Created on 3. März 2001, 10:48
  *
  * @author  Matthias Heuer
  * @version
  * @stereotype enumeration
  */
public interface IStreamIn_String {
	
	////////////////////////////////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** @return the (minimum) Number of Items left (in the Buffer),
	  * i.e. the minimum Number of times to call nextItem().
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number.
	  *
	  * Nearly equivalent is currItem != null
	  * (when the Container does not contain null Entries, like e.g. HashTables)
	  */
	public long availAble();
	
	/** @return the next (Parent) String of this one.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	  * This is less explicit, but much faster because Exception Handling can be extremely slow.
	  * Alternatively this Method can block until new Data is available,
	  * but this should always have a TimeOut to avoid DeadLocks.
	  */
	public String nextString();
	
}
