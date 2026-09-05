/*
 * Class to encapsulate all Properties of a DB Column or a (String) Constant. 
 * This is more object oriented 
 * than maintaining several Arrays of the same Size in AResultSet
 * Created on 26.03.2005
 *
 */
package streamIO.integer.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import math.vector.VectorString;


/**
 * Class to encapsulate all Properties of a DB Column. 
 * This is more object oriented 
 * than maintaining several Arrays of the same Size in AResultSet
 * Created on 26.03.2005
 * 
 * @author heuerm
 * 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:56:35Z
 * digest: 7a3993443fa78107bc033ea95bda7cc1d98db27688204e5826cd14f64c51b389
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class DbColumn 
implements IDbStringValue {
	
	/** Default Value for the Column Default 	 */
	public static Class DEFAULT_COLUMN_TYPE = String.class;  
	
	/** Default Value for the Column Default 	 */
	public static String DEFAULT_COLUMN_VALUE = "";  
	
	///////////////////////////////////////////////////////////////////////////
	/// Member Variables 
	///////////////////////////////////////////////////////////////////////////
	
	/** Reference back to the Table for this Column	 */
	final public ResultSet table; 
	
	/** Column Name 	 */
	final public String name; 
	
	/** Position of this Column in the Table 	 */
	final public int position; 
	
	/** Column Alias or Print-Label (set by the Select or read from the MetaData) 	 */
	public String alias; 
	
	/** Default Value for this Column 	 */
	public String defaultValue = DEFAULT_COLUMN_VALUE; 
	
	/** Flag for read only Fields (only on Insert) */
	public boolean isWritable = true; 
	 
	/** Flag for Nullable Fields (only on Insert) */
	public boolean isNullable = true; 
	 
	/** Field Size */
	public int size = Integer.MAX_VALUE;

	/** preferred Data Type for this Column 	 */
	public Class colClass = DEFAULT_COLUMN_TYPE; 
	
	/**
	  * the designated column's SQL type.
	  * @see Types
	  * @see java.sql.ResultSetMetaData#getColumnType(int)
	  */
	public int type = java.sql.Types.VARCHAR; 
	
	/**returns a String Represenation of this Object
	 * @return a String Represenation of this Object
	 */
	public String toString() {
		final StringBuffer buf = new StringBuffer();
		buf.append(tableName).append('.').append(name).append(" as ").append(alias)
		.append(':').append(colClass).append('[').append(size).append(']')
		.append('(').append(isWritable?'W':'R').append(isNullable?'N':' ') 
		.append('=').append(defaultValue).append(')'); 
		return buf.toString(); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors 
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializing Constructor taking all identifying Columns
	 * @param table
	 * @param _FieldName
	 */
	public DbColumn(final ResultSet table, final String _FieldName) throws SQLException {
		this(table, _FieldName, DEFAULT_COLUMN_TYPE);
	}
	
	/**
	 * Initializing Constructor taking all identifying Columns
	 * @param table
	 * @param _FieldName
	 */
	public DbColumn(final ResultSet table, final String _FieldName, final String _FieldAlias) throws SQLException {
		this(table, _FieldName, DEFAULT_COLUMN_TYPE, DEFAULT_COLUMN_VALUE, _FieldAlias);
	}
	
	/**
	 * Initializing Constructor taking all identifying Columns
	 * @param table
	 * @param name
	 */
	public DbColumn(final ResultSet _table, final String _FieldName, final String _FieldAlias, final int _position) throws SQLException {
		this(_table, _FieldName, DEFAULT_COLUMN_TYPE, DEFAULT_COLUMN_VALUE, _FieldAlias, _position);
	}
	
	/**
	 * Initializing Constructor taking all identifying Columns
	 * @param table
	 * @param name
	 */
	public DbColumn(final ResultSet _table, final String _TableName, final String _FieldName, final String _FieldAlias, final int _position) throws SQLException {
		this(_table, _TableName, _FieldName, DEFAULT_COLUMN_TYPE, DEFAULT_COLUMN_VALUE, _FieldAlias, _position);
	}
	
	/**
	 * Initializing Constructor 
	 * @param table
	 * @param name
	 */
	public DbColumn(final ResultSet table, final String name, final int position) {
		this(table, name, DEFAULT_COLUMN_TYPE, DEFAULT_COLUMN_VALUE, name, position);
	}
	
	/**
	 * Initializing Constructor 
	 * @param table
	 * @param name
	 * @param type
	 */
	public DbColumn(final ResultSet table, final String name, final Class type) throws SQLException {
		this(table, name, type, DEFAULT_COLUMN_VALUE);
	}
	
	/**
	 * Initializing Constructor taking all Columns
	 * @param table
	 * @param name
	 * @param type
	 */
	public DbColumn(final ResultSet table, final String name, 
			final Class type, final String fieldDefault) throws SQLException {
		this(table, name, type, fieldDefault, name);
	}
	
	/**
	 * Initializing Constructor taking all Columns
	 * @param table
	 * @param _FieldName
	 * @param type
	 */
	public DbColumn(final ResultSet table, final String _FieldName, final Class type, 
			final String fieldDefault, final String alias) throws SQLException {
		this(table, _FieldName, type, fieldDefault, alias, table == null ? -1 : table.findColumn(_FieldName));
	}
	
	/**
	 * Initializing Constructor taking all Columns
	 * @param table
	 * @param _FieldName
	 * @param _FieldType
	 */
	public DbColumn(final ResultSet table, final String _TableName, final String _FieldName, 
			final Class _FieldType, final String _FieldDefault, final String _FieldAlias, final int position) {
		this.defaultValue = _FieldDefault; 
		this.position = position; 
		this.table = table;
		this.name  = _FieldName ;
		this.colClass  = _FieldType ;
		this.alias = _FieldAlias; 
		if ((table != null) && 
			(_TableName == null)) {
			try {
				this.tableName = table.getCursorName();
			} catch (final SQLException x) {}
		} else {
			this.tableName = _TableName; 
		}
	}
	
	/**
	 * Initializing Constructor taking all Columns
	 * @param table
	 * @param _FieldName
	 * @param _FieldType
	 */
	public DbColumn(final ResultSet table, final String _FieldName, 
			final Class _FieldType, final String _FieldDefault, final String _FieldAlias, final int position) {
		this(table, null, _FieldName, _FieldType, _FieldDefault, _FieldAlias, position); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Interface 
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns this field's current value: the constant name itself when {@code table} is
	 * {@code null}, otherwise the current value of the backing result set column.
	 * @return the Value of this Field
	 * @throws SQLException
	 */
	public String getString() throws SQLException {
		if (table == null) //Constant, with Quotes removed
			return name;
		return table.getString(position);
	}

	/**
	 * Updates this field's value in the backing result set column.
	 * @param value the new Value
	 * @return the old Value of this Field
	 * @throws SQLException when this is not a DB Field
	 */
	public String setString(final String value) throws SQLException {
		if (table == null) //Constant, with Quotes removed
			throw new SQLException("Constant '"+name+"' cannot be updated to Value '"+value+"'"); 
		final String ret = table.getString(name);
		table.updateString(position, value);
		return ret;  
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Preparation for the corresponding RSMetaData Methods
	///////////////////////////////////////////////////////////////////////////

	/** Indicates whether the designated column is a cash value.	 */
	public boolean isCurrency; // = false; 
	
	/**
	  * Indicates whether the designated column is a cash value.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isCurrency(int)
	  */
	public boolean isCurrency() { return isCurrency; }

	///////////////////////////////////////////////////////////////////////////

	/** Indicates whether a column's case matters.	 */
	public boolean isCaseSensitive = true; 
	
	/**
	  * Indicates whether a column's case matters.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isCaseSensitive(int)
	  */
	public boolean isCaseSensitive() { // throws SQLException {
		return isCaseSensitive; }

	///////////////////////////////////////////////////////////////////////////

	/** Indicates whether the designated column can be used (effectively) in a where clause.	 */
	public boolean isSearchable = true; 
	
	/**
	  * Indicates whether the designated column can be used (effectively) in a where clause.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isSearchable(int)
	  */
	public boolean isSearchable() { return isSearchable; }

	///////////////////////////////////////////////////////////////////////////

	/** Indicates whether values in the designated column are signed numbers.	 */
	public boolean isSigned = true; 
	
	/**
	  * Indicates whether values in the designated column are signed numbers.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return true if so; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#isSigned(int)
	  */
	public boolean isSigned() { return isSigned; }

	///////////////////////////////////////////////////////////////////////////

	/** Indicates whether the designated column is automatically numbered, thus read-only.	*/
	public boolean isAutoIncrement; // = false; 
	
	/**
	  * Indicates whether the designated column is automatically numbered, thus read-only.
	  * the java.sql Types don't allow to distinguish this
	  * @return true if so; false otherwise
	  * @see java.sql.ResultSetMetaData#isAutoIncrement(int)
	  */
	public boolean isAutoIncrement() { return isAutoIncrement; } //

	/** The Table Name for this Column, defaulted to the Cursor Name */
	public String tableName;
	
	/**
	  * Gets the designated column's table name or the Alias that is used.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return table name or "" if not applicable
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getTableName(int)
	  */
	public String getTableName() { //throws SQLException {
		return tableName; }

	/** The Schema Name for this Column */
	public String schemaName;
	
	/**
	  * Get the designated column's table's schema.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return schema name or "" if not applicable
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getSchemaName(int)
	  */
	public String getSchemaName() { // throws SQLException {
		return schemaName; }

	/** The Catalog for all Columns, since the Drivers don't support Joins yet */
	//public String catalogName;
	
	/**
	  * Gets the designated column's table's catalog name.
	  * @param column - the first column is 1, the second is 2, ...
	  * @return column name or "" if not applicable
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSetMetaData#getCatalogName(int)
	  */
	public String getCatalogName() throws SQLException {
		return table.getStatement().getConnection().getCatalog(); } 

	///////////////////////////////////////////////////////////////////////////
	/// static Helper Methods
	///////////////////////////////////////////////////////////////////////////

	/** fills the Default Values into the given Array 	 */
	final static public void FILL_DEFAULTS(final DbColumn[] columns, final String[] row) {
		//VectorString.COPY_AT(newRow, fieldDefaults);
		for(int i  = columns.length; --i >= 0; ) 
			row[i] = columns[i].defaultValue;
	}

	/** fills the given Columns into the ArrayList in the correct Order	 */
	final static public void FILL_DEFAULTS(final DbColumn[] columns, final VectorString Fields) {
		Fields.setSize(0);
		for(int i = -1; ++i < columns.length; ) 
			Fields.addItem(columns[i].defaultValue);
	}

	/** fills the given Columns into the ArrayList in the correct Order	 */
	final static public void FILL_DEFAULTS(final DbColumn[] columns, final ArrayList Fields) {
		Fields.clear();
		for(int i = -1; ++i < columns.length; ) 
			Fields.add(columns[i].defaultValue);
	}

	/**
	 * Copies each entry of {@code _fieldDefaults} into the matching column's default value.
	 * @param _fieldDefaults default values, one per column, or {@code null} to leave columns unchanged
	 */
	final static public void INIT_DEFAULTS(final String[] _fieldDefaults, final DbColumn[] columns) {
		if (_fieldDefaults == null) 
			return; 
		int j = _fieldDefaults.length; 
		while (--j >= 0)
			columns[j].defaultValue = _fieldDefaults[j]; 
	}
	
}
