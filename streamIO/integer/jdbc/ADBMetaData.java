/*
 * File Name: ADBMetaData.java
 * Created on: 09.10.2003
 *
 */
package streamIO.integer.jdbc;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

import math.vector.VectorString;
import streamIO.fileSystem.DirectoryFilter;
import streamIO.fileSystem.SuffixFileNameFilter;

/**
 * Filesystem-backed {@link DatabaseMetaData} that reports catalogs as the immediate
 * sub-directories of the connection's {@link AConnection#urlDir}, and tables as the files
 * matching {@link AConnection#suffix} within a catalog directory.
 *
 * <p>Most capability-negotiation predicates ({@code supportsX()}) answer with a fixed
 * {@code true}/{@code false} reflecting this driver's actual, deliberately narrow feature set
 * (no transactions, no stored procedures, no schemas beyond the catalog directory itself)
 * rather than delegating to a real SQL engine. It is also a sister class to
 * {@link RSMetaData}, which plays the equivalent role for {@link java.sql.ResultSetMetaData}.
 *
 * <h2>Collaborators</h2>
 *
 * | Type | Relationship |
 * |---|---|
 * | {@link AConnection} | Supplies the directory, suffix and table-listing filters this metadata reports over. |
 * | {@link ResultSetArray} | In-memory {@link ResultSet} used to return every metadata row set. |
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see streamIO.integer.jdbc.DBMetaDataFix
 * @see streamIO.object.parser.jdbc.DBMetaDataSep
 * @see AConnection the connection this metadata describes
 * @see ResultSetArray the ResultSet implementation used for every metadata row set
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:42:25Z
 * digest: 63743f971680081bbbfb7da0d6116d75156e39bbdc7060b36b703477b298a5ee
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public abstract class ADBMetaData
implements DatabaseMetaData {

	//////////////////////////////////////////////////////////////////////////////////
	/// De- and En-Coding of SQL Types
	//////////////////////////////////////////////////////////////////////////////////

	/** Unique Mappings from Java Classes to SQL Types */
	private static final Object[][] SQL_TYPES = {
		{new Integer(Types.INTEGER), Integer.class}, 
		{new Integer(Types.BOOLEAN), Boolean.class}, 
		{new Integer(Types.VARCHAR), String.class},
		{new Integer(Types.SMALLINT), Short.class},
		{new Integer(Types.BIGINT), Long.class}, //BigInt.class}, 
		{new Integer(Types.DOUBLE), Double.class},
		{new Integer(Types.DECIMAL), BigDecimal.class}, 
		{new Integer(Types.DATE), java.sql.Date.class},
		{new Integer(Types.TIME), java.sql.Time.class}, 
		{new Integer(Types.TIMESTAMP), java.sql.Timestamp.class},
		{new Integer(Types.TINYINT), Byte.class},
		{new Integer(Types.VARBINARY), Byte[].class},
		{new Integer(Types.BLOB), Blob.class}, // IStreamIn.class}, 
		{new Integer(Types.CLOB), Clob.class},
	}; 

	/** Additional (no longer unique) Mappings from Java Classes to SQL Types
	 * (the Inverse: Type => Class is still unique) */
	private static final Object[][] SQL_TYPES_DUPS = {
		{new Integer(Types.BIT), Boolean.class}, 
		{new Integer(Types.CHAR), String.class}, 
		{new Integer(Types.LONGVARCHAR), String.class},
		{new Integer(Types.FLOAT), Double.class},
		{new Integer(Types.NUMERIC), BigDecimal.class},
		{new Integer(Types.REAL), Float.class}, 
		{new Integer(Types.BINARY), Byte[].class}, //Boolean.class}, 
		{new Integer(Types.LONGVARBINARY), Byte[].class},

		{new Integer(Types.NULL), null}, //Object.class},
		{new Integer(Types.JAVA_OBJECT), Object.class},
		{new Integer(Types.ARRAY), Object[].class},
		{new Integer(Types.DATALINK), null},
		{new Integer(Types.DISTINCT), null},
		{new Integer(Types.OTHER), null},
		{new Integer(Types.REF), null},
		{new Integer(Types.STRUCT), null},
	};

	private static final HashMap Ints2Types = VectorString.MAP(SQL_TYPES, 0, 1); 
	
	private static final HashMap Types2Ints = VectorString.MAP(SQL_TYPES, 1, 0);

	static { VectorString.MAP(Ints2Types, SQL_TYPES_DUPS, 0, 1); }
	
	/**
	 * Encodes a Class Object into its @see Types
	 * @param type Class Object
	 * @return Integer Type Number
	 */
	final static public Class DECODE_TYPE(final Integer type) {
		return (Class) Types2Ints.get(type);
	}
	
	/**
	 * Encodes a List of Class Objects into their @see Types
	 * @param type List of Class Objects 
	 * @return a List of Integer Type Numbers
	 */
	final static public Class[] DECODE_TYPE(final Integer[] type) {
		final Class[] ret = new Class[type.length];
		for (int i = type.length; --i >= 0;) {
			ret[i] = DECODE_TYPE(type[i]);
		}
		return ret;
	}
	
	//////////////////////////////////////////////////////////////////////////////

	/**
	 * Encodes a Class Object into its @see Types
	 * @param type Class Object
	 * @return Integer Type Number
	 */
	final static public Integer ENCODE_TYPE(final Class type) {
		return (Integer) Types2Ints.get(type);
	}
	
	/**
	 * Encodes a List of Class Objects into their @see Types
	 * @param type List of Class Objects 
	 * @return a List of Integer Type Numbers
	 */
	final static public Integer[] ENCODE_TYPE(final Class[] type) {
		final Integer[] ret = new Integer[type.length];
		for (int i = type.length; --i >= 0;) {
			ret[i] = ENCODE_TYPE(type[i]);
		}
		return ret;
	}
	
	//private static final Class DECODE_TYPE(final int type) {}

	//////////////////////////////////////////////////////////////////////////////////

	private static final String STR_IS_GRANTABLE = "IS_GRANTABLE";
	private static final String STR_PRIVILEGE = "PRIVILEGE";
	private static final String STR_GRANTEE = "GRANTEE";
	private static final String STR_GRANTOR = "GRANTOR";
	private static final String STR_NUMBER_PRECISION_RADIX = "NUM_PREC_RADIX";
	private static final String STR_DECIMAL_DIGITS = "DECIMAL_DIGITS";
	private static final String STR_BUFFER_LENGTH = "BUFFER_LENGTH";
	private static final String STR_COLUMN_SIZE = "COLUMN_SIZE";
	private static final String STR_TABLE_NAME = "TABLE_NAME";
	private static final String STR_TABLE_CATALOG = "TABLE_CAT";
	private static final String STR_TABLE_SCHEMA = "TABLE_SCHEM";
	private static final String STR_IS_NULLABLE = "IS_NULLABLE";
	private static final String STR_REMARKS = "REMARKS";
	private static final String STR_NULLABLE = "NULLABLE";
	private static final String STR_PRECISION = "PRECISION";
	private static final String STR_PROCEDURE_NAME = "PROCEDURE_NAME";
	private static final String STR_PROCEDURE_SCHEMA = "PROCEDURE_SCHEM";
	private static final String STR_COLUMN_NAME = "COLUMN_NAME";
	private static final String STR_DATA_TYPE = "DATA_TYPE";
	private static final String STR_TYPE_NAME = "TYPE_NAME";
	private static final String STR_TYPE_SCHEMA = "TYPE_SCHEM";
	private static final String STR_TYPE_CATALOG = "TYPE_CAT";

	/**
	 * Initializing Constructor
	 * @param connection_ the connection whose directory this metadata describes
	 */
	public ADBMetaData(final AConnection connection_) {
		this.connection = connection_;
	}

	/** Reference to the Connection Object
	 * not completely moved to Interfaces yet...
	 */
	final AConnection connection;

	/**
	 * Reports {@code false}.
	 *
	 * TODO support Column Aliasing in SQL Statements
	 *
	 * @see java.sql.DatabaseMetaData#supportsColumnAliasing()
	 */
	public boolean supportsColumnAliasing() { //throws SQLException {
		// TODO support Column Aliasing in SQL Statements
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * TODO support Table Correlation Names (Alias 'as')
	 *
	 * @see java.sql.DatabaseMetaData#supportsTableCorrelationNames()
	 */
	public boolean supportsTableCorrelationNames() { //throws SQLException {
		// TODO support Table Correlation Names (Alias 'as')
		return false;
	}

	/**
	 * Lists this connection's table files (matching {@link AConnection#suffix}) inside the
	 * directory named by {@code catalog} as a single-row-per-table {@link ResultSetArray}.
	 *
	 * @param catalog sub-directory (relative to {@link AConnection#urlDir}) to list table files from
	 * @param schemaPattern reported verbatim as the schema of every row; not used for filtering
	 * @param tableNamePattern SQL LIKE Pattern ('%' and '_') the Table Name must match;
	 * {@code null} or "%" returns every Table
	 * @param tableTypes the Table Types to return; {@code null} returns every Type.
	 * Every Table of this Connection is of Type {@link #TABLE_TYPE_TABLE},
	 * so any other List yields an empty Result.
	 * @see java.sql.DatabaseMetaData#getTables(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	public ResultSet getTables(
		final String catalog,
		final String schemaPattern,
		final String tableNamePattern,
		final String[] tableTypes) { //throws SQLException {
		final File file = new File(connection.urlDir, catalog);
		final Object[][] tableCols = new Object[TABLE_FIELDS.length][];
		String[] tableNames = VectorString.SUBSTRING(file.list(new SuffixFileNameFilter(connection.suffix)), connection.suffix.length(), true);
		if (! CONTAINS_TABLE_TYPE(tableTypes))
			 tableNames = new String[0]; //no Type of this Connection was asked for
		tableCols[2] = FILTER_BY_PATTERN(tableNames, tableNamePattern);
		final String[] defaults = VectorString.COPY(TABLE_FIELD_DEFAULTS); 
		defaults[0] = catalog; //"TABLE_CAT"; //String => table catalog (may be null)
		defaults[1] = schemaPattern;
		//"TABLE_SCHEM"; //String => table schema (may be null)
		//String => specifies how values in SELF_REFERENCING_COL_NAME are created. @see REF_GENERATION_TYPES
		return new ResultSetArray(tableCols, TABLE_FIELDS); //, defaults);
	}

	/**
	 * Tells whether the given List of Table Types includes the only Type this Connection
	 * knows, {@link #TABLE_TYPE_TABLE}.
	 * @param tableTypes the requested Table Types; {@code null} means every Type
	 * @return true when Tables should be returned
	 */
	protected static boolean CONTAINS_TABLE_TYPE(final String[] tableTypes) {
		if (tableTypes == null)
			return true; //no Restriction
		for (int i = tableTypes.length; --i >= 0;) {
			if (TABLE_TYPE_TABLE.equalsIgnoreCase(tableTypes[i]))
				return true;
		}
		return false;
	}

	/**
	 * Keeps only the Names matching the given SQL LIKE Pattern ('%' and '_').
	 * @param names the Names to filter
	 * @param pattern the LIKE Pattern; {@code null} keeps every Name
	 * @return a new Array with the matching Names, in the original Order
	 */
	protected static String[] FILTER_BY_PATTERN(final String[] names, final String pattern) {
		if ((names == null) || (pattern == null))
			return names;
		final StringBuffer regex = new StringBuffer(pattern.length()+8);
		for (int i = -1; ++i < pattern.length();) {
			final char chr = pattern.charAt(i);
			if      (chr == '%') regex.append(".*");
			else if (chr == '_') regex.append('.');
			else                 regex.append(java.util.regex.Pattern.quote(String.valueOf(chr)));
		}
		final java.util.regex.Pattern compiled = java.util.regex.Pattern.compile(regex.toString());
		final String[] tmp = new String[names.length];
		int num = 0;
		for (int i = -1; ++i < names.length;) {
			if ((names[i] != null) && compiled.matcher(names[i]).matches())
				tmp[num++] = names[i];
		}
		final String[] ret = new String[num];
		System.arraycopy(tmp, 0, ret, 0, num);
		return ret;
	}

	/**
	 * Lists the immediate sub-directories of {@link AConnection#urlDir} as a single-column
	 * {@link ResultSetArray} of catalog names.
	 *
	 * @see java.sql.DatabaseMetaData#getCatalogs()
	 */
	public ResultSet getCatalogs() { //throws SQLException {
		final String[][] tableCols = new String[CATALOG_FIELDS.length][];
		tableCols[0] = connection.urlDir.list(DirectoryFilter.FILTER);
		return new ResultSetArray(tableCols, CATALOG_FIELDS); //only a single Column! No Default
	}

	/**
	 * Delegates to {@link #getCatalogs()}.
	 *
	 * @see java.sql.DatabaseMetaData#getSchemas()
	 */
	public ResultSet getSchemas() { //throws SQLException {
		return getCatalogs();
	}

	/**
	 * Returns {@code new ResultSetArray(new String[][] { TABLE_TYPES }, TABLE_TYPES_FIELDS)}.
	 *
	 * only single Column
	 *
	 * @see java.sql.DatabaseMetaData#getTableTypes()
	 */
	final public ResultSet getTableTypes() { //throws SQLException {
		//only single Column
		return new ResultSetArray(new String[][] { TABLE_TYPES }, TABLE_TYPES_FIELDS); 
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#allProceduresAreCallable()
	 */
	public boolean allProceduresAreCallable() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#allTablesAreSelectable()
	 */
	public boolean allTablesAreSelectable() throws SQLException {
		return true;
	}

	/**
	 * Returns {@code "anonymous"}.
	 *
	 * @see java.sql.DatabaseMetaData#getUserName()
	 */
	public String getUserName() { //throws SQLException {
		return "anonymous";
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#isReadOnly()
	 */
	public boolean isReadOnly() { //throws SQLException {
		return false;
	}

	/**
	 * Returns {@code " ".compareTo("A") > 0}.
	 *
	 * @see java.sql.DatabaseMetaData#nullsAreSortedHigh()
	 */
	public boolean nullsAreSortedHigh() { //throws SQLException {
		return " ".compareTo("A") > 0;
	}

	/**
	 * Reports the negation of {@link #nullsAreSortedHigh()}.
	 *
	 * @see java.sql.DatabaseMetaData#nullsAreSortedLow()
	 */
	public boolean nullsAreSortedLow() { //throws SQLException {
		return !nullsAreSortedHigh();
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#nullsAreSortedAtStart()
	 */
	public boolean nullsAreSortedAtStart() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#nullsAreSortedAtEnd()
	 */
	public boolean nullsAreSortedAtEnd() { //throws SQLException {
		return false;
	}

	/**
	 * Returns {@code getDriverMajorVersion() + "." + getDriverMinorVersion()}.
	 *
	 * @see java.sql.DatabaseMetaData#getDriverVersion()
	 */
	public String getDriverVersion() { //throws SQLException {
		return getDriverMajorVersion() + "." + getDriverMinorVersion();
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#usesLocalFiles()
	 */
	public boolean usesLocalFiles() { //throws SQLException {
		return true;
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#usesLocalFilePerTable()
	 */
	public boolean usesLocalFilePerTable() { //throws SQLException {
		return true;
	}

	/**Retrieves a comma-separated list of all of this database's SQL keywords 
		 * that are NOT also SQL92 keywords.  
		 * @see java.sql.DatabaseMetaData#getSQLKeywords()
		 */
	public String getSQLKeywords() { //throws SQLException {
		return "";
	}

	/**
	 * Returns an empty string; this driver reports no numeric functions.
	 *
	 * @see java.sql.DatabaseMetaData#getNumericFunctions()
	 */
	public String getNumericFunctions() { //throws SQLException {
		return "";
	}

	/**
	 * Returns {@code ""}.
	 *
	 * @see java.sql.DatabaseMetaData#getStringFunctions()
	 */
	public String getStringFunctions() { //throws SQLException {
		return "";
	}

	/**
	 * Returns {@code ""}.
	 *
	 * @see java.sql.DatabaseMetaData#getSystemFunctions()
	 */
	public String getSystemFunctions() { //throws SQLException {
		return "";
	}

	/**
	 * Returns {@code ""}.
	 *
	 * @see java.sql.DatabaseMetaData#getTimeDateFunctions()
	 */
	public String getTimeDateFunctions() { //throws SQLException {
		return "";
	}

	/**
	 * Returns {@code " "}.
	 *
	 * @see java.sql.DatabaseMetaData#getIdentifierQuoteString()
	 */
	public String getIdentifierQuoteString() { //throws SQLException {
		return " "; //indicates no Quoting
	}

	/**
	 * Returns {@code "\\"}.
	 *
	 * @see java.sql.DatabaseMetaData#getSearchStringEscape()
	 */
	public String getSearchStringEscape() { //throws SQLException {
		return "\\";
	}

	/**
	 * Returns {@code ""}.
	 *
	 * @see java.sql.DatabaseMetaData#getExtraNameCharacters()
	 */
	public String getExtraNameCharacters() { //throws SQLException {
		return "";
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsAlterTableWithAddColumn()
	 */
	public boolean supportsAlterTableWithAddColumn() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsAlterTableWithDropColumn()
	 */
	public boolean supportsAlterTableWithDropColumn() { //throws SQLException {
		return false;
	}

	/**No support, becase null is not distinct from ""
		 * @see java.sql.DatabaseMetaData#nullPlusNonNullIsNull()	 */
	public boolean nullPlusNonNullIsNull() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsConvert()
	 */
	public boolean supportsConvert() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false} regardless of {@code fromType}/{@code toType}; {@code CONVERT} is
	 * not supported between any pair of SQL types.
	 *
	 * @see java.sql.DatabaseMetaData#supportsConvert(int, int)
	 */
	public boolean supportsConvert(int fromType, int toType) { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsDifferentTableCorrelationNames()
	 */
	public boolean supportsDifferentTableCorrelationNames() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsExpressionsInOrderBy()
	 */
	public boolean supportsExpressionsInOrderBy() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsOrderByUnrelated()
	 */
	public boolean supportsOrderByUnrelated() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsGroupBy()
	 */
	public boolean supportsGroupBy() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsGroupByUnrelated()
	 */
	public boolean supportsGroupByUnrelated() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsGroupByBeyondSelect()
	 */
	public boolean supportsGroupByBeyondSelect() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsLikeEscapeClause()
	 */
	public boolean supportsLikeEscapeClause() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsMultipleResultSets()
	 */
	public boolean supportsMultipleResultSets() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsMultipleTransactions()
	 */
	public boolean supportsMultipleTransactions() { //throws SQLException {
		return false;
	}

	/**
		 * Only possible, if the corresponding MetaData is stored in the File.   
		 * @see java.sql.DatabaseMetaData#supportsNonNullableColumns() 
		 */
	public boolean supportsNonNullableColumns() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsMinimumSQLGrammar()
	 */
	public boolean supportsMinimumSQLGrammar() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsCoreSQLGrammar()
	 */
	public boolean supportsCoreSQLGrammar() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsExtendedSQLGrammar()
	 */
	public boolean supportsExtendedSQLGrammar() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsANSI92EntryLevelSQL()
	 */
	public boolean supportsANSI92EntryLevelSQL() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsANSI92IntermediateSQL()
	 */
	public boolean supportsANSI92IntermediateSQL() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsANSI92FullSQL()
	 */
	public boolean supportsANSI92FullSQL() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsIntegrityEnhancementFacility()
	 */
	public boolean supportsIntegrityEnhancementFacility() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsOuterJoins()
	 */
	public boolean supportsOuterJoins() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsFullOuterJoins()
	 */
	public boolean supportsFullOuterJoins() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsLimitedOuterJoins()
	 */
	public boolean supportsLimitedOuterJoins() { //throws SQLException {
		return false;
	}

	/**
	 * Returns {@code "Directory"}.
	 *
	 * @see java.sql.DatabaseMetaData#getSchemaTerm()
	 */
	public String getSchemaTerm() { //throws SQLException {
		return "Directory";
	}

	/**
	 * Returns {@code "notSupported"}.
	 *
	 * @see java.sql.DatabaseMetaData#getProcedureTerm()
	 */
	public String getProcedureTerm() { //throws SQLException {
		return "notSupported";
	}

	/**
	 * Returns {@code "Directory"}.
	 *
	 * @see java.sql.DatabaseMetaData#getCatalogTerm()
	 */
	public String getCatalogTerm() { //throws SQLException {
		return "Directory";
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#isCatalogAtStart()
	 */
	public boolean isCatalogAtStart() { //throws SQLException {
		return true;
	}

	/**
	 * Returns {@code "/"}.
	 *
	 * @see java.sql.DatabaseMetaData#getCatalogSeparator()
	 */
	public String getCatalogSeparator() { //throws SQLException {
		return "/";
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsSchemasInDataManipulation()
	 */
	public boolean supportsSchemasInDataManipulation() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsSchemasInProcedureCalls()
	 */
	public boolean supportsSchemasInProcedureCalls() { //throws SQLException {
		return false;
	}

	/** (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsSchemasInTableDefinitions()
		 */
	public boolean supportsSchemasInTableDefinitions() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsSchemasInIndexDefinitions()
	 */
	public boolean supportsSchemasInIndexDefinitions() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsSchemasInPrivilegeDefinitions()
	 */
	public boolean supportsSchemasInPrivilegeDefinitions() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInDataManipulation()
	 */
	public boolean supportsCatalogsInDataManipulation() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInProcedureCalls()
	 */
	public boolean supportsCatalogsInProcedureCalls() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInTableDefinitions()
	 */
	public boolean supportsCatalogsInTableDefinitions() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInIndexDefinitions()
	 */
	public boolean supportsCatalogsInIndexDefinitions() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInPrivilegeDefinitions()
	 */
	public boolean supportsCatalogsInPrivilegeDefinitions() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsPositionedDelete()
	 */
	public boolean supportsPositionedDelete() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsPositionedUpdate()
	 */
	public boolean supportsPositionedUpdate() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsSelectForUpdate()
	 */
	public boolean supportsSelectForUpdate() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsStoredProcedures()
	 */
	public boolean supportsStoredProcedures() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInComparisons()
	 */
	public boolean supportsSubqueriesInComparisons() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInExists()
	 */
	public boolean supportsSubqueriesInExists() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInIns()
	 */
	public boolean supportsSubqueriesInIns() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInQuantifieds()
	 */
	public boolean supportsSubqueriesInQuantifieds() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsCorrelatedSubqueries()
	 */
	public boolean supportsCorrelatedSubqueries() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsUnion()
	 */
	public boolean supportsUnion() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsUnionAll()
	 */
	public boolean supportsUnionAll() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsOpenCursorsAcrossCommit()
	 */
	public boolean supportsOpenCursorsAcrossCommit() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsOpenCursorsAcrossRollback()
	 */
	public boolean supportsOpenCursorsAcrossRollback() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsOpenStatementsAcrossCommit()
	 */
	public boolean supportsOpenStatementsAcrossCommit() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsOpenStatementsAcrossRollback()
	 */
	public boolean supportsOpenStatementsAcrossRollback() { //throws SQLException {
		return false;
	}

	/**
	 * Returns {@code Integer.MAX_VALUE}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxBinaryLiteralLength()
	 */
	public int getMaxBinaryLiteralLength() { //throws SQLException {
		return Integer.MAX_VALUE;
	}

	/**
	 * Returns {@code Integer.MAX_VALUE}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxCharLiteralLength()
	 */
	public int getMaxCharLiteralLength() { //throws SQLException {
		return Integer.MAX_VALUE;
	}

	/**
	 * Returns {@code Integer.MAX_VALUE}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxColumnNameLength()
	 */
	public int getMaxColumnNameLength() { //throws SQLException {
		return Integer.MAX_VALUE;
	}

	/**
	 * Returns {@code 0}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInGroupBy()
	 */
	public int getMaxColumnsInGroupBy() { //throws SQLException {
		return 0;
	}

	/**
	 * Returns {@code 1}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInIndex()
	 */
	public int getMaxColumnsInIndex() { //throws SQLException {
		return 1;
	}

	/**
	 * Returns {@code 1}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInOrderBy()
	 */
	public int getMaxColumnsInOrderBy() { //throws SQLException {
		return 1;
	}

	/**
	 * Returns {@code Integer.MAX_VALUE}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInSelect()
	 */
	public int getMaxColumnsInSelect() { //throws SQLException {
		return Integer.MAX_VALUE;
	}

	/**
	 * Returns {@code Integer.MAX_VALUE}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInTable()
	 */
	public int getMaxColumnsInTable() { //throws SQLException {
		return Integer.MAX_VALUE;
	}

	/**
	 * Returns {@code 1}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxConnections()
	 */
	public int getMaxConnections() { //throws SQLException {
		return 1;
	}

	/**
	 * Returns {@code Integer.MAX_VALUE}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxCursorNameLength()
	 */
	public int getMaxCursorNameLength() { //throws SQLException {
		return Integer.MAX_VALUE;
	}

	/**
	 * Returns {@code Integer.MAX_VALUE}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxIndexLength()
	 */
	public int getMaxIndexLength() { //throws SQLException {
		return Integer.MAX_VALUE;
	}

	/**
	 * Returns {@code Integer.MAX_VALUE}; schema names are directory names and are not
	 * length-limited by this driver.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxSchemaNameLength()
	 */
	public int getMaxSchemaNameLength() { //throws SQLException {
		return Integer.MAX_VALUE;
	}

	/**
	 * Returns {@code 0}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxProcedureNameLength()
	 */
	public int getMaxProcedureNameLength() { //throws SQLException {
		return 0;
	}

	/**
	 * Returns {@code 0}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxCatalogNameLength()
	 */
	public int getMaxCatalogNameLength() { //throws SQLException {
		return 0;
	}

	/**
	 * Returns {@code storesMixedCaseIdentifiers()}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsMixedCaseQuotedIdentifiers()
	 */
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		return storesMixedCaseIdentifiers();
	}

	/**
	 * Returns {@code storesUpperCaseIdentifiers()}.
	 *
	 * @see java.sql.DatabaseMetaData#storesUpperCaseQuotedIdentifiers()
	 */
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return storesUpperCaseIdentifiers();
	}

	/**
	 * Returns {@code storesLowerCaseIdentifiers()}.
	 *
	 * @see java.sql.DatabaseMetaData#storesLowerCaseQuotedIdentifiers()
	 */
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return storesLowerCaseIdentifiers();
	}

	/**
	 * Returns {@code storesMixedCaseIdentifiers()}.
	 *
	 * @see java.sql.DatabaseMetaData#storesMixedCaseQuotedIdentifiers()
	 */
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return storesMixedCaseIdentifiers();
	}

	/**
	 * Returns {@code Integer.MAX_VALUE}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxRowSize()
	 */
	public int getMaxRowSize() { //throws SQLException {
		return Integer.MAX_VALUE;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#doesMaxRowSizeIncludeBlobs()
	 */
	public boolean doesMaxRowSizeIncludeBlobs() { //throws SQLException {
		return false;
	}

	/**
	 * Returns {@code 0}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxStatementLength()
	 */
	public int getMaxStatementLength() { //throws SQLException {
		return 0;
	}

	/**
	 * Returns {@code 0}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxStatements()
	 */
	public int getMaxStatements() { //throws SQLException {
		return 0;
	}

	/**
	 * Returns {@code Integer.MAX_VALUE}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxTableNameLength()
	 */
	public int getMaxTableNameLength() { //throws SQLException {
		return Integer.MAX_VALUE;
	}

	/**
	 * Returns {@code 1}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxTablesInSelect()
	 */
	public int getMaxTablesInSelect() { //throws SQLException {
		return 1;
	}

	/**
	 * Returns {@code Integer.MAX_VALUE}.
	 *
	 * @see java.sql.DatabaseMetaData#getMaxUserNameLength()
	 */
	public int getMaxUserNameLength() { //throws SQLException {
		return Integer.MAX_VALUE;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsTransactions()
	 */
	public boolean supportsTransactions() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsDataDefinitionAndDataManipulationTransactions()
	 */
	public boolean supportsDataDefinitionAndDataManipulationTransactions() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsDataManipulationTransactionsOnly()
	 */
	public boolean supportsDataManipulationTransactionsOnly() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()
	 */
	public boolean dataDefinitionCausesTransactionCommit() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#dataDefinitionIgnoredInTransactions()
	 */
	public boolean dataDefinitionIgnoredInTransactions() { //throws SQLException {
		return false;
	}

	/**
	 * Always returns {@code null}; this filesystem-backed driver has no notion of stored
	 * procedures.
	 *
	 * @see java.sql.DatabaseMetaData#getProcedures(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getProcedures(
		String catalog,
		String schemaPattern,
		String procedureNamePattern) { //throws SQLException {
		return null;
	}

	/**
	 * Always returns {@code null}; this filesystem-backed driver has no notion of stored
	 * procedures.
	 *
	 * @see java.sql.DatabaseMetaData#getProcedureColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getProcedureColumns(
		String catalog,
		String schemaPattern,
		String procedureNamePattern,
		String columnNamePattern) { //throws SQLException {
		return null;
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsResultSetType(int)
	 */
	public boolean supportsResultSetType(int type) { //throws SQLException {
		return true;
	}

	/**
	 * Reports {@code true} for every combination of {@code type} and {@code concurrency}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsResultSetConcurrency(int, int)
	 */
	public boolean supportsResultSetConcurrency(
		int type,
		int concurrency) { //throws SQLException {
		return true;
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#ownUpdatesAreVisible(int)
	 */
	public boolean ownUpdatesAreVisible(int type) { //throws SQLException {
		return true;
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#ownDeletesAreVisible(int)
	 */
	public boolean ownDeletesAreVisible(int type) { //throws SQLException {
		return true;
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#ownInsertsAreVisible(int)
	 */
	public boolean ownInsertsAreVisible(int type) { //throws SQLException {
		return true;
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#othersUpdatesAreVisible(int)
	 */
	public boolean othersUpdatesAreVisible(int type) { //throws SQLException {
		return true;
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#othersDeletesAreVisible(int)
	 */
	public boolean othersDeletesAreVisible(int type) { //throws SQLException {
		return true;
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#othersInsertsAreVisible(int)
	 */
	public boolean othersInsertsAreVisible(int type) { //throws SQLException {
		return true;
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#updatesAreDetected(int)
	 */
	public boolean updatesAreDetected(int type) { //throws SQLException {
		return true;
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#deletesAreDetected(int)
	 */
	public boolean deletesAreDetected(int type) { //throws SQLException {
		return true;
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#insertsAreDetected(int)
	 */
	public boolean insertsAreDetected(int type) { //throws SQLException {
		return true;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsBatchUpdates()
	 */
	public boolean supportsBatchUpdates() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsSavepoints()
	 */
	public boolean supportsSavepoints() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsNamedParameters()
	 */
	public boolean supportsNamedParameters() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsMultipleOpenResults()
	 */
	public boolean supportsMultipleOpenResults() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsGetGeneratedKeys()
	 */
	public boolean supportsGetGeneratedKeys() { //throws SQLException {
		return false;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsResultSetHoldability(int)
	 */
	public boolean supportsResultSetHoldability(int holdability) { //throws SQLException {
		return false;
	}

	/**
	 * Returns {@code 0}.
	 *
	 * @see java.sql.DatabaseMetaData#getResultSetHoldability()
	 */
	public int getResultSetHoldability() { //throws SQLException {
		return 0;
	}

	/**
	 * Reports {@code true}.
	 *
	 * @see java.sql.DatabaseMetaData#locatorsUpdateCopy()
	 */
	public boolean locatorsUpdateCopy() { //throws SQLException {
		return true;
	}

	/**
	 * Reports {@code false}.
	 *
	 * @see java.sql.DatabaseMetaData#supportsStatementPooling()
	 */
	public boolean supportsStatementPooling() { //throws SQLException {
		return false;
	}

	/**
	 * Stub override of {@link java.sql.DatabaseMetaData#autoCommitFailureClosesAllResultSets}; not implemented and always returns false.
	 *
	 * @see java.sql.DatabaseMetaData#autoCommitFailureClosesAllResultSets()
	 */
	public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Stub override of {@link java.sql.DatabaseMetaData#getClientInfoProperties}; not implemented and always returns null.
	 *
	 * @see java.sql.DatabaseMetaData#getClientInfoProperties()
	 */
	public ResultSet getClientInfoProperties() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stub override of {@link java.sql.DatabaseMetaData#getFunctionColumns}; not implemented and always returns null.
	 *
	 * @see java.sql.DatabaseMetaData#getFunctionColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getFunctionColumns(String arg0, String arg1, String arg2, String arg3) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stub override of {@link java.sql.DatabaseMetaData#getFunctions}; not implemented and always returns null.
	 *
	 * @see java.sql.DatabaseMetaData#getFunctions(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getFunctions(String arg0, String arg1, String arg2) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stub override of {@link java.sql.DatabaseMetaData#getRowIdLifetime}; not implemented and always returns null.
	 *
	 * @see java.sql.DatabaseMetaData#getRowIdLifetime()
	 */
	public RowIdLifetime getRowIdLifetime() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stub override of {@link java.sql.DatabaseMetaData#getSchemas}; not implemented and always returns null.
	 *
	 * @see java.sql.DatabaseMetaData#getSchemas(java.lang.String, java.lang.String)
	 */
	public ResultSet getSchemas(String arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Reports {@code false}.
	 *
	 * TODO Auto-generated method stub
	 *
	 * @see java.sql.DatabaseMetaData#supportsStoredFunctionsUsingCallSyntax(
	 */
	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Stub override of {@link java.sql.Wrapper#isWrapperFor}; not implemented and always returns false.
	 *
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	public boolean isWrapperFor(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Stub override of {@link java.sql.Wrapper#unwrap}; not implemented and always returns null.
	 *
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	public Object unwrap(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns {@code sqlStateXOpen}.
	 *
	 * @see java.sql.DatabaseMetaData#getSQLStateType()
	 */
	public int getSQLStateType() { //throws SQLException {
		return sqlStateXOpen; //sqlStateSQL99 
	}

	/**
	 * Returns {@code 0}.
	 *
	 * @see java.sql.DatabaseMetaData#getJDBCMajorVersion()
	 */
	public int getJDBCMajorVersion() { //throws SQLException {
		return 0;
	}

	/**
	 * Returns {@code 0}.
	 *
	 * @see java.sql.DatabaseMetaData#getJDBCMinorVersion()
	 */
	public int getJDBCMinorVersion() { //throws SQLException {
		return 0;
	}

	/** @see #getProcedures(String, String, String) returns this List of Columns */
	protected static final String[] PROCEDURE_FIELDS 
	= { "PROCEDURE_CAT", //String => table catalog (may be null)
		STR_PROCEDURE_SCHEMA, //String => table schema (may be null) 
		STR_PROCEDURE_NAME, //String => table name 
		"RESERVED", //for future use 
		"RESERVED", //for future use 
		"RESERVED", //for future use 
		STR_REMARKS, //String => explanatory comment on the procedure 
		"PROCEDURE_TYPE" //short => kind of procedure, @see PROCEDURE_TYPES
	};

	/** @see #getProcedures(String, String, String) returns this List of Columns */
	protected static final String[] PROCEDURE_TYPES 
	= { "procedureResultUnknown", // - May return a result
		"procedureNoResult", // - Does not return a result 
		"procedureReturnsResult" // - Returns a result 
	};

	/** @see #getProcedureColumns(String, String, String, String) returns this List of Columns */
	protected static final String[] PROCEDURE_COLUMN_FIELDS 
	= { "PROCEDURE_CAT", //String => table catalog (may be null)
		STR_PROCEDURE_SCHEMA, //String => table schema (may be null) 
		STR_PROCEDURE_NAME, //String => table name 
		STR_COLUMN_NAME, //String => column name 
		STR_COLUMN_NAME, //String => column/parameter name 
		"COLUMN_TYPE", //Short => kind of column/parameter, @see COLUMN_TYPES
		STR_DATA_TYPE, //int => SQL type from java.sql.Types 
		STR_TYPE_NAME,
		//String => SQL type name, for a UDT type the type name is fully qualified
		STR_PRECISION, //int => precision 
		"LENGTH", //int => length in bytes of data 
		"SCALE", //short => scale 
		"RADIX", //short => radix 
		STR_NULLABLE, //short => can it contain NULL, @see PROC_NULLABLE_TYPES 
		STR_REMARKS //String => comment describing parameter/column 
	};

	/** @see #getProcedureColumns(String, String, String, String) returns this List of Columns */
	protected static final String[] COLUMN_TYPES 
	= { "procedureColumnUnknown", // - nobody knows
		"procedureColumnIn", // - IN parameter 
		"procedureColumnInOut", // - INOUT parameter 
		"procedureColumnOut", // - OUT parameter 
		"procedureColumnReturn", // - procedure return value 
		"procedureColumnResult" // - result column in ResultSet 
	};

	/** @see #getProcedureColumns(String, String, String, String) returns this List of Columns */
	protected static final String[] PROC_NULLABLE_TYPES 
	= { "procedureNoNulls", // - does not allow NULL values
		"procedureNullable", // - allows NULL values 
		"procedureNullableUnknown" // - nullability unknown 
	};

	/** @see #getTables(String, String, String, String[]) returns this List of Columns */
	protected static final String[] TABLE_FIELDS 
	= { STR_TABLE_CATALOG, //String => table catalog (may be null)
		STR_TABLE_SCHEMA, //String => table schema (may be null) 
		STR_TABLE_NAME, //String => table name 
		"TABLE_TYPE", //String => table type, @see TABLE_TYPES
		STR_REMARKS, //String => explanatory comment on the table 
		STR_TYPE_CATALOG, //String => the types catalog (may be null) 
		STR_TYPE_SCHEMA, //String => the types schema (may be null) 
		STR_TYPE_NAME, //String => type name (may be null) 
		"SELF_REFERENCING_COL_NAME",
		//String => name of the designated "identifier" column of a typed table (may be null)
		"REF_GENERATION" //String => specifies how values in SELF_REFERENCING_COL_NAME are created. @see REF_GENERATION_TYPES 
	};

	/** User-defined Reference-ID Generation  */
	protected static final String REF_GENERATION_TYPE_USER = "USER";

	/** @see #getTables(String, String, String, String[]) returns this List of Columns */
	protected static final String[] REF_GENERATION_TYPES 
	= { "SYSTEM", REF_GENERATION_TYPE_USER, "DERIVED" };

	/** Name of the table Type for a physical Table */
	protected static final String TABLE_TYPE_TABLE = "TABLE";

	/** @see #getTables(String, String, String, String[]) returns this List of Columns */
	protected static final String[] TABLE_TYPES 
	= { TABLE_TYPE_TABLE,
		"VIEW",
		"SYSTEM TABLE",
		"GLOBAL TEMPORARY",
		"LOCAL TEMPORARY",
		"ALIAS",
		"SYNONYM" };

	/** @see #getSchemas() returns this List of Columns */
	protected static final String[] SCHEMA_FIELDS 
	= { STR_TABLE_SCHEMA, //String => table schema (may be null)
		"TABLE_CATALOG", //String => table catalog (may be null)
	};

	/** @see #getCatalogs() returns this List of Columns */
	protected static final String[] CATALOG_FIELDS = { STR_TABLE_CATALOG };

	/** @see #getTables(String, String, String, String[]) returns this List of Columns */
	protected static final String[] TABLE_TYPES_FIELDS = { "TABLE_TYPE" };

	/** @see #getColumns(String, String, String, String) returns this List of Columns */
	protected static final String[] COLUMN_FIELDS 
	= { STR_TABLE_CATALOG, //String => table catalog (may be null)
		STR_TABLE_SCHEMA, //String => table schema (may be null) 
		STR_TABLE_NAME, //String => table name 
		STR_COLUMN_NAME, //String => column name 
		STR_DATA_TYPE, //int => SQL type from java.sql.Types 
		STR_TYPE_NAME,
		//String => Data source dependent type name, for a UDT the type name is fully qualified
		STR_COLUMN_SIZE,
		//int => column size. For char or date types this is the maximum number of characters, for numeric or decimal types this is precision
		STR_BUFFER_LENGTH, //is not used
		STR_DECIMAL_DIGITS, //int => the number of fractional digits 
		STR_NUMBER_PRECISION_RADIX, //int => Radix (typically either 10 or 2)
		STR_NULLABLE, //int => is NULL allowed @see NULLABLE_TYPES
		STR_REMARKS, //String => comment describing column (may be null)
		"COLUMN_DEF", //String => default value (may be null) 
		"SQL_DATA_TYPE", //int => unused
		"SQL_DATETIME_SUB", //int => unused 
		"CHAR_OCTET_LENGTH",
		//int => for char types the maximum number of bytes in the column
		"ORDINAL_POSITION", //int => index of column in table (starting at 1)
		STR_IS_NULLABLE, //String => @see IS_NULLABLE_TYPES
		"SCOPE_CATLOG",
		//String => catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
		"SCOPE_SCHEMA",
		//String => schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)
		"SCOPE_TABLE",
		//String => table name that this the scope of a reference attribure (null if the DATA_TYPE isn't REF)
		"SOURCE_DATA_TYPE" //short => source type of a distinct type or user-generated Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF) 
	};

	/** @see #getColumns(String, String, String, String) returns this List of Columns */
	protected static final String[] NULLABLE_TYPES 
	= { "columnNoNulls", "columnNullable", "columnNullableUnknown" };

	/** @see #getColumns(String, String, String, String) returns this List of Columns */
	protected static final String[] IS_NULLABLE_TYPES = { "NO", "YES", "" };

	/** @see #getColumnPrivileges(String, String, String, String) returns this List of Columns */
	protected static final String[] COL_PRIVILEGE_FIELDS 
	= { STR_TABLE_CATALOG, //String => table catalog (may be null)
		STR_TABLE_SCHEMA, //String => table schema (may be null) 
		STR_TABLE_NAME, //String => table name 
		STR_COLUMN_NAME, //String => column name 
		STR_GRANTOR, //String => grantor of access (may be null) 
		STR_GRANTEE, //String => grantee of access 
		STR_PRIVILEGE, //String => name of access, @see PRIVILEGE_TYPES 
		STR_IS_GRANTABLE //String => @see IS_GRANTABLE_TYPES 
	};

	/** @see #getColumnPrivileges(String, String, String, String) returns this List of Columns */
	protected static final String[] PRIVILEGE_TYPES 
	= { "SELECT", "INSERT", "UPDATE", "DELETE", "REFRENCES" };

	protected static final String[] IS_GRANTABLE_TYPES = IS_NULLABLE_TYPES;

	/** @see #getColumnPrivileges(String, String, String, String) returns this List of Columns */
	protected static final String[] TABLE_PRIVILEGE_FIELDS 
	= { STR_TABLE_CATALOG, //String => table catalog (may be null)
		STR_TABLE_SCHEMA, //String => table schema (may be null) 
		STR_TABLE_NAME, //String => table name 
		STR_GRANTOR, //String => grantor of access (may be null) 
		STR_GRANTEE, //String => grantee of access 
		STR_PRIVILEGE, //String => name of access, @see PRIVILEGE_TYPES 
		STR_IS_GRANTABLE //String => @see IS_GRANTABLE_TYPES 
	};

	/** @see #get returns this List of Columns */
	protected static final String[] BEST_ROW_ID_FIELDS 
	= { "SCOPE",
		//short => actual scope of result, @see ROW_ID_SCOPE_TYPES
		STR_COLUMN_NAME, //String => column name
		STR_DATA_TYPE, //int => SQL data type from java.sql.Types 
		STR_TYPE_NAME,
		//String => Data source dependent type name, for a UDT the type name is fully qualified
		STR_COLUMN_SIZE, //int => precision 
		STR_BUFFER_LENGTH, //int => not used 
		STR_DECIMAL_DIGITS, //short => scale 
		"PSEUDO_COLUMN" //short => is this a pseudo column like an Oracle ROWID, @see PSEUDO_COLUMN_TYPES 
	};

	/** @see #get returns this List of Columns */
	protected static final String[] ROW_ID_SCOPE_TYPES 
	= { "bestRowTemporary", //very temporary, while using row
		"bestRowTransaction", //valid for remainder of current transaction 
		"bestRowSession" //valid for remainder of current session 
	};

	/** @see #get returns this List of Columns */
	protected static final String[] PSEUDO_COLUMN_TYPES 
	= { "bestRowUnknown", // - may or may not be pseudo column
		"bestRowNotPseudo", //- is NOT a pseudo column 
		"bestRowPseudo", // - is a pseudo column 
	};

	/** @see #getVersionColumns(String, String, String) returns this List of Columns */
	protected static final String[] VERSION_COLUMN_FIELDS 
	= { "SCOPE short", // => is not used
		STR_COLUMN_NAME, // String => column name 
		STR_DATA_TYPE, // int => SQL data type from java.sql.Types 
		STR_TYPE_NAME, // String => Data source-dependent type name 
		STR_COLUMN_SIZE, // int => precision 
		STR_BUFFER_LENGTH, // int => length of column value in bytes 
		STR_DECIMAL_DIGITS, // short => scale 
		"PSEUDO_COLUMN short" // => whether this is pseudo column like an Oracle ROWID 
	};

	/** @see #getVersionColumns(String, String, String) returns this List of Columns */
	protected static final String[] VERSION_COLUMN_TYPES 
	= { "versionColumnUnknown", // - may or may not be pseudo column
		"versionColumnNotPseudo", // - is NOT a pseudo column 
		"versionColumnPseudo", // - is a pseudo column 
	};

	/** @see #getPrimaryKeys(String, String, String) returns this List of Columns */
	protected static final String[] PRIMARY_KEYS_FIELDS 
	= { STR_TABLE_CATALOG, // String => table catalog (may be null)
		STR_TABLE_SCHEMA, // String => table schema (may be null) 
		STR_TABLE_NAME, // String => table name 
		STR_COLUMN_NAME, // String => column name 
		"KEY_SEQ", // short => sequence number within primary key 
		"PK_NAME", // String => primary key name (may be null) 
	};

	/** 
		 * @see #getExportedKeys(String, String, String) returns this List of Columns 
		 * @see #getImportedKeys(String, String, String) returns this List of Columns
		 * @see #getCrossReference(String, String, String, String, String, String) returns this List of Columns 
		 */
	protected static final String[] KEYS_FIELDS 
	= { "PKTABLE_CAT", // String => primary key table catalog (may be null)
		"PKTABLE_SCHEM", // String => primary key table schema (may be null) 
		"PKTABLE_NAME", // String => primary key table name 
		"PKCOLUMN_NAME", // String => primary key column name 
		"FKTABLE_CAT",
		// String => foreign key table catalog (may be null) being exported (may be null)
		"FKTABLE_SCHEM",
		// String => foreign key table schema (may be null) being exported (may be null)
		"FKTABLE_NAME", // String => foreign key table name being exported 
		"FKCOLUMN_NAME", // String => foreign key column name being exported 
		"KEY_SEQ", // short => sequence number within foreign key 
		"UPDATE_RULE",
		// short => What happens to foreign key when primary is updated: @see RULE_TYPES
		"DELETE_RULE",
		// short => What happens to the foreign key when primary is deleted. @see RULE_TYPES
		"FK_NAME", // String => foreign key name (may be null) 
		"PK_NAME", // String => primary key name (may be null) 
		"DEFERRABILITY",
		// short => can the evaluation of foreign key constraints be deferred until commit. @see DEFERRABILITY_TYPES
	};

	/** 
		 * @see #getExportedKeys(String, String, String) returns this List of Columns 
		 * @see #getImportedKeys(String, String, String) returns this List of Columns
		 * @see #getCrossReference(String, String, String, String, String, String) returns this List of Columns 
		 */
	protected static final String[] RULE_TYPES 
	= { "importedNoAction",
		// - do not allow update of primary key if it has been imported
		"importedKeyCascade", // - change imported key to agree with primary key update 
		"importedKeySetNull", // - change imported key to NULL if its primary key has been updated 
		"importedKeySetDefault",
		// - change imported key to default values if its primary key has been updated
		"importedKeyRestrict", // - same as importedKeyNoAction (for ODBC 2.x compatibility) 
	};

	/** @see #getImportedKeys(String, String, String) returns this List of Columns */
	protected static final String[] DEFERRABILITY_TYPES 
	= { "importedKeyInitiallyDeferred", // - see SQL92 for definition
		"importedKeyInitiallyImmediate", // - see SQL92 for definition 
		"importedKeyNotDeferrable", // - see SQL92 for definition 
	};

	/** @see #getTypeInfo returns this List of Columns */
	protected static final String[] TYPE_INFO_FIELDS 
	= { STR_TYPE_NAME, // String => Type name
		STR_DATA_TYPE, // int => SQL data type from java.sql.Types 
		STR_PRECISION, // int => maximum precision 
		"LITERAL_PREFIX", // String => prefix used to quote a literal (may be null) 
		"LITERAL_SUFFIX", // String => suffix used to quote a literal (may be null) 
		"CREATE_PARAMS", // String => parameters used in creating the type (may be null) 
		STR_NULLABLE, // short => can you use NULL for this type. @see TYPE_NULLABLE_TYPES 
		"CASE_SENSITIVE", // boolean=> is it case sensitive. 
		"SEARCHABLE", // short => can you use "WHERE" based on this type: @see SEARCHABLE_TYPES 
		"UNSIGNED_ATTRIBUTE", // boolean => is it unsigned. 
		"FIXED_PREC_SCALE", // boolean => can it be a money value. 
		"AUTO_INCREMENT", // boolean => can it be used for an auto-increment value. 
		"LOCAL_TYPE_NAME", // String => localized version of type name (may be null) 
		"MINIMUM_SCALE", // short => minimum scale supported 
		"MAXIMUM_SCALE", // short => maximum scale supported 
		"SQL_DATA_TYPE", // int => unused 
		"SQL_DATETIME_SUB", // int => unused 
		STR_NUMBER_PRECISION_RADIX, // int => usually 2 or 10 
	};

	/** @see #getTypeInfo returns this List of Columns */
	protected static final String[] TYPE_NULLABLE_TYPES 
	= { "typeNoNulls", // - does not allow NULL values
		"typeNullable", // - allows NULL values 
		"typeNullableUnknown", // - nullability unknown 
	};

	/** @see #getTypeInfo returns this List of Columns */
	protected static final String[] SEARCHABLE_TYPES 
	= { "typePredNone", // - No support
		"typePredChar", // - Only supported with WHERE .. LIKE 
		"typePredBasic", // - Supported except for WHERE .. LIKE 
		"typeSearchable", // - Supported for all WHERE .. 
	};

	/** @see #getIndexInfo(String, String, String, boolean, boolean) returns this List of Columns */
	protected static final String[] INDEX_FIELDS 
	= { STR_TABLE_CATALOG, // String => table catalog (may be null)
		STR_TABLE_SCHEMA, // String => table schema (may be null) 
		STR_TABLE_NAME, // String => table name 
		"NON_UNIQUE",
		// boolean => Can index values be non-unique. false when TYPE is tableIndexStatistic
		"INDEX_QUALIFIER",
		// String => index catalog (may be null); null when TYPE is tableIndexStatistic
		"INDEX_NAME", // String => index name; null when TYPE is tableIndexStatistic 
		"TYPE", // short => index type: @see INDEX_TYPES  
		"ORDINAL_POSITION",
		// short => column sequence number within index; zero when TYPE is tableIndexStatistic
		STR_COLUMN_NAME, // String => column name; null when TYPE is tableIndexStatistic 
		"ASC_OR_DESC",
		// String => column sort sequence, "A" => ascending, "D" => descending, may be null if sort sequence is not supported; null when TYPE is tableIndexStatistic
		"CARDINALITY",
		// int => When TYPE is tableIndexStatistic, then this is the number of rows in the table; otherwise, it is the number of unique values in the index.
		"PAGES",
		// int => When TYPE is tableIndexStatisic then this is the number of pages used for the table, otherwise it is the number of pages used for the current index.
		"FILTER_CONDITION", // String => Filter condition, if any. (may be null) 
	};

	/** @see #getIndexInfo(String, String, String, boolean, boolean) returns this List of Columns */
	protected static final String[] INDEX_TYPES 
	= { "tableIndexStatistic",
		// - this identifies table statistics that are returned in conjuction with a table's index descriptions
		"tableIndexClustered", // - this is a clustered index 
		"tableIndexHashed", // - this is a hashed index 
		"tableIndexOther", // - this is some other style of index 
	};

	/** @see #get returns this List of Columns */
	protected static final String[] UDT_FIELDS 
	= { STR_TYPE_CATALOG, // String => the type's catalog (may be null)
		STR_TYPE_SCHEMA, // String => type's schema (may be null) 
		STR_TYPE_NAME, // String => type name 
		"CLASS_NAME", // String => Java class name 
		STR_DATA_TYPE,
		// int => type value defined in java.sql.Types. One of JAVA_OBJECT, STRUCT, or DISTINCT
		STR_REMARKS, // String => explanatory comment on the type 
		"BASE_TYPE",
		// short => type code of the source type of a DISTINCT type or the type that implements the user-generated reference type of the SELF_REFERENCING_COLUMN of a structured type as defined in java.sql.Types (null if DATA_TYPE is not DISTINCT or not STRUCT with REFERENCE_GENERATION = USER_DEFINED)
	};

	/** @see #getSuperTypes(String, String, String) returns this List of Columns */
	protected static final String[] SUPER_TYPE_FIELDS 
	= { STR_TYPE_CATALOG, // String => the UDT's catalog (may be null)
		STR_TYPE_SCHEMA, // String => UDT's schema (may be null) 
		STR_TYPE_NAME, // String => type name of the UDT 
		"SUPERTYPE_CAT", // String => the direct super type's catalog (may be null) 
		"SUPERTYPE_SCHEM", // String => the direct super type's schema (may be null) 
		"SUPERTYPE_NAME", // String => the direct super type's name 
	};

	/** @see #getSuperTables(String, String, String) returns this List of Columns */
	protected static final String[] SUPER_TABLES_FIELDS 
	= { STR_TABLE_CATALOG, // String => the type's catalog (may be null)
		STR_TABLE_SCHEMA, // String => type's schema (may be null) 
		STR_TABLE_NAME, // String => type name 
		"SUPERTABLE_NAME", // String => the direct super type's name 
	};

	/** @see #getAttributes(String, String, String, String) returns this List of Columns */
	protected static final String[] ATTRIBUTE_FIELDS 
	= { STR_TYPE_CATALOG, // String => type catalog (may be null)
		STR_TYPE_SCHEMA, // String => type schema (may be null) 
		STR_TYPE_NAME, // String => type name 
		"ATTR_NAME", // String => attribute name 
		STR_DATA_TYPE, // int => attribute type SQL type from java.sql.Types 
		"ATTR_TYPE_NAME",
		// String => Data source dependent type name. For a UDT, the type name is fully qualified. For a REF, the type name is fully qualified and represents the target type of the reference type.
		"ATTR_SIZE",
		// int => column size. For char or date types this is the maximum number of characters; for numeric or decimal types this is precision.
		STR_DECIMAL_DIGITS, // int => the number of fractional digits 
		STR_NUMBER_PRECISION_RADIX, // int => Radix (typically either 10 or 2) 
		STR_NULLABLE, // int => whether NULL is allowed @see ATTR_NULLABLE_TYPES
		STR_REMARKS, // String => comment describing column (may be null) 
		"ATTR_DEF", // String => default value (may be null) 
		"SQL_DATA_TYPE", // int => unused 
		"SQL_DATETIME_SUB", // int => unused 
		"CHAR_OCTET_LENGTH", // int => for char types the maximum number of bytes in the column 
		"ORDINAL_POSITION", // int => index of column in table (starting at 1) 
		STR_IS_NULLABLE,
		// String => "NO" means column definitely does not allow NULL values; "YES" means the column might allow NULL values. An empty string means unknown.
		"SCOPE_CATALOG",
		// String => catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
		"SCOPE_SCHEMA",
		// String => schema of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
		"SCOPE_TABLE",
		// String => table name that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)
		"SOURCE_DATA_TYPE",
		// short => source type of a distinct type or user-generated Ref type,SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF)
	};

	/** @see #get returns this List of Columns */
	protected static final String[] ATTR_NULLABLE_TYPES 
	= { "attributeNoNulls", // - might not allow NULL values
		"attributeNullable", // - definitely allows NULL values 
		"attributeNullableUnknown", // - nullability unknown 
	};

	/** @see #getTables(String, String, String, String[]) returns this List of Columns */
	protected static final String[] TABLE_FIELD_DEFAULTS 
	= { null, //String => table catalog (may be null)
		null, //String => table schema (may be null) 
		STR_TABLE_NAME, //String => table name 
		TABLE_TYPE_TABLE, //String => table type, @see TABLE_TYPES
		STR_REMARKS, //String => explanatory comment on the table 
		STR_TYPE_CATALOG, //String => the types catalog (may be null) 
		STR_TYPE_SCHEMA, //String => the types schema (may be null) 
		STR_TYPE_NAME, //String => type name (may be null) 
		null, //String => name of the designated "identifier" column of a typed table (may be null)
		REF_GENERATION_TYPE_USER //"REF_GENERATION"; //String => specifies how values in SELF_REFERENCING_COL_NAME are created. @see REF_GENERATION_TYPES 
	};

}
