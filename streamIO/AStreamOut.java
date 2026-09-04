package streamIO;

//import java.lang.reflect.Array;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import streamIO.exception.BaseException;
import streamIO.integer.filter.LimitedSizeOutputStream;
import streamIO.object.IStreamIn;
import streamIO.object.backTrack.BackTracker;
import streamIO.object.enumer.container.AContainer;
import tester.process.StreamProcessor;

/**
 * AStreamOut
 * Abstract Object Output streamIO Class.
 * Static Methods for writing Arrays to a streamIO!
 * Default Implementations for most Methods.
 *
 * Created on 27. Mai 2001, 01:17
 *
 * @author  Matthias Heuer
 * @version
 */
public abstract class AStreamOut
implements IStreamOut {
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOut: abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/** adds this Item to the Store in Place: +=
	 * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public abstract IIStreamOut addItem (final Object arg);
	
	////////////////////////////////////////////////////////////////////////////
	//  static Methods: generic writing of an Array to a Stream via Reflection:
	////////////////////////////////////////////////////////////////////////////
	
	/** Writes the textual Representation of this Array with Elements
	  * of the given Class Type to the Output streamIO.
	  * Especially Arrays are easier to read in this Representation
	  * compared to the Result of XMLOutputStream.ArrayToXML().
	  * This is complementary to the Result returned by the Scanner Class,
	  * which can parse such a streamIO back to this Array structure.
	  * According to the Logic there the most significant Separators come first.
	  * Thus the first Character of the Separator String has to be cut off.
	  */
	final static public void ARRAY_TO_STREAM(PrintStream OS, Object arg, String Separator) {
		ARRAY_TO_STREAM(OS, arg, arg.getClass(), Separator, 0); 
	}

	/** Writes the textual Representation of this Array with Elements
	  * of the given Class Type to the Output streamIO.
	  * Especially Arrays are easier to read in this Representation
	  * compared to the Result of XMLOutputStream.ArrayToXML().
	  * This is complementary to the Result returned by the Scanner Class,
	  * which can parse such a streamIO back to this Array structure.
	  * According to the Logic there the most significant Separators come first.
	  * Thus the first Character of the Separator String has to be cut off.
	  */
	final static public String ARRAY_TO_STRING(Object arg, String Separator) {
		ByteArrayOutputStream OS = new ByteArrayOutputStream();  //
		ARRAY_TO_STREAM(OS, arg, arg.getClass(), Separator, 0);
//		try { XOS.toXML(arg, Name); } //should not happen!
//		catch (IOException e) { throw new IllegalAccessException (e.toString()); }
		return OS.toString(); }

	/** Writes the textual Representation of this Array with Elements
	  * of the given Class Type to the Output streamIO.
	  * Especially Arrays are easier to read in this Representation
	  * compared to the Result of XMLOutputStream.ArrayToXML().
	  * This is complementary to the Result returned by the Scanner Class,
	  * which can parse such a streamIO back to this Array structure.
	  * According to the Logic there the most significant Separators come first.
	  * Thus the first Character of the Separator String has to be cut off.
	  */
	final static public String ARRAY_TO_STRING(final Object arg, final String Separator, final int length) {
		ByteArrayOutputStream OS = new ByteArrayOutputStream();  //
		ARRAY_TO_STREAM(OS, arg, arg.getClass(), Separator, 0, length);
//		try { XOS.toXML(arg, Name); } //should not happen!
//		catch (IOException e) { throw new IllegalAccessException (e.toString()); }
		return OS.toString(); }

	/** Writes the textual Representation of this Array with Elements
	  * of the given Class Type to the Output streamIO.
	  * Especially Arrays are easier to read in this Representation
	  * compared to the Result of XMLOutputStream.ArrayToXML().
	  * This is complementary to the Result returned by the Scanner Class,
	  *  which can parse such a streamIO back to this Array structure.
	  * According to the Logic there the most significant Separators come first.
	  * Thus the first Character of the Separator String has to be cut off.
	  */
	final static public void ARRAY_TO_STREAM(OutputStream OS, Object arg, Class myClass, String Separator, int Depth) {
		ARRAY_TO_STREAM(new PrintStream(OS), arg, myClass, Separator, Depth); }

	/** Writes the textual Representation of this Array with Elements
	  * of the given Class Type to the Output streamIO.
	  * Especially Arrays are easier to read in this Representation
	  * compared to the Result of XMLOutputStream.ArrayToXML().
	  * This is complementary to the Result returned by the Scanner Class,
	  *  which can parse such a streamIO back to this Array structure.
	  * According to the Logic there the most significant Separators come first.
	  * Thus the first Character of the Separator String has to be cut off.
	  */
	final static public void ARRAY_TO_STREAM(final OutputStream OS, final Object arg, final Class myClass, final String Separator, final int Depth, final int length) {
		ARRAY_TO_STREAM(new PrintStream(OS), arg, myClass, Separator, Depth, length); }

	/** Recursively writes the textual Representation of this Array with Elements
	  * of the given Class Type to the Output streamIO.
	  * Especially Arrays are easier to read in this Representation
	  * compared to the Result of XMLOutputStream.ArrayToXML().
	  * This is complementary to the Result returned by the Scanner Class,
	  * which can parse such a streamIO back to this Array structure.
	  * According to the Logic there the most significant Separators come first.
	  * Thus the first Character of the Separator String has to be cut off.
	  */
	final static public void COLLECTION_TO_STREAM(PrintStream OS, Collection arg, String Separator) {
		Iterator iter = arg.iterator();
		while (iter.hasNext()) {
			OS.print(iter.next());
			OS.print(Separator);
		}
	}

	/** Recursively writes the textual Representation of this Array with Elements
	  * of the given Class Type to the Output streamIO.
	  * Especially Arrays are easier to read in this Representation
	  * compared to the Result of XMLOutputStream.ArrayToXML().
	  * This is complementary to the Result returned by the Scanner Class,
	  * which can parse such a streamIO back to this Array structure.
	  * According to the Logic there the most significant Separators come first.
	  * Thus the first Character of the Separator String has to be cut off.
	  */
	final static public void ARRAY_TO_STREAM(final PrintStream OS, final Object arg, final Class myClass, final String Separator, final int Depth) {
		final int Length = java.lang.reflect.Array.getLength(arg);
		ARRAY_TO_STREAM(OS, arg, myClass, Separator, Depth, Length);
	}

	/** Recursively writes the textual Representation of this Array with Elements
	  * of the given Class Type to the Output streamIO.
	  * Especially Arrays are easier to read in this Representation
	  * compared to the Result of XMLOutputStream.ArrayToXML().
	  * This is complementary to the Result returned by the Scanner Class,
	  * which can parse such a streamIO back to this Array structure.
	  * According to the Logic there the most significant Separators come first.
	  * Thus the first Character of the Separator String has to be cut off.
	  */
	final static public void ARRAY_TO_STREAM(final PrintStream OS, final Object arg, final Class myClass, final String Separator, int Depth, final int Length) {
		if (!myClass.isArray()) {
			throw new AbstractMethodError(); }
		char Sep = 0; if (Separator != null) {
			Sep = Separator.charAt(Depth);  }
		++Depth;
		Class CmpType = myClass.getComponentType();
//		OS.println(Length); //should rather be an Attribute than an Element.
		int i = -1;
		if (CmpType.isArray()) { //Optimization: since all Elements in an Array are similar
			while (++i < Length) { //print the Elements in correct Order!
				ARRAY_TO_STREAM(OS, Array.get(arg, i), CmpType, Separator, Depth);
				if (Separator != null) {
					OS.print(Sep); }
			}
		} else { //if (! CmpType.isPrimitive())
			while (++i < Length) { //print the Elements in correct Order!
				OS.print(Array.get(arg, i).toString());
				if (Separator != null) {
					OS.print(Sep); }
			}
		}
	} //

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns the Number of Items in this Input streamIO 	*/
	final static public long GET_NUM_ITEMS(IIStreamIn Iter) {
		int i = 0;
		while ((IStreamIn.EOI != Iter.nextItem()) || Iter.isValid()) {
			++i; }
		return i; }

	/** Recursively adds these Items to the Store in Place: +=
	  * The Type of Item is analyzed, i.e. Containers Contents is added recursively,
	  * up to the given flatDepth.
	  */
	final static public long ADD_ITEMS(final IStreamOut out, final Object arg, final int flatDepth) { //
		return ADD_ITEMS(out, arg, flatDepth, false); 
	}

	/** Recursively adds these Items to the Store in Place: +=
	  * The Type of Item is analyzed, i.e. Containers Contents is added recursively,
	  * up to the given flatDepth.
	  */
	final static public long ADD_ITEMS(final IStreamOut _out, final Object arg, int flatDepth, final boolean addDescription) { //Check first is a small Optimization ...
		if ((--flatDepth < 0) || (arg == null)) {
			_out.addItem(arg); return 1; }
//		if (arg instanceof IStreamIn) return Out.add((IStreamIn) arg); //and allows for flatDepth = 0
		final Class cls = arg.getClass();
		if (! cls.isArray()) {
			_out.addItem(arg); return 1; } //and allows for flatDepth = 0
		int i   = -1;
		int len = java.lang.reflect.Array.getLength(arg);
		final boolean isArray = cls.getComponentType().isArray(); 
		if (! cls.getComponentType().isPrimitive() &&
			! isArray) {
			return _out.addItems((Object[]) arg); }
/*		if (! cls.getComponentType().isArray()) { //Array Types
//			while (--i >= 0  ) { //reverses the Order though!!!
			while (++i <  len) { //reverses the Order though!!!
			}
			return Out; }
*/		try { //primitive Types or Array...
			if (addDescription) {
				_out.addItem(cls.getComponentType()+"[" + len + "]"); }
//			while (--i >= 0  ) { //reverses the Order though!!!
			while (++i <  len) { //reverses the Order though!!!
				if (flatDepth > 0) {
					if (isArray) _out.addItem("\n"); 
					_out.addItems(java.lang.reflect.Array.get(arg, i), flatDepth); 
				} else {
					_out.addItem (java.lang.reflect.Array.get(arg, i));
				}
			}
		} catch (IllegalArgumentException       x) { throw new BaseException(x);
		} catch (ArrayIndexOutOfBoundsException x) { throw new BaseException(x);
		} return len; }

	/** adds these Items to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * Could also use an Instance of ArrayStreamIn, but that would be Overhead!
	  */
	final static public long ADD_ITEMS(final IIStreamOut Out, final Object[] arg) {
		return ADD_ITEMS(Out, arg, false); }

	/** adds these Items to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * Could also use an Instance of ArrayStreamIn, but that would be Overhead!
	  */
	final static public long ADD_ITEMS(final IIStreamOut Out, final Object[] arg, final boolean addDescription) {
		if (addDescription) {
			Out.addItem("Object[" + arg.length + "]"); }
		int i = -1; //arg.length;
//		while (--i >= 0) //reverses the Order though!!!
		while (++i < arg.length) //
			Out.addItem(arg[i]);
		return arg.length; }

	/** streams all Items from the Enumerator to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * @see BackTracker.operate
	  * @see ProcessorRunner.run
	  * @see StreamProcessor.run
	  * @see LimitedSizeOutputStream.stream for Bytes
	  * @see AContainer.copyAt for creating Copies of the Items
	  * @see AStreamOut.stream for fast streaming
	  * @see AStreamOut.add for flattening
	  */
	final static public long STREAM(final IIStreamIn Iter, final IIStreamOut Out) {
		return STREAM(Iter, Out, Integer.MAX_VALUE, false, false, null, Long.MAX_VALUE); }
//	public static IStreamOut add (IStreamOut Out, IStreamIn Iter) { }

	/** Recursively streams all Items from the Enumerator to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * @see BackTracker.operate
	  * @see ProcessorRunner.run
	  * @see StreamProcessor.run
	  * @see LimitedSizeOutputStream.stream for Bytes
	  * @see AContainer.copyAt for creating Copies of the Items
	  * @see AStreamOut.stream for fast streaming
	  * @see AStreamOut.add for flattening
	  */
	final static public long STREAM(final IIStreamIn iter, final IIStreamOut out, final int depth) {
		return STREAM(iter, out, depth, false, false, null, Long.MAX_VALUE); }
	//public static IStreamOut add (IStreamOut Out, IStreamIn Iter) { }
	
	/** Recursively streams all Items from the Enumerator to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * @see BackTracker.operate
	  * @see ProcessorRunner.run
	  * @see StreamProcessor.run
	  * @see LimitedSizeOutputStream.stream for Bytes
	  * @see AContainer.copyAt for creating Copies of the Items
	  * @see AStreamOut.stream for fast streaming
	  * @see AStreamOut.add for flattening
	  */
	final static public long STREAM(final IIStreamIn iter, final IIStreamOut out, final long numItems) {
		return STREAM(iter, out, Integer.MAX_VALUE, false, false, null, numItems); }
	
	/** streams all Items from the Enumerator to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  *
	  * Transfers the whole Content of the given Input streamIO to the Output streamIO.
	  * Recursion is necessary, because both Streams should not have to know their Parents.
	  * In fact they could even be shared by different Parents in a Diamond Shape.
	  * The Depth makes it clear that it is possible to do shallow Copies
	  * and Copies up to a certain Depth.
	  * If the Objects are providing StreamIn Instances or are themselves
	  * Instances of StreamIn, handing them over ByRef
	  * allows to transfer later Changes via the Object Reference
	  * but also the Danger of encountering mysterious Changes and Side Effects!
	  *
	  * @see BackTracker.operate
	  * @see ProcessorRunner.run
	  * @see StreamProcessor.run
	  * @see LimitedSizeOutputStream.stream for Bytes
	  * @see AContainer.copyAt for creating Copies of the Items
	  * @see AStreamOut.stream for fast streaming
	  * @see AStreamOut.add for flattening
	  */
	final static public long STREAM(final IIStreamIn iter, final IIStreamOut out, int depth, final boolean filterNulls, final boolean singleStep, final Object separator, long numItems) {
		if (--depth < 0) {
			return    0; }
		AReSetAble.TRY_TO_RESET(iter, "");
		long ret = 0;
		while(--numItems >= 0) {
			final Object obj = iter.nextItem();
			if ((IIStreamIn.EOI == obj) && !iter.isValid()) {
				break; }
			if (filterNulls && (obj == null)) {
				++numItems;
				continue; }
			final IIStreamOut NSO = out.addItem(obj); ++ret;
			if (NSO != out) { //Also reacts to the Indicator that Streaming is still necessary
//			if (obj instanceof IStreamIn ) { //equivalent but slower!
				if (depth > 0) { //Optimization to save the Call.
					ret += STREAM((IIStreamIn) obj, NSO, depth, filterNulls, singleStep, separator, Long.MAX_VALUE); }
			}
			if (separator != null) { //This could also be accomplished by @see FilterSeparator
				out.addItem(separator); }
			if (singleStep) {
				break;
			}
		}
		return ret; }

	////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOut: Implementation
	////////////////////////////////////////////////////////////////////////////

	/** adds these Items to the Store in Place: +=
	  * @return the Output streamIO for adding further Items
	  * The Type of Item is analyzed, i.e. Containers Contents is added,
	  * but not recursively, but only flattened by one Level (flatDepth == 1).	  */
	public long addItems(Object arg) { return ADD_ITEMS(this, arg, 1); }

	/** adds these Items to the Store in Place: +=
	  * @return the Output streamIO for adding further Items
	  * The Type of Item is analyzed, i.e. Containers Contents is added recursively,
	  * up to the given flatDepth.	  */
	public long addItems(Object arg, int flatDepth) {
		return ADD_ITEMS(this, arg, flatDepth); }

	/** adds these Items to the Store in Place: +=
	  * @return the Output streamIO for adding further Items
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
	public long addItems(Object[] arg) { return ADD_ITEMS(this, arg); }

	/** adds all Items from the Enumerator to the Store in Place: +=
	  * @return the Output streamIO for adding further Items
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public long addItems(IIStreamIn Iter) { return STREAM(Iter, this); }

	/** @see streamIO.IStreamOut#flush()	 */
	public void flush() throws IOException { }
	
}
