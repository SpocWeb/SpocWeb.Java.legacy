/**
 * File  Name: OutputStreamToStreamOutByte.java
 * Created on: 23.02.2003
 */
package streamIO.integer.adapter;

import java.io.IOException;
import java.io.OutputStream;

import streamIO.integer.AStreamOutByte;

/**
 * Title: enclosing_type<p>
 * Description:
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
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
 * @author mheuer
 * @version	1.0
 *
 */
public class OutputStreamToStreamOutByte 
extends AStreamOutByte {

	/** Reference to the actual streamIO delegated to */
	protected OutputStream stream; 

	/**
	 * Constructor for InputStreamToStreamIn_Byte.
	 */
	public OutputStreamToStreamOutByte(OutputStream stream_) {
		this.stream = stream_;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Implementation of IStreamOutByte 
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** @see streamIO.Byte.IStreamOutByte#addString(int)	 */
	public void write(int b) throws IOException { stream.write(b); }

	/** @see streamIO.Byte.IStreamOutByte#flush()	 */
	public void flush() throws IOException { stream.flush(); }

	/** @see streamIO.Byte.IStreamOutByte#close()	 */
	public void close() throws IOException { stream.close(); }

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Optimizations 
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.Byte.IStreamOutByte#addString(byte[], int, int)	 */
	public void write(byte[] b, int off, int len) throws IOException {
		stream.write(b, off, len); }

	/** @see streamIO.Byte.IStreamOutByte#addString(byte[])	 */
	public void write(byte[] b) throws IOException { stream.write(b); }

}
