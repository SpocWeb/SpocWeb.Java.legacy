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
 */
public class StreamOutStructCollection 
extends AStreamOutStruct {
	
	///////////////////////////////////////////////////////////////////////////
	///
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param _stream
	 */
	public StreamOutStructCollection(final IStreamOutByte _stream) {
		super(null); //_stream);
	}
	
	///////////////////////////////////////////////////////////////////////////
	///
	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamOutStruct#peek_Struct()	 */
	public String peek_Struct() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStruct#open_Struct(java.lang.String)	 */
	public IStreamOutStruct open_Struct(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStruct#closeStruct(java.lang.String)	 */
	public IStreamOutStruct closeStruct(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStruct#closeStruct()	 */
	public IStreamOutStruct closeStruct() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStruct#closeAll()	 */
	public void closeAll() {
		// TODO Auto-generated method stub

	}
	
	/** @see streamIO.integer.IStreamOutStruct#open_Struct()	 */
	public IStreamOutStruct open_Struct() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStruct#writeName(java.lang.String)	 */
	public IStreamOutStruct writeName(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStruct#writeNameValuePair(java.lang.String, java.lang.String)	 */
	public IStreamOutStruct writeNameValuePair(String name, String value) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStruct#writeNameValuePair(java.lang.String, java.lang.String, boolean)	 */
	public IStreamOutStruct writeNameValuePair(String name, String value,
			boolean useQuotes) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStruct#open_Quote()	 */
	public IStreamOutStruct open_Quote() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStruct#closeQuote()	 */
	public IStreamOutStruct closeQuote() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStructArrays#addShorts(short[], int, int)	 */
	public IStreamOutStruct addShorts(short[] values, int stop, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStructArrays#addInts(int[], int, int)	 */
	public IStreamOutStruct addInts(int[] values, int stop, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStructArrays#addLongs(long[], int, int)	 */
	public IStreamOutStruct addLongs(long[] values, int stop, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStructArrays#addFloats(float[], int, int)	 */
	public IStreamOutStruct addFloats(float[] values, int stop, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStructArrays#addDoubles(double[], int, int)	 */
	public IStreamOutStruct addDoubles(double[] values, int stop, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStructArrays#addStrings(java.lang.String[], int, int)	 */
	public IStreamOutStruct addStrings(String[] values, int stop, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** @see streamIO.integer.IStreamOutStructArrays#addItems(java.lang.Object[], int, int)	 */
	public IStreamOutStruct addItems(Object[] values, int stop, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	///////////////////////////////////////////////////////////////////////////
	///
	///////////////////////////////////////////////////////////////////////////
	
	public static void main(final String[] args) throws Exception {
		
	}
	
}
