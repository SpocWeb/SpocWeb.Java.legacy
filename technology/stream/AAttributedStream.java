/*
 * File Name: AAttributedStream.java
 * Created on: 03.12.2003
 *
 */
package technology.stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Title: AAttributedStream<p>
 * Description:
 * Implements Default Methods for both Handling Types. 
 * Inheriting Classes need to implement only that Method 
 * that best fits its own Processing Model, 
 * either writing actively ALL DATA into an OutputStream
 * or delivering the Data on Demand via an InputStream.  
 *
 * Known SubClasses: 
 * @see com.ctp.soap.proxy.SOAPAdapter
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
public abstract class AAttributedStream 
implements IAttributedStreamIn, IAttributedStreamOut {

	/** @see AttributedStreamInAdapter#process(InputStream, Map)	 */
	public InputStream process(final InputStream request, final Map params)
	throws ProcessingException { //save copying it twice by reusing the Cache!
		OpenByteArrayOutputStream cache = new OpenByteArrayOutputStream();
		process(request, params, cache); 
		return new ByteArrayInputStream(cache.getBuffer(), 0, cache.size());
	}

	/** @see com.ctp.soap.proxy.IAttributedStreamOut#process(java.io.InputStream, java.util.Map, java.io.OutputStream)	 */
	public void process(final InputStream inStream, final Map params, final OutputStream outStream) throws ProcessingException {
		InputStream result = process(inStream, params);
		try {
			OpenByteArrayOutputStream.STREAM(result, outStream);
		} catch (IOException x) {
			throw new ProcessingException(x);
		}
	}
	
}
