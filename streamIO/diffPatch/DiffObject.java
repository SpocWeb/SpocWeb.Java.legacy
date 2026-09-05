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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:22:47Z
 * digest: 2ac3374cf13562f32f66d5c341b943a702cb72587d57bfb26b07ff4c1e7e7ce3
 * stale: false
 * tags: [code/diff_object]
 * concepts: [Diffing]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
final public class DiffObject 
extends DiffBase {
	
	/** Reference to the Object changed. 	 */
	protected Object value; //final public

	/** Returns the Object Value referenced by this Change.
	 * @return the Object Value referenced by this Change.	 */
	public Object getValue() { return value;  }

	/** Empty Constructor solely for DeSerialization	 */
	public DiffObject() { }

	/** Creates a single Object-valued Change at the given Position.
	 * @param _value the new Object Value at this Position
	 * @param _position the Position of the Change
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
	
	/** Reads the {@link #value} field by Name from the Stream; delegates unknown Fields to the Superclass.
	 * @see streamIO.integer.AStreamAble#readField(java.lang.String, streamIO.integer.IStreamIn_Struct)	 */
	public Object readField(final String name, final IStreamIn_Struct stream) {
		if (STR_VALUE.equals(name))
			return value = stream.nextItem();
		else
			return super.readField(name, stream);
	}

	/** Field Name used to (de)serialize the {@link #value}.	 */
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
