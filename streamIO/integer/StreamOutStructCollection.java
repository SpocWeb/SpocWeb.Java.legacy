/*
 * Created on 18.04.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

/**
 * Title: <p>
 * Description:
 * Makes the Contents of a parsed IStreamIn_Struct available 
 * in a Random-Access Manner. 
 * This is important for Constructors, 
 * that cannot be externally driven 
 * but still need to accomodate for varying Sequences of Elements.  
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
 * concepts: [Primitive and Structured Stream I/O Core Abstractions]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class StreamOutStructCollection
extends AStreamOutStruct {

	// TODO: LOGIC: this whole class is an IDE-generated stub - every overridden method below is
	// unimplemented (returns null or does nothing) despite the class Javadoc above describing a
	// real "Random-Access" collection Writer. Any code that actually uses a
	// StreamOutStructCollection instance silently gets no-ops instead of real structured output.

	///////////////////////////////////////////////////////////////////////////
	///
	///////////////////////////////////////////////////////////////////////////

	/** Constructs this Writer; the given Stream is currently discarded (see super(null) below).
	 * @param _stream the byte Stream that should back this Writer
	 */
	public StreamOutStructCollection(final IStreamOutByte _stream) {
		// TODO: LOGIC: the _stream parameter is ignored - super(null) is always passed instead
		// of super(_stream), so this Writer never actually writes to the given Stream.
		super(null); //_stream);
	}
	
	///////////////////////////////////////////////////////////////////////////
	///
	///////////////////////////////////////////////////////////////////////////
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStruct#peek_Struct()	 */
	public String peek_Struct() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStruct#open_Struct(java.lang.String)	 */
	public IStreamOutStruct open_Struct(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStruct#closeStruct(java.lang.String)	 */
	public IStreamOutStruct closeStruct(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStruct#closeStruct()	 */
	public IStreamOutStruct closeStruct() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; no-op.
	 * @see streamIO.integer.IStreamOutStruct#closeAll()	 */
	public void closeAll() {
		// TODO Auto-generated method stub

	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStruct#open_Struct()	 */
	public IStreamOutStruct open_Struct() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStruct#writeName(java.lang.String)	 */
	public IStreamOutStruct writeName(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStruct#writeNameValuePair(java.lang.String, java.lang.String)	 */
	public IStreamOutStruct writeNameValuePair(String name, String value) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStruct#writeNameValuePair(java.lang.String, java.lang.String, boolean)	 */
	public IStreamOutStruct writeNameValuePair(String name, String value,
			boolean useQuotes) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStruct#open_Quote()	 */
	public IStreamOutStruct open_Quote() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStruct#closeQuote()	 */
	public IStreamOutStruct closeQuote() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStructArrays#addShorts(short[], int, int)	 */
	public IStreamOutStruct addShorts(short[] values, int stop, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStructArrays#addInts(int[], int, int)	 */
	public IStreamOutStruct addInts(int[] values, int stop, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStructArrays#addLongs(long[], int, int)	 */
	public IStreamOutStruct addLongs(long[] values, int stop, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStructArrays#addFloats(float[], int, int)	 */
	public IStreamOutStruct addFloats(float[] values, int stop, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStructArrays#addDoubles(double[], int, int)	 */
	public IStreamOutStruct addDoubles(double[] values, int stop, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStructArrays#addStrings(java.lang.String[], int, int)	 */
	public IStreamOutStruct addStrings(String[] values, int stop, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStructArrays#addItems(java.lang.Object[], int, int)	 */
	public IStreamOutStruct addItems(Object[] values, int stop, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	///////////////////////////////////////////////////////////////////////////
	///
	///////////////////////////////////////////////////////////////////////////
	
	/** Empty smoke-test entry point; performs no action. */
	public static void main(final String[] args) throws Exception {
		
	}
	
}
