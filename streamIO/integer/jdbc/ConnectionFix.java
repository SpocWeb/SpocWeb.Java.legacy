/*
 * File Name: ConnectionFix.java
 * Created on: 15.08.2003
 *
 */
package streamIO.integer.jdbc;

import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * {@link AConnection} implementation for {@code jdbc:fix:} URLs backed by files with a
 * fixed record size, defaulting every JDBC statement/result-set interface to this
 * package's fixed-length-table classes ({@link StatementFix}, {@link PrepStatementFix},
 * {@link CallStatementFix}). Fixed-length records allow arbitrary character content with
 * no escaping and in-place updates, at the cost of a hard field-size limit.
 *
 * <h2>Collaborators</h2>
 *
 * | Type | Relationship |
 * |---|---|
 * | {@link StatementFix} | Created by the {@code createStatement} overloads. |
 * | {@link PrepStatementFix} | Created by the {@code prepareStatement} overloads. |
 * | {@link CallStatementFix} | Created by the {@code prepareCall} overloads. |
 * | {@link DriverFix} | Default driver used when none is passed explicitly. |
 *
 * @see streamIO.object.parser.jdbc.ConnectionSep a similar Connection over a Set of
 * Separated Files; the two Table Types can in principle be mixed.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T22:09:29Z
 * digest: aeab9cb357ba5027bbae4c39f5fd00877fbe790fdf6b1440c2521d118f0e283d
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class ConnectionFix 
extends AConnection {

	/** Default Suffix for Files with Fixed Length Format RecordSets */
	final static public String SUFFIX_FIX = ".fix";

	/** Default Suffix for Files with Fixed Length Format RecordSets */
	final static public String SUFFIX_DAT = ".dat";

	///////////////////////////////////////////////////////////////////////////
	/// Constructors 
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Initializing constructor using {@link DriverFix#driver} and fixed-length defaults.
	 * @param url URL of the jdbc Connection
	 * @param propSuffix Properties Object containing the Table Files Suffix (Default: ".fix")
	 * @throws SQLException when the Directory does not exist or the Prefix does not match.
	 */
	public ConnectionFix(final String url, final Properties propSuffix)
		throws SQLException {
		super(url, DriverFix.driver, propSuffix, SUFFIX_FIX, ResultSetFix.TAB_SEPARATORS); 
	}

	/**
	 * Initializing constructor with an explicit {@link Driver} and default suffix/separator.
	 * @param path URL of the jdbc Connection
	 * @param propSuffix Properties Object containing the Table Files Suffix
	 * @param defaultSuffix fallback Table File Suffix if {@code propSuffix} does not specify one
	 * @param defaultSep fallback Field Separator
	 * @param driver_ the Driver that created this Connection
	 * @throws SQLException when the Directory does not exist or the Prefix does not match.
	 */
	public ConnectionFix(final String path, final Properties propSuffix,
			final String defaultSuffix, final String defaultSep, final Driver driver_) throws SQLException {
		super(path, driver_, propSuffix, defaultSuffix, defaultSep);
	}

	/**
	 * Initializing constructor with an explicit {@link Driver}, fixed suffix and separator.
	 * @param _path URL of the jdbc Connection
	 * @param _suffix the Table File Suffix
	 * @param _separator the Field Separator
	 * @param _driver the Driver that created this Connection
	 * @throws SQLException when the Directory does not exist or the Prefix does not match.
	 */
	public ConnectionFix(final String _path, final String _suffix,
			final String _separator, final Driver _driver) throws SQLException {
		super(_path, _driver, _suffix, _separator);
	}

	/**
	 * Initializing constructor using {@link DriverFix#driver} with a fixed suffix and separator.
	 * @param _path URL of the jdbc Connection
	 * @param _suffix the Table File Suffix
	 * @param _separator the Field Separator
	 * @throws SQLException when the Directory does not exist or the Prefix does not match.
	 */
	public ConnectionFix(final String _path, final String _suffix,
			final String _separator) throws SQLException {
		super(_path, _suffix, _separator);
	}

	/**
	 * Initializing constructor using {@link DriverFix#driver} and no explicit separator.
	 * @param _path URL of the jdbc Connection
	 * @param _suffix the Table File Suffix
	 * @throws SQLException when the Directory does not exist or the Prefix does not match.
	 */
	public ConnectionFix(final String _path, final String _suffix) throws SQLException {
		super(_path, _suffix, null);
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a new {@link StatementFix} bound to this connection.
	 * @see java.sql.Connection#createStatement()
	 */
	public Statement createStatement() { //throws SQLException {
		return new StatementFix(this);
	}

	/**
	 * Returns a new {@link PrepStatementFix} bound to this connection.
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public PreparedStatement prepareStatement(final String sql) { //throws SQLException {
		return new PrepStatementFix(this, sql);
	}

	/**
	 * Returns a new {@link CallStatementFix} bound to this connection.
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public CallableStatement prepareCall(final String sql) { //throws SQLException {
		return new CallStatementFix(this, sql);
	}

	/**
	 * Returns a new {@link StatementFix} with the given result set type and concurrency.
	 * @see java.sql.Connection#createStatement(int, int)
	 */
	public Statement createStatement(
		final int resultSetType,
		final int resultSetConcurrency) { //throws SQLException {
		return new StatementFix(this, resultSetType, resultSetConcurrency);
	}

	/**
	 * Returns a new {@link PrepStatementFix} with the given result set type and concurrency.
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
	 */
	public PreparedStatement prepareStatement(final String sql,
		final int resultSetType,
		final int resultSetConcurrency) { //throws SQLException {
		return new PrepStatementFix(this, sql, resultSetType, resultSetConcurrency);
	}

	/**
	 * Returns a new {@link CallStatementFix} with the given result set type and concurrency.
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	public CallableStatement prepareCall(final String sql,
		final int resultSetType,
		final int resultSetConcurrency) { //throws SQLException {
		return new CallStatementFix(this, sql, resultSetType, resultSetConcurrency);
	}

	/**
	 * Returns a new {@link StatementFix} with the given result set type, concurrency and
	 * holdability.
	 * @see java.sql.Connection#createStatement(int, int, int)
	 */
	public Statement createStatement(
			final int resultSetType,
			final int resultSetConcurrency,
			final int resultSetHoldability) { //throws SQLException {
		return new StatementFix(this,
			resultSetType,
			resultSetConcurrency,
			resultSetHoldability);
	}

	/**
	 * Returns a new {@link PrepStatementFix} with the given result set type, concurrency and
	 * holdability.
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
	 */
	public PreparedStatement prepareStatement(
			final String sql,
			final int resultSetType,
			final int resultSetConcurrency,
			final int resultSetHoldability) { //throws SQLException {
		return new PrepStatementFix(this, sql,
			resultSetType,
			resultSetConcurrency,
			resultSetHoldability);
	}

	/**
	 * Returns a new {@link CallStatementFix} with the given result set type, concurrency and
	 * holdability.
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
	 */
	public CallableStatement prepareCall(
			final String sql,
			final int resultSetType,
			final int resultSetConcurrency,
			final int resultSetHoldability) { //throws SQLException {
		return new CallStatementFix(this, sql,
			resultSetType,
			resultSetConcurrency,
			resultSetHoldability);
	}

	/** TODO: support autogenerated Keys */

	/**
	 * Returns a forward-only, read-only {@link PrepStatementFix}; {@code autoGeneratedKeys}
	 * is accepted but not yet honored (see the {@code TODO} above).
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
	 */
	public PreparedStatement prepareStatement(final String sql,
			final int autoGeneratedKeys) { //throws SQLException {
		return new PrepStatementFix(this, sql,
			ResultSet.TYPE_FORWARD_ONLY,
			ResultSet.CONCUR_READ_ONLY);
	}

	/**
	 * Returns a forward-only, read-only {@link PrepStatementFix}; {@code columnIndexes} is
	 * accepted but not yet honored (see the {@code TODO} above).
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
	 */
	public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) { //throws SQLException {
		return new PrepStatementFix(this, sql,
			ResultSet.TYPE_FORWARD_ONLY,
			ResultSet.CONCUR_READ_ONLY);
	}

	/**
	 * Returns a forward-only, read-only {@link PrepStatementFix}; {@code columnNames} is
	 * accepted but not yet honored (see the {@code TODO} above).
	 * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
	 */
	public PreparedStatement prepareStatement(final String sql, final String[] columnNames) { //throws SQLException {
		return new PrepStatementFix(this, sql,
			ResultSet.TYPE_FORWARD_ONLY,
			ResultSet.CONCUR_READ_ONLY);
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** File Path for the static Testing Methods	*/
	final static public String TEST_DB_PATH = "../../Databases/MusicCollection/";
	
	/** File Suffix for the static Testing Methods	*/
	final static public String TEST_DB_SUFFIX = ".fix";
	
	/** File Suffix for the static Testing Methods	*/
	final static public String TEST_DB_QUERY  
		= "Select * From Artists "
		//+ "Where Artists.Name = 'SEAL' "
		;
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws SQLException {
		PRINT_RESULT(TEST_DB_PATH, TEST_DB_SUFFIX, TEST_DB_QUERY, System.out); 
	}

	/** Tests all Methods of this Class	 */
	public static void PRINT_RESULT(final String url, final String suffix, final String query,
			final PrintStream out) throws SQLException {
		final ConnectionFix conn = new ConnectionFix(url, suffix); 
		final Statement statemnt = conn.createStatement(); 
		final ResultSet rs = statemnt.executeQuery(query); 
		AResultSet.PRINT_RS(rs, out); 
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
