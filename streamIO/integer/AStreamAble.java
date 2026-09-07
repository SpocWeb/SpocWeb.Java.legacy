/*
 * Created on 29.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;


/**
 * Abstract base class for structured types that support both serialization and
 * deserialization, requiring subclasses to expose a constructor taking an
 * {@link IStreamIn_Struct} so that {@code final} fields can be populated during
 * construction, since this cannot be enforced through an interface alone.
 *
 * ## Collaborators
 * | Type | Relationship |
 * |---|---|
 * | {@link AStreamWriteAble} | Superclass supplying the write/serialization half of this
 * class's read-and-write contract. |
 * | {@link IStreamReadAble} | Interface this class implements, defining the deserialization
 * contract fulfilled here. |
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * @see AStreamWriteAble write/serialization superclass
 * @see IStreamReadAble deserialization contract implemented by this class
 * <!-- docstate
 * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
 * concepts: [Primitive and Structured Stream I/O Core Abstractions]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
abstract public class AStreamAble
extends AStreamWriteAble
implements IStreamReadAble {
	
	/** Flag to ignore unknown Fields from the IStreamIn_Struct
	 * these could possibly stem from a newer Implementation. 
	 */
	static boolean IGNORE_UNKNOWN_FIELDS = true; 
	
	/** Flag to ignore passing null to the Constructor
	 * which typically comes from Constructors other than for Deserialization. 
	 */
	//static boolean IGNORE_EMPTY_CONSTRUCTOR = true; 
	
	/** Empty Constructor to allow for direct Creation of Subclasses. 	 */
	//protected AStreamAble() {}
	
	/** Constructor to allow for Initialization during Construction. 
	 * This can also initialize Fields declared as final! 
	 * This Constructor is merely a reminder to implement it in Subclasses! 
	 * @param stream the Stream to read Data from. 
	 */
	protected AStreamAble(final IStreamIn_Struct stream) {
		//if (!IGNORE_EMPTY_CONSTRUCTOR)
		//	stream.toString(); //provoke a NullPointer Exception  
	}
	
	/** reads the given Fields from the structured Stream. 
	 * This Method should be overloaded by every Subclass 
	 * and possibly dispatch to specific (private) read... Methods taking the Stream 
	 * (just to make it more modular). 
	 * This Method is necessary to support random Field Sequences in the Stream. 
	 * It must be called only AFTER the full Information is available in the stream. 
	 * Reading from a Stream then is a simple Loop of reading the next Field Name 
	 * and then calling this Method to read the Contents. 
	 * Instantiation can either happen directly (no Polymorphism) 
	 * or by reading the Class Information from the Stream. 
	 * Callable only when the stream is ready (filled), i.e. for .  
	 * @param name the Field Name to read 
	 * @param stream the Stream to read from 
	 */
	public Object readField(final String name, final IStreamIn_Struct stream) {
		if (IGNORE_UNKNOWN_FIELDS) //shortcut the following Checks! 
			return null; 
		if (IStreamOutStruct.STR_CLASS.equals(name))
			return null; 
		if (IStreamOutStruct.STR_OBJ_ID.equals(name))
			return null; 
		if (IStreamOutStruct.STR_REF_ID.equals(name))
			return null; 
		throw new RuntimeException("Unknown Field name="+name); 
	}
	
	//abstract public AStreamWriteAble getReader(final String name); 
	
	/** @see streamIO.integer.IStreamWriteAble#Reader()	 
	 * 
	 * @return
	public IStreamIn_Struct Reader() {
		//get all Fields via Reflection and populate the Reader Instance with it.
		// TODO Auto-generated method stub
		return null;
	}
	 */
	
	/** Empty smoke-test entry point; performs no action. */
	public static void main(final String[] args) throws Exception {
	}
}
