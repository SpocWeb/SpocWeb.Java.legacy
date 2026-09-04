package streamIO.integer.jdbc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;


public abstract class APrepStatement
extends AStatement
implements PreparedStatement {

	protected int rsHoldability;

	protected String sql;

	/** @see streamIO.integer.jdbc.AStatement#getResultSet(java.io.File, java.lang.String)	 */
	protected abstract ResultSet getResultSet(File table, String tableName) 
	throws SQLException, IOException; 

	/** Constructor 	 */
	public APrepStatement(
		final AConnection conn,
		final String sql_,
		final int resultSetType,
		final int resultSetConcurrency,
		final int resultSetHoldability) {
		super(conn, resultSetType, resultSetConcurrency);
		this.rsHoldability = resultSetHoldability;
		this.sql = sql_; 
	}

	/** Constructor 	 */
	public APrepStatement(
		final AConnection conn,
		final String sql,
		final int resultSetType,
		final int resultSetConcurrency) {
		this(
			conn,
			sql,
			resultSetType,
			resultSetConcurrency,
			resultSetHoldabilityDefault);
	}

	/** Constructor 	 */
	public APrepStatement(final AConnection conn, final String sql) {
		this(
			conn,
			sql,
			resultSetTypeDefault,
			resultSetConcurrencyDefault,
			resultSetHoldabilityDefault);
	}

	/** @see java.sql.PreparedStatement#executeQuery()	 */
	public ResultSet executeQuery() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.PreparedStatement#executeUpdate()	 */
	public int executeUpdate() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/** @see java.sql.PreparedStatement#setNull(int, int)	 */
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setBoolean(int, boolean)	 */
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setByte(int, byte)	 */
	public void setByte(int parameterIndex, byte x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setShort(int, short)	 */
	public void setShort(int parameterIndex, short x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setInt(int, int)	 */
	public void setInt(int parameterIndex, int x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setLong(int, long)	 */
	public void setLong(int parameterIndex, long x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setFloat(int, float)	 */
	public void setFloat(int parameterIndex, float x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setDouble(int, double)	 */
	public void setDouble(int parameterIndex, double x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)	 */
	public void setBigDecimal(int parameterIndex, BigDecimal x)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setString(int, java.lang.String)	 */
	public void setString(int parameterIndex, String x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setBytes(int, byte[])	 */
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setDate(int, java.sql.Date)	 */
	public void setDate(int parameterIndex, Date x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setTime(int, java.sql.Time)	 */
	public void setTime(int parameterIndex, Time x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)	 */
	public void setTimestamp(int parameterIndex, Timestamp x)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)	 */
	public void setAsciiStream(int parameterIndex, InputStream x, int length)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)	 */
	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, int)	 */
	public void setBinaryStream(int parameterIndex, InputStream x, int length)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#clearParameters()	 */
	public void clearParameters() throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int, int)	 */
	public void setObject(
		int parameterIndex,
		Object x,
		int targetSqlType,
		int scale)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setObject(int, java.lang.Object)	 */
	public void setObject(int parameterIndex, Object x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#execute()	 */
	public boolean execute() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/** @see java.sql.PreparedStatement#addBatch()	 */
	public void addBatch() throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)	 */
	public void setCharacterStream(
		int parameterIndex,
		Reader reader,
		int length)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)	 */
	public void setRef(int i, Ref x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)	 */
	public void setBlob(int i, Blob x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)	 */
	public void setClob(int i, Clob x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setArray(int, java.sql.Array)	 */
	public void setArray(int i, Array x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#getMetaData()	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.PreparedStatement#setDate(int, java.sql.Date, java.util.Calendar)	 */
	public void setDate(int parameterIndex, Date x, Calendar cal)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setTime(int, java.sql.Time, java.util.Calendar)	 */
	public void setTime(int parameterIndex, Time x, Calendar cal)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp, java.util.Calendar)	 */
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)	 */
	public void setNull(int paramIndex, int sqlType, String typeName)
		throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#setURL(int, java.net.URL)	 */
	public void setURL(int parameterIndex, URL x) throws SQLException {
		// TODO Auto-generated method stub

	}

	/** @see java.sql.PreparedStatement#getParameterMetaData()	 */
	public ParameterMetaData getParameterMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, long)	 */
	public void setAsciiStream(int arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream)	 */
	public void setAsciiStream(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, long)	 */
	public void setBinaryStream(int arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream)	 */
	public void setBinaryStream(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream, long)	 */
	public void setBlob(int arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream)	 */
	public void setBlob(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, long)	 */
	public void setCharacterStream(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader)	 */
	public void setCharacterStream(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setClob(int, java.io.Reader, long)	 */
	public void setClob(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setClob(int, java.io.Reader)
	 */
	public void setClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader, long)	 */
	public void setNCharacterStream(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader)	 */
	public void setNCharacterStream(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setNClob(int, java.sql.NClob)	 */
	public void setNClob(int arg0, NClob arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setNClob(int, java.io.Reader, long)	 */
	public void setNClob(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setNClob(int, java.io.Reader)	 */
	public void setNClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setNString(int, java.lang.String)	 */
	public void setNString(int arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setRowId(int, java.sql.RowId)	 */
	public void setRowId(int arg0, RowId arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.PreparedStatement#setSQLXML(int, java.sql.SQLXML)	 */
	public void setSQLXML(int arg0, SQLXML arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
