/*
 * Created on 28.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.diffPatch;

import streamIO.integer.AStreamAble;
import streamIO.integer.IStreamIn_Struct;
import streamIO.integer.IStreamOutStruct;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Base Class for Value Objects to hold the Position and the actual Change. 
 *
 * Design Decisions / Implementation Details:
 * In order to be able to use the Diff to roll both forward and backward, 
 * Changes are stored in both Cases, 
 * although Deletions could simply be noted with Line Numbers. 
 * CVS batches Changes, so Conflicts always embrace the whole Change 
 * and not individual Lines.   
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
 * pass: 2
 * mtime: 2026-09-05T10:22:33Z
 * digest: 4bbde5c0895d6f5441d2a436b1057ebf3443a4520dc197da422603c74bfed22c
 * stale: false
 * tags: [code/diff_object]
 * concepts: [Diffing]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public abstract class DiffBase 
extends AStreamAble 
implements Cloneable {
	
	/** the Position of the Change; 
	 * positive Values indicate Addition ad the Position, 
	 * negative Values indicate Deletion at the binary complemented Position!	 */
	protected int position;

	/** Returns the Position of this Change.
	 * @return the Position of this Change; negative Values are the binary complement of a Deletion Position.	 */
	public int getPosition() { return position; }
	
	/** Constructor to initialize this Object from a Stream  
	 * @param stream
	 */
	public DiffBase(final IStreamIn_Struct stream) {
		super(stream); 
		/**
		 * wie liest man aus einem Stream? 
		 * die readField Methoden sind Daten-getrieben 
		 * @see streamIO.integer.StreamIn_Struct#stream(IStreamOutStruct) ist Stream-getrieben, 
		 * @see streamIO.integer.StreamOutInstantiator wird dabei verwendet, 
		 * aber der Konstruktor muss vor�bergehend die Kontrolle erhalten! 
		 * In der ersten Implementierung k�nnte man davon ausgehen, 
		 * dass die Reihenfolge der Daten gleich bleibt, so dass hart geparsed werden kann.
		 * 
		 * Man k�nnte auch m�glichst viele Name-Value nur zu Strings parsen 
		 * & f�r Konstruktoren cachen. 
		 * Daf�r k�nnte man eine extra Container Hierarchie aufbauen, 
		 * was nat�rlich nicht so effizient ist wie das direkte parsen aus dem Stream
		 * (weder vom Memory noch von der Verarbeitung aus). 
		 * Diese Container Hierarchie k�nnte das IStreamOutStruct Interface implementieren 
		 * und ein erweitertes Random-Access-IStreamIn_Struct Interface, 
		 * das es erlaubt, ein bestimmtes Feld zu lesen. 
		 * Weiterhin k�nnte diese Hierarchie generell n�tzlich sein, 
		 * da sie auch beliebige Strukturen geparsed zur Verf�gung stellt. 
		 * 
		 * Ein Problem dabei ist, dass auch innere Objekte geparsed werden m�ssen. 
		 * Zirkul�re Referenzen sind mit finalen Variablen zum Gl�ck prinzipiell 
		 * NICHT m�glich! 
		 */
		stream.mark(); 
		String name = stream.nextString(); 
		if (STR_POS.equals(name)) 
			position = stream.nextInt();  
		stream.reSet(); 
	}
	
	/** Unique (ascending) ID for the Version to be able to batch Changes	 */
	//final public int version; 
	
	/** Reference to the containing Difference Set to be able to navigate along the Tree 	 */
	//final public DiffSet parent; 
	
	/** Empty Constructor solely for DeSerialization	 */ 
	protected DiffBase() { super(null); } 
	
	/** initializing Constructor 
	 * @param _position
	 */
	//public DiffBase(final int _position) { this(_position); }
	
	/** initializing Constructor 
	 * @param _position
	 * @param _version
	 */
	//public DiffBase(final int _position, final DiffBase _parent) {
	//	this(_position, _parent, 0); }
	
	/** initializing Constructor 
	 * @param _position
	 * @param _version
	 */
	public DiffBase(final int _position //, final DiffBase _parent, final int _version
		) { super(null); 
		this.position = _position; 
		//this.version = _version; 
		//this.parent = _parent; 
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// De-/Serialization 
	////////////////////////////////////////////////////////////////////////////
	
	/** Field Name used to (de)serialize the {@link #position}.	 */
	final public static String STR_POS = "pos";

	/** Reads the {@link #position} field by Name from the Stream; delegates unknown Fields to the Superclass.
	 * @see streamIO.integer.AStreamAble#readField(java.lang.String, streamIO.integer.IStreamIn_Struct)	 */
	public Object readField(final String name, final IStreamIn_Struct stream) {
		if (STR_POS.equals(name)) 
			return new Integer(position = stream.nextInt());  
		else
			return super.readField(name, stream); 
	}
	
	/** appends all characteristic Members of this Object to the Stream.
	 * @see streamIO.integer.IStreamWriteAble#writeTo(streamIO.integer.IStreamOutStruct)
	 * @param stream the PrintStream to write to.  
	 * @return a new PrintStreamOut writing into a StringBuffer. 
	 */
	public void writeTo(final IStreamOutStruct stream) {
		//super.writeTo(stream); 
		stream.writeName(STR_POS); 
		stream.addInt(position);// addInt(position); 
	}
	
	/** 
	 * Has to be implemented so as to compare the Values of the Differences, ignoring the Positions. 
	 * @param obj any DiffBase Object of same Type
	 * @return true when both Diffs denote the same Position and the same Value. 
	 */
	public abstract boolean equals(final Object obj); /* {
		if (obj instanceof DiffBase) 
			return equals((DiffBase) obj);
		return false; 
	} */
	
	/**returns the inverse Difference, which has the same Value, but the complemented Position.
	 * @return the inverse Difference, which has the same Value, but the complemented Position. 
	 */
	public DiffBase inv() { 
		try {
			final DiffBase ret = (DiffBase) this.clone(); 
			ret.position = ~position;  
			return ret;
		} catch (final CloneNotSupportedException x) {
			return null; 
		}
	}
	
}
