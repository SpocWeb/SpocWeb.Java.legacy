/*
 * Created on 12.02.2006
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
 * mtime: 2026-09-05T16:41:09Z
 * digest: 05dcf91b42651492158fa027ecab49c1e7302b950a6978a4fa76e3f21da4515c
 * stale: false
 * tags: [code/indexing]
 * concepts: [Indexed Collection Access]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IIndex {

	/** Returns the index at which {@code arg} is stored.
	 * @param arg the Object to retrieve the Index for
	 * @return the Index of arg (typically in an Array, but may also be a File Offset)
	 */
	public int getIndexOf(final Object arg);
	
}
