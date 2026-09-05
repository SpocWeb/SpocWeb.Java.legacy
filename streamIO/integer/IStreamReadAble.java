/*
 * Created on 28.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Defines the Interface for an Object that can be read/restored from a structured Stream. 
 * Additionally all Components of the Object must implement this Interface 
 * AND have empty Constructors. 
 *
 * Design Decisions / Implementation Details:
 * It also requires a Factory that creates Objects of the required Type. 
 * Constructors with Parameters could be possible, 
 * but that would be too complicated. 
 * Additionally Fields cannot be made final, since they are initialized later! 
 * To have several Threads concurrently reading from the same Object 
 * the Object needs to be able to create Iterators/IStreamInStruct Instances.  
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
public interface IStreamReadAble {
	
	/** initiates reading the given Field from the structured Stream. 
	 * This Method should be overloaded by every Subclass 
	 * and possibly dispatch to specific (private) read... Methods taking the Stream. 
	 * This Method is necessary to support random Field Sequences in the Stream. 
	 * It must be called only AFTER the full Information is available in the stream. 
	 * Reading from a Stream then is a simple Loop of reading the next Field Name 
	 * and then calling this Method to read the Contents. 
	 * Instantiation can either happen directly (no Polymorphism) 
	 * or by reading the Class Information from the Stream. 
	 * Callable only when the stream is ready (filled), i.e. for .  
	 * @param name the Field Name to read 
	 * @param stream the Stream to read from 
	 * @return true when the Field was successfully read
	 */
	public Object readField(final String name, final IStreamIn_Struct stream); 
	
	/** active Method to write this Object to the given Stream. 
	 * The Default Implementation could just stream 
	 * from the given stream 
	 * to a newly created Writer Instance. 
	 * @param stream the Stream to write to 
	 */
	//public void readFrom(final IStreamIn_Struct stream); 
	
	/** passive Method to write to this Object. 
	 * @see streamIO.integer.StreamOutInstantiator achieves this, 
	 * using the Method readField 
	 * @return an IStreamOutStruct to write to or read it into a Stream
	 */
	//public IStreamOutStruct Writer(); 
	
}
