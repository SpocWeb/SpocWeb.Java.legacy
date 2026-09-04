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
 * Base Class for Filters the Rows of a (joined) ResultSet. 
 * This Class does not filter at all, final but returns all Rows and Columns.  
 * @author heuerm
 *
 */
public class FilterResultSet 
extends AResultSetBase  //
implements ResultSet {

	/** Base ResultSet being iterated.	 */
	protected final ResultSet rsIter; 

	/**
	 * Initializing Constructor 
	 * @param _rsIter Base Iterator with all Rows  
	 * @param init Flag 
	 * @throws SQLException  */
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
	 * Initializing Constructor 
	 * @param _rsIter Base Iterator with all Rows  
	 * @param init Flag 
	 * @throws SQLException  */
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
	
	/** @see java.sql.ResultSet#getString(int)	 */
	public String getString(final int columnIndex) throws SQLException {
		return rsIter.getString(columnIndex); }

	/** @see java.sql.ResultSet#updateString(int, final java.lang.String)	 */
	public void updateString(final int columnIndex, final String x) 
	throws SQLException { rsIter.updateString(columnIndex, x); }

	/** @see java.sql.ResultSet#close()	 */
	public void close() throws SQLException { rsIter.close(); }

	///////////////////////////////////////////////////////////////////////////
	/// handling Null Values
	///////////////////////////////////////////////////////////////////////////

	/** @see java.sql.ResultSet#updateNull(int)  */
	public void updateNull(final int columnIndex) throws SQLException {
		rsIter.updateNull(columnIndex); }
	
	/** @see java.sql.ResultSet#wasNull()  */
	public boolean wasNull() throws SQLException { return rsIter.wasNull(); }
	
	///////////////////////////////////////////////////////////////////////////
	/// Navigation
	///////////////////////////////////////////////////////////////////////////
	
	/** @see java.sql.ResultSet#absolute(int)	 */
	public boolean absolute(final int row) throws SQLException { 
		return rsIter.absolute(row); }
	
	/** @see java.sql.ResultSet#relative(int)	 */
	public boolean relative(final int rows) throws SQLException {
		return rsIter.relative(rows); }

	/** @see java.sql.ResultSet#afterLast()	 */
	public void afterLast() throws SQLException { rsIter.afterLast(); }
	
	/** @see java.sql.ResultSet#beforeFirst()	 */
	public void beforeFirst() throws SQLException { rsIter.beforeFirst(); }
	
	/** @see java.sql.ResultSet#first()	 */
	public boolean first() throws SQLException { return rsIter.first(); }
	
	/** @see java.sql.ResultSet#last()  */
	public boolean last() throws SQLException { return rsIter.last(); }
	
	/** @see java.sql.ResultSet#moveToCurrentRow()  */
	public void moveToCurrentRow() throws SQLException { rsIter.moveToCurrentRow(); }
	
	/** @see java.sql.ResultSet#moveToInsertRow()  */
	public void moveToInsertRow() throws SQLException { rsIter.moveToInsertRow(); }
	
	/** @see java.sql.ResultSet#next()  */
	public boolean next() throws SQLException { return rsIter.next(); }
	
	/** @see java.sql.ResultSet#previous()  */
	public boolean previous() throws SQLException { return rsIter.previous(); }

	/** @see java.sql.ResultSet#refreshRow()  */
	public void refreshRow() throws SQLException { rsIter.refreshRow(); }
	
	///////////////////////////////////////////////////////////////////////////
	/// Query Position
	///////////////////////////////////////////////////////////////////////////
	
	/** @see java.sql.ResultSet#getRow()  */
	public int getRow() throws SQLException {
		return rsIter.getRow(); }
	
	/** @see java.sql.ResultSet#isAfterLast()  */
	public boolean isAfterLast() throws SQLException { return rsIter.isAfterLast(); }
	/** @see java.sql.ResultSet#isBeforeFirst()  */
	public boolean isBeforeFirst() throws SQLException { return rsIter.isBeforeFirst(); }
	/** @see java.sql.ResultSet#isFirst()  */
	public boolean isFirst() throws SQLException { return rsIter.isFirst(); }
	/** @see java.sql.ResultSet#isLast()  */
	public boolean isLast() throws SQLException { return rsIter.isLast(); }
	
	///////////////////////////////////////////////////////////////////////////
	/// Row Status
	///////////////////////////////////////////////////////////////////////////
	
	/** @see java.sql.ResultSet#rowDeleted()  */
	public boolean rowDeleted () throws SQLException { return rsIter.rowDeleted(); }
	
	/** @see java.sql.ResultSet#rowInserted()  */
	public boolean rowInserted() throws SQLException { return rsIter.rowInserted(); }
	
	/** @see java.sql.ResultSet#rowUpdated()  */
	public boolean rowUpdated () throws SQLException { return rsIter.rowUpdated(); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** @see java.sql.ResultSet#deleteRow()	 */
	public void deleteRow() throws SQLException { rsIter.deleteRow(); }
	
	/** @see java.sql.ResultSet#insertRow()	 */
	public void insertRow() throws SQLException { rsIter.insertRow(); }

	/** @see java.sql.ResultSet#updateRow()	 */
	public void updateRow() throws SQLException { rsIter.updateRow(); }

	/** @see java.sql.ResultSet#cancelRowUpdates()	 */
	public void cancelRowUpdates() throws SQLException { rsIter.cancelRowUpdates(); }
	
	///////////////////////////////////////////////////////////////////////////
	/// MetaData
	///////////////////////////////////////////////////////////////////////////
	
	/** @see java.sql.ResultSet#getConcurrency()  */
	public int getConcurrency() throws SQLException { return rsIter.getConcurrency(); }
	
	/** @see java.sql.ResultSet#getCursorName()  */
	//public String getCursorName() { rsIter.getCursorName(); }
	
	/** @see java.sql.ResultSet#getMetaData()  */
	//public ResultSetMetaData getMetaData() {
	//	return rsIter.getMetaData(); }
	
	/** @see java.sql.ResultSet#findColumn(java.lang.String)	 */
	public int findColumn(final String columnName) throws SQLException {
		return rsIter.findColumn(columnName); }
	
	/** @see java.sql.ResultSet#getStatement()  */
	public Statement getStatement() throws SQLException {
		return rsIter.getStatement(); }
	
	/** @see java.sql.ResultSet#getType()  */
	public int getType() throws SQLException { return rsIter.getType(); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** @see java.sql.ResultSet#clearWarnings()	 */
	public void clearWarnings() throws SQLException { rsIter.clearWarnings(); }
	
	/** @see java.sql.ResultSet#getWarnings()  */
	public SQLWarning getWarnings() throws SQLException { return rsIter.getWarnings(); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** @see java.sql.ResultSet#getFetchDirection()  */
	public int getFetchDirection() throws SQLException {
		return rsIter.getFetchDirection(); }
	
	/** @see java.sql.ResultSet#setFetchDirection(int)  */
	public void setFetchDirection(final int direction) throws SQLException {
		rsIter.setFetchDirection(direction); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** @see java.sql.ResultSet#getFetchSize()  */
	public int getFetchSize() throws SQLException {
		return rsIter.getFetchSize(); }
	
	/** @see java.sql.ResultSet#setFetchSize(int)  */
	public void setFetchSize(final int rows) throws SQLException {
		rsIter.setFetchSize(rows); }
	
	///////////////////////////////////////////////////////////////////////////
	/// getter Routines
	///////////////////////////////////////////////////////////////////////////
	
	/** @see java.sql.ResultSet#getArray(int)	 */
	public Array getArray(final int i) throws SQLException {
		return rsIter.getArray(i); }
	
	/** @see java.sql.ResultSet#getAsciiStream(int)	 */
	public InputStream getAsciiStream(final int columnIndex) throws SQLException {
		return rsIter.getAsciiStream(columnIndex); }
	
	/** @see java.sql.ResultSet#getBigDecimal(int, final int)
	 * @deprecated due to ResultSet Deprecation
	 */
	public BigDecimal getBigDecimal(final int columnIndex, final int scale)
	throws SQLException { return rsIter.getBigDecimal(columnIndex, scale); }
	
	/** @see java.sql.ResultSet#getBigDecimal(int)	 */
	public BigDecimal getBigDecimal(final int columnIndex) 
	throws SQLException { return rsIter.getBigDecimal(columnIndex); }
	
	/** @see java.sql.ResultSet#getBinaryStream(int)	 */
	public InputStream getBinaryStream(final int columnIndex) 
	throws SQLException { return rsIter.getBinaryStream(columnIndex); }
	
	/** @see java.sql.ResultSet#getBlob(int)	 */
	public Blob getBlob(final int i) throws SQLException { return rsIter.getBlob(i); }
	
	/** @see java.sql.ResultSet#getBoolean(int)	 */
	public boolean getBoolean(final int columnIndex) throws SQLException {
		return rsIter.getBoolean(columnIndex); }
	
	/** @see java.sql.ResultSet#getByte(int)	 */
	public byte getByte(final int columnIndex) throws SQLException {
		return rsIter.getByte(columnIndex); }
	
	/** @see java.sql.ResultSet#getBytes(int)	 */
	public byte[] getBytes(final int columnIndex) throws SQLException {
		return rsIter.getBytes(columnIndex); }
	
	/** @see java.sql.ResultSet#getCharacterStream(int)  */
	public Reader getCharacterStream(final int columnIndex) throws SQLException {
		return rsIter.getCharacterStream(columnIndex); }
	
	/** @see java.sql.ResultSet#getClob(int)  */
	public Clob getClob(final int columnIndex) throws SQLException {
		return rsIter.getClob(columnIndex); }
	
	/** @see java.sql.ResultSet#getDate(int, final java.util.Calendar)  */
	public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
		return rsIter.getDate(columnIndex, cal); }
	
	/** @see java.sql.ResultSet#getDate(int)  */
	public Date getDate(final int columnIndex) throws SQLException {
		return rsIter.getDate(columnIndex); }
	
	/** @see java.sql.ResultSet#getDouble(int)  */
	public double getDouble(final int columnIndex) throws SQLException {
		return rsIter.getDouble(columnIndex); }
	
	/** @see java.sql.ResultSet#getFloat(int)  */
	public float getFloat(final int columnIndex) throws SQLException {
		return rsIter.getFloat(columnIndex); }
	
	/** @see java.sql.ResultSet#getInt(int)  */
	public int getInt(final int columnIndex) throws SQLException {
		return rsIter.getInt(columnIndex); }
	
	/** @see java.sql.ResultSet#getLong(int)  */
	public long getLong(final int columnIndex) throws SQLException {
		return rsIter.getLong(columnIndex); }
	
	/** @see java.sql.ResultSet#getObject(int, final java.util.Map)  */
	public Object getObject(final int columnIndex, final Map map) throws SQLException {
		return rsIter.getObject(columnIndex, map); }
	
	/** @see java.sql.ResultSet#getObject(int)  */
	public Object getObject(final int columnIndex) throws SQLException {
		return rsIter.getObject(columnIndex); }
	
	/** @see java.sql.ResultSet#getRef(int)  */
	public Ref getRef(final int columnIndex) throws SQLException {
		return rsIter.getRef(columnIndex); }
	
	/** @see java.sql.ResultSet#getShort(int)  */
	public short getShort(final int columnIndex) throws SQLException {
		return rsIter.getShort(columnIndex); }
	
	/** @see java.sql.ResultSet#getTime(int, final java.util.Calendar)  */
	public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
		return rsIter.getTime(columnIndex, cal);
	}
	/** @see java.sql.ResultSet#getTime(int)  */
	public Time getTime(final int columnIndex) throws SQLException {
		return rsIter.getTime(columnIndex); }
	
	/** @see java.sql.ResultSet#getTimestamp(int, final java.util.Calendar)  */
	public Timestamp getTimestamp(final int columnIndex, final Calendar cal)
	throws SQLException { return rsIter.getTimestamp(columnIndex, cal); }
	
	/** @see java.sql.ResultSet#getTimestamp(int)  */
	public Timestamp getTimestamp(final int columnIndex) throws SQLException {
		return rsIter.getTimestamp(columnIndex); }
	
	/** @see java.sql.ResultSet#getUnicodeStream(int)  
	 * @deprecated due to ResultSet Deprecation
	 */
	public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
		return rsIter.getUnicodeStream(columnIndex);
	}
	/** @see java.sql.ResultSet#getURL(int)  */
	public URL getURL(final int columnIndex) throws SQLException {
		return rsIter.getURL(columnIndex);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** @see java.sql.ResultSet#updateArray(int, final java.sql.Array)  */
	public void updateArray(final int columnIndex, final Array x) throws SQLException {
		rsIter.updateArray(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateAsciiStream(int, final java.io.InputStream, final int)  */
	public void updateAsciiStream(final int columnIndex, final InputStream x, final int length)
	throws SQLException { rsIter.updateAsciiStream(columnIndex, x, length); }
	
	/** @see java.sql.ResultSet#updateBigDecimal(int, final java.math.BigDecimal)  */
	public void updateBigDecimal(final int columnIndex, final BigDecimal x)
	throws SQLException { rsIter.updateBigDecimal(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateBinaryStream(int, final java.io.InputStream, final int)  */
	public void updateBinaryStream(final int columnIndex, final InputStream x, final int length)
	throws SQLException { rsIter.updateBinaryStream(columnIndex, x, length); }
	
	/** @see java.sql.ResultSet#updateBlob(int, final java.sql.Blob)  */
	public void updateBlob(final int columnIndex, final Blob x) 
	throws SQLException { rsIter.updateBlob(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateBoolean(int, final boolean)  */
	public void updateBoolean(final int columnIndex, final boolean x) 
	throws SQLException { rsIter.updateBoolean(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateByte(int, final byte)  */
	public void updateByte(final int columnIndex, final byte x) 
	throws SQLException { rsIter.updateByte(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateBytes(int, final byte[])  */
	public void updateBytes(final int columnIndex, final byte[] x) 
	throws SQLException { rsIter.updateBytes(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateCharacterStream(int, final java.io.Reader, final int)  */
	public void updateCharacterStream(final int columnIndex, final Reader x, final int length)
	throws SQLException { rsIter.updateCharacterStream(columnIndex, x, length); }
	
	/** @see java.sql.ResultSet#updateClob(int, final java.sql.Clob)  */
	public void updateClob(final int columnIndex, final Clob x) throws SQLException {
		rsIter.updateClob(columnIndex, x); }
	/** @see java.sql.ResultSet#updateDate(int, final java.sql.Date)  */
	public void updateDate(final int columnIndex, final Date x) throws SQLException {
		rsIter.updateDate(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateDouble(int, final double)  */
	public void updateDouble(final int columnIndex, final double x) throws SQLException {
		rsIter.updateDouble(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateFloat(int, final float)  */
	public void updateFloat(final int columnIndex, final float x) 
	throws SQLException { rsIter.updateFloat(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateInt(int, final int)  */
	public void updateInt(final int columnIndex, final int x) 
	throws SQLException { rsIter.updateInt(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateLong(int, final long)  */
	public void updateLong(final int columnIndex, final long x) 
	throws SQLException { rsIter.updateLong(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateObject(int, final java.lang.Object, final int)  */
	public void updateObject(final int columnIndex, final Object x, final int scale)
	throws SQLException { rsIter.updateObject(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateObject(int, final java.lang.Object)  */
	public void updateObject(final int columnIndex, final Object x) 
	throws SQLException { rsIter.updateObject(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateRef(int, final java.sql.Ref)  */
	public void updateRef(final int columnIndex, final Ref x) 
	throws SQLException { rsIter.updateRef(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateShort(int, final short)  */
	public void updateShort(final int columnIndex, final short x) 
	throws SQLException { rsIter.updateShort(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateTime(int, final java.sql.Time)  */
	public void updateTime(final int columnIndex, final Time x) 
	throws SQLException { rsIter.updateTime(columnIndex, x); }
	
	/** @see java.sql.ResultSet#updateTimestamp(int, final java.sql.Timestamp)  */
	public void updateTimestamp(final int columnIndex, final Timestamp x) 
	throws SQLException { rsIter.updateTimestamp(columnIndex, x); }
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { 
		try { return rsIter.getFetchSize(); 
		} catch (final SQLException x) {
			throw new BaseException(x); 
		}
	}
	
}
