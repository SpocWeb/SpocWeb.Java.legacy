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
 * Read-only, single-row, single-column {@link AResultSetContainer} wrapping a single
 * already-computed number (the count of rows found, deleted or modified by a statement).
 *
 * @author heuerm
 * @see AResultSetContainer the superclass
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T22:03:23Z
 * digest: 78aab601b17f7c02a48ce1691ab7ec75c27b246f79ac134a6b29aa50d463b286
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class ResultSetCount
extends AResultSetContainer {

	/** the Value returned 	 */
	final public long count; 
	
	/**
	 * Initializing constructor wrapping a single already-computed count as a read-only,
	 * single-row, single-column result set.
	 * @param _count the aggregate value to expose
	 * @param _cursorName
	 * @param _statement
	 */
	public ResultSetCount(final long _count, final String _cursorName, final Statement _statement) {
		super(_cursorName, _statement);
		this.count = _count;
		this.readOnly = true;
	}

	/**
	 * Returns {@link #count} as a string for column 0, {@code null} for any other column.
	 * @see java.sql.ResultSet#getString(int)
	 */
	public String getString(final int columnIndex) {
		if (columnIndex > 0)
			return null;
		return Long.toString(count);
	}

	/**
	 * Returns {@link #count} for column 0, {@link Long#MIN_VALUE} for any other column.
	 * @see java.sql.ResultSet#getLong(int)
	 */
	public long getLong(final int columnIndex) {
		if (columnIndex > 0)
			return Long.MIN_VALUE;
		return count;
	}

	/**
	 * Returns {@link #count} for column 0, {@link Double#NaN} for any other column.
	 * @see java.sql.ResultSet#getLong(int)
	 */
	public double getDouble(final int columnIndex) {
		if (columnIndex > 0)
			return Double.NaN;
		return count;
	}

	/**
	 * Always returns {@code false}; this single-row result set never moves.
	 * @see java.sql.ResultSet#relative(int)
	 */
	public boolean relative(final int rows) { return false;	}

	/**
	 * Always returns {@code false}; there is only ever the one synthetic row.
	 * @see streamIO.integer.jdbc.AResultSet#readNext()
	 */
	protected boolean readNext() { return false; }

	/**
	 * Always returns {@code false}; the single synthetic row is never past the end.
	 * @see streamIO.integer.jdbc.AResultSetContainer#isAfterLast()
	 */
	public boolean isAfterLast() { return false; }

	/**
	 * Returns this instance itself, since it already exposes {@link #count} directly.
	 * @see streamIO.object.IStreamIn#currItem()
	 */
	public Object currItem() { return this;	}

	/**
	 * Always throws, since inserting into an aggregate like {@code COUNT()} is undefined.
	 * @throws SQLException always
	 * @see java.sql.ResultSet#insertRow()
	 */
	public void insertRow() throws SQLException {
		throw new SQLException("Not well defined to insert Rows into an Aggregation like Count() !");
	}

	/**
	 * Always throws, since updating an aggregate like {@code COUNT()} is undefined.
	 * @throws SQLException always
	 * @see java.sql.ResultSet#updateString(int, java.lang.String)
	 */
	public void updateString(final int columnIndex, final String x) throws SQLException {
		throw new SQLException("Not well defined to update an Aggregation like Count() !");
	}

	/**
	 * Always returns {@link Long#MAX_VALUE}; a single synthetic row imposes no mark-size limit.
	 * @see streamIO.integer.jdbc.AResultSetContainer#getMaxMarkSize()
	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }

}
