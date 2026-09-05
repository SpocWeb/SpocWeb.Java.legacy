/*
 * File Name: IAttributedStreamOut.java
 * Created on: 03.12.2003
 *
 */
package technology.stream;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Defines the eager, fully-written side of an attributed stream processing chain: handing
 * over an InputStream plus a Map of Attributes between asynchronous and synchronous
 * protocols and technologies like HTTP, JMS, DB etc.
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
 * @see 
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
 * mtime: 2026-09-05T11:11:50Z
 * digest: 0b8b488aebfd15c4cab93f70fccc38cbefc3625e13cf77cd01ba895fa48bac17
 * stale: false
 * tags: [code/stream_adapter]
 * concepts: [Attributed Stream Output Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IAttributedStreamOut 
extends IManagedComponent {

	/**
	 * process the Data 
	 * The Return Value is to be written into the given OutputStream 
	 * which potentially imperformant, because it is fully evaluated 
	 * and requires the Memory to hold the Stream. 
	 * @param inStream Data to be processed
	 * @param params MetaData for this Data 
	 * @param result an OutputStream to be populated by the Result of this Method 
	 * @throws ProcessingException when the Data cannot be processed
	 */
	void process(final InputStream inStream, final Map params, final OutputStream result)
	throws ProcessingException;
	
}
