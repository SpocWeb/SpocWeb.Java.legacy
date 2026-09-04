/*
 * File Name: DBMetaDataFix.java
 * Created on: 15.08.2003
 *
 */
package streamIO.integer.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import math.vector.VectorInt;

/**
 * Title: DBMetaDataFix<p>
 * Description:
 * Purpose:
 *
 * @see java.sql.DatabaseMetaData Implementation 
 * possibly use the same Implementation for both RSFix and RSSep! 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class DBMetaData 
extends ADBMetaData {

	/** @see java.sql.DatabaseMetaData#getDefaultTransactionIsolation()	 */
	public int getDefaultTransactionIsolation() { //throws SQLException {
		return connection.getTransactionIsolation();
	}

	/** @see java.sql.DatabaseMetaData#supportsTransactionIsolationLevel(int)	 */
	public boolean supportsTransactionIsolationLevel(final int level) { //throws SQLException {
		return level == connection.getTransactionIsolation();
	}

	/** @see java.sql.DatabaseMetaData#getURL()	 */
	public String getURL() { //throws SQLException {
		return connection.urlDir.getAbsolutePath();
	}

	/** @see java.sql.DatabaseMetaData#getConnection()	 */
	public Connection getConnection() { //throws SQLException {
		return connection;
	}

	/** initializing Constructor	 */
	public DBMetaData(final AConnection connection_) {
		super(connection_);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Implementations of required Methods. 
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	

	/** @see java.sql.DatabaseMetaData#getDatabaseProductName()	 */
	public String getDatabaseProductName() { //throws SQLException {
		return "ASCII_FIXED_LENGTH";
	}

	/** @see java.sql.DatabaseMetaData#getDatabaseProductVersion()	 */
	public String getDatabaseProductVersion() { //throws SQLException {
		return "1.0";
	}

	/** @see java.sql.DatabaseMetaData#getDriverName()	 */
	public String getDriverName() { //throws SQLException {
		return connection.driver.getClass().getName();
	}

	/**
	 * @see java.sql.DatabaseMetaData#getDriverMajorVersion()
	 */
	public int getDriverMajorVersion() {
		return connection.driver.getMajorVersion();
	}

	/**
	 * @see java.sql.DatabaseMetaData#getDriverMinorVersion()
	 */
	public int getDriverMinorVersion() {
		return connection.driver.getMinorVersion();
	}

	/** @see java.sql.DatabaseMetaData#supportsMixedCaseIdentifiers()	 */
	public boolean supportsMixedCaseIdentifiers() { //throws SQLException {
		return !connection.ignoreIDCase;
	}

	/** @see java.sql.DatabaseMetaData#storesUpperCaseIdentifiers()	 */
	public boolean storesUpperCaseIdentifiers() { //throws SQLException {
		return connection.useToUpper;
	}

	/** @see java.sql.DatabaseMetaData#storesLowerCaseIdentifiers()	 */
	public boolean storesLowerCaseIdentifiers() { //throws SQLException {
		return !connection.useToUpper;
	}

	/** @see java.sql.DatabaseMetaData#storesMixedCaseIdentifiers()	 */
	public boolean storesMixedCaseIdentifiers() { //throws SQLException {
		return !connection.ignoreIDCase;
	}

	/** @see java.sql.DatabaseMetaData#getColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)	 */
	public ResultSet getColumns(
		final String catalog,
		final String schema_,
		final String tableName,
		final String columnName) throws SQLException {
		String relPath = tableName; 
		if (schema_ != null) { relPath = schema_ + File.separatorChar + relPath; }
		if (catalog != null) { relPath = catalog + File.separatorChar + relPath; }
		//relPath+=connection.suffix; 
		//Interessant: Initialisierungsfehler new File[][] hier führte zu irreführender Fehlermeldung erst zur Runtime
		final Object[][] tableCol = new Object[COLUMN_FIELDS.length][]; //
		final File table = new File(relPath); //connection.urlDir, relPath);  
		final String[] defaults = new String[COLUMN_FIELDS.length]; //VectorString.COPY(COLUMN_FIELD_DEFAULTS); 
		final AResultSet rsf = (AResultSet) connection.createStatement().executeQuery(AStatement.STR_SELECT_ALL+table); //new ResultSetFix(table);
		rsf.next(); //to read the Header Meta-Info 
		defaults[0] = catalog; //"TABLE_CAT"; //String => table catalog (may be null)
		defaults[1] = schema_; //"TABLE_SCHEM", //String => table schema (may be null) 
		defaults[2] = tableName; //"TABLE_NAME", //String => table name 
		defaults[3] = "COLUMN_NAME"; //String => column name
		tableCol[3] = rsf.columns;
		defaults[4] = "DATA_TYPE"; //int => SQL type from java.sql.Types 
		tableCol[4] = null; //TODO: ENCODE_TYPE(rsf.metaData.fieldTypes);
		defaults[5] = "TYPE_NAME"; //String => Data source dependent type name, for a UDT the type name is fully qualified
		tableCol[5] = null; //TODO: rsf.metaData.fieldTypes;
		defaults[6] = Integer.toString(Integer.MAX_VALUE); //"COLUMN_SIZE"; //int => column size. For char or date types this is the maximum number of characters, for numeric or decimal types this is precision
		//if (rsf instanceof ResultSetFix) 
		//TODO: 	tableCol[6] = VectorInt.TO_INTEGER(((ResultSetFix)rsf).fieldSizes);
		defaults[7] = "BUFFER_LENGTH"; //is not used
		defaults[8] = "DECIMAL_DIGITS"; //int => the number of fractional digits 
		//the best is to use two Fields, one right aligned and one left aligned. 
		defaults[9] = "NUM_PREC_RADIX"; //int => Radix (typically either 10 or 2)
		defaults[10] = "NULLABLE"; //int => is NULL allowed @see NULLABLE_TYPES
		tableCol[10] = null; //TODO: VectorInt.TO_BOOLEAN(rsf.metaData.fieldIsNullable);
		defaults[11] = "REMARKS"; //String => comment describing column (may be null)
		defaults[12] = "COLUMN_DEF"; //String => default value (may be null) 
		tableCol[12] = null; //TODO: rsf.fieldDefaults;
		defaults[13] = "SQL_DATA_TYPE"; //int => unused
		defaults[14] = "SQL_DATETIME_SUB"; //int => unused 
		defaults[15] = "CHAR_OCTET_LENGTH";
		//if (rsf instanceof ResultSetFix) 
		//TODO:	tableCol[15] = VectorInt.TO_INTEGER(((ResultSetFix)rsf).fieldSizes);
		defaults[16] = "ORDINAL_POSITION"; //int => index of column in table (starting at 1)
		tableCol[16] = VectorInt.TO_INTEGER(VectorInt.IDENTITY(COLUMN_FIELDS.length));
		defaults[17] = "YES"; //"IS_NULLABLE", //String => @see IS_NULLABLE_TYPES
		tableCol[17] = null; //TODO: VectorInt.TO_BOOLEAN(rsf.metaData.fieldIsNullable);
		defaults[18] = null; //"SCOPE_CATLOG",
		//String => catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
		defaults[19] = null; //"SCOPE_SCHEMA",
		//String => schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)
		defaults[20] = null; //"SCOPE_TABLE",
		//String => table name that this the scope of a reference attribure (null if the DATA_TYPE isn't REF)
		defaults[21] = null; //"SOURCE_DATA_TYPE" //short => source type of a distinct type or user-generated Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF) 
		return new ResultSetArray(tableCol, TABLE_FIELDS); //, defaults); 
	}

	/** @see java.sql.DatabaseMetaData#getColumnPrivileges(java.lang.String, java.lang.String, java.lang.String, java.lang.String)	 */
	public ResultSet getColumnPrivileges(
		String catalog,
		String schema,
		String table,
		String columnNamePattern) { //throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.DatabaseMetaData#getTablePrivileges(java.lang.String, java.lang.String, java.lang.String)	 */
	public ResultSet getTablePrivileges(
		String catalog,
		String schemaPattern,
		String tableNamePattern) { //throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.DatabaseMetaData#getBestRowIdentifier(java.lang.String, java.lang.String, java.lang.String, int, boolean)	 */
	public ResultSet getBestRowIdentifier(
		String catalog,
		String schema,
		String table,
		int scope,
		boolean nullable) { //throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.DatabaseMetaData#getVersionColumns(java.lang.String, java.lang.String, java.lang.String)	 */
	public ResultSet getVersionColumns(
		String catalog,
		String schema,
		String table) { //throws SQLException {
		return null;
	}

	/** @see java.sql.DatabaseMetaData#getPrimaryKeys(java.lang.String, java.lang.String, java.lang.String)	 */
	public ResultSet getPrimaryKeys(
		String catalog,
		String schema,
		String table) { //throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.DatabaseMetaData#getImportedKeys(java.lang.String, java.lang.String, java.lang.String)	 */
	public ResultSet getImportedKeys(
		String catalog,
		String schema,
		String table) { //throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.DatabaseMetaData#getExportedKeys(java.lang.String, java.lang.String, java.lang.String)	 */
	public ResultSet getExportedKeys(
		String catalog,
		String schema,
		String table) { //throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.DatabaseMetaData#getCrossReference(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)	 */
	public ResultSet getCrossReference(
		String primaryCatalog,
		String primarySchema,
		String primaryTable,
		String foreignCatalog,
		String foreignSchema,
		String foreignTable) { //throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.DatabaseMetaData#getTypeInfo()	 */
	public ResultSet getTypeInfo() { //throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.DatabaseMetaData#getIndexInfo(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)	 */
	public ResultSet getIndexInfo(
		String catalog,
		String schema,
		String table,
		boolean unique,
		boolean approximate) { //throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.DatabaseMetaData#getUDTs(java.lang.String, java.lang.String, java.lang.String, int[])	 */
	public ResultSet getUDTs(
		String catalog,
		String schemaPattern,
		String typeNamePattern,
		int[] types) { //throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.DatabaseMetaData#getSuperTypes(java.lang.String, java.lang.String, java.lang.String)	 */
	public ResultSet getSuperTypes(
		String catalog,
		String schemaPattern,
		String typeNamePattern) { //throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.DatabaseMetaData#getSuperTables(java.lang.String, java.lang.String, java.lang.String)	 */
	public ResultSet getSuperTables(
		String catalog,
		String schemaPattern,
		String tableNamePattern) { //throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.DatabaseMetaData#getAttributes(java.lang.String, java.lang.String, java.lang.String, java.lang.String)	 */
	public ResultSet getAttributes(
		String catalog,
		String schemaPattern,
		String typeNamePattern,
		String attributeNamePattern) { //throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.DatabaseMetaData#getDatabaseMajorVersion()	 */
	public int getDatabaseMajorVersion() { //throws SQLException {
		return connection.driver.getMajorVersion();
	}

	/** @see java.sql.DatabaseMetaData#getDatabaseMinorVersion()	 */
	public int getDatabaseMinorVersion() { //throws SQLException {
		return connection.driver.getMinorVersion();
	}

	public static void main(String[] args) {
	}

}
