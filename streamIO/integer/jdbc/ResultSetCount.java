/*
 * Created on 21.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.jdbc;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * ResultSet to return a single Number (the Number of Rows found / deleted / modified)
 * @author heuerm
 *
 */
public class ResultSetCount 
extends AResultSetContainer {

	/** the Value returned 	 */
	final public long count; 
	
	/**
	 * @param statement_
	 */
	public ResultSetCount(final long _count, final String _cursorName, final Statement _statement) {
		super(_cursorName, _statement);
		this.count = _count;
		this.readOnly = true; 
	}

	/** @see java.sql.ResultSet#getString(int)	 */
	public String getString(final int columnIndex) {
		if (columnIndex > 0)
			return null;
		return Long.toString(count);
	}

	/** @see java.sql.ResultSet#getLong(int)	 */
	public long getLong(final int columnIndex) {
		if (columnIndex > 0)
			return Long.MIN_VALUE; 
		return count;
	}
	
	/** @see java.sql.ResultSet#getLong(int)	 */
	public double getDouble(final int columnIndex) {
		if (columnIndex > 0)
			return Double.NaN;
		return count;
	}
	
	/** @see java.sql.ResultSet#relative(int)	 */
	public boolean relative(final int rows) { return false;	}

	/** @see streamIO.integer.jdbc.AResultSet#readNext()	 */
	protected boolean readNext() { return false; }

	/** @see streamIO.integer.jdbc.AResultSetContainer#isAfterLast()	 */
	public boolean isAfterLast() { return false; }

	/** @see streamIO.object.IStreamIn#currItem()	 */
	public Object currItem() { return this;	}

	/** @see java.sql.ResultSet#insertRow()	 */
	public void insertRow() throws SQLException {
		throw new SQLException("Not well defined to insert Rows into an Aggregation like Count() !"); 
	}

	/** @see java.sql.ResultSet#updateString(int, java.lang.String)	 */
	public void updateString(final int columnIndex, final String x) throws SQLException {
		throw new SQLException("Not well defined to update an Aggregation like Count() !");
	}

	/** @see streamIO.integer.jdbc.AResultSetContainer#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }

}
