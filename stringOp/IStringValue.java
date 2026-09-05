/*
 * Created on 02.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stringOp;

/**
 * used with Patricia Tries to implement different unique Indices
 * for the same Class. 
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
 * mtime: 2026-09-05T10:13:32Z
 * digest: 4d615cb9904baf4fb1ebd76bbd1ebd15221952fbed1f1185a28c7dd9cce0a7f2
 * stale: false
 * tags: [code/string_algorithms]
 * concepts: [String Value Interface]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IStringValue {
	
	/**
	 * used with Patricia Tries to implement different unique Indices 
	 * @param arg the Object to retrieve a String Representation for. 
	 * @return a String Representation of arg
	 * a Default Implementation would return arg.toString(). 
	 */
	public String getString(final Object arg); 
	
}
