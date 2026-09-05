/*
 * Created on 17.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.jdbc;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abstract {@link AResultSet} specialization for an in-memory container backing store, where
 * every change is applied immediately rather than buffered until {@code updateRow()}.
 *
 * <h2>Collaborators</h2>
 *
 * | Type | Relationship |
 * |---|---|
 * | {@link AResultSet} | Superclass providing the shared {@link ResultSet} machinery. |
 * | {@link DbColumn} | Column descriptors accepted by one of the constructors. |
 *
 * @author heuerm
 * @see AResultSet the superclass
 * @see DbColumn
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:48:19Z
 * digest: 83b6ed726cf82c4130920f1a133bc925e0e8c52a74604c264e6210502923a117
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public abstract class AResultSetContainer
extends AResultSet {

	/**
	 * Initializing constructor deriving columns from field names only.
	 * @param _cursorName
	 * @param _statement
	 */
	public AResultSetContainer(final String _cursorName, final Statement _statement) {
		super(_cursorName, _statement);
	}

	/**
	 * Initializing constructor with an explicit column count.
	 * @param _fieldNames
	 * @param _numCols
	 * @param _cursorName
	 * @param _statement
	 */
	public AResultSetContainer(final String[] _fieldNames, final int _numCols,
			final String _cursorName, final Statement _statement) {
		super(_fieldNames, _numCols, _cursorName, _statement);
	}

	/**
	 * Initializing constructor deriving the column count from {@code _fieldNames}.
	 * @param _fieldNames
	 * @param _cursorName
	 * @param _statement
	 */
	public AResultSetContainer(final String[] _fieldNames, final String _cursorName,
			final Statement _statement) {
		super(_fieldNames, _cursorName, _statement);
	}

	/**
	 * Initializing constructor accepting pre-built {@link DbColumn} descriptors.
	 * @param _cols
	 * @param _cursorName
	 * @param _statement
	 */
	public AResultSetContainer(final DbColumn[] _cols, final String _cursorName,
			final Statement _statement) {
		super(_cols, _cursorName, _statement);
	}

	/**
	 * Always throws, since every change to an in-memory container is already applied
	 * immediately and there is nothing to refresh from.
	 * @see streamIO.integer.jdbc.AResultSet#refreshRow()
	 */
	public void refreshRow() throws SQLException {
		throw new SQLException("Not possible, all Changes immediately performed!"); } //

	/**
	 * No-op, since this result set never leaves the current row.
	 * @see streamIO.integer.jdbc.AResultSet#moveToCurrentRow()
	 */
	public void moveToCurrentRow() { } //never leaves the current Row

	/**
	 * No-op, since a row can be inserted at any position at any time.
	 * @see streamIO.integer.jdbc.AResultSet#moveToInsertRow()
	 */
	public void moveToInsertRow() {	} //can be inserted anytime
	
	/**empty since everything happens synchronously in RAM 
	 * but setting the Container to null to allow for Garbage Collection
	 * @see java.sql.ResultSet#close()	 */
	public void close() { }
	
	/**empty since everything happens synchronously in RAM 
	 * @see java.sql.ResultSet#updateRow()	 */
	public void updateRow() { }
	
	/**
	 * Returns the maximum number of items a mark can span; left to the concrete container.
	 * @see streamIO.IMarkAble#getMaxMarkSize()
	 */
	abstract public long getMaxMarkSize();

	/**
	 * Reports whether the cursor is positioned after the last row; left to the concrete
	 * container.
	 * @see java.sql.ResultSet#isAfterLast()
	 */
	abstract public boolean isAfterLast() throws SQLException;

	/**
	 * Gets the designated column's value as a {@code String}; left to the concrete container.
	 * @see java.sql.ResultSet#getString(int)
	 */
	abstract public String getString(final int columnIndex);

	/**
	 * Moves the cursor a relative number of rows; left to the concrete container.
	 * @see java.sql.ResultSet#relative(int)
	 */
	abstract public boolean relative(final int rows) throws SQLException;

	/**
	 * Inserts the contents of the insert row; left to the concrete container.
	 * @see java.sql.ResultSet#insertRow()
	 */
	abstract public void insertRow() throws SQLException;

	/**
	 * Reads and returns whether a further row is available; left to the concrete container.
	 * @see streamIO.integer.jdbc.AResultSet#readNext()
	 */
	abstract protected boolean readNext() throws SQLException;

	/**
	 * Updates the designated column of the current or insert row; left to the concrete
	 * container.
	 * @see java.sql.ResultSet#updateString(int, java.lang.String)
	 */
	abstract public void updateString(final int columnIndex, final String x)
	throws SQLException;
	
}
