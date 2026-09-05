/*
 * File Name: RiffFile.java
 * Created on: 03.01.2004
 *
 */
package sound;

import java.io.IOException;

import streamIO.integer.encoding.BigEndianReader;

/**
 * Title: RiffFile<p>
 * Description:
 * Functionality to read and write Windows RIFF Files
 * (Resource Interchange File Format), which consist of several consecutive Chunks. 
 * 
 * Each RIFF File starts with an 8 Byte RIFF Header consisting of
 * 4 Byte Text = "RIFF" 
 * 4 Byte = 32 Bit File Size (allowing for up to 4 GByte Files) 
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 204d0ee3a8988c8e45876d595271e3191c8353ccd78b07ad0a8e9573ebe8507d
 * stale: false
 * tags: [code/binary_file_format]
 * concepts: [RIFF Container]
 * facets: {layer: domain, status: stable, complexity: low}
 * -->
 */
public class RiffFile 
extends FileChunk { //extension is not really necessary, only as a Convenience! 

	/** Header Prefix indicating a RIFF File 	*/
	final static public String RIFF_HEADER = "RIFF"; 
	
	/** Data contained in the RIFF Format 	*/
	final public String riffType; 
	
	/** Initializing Constructor 
	 * 
	 * @param streamIn_ the DataInput Implementation to use
	 */
	public RiffFile(final BigEndianReader streamIn_, final String riffType_) throws IOException {
		super(streamIn_, RIFF_HEADER); 
		riffType = readChunkTyp(); 
		if ((riffType_ != null) && 
			!riffType_.equals(riffType))
			throw new IOException("Expected RIFF-Type: "+riffType_+" but actual:"+riffType); 
	}

}
