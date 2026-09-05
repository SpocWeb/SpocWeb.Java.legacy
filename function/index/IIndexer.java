/*
 * Created on 15.02.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package function.index;

/**
 * Title: <p>
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
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:41:14Z
 * digest: e5482723b6c75030aff5401b3885900ed4a968a94d7bf2c310f3c278e02c1690
 * stale: false
 * tags: [code/indexing]
 * concepts: [Indexed Collection Access]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IIndexer 
extends IIndex {

	/** Records {@code ndx} as the index of {@code arg}, replacing any previous index.
	 * @param arg the Object to retrieve the Index for.
	 * @param ndx the Index to remember for the given Object.
	 * @return the previous Index of arg, if it had one, -1 otherwise
	 */
	public int setIndexOf(final Object arg, final int ndx);
	
}
