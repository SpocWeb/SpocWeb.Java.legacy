/*
 * Created on 29.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import java.io.IOException;
import java.util.HashMap;

import streamIO.IIStreamOut;
import tools.IOError;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Abstract Base Class of all structured Stream Writers 
 * 
 * Design Decisions / Implementation Details:
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
abstract public class AStreamOutStruct 
extends StreamOutPrimitive
implements IStreamOutStruct {
	
	/** Pretty Printing or possibly Separator 	 */
	public static final char CHR_TAB = '\t';
	
	/** Pretty Printing and Separator 	 */
	public static final char CHR_ROW = '\n';
	
	/** Pretty Printing Separator 	 */
	//public static String STR_CRLF = "\r\n";
	
	/** Row Separator Character for this Writer	 */
	public char chrRow = CHR_ROW; 
	
	/** Formatting White Space Character for this Writer
	 * Set to CHR_IGNORE to disable indenting	 */
	public char chrTab = CHR_TAB; 
	
	/** Creates a structured writer on top of the given byte stream.
	 * @param _stream the underlying byte output stream to write to
	 */
	public AStreamOutStruct(final IStreamOutByte _stream) { super(_stream); }
	
	/** @see streamIO.integer.IStreamOutStruct#getStream()	 */
	//public IStreamOutPrimitive getStreamOutPrimitive() { return this; }
	
	///////////////////////////////////////////////////////////////////////////
	/// Abstract Methods
	///////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////
	/// Implementations: Indenting for Structures 
	///////////////////////////////////////////////////////////////////////////
	
	/** Current Position in the Structure Stack	*/
	protected int	SP;
	
	/**
	 * @throws IOException
	 */
	protected void indent(final int delta) throws IOException {
		//AStreamOutByte.WRITE(streamByte, STR_CRLF); //pretty printing with CR/LF
		if (chrRow != CHR_IGNORE)
			streamByte.write(chrRow);
		if (chrTab != CHR_IGNORE)
			for(int i = SP; (i-=delta) >= 0;)
				streamByte.write(chrTab); 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  IStreamOutArrays Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** used for anonymous Lists	 */
	public static final String STR_LIST = "list"; 
	
	/** Structure/type-tag name used for {@code short} array elements. */
	public static final String STR_SHORT = "short";

	/** Writes the whole short array in structured form.
	 * @see streamIO.integer.IStreamOutStructArrays#addShorts(short[]) */
	public IStreamOutStruct addShorts(final short[] values) {
		return addShorts(values, values.length); }

	/** Writes the leading range [0, stop) of the short array in structured form.
	 * @see streamIO.integer.IStreamOutStructArrays#addShorts(short[], int) */
	public IStreamOutStruct addShorts(final short[] values, final int stop) {
		return addShorts(values, stop, 0); }

	/** Structure/type-tag name used for {@code int} array elements. */
	public static final String STR_INT = "int";

	/** Writes the whole int array in structured form.
	 * @see streamIO.integer.IStreamOutStructArrays#addInts(int[])	 */
	public IStreamOutStruct addInts(final int[] values) {
		return addInts(values, values.length); }

	/** Writes the leading range [0, stop) of the int array in structured form.
	 * @see streamIO.integer.IStreamOutStructArrays#addInts(int[], int) */
	public IStreamOutStruct addInts(final int[] values, final int stop) {
		return addInts(values, stop, 0); }

	/** Structure/type-tag name used for {@code long} array elements. */
	public static final String STR_LONG = "long";

	/** Writes the whole long array in structured form.
	 * @see streamIO.integer.IStreamOutStructArrays#addLongs(long[]) */
	public IStreamOutStruct addLongs(final long[] values) {
		return addLongs(values, values.length); }

	/** Writes the leading range [0, stop) of the long array in structured form.
	 * @see streamIO.integer.IStreamOutStructArrays#addLongs(long[], int) */
	public IStreamOutStruct addLongs(final long[] values, final int stop) {
		return addLongs(values, stop, 0); }

	/** Structure/type-tag name used for {@code float} array elements. */
	public static final String STR_FLOAT = "float";

	/** Writes the whole float array in structured form.
	 * @see streamIO.integer.IStreamOutStructArrays#addFloats(float[]) */
	public IStreamOutStruct addFloats(final float[] values) {
		return addFloats(values, values.length); }

	/** Writes the leading range [0, stop) of the float array in structured form.
	 * @see streamIO.integer.IStreamOutStructArrays#addFloats(float[], int) */
	public IStreamOutStruct addFloats(final float[] values, final int stop) {
		return addFloats(values, stop, 0); }

	/** Structure/type-tag name used for {@code double} array elements. */
	public static final String STR_DOUBLE = "double";

	/** Writes the whole double array in structured form.
	 * @see streamIO.integer.IStreamOutStructArrays#addDoubles(double[]) */
	public IStreamOutStruct addDoubles(final double[] values) {
		return addDoubles(values, values.length); }

	/** Writes the leading range [0, stop) of the double array in structured form.
	 * @see streamIO.integer.IStreamOutStructArrays#addDoubles(double[], int) */
	public IStreamOutStruct addDoubles(final double[] values, final int stop) {
		return addDoubles(values, stop, 0); }

	/** Structure/type-tag name used for {@code String} array elements. */
	public static final String STR_STRING = "String";

	/** Writes the whole String array in structured form.
	 * @see streamIO.integer.IStreamOutStructArrays#addStrings(java.lang.String[]) */
	public IStreamOutStruct addStrings(final String[] values) {
		return addStrings(values, values.length); }

	/** Writes the leading range [0, stop) of the String array in structured form.
	 * @see streamIO.integer.IStreamOutStructArrays#addStrings(java.lang.String[], int)	 */
	public IStreamOutStruct addStrings(final String[] values, final int stop) {
		return addItems(values, stop, 0); }

	/** Structure/type-tag name used for generic {@code Object} array elements. */
	public static final String STR_OBJECT = "Object";
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public long addItems(final Object[] values) {
		//super.addItems(values); 
		addItems(values, values.length); 
		return values.length; }
	
	/** Writes the leading range [0, stop) of the Object array in structured form.
	 * @see streamIO.integer.IStreamOutStructArrays#addItems(java.lang.Object[], int)	 */
	public IStreamOutStruct addItems(final Object[] values, final int stop) {
		return addItems(values, stop, 0); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  IStreamOut Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Name for Items added to the current Structure	 */
	public final String itemName = "item"; 
	
	/** Writes a self-serializing Item by delegating to its {@link IStreamWriteAble#writeTo(IStreamOutStruct, String)}.
	 * @see streamIO.IIStreamOut#addItem(java.lang.Object)	 */
	public IIStreamOut addItem(final IStreamWriteAble arg) {
		try { listChr();
			arg.writeTo(this, itemName);
		} catch(final IOException x) {
			throw new IOError(x);
		}
		return this; }

	/** Writes an arbitrary Item: delegates to {@link #addItem(IStreamWriteAble)} when possible,
	 * otherwise writes {@code String.valueOf(arg)}.
	 * @see streamIO.IIStreamOut#addItem(java.lang.Object)	 */
	public IIStreamOut addItem(final Object arg) {
		if (arg instanceof  IStreamWriteAble) 
			return addItem((IStreamWriteAble) arg);
		try { listChr(); //addString(arg.toString());
			WRITE_UNSAFE(this, String.valueOf(arg)); //write(String.valueOf(arg)); //super.addItem(arg); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Object Reference Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** List of Objects already serialized and their IDs */
	protected HashMap alreadyWritten = new HashMap(); 
	
	/** The Counter for the next ID used in referencing serialized Objects 	 */
	protected int currID; 
	
	/** Opens a named structure for {@code obj}: writes a back-reference if {@code obj} was
	 * already serialized, otherwise writes its class/ID and delegates to
	 * {@link IStreamWriteAble#writeTo(IStreamOutStruct)} when applicable.
	 * @see streamIO.integer.IStreamOutStruct#open_Struct(java.lang.String, java.lang.Object)	 */
	public IStreamOutStruct open_Struct(final String name, final Object obj) {
		open_Struct(name);
		final String id = (String) alreadyWritten.get(obj);
		if (id != null) {
			writeNameValuePair(STR_REF_ID, id);
		} else {
			final IStreamWriteAble writable = (obj instanceof IStreamWriteAble) ? ((IStreamWriteAble) obj) : null;
			if (writable == null) {
				addItem(obj); //null or not IStreamWriteAble
				try { listChr(); } catch(IOException x) { throw new IOError(x); }
			}
			if (obj != null)
				writeNameValuePair(STR_CLASS, obj.getClass().getName());
			if (obj != null) {
				final String newId = Integer.toString(++currID);
				// TODO: LOGIC: key/value are reversed here - alreadyWritten is read as
				// alreadyWritten.get(obj) above (obj -> id), but this inserts (newId -> obj)
				// instead of (obj -> newId). As written, a previously-seen obj will never be
				// found again by get(obj), so back-references (STR_REF_ID) are never emitted
				// and circular object graphs will recurse indefinitely / stack-overflow.
				alreadyWritten.put(newId, obj);
				writeNameValuePair(STR_OBJ_ID, newId);
			}
			if (writable != null) {
				writable.writeTo(this); //
				return this; //to break Recursion!
			}
		}
		return null; //nothing to add to this Structure
	}

	/** Opens the named structure for {@code obj}, writes its content, and closes it.
	 * @see IStreamOutStruct#writeStruct(String, Object)	 */
	public IStreamOutStruct writeStruct(final String name, final Object obj) {
		open_Struct(name, obj); 
		closeStruct(); 
		return this; }
	
}
