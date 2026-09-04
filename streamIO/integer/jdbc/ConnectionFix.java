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
 * Title: ConnectionFix<p>
 * Description:
 * Purpose:
 * Provides a Connection Implementation for the jdbc 1.0 Framework
 * using Files with fixed Record Size Content. 
 * This Format has three Advantages: 
 * 1) all Characters can be used, no Escaping is necessary  
 * 2) Updates can be done in Place, only Deletions create holes 
 * 3) Editing is easy even with simple Editors.
 * Disadvantages are: 
 * 1) Field Sizes can not be exceeded easily (although Size doesn't matter much anymore...), 
 * either several Rows are concatenated 
 * or the variable-Length Data is externalized into a separate file (BLOB). 
 * 2) since Columns are only named at the Top, it is easy to mistype Data
 * 
 * defaults all Interface Implementations to the Classes of this Package.  
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 * 
 * similar Classes: 
 * @see streamIO.object.parser.jdbc.ConnectionSep which represents a Connection 
 * to a Set of Separated Files. 
 * In Principal both Types of DB Tables can be mixed. 
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
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
	 * 
	 * @param url URL of the jdbc Connection
	 * @param suffix Properties Object containing the Table Files Suffix (Default: ".fix")
	 * @throws SQLException when the Directory does not exist or the Prefix does not match. 
	 */
	public ConnectionFix(final String url, final Properties propSuffix)
		throws SQLException {
		super(url, DriverFix.driver, propSuffix, SUFFIX_FIX, ResultSetFix.TAB_SEPARATORS); 
	}

	/**
	 * @param path
	 * @param driver_
	 * @param propSuffix
	 * @param defaultSuffix
	 * @param defaultSep
	 * @throws SQLException
	 */
	public ConnectionFix(final String path, final Properties propSuffix,
			final String defaultSuffix, final String defaultSep, final Driver driver_) throws SQLException {
		super(path, driver_, propSuffix, defaultSuffix, defaultSep);
	}
	
	/**
	 * @param _path
	 * @param _driver
	 * @param _suffix
	 * @param _separator
	 * @throws SQLException
	 */
	public ConnectionFix(final String _path, final String _suffix, 
			final String _separator, final Driver _driver) throws SQLException {
		super(_path, _driver, _suffix, _separator);
	}
	
	/**
	 * @param _path
	 * @param _suffix
	 * @param _separator
	 * @throws SQLException
	 */
	public ConnectionFix(final String _path, final String _suffix, 
			final String _separator) throws SQLException {
		super(_path, _suffix, _separator);
	}
	
	/**
	 * @param _path
	 * @param _suffix
	 * @param _separator
	 * @throws SQLException
	 */
	public ConnectionFix(final String _path, final String _suffix) throws SQLException {
		super(_path, _suffix, null);
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods
	///////////////////////////////////////////////////////////////////////////

	/**@see java.sql.Connection#createStatement()	 */
	public Statement createStatement() { //throws SQLException {
		return new StatementFix(this);
	}

	/**@see java.sql.Connection#prepareStatement(java.lang.String)	 */
	public PreparedStatement prepareStatement(final String sql) { //throws SQLException {
		return new PrepStatementFix(this, sql);
	}

	/**@see java.sql.Connection#prepareCall(java.lang.String)	 */
	public CallableStatement prepareCall(final String sql) { //throws SQLException {
		return new CallStatementFix(this, sql);
	}

	/** @see java.sql.Connection#createStatement(int, int)	 */
	public Statement createStatement(
		final int resultSetType,
		final int resultSetConcurrency) { //throws SQLException {
		return new StatementFix(this, resultSetType, resultSetConcurrency);
	}

	/** @see java.sql.Connection#prepareStatement(java.lang.String, int, int)	 */
	public PreparedStatement prepareStatement(final String sql,
		final int resultSetType,
		final int resultSetConcurrency) { //throws SQLException {
		return new PrepStatementFix(this, sql, resultSetType, resultSetConcurrency);
	}

	/** @see java.sql.Connection#prepareCall(java.lang.String, int, int)	 */
	public CallableStatement prepareCall(final String sql,
		final int resultSetType,
		final int resultSetConcurrency) { //throws SQLException {
		return new CallStatementFix(this, sql, resultSetType, resultSetConcurrency);
	}

	/** @see java.sql.Connection#createStatement(int, int, int)	 */
	public Statement createStatement(
			final int resultSetType,
			final int resultSetConcurrency,
			final int resultSetHoldability) { //throws SQLException {
		return new StatementFix(this,
			resultSetType,
			resultSetConcurrency,
			resultSetHoldability);
	}

	/** @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)	 */
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

	/** @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)	 */
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

	/** @see java.sql.Connection#prepareStatement(java.lang.String, int)	 */
	public PreparedStatement prepareStatement(final String sql,
			final int autoGeneratedKeys) { //throws SQLException {
		return new PrepStatementFix(this, sql,
			ResultSet.TYPE_FORWARD_ONLY,
			ResultSet.CONCUR_READ_ONLY);
	}

	/** @see java.sql.Connection#prepareStatement(java.lang.String, int[])	 */
	public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) { //throws SQLException {
		return new PrepStatementFix(this, sql,
			ResultSet.TYPE_FORWARD_ONLY,
			ResultSet.CONCUR_READ_ONLY);
	}
	
	/** @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])	 */
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
