package streamIO.object;

import java.util.Comparator;

import streamIO.IAvailAble;
import streamIO.IReSetAble;
import streamIO.IIStreamIn;
import streamIO.Log;
import streamIO.integer.StreamIn_Arithmetic;
import streamIO.integer.filter.FilterIn_Int2Object;
import streamIO.object.filterInOut.FilterByFunction;
import function.IIOrderAble;

/** Merges two similarly sorted Streams
  * into a new sorted streamIO.
  * Merging is a commutative and associative Operation because of the intrinsic Order
  * of the Elements, and is possible when ALL Streams have the same Order.
  * By nesting the Merge you can merge any finite Number of Streams
  * because the Frequency of Elements is determined intrinsically.
  *
  * An optimum Merge can be achieved both iteratively or recursively:
  * Recursively:
  * merge A,B into C multiplexing 2 Streams A,B
  * merge X,Y into Z multiplexing 2 Streams X,Y
  * merge C,Z into O multiplexing 4 Streams
  * merge D,E into F multiplexing 2 Streams D,E
  * merge U,V into W multiplexing 2 Streams U,V
  * merge F,W into P multiplexing 4 Streams
  * merge O,P into Q multiplexing 8 Streams
  *
  * Iteratively:
  * merge A,B into C multiplexing 2 Streams A,B
  * merge C,D into F multiplexing 3 Streams D
  * merge F,E into G multiplexing 4 Streams E
  * merge G,X into H multiplexing 5 Streams X
  * merge H,Y into I multiplexing 6 Streams Y
  * merge I,U into K multiplexing 7 Streams U
  * merge K,V into L multiplexing 8 Streams V
  *
  *
  * Zur Ermittlung des Maximums:
  * zwei Elemente direkt vergleichen
  * und das  größere mit dem momentanen Maximum
  * und das kleinere mit dem momentanen Minimum
  * TODO: irgendwo ist ein optimaler Algorithmus in Pascal (vielleicht auch Java?)!
  *
  * Instead of appending an (infinite) Number of finite Input Streams like in
  * @see Union
  * or interleaving a finite Number (an Array) of (possibly infinite) Input Streams like in
  * @see Multiplexer which cannot be done recursively by (de-) multiplexing two Streams
  * except for a binary Powers of streamIO Numbers
  * or by accepting a mixed Frequency of Elements.
  *
  * Created on 26. Mai 2001, 22:08
  *
  * @author  Matthias Heuer
  * @version
  */
public class Merger
extends AFilterIn {
	
	private static final Log L = new Log(Merger.class, 0); 
	
	////////////////////////////////////////////////////////////////////////////
	//  Members
	////////////////////////////////////////////////////////////////////////////

	/** Reference to the second Input streamIO */
	protected IStreamIn in2;

	/** Reference to the current Item of the first  Input streamIO */
	protected Object item1; // = null; //not necessary

	/** Reference to the current Item of the second Input streamIO */
	protected Object item2; // = null; //not necessary

	/** The Comparator being used to compare Elements.
	  * If 'null', the Elements are assumed to implement
	  * @see IScalarMetric or
	  * @see Comparable  or
	  * @see IIOrderAble	 */
	protected Comparator mComparator;

	/** The Sort order of the Array	 */
	protected boolean ascending = true;

	////////////////////////////////////////////////////////////////////////////
	//  Constructor
	////////////////////////////////////////////////////////////////////////////

	/** Creates new Union */
	public Merger(IIStreamIn Stream1, IStreamIn Stream2) {
		super  (Stream1); //create a Copy not to disturb other Processes
		this.in2 =(IStreamIn) Stream2.Iterator(); //create a distinct new Iterator to operate independently!
	}

	////////////////////////////////////////////////////////////////////////////
	//  Delegation
	////////////////////////////////////////////////////////////////////////////

	/** Returns the Number of (currently available) Items,
	  * which is the Sum of Elements available from both Streams. 	 */
	public long availAble() { //truncate both Results to -1!
		long ret1; if ((ret1 = ((IAvailAble)in).availAble()) < -1) ret1 = -1;
		long ret2; if ((ret2 =             in2.availAble()) < -1) ret2 = -1;
		return ret1 + ret2; } //

	/**Returns the next Object (Parent) of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 */
	protected Object nextItemInternal() { //get valid Elements from both Streams and compare them
		while (item1 == null) { if ((EOI != (item1 = in .nextItem ())) || !in .isValid()) break; } //Start Sequence...
		while (item2 == null) { if ((EOI != (item2 = in2.nextItem ())) || !in2.isValid()) break; } //this allows an autoStart!
		if    (item1 == null) { currItem = item2; item2 = null; return currItem; } //End Sequence...
		if    (item2 == null) { currItem = item1; item1 = null; return currItem; } //no more Items found...
		if (mComparator != null) {
			if (ascending == (mComparator.compare(item1, item2) > 0)) {
				currItem = item2; item2 = null; return currItem; }
				currItem = item1; item1 = null; return currItem;
		} else if(item1 instanceof Comparable) {
			if (ascending == (((Comparable) item1).compareTo(item2) > 0)) {
				currItem = item2; item2 = null; return currItem; }
				currItem = item1; item1 = null; return currItem;
		} else if(item1 instanceof IIOrderAble) {
			if (ascending == (((IIOrderAble) item2).isLessThan(item1))) {
				currItem = item2; item2 = null; return currItem; }
				currItem = item1; item1 = null; return currItem;
		} else { //no criterion, flip between both Streams
			if (ascending = !ascending) {
				currItem = item2; item2 = null; return currItem; }
				currItem = item1; item1 = null; return currItem;
		}
	}

	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public long reSet(long Position) { //throws NoSuchMethodException {
		reSet (); return jump(Position); }

	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public IReSetAble reSet() { //throws NoSuchMethodException {
		super.reSet(); //((StreamIn) Enum).reset(); currItem = SOI;
		in2.reSet(); //curr.Key = Enum.nextItem();
		item1 = item2 = null;
		return this; }

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt() throws java.io.IOException {
		L.n("Testing");
		final IStreamIn NumbStream = new FilterIn_Int2Object(new StreamIn_Arithmetic(-2, 20));
		final IStreamIn even = new FilterByFunction(new FilterIn_Int2Object(new StreamIn_Arithmetic(0, 10)), new DblAt());
		final Merger mrg = new Merger(NumbStream, even);
		L.n().l(mrg); //takes very long otherwise!
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(); }

}
