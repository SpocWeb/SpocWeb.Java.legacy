package streamIO.object;

import java.io.IOException;

import streamIO.IMarkAble;
import streamIO.StreamOutPrimitive;
import streamIO.StringBufferOutputStream;
import streamIO.integer.AStreamIn_Int;
import streamIO.integer.IStreamIn_Int;

/**
  * Title: StringStreamIn.java<p>
  * Description:
  * Simple, lightweight read only Iterator for Character Arrays (Strings)
  * Instead of StringStreamOut use a Composition of StringBufferOutputStream
  * and PrintStreamOut.
  * 
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-06, 10;39;48<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class StringStreamIn
extends AStreamIn_Int
implements IStreamIn_Int{
	
	/** @see streamIO.real.IStreamIn_Bound_Float#getMinDouble()	 */
	public double getMinDouble() { return 0; }
	
	/** @see streamIO.IOrdered#getOrder()	 */
	public byte getOrder() { return ORDER_QUEUE; }
	
	/**Returns the next (Parent) Object of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	 * This is less explicit, but much faster because Exception Handling can be extremely slow.
	 * Alternatively this Method can block until new Data is available,
	 * but this should always have a TimeOut to avoid DeadLocks.
	 * @see streamIO.integer.AStreamIn_Int#nextLongInternal()	 
	 */
	protected long nextLongInternal() {
		if (++curr >= getInt()) 
			return EOF;
		return (arr != null) ? arr.charAt(curr) : arrBuf.charAt(curr); 
	}
	
	/** Instead of StringStreamOut
	  * use a Composition of StringBufferOutputStream and PrintStreamOut. */
	public static StreamOutPrimitive StringStreamOut() {
		return new StreamOutPrimitive(new StringBufferOutputStream()); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Current Record	 */
	protected int curr = -1;
	
	/** Local Cache for the Value mark()ed	 */
	protected int markValue = -1;
	
	/** Reference to the String being iterated.	 */
	protected final String arr;
	
	/** Reference to the String being iterated.	 */
	protected final StringBuffer arrBuf;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	public StringStreamIn(final String _arr) { arr = _arr; arrBuf = null; }
	
	/** Empty Constructor	 */
	public StringStreamIn(final StringBuffer _arr) { arrBuf = _arr; arr = null; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**Returns the (minimum) Number of Items left (in the Buffer).
	 * The actual Number may be higher, so available() should be called again
	 * at the End of this Number.
	 *
	 * Nearly equivalent is currItem != null
	 * (when the Container does not contain null Entries, like e.g. HashTables)
	 */
	public long availAble() {  return getInt() - curr - 1; }
	
	public long getInt() { return (arr != null) ? arr.length() : arrBuf.length(); }
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return getInt(); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	//Marking and Resetting a Stream (for re-Processing, if supported)
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return curr; }
	
	/**Skips over and discards n Items from this Iterator.
	 * Returns the actual number of bytes skipped.
	 * This dumb Implementation just reads all Elements and discards them.	 */
	public long jump(final long Position) { curr += Position; return Position; }
	
	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public long reSet(final long Position) { //throws    NoSuchMethodException {
		curr = (int) (markValue + Position); return Position; }
	
	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources	 */
	public IMarkAble mark(final long ReadLimit) { //throws    NoSuchMethodException {
		markValue = curr; return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	//	Interface Object
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return  a string representation of the object.
	  * In general, the toString method returns a string that "textually represents" this object.
	  * The result should be a concise but informative representation that is easy for a person to read.
	  * It is recommended that all subclasses override this method.
	  */
	public String toString() { return (arr != null) ? arr : arrBuf.toString(); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws IOException {
		System.out.println("Testing " + StringStreamIn.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws IOException {
		testIt(args); }
	
}
