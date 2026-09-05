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
 * <!-- docstate
 * tags: [code/jdbc_adapter, code/sax_event_generation]
 * concepts: [Minimal JDBC Driver over Separated-Format Flat Files]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
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

	/** Initializing Constructor forwarding an explicit Driver, Suffix Properties, Default Suffix
	  * and Default Separators to the base Class.
	 * @param path Directory holding the separated-Files "Tables"
	 * @param driver_ jdbc Driver to register this Connection with
	 * @param propSuffix Properties mapping per-Table Suffixes, when they differ from the Default
	 * @param defaultSuffix Suffix assumed for a Table when not present in propSuffix
	 * @param defaultSep Separator Characters assumed for a Table when not present in propSuffix
	 * @throws SQLException
	 */
	public ConnectionSep(final String path, final Driver driver_, final Properties propSuffix,
			final String defaultSuffix, final String defaultSep) throws SQLException {
		super(path, driver_, propSuffix, defaultSuffix, defaultSep);
	}
	
	/** Initializing Constructor defaulting the Driver to {@link DriverSep#driver}, the File Suffix
	  * to {@link #SUFFIX_SEP} and the Separators to {@link ResultSetFix#TAB_SEPARATORS}.
	 * @param url URL of the jdbc Connection
	 * @param suffix Properties Object containing the Table Files Suffix (Default: ".fix")
	 * @throws SQLException when the Directory does not exist or the Prefix does not match.
	 */
	public ConnectionSep(final String url, final Properties propSuffix) throws SQLException {
		super(url, DriverSep.driver, propSuffix, SUFFIX_SEP, ResultSetFix.TAB_SEPARATORS); 
	}

	/** Initializing Constructor using an explicit jdbc Driver and a single Suffix/Separator pair
	  * for every Table in the Directory.
	 * @param _path Directory holding the separated-Files "Tables"
	 * @param _driver jdbc Driver to register this Connection with
	 * @param _suffix File Suffix used for every Table
	 * @param _separator Separator Characters used for every Table
	 * @throws SQLException
	 */
	public ConnectionSep(final String _path, final Driver _driver, final String _suffix,
			final String _separator) throws SQLException {
		super(_path, _driver, _suffix, _separator);
	}

	/** Initializing Constructor defaulting to {@link DriverSep#driver} with a single Suffix/Separator
	  * pair for every Table in the Directory.
	 * @param _path Directory holding the separated-Files "Tables"
	 * @param _suffix File Suffix used for every Table
	 * @param _separator Separator Characters used for every Table
	 * @throws SQLException
	 */
	public ConnectionSep(final String _path, final String _suffix, final String _separator)
			throws SQLException {
		super(_path, _suffix, _separator);
	}

	/** Initializing Constructor deriving the Separator Characters from the given Suffix
	  * (Comma-separated for {@link #SUFFIX_CSV}, Tab-separated otherwise).
	 * @param _path Directory holding the separated-Files "Tables"
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

	/** Creates a plain Statement backed by this separated-Files Connection.
	 * @see java.sql.Connection#createStatement()
	 */
	public Statement createStatement() { //throws SQLException {
		return new StatementSep(this);
	}

	/** Creates a PreparedStatement for the given SQL, backed by this separated-Files Connection.
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public PreparedStatement prepareStatement(final String sql) { //throws SQLException {
		return new PrepStatementSep(this, sql);
	}

	/** Creates a CallableStatement for the given SQL, backed by this separated-Files Connection.
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public CallableStatement prepareCall(final String sql) { //throws SQLException {
		return new CallStatementSep(this, sql);
	}

	/** Creates a Statement with the given ResultSet Type and Concurrency.
	  * @see java.sql.Connection#createStatement(int, int)	 */
	public Statement createStatement(
		final int resultSetType,
		final int resultSetConcurrency) { //throws SQLException {
		return new StatementSep(this, resultSetType, resultSetConcurrency);
	}

	/** Creates a PreparedStatement with the given ResultSet Type and Concurrency.
	  * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)	 */
	public PreparedStatement prepareStatement(final String sql,
		final int resultSetType,
		final int resultSetConcurrency) { //throws SQLException {
		return new PrepStatementSep(this, sql, resultSetType, resultSetConcurrency);
	}

	/** Creates a CallableStatement with the given ResultSet Type and Concurrency.
	  * @see java.sql.Connection#prepareCall(java.lang.String, int, int)	 */
	public CallableStatement prepareCall(final String sql,
		final int resultSetType,
		final int resultSetConcurrency) { //throws SQLException {
		return new CallStatementSep(this, sql, resultSetType, resultSetConcurrency);
	}

	/** Creates a Statement with the given ResultSet Type, Concurrency and Holdability.
	  * @see java.sql.Connection#createStatement(int, int, int)	 */
	public Statement createStatement(
			final int resultSetType,
			final int resultSetConcurrency,
			final int resultSetHoldability) { //throws SQLException {
		return new StatementSep(this,
			resultSetType,
			resultSetConcurrency,
			resultSetHoldability);
	}

	/** Creates a PreparedStatement with the given ResultSet Type, Concurrency and Holdability.
	  * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)	 */
	public PreparedStatement prepareStatement(final String sql,
			final int resultSetType,
			final int resultSetConcurrency,
			final int resultSetHoldability) { //throws SQLException {
		return new PrepStatementSep(this, sql,
			resultSetType,
			resultSetConcurrency,
			resultSetHoldability);
	}

	/** Creates a CallableStatement with the given ResultSet Type, Concurrency and Holdability.
	  * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)	 */
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

	// TODO: LOGIC: `autoGeneratedKeys` is accepted but never consulted; this always behaves
	// like the plain 2-arg prepareStatement(String) with fixed TYPE_FORWARD_ONLY/CONCUR_READ_ONLY,
	// silently ignoring a caller's request for generated Keys instead of honoring or rejecting it.
	/** Always uses TYPE_FORWARD_ONLY/CONCUR_READ_ONLY, ignoring the requested autoGeneratedKeys Flag.
	  * @see java.sql.Connection#prepareStatement(java.lang.String, int)	 */
	public PreparedStatement prepareStatement(final String sql,
			final int autoGeneratedKeys) { //throws SQLException {
		return new PrepStatementSep(this, sql,
			ResultSet.TYPE_FORWARD_ONLY,
			ResultSet.CONCUR_READ_ONLY);
	}

	// TODO: LOGIC: `columnIndexes` is accepted but never consulted, same as the autoGeneratedKeys
	// overload above.
	/** Always uses TYPE_FORWARD_ONLY/CONCUR_READ_ONLY, ignoring the requested Column Indexes.
	  * @see java.sql.Connection#prepareStatement(java.lang.String, int[])	 */
	public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) { //throws SQLException {
		return new PrepStatementSep(this, sql,
			ResultSet.TYPE_FORWARD_ONLY,
			ResultSet.CONCUR_READ_ONLY);
	}

	// TODO: LOGIC: `columnNames` is accepted but never consulted, same as the two overloads above.
	/** Always uses TYPE_FORWARD_ONLY/CONCUR_READ_ONLY, ignoring the requested Column Names.
	  * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])	 */
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
