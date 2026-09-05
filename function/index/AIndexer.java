/*
 * Created on 16.02.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package function.index;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Abstract {@link IIndexer} that adds a bulk {@link #update(ResultSet, int[])} helper for
 * indexing a JDBC {@link ResultSet} by one or more columns.
 *
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
 * mtime: 2026-09-05T16:41:04Z
 * digest: 0c441140a5630e8953cc549451e9b2f5ad29863bc5aaa89f1bafae6684e93bf9
 * stale: false
 * tags: [code/indexing]
 * concepts: [Indexed Collection Access]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
abstract public class AIndexer
implements IIndexer {

	/** Records the index at which {@code arg} occurs.
	 * @see function.index.IIndexer#setIndexOf(java.lang.Object, int)	 */
	abstract public int setIndexOf(Object arg, int ndx);

	/** Returns the index previously recorded for {@code arg}.
	 * @see function.index.IIndex#getIndexOf(java.lang.Object)  */
	abstract public int getIndexOf(Object arg);
	
	/**
	 * creates an Index on the given ResultSet
	 * @param _rsFind the ResultSet to index 
	 * @param _colFind the columns to index by 
	 * @return the Number of Rows indexed 
	 * @throws SQLException when accessing the ResultSet fails
	 */
	final public int update(final ResultSet rsFind, final int[] colFind) throws SQLException {
		int ret = 0; 
		final StringBuffer buf = new StringBuffer(); 
		for(rsFind.beforeFirst(); rsFind.next(); ++ret) { //initialize the Index now
			buf.setLength(0); 
			for (int i =-1; ++i < colFind.length; ) 
				buf.append(rsFind.getString(colFind[i])); //Sequence is Precedence
			setIndexOf(buf.toString(), rsFind.getRow()); 
		}
		return ret; 
	}

}
