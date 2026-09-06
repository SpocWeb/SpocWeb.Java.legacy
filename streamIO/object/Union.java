package streamIO.object;

import streamIO.IAvailAble;
import streamIO.IIStreamIn;
import streamIO.IReSetAble;

/** Streams the concatenated union of a (possibly infinite) sequence of finite input streams,
 * appending each one's elements in turn.
 * <p>
 * This Class creates a Stream with the Elements of the Union
 * of all Input Streams (with a finite Number of Elements)
 * given by an (possibly infinite) Enumerator.
 * I.e. the Sequence is: 
 * s[1][1],s[1][2],...s[1][n],
 * s[2][1],s[2][2],...
 * ...
 * 
 * This can be used to implement the "OR" Operation, 
 * if all Streams are disjoint,
 * otherwise you have to use a Set Container to filter out Duplicates.
 * 
 * For efficient union and intersection operations,
 * it pays to keep the elements in each subset sorted by an intrinsic 1-Dim Metric,
 * so a linear-time traversal through both subsets identifies all duplicates
 * (i.e. AND and OR are O(N+M) Algorithms instead of an (N*M) Algorithms.
 *  Of course sorting is an O(N log N) Problem of its own, but it still pays off!
 *  Only in infinite Sets it is not possible to sort,
 *  unless the streamIO fulfills the Monotony Criterion!)
 * This corresponds to the divide and conquer Method used for sort and merge.
 * 
 * To create an XOR Union you can also use this Union, but instead of just adding the Items,
 * use the flip() Method to add/remove the Items.
 * 
 * This is the Object Analogon of
 * @see streamIO.Byte.LimitedSizeOutputStream
 * Instead of interleaving Data 
 * from a fixed Number of (possibly infinite) Input Streams like in
 * @see DeMultiplexerIn, this appends a finite Number of possibly infinite Input Streams.
 * @see Merger, which merges two sorted Streams into a new sorted streamIO.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:42:48Z
 * digest: 88eda9c4803c7899c9f66d69204d55238b87677afdbeca42add3c022c676bd10
 * stale: false
 * tags: [code/stream_processing, code/iterator]
 * concepts: [Object Stream Pipeline]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public class Union
extends AFilterIn {

	////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////

	/** Returns a streamIO that returns the XOR of the two Streams without Duplicates.
	  * For not disjoint Streams use the Fact that:
	  * A ^ B = A-B + B-A
	  */
	final static public IStreamIn XOR(final IStreamIn A, final IStreamIn B) {
		final IStreamIn[] Parts = new IStreamIn[2];
		Parts[0] = new DIFF(A,B);
		Parts[1] = new DIFF(B,A);
//		Parts[3] = new AND (A,B);
		return new Union(Parts); }

	/** Returns a streamIO that returns the correct Set Union from Streams without Duplicates.
	  * For not disjoint Streams use the Fact that:
	  * A || B = A-B + B-A + B && A
	  */
	final static public IStreamIn OR(final IStreamIn A, final IStreamIn B) {
		final IStreamIn[] Parts = new IStreamIn[3];
		Parts[0] = new DIFF(A,B);
		Parts[1] = new DIFF(B,A);
		Parts[2] = new AND (A,B);
		return new Union(Parts); }

	/**
	 * Compares two streams element by element in sequence order.
	 *
	 * @return true, when both Streams contain the same Elements in the same Sequence.  */
	final static public boolean EQUALS(final IStreamIn A, final IStreamIn B) {
		Object ItemA, ItemB;
		while ((EOI != (ItemA = A.nextItem())) || A.isValid()) {
//			if (!B.isValid()) { return false; }
			ItemB = B.nextItem();  //Using ShoutCut Evaluation here!
			if (EOI == ItemA) { //&&
			if (EOI != ItemB) return false; } else
			if (  ItemA !=     ItemB) //&&
			if (! ItemA.equals(ItemB)) return false;
		}
		if (B.availAble() > 0) return false; //B contains more Elements...
		return true; }

	/** Expensive (n*m) Implementation of the SubSet Relation, an Order Relation:
	  * @return true, when streamIO B contains all Elements of streamIO A.
	  */
	final static public boolean SUB_EQ(final IIStreamIn A, IStreamIn B) {
		Object ItemA; B = (IStreamIn) B.Iterator(); //create a Copy not to disturb other Processes
		while ((EOI != (ItemA = A.nextItem())) || A.isValid()) { B.reSet();
			if (EOI == FIND_NEXT(B,  ItemA)) //when not found...
				return false; } //not equal... (except for nulls, these are not comparable!)
		return true; }
/*			while (true) {
				if((EOI     ==  (ItemB = B.nextItem())) && !B.isValid()) { return false; }
				if (ItemA   ==   ItemB) break;
				if (EOI     ==   ItemA) return false;
				if (ItemA.equals(ItemB))break;
			}
		} return true; } //B contains more Elements...

	/** Expensive (n*m) Implementation of the equals Relation ignoring Sequence.
	  * For ordered Streams better use equals().
	  * @return true, when Stream A and Stream A contain the same Elements.
	  */
	/**
	 * Compares two streams as unordered sets, checking each is a subset of the other.
	 *
	 * @return true when {@code A} and {@code B} contain the same elements, ignoring order
	 */
	final static public boolean EQUAL_IGNORING_SEQUENCE(final IStreamIn A, final IStreamIn B) {
		return SUB_EQ(A, B) && SUB_EQ(B, A); }
	
	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the streamIO of Input Streams */
	protected final IIStreamIn Streams;
	
	/** Flag to reset each streamIO of the Input Streams */
	protected boolean reset;
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructor
	////////////////////////////////////////////////////////////////////////////
	
	/** Creates new Union from an Enumeration of Streams */
	public Union (final IIStreamIn Streams) {
		super((IIStreamIn)Streams.nextItem());
		this.Streams = Streams; }
	
	/** Creates new Union from an Array of Streams */
	public Union (final IIStreamIn[] Streams) {
		this(new ArrayStreamIn(Streams)); } //TODO: remove this Forward Reference (by making the
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns the Number of (currently available) Items,
	  * may return 0 at the End of a constituent streamIO. 	 */
	public long availAble() {
		if (EOI == in) {
			return -1; }
		return ((IAvailAble)in).availAble(); }
	
	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public IReSetAble reSet() { //throws NoSuchMethodException { //not well defined! Reset Streams, reset each single Stream (could be infinitely many!), again reset Streams
//		super  .reset(); //((StreamIn) Enum).reset(); currItem = SOI;
		((IStreamIn) Streams).reSet(); //
		reset = true;
		in    = null; 
		return  this; } //better set a Flag to reset each single Stream on starting.

	/**Returns the next Object (Parent) of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 */
	protected Object nextItemInternal() {
		while(true) { //try it until it reaches a full loop!
			if  (EOI !=  in)
			if ((EOI != (currItem = in.nextItem())) || in.isValid()) {
				return   currItem; }
			if ((EOI == (in = (IIStreamIn) Streams.nextItem())) && !Streams.isValid()) {
				return null; }
			if (reset) ((IStreamIn) in).reSet();
		} 
	}

}
