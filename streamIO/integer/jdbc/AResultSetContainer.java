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
 * Implements the ResultSet Interface for a Container 
 * @author heuerm
 *
 */
public abstract class AResultSetContainer 
extends AResultSet {

	/**
	 * @param _cursorName
	 * @param _statement
	 */
	public AResultSetContainer(final String _cursorName, final Statement _statement) {
		super(_cursorName, _statement);
	}

	/**
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
	 * @param _fieldNames
	 * @param _cursorName
	 * @param _statement
	 */
	public AResultSetContainer(final String[] _fieldNames, final String _cursorName,
			final Statement _statement) {
		super(_fieldNames, _cursorName, _statement);
	}
	
	/**
	 * @param _cols
	 * @param _cursorName
	 * @param _statement
	 */
	public AResultSetContainer(final DbColumn[] _cols, final String _cursorName,
			final Statement _statement) {
		super(_cols, _cursorName, _statement);
	}
	
	/** @see streamIO.integer.jdbc.AResultSet#refreshRow()	 */
	public void refreshRow() throws SQLException {
		throw new SQLException("Not possible, all Changes immediately performed!"); } //
	
	/** @see streamIO.integer.jdbc.AResultSet#moveToCurrentRow()	 */
	public void moveToCurrentRow() { } //never leaves the current Row
	
	/** @see streamIO.integer.jdbc.AResultSet#moveToInsertRow()	 */
	public void moveToInsertRow() {	} //can be inserted anytime
	
	/**empty since everything happens synchronously in RAM 
	 * but setting the Container to null to allow for Garbage Collection
	 * @see java.sql.ResultSet#close()	 */
	public void close() { }
	
	/**empty since everything happens synchronously in RAM 
	 * @see java.sql.ResultSet#updateRow()	 */
	public void updateRow() { }
	
	/** @see streamIO.IMarkAble#getMaxMarkSize()	 */
	abstract public long getMaxMarkSize(); 
	
	/** @see java.sql.ResultSet#isAfterLast()	 */
	abstract public boolean isAfterLast() throws SQLException; 
	
	/** @see java.sql.ResultSet#getString(int)	 */
	abstract public String getString(final int columnIndex);
	
	/** @see java.sql.ResultSet#relative(int)	 */
	abstract public boolean relative(final int rows) throws SQLException;
	
	/** @see java.sql.ResultSet#insertRow()	 */
	abstract public void insertRow() throws SQLException;
	
	/** @see streamIO.integer.jdbc.AResultSet#readNext()	 */
	abstract protected boolean readNext() throws SQLException;
	
	/** @see java.sql.ResultSet#updateString(int, java.lang.String)	 */
	abstract public void updateString(final int columnIndex, final String x) 
	throws SQLException;
	
}
