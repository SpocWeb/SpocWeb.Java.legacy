/*
 * Created on 02.04.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import java.lang.reflect.Field;

import streamIO.StringBufferOutputStream;
import streamIO.object.parser.StreamOutXML;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Abstract Base Class for WriteAble Classes
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
abstract public class AStreamWriteAble 
implements IStreamWriteAble {
	
	/** Deriving Classes must add their own Attributes and Elements (possibly via Reflection)  
	 * @see streamIO.integer.IStreamWriteAble#writeTo(streamIO.integer.IStreamOutStruct)	 */
	abstract public void writeTo(final IStreamOutStruct stream); 
	//	stream.writeNameValuePair(IStreamOutStruct.STR_CLASS, getClass().getName()); }
	
	/** The Name of the Root Element in the toString() Operation 	 */
	public static String ROOT_NAME = "root"; 
	
	/** Flag whether to use Xml or a Separated Format in the toString() Operation 	 */
	public static boolean USE_XML = false; //true; //
	
	/** Deriving Classes must add their own Attributes and Elements (possibly via Reflection)  
	 * @see streamIO.integer.IStreamWriteAble#writeTo(streamIO.integer.IStreamOutStruct)	 */
	public static final void WRITE_TO(final IStreamWriteAble obj, final IStreamOutStruct stream, final String name) {
		stream.open_Struct(name); 
		obj.writeTo(stream); 
		stream.closeStruct(); 
	}
	
	/**
	 * Slow Default Implementation for filling a Field in the given Object.  
	 * allows to use this Class to be filled directly from a Stream
	 * without Serialization. 
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException	 */
	final public static void SET_MEMBER(final Object ths, final String name, final Object value) 
	throws SecurityException, NoSuchFieldException, 
	IllegalArgumentException, IllegalAccessException {
		Class cls = ths.getClass(); 
		Field field = null; 
		do { //very slow due to Exceptions or looping through Lists. Can be sped up using a HashMap. 
			try { field = cls.getField(name); //DeclaredField(name); //since private or protected Fields cannot be set or read anyway, 
			} catch (final NoSuchFieldException x) { //this Method doesn't really make sense! 
				System.out.println(x.toString()); 
			} //protected Fields can be set from internal Methods, private cannot
			cls = cls.getSuperclass(); 
		} while((field == null) && 
				(cls != Object.class));
		if (field == null)
			throw new NoSuchFieldException(name); 
		field.set(ths, value); 
	}
	
	/**
	 * Slow Default Implementation for writing all Fields into the given Stream. 
	 * Since Child Classes don't have Access to private Fields, 
	 * this Method must be called on every Level in the Class Hierarchy, 
	 * if no custom Implementation is desired! 
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException	 */
	final public static void WRITE_TO(final IStreamOutStruct stream, 
			final Object ths) 
	throws SecurityException, NoSuchFieldException, 
	IllegalArgumentException, IllegalAccessException {
		Class cls = ths.getClass(); 
		final Field[] fields = cls.getDeclaredFields(); 
		for (int i  = fields.length; --i >= 0;) {
			final Field field = fields[i]; 
			stream.open_Struct(field.getName(), field.get(ths)); 
			stream.closeStruct(); 
		}
	}
	
	/** return the Characteristics of this DiffSet as a String
	 * @return the Characteristics of this DiffSet as a String
	 */
	public static final String TO_STRING(final IStreamWriteAble ths, final String name) {
		return TO_STRING(ths, name, USE_XML); }
	
	/** return the Characteristics of this DiffSet as a String
	 * @return the Characteristics of this DiffSet as a String
	 */
	public static final String TO_STRING(final IStreamWriteAble ths, final String name, final boolean useXml) { 
		final IStreamOutStruct stream;
		if (useXml) 
			stream = new StreamOutXML   (new StringBufferOutputStream()); 
		else
			stream = new StreamOutStruct(new StringBufferOutputStream()); 
		ths.writeTo(stream, name); 
		return stream.toString(); 
	}
	
	/** Necessary on every SubClass to be able to write private or protected Fields 	
	public void setField(final String name, final Object value) 
	throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		try { getClass().getDeclaredField(name).set(this, value); 
		} catch (final NoSuchFieldException x) {
			super.setField(name, value); 
		} //protected and private Fields can only be set from derived or internal Methods! 
	}
	 */
	
	/////////////////////////////////////////////////////////////////////////////
	
	/** return the Characteristics of this DiffSet as a String
	 * @return the Characteristics of this DiffSet as a String
	 */
	public String toString() { return TO_STRING(this, ROOT_NAME); }
	
	/** Deriving Classes must add their own Attributes and Elements (possibly via Reflection) 
	 * @see streamIO.integer.IStreamWriteAble#writeTo(streamIO.integer.IStreamOutStruct)	 */
	public void writeTo(final IStreamOutStruct stream, final String name) {
		stream.open_Struct(name, this); 
		//writeTo(stream); //already done above!  
		stream.closeStruct(); 
	}
	
	/////////////////////////////////////////////////////////////////////////////
	
	public static void main(final String[] args) throws Exception {
	}
	
}
