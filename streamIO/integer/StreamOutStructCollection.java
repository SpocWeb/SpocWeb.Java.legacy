/*
 * Created on 18.04.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

/**
 * Title: <p>
 * Description: unimplemented Skeleton - every Method below throws
 * UnsupportedOperationException; subclass this Class and override them before use.
 * It is intended to make the Contents of a parsed IStreamIn_Struct available
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

	/** Message used by every unimplemented Method of this Skeleton. */
	private static final String STR_NOT_IMPLEMENTED =
		"StreamOutStructCollection is an unimplemented Skeleton; subclass it and override this Method.";

	///////////////////////////////////////////////////////////////////////////
	///
	///////////////////////////////////////////////////////////////////////////

	/** Constructs this Writer on top of the given byte Stream.
	 * @param _stream the byte Stream backing this Writer
	 */
	public StreamOutStructCollection(final IStreamOutByte _stream) {
		super(_stream);
	}
	
	///////////////////////////////////////////////////////////////////////////
	///
	///////////////////////////////////////////////////////////////////////////
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStruct#peek_Struct()	 */
	public String peek_Struct() {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStruct#open_Struct(java.lang.String)	 */
	public IStreamOutStruct open_Struct(String name) {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStruct#closeStruct(java.lang.String)	 */
	public IStreamOutStruct closeStruct(String name) {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStruct#closeStruct()	 */
	public IStreamOutStruct closeStruct() {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStruct#closeAll()	 */
	public void closeAll() {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStruct#open_Struct()	 */
	public IStreamOutStruct open_Struct() {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStruct#writeName(java.lang.String)	 */
	public IStreamOutStruct writeName(String name) {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStruct#writeNameValuePair(java.lang.String, java.lang.String)	 */
	public IStreamOutStruct writeNameValuePair(String name, String value) {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStruct#writeNameValuePair(java.lang.String, java.lang.String, boolean)	 */
	public IStreamOutStruct writeNameValuePair(String name, String value,
			boolean useQuotes) {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStruct#open_Quote()	 */
	public IStreamOutStruct open_Quote() {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStruct#closeQuote()	 */
	public IStreamOutStruct closeQuote() {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStructArrays#addShorts(short[], int, int)	 */
	public IStreamOutStruct addShorts(short[] values, int stop, int start) {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStructArrays#addInts(int[], int, int)	 */
	public IStreamOutStruct addInts(int[] values, int stop, int start) {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStructArrays#addLongs(long[], int, int)	 */
	public IStreamOutStruct addLongs(long[] values, int stop, int start) {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStructArrays#addFloats(float[], int, int)	 */
	public IStreamOutStruct addFloats(float[] values, int stop, int start) {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStructArrays#addDoubles(double[], int, int)	 */
	public IStreamOutStruct addDoubles(double[] values, int stop, int start) {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStructArrays#addStrings(java.lang.String[], int, int)	 */
	public IStreamOutStruct addStrings(String[] values, int stop, int start) {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	/** Not implemented: always throws UnsupportedOperationException.
	 * @see streamIO.integer.IStreamOutStructArrays#addItems(java.lang.Object[], int, int)	 */
	public IStreamOutStruct addItems(Object[] values, int stop, int start) {
		throw new UnsupportedOperationException(STR_NOT_IMPLEMENTED);
	}
	
	///////////////////////////////////////////////////////////////////////////
	///
	///////////////////////////////////////////////////////////////////////////
	
	/** Empty smoke-test entry point; performs no action. */
	public static void main(final String[] args) throws Exception {
		
	}
	
}
