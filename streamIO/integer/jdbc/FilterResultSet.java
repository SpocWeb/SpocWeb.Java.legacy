/*
 * Filters the Rows of a (joined) ResultSet 
 * Created on 13.03.2005
 *
 */
package streamIO.integer.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import streamIO.exception.BaseException;

/**
 * Base class for filtering the rows of a (possibly joined) {@link ResultSet} by wrapping
 * and delegating every {@link ResultSet} method to an underlying {@link #rsIter}. This
 * class itself performs no filtering at all - it returns every row and column unchanged -
 * and exists purely so that subclasses (e.g. {@link ResultSetCrossJoin}, {@link FilterRsCols},
 * {@link FilterRsRows}) can selectively override the methods that need to filter or
 * translate, while inheriting a correct implementation of everything else.
 *
 * <h2>Collaborators</h2>
 *
 * | Type | Relationship |
 * |---|---|
 * | {@link #rsIter} | The wrapped {@link ResultSet} that every unmodified method delegates to. |
 * | {@link AResultSetBase} | Superclass supplying the shared cursor-name/statement bookkeeping. |
 *
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T22:12:06Z
 * digest: 1986594cce9f6a6932437847a92ca74b412a19a263bb6722a60a95f27acfbf0b
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class FilterResultSet
extends AResultSetBase  //
implements ResultSet {

	/** Base ResultSet being iterated.	 */
	protected final ResultSet rsIter;

	/**
	 * Initializing constructor that always builds the field-name array from {@code _rsIter}'s
	 * metadata.
	 * @param _rsIter Base Iterator with all Rows
	 * @throws SQLException when reading {@code _rsIter}'s metadata fails
	 */
	public FilterResultSet(final ResultSet _rsIter) throws SQLException {
		super(_rsIter.getCursorName(), _rsIter.getStatement());
		this.rsIter = _rsIter;
		final ResultSetMetaData rsMeta = _rsIter.getMetaData();
		final String[] fieldNames = new String[rsMeta.getColumnCount()];
		for (int i = fieldNames.length; --i >= 0; )
			fieldNames[i] = rsMeta.getColumnName(i);
		init(fieldNames);
	}

	/**
	 * Initializing constructor letting a subclass skip building the field-name array (e.g.
	 * when it will call {@link #init(int, String[])} itself with a different column layout).
	 * @param _rsIter Base Iterator with all Rows
	 * @param init Flag whether to build the field-name array from {@code _rsIter}'s metadata now
	 * @throws SQLException when reading {@code _rsIter}'s metadata fails
	 */
	protected FilterResultSet(final ResultSet _rsIter, boolean init) throws SQLException {
		super(_rsIter.getCursorName(), _rsIter.getStatement());
		this.rsIter = _rsIter; 
		if (! init)
			return; 
		final ResultSetMetaData rsMeta = _rsIter.getMetaData();
		final String[] fieldNames = new String[rsMeta.getColumnCount()];
		for (int i = fieldNames.length; --i >= 0; )
			fieldNames[i] = rsMeta.getColumnName(i); 
		init(fieldNames);
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// basic Operations
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getString(int)
	 */
	public String getString(final int columnIndex) throws SQLException {
		return rsIter.getString(columnIndex); }

	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateString(int, final java.lang.String)
	 */
	public void updateString(final int columnIndex, final String x) 
	throws SQLException { rsIter.updateString(columnIndex, x); }

	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#close()
	 */
	public void close() throws SQLException { rsIter.close(); }

	///////////////////////////////////////////////////////////////////////////
	/// handling Null Values
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateNull(int)
	 */
	public void updateNull(final int columnIndex) throws SQLException {
		rsIter.updateNull(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#wasNull()
	 */
	public boolean wasNull() throws SQLException { return rsIter.wasNull(); }
	
	///////////////////////////////////////////////////////////////////////////
	/// Navigation
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#absolute(int)
	 */
	public boolean absolute(final int row) throws SQLException { 
		return rsIter.absolute(row); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#relative(int)
	 */
	public boolean relative(final int rows) throws SQLException {
		return rsIter.relative(rows); }

	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#afterLast()
	 */
	public void afterLast() throws SQLException { rsIter.afterLast(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#beforeFirst()
	 */
	public void beforeFirst() throws SQLException { rsIter.beforeFirst(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#first()
	 */
	public boolean first() throws SQLException { return rsIter.first(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#last()
	 */
	public boolean last() throws SQLException { return rsIter.last(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#moveToCurrentRow()
	 */
	public void moveToCurrentRow() throws SQLException { rsIter.moveToCurrentRow(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#moveToInsertRow()
	 */
	public void moveToInsertRow() throws SQLException { rsIter.moveToInsertRow(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#next()
	 */
	public boolean next() throws SQLException { return rsIter.next(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#previous()
	 */
	public boolean previous() throws SQLException { return rsIter.previous(); }

	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#refreshRow()
	 */
	public void refreshRow() throws SQLException { rsIter.refreshRow(); }
	
	///////////////////////////////////////////////////////////////////////////
	/// Query Position
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getRow()
	 */
	public int getRow() throws SQLException {
		return rsIter.getRow(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#isAfterLast()
	 */
	public boolean isAfterLast() throws SQLException { return rsIter.isAfterLast(); }
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#isBeforeFirst()
	 */
	public boolean isBeforeFirst() throws SQLException { return rsIter.isBeforeFirst(); }
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#isFirst()
	 */
	public boolean isFirst() throws SQLException { return rsIter.isFirst(); }
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#isLast()
	 */
	public boolean isLast() throws SQLException { return rsIter.isLast(); }
	
	///////////////////////////////////////////////////////////////////////////
	/// Row Status
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#rowDeleted()
	 */
	public boolean rowDeleted () throws SQLException { return rsIter.rowDeleted(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#rowInserted()
	 */
	public boolean rowInserted() throws SQLException { return rsIter.rowInserted(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#rowUpdated()
	 */
	public boolean rowUpdated () throws SQLException { return rsIter.rowUpdated(); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#deleteRow()
	 */
	public void deleteRow() throws SQLException { rsIter.deleteRow(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#insertRow()
	 */
	public void insertRow() throws SQLException { rsIter.insertRow(); }

	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateRow()
	 */
	public void updateRow() throws SQLException { rsIter.updateRow(); }

	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#cancelRowUpdates()
	 */
	public void cancelRowUpdates() throws SQLException { rsIter.cancelRowUpdates(); }
	
	///////////////////////////////////////////////////////////////////////////
	/// MetaData
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getConcurrency()
	 */
	public int getConcurrency() throws SQLException { return rsIter.getConcurrency(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getCursorName()
	 */
	//public String getCursorName() { rsIter.getCursorName(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getMetaData()
	 */
	//public ResultSetMetaData getMetaData() {
	//	return rsIter.getMetaData(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#findColumn(java.lang.String)
	 */
	public int findColumn(final String columnName) throws SQLException {
		return rsIter.findColumn(columnName); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getStatement()
	 */
	public Statement getStatement() throws SQLException {
		return rsIter.getStatement(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getType()
	 */
	public int getType() throws SQLException { return rsIter.getType(); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#clearWarnings()
	 */
	public void clearWarnings() throws SQLException { rsIter.clearWarnings(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException { return rsIter.getWarnings(); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		return rsIter.getFetchDirection(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#setFetchDirection(int)
	 */
	public void setFetchDirection(final int direction) throws SQLException {
		rsIter.setFetchDirection(direction); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		return rsIter.getFetchSize(); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#setFetchSize(int)
	 */
	public void setFetchSize(final int rows) throws SQLException {
		rsIter.setFetchSize(rows); }
	
	///////////////////////////////////////////////////////////////////////////
	/// getter Routines
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getArray(int)
	 */
	public Array getArray(final int i) throws SQLException {
		return rsIter.getArray(i); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getAsciiStream(int)
	 */
	public InputStream getAsciiStream(final int columnIndex) throws SQLException {
		return rsIter.getAsciiStream(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getBigDecimal(int, final int)
	 * @deprecated due to ResultSet Deprecation
	 */
	public BigDecimal getBigDecimal(final int columnIndex, final int scale)
	throws SQLException { return rsIter.getBigDecimal(columnIndex, scale); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(final int columnIndex) 
	throws SQLException { return rsIter.getBigDecimal(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getBinaryStream(int)
	 */
	public InputStream getBinaryStream(final int columnIndex) 
	throws SQLException { return rsIter.getBinaryStream(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getBlob(int)
	 */
	public Blob getBlob(final int i) throws SQLException { return rsIter.getBlob(i); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getBoolean(int)
	 */
	public boolean getBoolean(final int columnIndex) throws SQLException {
		return rsIter.getBoolean(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getByte(int)
	 */
	public byte getByte(final int columnIndex) throws SQLException {
		return rsIter.getByte(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getBytes(int)
	 */
	public byte[] getBytes(final int columnIndex) throws SQLException {
		return rsIter.getBytes(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getCharacterStream(int)
	 */
	public Reader getCharacterStream(final int columnIndex) throws SQLException {
		return rsIter.getCharacterStream(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getClob(int)
	 */
	public Clob getClob(final int columnIndex) throws SQLException {
		return rsIter.getClob(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getDate(int, final java.util.Calendar)
	 */
	public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
		return rsIter.getDate(columnIndex, cal); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getDate(int)
	 */
	public Date getDate(final int columnIndex) throws SQLException {
		return rsIter.getDate(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getDouble(int)
	 */
	public double getDouble(final int columnIndex) throws SQLException {
		return rsIter.getDouble(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getFloat(int)
	 */
	public float getFloat(final int columnIndex) throws SQLException {
		return rsIter.getFloat(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getInt(int)
	 */
	public int getInt(final int columnIndex) throws SQLException {
		return rsIter.getInt(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getLong(int)
	 */
	public long getLong(final int columnIndex) throws SQLException {
		return rsIter.getLong(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getObject(int, final java.util.Map)
	 */
	public Object getObject(final int columnIndex, final Map map) throws SQLException {
		return rsIter.getObject(columnIndex, map); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getObject(int)
	 */
	public Object getObject(final int columnIndex) throws SQLException {
		return rsIter.getObject(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getRef(int)
	 */
	public Ref getRef(final int columnIndex) throws SQLException {
		return rsIter.getRef(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getShort(int)
	 */
	public short getShort(final int columnIndex) throws SQLException {
		return rsIter.getShort(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getTime(int, final java.util.Calendar)
	 */
	public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
		return rsIter.getTime(columnIndex, cal);
	}
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getTime(int)
	 */
	public Time getTime(final int columnIndex) throws SQLException {
		return rsIter.getTime(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getTimestamp(int, final java.util.Calendar)
	 */
	public Timestamp getTimestamp(final int columnIndex, final Calendar cal)
	throws SQLException { return rsIter.getTimestamp(columnIndex, cal); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getTimestamp(int)
	 */
	public Timestamp getTimestamp(final int columnIndex) throws SQLException {
		return rsIter.getTimestamp(columnIndex); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getUnicodeStream(int)
	 * @deprecated due to ResultSet Deprecation
	 */
	public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
		return rsIter.getUnicodeStream(columnIndex);
	}
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#getURL(int)
	 */
	public URL getURL(final int columnIndex) throws SQLException {
		return rsIter.getURL(columnIndex);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateArray(int, final java.sql.Array)
	 */
	public void updateArray(final int columnIndex, final Array x) throws SQLException {
		rsIter.updateArray(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateAsciiStream(int, final java.io.InputStream, final int)
	 */
	public void updateAsciiStream(final int columnIndex, final InputStream x, final int length)
	throws SQLException { rsIter.updateAsciiStream(columnIndex, x, length); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateBigDecimal(int, final java.math.BigDecimal)
	 */
	public void updateBigDecimal(final int columnIndex, final BigDecimal x)
	throws SQLException { rsIter.updateBigDecimal(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateBinaryStream(int, final java.io.InputStream, final int)
	 */
	public void updateBinaryStream(final int columnIndex, final InputStream x, final int length)
	throws SQLException { rsIter.updateBinaryStream(columnIndex, x, length); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateBlob(int, final java.sql.Blob)
	 */
	public void updateBlob(final int columnIndex, final Blob x) 
	throws SQLException { rsIter.updateBlob(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateBoolean(int, final boolean)
	 */
	public void updateBoolean(final int columnIndex, final boolean x) 
	throws SQLException { rsIter.updateBoolean(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateByte(int, final byte)
	 */
	public void updateByte(final int columnIndex, final byte x) 
	throws SQLException { rsIter.updateByte(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateBytes(int, final byte[])
	 */
	public void updateBytes(final int columnIndex, final byte[] x) 
	throws SQLException { rsIter.updateBytes(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateCharacterStream(int, final java.io.Reader, final int)
	 */
	public void updateCharacterStream(final int columnIndex, final Reader x, final int length)
	throws SQLException { rsIter.updateCharacterStream(columnIndex, x, length); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateClob(int, final java.sql.Clob)
	 */
	public void updateClob(final int columnIndex, final Clob x) throws SQLException {
		rsIter.updateClob(columnIndex, x); }
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateDate(int, final java.sql.Date)
	 */
	public void updateDate(final int columnIndex, final Date x) throws SQLException {
		rsIter.updateDate(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateDouble(int, final double)
	 */
	public void updateDouble(final int columnIndex, final double x) throws SQLException {
		rsIter.updateDouble(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateFloat(int, final float)
	 */
	public void updateFloat(final int columnIndex, final float x) 
	throws SQLException { rsIter.updateFloat(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateInt(int, final int)
	 */
	public void updateInt(final int columnIndex, final int x) 
	throws SQLException { rsIter.updateInt(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateLong(int, final long)
	 */
	public void updateLong(final int columnIndex, final long x) 
	throws SQLException { rsIter.updateLong(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateObject(int, final java.lang.Object, final int)
	 */
	public void updateObject(final int columnIndex, final Object x, final int scale)
	throws SQLException { rsIter.updateObject(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateObject(int, final java.lang.Object)
	 */
	public void updateObject(final int columnIndex, final Object x) 
	throws SQLException { rsIter.updateObject(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateRef(int, final java.sql.Ref)
	 */
	public void updateRef(final int columnIndex, final Ref x) 
	throws SQLException { rsIter.updateRef(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateShort(int, final short)
	 */
	public void updateShort(final int columnIndex, final short x) 
	throws SQLException { rsIter.updateShort(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateTime(int, final java.sql.Time)
	 */
	public void updateTime(final int columnIndex, final Time x) 
	throws SQLException { rsIter.updateTime(columnIndex, x); }
	
	/**
	 * Delegates to the underlying {@link #rsIter}.
	 * @see java.sql.ResultSet#updateTimestamp(int, final java.sql.Timestamp)
	 */
	public void updateTimestamp(final int columnIndex, final Timestamp x) 
	throws SQLException { rsIter.updateTimestamp(columnIndex, x); }
	
	/**
	 * Reuses the underlying {@link #rsIter}'s fetch size as this filter's mark-size limit.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()
	 */
	public long getMaxMarkSize() {
		try { return rsIter.getFetchSize(); 
		} catch (final SQLException x) {
			throw new BaseException(x); 
		}
	}
	
}
