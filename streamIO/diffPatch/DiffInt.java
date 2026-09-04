/*
 * Created on 28.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.diffPatch;

import streamIO.integer.IStreamOutStruct;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Diff Implementation for integer Values. 
 * creates a Diff on Streams of Integer Values. 
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
 */
public class DiffInt 
extends DiffBase {
	
	/** The actual Value of the Change 
	 * In order to be able to use the Diff to roll both forward and backward, 
	 * Changes are stored in both Cases, 
	 * although Deletions could simply be noted with Line Numbers. 
	 */
	final public int value; 
	
	/**
	 * @param _position
	 * @param _version
	 * @param _value
	 */
	//public DiffInt(final int _value, final int _position) { this(_value, _position, null); }
	
	/**
	 * @param _position
	 * @param _version
	 * @param _value
	 */
	//public DiffInt(final int _value, final int _position, final DiffInt parent) { 
	//	this(_value, _position, parent, 0); }
	
	/**
	 * @param _position 
	 * @param _version 
	 * @param _value 
	 */
	public DiffInt(final int _value, final int _position) {//, final DiffInt parent, final int _version) {
		super(_position); //, parent, _version);
		this.value = _value; 
	}

	/** 
	 * Has to be implemented so as to compare the Values of the Differences, ignoring the Positions. 
	 * @param obj any DiffBase Object of same Type
	 * @return true when both Diffs denote the same Position and the same Value. 
	 */
	public boolean equals(final DiffInt diff) {
		if (this.position != diff.position) 
			return false; 
		return this.value == diff.value; 
	} 
	
	/** 
	 * Has to be implemented so as to compare the Values of the Differences, ignoring the Positions. 
	 * @param obj any DiffBase Object of same Type
	 * @return true when both Diffs denote the same Position and the same Value. 
	 */
	public boolean equals(final Object obj) {
		if (obj instanceof DiffInt) 
			return equals((DiffInt) obj);
		return false; 
	} 
	
	/** appends all characteristic Members of this Object to the Stream.
	 * @param stream the PrintStream to write to.  
	 * @return a new PrintStreamOut writing into a StringBuffer. 
	 */
	public void writeTo(final IStreamOutStruct stream) {
		super.writeTo(stream); 
		stream.writeName("val"); 
		stream.addInt(value); 
	}
	
}
