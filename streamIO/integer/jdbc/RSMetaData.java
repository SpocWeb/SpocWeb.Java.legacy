package streamIO.integer.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
//import java.sql.SQLException;

// TODO: LOGIC: every column-indexed method here (isCurrency, isCaseSensitive, getColumnName,
// getColumnLabel, ...) does "columns[column]" directly against the caller-supplied column
// number, but java.sql.ResultSetMetaData's contract is 1-based ("the first column is 1",
// documented on every method below) while `columns` is a plain 0-based array. Confirmed by
// AResultSet#findColumn(String), which returns the raw 0-based loop index `i` rather than
// `i+1`. A caller following the standard JDBC contract (column 1 = first column) reads the
// second column instead of the first throughout, and column == columnCount throws
// ArrayIndexOutOfBoundsException instead of a documented SQLException for an out-of-range index.
/**
 * Implements {@link ResultSetMetaData}, sharing the field names and optionally the field
 * sizes with the {@link ResultSet} it describes; similar in role to
 * {@code org.xml.sax.Attributes}.
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
		return columns[column].isCurrency(); }
	
	/**
	  * Indicates whether a column's case matters.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isCaseSensitive(int)
	  */
	public boolean isCaseSensitive(final int column) { // throws SQLException {
		return columns[column].isCaseSensitive(); }
	
	/**
	  * Indicates whether the designated column can be used in a where clause.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isSearchable(int)
	  */
	public boolean isSearchable(final int column) { // throws SQLException {
		return columns[column].isSearchable(); }
	
	/**
	  * Indicates whether values in the designated column are signed numbers.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isSigned(int)
	  */
	public boolean isSigned(final int column) { // throws SQLException {
		return columns[column].isSigned(); }
	
	/**
	  * Indicates whether the designated column is automatically numbered, thus read-only.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isAutoIncrement(int)
	  */
	public boolean isAutoIncrement(final int column) { // throws SQLException {
		return columns[column].isAutoIncrement(); }
	
	/**
	  * Indicates the nullability of values in the designated column.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return the nullability status of the given column;
	  *         one of columnNoNulls, columnNullable or columnNullableUnknown
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isNullable(int)
	  */
	public int isNullable(final int column) { // throws SQLException {
		return columns[column].isNullable ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls; 
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
		return columns[column].isWritable; }
	
	/**
	  * Indicates whether a write on the designated column will definitely succeed.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isDefinitelyWritable(int)
	  */
	public boolean isDefinitelyWritable(final int column) { // throws SQLException {
		return columns[column].isWritable; } //true; }
	
	/**
	  * Gets the designated column's suggested title for use in printouts and displays.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return the suggested column title
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getColumnLabel(int)
	  */
	public String getColumnLabel(final int column) { // throws SQLException {
		return columns[column].alias; }
	
	/**
	  * Get the designated column's name (in UpperCase).
	  * @param column - the first column is 1, the second is 2, ...
	  * @return column name
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getColumnName(int)
	  */
	public String getColumnName(final int column) { // throws SQLException {
		return columns[column].name; }
	
	/**
	  * Gets the designated column's table name.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return table name or "" if not applicable
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getTableName(int)
	  */
	public String getTableName(final int column) throws SQLException {
		return columns[column].getTableName(); }
	
	/**
	  * Get the designated column's table's schema.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return schema name or "" if not applicable
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getSchemaName(int)
	  */
	public String getSchemaName(final int column) { // throws SQLException {
		return columns[column].getSchemaName(); }

	/**
	  * Gets the designated column's table's catalog name.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return column name or "" if not applicable
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getCatalogName(int)
	  */
	public String getCatalogName(final int column) throws SQLException {
		return columns[column].getCatalogName(); }

	/**
	  * Indicates the designated column's normal maximum width in characters.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return the normal maximum number of characters allowed as the width of the designated column
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getColumnDisplaySize(int)
	  */
	public int getColumnDisplaySize(final int column) { // throws SQLException {
		return columns[column].size; }

	/**
	  * Get the designated column's number of decimal digits.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return precision
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getPrecision(int)
	  */
	public int getPrecision(final int column) { // throws SQLException {
		return columns[column].size; }

	/**
	  * Gets the designated column's number of digits to right of the decimal point.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return scale
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getScale(int)
	  */
	public int getScale(final int column) { // throws SQLException {
		return columns[column].size; }

	/**
	  * Retrieves the designated column's SQL type.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return SQL type from java.sql.Types
	  * @throws SQLException - if a database access error occurs
	  * @see Types
	  * @see java.sql.ResultSetMetaData#getColumnType(int)
	  */
	public int getColumnType(final int column) { // throws SQLException {
		return columns[column].type; } //java.sql.Types.VARCHAR; }//ResultSetMetaData.columnNullableUnknown;	}

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
		return columns[column].colClass.toString(); }		
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
