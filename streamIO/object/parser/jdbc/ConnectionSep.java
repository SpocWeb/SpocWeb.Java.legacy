/*
 * File Name: ConnectionSep.java
 * Created on: 15.08.2003
 *
 */
package streamIO.object.parser.jdbc;

import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import streamIO.Assert;
import streamIO.Log;
import streamIO.integer.jdbc.AConnection;
import streamIO.integer.jdbc.AResultSet;
import streamIO.integer.jdbc.ResultSetFix;

/**
 * Title: ConnectionSep<p>
 * Description:
 * Purpose:
 * Provides a Connection Implementation for the jdbc 1.0 Framework
 * using Files with separated Content. 
 * 
 * Defaults all Interface Implementations to the Classes of this Package.  
 *
 * Known SubClasses: <none>
 * 
 * Known Uses: <none>
 * 
 * Similar Classes: 
 * @see streamIO.integer.jdbc.ConnectionFix 
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see streamIO.integer.jdbc.ConnectionFix
 */
public class ConnectionSep 
extends AConnection {
	
	private static final Log L = new Log(ConnectionSep.class); 
	
	/** Default Separators for Comma and CR/LF separated Files	 */
	private static final String SEPARATORS_CSV = "\\,\r\n";
	
	/** Default Separators for Tab and CR/LF separated Files	 */
	private static final String SEPARATORS_TAB = null; //"\\\t\r\n";
	
	/** Default Suffix for Files with Separated Format RecordSets */
	final static public String SUFFIX_SEP = ".sep"; 

	/** Default Suffix for Files with Comma Separated Format RecordSets */
	final static public String SUFFIX_CSV = ".csv"; 

	/** Default Suffix for Files with Tab and CR/LF separated Format RecordSets */
	final static public String SUFFIX_TAB = ".tab"; 
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors 
	///////////////////////////////////////////////////////////////////////////

	/**
	 * @param path
	 * @param driver_
	 * @param propSuffix
	 * @param defaultSuffix
	 * @param defaultSep
	 * @throws SQLException
	 */
	public ConnectionSep(final String path, final Driver driver_, final Properties propSuffix,
			final String defaultSuffix, final String defaultSep) throws SQLException {
		super(path, driver_, propSuffix, defaultSuffix, defaultSep);
	}
	
	/**
	 * 
	 * @param url URL of the jdbc Connection
	 * @param suffix Properties Object containing the Table Files Suffix (Default: ".fix")
	 * @throws SQLException when the Directory does not exist or the Prefix does not match. 
	 */
	public ConnectionSep(final String url, final Properties propSuffix) throws SQLException {
		super(url, DriverSep.driver, propSuffix, SUFFIX_SEP, ResultSetFix.TAB_SEPARATORS); 
	}

	/**
	 * @param _path
	 * @param _driver
	 * @param _suffix
	 * @param _separator
	 * @throws SQLException
	 */
	public ConnectionSep(final String _path, final Driver _driver, final String _suffix,
			final String _separator) throws SQLException {
		super(_path, _driver, _suffix, _separator);
	}
	
	/**
	 * @param _path
	 * @param _suffix
	 * @param _separator
	 * @throws SQLException
	 */
	public ConnectionSep(final String _path, final String _suffix, final String _separator)
			throws SQLException {
		super(_path, _suffix, _separator);
	}
	
	/**
	 * @param _path
	 * @param _suffix If the Suffix is not given, it is assumed to be present in the Table Names. 
	 * @throws SQLException
	 */
	public ConnectionSep(final String _path, final String _suffix)
			throws SQLException {
		super(_path, _suffix, SUFFIX_CSV.equals(_suffix) ? SEPARATORS_CSV : SEPARATORS_TAB);
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods
	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see java.sql.Connection#createStatement()
	 */
	public Statement createStatement() { //throws SQLException {
		return new StatementSep(this);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public PreparedStatement prepareStatement(final String sql) { //throws SQLException {
		return new PrepStatementSep(this, sql);
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public CallableStatement prepareCall(final String sql) { //throws SQLException {
		return new CallStatementSep(this, sql);
	}

	/** @see java.sql.Connection#createStatement(int, int)	 */
	public Statement createStatement(
		final int resultSetType,
		final int resultSetConcurrency) { //throws SQLException {
		return new StatementSep(this, resultSetType, resultSetConcurrency);
	}

	/** @see java.sql.Connection#prepareStatement(java.lang.String, int, int)	 */
	public PreparedStatement prepareStatement(final String sql,
		final int resultSetType,
		final int resultSetConcurrency) { //throws SQLException {
		return new PrepStatementSep(this, sql, resultSetType, resultSetConcurrency);
	}

	/** @see java.sql.Connection#prepareCall(java.lang.String, int, int)	 */
	public CallableStatement prepareCall(final String sql,
		final int resultSetType,
		final int resultSetConcurrency) { //throws SQLException {
		return new CallStatementSep(this, sql, resultSetType, resultSetConcurrency);
	}

	/** @see java.sql.Connection#createStatement(int, int, int)	 */
	public Statement createStatement(
			final int resultSetType,
			final int resultSetConcurrency,
			final int resultSetHoldability) { //throws SQLException {
		return new StatementSep(this,
			resultSetType,
			resultSetConcurrency,
			resultSetHoldability);
	}

	/** @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)	 */
	public PreparedStatement prepareStatement(final String sql,
			final int resultSetType,
			final int resultSetConcurrency,
			final int resultSetHoldability) { //throws SQLException {
		return new PrepStatementSep(this, sql,
			resultSetType,
			resultSetConcurrency,
			resultSetHoldability);
	}

	/** @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)	 */
	public CallableStatement prepareCall(final String sql,
			final int resultSetType,
			final int resultSetConcurrency,
			final int resultSetHoldability) { //throws SQLException {
		return new CallStatementSep(this, sql,
			resultSetType,
			resultSetConcurrency,
			resultSetHoldability);
	}

	/** TODO: support autogenerated Keys */

	/** @see java.sql.Connection#prepareStatement(java.lang.String, int)	 */
	public PreparedStatement prepareStatement(final String sql,
			final int autoGeneratedKeys) { //throws SQLException {
		return new PrepStatementSep(this, sql,
			ResultSet.TYPE_FORWARD_ONLY,
			ResultSet.CONCUR_READ_ONLY);
	}

	/** @see java.sql.Connection#prepareStatement(java.lang.String, int[])	 */
	public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) { //throws SQLException {
		return new PrepStatementSep(this, sql,
			ResultSet.TYPE_FORWARD_ONLY,
			ResultSet.CONCUR_READ_ONLY);
	}

	/** @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])	 */
	public PreparedStatement prepareStatement(final String sql, final String[] columnNames) { //throws SQLException {
		return new PrepStatementSep(this, sql,
			ResultSet.TYPE_FORWARD_ONLY,
			ResultSet.CONCUR_READ_ONLY);
	}

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** File Path for the static Testing Methods	*/
	final static public String TEST_DB_PATH = "../../Databases/MusicCollection/";
	
	/** File Suffix for the static Testing Methods	*/
	final static public String TEST_DB_SUFFIX = ".tab";
	
	/** Query String for the static Testing Methods	*/
	final static public String TEST_DB_QUERY_CROSS_JOIN = 
		"SELECT * \n"+
		"FROM Artists, CDs "+
		"WHERE Artists.ID = CDs.ArtistID AND Artists.Name = 'SEAL' ";
	
	/** Query String for the static Testing Methods	*/
	final static public String TEST_DB_QUERY_JOIN = 
		"SELECT * \n"+
		"FROM Artists RIGHT JOIN CDs \n"+ //TODO: RIGHT here is a Hint, but the ResultSetCrossJoin should be able to identify the N-Side to iterate over
		"ON Artists.ID = CDs.ArtistID \n" +
		"WHERE Artists.Name = 'SEAL' ";
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws SQLException {
		L.timer(null); 
		Assert.EQUALS(2, PRINT_RESULT(TEST_DB_PATH, TEST_DB_SUFFIX, TEST_DB_QUERY_CROSS_JOIN, System.out)); 
		L.timer(TEST_DB_QUERY_CROSS_JOIN); 
		Assert.EQUALS(2, PRINT_RESULT(TEST_DB_PATH, TEST_DB_SUFFIX, TEST_DB_QUERY_JOIN, System.out)); 
		L.timer(TEST_DB_QUERY_JOIN); 
	}
	
	/** Tests all Methods of this Class	 */
	public static int PRINT_RESULT(final String url, final String suffix, final String query,
			final PrintStream out) throws SQLException {
		final ConnectionSep conn = new ConnectionSep(url, suffix); 
		final Statement statemnt = conn.createStatement(); 
		final ResultSet rs = statemnt.executeQuery(query); 
		return AResultSet.PRINT_RS(rs, out); 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws SQLException {
		if (args.length == 0) 
			testIt();
		else 
			PRINT_RESULT(args[0], args[1], args[2], System.out); 
	}
	
}
