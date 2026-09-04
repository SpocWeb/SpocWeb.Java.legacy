package streamIO.object;

import java.io.InputStream;
import java.util.Comparator;

import streamIO.IAvailAble;
import streamIO.IIStreamIn;
import streamIO.IIterAble;
import streamIO.IMarkAble;
import streamIO.IOrdered;
import streamIO.IPushBackAble;
import streamIO.IReSetAble;
import tester.IEquivalence;
import tester.IScalarMetric;
import tester.ITester;
import function.IIOrderAble;
import function.IProcessor;

/**
  * defines additional Iterator Operations; indirectly derived from IStreamIn:
  * An Iterator is possibly infinitely long (like a streamIO),
  * thus lastXxx Operations are not well defined!
  *
  * An Iterator is also IterAble, because it knows the Container it is operating on
  * and can call it's Iterator() Method or create a new Iterator itself.
  * If the Source is not a Container or not IterAble, it should return 'null'.
  *
  * Any StreamIn can also be used as a Multiplexer (on a Get Request Basis)
  * by just connecting several Processes, Threads etc. to it
  *
  * Created on 26. Mai 2001, 22:59
  *
  * @author  Matthias Heuer
  * @version
  * @stereotype enumeration
  */
public interface IStreamIn
extends IIStreamIn, IAvailAble, IIterAble, IMarkAble, IOrdered {
	
	/**
	 * Reference to the single empty Inputstream.
	 * It always returns -1 (EOF)
	 */
	final static public DevNullIn DEV_NULL_IN = new DevNullIn();
	
	////////////////////////////////////////////////////////////////////////////////
	//  Member Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return The Comparator being used to compare Elements on Searching.
	  * If 'null', the Elements are assumed to implement
	  * @see IScalarMetric or
	  * @see Comparable  or
	  * @see IIOrderAble	 */
	public Comparator getComparator();
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX/isXXX/makeXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** @return the Filter Object
	  * only Items that are equal to this Object are returned by nextItem()! */
	public Object getFilter();
	
	/** Sets the Filter Object
	  * only Items that are equal to this Object are returned by nextItem()!
	  * This allows for Optimizations on hashed and sorted Containers
	  * because the Result Set can be decreased dramatically. */
	public void setFilter(Object Value);

	////////////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////////////

	/** Stores all Items availAble().
	  * This Method is unnecessary, because it corresponds to Store.add(this); */
	//public long storeItems(StreamOut Store);
	
	/** @return and moves to the last (Root) Object of this one.
	  * This should be used with Care, because it could result in Blocking
	  * or infinite Loops with infinite Streams. */
	public Object lastItem();
	
	/** @return the current Object without moving.
	  * This is just a caching Functionality and should be done
	  * at the Client Process, for faster Access.	 */
	public Object currItem();
	
	/** Returns the next Item without moving to it.	 */
	public Object peekItem(); // throws NoSuchMethodException;
	
	/**Returns the Item after the next Item by moving to the next one.
	  * This allows a pre-read with removing the current Item.	 */
	//	public Object nextNextItem();
	
	/** @return the Object at the given Position in this Enumeration
	  * The Result depends on whether the Iterator is deterministic
	  * and supports these Operations.
	  * The Exception is typically thrown in the reSet() Op.
	  */
	public Object getAt(final int Position) throws NoSuchMethodException;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Convenience Array Read Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns as many Items as are availAble() currently 	 */
	public Object[] nextItems();
	
	/** Returns as many Items as possible, but maximum numItems stored in Items from Index Begin 	 */
	public int nextItems(final Object[] Items, int numItems, int Begin);
	
	/** Returns as many Items as possible, but maximum numItems stored in Items 	 */
	public int nextItems(final Object[] Items, int numItems);
	
	/** Returns as many Items as possible, but maximum Items.length in Items 	 */
	public int nextItems(final Object[] Items);
	
	/** Tests, whether this Object exists in the Set,
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences.
	  */
	public Object findNext (final Object Item);
	
	/** Tests, whether this Object[index] exists in the Set of Arrays or Streams,
	  * @param index The Index of the Object interpreted as an Array or StreamIn
	  * This Index could also be a String in not numbered Collections (Strings are nearly Objects)
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences.
	  * Implementation should allow for both Object[index] and StreamIn.getAt(index)
	  */
	public Object findNext (Object Item, int index);

	/** Tests, whether this Object[index] exists in the Set of Arrays or Streams,
	  * @param index The Index of the Object interpreted as an Array or StreamIn
	  * This Index could also be a String in not numbered Collections (Strings are nearly Objects)
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences.
	  * Implementation should allow for both Object[index] and StreamIn.getAt(index)
	  */
	public Object findNext (Object Item, int index, IEquivalence EQ);

	/** Tests, whether this Object exists in the Set,
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences. 	 */
	public Object findNext (Object Item, IEquivalence EQ);

	/**Tests, whether this Object exists in the Set,
	 * Returns it, when found, otherwise returns streamIO.Iterator.EOL	
	 * Returns any found Instance, not necessarily the first or the last one!  */
	public Object find(final Object Item); 

	/** Tests, whether this Object exists in the Set,
	  * @return the first Item found that equals Item, otherwise IStreamIn.EOI
	  * Cannot be used iteratively, because it resets the streamIO. 	 */
	public Object findFirst (final Object Item) throws NoSuchMethodException;

	/** Tests, whether this Object exists in the Set,
	  * @return the first Item found that equals Item, otherwise IStreamIn.EOI
	  * @param EQ Equivalence Relation used to test Equality, instead of equals()
	  * Cannot be used iteratively, because it resets the streamIO. 	 */
	public Object findFirst (Object Item, IEquivalence EQ) throws NoSuchMethodException;
	
	/** @return the Object when it is contained in this Container
	  * This is the same Operation as findFirst()
	  */
	//public Object containsItem(Object Item); //
	
	/** @return true when this Object is contained in this or streamIO
	  * This is the same Operation as (findFirst() != EOI) || (availAble() >= 0)
	  * @see Sub() and SubEq() for the according Container Methods,
	  * The Name contains() is only to be used for single Elements
	  */
	public boolean contains(Object Item);

	/**Tests, whether all Objects of streamIO arg exist in this streamIO,
	 * Requires this streamIO to be restartAble.
	 * More restrictive and thus 'cheaper' Searches exist for predictive Iterators:
	 * -finding all Elements of arg in Sequence
	 * -finding all Elements of arg in Sequence with intermittent Objects
	 * Uses the Monotony Criterion (for infinite Streams) of findFirst()
	 * This corresponds to the contains() Method.	 */
	public boolean SubEq (IIStreamIn arg, boolean Sequence);
	
	//Convenience Bulk Tests and Operations
	
	/**Performs the Operation of the Operator on each Item in the Collection
	 * that equals this Item. The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public int forEachThatEquals(Object Item, IProcessor op);
	
	/**Returns the first Item of those Item in the Collection that equals this Item,
	 * that also fulfills the Test of the ITester Object.
	 * The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public Object firstOfEachThatEqualsThat(Object Item, ITester Test) throws NoSuchMethodException;
	
	/**Returns the first Item of those Item in the Collection that equals this Item,
	 * that also fulfills the Test of the ITester Object.
	 * The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public Object nextOfEachThatEqualsThat(Object Item, ITester Test);
	
	/**Tests each Object with the given Test.
	 * Stops on the first Object that returns true.
	 * Returns it, otherwise returns streamIO.Iterator.EOI	 */
	public Object nextThat (ITester tst);
	
	/**Tests each Object with the given Test.
	 * Stops on the first Object that returns true.
	 * Returns it, otherwise returns streamIO.Iterator.EOI	 */
	public Object firstThat (ITester tst) throws NoSuchMethodException;
	
	/**Performs an Operation for each Object in this Iteration.
	 * Returns the Number of Operations performed.	 */
	public int forEach (IProcessor op);
	
}

/**
 * Instance of a Null Device that gives no Input
 * Simple Helper Class to avoid complicated Workarounds
 * Always symbolizes an empty Input streamIO.
 *
 */
class DevNullIn
extends InputStream
implements IStreamIn {
    
    /** @see streamIO.IReSetAble#getPosition()     */
    public long getPosition() { return 0; }
    
	/** @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() { return ORDER_NONE; }
	
	/** @return The Comparator being used to compare Elements.
	  * If 'null', the Elements are assumed to implement
	  * @see IScalarMetric or
	  * @see Comparable  or
	  * @see IIOrderAble	 */
	public Comparator getComparator () { return null; }
	
	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator     */
	public IIStreamIn Iterator() { return null; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX/isXXX/makeXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** @return the Filter Object
	  * only Items that are equal to this Object are returned by nextItem()! */
	public Object getFilter() { return null; }
	
	/** Sets the Filter Object
	  * only Items that are equal to this Object are returned by nextItem()!
	  * This allows for Optimizations on hashed and sorted Containers
	  * because the Result Set can be decreased dramatically. */
	public void setFilter(final Object Value) { }
	
	/** @return the (minimum) Number of Items left (in the Buffer),
	  * i.e. the minimum Number of times to call nextItem().
	  * The actual Number may be higher, so availAble() should be called again
	  * at the End of this Number.
	  *
	  * Nearly equivalent is currItem != null
	  * (when the Container does not contain null Entries, like e.g. HashTables)
	  *
	  * @see streamIO.Byte.IStreamIn_Byte#available() which returns an int
	  * 	and is therefore written differently!
	  */
	public long availAble() { return -1; }
	
	/**
	 * @see streamIO.IIStreamIn#isValid()
	 */
	public boolean isValid() { return false; }
	
	/** @return the next (Parent) Object of this one.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	  * This is less explicit, but much faster because Exception Handling can be extremely slow.
	  * Alternatively this Method can block until new Data is available,
	  * but this should always have a TimeOut to avoid DeadLocks.
	  */
	public Object nextItem() { return EOI; }
	
	/** single Method always returning EOI,
	  * signifying an empty Input streamIO
	  */
	public int read() { return -1; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Stores all Items available().
	  * This Method is unnecessary, because it corresponds to Store.add(this); */
	//public long storeItems(StreamOut Store);
	
	/** @return and moves to the last (Root) Object of this one.
	  * This should be used with Care, because it could result in Blocking
	  * or infinite Loops with infinite Streams. */
	public Object lastItem() { return null; }

	/** @return the current Object without moving.
	  * This is just a caching Functionality and should be done
	  * at the Client Process, for faster Access.	 */
	public Object currItem() { return null; }
	
	/** Returns the next Item without moving to it.	 */
	public Object peekItem() { //throws NoSuchMethodException { 
		return null; }
	
	/**Returns the Item after the next Item by moving to the next one.
	  * This allows a pre-read with removing the current Item.	 */
	//	public Object nextNextItem();
	
	/** @return true, when the Items returned support the OrderAble Interface
	  * and they are returned in (strictly) ascending or descending Order.
	  * This is used as an additional criterion for Search Operations like findFirst()
	  * It is replaced by the @see Pipe.getOrder() Method: ordered, random, sorted
	  * Monotonous is implicitly sorted!	 */
	//	public boolean isMonotonous();
	
	////////////////////////////////////////////////////////////////////////////////
	//  Navigation, Marking and Resetting a Stream (for re-Processing in Parsing, if supported)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Skips over and discards n Items from this Iterator.
	  * Returns the actual number of Items skipped.	 */
	public long jump(final long n) { return 0; }

	/** Resets the Iterator to the last marked Position. 
	  * By Default the Start of the Iterator is marked on Instantiation. 
	  */
	public IReSetAble reSet() { return this; }

	/** Resets the Iterator to the given Position
	  * counting from the last marked Position.	 */
	public long reSet(long Position) { return 0; }

    /** @see streamIO.IReSetAble#reSet(java.lang.String)   */
    public IReSetAble reSet(final String failureExceptionMessage) { return this; }
    
    /** @see streamIO.IMarkAble#getMaxMarkSize()     */
    public long getMaxMarkSize() { return Long.MAX_VALUE; }
    
	/** Marks the current position in this Iterator.
	  * A subsequent call to the reset method repositions this Iterator
	  * to the last marked position.	 */
	public IMarkAble mark() { return this; }

	/** Marks the current position in this Iterator.
	  * A subsequent call to the reset method repositions this Iterator
	  * at the last marked position.
	  * The readlimit arguments tells this input stream to allow that many Items
	  * to be read before the mark position gets invalidated.
	  * This is to limit the Blocking of System Ressources	 */
	public IMarkAble mark(final long readLimit) { return this; }
	
	/** @return the Object at the given Position in this Enumeration
	  * The Result depends on whether the Iterator is deterministic
	  * and supports these Operations */
	public Object getAt(final int Position) { return null; }

	//  Convenience Array Read Methods

	/** Returns as many Items as are available() currently 	 */
	public Object[] nextItems() { return new Object[0]; }
	
	/** Returns as many Items as possible, but maximum numItems stored in Items from Index Begin 	 */
	public int nextItems(Object[] Items, int numItems, int Begin) { return 0; }

	/** Returns as many Items as possible, but maximum numItems stored in Items 	 */
	public int nextItems(Object[] Items, int numItems) { return 0; }

	/** Returns as many Items as possible, but maximum Items.length in Items 	 */
	public int nextItems(Object[] Items) { return 0; }

	/** Tests, whether this Object exists in the Set,
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences. 	 */
	public Object findNext (Object Item) { return null; }

	/** Tests, whether this Object exists in the Set,
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences. 	 */
	public Object findNext (Object Item, IEquivalence EQ) { return null; }
	
	/** Tests, whether this Object exists in the Set,
	  * @return the first Item found that equals Item, otherwise IStreamIn.EOI
	  * Cannot be used iteratively, because it resets the streamIO. 	 */
	public Object findFirst (Object Item) throws NoSuchMethodException { return null; }
	
	/**Tests, whether this Object exists in the Set,
	 * Returns it, when found, otherwise returns streamIO.Iterator.EOL	 */
	public Object find(final Object Item) { return null; }

	/** Tests, whether this Object[index] exists in the Set,
	  * @param index The Index of the Object interpreted as an Array or StreamIn
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences.
	  * Implementation should allow for both Object[index] and StreamIn.getAt(index)
	  */
	public Object findNext (Object Item, int index) { return null; }

	/** Tests, whether this Object[index] exists in the Set,
	  * @param index The Index of the Object interpreted as an Array or StreamIn
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences.
	  * Implementation should allow for both Object[index] and StreamIn.getAt(index)
	  */
	public Object findNext (Object Item, int index, IEquivalence EQ) { return null; }

	/** Tests, whether this Object exists in the Set,
	  * @return the first Item found that equals Item, otherwise IStreamIn.EOI
	  * @param EQ Equivalence Relation used to test Equality, instead of equals()
	  * Cannot be used iteratively, because it resets the streamIO. 	 */
	public Object findFirst (Object Item, IEquivalence EQ) { return null; }

	/** @return the Object when it is contained in this Container
	  * This is the same Operation as findFirst()
	  */
	//Object containsItem(Object Item); //

	/** @return true when this Object is contained in this or streamIO
	  * This is the same Operation as (findFirst() != EOI) || (available() >= 0)
	  * @see Sub() and SubEq() for the according Container Methods,
	  * The Name contains() is only to be used for single Elements
	  */
	public boolean contains(Object Item) { return false; }

	/**Tests, whether all Objects of streamIO arg exist in this streamIO,
	 * Requires this streamIO to be restartAble.
	 * More restrictive and thus 'cheaper' Searches exist for predictive Iterators:
	 * -finding all Elements of arg in Sequence
	 * -finding all Elements of arg in Sequence with intermittent Objects
	 * Uses the Monotony Criterion (for infinite Streams) of findFirst()
	 * This corresponds to the contains() Method.	 */
	public boolean SubEq (IIStreamIn arg, boolean Sequence) { return false; }

	//Convenience Bulk Tests and Operations

	/**Performs the Operation of the Operator on each Item in the Collection
	 * that equals this Item. The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public int forEachThatEquals(Object Item, IProcessor op) { return 0; }

	/**Returns the first Item of those Item in the Collection that equals this Item,
	 * that also fulfills the Test of the ITester Object.
	 * The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public Object firstOfEachThatEqualsThat(Object Item, ITester Test) throws NoSuchMethodException {
		return null; }

	/**Returns the first Item of those Item in the Collection that equals this Item,
	 * that also fulfills the Test of the ITester Object.
	 * The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public Object nextOfEachThatEqualsThat(Object Item, ITester Test) { return null; }

	/**Tests each Object with the given Test.
	 * Stops on the first Object that returns true.
	 * Returns it, otherwise returns streamIO.Iterator.EOI	 */
	public Object nextThat (ITester tst) { return null; }

	/**Tests each Object with the given Test.
	 * Stops on the first Object that returns true.
	 * Returns it, otherwise returns streamIO.Iterator.EOI	 */
	public Object firstThat (ITester tst) { return null; }

	/**Performs an Operation for each Object in this Iteration.
	 * Returns the Number of Operations performed.	 */
	public int forEach (IProcessor op) { return 0; }

	/** @see streamIO.object.IStreamIn#jump()     */
	public IReSetAble jump() { return null; }
    
	/** 
	 * Jumps a single Position back in this Iterator.
     * equivalent to jump(-1); 
	 * @see streamIO.IReSetAble#pushBack()  
	 * @return this Stream if jumping worked, null otherwise. 
	 */
    public IPushBackAble pushBack() { return null; }
	
}


