/*
 * File Name: AStatement.java
 * Created on: 17.08.2003
 *
 */
package streamIO.integer.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import math.vector.VectorString;
import streamIO.integer.jdbc.dbTest.DbTestEquals;
import streamIO.integer.jdbc.dbTest.DbTestFullOuter;
import streamIO.integer.jdbc.dbTest.DbTestLess;
import streamIO.integer.jdbc.dbTest.DbTestNegate;
import streamIO.integer.jdbc.dbTest.DbTestOuter;
import streamIO.integer.jdbc.dbTest.DbTestSwapOperands;
import streamIO.integer.jdbc.dbTest.FilterRsRows;
import streamIO.integer.jdbc.dbTest.IDbTest;

/**
 * Base class for a {@link Statement} over this package's filesystem-backed tables, providing
 * the get/set pairs of the {@code Statement} contract plus a hand-rolled parser and evaluator
 * for a subset of SQL (mainly {@code SELECT}, with row-at-a-time {@code INSERT}/{@code UPDATE}/
 * {@code DELETE}).
 *
 * <p>Design note: parsing can create and aggregate {@link ResultSet}s recursively in RAM (the
 * simple approach used here), or build a structure of RS-filters that filters and updates
 * dynamically without materializing intermediate cross-joins (more complex, more performant,
 * not implemented). Condition parsing and evaluation is the hardest part; a canonical query -
 * every field prefixed with its table name, every field and table indexed - makes both easier.
 *
 * <p>{@code javax.sql} defines an SPI for implementors, with a reference implementation using
 * disconnected record sets read from and written to XML documents, based on the {@code RowSet}
 * interface (itself an extension of {@code ResultSet} with schema-construction methods) - not
 * used here.
 *
 * <h2>Collaborators</h2>
 *
 * | Type | Relationship |
 * |---|---|
 * | {@link AConnection} | Supplies the directory this statement's tables are resolved against. |
 * | {@link IDbTest} | Parsed condition tree used to evaluate {@code WHERE}/{@code ON} clauses. |
 * | {@link DbColumn} | Column descriptor resolved by name/alias during parsing. |
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see AConnection
 * @see IDbTest the parsed condition contract
 * @see DbColumn
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:53:32Z
 * digest: 14542c063ba1db8ed7f8006df857612d89aef18ee5719a7da8743b910b6dd473
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public abstract class AStatement
implements Statement {

	//////////////////////////////////////////////////////////////////////////////////
	/// Abstract Methods
	//////////////////////////////////////////////////////////////////////////////////
	
	/** abstract Factory Method to create the correct Type of ResultSet 	 */ 
	protected abstract ResultSet getResultSet(final File table, final String tableName) 
	throws SQLException, IOException;// { return null; }
	
	/**
	 * Always returns {@code false}; this statement tracks no closed state of its own.
	 * @see java.sql.Statement#isClosed()
	 */
	public boolean isClosed() throws SQLException { return false; }

	/**
	 * Always returns {@code false}; statement pooling is not supported.
	 * @see java.sql.Statement#isPoolable()
	 */
	public boolean isPoolable() throws SQLException { return false;	}

	/**
	 * Always throws, since statement pooling is not supported.
	 * @throws SQLException always
	 * @see java.sql.Statement#setPoolable(boolean)
	 */
	public void setPoolable(boolean arg0) throws SQLException {
		throw new SQLException("Poolable not supported"); }

	/**
	 * Always returns {@code false}; this statement wraps no other implementation.
	 * @see java.sql.Wrapper#isWrapperFor(Class)
	 */
	public boolean isWrapperFor(Class arg0) throws SQLException { return false; }

	/**
	 * Always returns {@code null}; this statement wraps no other implementation.
	 * @see java.sql.Wrapper#unwrap(Class)
	 */
	public Object unwrap(Class arg0) throws SQLException { return null;	}

	/** Default result-set type used when a statement is created without one specified. */
	final static public int resultSetTypeDefault        = ResultSet.TYPE_SCROLL_SENSITIVE; // FORWARD_ONLY;
	/** Default result-set concurrency used when a statement is created without one specified. */
	final static public int resultSetConcurrencyDefault = ResultSet.CONCUR_UPDATABLE; // READ_ONLY;
	/** Default result-set holdability used when a statement is created without one specified. */
	final static public int resultSetHoldabilityDefault = ResultSet.HOLD_CURSORS_OVER_COMMIT; // CLOSE_CURSORS_AT_COMMIT;
	
	
	//////////////////////////////////////////////////////////////////////////////////
	/// Keyword Constants
	//////////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////
	/// The four CRUD Commands: Change/Create, Read, Update, Delete
	//////////////////////////////////////////////////////////////////////////////////
	
	/** Select Command 
	 * SELECT [DISTINCT] [table.]field[ AS alias][,[table.]field[ AS alias]]* 
	 * FROM table[ AS alias][,table[ AS alias]]*
	 * [WHERE condition ] 
	 * [ORDER BY (ASC|DESC)]
	 * [GROUP BY fieldOrAlias]
	 * 
	 * returns the ResultSet to navigate
	 */
	final static public String STR_SELECT = "SELECT ";
	
	/** Update Command 
	 * UPDATE table
	 * SET field=value[, field=value]
	 * [WHERE condition ] 
	 * 
	 * returns the Number of Rows updated  
	 */
	final static public String STR_UPDATE = "UPDATE ";
	
	/** Delete Command 
	 * DELETE FROM table
	 * [WHERE condition ]
	 * 
	 * returns the Number of Rows deleted  
	 */
	final static public String STR_DELETE = "DELETE FROM ";
	
	/** Insert Command 
	 * INSERT INTO table (field[, field]*)
	 * VALUES (value[, value]*)
	 * 
	 * returns 1
	 */
	final static public String STR_INSERT = "INSERT INTO ";
	
	/** List of all Commands */
	final static public String[] COMMANDS = {STR_SELECT, STR_INSERT, STR_UPDATE, STR_DELETE};
	
	protected static final IDbTest DB_TEST_EQUALS = new DbTestEquals(null, null);  
	
	protected static final IDbTest DB_TEST_LESS = new DbTestLess(null, null);  
	
	protected static final IDbTest DB_TEST_GREATER = new DbTestSwapOperands(DB_TEST_LESS, ">");  
	
	protected static final IDbTest DB_TEST_LESS_EQ = new DbTestNegate(DB_TEST_GREATER, "<=");  
	
	protected static final IDbTest DB_TEST_GREATER_EQ = new DbTestNegate(DB_TEST_LESS, ">=");  
	
	protected static final IDbTest DB_TEST_OUTER = new DbTestOuter(null, null);  
	
	/** List of all Operators, shorter Operators first */
	protected static final IDbTest[] OPERATORS = { 
		DB_TEST_EQUALS, //= 
		DB_TEST_LESS, //<
		DB_TEST_GREATER, //>
		DB_TEST_LESS_EQ, //<= 
		DB_TEST_GREATER_EQ, //>=
		new DbTestNegate(DB_TEST_EQUALS, "!="),  
		DB_TEST_OUTER, //"=*", 
		new DbTestSwapOperands(DB_TEST_OUTER, "*="), 
		new DbTestFullOuter(null, null) //"*=*", 
	}; 
	
	/** 'From' Clause for Select Statement 	 */
	final static public String STR_FROM = " FROM ";

	/** 'Where' Clause for Select, Delete and Update Statement 	 */
	final static public String STR_WHERE = " WHERE ";

	/** 'Order By' Clause only for Select Statements 	 */
	final static public String STR_ORDER_BY = " ORDER BY "; 
		
	/** Insert Statement for the full Table */
	final static public String STR_VALUES = " VALUES ";

	/** Update Statement for the full Table */
	final static public String STR_SET = " SET ";

	/** Separator for 'AND' Clauses in Conditions. 
	 * 'OR' Clauses have to be simulated by iteratively executing the alternative Statements
	 * and creating a Union of the Results (for Select Statements). 	*/
	final static public String STR_AND = " AND "; 

	/** Separator between Name and Alias; 
	 * The Separator is optional, a WhiteSpace is sufficient too! 
	 */
	final static public String STR_AS = " AS "; 
	
	/** 'On' Clause introducing a join condition. */
	final static public String STR_ON = " ON ";

	/** Select Statement for the full Table */
	final static public String STR_SELECT_ALL = STR_SELECT + "* FROM ";

	/** 'Left' Join-type keyword fragment. */
	final static public String STR_LEFT = " LEFT";
	/** 'Right' Join-type keyword fragment. */
	final static public String STR_RIGHT= " RIGHT";
	/** 'Full' Join-type keyword fragment. */
	final static public String STR_FULL = " FULL";
	/** 'Inner' Join-type keyword fragment. */
	final static public String STR_INNER = " INNER";
	/** 'Outer' Join-type keyword fragment. */
	final static public String STR_OUTER = " OUTER";
	/** 'Join' keyword fragment, combined with one of the join-type fragments above. */
	final static public String STR_JOIN = " JOIN ";
	
	/** the maximum Number of direct Joins to expect	 */
	public static char MAX_NUM_JOINS = 10;
	
	//////////////////////////////////////////////////////////////////////////////////
	/// static Helper Methods 
	//////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * returns true when the String at the given Position ends with the given test String.  
	 * @param string the String to trim 
	 * @param test the String to trim by 
	 * @return true when the String at the given Position ends with the given test String.  
	 */
	final static public String REMOVE_SUFFIX(final String string, final String suffix) {
		if (!string.endsWith(suffix))
			return string; 
		return string.substring(0, string.length()-suffix.length()); 
	}
	
	/** evaluates all Conditions in the List 
	 * with early Return. 
	 * @param conditions List of Conditions to test. 
	 * @return the 'AND' Combination of all Conditions 
	 * @throws SQLException
	 */
	final static public boolean TEST_AND(final IDbTest[] conditions) throws SQLException {
		for(int i = conditions.length; --i >= 0; ) 
			if (! conditions[i].test())
				return false; 
		return true; 
	}

	/** evaluates all Conditions in the List 
	 * with early Return. 
	 * @param conditions List of Conditions to test. 
	 * @return the 'OR' Combination of all Conditions 
	 * @throws SQLException
	 */
	final static public boolean TEST_OR(final IDbTest[] conditions) throws SQLException {
		for(int i = conditions.length; --i >= 0; ) 
			if (conditions[i].test())
				return true; 
		return false; 
	}

	//////////////////////////////////////////////////////////////////////////////////
	/// static Helper Methods for Parsing
	//////////////////////////////////////////////////////////////////////////////////

	/**
	 * extracts the Substring up to the next Separator Occurrence 
	 * removes the Characters from the StringBuffer. 
	 * @param str the String to extract from 
	 * @param sep the Separator String to search for 
	 * @return the Substring up to the next Separator Occurrence
	 */
	final static public void TRUNCATE(final StringBuffer str, final String sep) {
		EXTRACT(str, sep, true); 
	}

	/**
	 * extracts the Substring up to the next Separator Occurrence 
	 * removes the Characters from the StringBuffer. 
	 * @param str the String to extract from 
	 * @param sep the Separator String to search for 
	 * @return the Substring up to the next Separator Occurrence
	 */
	final static public String[] EXTRACT_SPLIT(final StringBuffer str, final String sep, final String splitter) {
		return EXTRACT(str, sep, false).split(splitter); 
	}

	/**
	 * extracts the Substring up to the next Separator Occurrence 
	 * removes the Characters from the StringBuffer. 
	 * @param str the String to extract from 
	 * @param sep the Separator String to search for 
	 * @return the Substring up to the next Separator Occurrence
	 */
	final static public String EXTRACT(final StringBuffer str, final String sep) {
		return EXTRACT(str, sep, false); 
	}

	/**
	 * extracts the Substring up to the next Separator Occurrence 
	 * removes the Characters from the StringBuffer. 
	 * @param str the String to extract from 
	 * @param sep the Separator String to search for 
	 * @return the Substring up to the next Separator Occurrence
	 */
	final static public String EXTRACT(final StringBuffer str, final String sep, final boolean truncate) {
		int pos = str.indexOf(sep); if (pos < 0) pos = str.length(); 
		final String ret = truncate ? null : str.substring(0, pos); 
		str.delete(0, pos+sep.length()); 
		return ret; 
	}
	
	/**
	 * unnecessary, the ResultSet has a Method called findColumn(String)
	 * @param rsMeta
	 * @param fieldName
	 * @return
	 * @throws SQLException
	 */
	/*final static public int FIND_COLUMN(final ResultSetMetaData rsMeta, final String fieldName) throws SQLException {
		for (int k = rsMeta.getColumnCount(); --k >= 0; ) 
			if (rsMeta.getColumnName(k).equals(fieldName))
				return k; 
		return -1; 
	}*/
	
	/**
	 * splits and trims the given String by the Splitter String 
	 * @param str
	 * @param splitter
	 * @param parts
	 * @return the Number of Parts found 
	 */
	final static public int SPLIT (final String str, final String splitter, final String[] parts) {
		final int pos = str.indexOf(splitter); 
		if (pos < 0) {
			parts[0] = str.trim(); 
			return 1; }
		parts[0] = str.substring(0, pos).trim(); 
		parts[1] = str.substring(pos+splitter.length()).trim(); 
		return 2; 
	}
	
	/**
	 * splits and trims the given String by the Splitter String 
	 * @param str
	 * @param splitter
	 * @param parts
	 * @return the Number of Parts found 
	 */
	final static public int SPLIT (final StringBuffer str, final String splitter, final String[] parts) {
		final int pos = str.indexOf(splitter); 
		if (pos < 0)
			return 1; 
		parts[0] = str.substring(0, pos).trim(); 
		parts[1] = str.substring(pos+splitter.length()).trim(); 
		return 2; 
	}
	
	/**
	 * Splits a {@code FROM}-clause table reference into its table name and alias.
	 * @param table the table reference, optionally followed by an {@code AS} alias or a bare alias
	 * @return a 2-element array of {table name, alias}; both elements equal the trimmed
	 * input when no alias separator is found
	 */
	final static public String[] SPLIT_ALIAS(String table) {
		table = table.trim(); 
		final String[] alias = new String[2]; 
		if ((SPLIT(table , STR_AS, alias) < 2) &&
			//(SPLIT(table , STR_ON, alias) < 2) &&
			(SPLIT(table , " "   , alias) < 2)) {
			alias[0] = alias[1] = table; 
		}
		return alias; 
	}
	
	/**
	 * Rejects a Table Name that could escape the Connection's Directory.
	 * Only a single, relative Path Element without any Path Separator,
	 * Drive Letter or Parent-Directory Reference is accepted,
	 * so a Name parsed out of a FROM-Clause cannot traverse the File System.
	 * @param tableName the Table Name taken from the SQL Statement
	 * @throws SQLException when the Name is empty, absolute, or contains a
	 * Path Separator or a ".." Element
	 */
	final static public void CHECK_TABLE_NAME(final String tableName) throws SQLException {
		if ((tableName == null) || (tableName.trim().length() == 0))
			throw new SQLException("Illegal Table Name: must not be empty!");
		if ((tableName.indexOf('/') >= 0) || (tableName.indexOf('\\') >= 0)
		 || (tableName.indexOf(':') >= 0) || (tableName.indexOf('\0') >= 0))
			throw new SQLException("Illegal Table Name '"+tableName+"': must not contain a Path Separator!");
		if (tableName.equals(".") || tableName.equals("..")
		 || new File(tableName).isAbsolute())
			throw new SQLException("Illegal Table Name '"+tableName+"': must be a simple relative Name!");
	}

	/**
	 * Resolves {@code tableName} (with or without the connection's file suffix) to a table
	 * file under the connection's directory and delegates to {@link #getResultSet(File, String)}.
	 * The Name is validated by {@link #CHECK_TABLE_NAME(String)} first, so it cannot
	 * escape the Connection's Directory.
	 * @throws SQLException when {@code tableName} is not a simple relative Name
	 * @throws IOException when neither {@code tableName} nor {@code tableName + suffix} exists
	 */
	protected ResultSet getResultSet(final String tableName) throws SQLException, IOException {
		CHECK_TABLE_NAME(tableName);
		File table = new File(((AConnection)conn).urlDir, tableName);
		if (!table.exists()) 
			 table = new File(((AConnection)conn).urlDir, tableName+((AConnection)conn).suffix);
		if (!table.exists()) 
			throw new IOException("The Table '"+table.getAbsoluteFile()+"' does not exist!"); //FileNotFoundException
		return getResultSet(table, tableName);
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	/// Member Variables 
	//////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Connection, needed for the URL */
	protected final AConnection conn;
	
	/**
	 * Returns the {@link Connection} that created this statement.
	 * @see java.sql.Statement#getConnection()
	 */
	public Connection getConnection() { //throws SQLException {
		return conn;
	}

	/**
	 * Delegates to {@link AConnection#getWarnings()}.
	 * @see java.sql.Statement#getWarnings()
	 */
	public SQLWarning getWarnings() { //throws SQLException {
		return conn.getWarnings();
	}

	/**
	 * Delegates to {@link AConnection#clearWarnings()}.
	 * @see java.sql.Statement#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		conn.clearWarnings();
	}
	
	/** the SQL Statement currently executed	 */
	protected String currStatement; 
	
	/** return a readable Representation of this Object
	 * @return a readable Representation of this Object
	 */
	public String toString() { return conn+":"+currStatement; }
	
	//////////////////////////////////////////////////////////////////////////////////
	/// Constructors
	//////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializing constructor using the default result-set type, concurrency and holdability.
	 * @param conn
	 */
	public AStatement(final AConnection conn) {
		this(conn, resultSetTypeDefault, resultSetConcurrencyDefault, resultSetHoldabilityDefault);
	}
	
	/** Constructor	 */
	public AStatement(final AConnection conn, final int resultSetType, final int resultSetConcurrency) {
		this(conn, resultSetType, resultSetConcurrency, resultSetHoldabilityDefault);
	}
	
	/** Constructor	 */
	public AStatement(final AConnection conn, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) {
		this.resultSetHoldability = resultSetHoldability;
		this.resultSetConcurrency = resultSetConcurrency;
		this.resultSetType = resultSetType;
		this.conn = conn;
		if (conn != null)
			conn.addStatement(this); //so AConnection.close() closes this Statement too
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	/// Methods
	//////////////////////////////////////////////////////////////////////////////////
	
	/**parses simple Queries 
	 * @see java.sql.Statement#executeQuery(java.lang.String)	 */
	public ResultSet executeQuery(final String _sql) throws SQLException {
		currStatement = _sql; 
		final StringBuffer sql = VectorString.NORMALIZE(_sql, 1);
		final int commandNo = VectorString.STARTS_WITH(sql, COMMANDS); //exploit the Fact that the first Characters are distinct
		sql.delete(0, COMMANDS[commandNo].length());
		if (commandNo == 0) 
			return executeSelect(sql);
		final String tableName = EXTRACT(sql, " "); 
		try {
			final ResultSet rs = getResultSet(tableName); 
			switch (commandNo) {
			case 1:	return new ResultSetCount(executeInsert(sql, rs), tableName, this); 
			case 2:	return new ResultSetCount(executeUpdate(sql, rs, tableName), tableName, this); 
			case 3:	return new ResultSetCount(executeDelete(sql, rs, tableName), tableName, this); 
			default:
				throw new SQLException("Invalid Sql Command: "+sql); 
			}
		} catch (final IOException x) {
			throw new SQLException(x.toString());
		}
	}

	/**
	 * Executes {@code _sql} as a query and returns the number of rows it produced or
	 * updated, reading the count directly off a {@link ResultSetCount} when the query
	 * returns one.
	 * @see java.sql.Statement#executeUpdate(java.lang.String)
	 */
	public int executeUpdate(final String _sql) throws SQLException {
		currStatement = _sql; 
		final ResultSet rs = executeQuery(_sql); 
		if (rs instanceof ResultSetCount)
			return (int) ((ResultSetCount) rs).count;
		rs.last(); 
		return rs.getRow(); //return count(), i.e. the Number of the last Row
	}

	/**
	 * INSERT INTO table (field[, field]*)
	 * VALUES (value[, value]*)
	 * 
	 * returns 1
	 * 
	 * @param _sql
	 * @return 1 if the Insert succeeded
	 * @throws SQLException when the insert failed (e.g. due to Integrity Constraints) 
	 */
	public int executeInsert(final StringBuffer _sql, final ResultSet rs) throws SQLException {
		currStatement = _sql.toString(); 
		final String[] parts = new String[2];
		if (2 > SPLIT(_sql, STR_VALUES, parts))
			throw new SQLException("Wrong Syntax on INSERT:"+_sql);
		final String[] fields = parts[0].split(","); 
		final String[] values = parts[1].split(","); 
		if (fields.length != values.length)
			throw new SQLException("Mismatch in Lists on INSERT:"+_sql
					+"\nFields:"+fields.length
					+"\nValues:"+values.length);
		rs.moveToInsertRow(); //update the Fields
		for (int i = fields.length; --i >= 0; ) 
			rs.updateString(fields[i], values[i]); 
		rs.insertRow(); //check Constraints 
		return 1; 
	}

	/**
	 * DELETE FROM table
	 * [WHERE condition ]
	 * 
	 * returns the Number of Rows deleted 
	 * 
	 * @param _sql
	 * @return the Number of Rows deleted
	 * @throws SQLException
	 */
	public int executeDelete(final StringBuffer sql, final ResultSet rs, final String tableName) throws SQLException {
		currStatement = sql.toString(); 
		final IDbTest[] conditions = analyzeConditions(sql, rs, tableName); 
		int counter = 0; 
		for(; rs.next(); ) {
			if (TEST_AND(conditions)) {
				rs.deleteRow(); ++counter; 
			}
		}
		return counter; 
	}

	/**
	 * UPDATE table
	 * SET field=value[, field=value]
	 * [WHERE condition ] 
	 * 
	 * returns the Number of Rows updated  
	 * @param _sql
	 * @return the Number of Rows updated  
	 * @throws SQLException
	 */
	public int executeUpdate(final StringBuffer sql, final ResultSet rs, final String tableName) throws SQLException {
		currStatement = sql.toString(); 
		final IDbTest[] assignmnts = analyzeConditions(sql, rs, tableName, STR_WHERE, ","); 
		final IDbTest[] conditions = analyzeConditions(sql, rs, tableName); 
		int counter = 0; 
		for(; rs.next(); ) {
			if (TEST_AND(conditions)) {
				for(int i = assignmnts.length; --i >= 0; ) {
					IDbTest assignmnt = assignmnts[i]; 
					assignmnt.getOperand0().setString(assignmnt.getOperand1().getString()); 
				}
				++counter; 
			}
		}
		return counter; 
	}

	/**parses simple Queries without Fields and without Conditions.  
	 * SELECT [DISTINCT] [table.]field[ AS alias][,[table.]field[ AS alias]]* 
	 * FROM table[ AS alias][,table[ AS alias]]*
	 * [WHERE condition ] 
	 * [ORDER BY (ASC|DESC)]
	 * [GROUP BY fieldOrAlias]
	 * 
	 * returns the ResultSet to navigate
	 */
	public ResultSet executeSelect(final StringBuffer sql) throws SQLException {
		currStatement = sql.toString(); 
		final String[] fieldList = EXTRACT_SPLIT(sql, STR_FROM , ",");
		final String[] tableList = EXTRACT_SPLIT(sql, STR_WHERE, ",");
		try {
			final Map tables = new HashMap(tableList.length);
			final ArrayList joinTables = new ArrayList(); 
			//final ResultSetMetaData[] tableMeta = new ResultSetMetaData[tableList.length]; //unnecessary 
			final Map fieldsByAlias = new HashMap(fieldList.length);
			for (int i = tableList.length; --i >= 0; ) 
				joinTables.add(parseJoinedTable(tables, tableList[i], fieldsByAlias));
			final DbColumn[] tableFieldAlias = new DbColumn[fieldList.length]; 
			for (int i = fieldList.length; --i >= 0; ) 
				tableFieldAlias[i] = analyzeField(fieldList[i], tables, fieldsByAlias);
			final IDbTest[] conditions = analyzeConditions(sql, tables, fieldsByAlias);
			final String [] orderByList = sql.length() == 0 ? new String[0] : sql.toString().split(",");
			final DbColumn[] tableOrderBy = new DbColumn[orderByList.length]; 
			for (int i = orderByList.length; --i >= 0; ) 
				tableOrderBy[i] = analyzeField(orderByList[i], tables, fieldsByAlias);
			//typically there is one Condition for any consecutive Pair of Tables
			//create LeftJoin 
			//constructing the Cursor...
			ResultSet rs = (ResultSet) joinTables.get(0); 
			for(int i = 0; ++i < joinTables.size(); ) //cascaded Cross Join
				rs = new ResultSetCrossJoin(rs, (ResultSet) joinTables.get(i), false, this);
			for(int i = conditions.length; --i >= 0;) //use all available Columns for Filtering at the End 
				rs = new FilterRsRows(rs, conditions[i]); //(which is most ineffective!) 
			if ((tableFieldAlias.length > 1) || !"*".equals(tableFieldAlias[0].name))
				rs = new FilterRsCols(rs, tableFieldAlias);
			return rs; 
		} catch (final IOException x) {
			throw new SQLException(x.toString()); 
		}
	}
	
	/**
	 * @param _TablesByAlias
	 * @param _tableArray dynamic List to collect the Table ResultSets, since the Number cannot be determined ahead.  
	 * @param tableDescr 
	 * @throws IOException
	 * @throws SQLException
	 */
	private ResultSet parseJoinedTable(final Map _TablesByAlias, final String tableDescr, final Map _FieldsByAlias) 
	throws IOException, SQLException {
		//<LeftTable> {[{Inner| Left | Full | Right} [Outer]] | [INNER]} Join <RightTable> ON <Bedingung>
		final String[] joins = new String[MAX_NUM_JOINS]; //max. expected #of Joins 
		final String[] tableCond = new String[2]; 
		final boolean[] left  = new boolean[2]; //Code is more readable with 3 Variables
		final boolean[] right = new boolean[2]; //than with a Matrix
		final int len = SPLIT(tableDescr, STR_JOIN, joins); 
		ResultSet ret = null; 
		for(int j = -1; ++j < len;) {
			String currJoin; 
			currJoin = analyzeJoin(left, right, joins[j]); 
			if (ret == null) { //<LeftTable> ... {[{Inner| Left | Full | Right} [Outer]] | [INNER]}
				ret = parseTable(_TablesByAlias, currJoin); 
			} else { //<RightTable> ON <Bedingung> {[{Inner| Left | Full | Right} [Outer]] | [INNER]}
				SPLIT(currJoin, STR_ON, tableCond);  
				final ResultSet rs2 = parseTable(_TablesByAlias, tableCond[0]); 
				final IDbTest[] conditions = analyzeConditions(new StringBuffer(tableCond[1]), _TablesByAlias, _FieldsByAlias);
				if (left[1] && right[1])
					throw new SQLException("Full Joins are not supported!"); 
				ret = new ResultSetLeftJoin(ret, rs2, conditions, left[1] || right[1], right[1], this); 
			} //<LeftTable> {[{Inner| Left | Full | Right} [Outer]] | [INNER]} 
		}
		return ret; 
	}
	
	/**
	 * Queues the previous Result, parses the Join Expression and returns the Table and Alias Name
	 * @param outer
	 * @param left
	 * @param right
	 * @param currJoin
	 * @throws SQLException
	 */
	private String analyzeJoin(final boolean[] left, final boolean[] right, 
			String currJoin) throws SQLException {
			String newJoin;
			right[1] = right[0]; 
			left [1] = left [0]; 
			left [0] = right[0] = false; //default to an inner Join
		if (currJoin != (newJoin = REMOVE_SUFFIX(currJoin, STR_INNER))) { //'INNER' is optional though! 
			currJoin  =  newJoin; 
		} else { 
			if (currJoin != (newJoin = REMOVE_SUFFIX(currJoin, STR_OUTER))) {
				currJoin  =  newJoin; left[0] = right[0] = true; 
			}
			if (currJoin != (newJoin = REMOVE_SUFFIX(currJoin, STR_LEFT))) {
				currJoin  =  newJoin; right[0] = false; left[0] = true;
			} else
			if (currJoin != (newJoin = REMOVE_SUFFIX(currJoin, STR_RIGHT))) {
				currJoin  =  newJoin; left[0] = false; right[0] = true;
			} else 
			if (currJoin != (newJoin = REMOVE_SUFFIX(currJoin, STR_FULL))) {
				currJoin  =  newJoin; left[0] = right[0] = true;  
			} else //no further Specification...
				if (left[0] && right[0]) //OUTER, but neither left nor right
					throw new SQLException("Outer Joins must be qualified with LEFT or RIGHT!"); 
		}
		return currJoin; 
	}
	
	/**
	 * parses the Description for a Table with optional Alias, 
	 * loads the ResultSet and puts it into the Map keyed by the Alias. 
	 * @param tables Map keyed by the Alias
	 * @param tableDescr Description for a Table with optional Alias
	 * @return the created ResultSet 
	 * @throws SQLException
	 * @throws IOException
	 */
	protected ResultSet parseTable(final Map tables, final String tableDescr) 
	throws SQLException, IOException {
		final String[] tableAlias = SPLIT_ALIAS(tableDescr);
		final ResultSet rs = getResultSet(tableAlias[0]); 
		if (rs instanceof AResultSetBase) {
			AResultSetBase ars = (AResultSetBase) rs; 
			ars.cursorName = tableAlias[1]; 
		}
		tables.put(tableAlias[1], rs); 
		return rs; 
	}
	
	/** parses the Conditions (separated by "AND") and assigns them to their corresponding Tables 
	 * @param _sql
	 * @param _TablesByAlias Map of Table ResultSets to search by Table Name or Alias
	 * @param tableNames List of all Tables Names to search for Field Name Matches 
	 * @param tableArray List of all Table ResultSets to assign them to Fields 
	 * @param tableMeta  List of all Table ResultSetMetadata to quickly retrieve Fields 
	 * @param _FieldsByAlias Map of all Fields by their Alias, updated on Identification. 
	 * 
	 * @return a List of FieldAlias Pairs with Name, Alias and relevant ResultSet 
	 * @throws SQLException when Fields cannot be uniquely identified. 
	 */
	private static final IDbTest[] analyzeConditions(final StringBuffer _sql, 
			final Map _TablesByAlias, final Map _FieldsByAlias) throws SQLException {
		return analyzeConditions(_sql, _TablesByAlias, _FieldsByAlias, STR_ORDER_BY, STR_AND); 
	}

	/** 
	 * @param _where the WHERE Clause of the Sql Statemen
	 * @param _TablesByAlias Map of Table ResultSets to search by Table Name or Alias
	 * @param _FieldsByAlias  Map of all Fields by their Alias, updated on Identification. 
	 * @param nextToken indicates the End of the Conditions  
	 * @param splitter the String to split the Conditions by, typically 'AND' 
	 * @return a List of FieldAlias Pairs with Name, Alias and relevant ResultSet 
	 * @throws SQLException when Fields cannot be uniquely identified. 
	 */
	private static final IDbTest[] analyzeConditions(final StringBuffer _where, 
			final Map _TablesByAlias,  
			final Map _FieldsByAlias, final String nextToken, final String splitter) throws SQLException {
		if (_where.length() == 0) 
			return new IDbTest[0]; 
		final String[] Conditions = EXTRACT_SPLIT(_where, nextToken, splitter);
		final IDbTest[] ret = new IDbTest[Conditions.length]; 
		for (int i = Conditions.length; --i >= 0; ) {
			final String condition = Conditions[i];
			final String[] leftRight = new String[2]; 
			final IDbTest operator = FIND_OPERATOR(condition, leftRight);
			if (operator == null)
				throw new SQLException("Operator cannot be identified in '"+condition+"'");
			final DbColumn left  = analyzeField(leftRight[0], _TablesByAlias, _FieldsByAlias); 
			final DbColumn right = analyzeField(leftRight[1], _TablesByAlias, _FieldsByAlias);
			IDbTest test = ret[i] = operator.newInstance(left, right); 
			if ( left.table == null) { //Comparison with a Constant...
				_TablesByAlias.put(right.getTableName(), new FilterRsRows(right.table, test)); 
			}
			if (right.table == null) { //...replace the ResultSet directly with a filtered to increase Performance!  
				_TablesByAlias.put(left.getTableName(), new FilterRsRows(left.table, test)); 
			}
		}
		return ret; 
	}
	
	/**
	 * @param condition
	 * @param leftRight
	 * @param operator
	 * @return
	 */
	private static IDbTest FIND_OPERATOR(final String condition, final String[] leftRight) {
		for (int j = OPERATORS.length; --j >= 0; ) {
			final IDbTest operator = OPERATORS[j]; 
			if (2 <= SPLIT(condition, operator.getOperator(), leftRight)) 
				return operator;
		}
		return null;
	}

	/**
	 * @param fieldName Name of the Field to identify
	 * @param _TablesByAlias Map of Table ResultSets to search by Table Name or Alias
	 * @param tableNames List of all Tables Names to search for Field Name Matches 
	 * @param tableArray List of all Table ResultSets to assign them to Fields 
	 * @param tableMeta  List of all Table ResultSetMetadata to quickly retrieve Fields 
	 * @param _FieldsByAlias   Map of all Fields by their Alias, updated on Identification. 
	 * @return a FieldAlias Object containing Name, Alias and a Reference to the relevant ResultSet. 
	 * @throws SQLException
	 */
	private static final DbColumn analyzeField(final String fieldName, 
			final Map _TablesByAlias, //final ResultSetMetaData[] tableMeta, 
			final Map _FieldsByAlias) throws SQLException {
		if ((fieldName.charAt(0) == '\'') && //Constant (String) on one side!  
			(fieldName.charAt(fieldName.length()-1) == '\'')) 
			return new DbColumn(null, fieldName.substring(1, fieldName.length()-1), -1);
		final String[] fieldAlias = SPLIT_ALIAS(fieldName); 
		ResultSet table = null; 
		final int pos = fieldAlias[0].indexOf('.'); 
		String tableName = null; 
		if (pos > 0) { //Table Name given
			tableName = fieldAlias[0].substring(0, pos); 
			table = (ResultSet) _TablesByAlias.get(tableName);
			fieldAlias[0] = fieldAlias[0].substring(pos+1); 
		}
		if ("*".equals(fieldAlias[0])) 
			return new DbColumn(table, tableName, "*", "*", -1);
		final int column; 
		if (pos > 0)  //Table Name or Alias given
			column = table.findColumn(fieldAlias[0]);
		else { //if (pos <= 0) { //no Table Name, search Field in the Field Names of all Tables
			int col = -1; 
			for(final Iterator iter = _TablesByAlias.entrySet().iterator(); iter.hasNext();) {
				final Map.Entry entry = (Map.Entry) iter.next(); 
				final ResultSet rs = (ResultSet) entry.getValue(); 
				col = rs.findColumn(fieldAlias[0]); 
				if (col >= 0) {
					if (table != null) 
						throw new SQLException("Ambiguous Field Name: "+fieldAlias[0]); 
					table = rs; tableName = (String) entry.getKey(); 
				}
			}
			if (table == null) { //try to find the Field by Alias
				final DbColumn ret = (DbColumn) _FieldsByAlias.get(fieldAlias[0]);
				if (ret != null)
					return ret;
				try { //try to interpret the Field Name as a Column Number
					final double value = Double.parseDouble(fieldAlias[0]);
					if (value == (int) value)
						col    = (int) value;  //Table.ColumnNumber given
				} catch (final NumberFormatException ignore) {
				}
			}
			column = col; 
		}
		if ((table != null) && (column < 0)) 
			throw new SQLException("Field '"+fieldAlias[0]+"' cannot be found!"); 
		final DbColumn ret = new DbColumn(table, tableName, fieldAlias[0], fieldAlias[1], column);
		_FieldsByAlias.put(fieldAlias[1], ret); 
		return ret;
	}
	
	/**
	 * parses the Conditions for single Table Commands (Delete and Update)
	 * @param sql
	 * @param rs
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	protected static final IDbTest[] analyzeConditions(final StringBuffer sql, final ResultSet rs, final String tableName) throws SQLException {
		return analyzeConditions(sql, rs, tableName, STR_ORDER_BY, STR_AND); 
	}

	/**
	 * parses the Conditions for single Table Commands (Delete and Update)
	 * @param sql
	 * @param rs
	 * @param tableName
	 * @return 
	 * @throws SQLException
	 */
	protected static final IDbTest[] analyzeConditions(final StringBuffer sql, 
			final ResultSet rs, final String tableName, 
			final String nextToken, final String splitter) throws SQLException {
		final Map tablesByAlias = new HashMap(); tablesByAlias.put(tableName, rs); 
		final ResultSetMetaData[] tableMeta = new ResultSetMetaData[]{ rs.getMetaData() }; 
		final Map fieldsByAlias = new HashMap();
		return analyzeConditions(sql, tablesByAlias, fieldsByAlias, nextToken, splitter); 
	}
	
	//////////////////////////////////////////////////////////////////////////////////

	/** maximum Field Size, not used here */
	protected int maxFieldSize;

	/**
	 * Returns the value last set via {@link #setMaxFieldSize(int)}; not enforced.
	 * @see java.sql.Statement#getMaxFieldSize()
	 */
	public int getMaxFieldSize() { // throws SQLException {
		return maxFieldSize; //conn.dbMetaData.getMaxColumnNameLength();
	}

	/**
	 * Stores the maximum field size hint; not enforced by this driver.
	 * @see java.sql.Statement#setMaxFieldSize(int)
	 */
	public void setMaxFieldSize(int max) { // throws SQLException {
		maxFieldSize = max;
	}

	//////////////////////////////////////////////////////////////////////////////////

	/** maximum Field Size, not used here */
	protected int maxRows;

	/**
	 * Returns the value last set via {@link #setMaxRows(int)}; not enforced.
	 * @see java.sql.Statement#getMaxRows()
	 */
	public int getMaxRows() { //throws SQLException {
		return maxRows;
	}

	/**
	 * Stores the maximum row count hint; not enforced by this driver.
	 * @see java.sql.Statement#setMaxRows(int)
	 */
	public void setMaxRows(final int max) { // throws SQLException {
		maxRows = max;
	}

	//////////////////////////////////////////////////////////////////////////////////

	/** Flag whether to use Escaping, not used here	 */
	protected boolean escaping = false;

	/**
	 * Stores the escape-processing flag; not used by this driver's SQL parsing.
	 * @see java.sql.Statement#setEscapeProcessing(boolean)
	 */
	public void setEscapeProcessing(final boolean enable) { // throws SQLException {
		escaping = enable;
	}

	//////////////////////////////////////////////////////////////////////////////////

	/** Query Timeout in Seconds */
	protected int queryTimeoutInSecs;

	/**
	 * Returns the value last set via {@link #setQueryTimeout(int)}; not enforced.
	 * @see java.sql.Statement#getQueryTimeout()
	 */
	public int getQueryTimeout() { // throws SQLException {
		return queryTimeoutInSecs;
	}

	/**
	 * Stores the query timeout in seconds; not enforced by this driver.
	 * @see java.sql.Statement#setQueryTimeout(int)
	 */
	public void setQueryTimeout(final int seconds) { // throws SQLException {
		queryTimeoutInSecs = seconds;
	}

	//////////////////////////////////////////////////////////////////////////////////

	/** The Name of the Table or it's Alias	 */
	protected String cursorName;

	/**
	 * Stores the cursor name used to identify this statement's current row for
	 * positioned updates/deletes.
	 * @see java.sql.Statement#setCursorName(java.lang.String)
	 */
	public void setCursorName(final String name) { // throws SQLException {
		cursorName = name;
	}

	//////////////////////////////////////////////////////////////////////////////////

	/** Reference to the last computed ResultSet */
	protected ResultSet currRS;

	/**
	 * Returns the result set produced by the last executed query.
	 * @see java.sql.Statement#getResultSet()
	 */
	public ResultSet getResultSet() { //throws SQLException {
		return currRS;
	}

	/**
	 * Closes the result set produced by the last executed query, if there is one.
	 * @see java.sql.Statement#close()
	 */
	public void close() throws SQLException {
		if (currRS != null) {
			currRS.close();
			currRS = null;
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	
	/** the fetchDirection is only a Hint */
	protected int fetchDirection;
	
	/**
	 * Stores the fetch direction hint; not enforced by this driver.
	 * @see java.sql.Statement#setFetchDirection(int)
	 */
	public void setFetchDirection(final int direction) { // throws SQLException {
		fetchDirection = direction;
	}

	/**
	 * Returns the value last set via {@link #setFetchDirection(int)}.
	 * @see java.sql.Statement#getFetchDirection()
	 */
	public int getFetchDirection() { // throws SQLException {
		return fetchDirection;
	}

	//////////////////////////////////////////////////////////////////////////////////

	/** the fetchSize is only a Hint, ignored when 0 (default) */
	protected int fetchSize;

	/**
	 * Stores the fetch size hint; not enforced by this driver.
	 * @see java.sql.Statement#setFetchSize(int)
	 */
	public void setFetchSize(final int rows) { // throws SQLException {
		fetchSize = rows;
	}

	/**
	 * Returns the value last set via {@link #setFetchSize(int)}.
	 * @see java.sql.Statement#getFetchSize()
	 */
	public int getFetchSize() { // throws SQLException {
		return fetchSize;
	}

	///////////////////////////////////////////////////////////////////////////

	final int resultSetConcurrency;

	/**
	 * Returns the concurrency mode this Statement was created with.
	 * @see java.sql.Statement#getResultSetConcurrency()
	 */
	public int getResultSetConcurrency() throws SQLException {
		return resultSetConcurrency;
	}

	final int resultSetType;

	/**
	 * Returns the result set type this Statement was created with.
	 * @see java.sql.Statement#getResultSetType()
	 */
	public int getResultSetType() throws SQLException {
		return resultSetType;
	}

	///////////////////////////////////////////////////////////////////////////

	final int resultSetHoldability;

	/**
	 * Returns the holdability this Statement was created with.
	 * @see java.sql.Statement#getResultSetHoldability()
	 */
	public int getResultSetHoldability() throws SQLException {
		return resultSetHoldability;
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Asynchronous Execution of Query Batches
	///////////////////////////////////////////////////////////////////////////
	
	/** Cancels the (asynchronous) Execution
	 * @see java.sql.Statement#cancel()
	 * @see java.sql.Statement#execute(java.lang.String)	 
	 */
	public void cancel() throws SQLException {
	}
	
	/** asynchronous Query Execution
	 * @see java.sql.Statement#cancel()
	 * @see java.sql.Statement#execute(java.lang.String)	 
	 */
	public boolean execute(final String sql) throws SQLException {
		return false;
	}
	
	/**
	 * Always returns {@code 0}; batches of multiple statements are not supported, so there is
	 * no separate update count to report beyond {@link #executeUpdate(String)}'s own result.
	 * @see #executeUpdate(String)
	 * @see java.sql.Statement#getUpdateCount()
	 */
	public int getUpdateCount() throws SQLException {
		return 0;
	}

	/**
	 * Always returns {@code false}; multiple result sets from a batch of statements are not
	 * supported.
	 * @see #executeBatch() with several Statements
	 * @see java.sql.Statement#getMoreResults()
	 */
	public boolean getMoreResults() throws SQLException {
		return false;
	}
	
	/**Moves to this Statement object's next result (multiple SQL Statements), 
	 * deals with any current ResultSet object(s) according to the instructions specified by the given flag, 
	 * and returns true if the next result is a ResultSet object.  
	 * @see java.sql.Statement#getMoreResults(int)	 */
	public boolean getMoreResults(int current) throws SQLException {
		return false;
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Executing a Batch of Statements
	///////////////////////////////////////////////////////////////////////////
	
	/**Adds the given SQL command to the current list of commmands for this Statement object.  
	 * @see java.sql.Statement#addBatch(java.lang.String)	 */
	public void addBatch(final String sql) throws SQLException {
		throw new SQLException("Not Supported");
	}
	
	/**Empties this Statement object's current list of SQL commands.  
	 * @see java.sql.Statement#clearBatch()	 */
	public void clearBatch() throws SQLException {
		throw new SQLException("Not Supported");
	}
	
	/**Submits a batch of commands to the database for execution 
	 * and if all commands execute successfully, returns an array of update counts.  
	 * @see java.sql.Statement#executeBatch()	 */
	public int[] executeBatch() throws SQLException {
		throw new SQLException("Not Supported");
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**Retrieves any auto-generated keys created as a result of executing this Statement object. 
	 * e.g. the primary auto-generated Keys for Inserts.  
	 * @see java.sql.Statement#getGeneratedKeys()	 */
	public ResultSet getGeneratedKeys() throws SQLException {
		throw new SQLException("Not Supported");
	}
	
	/**
	 * Always throws, since retrieving auto-generated keys is not supported.
	 * @throws SQLException always
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int)
	 */
	public int executeUpdate(final String sql, final int autoGeneratedKeys)
		throws SQLException {
		throw new SQLException("Not Supported");
	}

	/**
	 * Always throws, since retrieving auto-generated keys is not supported.
	 * @throws SQLException always
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
	 */
	public int executeUpdate(final String sql, final int[] columnIndexes)
		throws SQLException {
		throw new SQLException("Not Supported");
	}

	/**
	 * Always throws, since retrieving auto-generated keys is not supported.
	 * @throws SQLException always
	 * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
	 */
	public int executeUpdate(final String sql, final String[] columnNames)
		throws SQLException {
		throw new SQLException("Not Supported");
	}

	/**
	 * Ignores {@code autoGeneratedKeys} and delegates to {@link #execute(String)}.
	 * @see java.sql.Statement#execute(java.lang.String, int)
	 */
	public boolean execute(final String sql, final int autoGeneratedKeys)
		throws SQLException {
		return execute(sql);
	}

	/**
	 * Ignores {@code columnIndexes} and delegates to {@link #execute(String)}.
	 * @see java.sql.Statement#execute(java.lang.String, int[])
	 */
	public boolean execute(final String sql, final int[] columnIndexes)
		throws SQLException {
		return execute(sql);
	}

	/**
	 * Ignores {@code columnNames} and delegates to {@link #execute(String)}.
	 * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
	 */
	public boolean execute(final String sql, final String[] columnNames)
		throws SQLException {
		return execute(sql);
	}
	
}
