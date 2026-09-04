/*
 * Created on 28.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

/**
 * Title: <p>
 * Defines the Interface for an Object that can be written to a structured Stream. 
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
public interface IStreamWriteAble {
	
	/** writes this Object to the given Stream 
	 * including the Name and Structure. 
	 * The Default Implementation could just stream 
	 * from a newly created Writer Instance. 
	 * to the given stream 
	 * @param stream the Stream to write to 
	 * @param name the Name to give to this Object (in XML)
	 */
	public void writeTo(final IStreamOutStruct stream, final String name); 
	
	/** writes this Object to the given Stream without enclosing Structure. 
	 * The Default Implementation could just stream 
	 * from a newly created Writer Instance. 
	 * to the given stream 
	 * @param stream the Stream to write to 
	 */
	public void writeTo(final IStreamOutStruct stream); 
	
	/** passive Method to read this Object (most flexible). 
	 * Analogous to an Iterator, but it iterates over the Fields. 
	 * This creates an Overhead, since it has to track, 
	 * which Fields have already been read.  
	 * @return an IStreamIn_Struct to read from or write it to a Stream
	 */
	//public IStreamIn_Struct Reader(); 
	
}
