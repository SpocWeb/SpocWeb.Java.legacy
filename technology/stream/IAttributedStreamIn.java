/*
 * File Name: IAttributedStreamIn
 * Created on: 02.12.2003
 *
 */
package technology.stream;

import java.io.InputStream;
import java.util.Map;

/**
 * Title: IAttributedStreamIn<p>
 * Description:
 * Defines the Interface 
 * for handing over an InputStream and a Map of Attributes along a Processing Line 
 * Interfaces between asynchronous and synchronous Protocols and Technologies 
 * like HTTP, JMS, DB etc. 
 *
 * Design Decisions / Implementation Details:
 * Using Streams is for once adequate for simple HTTP Requests and Responses 
 * and can always be aggregated / broken up into Strings.
 * When using certain Encodings these have to be specified 
 * either in the init() or the process() Method. 
 *  
 * InputStreams are most lightweight, because even large Quantities of Data 
 * can be handed back a Chain and only be processed at the End and only on Demand. 
 * Intermediate Processing usually requires assembling and parsing 
 * at least parts of the Stream and even possibly sending it further.  
 *
 * Known Implementations: 
 * @see com.ctp.soap.proxy.SOAPAdapter
 * @see com.ctp.soap.proxy.jms.JmsAdapter
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
public interface IAttributedStreamIn 
extends IManagedComponent {

	/**
	 * process the Data 
	 * The Return Value is an InputStream which is potentially more performant
	 * than an OutputStream, because it needn't be fully evaluated 
	 * and doesn't necessarily require Memory. 
	 * @param inStream Data to be processed
	 * @param params MetaData for this Data 
	 * @return an InputStream to be evaluated by the Caller
	 * @throws ProcessingException when the Data cannot be processed
	 */
	InputStream process(final InputStream inStream, final Map params)
	throws ProcessingException;
	
}
