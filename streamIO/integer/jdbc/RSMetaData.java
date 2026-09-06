package streamIO.integer.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
//import java.sql.SQLException;

/**
 * Implements {@link ResultSetMetaData}, sharing the field names and optionally the field
 * sizes with the {@link ResultSet} it describes; similar in role to
 * {@code org.xml.sax.Attributes}.
 *
 * <p><b>CONTRACT DEVIATION: Column Numbers are 0-based here, not 1-based.</b>
 * {@link ResultSetMetaData} documents "the first column is 1" on every Method,
 * but this Implementation - like the whole ResultSet Family of this Package,
 * whose {@code AResultSet.findColumn(String)} returns the raw 0-based Index -
 * addresses the first Column as 0 and the last as {@link #getColumnCount()}-1.
 * Pass the Value returned by {@code findColumn(String)} unchanged;
 * do not add 1 to it. An out-of-range Column Number raises an
 * {@link IndexOutOfBoundsException} naming the valid Range,
 * instead of a bare ArrayIndexOutOfBoundsException.
 * Converting this Class alone to 1-based would silently break every
 * Caller in this Package, so the Deviation is documented rather than changed.</p>
 *
 * @see DbColumn the column descriptors backing this metadata
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:58:21Z
 * digest: 03901f6006700e8aedd29b46b9bbe98300821ff329183bf816349e95f4f0c687
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class RSMetaData
implements ResultSetMetaData {

	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** The List of intended Field Types represented as java Classes */ 
	final protected DbColumn[] columns; //   

	/** can be changed to support the variable-length ResultSetSep 	 */
	public int columnCount; 
	
	/** Initializing Constructor */
	public RSMetaData(final DbColumn[] _columns) {
		this.columns = _columns;
		columnCount = _columns.length;
	}

	/**
	 * Validates a Column Number against this Class' 0-based Contract
	 * (see the Class Comment: the first Column is 0, not 1).
	 * @param column the 0-based Column Number
	 * @return the same Number, usable as an Index into {@link #columns}
	 * @throws IndexOutOfBoundsException when the Column Number is out of Range
	 */
	protected int checkColumn(final int column) {
		if ((column < 0) || (column >= columnCount))
			throw new IndexOutOfBoundsException("Column Number "+column
				+" out of Range; this ResultSetMetaData is 0-based, valid are 0.."+(columnCount-1));
		return column;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface ResultSetMetaData : Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/**
	  *	Returns the number of columns in this ResultSet object.
	  * @return the number of columns
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getColumnCount()
	  */
	public int getColumnCount() { //throws SQLException {
		return columnCount; } //columns.length; }
	
	/**
	  *	Returns the number of columns in this ResultSet object.
	  * @return the number of columns
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getColumnCount()
	  */
	public void setColumnCount(final int _columnCount) { //throws SQLException {
		this.columnCount = _columnCount; }
	
	/**
	  * Indicates whether the designated column is a cash value.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isCurrency(int)
	  */
	public boolean isCurrency(final int column) { // throws SQLException {
		return columns[checkColumn(column)].isCurrency(); }
	
	/**
	  * Indicates whether a column's case matters.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isCaseSensitive(int)
	  */
	public boolean isCaseSensitive(final int column) { // throws SQLException {
		return columns[checkColumn(column)].isCaseSensitive(); }
	
	/**
	  * Indicates whether the designated column can be used in a where clause.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isSearchable(int)
	  */
	public boolean isSearchable(final int column) { // throws SQLException {
		return columns[checkColumn(column)].isSearchable(); }
	
	/**
	  * Indicates whether values in the designated column are signed numbers.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isSigned(int)
	  */
	public boolean isSigned(final int column) { // throws SQLException {
		return columns[checkColumn(column)].isSigned(); }
	
	/**
	  * Indicates whether the designated column is automatically numbered, thus read-only.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isAutoIncrement(int)
	  */
	public boolean isAutoIncrement(final int column) { // throws SQLException {
		return columns[checkColumn(column)].isAutoIncrement(); }
	
	/**
	  * Indicates the nullability of values in the designated column.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return the nullability status of the given column;
	  *         one of columnNoNulls, columnNullable or columnNullableUnknown
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isNullable(int)
	  */
	public int isNullable(final int column) { // throws SQLException {
		return columns[checkColumn(column)].isNullable ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls; 
		//return ResultSetMetaData.columnNullableUnknown;
	}
	
	/**
	  * Indicates whether the designated column is definitely not writable.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isReadOnly(int)
	  */
	public boolean isReadOnly(final int column) { // throws SQLException {
		return !isWritable(column); }
	
	/**
	  * Indicates whether it is possible for a write on the designated column to succeed.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isWritable(int)
	  */
	public boolean isWritable(final int column) { // throws SQLException {
		return columns[checkColumn(column)].isWritable; }
	
	/**
	  * Indicates whether a write on the designated column will definitely succeed.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isDefinitelyWritable(int)
	  */
	public boolean isDefinitelyWritable(final int column) { // throws SQLException {
		return columns[checkColumn(column)].isWritable; } //true; }
	
	/**
	  * Gets the designated column's suggested title for use in printouts and displays.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return the suggested column title
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getColumnLabel(int)
	  */
	public String getColumnLabel(final int column) { // throws SQLException {
		return columns[checkColumn(column)].alias; }
	
	/**
	  * Get the designated column's name (in UpperCase).
	  * @param column - the first column is 1, the second is 2, ...
	  * @return column name
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getColumnName(int)
	  */
	public String getColumnName(final int column) { // throws SQLException {
		return columns[checkColumn(column)].name; }
	
	/**
	  * Gets the designated column's table name.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return table name or "" if not applicable
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getTableName(int)
	  */
	public String getTableName(final int column) throws SQLException {
		return columns[checkColumn(column)].getTableName(); }
	
	/**
	  * Get the designated column's table's schema.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return schema name or "" if not applicable
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getSchemaName(int)
	  */
	public String getSchemaName(final int column) { // throws SQLException {
		return columns[checkColumn(column)].getSchemaName(); }

	/**
	  * Gets the designated column's table's catalog name.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return column name or "" if not applicable
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getCatalogName(int)
	  */
	public String getCatalogName(final int column) throws SQLException {
		return columns[checkColumn(column)].getCatalogName(); }

	/**
	  * Indicates the designated column's normal maximum width in characters.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return the normal maximum number of characters allowed as the width of the designated column
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getColumnDisplaySize(int)
	  */
	public int getColumnDisplaySize(final int column) { // throws SQLException {
		return columns[checkColumn(column)].size; }

	/**
	  * Get the designated column's number of decimal digits.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return precision
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getPrecision(int)
	  */
	public int getPrecision(final int column) { // throws SQLException {
		return columns[checkColumn(column)].size; }

	/**
	  * Gets the designated column's number of digits to right of the decimal point.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return scale
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getScale(int)
	  */
	public int getScale(final int column) { // throws SQLException {
		return columns[checkColumn(column)].size; }

	/**
	  * Retrieves the designated column's SQL type.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return SQL type from java.sql.Types
	  * @throws SQLException - if a database access error occurs
	  * @see Types
	  * @see java.sql.ResultSetMetaData#getColumnType(int)
	  */
	public int getColumnType(final int column) { // throws SQLException {
		return columns[checkColumn(column)].type; } //java.sql.Types.VARCHAR; }//ResultSetMetaData.columnNullableUnknown;	}

	/**
	  * Retrieves the designated column's database-specific type name.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return type name used by the database.
	  * If the column type is a user-defined type,
	  * then a fully-qualified type name is returned.
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getColumnTypeName(int)
	  */
	public String getColumnTypeName(final int column) { // throws SQLException {
		return getColumnClassName(column); }
	
	/**
	  * Returns the fully-qualified name of the Java class whose instances are manufactured
	  * if the method ResultSet.getObject is called to retrieve a value from the column.
	  * ResultSet.getObject may return a subclass of the class returned by this method.
	  *
	  * @return the fully-qualified name of the class in the Java programming language that would be used by the method ResultSet.getObject to retrieve the value in the specified column. This is the class name used for custom mapping.
	  * @throws SQLException - if a database access error occurs
	  * @since 1.2
	  * @see What Is in the JDBC 2.0 API
	  * @see java.sql.ResultSetMetaData#getColumnClassName(int)
	  */
	public String getColumnClassName(final int column) { // throws SQLException {
		return columns[checkColumn(column)].colClass.toString(); }		
		//return "java.lang.String"; }

	/**
	 * Always returns {@code false}; this metadata wraps no other implementation.
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	public boolean isWrapperFor(Class arg0) throws SQLException { return false;	}

	/**
	 * Always returns {@code null}; this metadata wraps no other implementation.
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	public Object unwrap(Class arg0) throws SQLException { return null;	}

}
