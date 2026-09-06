/*
 * Created on 31.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import java.io.IOException;
import java.lang.reflect.Constructor;

import streamIO.Assert;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.IMarkAble;
import streamIO.IPushBackAble;
import streamIO.IReSetAble;
import streamIO.Log;
import streamIO.diffPatch.VersionTree;
import streamIO.diffPatch.VersionedObjects;
import streamIO.real.IStreamIn_Float;
import streamIO.real.IStreamOutFloat;
import synch.ValidationRule;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Implements IStreamOutStruct as a means to clone Objects 
 * and copy them without Marshaling within the same Address-Space. 
 * To avoid endless Loops for circular Object References, 
 * a HashSet with the ID References of already serialized Objects is needed. 
 * This HashSet is best maintained in the IStreamOutStruct and IStreamIn_Struct Instances.
 * 
 * Design Decisions / Implementation Details:
 * For Concurrency this would need to be Thread-local, 
 * but alternatively using a distinct Stream Instance per Thread would be sufficient.
 * (Using several Threads to write into the same Stream requires Synchronization anyway!)  
 * 
 * Alternatively the (de-)serialized need a (ThreadLocal for Concurrency) ID 
 * that needs to be reset in a second Pass (reset on Call Return is not sufficient!). 
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
public class StreamOutInstantiator 
extends AStreamOutStruct //StreamIn_Struct // 
implements IStreamOutStruct, IStreamIn_Struct {
	
	/** Logger for this Class	 */
	private static final Log L = new Log(StreamOutInstantiator.class, 1); 
	
	/** Does not delegate, it just reuses the Implementations in AStreamOutStruct	 */
	public StreamOutInstantiator() { this((IStreamOutByte) null); }
	
	/** Constructor for the case that the Object is allowed to read the Data itself!  
	 * @param _stream	 */
	public StreamOutInstantiator(final IStreamOutByte _stream) { 
		super(_stream); 
		streamIn = null; }
	
	/** Constructor for the case that the Object is allowed to read the Data itself!  
	 * @param _stream	 */
	public StreamOutInstantiator(final IStreamIn_Struct _stream) { 
		super(null); 
		streamIn = _stream; 
	}
	
	/** Reference to an InputStream to delegate to 
	 * when the Objects are allowed to read their Data. 
	 */
	final IStreamIn_Struct streamIn;
	
	protected String currName; 
	
	protected String structName; 
	
	/** try it with AStreamWriteAble first, to avoid using Reflection	 */
	protected IStreamReadAble currObj; 
	
	///////////////////////////////////////////////////////////////////////////
	
	protected int SP; 
	
	protected IStreamReadAble[] objects = new IStreamReadAble[5]; 
	
	protected String[] names = new String[objects.length]; 
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Returns a String representation of the innermost currently-open structure Object, without closing it.
	 * @see streamIO.integer.IStreamOutStruct#peek_Struct()	 */
	public String peek_Struct() {
		return objects[SP-1].toString(); }
	
	/** Opens (pushes) a nested structure of the given open/close tag onto the structure Stack.
	 * @see streamIO.integer.IStreamOutStruct#open_Struct(java.lang.String)	 */
	public IStreamOutStruct open_Struct(final String openClose) {
		L.enter().l(openClose);
		item = null; 
		writeName(null); //close the last Name-Value Pair
		if((item != null) && (streamIn != null))//if the Object read the Data...
			return this; //...don't put it onto the Stack!
		names  [SP] = structName; structName = openClose; //trigger only AFTER all Fields are read! 
		objects[SP] = currObj; //currObj = null; //this Assignment is left to Instantiation
		if (++SP >= objects.length) { //increase Stack Space
			final String[] tmpStr = new String[names.length+names.length]; 
			final IStreamReadAble[] tmp = new IStreamReadAble[objects.length+objects.length]; 
			System.arraycopy(names  , 0, tmpStr, 0,   names.length); 
			System.arraycopy(objects, 0, tmp   , 0, objects.length); 
			names   = tmpStr; 
			objects = tmp; 
		}
		return this; }
	
	/** Closes (pops) the innermost open structure, handing the parsed Object back to its parent via readField.
	 * @see streamIO.integer.IStreamOutStruct#closeStruct()	 */
	public IStreamOutStruct closeStruct() {
		L.enter(); 
		writeName(null); 
		if (null != currObj) //when item is set externally! 
			item  = currObj; 
		final IStreamReadAble parent = objects[--SP]; objects[SP] = null; 
		if (parent != null) { //trigger(); 
			parent.readField(structName, this); //read the Field last added 
			item = currObj; currObj = parent; string = null; 
		}
		structName = names[SP]; names[SP] = null; 
		return this; }
	
	/** Closes the innermost open structure, verifying it matches the given name.
	 * @see streamIO.integer.IStreamOutStruct#closeStruct(java.lang.String)	 */
	public IStreamOutStruct closeStruct(final String name) {
		L.enter().l(name); 
		if (!ValidationRule.EQUALS(structName, name)) 
			throw new RuntimeException("Expcected to close '"+structName+"'"); 
		return closeStruct(); }
	
	/** Opens a nested structure using the last written Name as its tag.
	 * @see streamIO.integer.IStreamOutStruct#open_Struct()	 */
	public IStreamOutStruct open_Struct() { 
		L.enter(); //used only on parsing, not on...
		//final String name = currName; currName = null; 
		return open_Struct(currName); } //give the Parent Object a Chance to parse the Contents
	
	///////////////////////////////////////////////////////////////////////////
	/// Interface IStreamOutStruct
	///////////////////////////////////////////////////////////////////////////
	
	/** Closes every structure still open on the Stack.
	 * @see streamIO.integer.IStreamOutStruct#closeAll() */
	public void closeAll() {
		while(SP > 0)
			closeStruct(); 
	}

	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStruct#open_Quote() */
	public IStreamOutStruct open_Quote() {
		// TODO Auto-generated method stub
		return null;
	}

	/** Not implemented; always returns null.
	 * @see streamIO.integer.IStreamOutStruct#closeQuote() */
	public IStreamOutStruct closeQuote() {
		// TODO Auto-generated method stub
		return null;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	protected String string;
	
	/** return the String Representation of the next Value read from the Stream
	 * @return the String Representation of the next Value read from the Stream */
	public String nextString() { 
		if (streamIn != null)
			return streamIn.nextString(); 
		return string; }
	
	/** Returns the next String truncated to the given length.
	 * @see streamIO.integer.IStreamIn_Byte#nextString(int) */
	public String nextString(final int length) {
		return nextString().substring(0, length); }
	
	/** Parameter Constant for String Constructors	 */
	private static final Class[] CLS_STRING = new Class[] { String.class };
	
	/** made a Member Variable to save Garbage Collection	 */
	transient private final Object[] params = new Object[1]; 
	
	protected static final Class[] CLS_ISTREAM_IN_STRUCt = { IStreamIn_Struct.class }; 
	
	protected IStreamOutStruct[] paramThis = { this }; 
	
	/** Handles the given String Value: instantiates/looks up an Object for {@code _class_}/{@code _objId_}/{@code _refId_}
	 * Name-Values, otherwise appends it to the cached current String.
	 * @see streamIO.integer.IStreamOutInt#addInt(int)	 */
	public IStreamOutChar addString(final String _str) {
		L.enter().l(_str); 
		if (IStreamOutStruct.STR_CLASS.equals(currName)) { 
			try { 
				final Class cls = Class.forName(_str); 
				if (IStreamReadAble.class.isAssignableFrom(cls)) {
					try { 
						final Constructor cnst = cls.getConstructor(CLS_ISTREAM_IN_STRUCt); 
						cnst.newInstance(paramThis); //also reads the Date into this Object. 
					} catch(final Exception x) {
						currObj = (IStreamReadAble) cls.newInstance();
					}
				} else {
					currObj = null; //item;
					if (string != null) {
						final Constructor cnst = cls.getConstructor(CLS_STRING);
						params[0] = string; 
						item = cnst.newInstance(params); 
					}
				}
			} catch (final Exception x) {
				throw new RuntimeException(x);
			}
		} else if (IStreamOutStruct.STR_OBJ_ID.equals(currName)) {
			alreadyWritten.put(_str, currObj); 
		} else if (IStreamOutStruct.STR_REF_ID.equals(currName)) {
			currObj = (IStreamReadAble) alreadyWritten.get(_str); 
		} else {
			//strings[0] = _str; //use addString as well as addStrings() 
			if  (string == null)
				 string  = _str; 
			else string += _str; 
		}
		return this; }
	
	/** Writes the Name of the next Name-Value Pair, triggering the previous Field's readField first.
	 * @see streamIO.integer.IStreamOutStruct#writeName(java.lang.String) */
	public IStreamOutStruct writeName(final String name) {
		L.enter().l(name);
		if (currObj  != null) {
			if (currName != null) { //now the previous Item is finished...
				//...trigger the Target to read the Value
				item = currObj.readField(currName, this); //trigger();
				string = null; }
			if ((streamIn != null) && 
				(name     != null)){
				item = null; 
				if (null != (item = currObj.readField(name, this))) { //
					currName = null; //value already read, don't read it next time! 
					return this; }
			}
		} 
		currName = name; 
		return this; }
	
	/** Writes a Name-Value Pair by writing the Name followed by the Value.
	 * @see streamIO.integer.IStreamOutStruct#writeNameValuePair(java.lang.String, java.lang.String)	 */
	public  IStreamOutStruct writeNameValuePair(final String name, final String value) {
		L.enter().l("name=").l(name).s().l("value=").l(value); 
		writeName(name); 
		addString(value); 
		return this; }
	
	/** Writes a Name-Value Pair; the quoting flag is ignored by this in-memory implementation.
	 * @see streamIO.integer.IStreamOutStruct#writeNameValuePair(java.lang.String, java.lang.String, boolean)	 */
	public IStreamOutStruct writeNameValuePair(String name, String value,
			boolean useQuotes) { return writeNameValuePair(name, value); }
	
	////////////////////////////////////////////////////////////////////////////
	//	specific typed Reader Methods for reuse. 
	////////////////////////////////////////////////////////////////////////////
	
	/** Flag whether to clone Arrays (and Objects)	 */
	public boolean clone = true; 
	
	/* An Alternative for Arrays would be not to clone them, 
	 * but to remember the start and stop Indices and return them on readInts().  */ 
	
	/** Delegates to the wrapped Reader's nextBuffer(int), or returns null when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Byte#nextBuffer(int) */
	public StringBuffer nextBuffer(final int length) {
		if (streamIn != null) //
			return streamIn.nextBuffer(length); //read it from the Stream
		return null;
	}
	
	/** Delegates to the wrapped Reader's nextBuffer(int, StringBuffer), or returns null when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Byte#nextBuffer(int, java.lang.StringBuffer) */
	public StringBuffer nextBuffer(final int length, final StringBuffer buffer) {
		if (streamIn != null) //
			return streamIn.nextBuffer(length, buffer); //read it from the Stream
		return null;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	protected long lng; 
	
	/** Caches the given value as the current long value, to be picked up by writeName(String).
	 * @see streamIO.integer.IStreamOutInt#addInt(int)	 */
	public IStreamOutInt addLong(final long value) {
		L.enter().l("value=").l(value); 
		lng = value; return this; }
	
	/** Delegates to the wrapped Reader's nextLong(), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Int#nextLong() */
	public long nextLong() {
		if (streamIn != null) //
			return streamIn.nextLong(); //read it from the Stream
		return 0;
	}
	
	/** Delegates to the wrapped Reader's currLong(), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Int#currLong() */
	public long currLong() {
		if (streamIn != null) //
			return streamIn.currLong(); //read it from the Stream
		return 0;
	}
	
	/** Delegates to the wrapped Reader's peekLong(), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Int#peekLong() */
	public long peekLong() {
		if (streamIn != null) //
			return streamIn.peekLong(); //read it from the Stream
		return 0;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Caches the given value as the current int value, to be picked up by writeName(String).
	 * @see streamIO.integer.IStreamOutInt#addInt(int)	 */
	public IStreamOutInt addInt(final int value) {
		L.enter().l("value=").l(value); 
		lng = value; return this; }
	
	/** Delegates to the wrapped Reader's nextInt(), or returns the cached lng narrowed to int.
	 * @see stringOp.parser.IIStreamIn_Int#nextInt()	 */
	public int nextInt() { 
		if (streamIn != null)
			return (int) (lng = streamIn.nextInt()); 
		return (int) lng; }
	
	/** Delegates to the wrapped Reader's currInt(), or returns the cached lng narrowed to int.
	 * @see streamIO.integer.IStreamIn_Int#currInt() */
	public int currInt() {
		if (streamIn != null) //
			return streamIn.currInt(); //read it from the Stream
		return (int) lng;
	}
	
	/** Delegates to the wrapped Reader's peekInt(), or returns the cached lng narrowed to int.
	 * @see streamIO.integer.IStreamIn_Int#peekInt() */
	public int peekInt() {
		if (streamIn != null) //
			return streamIn.peekInt(); //read it from the Stream
		return (int) lng;
	}
	
	/** Delegates to the wrapped Reader's IntIterator(), or returns this when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Int#IntIterator() */
	public IStreamIn_Int IntIterator() {
		if (streamIn != null) //
			return streamIn.IntIterator(); //read it from the Stream
		return this; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Delegates to the wrapped Reader's nextBool(), or decodes the cached lng (0/1) when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Primitive#nextBool() */
	public boolean nextBool() {
		if (streamIn != null) //
			return streamIn.nextBool(); //read it from the Stream
		if (lng == 0)
			return false;
		if (lng == 1)
			return true; 
		throw new RuntimeException("undefined boolean Value:"+lng); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Caches the given value as the current short value, to be picked up by writeName(String).
	 * @see streamIO.integer.IStreamOutInt#addInt(int)	 */
	public IStreamOutInt addShort(final short value) {
		L.enter().l("value=").l(value); 
		lng = value; return this; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Caches the given value as the current double value, to be picked up by writeName(String).
	 * @see streamIO.integer.IStreamOutInt#addInt(int)	 */
	public IStreamOutFloat addFloat(final float value) {
		L.enter().l("value=").l(value); 
		dbl = value; return this; }
	
	/** Delegates to the wrapped Reader's nextFloat(), or returns the cached dbl narrowed to float.
	 * @see streamIO.real.IStreamIn_Float#nextFloat() */
	public float nextFloat() {
		if (streamIn != null)
			return (float) (dbl = streamIn.nextFloat()); 
		return (float) dbl; }
	
	/** Delegates to the wrapped Reader's currFloat(), or returns the cached dbl narrowed to float.
	 * @see streamIO.real.IStreamIn_Float#currFloat() */
	public float currFloat() {
		if (streamIn != null)
			return (float) (dbl = streamIn.currFloat()); 
		return (float) dbl; }
	
	/** Delegates to the wrapped Reader's peekFloat(), or returns the cached dbl narrowed to float.
	 * @see streamIO.real.IStreamIn_Float#peekFloat() */
	public float peekFloat() {
		if (streamIn != null)
			return (float) (dbl = streamIn.peekFloat()); 
		return (float) dbl; }
	
	/** Delegates to the wrapped Reader's FloatIterator(), or returns this when reading in-memory.
	 * @see streamIO.real.IStreamIn_Float#FloatIterator() */
	public IStreamIn_Float FloatIterator() {
		if (streamIn != null) //
			return streamIn.FloatIterator(); //read it from the Stream
		return this; }
	
	///////////////////////////////////////////////////////////////////////////
	
	protected double dbl; 
	
	/** Caches the given value as the current double value, to be picked up by writeName(String).
	 * @see streamIO.integer.IStreamOutInt#addInt(int)	 */
	public IStreamOutFloat addDouble(final double value) {
		L.enter().l("value=").l(value); 
		dbl = value; return this; }
	
	/** Delegates to the wrapped Reader's nextDouble(), or returns the cached dbl.
	 * @see streamIO.real.IStreamIn_Float#nextDouble() */
	public double nextDouble() {
		if (streamIn != null)
			return dbl = streamIn.nextDouble(); 
		return dbl; }
	
	/** Delegates to the wrapped Reader's currDouble(), or returns the cached dbl.
	 * @see streamIO.real.IStreamIn_Float#currDouble() */
	public double currDouble() {
		if (streamIn != null)
			return dbl = streamIn.currDouble(); 
		return dbl; }
	
	/** Delegates to the wrapped Reader's peekDouble(), or returns the cached dbl.
	 * @see streamIO.real.IStreamIn_Float#peekDouble() */
	public double peekDouble() {
		if (streamIn != null)
			return dbl = streamIn.peekDouble();
		return dbl; }
	
	///////////////////////////////////////////////////////////////////////////
	
	protected Object item; 
	
	/** Caches the given Object as the current Item to be picked up by writeName(String).
	 * @see streamIO.IIStreamOut#addItem(java.lang.Object)	 */
	public IIStreamOut addItem(final Object _value) { 
		L.enter().l("value=").l(_value); 
		item = _value; 
		//currName = structName; 
		//if (clone && (_value instanceof Cloneable)) 
		//	item = ((Cloneable) item).clone(); //the clone() Method is not public in Cloneable!  
		return this; }
	
	/** Returns the cached current Item.
	 * @see streamIO.IFactory#nextItem()	 */
	public Object nextItem() { 
		//final Object ret = item; //item = null; 
		return item; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Default Value when no Integers have been added 	 */
	public static final short[] DEFAULT_SHORTS = new short[0]; 
														
	/** Cache for the Array handed over	 */
	protected short[] shorts = DEFAULT_SHORTS; 
	
	/** Returns and clears the accumulated short Array.
	 * @see streamIO.integer.IStreamIn_Struct#nextShorts()	 */
	public short[] nextShorts() { final short[] ret = shorts; shorts = DEFAULT_SHORTS; return ret; }

	/** Appends the given range of the short Array to the accumulated shorts Array.
	 * @see streamIO.integer.IStreamOutStructArrays#addShorts(short[], int, int) */
	public IStreamOutStruct addShorts(final short[] values, final int stop, final int start) {
		final short[] tmp = new short[shorts.length+stop-start];
		System.arraycopy(shorts,     0, tmp, 0, shorts.length);
		System.arraycopy(values, start, tmp   , shorts.length, stop-start);
		shorts = tmp;
		return this; //trigger(); //zu fr�h! vielleicht kommt noch was nach?
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Default Value when no Integers have been added
	 * Safe to use it, since immutable! 	 */
	public static final int[] DEFAULT_INTS = new int[0]; 
														
	/** Cache for the Array handed over	 */
	protected int[] ints = DEFAULT_INTS; 
	
	/** Returns and clears the accumulated int Array, delegating to the wrapped Reader when reading.
	 * @see streamIO.integer.IStreamIn_Struct#nextInts()	 */
	public int[] nextInts() {
		if (streamIn != null) //&& (ints == DEFAULT_INTS) //no addInts happened
			return streamIn.nextInts(); //read it from the Stream
		final int[] ret = ints; ints = DEFAULT_INTS; return ret; }
	
	/** Appends the given range of the int Array to the accumulated ints Array.
	 * @see streamIO.integer.IStreamOutStructArrays#addInts(int[], int, int) */
	public IStreamOutStruct addInts(final int[] values, final int stop, final int start) {
		final int[] tmp = new int[ints.length+stop-start]; 
		System.arraycopy(  ints,     0, tmp, 0, ints.length); 
		System.arraycopy(values, start, tmp   , ints.length, stop-start);
		ints = tmp; 
		return this; //trigger(); //zu fr�h! vielleicht kommt noch was nach?  
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Default Value when no Integers have been added 	 */
	public static final long[] DEFAULT_LONGS = new long[0]; 
														
	/** Cache for the Array handed over	 */
	protected long[] longs = DEFAULT_LONGS; 
	
	/** Returns and clears the accumulated long Array, delegating to the wrapped Reader when reading.
	 * @see streamIO.integer.IStreamIn_Struct#nextInts()	 */
	public long[] nextLongs() {
		if (streamIn != null) //&& (longs == DEFAULT_LONGS)) //no addInts happened
			return streamIn.nextLongs(); //read it from the Stream
		final long[] ret = longs; longs = DEFAULT_LONGS; return ret; }
	
	/** Appends the given range of the long Array to the accumulated longs Array.
	 * @see streamIO.integer.IStreamOutStructArrays#addLongs(long[], int, int) */
	public IStreamOutStruct addLongs(final long[] values, final int stop, final int start) {
		final long[] tmp = new long[longs.length+stop-start];
		System.arraycopy( longs,     0, tmp, 0, longs.length);
		System.arraycopy(values, start, tmp   , longs.length, stop-start);
		longs = tmp;
		return this; //trigger(); //zu fr�h! vielleicht kommt noch was nach?  
	}

	///////////////////////////////////////////////////////////////////////////
	
	/** Default Value when no Integers have been added 	 */
	public static final float[] DEFAULT_FLOATS = new float[0]; 
														
	/** Cache for the Array handed over	 */
	protected float[] floats = DEFAULT_FLOATS; 
	
	/** Returns and clears the accumulated float Array, delegating to the wrapped Reader when reading.
	 * @see streamIO.integer.IStreamIn_Struct#nextInts()	 */
	public float[] nextFloats() {
		if (streamIn != null) //&& (floats == DEFAULT_FLOATS)) //no addInts happened
			return streamIn.nextFloats(); //read it from the Stream
		final float[] ret = floats; floats = DEFAULT_FLOATS; return ret; }
	
	/** Appends the given range of the float Array to the accumulated floats Array.
	 * @see streamIO.integer.IStreamOutStructArrays#addFloats(float[], int, int) */
	public IStreamOutStruct addFloats(final float[] values, final int stop, final int start) {
		final float[] tmp = new float[floats.length+stop-start];
		System.arraycopy(floats,     0, tmp, 0, floats.length);
		System.arraycopy(values, start, tmp   , floats.length, stop-start);
		floats = tmp;
		return this; //trigger(); //zu fr�h! vielleicht kommt noch was nach?  
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Default Value when no Integers have been added 	 */
	public static final double[] DEFAULT_DOUBLES = new double[0]; 
														
	/** Cache for the Array handed over	 */
	protected double[] doubles = DEFAULT_DOUBLES; 
	
	/** Returns and clears the accumulated double Array, delegating to the wrapped Reader when reading.
	 * @see streamIO.integer.IStreamIn_Struct#nextInts()	 */
	public double[] nextDoubles() {
		if (streamIn != null) //&& (doubles == DEFAULT_DOUBLES)) //no addInts happened
			return streamIn.nextDoubles(); //read it from the Stream
		final double[] ret = doubles; doubles = DEFAULT_DOUBLES; return ret; }
	
	/** Appends the given range of the double Array to the accumulated doubles Array.
	 * @see streamIO.integer.IStreamOutStructArrays#addDoubles(double[], int, int) */
	public IStreamOutStruct addDoubles(final double[] values, final int stop, final int start) {
		final double[] tmp = new double[doubles.length+stop-start];
		System.arraycopy(doubles,    0, tmp, 0, doubles.length);
		System.arraycopy(values, start, tmp   , doubles.length, stop-start);
		doubles = tmp;
		return this; //trigger(); //zu fr�h! vielleicht kommt noch was nach?  
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Default Value when no Integers have been added 	 */
	public static final Object[] DEFAULT_ITEMS = new Object[0]; 
														
	/** Cache for the Array handed over	 */
	protected Object[] items = DEFAULT_ITEMS; 
	
	/** Returns and clears the accumulated Object Array, delegating to the wrapped Reader when reading.
	 * @see streamIO.integer.IStreamIn_Struct#nextInts()	 */
	public Object[] nextItems() {
		if (streamIn != null) //&& (items == DEFAULT_ITEMS)) //no addInts happened
			return streamIn.nextItems(); //read it from the Stream
		final Object[] ret = items; items = DEFAULT_ITEMS; return ret; }
	
	/** Appends the given range of the Object Array to the accumulated items Array.
	 * @see streamIO.integer.IStreamOutStructArrays#addItems(java.lang.Object[], int, int) */
	public IStreamOutStruct addItems(final Object[] values, final int stop, final int start) {
		final Object[] tmp = new Object[items.length+stop-start];
		System.arraycopy( items,     0, tmp, 0, items.length);
		System.arraycopy(values, start, tmp   , items.length, stop-start);
		items = tmp;
		return this; //trigger(); //zu fr�h! vielleicht kommt noch was nach?  
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Default Value when no Integers have been added 	 */
	public static final String[] DEFAULT_STRINGS = new String[0]; 
														
	/** Cache for the Array handed over	 */
	protected String[] strings = DEFAULT_STRINGS; 
	
	/** Returns and clears the accumulated String Array, delegating to the wrapped Reader when reading.
	 * @see streamIO.integer.IStreamIn_Struct#nextInts()	 */
	public String[] nextStrings() {
		if (streamIn != null) //&& (strings == DEFAULT_STRINGS) //no addInts happened
			return streamIn.nextStrings(); //read it from the Stream
		final String[] ret = strings; strings = DEFAULT_STRINGS; return ret; }
	
	/** Appends the given range of the String Array to the accumulated strings Array.
	 * @see streamIO.integer.IStreamOutStructArrays#addStrings(java.lang.String[], int, int) */
	public IStreamOutStruct addStrings(final String[] values, final int stop, final int start) {
		final String[] tmp = new String[strings.length+stop-start];
		System.arraycopy(strings,    0, tmp, 0, strings.length);
		System.arraycopy(values, start, tmp   , strings.length, stop-start);
		strings = tmp;
		return this; //trigger(); //zu fr�h! vielleicht kommt noch was nach?  
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// Interface IStreamIn_Struct
	////////////////////////////////////////////////////////////////////////////
	
	/** Delegates to the wrapped Reader's nextToken(), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Struct#nextToken() */
	public int nextToken() throws IOException {
		if (streamIn != null) //
			return streamIn.nextToken(); //read it from the Stream
		return 0;
	}
	
	/** Delegates to the wrapped Reader's currToken(), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Struct#currToken() */
	public int currToken() {
		if (streamIn != null) //
			return streamIn.currToken(); //read it from the Stream
		return 0;
	}
	
	/** Delegates to the wrapped Reader's pushBack(), or returns null when reading in-memory.
	 * @see streamIO.IPushBackAble#pushBack() */
	public IPushBackAble pushBack() {
		if (streamIn != null) //
			return streamIn.pushBack(); //read it from the Stream
		return null;
	}
	
	/** Delegates to the wrapped Reader's isValid(), or returns false when reading in-memory.
	 * @see streamIO.IIStreamIn#isValid() */
	public boolean isValid() {
		if (streamIn != null) //
			return streamIn.isValid(); //read it from the Stream
		return false;
	}
	
	/** Delegates to the wrapped Reader's Iterator(), or returns null when reading in-memory.
	 * @see streamIO.IIterAble#Iterator() */
	public IIStreamIn Iterator() {
		if (streamIn != null) //
			return streamIn.Iterator(); //
		return null;
	}
	
	/** Delegates to the wrapped Reader's getOrder(), or returns 0 when reading in-memory.
	 * @see streamIO.IOrdered#getOrder() */
	public byte getOrder() {
		if (streamIn != null) //
			return streamIn.getOrder(); //
		return 0;
	}
	
	/** Delegates to the wrapped Reader's getStreamIn_Byte(), or returns null when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Char#getStreamIn_Byte() */
	public IStreamIn_Byte getStreamIn_Byte() {
		if (streamIn != null) //
			return streamIn.getStreamIn_Byte(); //
		return null;
	}
	
	/** Delegates to the wrapped Reader's nextChar(), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Char#nextChar() */
	public char nextChar() {
		if (streamIn != null) //
			return streamIn.nextChar(); //
		return 0;
	}
	
	/** Delegates to the wrapped Reader's read(), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Char#read() */
	public int read() throws IOException {
		if (streamIn != null) //
			return streamIn.read(); //
		return 0;
	}
	
	/** Delegates to the wrapped Reader's read(char[]), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Char#read(char[]) */
	public int read(final char[] cbuf) throws IOException {
		if (streamIn != null) //
			return streamIn.read(cbuf); //
		return 0;
	}
	
	/** Delegates to the wrapped Reader's read(char[], int, int), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Char#read(char[], int, int) */
	public int read(final char[] cbuf, final int off, final int len) throws IOException {
		if (streamIn != null) //
			return streamIn.read(cbuf, off, len); //
		return 0;
	}
	
	/** Delegates to the wrapped Reader's read(byte[]), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Byte#read(byte[]) */
	public int read(final byte[] b) throws IOException {
		if (streamIn != null) //
			return streamIn.read(b); //
		return 0;
	}
	
	/** Delegates to the wrapped Reader's read(byte[], int, int), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Byte#read(byte[], int, int) */
	public int read(final byte[] b, final int off, final int len) throws IOException {
		if (streamIn != null) //
			return streamIn.read(b, off, len); //
		return 0;
	}
	
	/** Delegates to the wrapped Reader's read(int, StringBuffer), or returns null when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Byte#read(int, java.lang.StringBuffer) */
	public StringBuffer read(final int _sep, final StringBuffer _b) throws IOException {
		if (streamIn != null) //
			return streamIn.read(_sep, _b); //
		return null;
	}
	
	/** Delegates to the wrapped Reader's read(int), or returns null when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Byte#read(int) */
	public StringBuffer read(final int _sep) throws IOException {
		if (streamIn != null) //
			return streamIn.read(_sep); //
		return null;
	}
	
	/** Delegates to the wrapped Reader's read(int[]), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Byte#read(int[]) */
	public int read(final int[] b) throws IOException {
		if (streamIn != null) //
			return streamIn.read(b); //
		return 0;
	}
	
	/** Delegates to the wrapped Reader's read(int[], int, int), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Byte#read(int[], int, int) */
	public int read(final int[] b, final int off, final int len) throws IOException {
		if (streamIn != null) //
			return streamIn.read(b, off, len); //
		return 0;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Delegates to the wrapped Reader's availAble(), or returns 0 when reading in-memory.
	 * @see streamIO.IAvailAble#availAble() */
	public long availAble() {
		if (streamIn != null) //
			return streamIn.availAble(); //
		return 0;
	}
	
	/** Delegates to the wrapped Reader's getPosition(), or returns 0 when reading in-memory.
	 * @see streamIO.IAvailAble#getPosition() */
	public long getPosition() {
		if (streamIn != null) //
			return streamIn.getPosition(); //
		return 0;
	}
	
	/** Delegates to the wrapped Reader's getMaxMarkSize(), or returns 0 when reading in-memory.
	 * @see streamIO.IMarkAble#getMaxMarkSize() */
	public long getMaxMarkSize() {
		if (streamIn != null) //
			return streamIn.getMaxMarkSize(); //
		return 0;
	}
	
	/** Delegates to the wrapped Reader's mark(), or returns null when reading in-memory.
	 * @see streamIO.IMarkAble#mark() */
	public IMarkAble mark() {
		if (streamIn != null) //
			return streamIn.mark(); //
		return null;
	}
	
	/** Delegates to the wrapped Reader's mark(long), or returns null when reading in-memory.
	 * @see streamIO.IMarkAble#mark(long) */
	public IMarkAble mark(final long readLimit) {
		if (streamIn != null) //
			return streamIn.mark(readLimit); //
		return null;
	}
	
	/** Delegates to the wrapped Reader's reSet(), or returns null when reading in-memory.
	 * @see streamIO.IReSetAble#reSet() */
	public IReSetAble reSet() {
		if (streamIn != null) //
			return streamIn.reSet(); //
		return null;
	}
	
	/** Delegates to the wrapped Reader's reSet(String), or returns null when reading in-memory.
	 * @see streamIO.IReSetAble#reSet(java.lang.String) */
	public IReSetAble reSet(final String failureExceptionMessage) {
		if (streamIn != null) //
			return streamIn.reSet(failureExceptionMessage); //
		return null;
	}
	
	/** Delegates to the wrapped Reader's reSet(long), or returns 0 when reading in-memory.
	 * @see streamIO.IReSetAble#reSet(long) */
	public long reSet(final long relPosition) {
		if (streamIn != null) //
			return streamIn.reSet(relPosition); //
		return 0;
	}
	
	/** Delegates to the wrapped Reader's jump(long), or returns 0 when reading in-memory.
	 * @see streamIO.IReSetAble#jump(long) */
	public long jump(final long offset) {
		if (streamIn != null) //
			return streamIn.jump(offset); //
		return 0;
	}
	
	/** Delegates to the wrapped Reader's jump(), or returns null when reading in-memory.
	 * @see streamIO.IReSetAble#jump() */
	public IReSetAble jump() {
		if (streamIn != null) //
			return streamIn.jump(); //
		return null;
	}
	
	/** Delegates to the wrapped Reader's available(), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Byte#available() */
	public int available() throws IOException {
		if (streamIn != null) //
			return streamIn.available(); //
		return 0;
	}
	
	/** Delegates to the wrapped Reader's fill(int[], int, int), or returns 0 when reading in-memory.
	 * @see streamIO.integer.IStreamIn_Int#fill(int[], int, int)	 */
	public int fill(int[] arr, int stop, int start) {
		if (streamIn != null) //
			return streamIn.fill(arr, stop, start);
		return 0; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Static Testing & Main Methods 
	///////////////////////////////////////////////////////////////////////////
	
	/** Smoke-tests round-tripping a {@link VersionTree} through this in-memory cloning Instantiator. */
	public static void main(final String[] args) throws Exception {
		final StreamOutInstantiator instant = new StreamOutInstantiator();
		final VersionTree vs = VersionedObjects.testIt();
		final String original = vs.toString(); 
		vs.writeTo(instant, "root"); 
		final String copy = instant.currObj.toString(); 
		Assert.EQUALS(original, copy); 
	}

}
