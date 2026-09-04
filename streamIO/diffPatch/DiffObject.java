/*
 * Created on 01.02.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.diffPatch;

import streamIO.integer.IStreamIn_Struct;
import streamIO.integer.IStreamOutStruct;
import synch.ValidationRule;

/**
 * Title: <p>
 * Description:
 * Generic single Difference Object with a Reference to the actual Object Value changed. 
 * 
 * Design Decisions / Implementation Details:
 * 
 * Known SubClasses: <none>
 * 
 * Known Uses: 
 * @see streamIO.diffPatch.DiffSetObject collects multiple DiffObjects in one List.   
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
final public class DiffObject 
extends DiffBase {
	
	/** Reference to the Object changed. 	 */
	protected Object value; //final public 
	
	public Object getValue() { return value;  }
	
	/** Empty Constructor solely for DeSerialization	 */ 
	public DiffObject() { } 
	
	/**
	 * @param _position
	 * @param _value 
	 */
	public DiffObject(final Object _value, final int _position) {
		super(_position);
		this.value = _value; 
	}
	
	/** 
	 * Has to be implemented so as to compare the Values of the Differences, ignoring the Positions. 
	 * @param obj any DiffBase Object of same Type
	 * @return true when both Diffs denote the same Position and the same Value. 
	 */
	public boolean equals(final DiffObject diff) {
		if (this.position != diff.position) 
			return false; 
		return ValidationRule.EQUALS(this.value, diff.value); 
	} 
	
	/** 
	 * Has to be implemented so as to compare the Values of the Differences, ignoring the Positions. 
	 * @param obj any DiffBase Object of same Type
	 * @return true when both Diffs denote the same Position and the same Value. 
	 */
	public boolean equals(final Object obj) {
		if (obj instanceof DiffObject) 
			return equals((DiffObject) obj);
		return false; } 
	
	////////////////////////////////////////////////////////////////////////////
	/// De-/Serialization 
	////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.AStreamAble#readField(java.lang.String, streamIO.integer.IStreamIn_Struct)	 */
	public Object readField(final String name, final IStreamIn_Struct stream) {
		if (STR_VALUE.equals(name)) 
			return value = stream.nextItem();  
		else
			return super.readField(name, stream); 
	}
	
	public static final String STR_VALUE = "value"; 
	
	/** appends all characteristic Members of this Object to the Stream.
	 * @see streamIO.integer.IStreamWriteAble#writeTo(streamIO.integer.IStreamOutStruct)
	 * @param stream the PrintStream to write to.  
	 * @return a new PrintStreamOut writing into a StringBuffer. 
	 */
	public void writeTo(final IStreamOutStruct stream) {
		super.writeTo(stream); 
		stream.writeStruct(STR_VALUE, value);
	}
	
}
