/*
 * File Name: DriverSep.java
 * Created on: 15.08.2003
 *
 */
package streamIO.object.parser.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import streamIO.integer.jdbc.AConnection;
import streamIO.integer.jdbc.DriverFix;

/**
 * Provides a Driver Implementation for the jdbc 2.0 Framework
 * defaults all Interface Implementations to the Classes of this Package.  
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Similar Classes: 
 * @see streamIO.integer.jdbc.DriverFix
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * tags: [code/jdbc_adapter, code/sax_event_generation]
 * concepts: [Minimal JDBC Driver over Separated-Format Flat Files]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class DriverSep implements Driver{

	/** URL Infix of this Driver */
	final static public String INFIX_SEP = "sep:"; 

	/** URL Prefix of this Driver */
	final static public String PREFIX_SEP = DriverFix.PREFIX_JDBC+INFIX_SEP; 

	/** Major Version of this Driver */
	final static public int MajorVersion = 0; 

	/** Minor Version of this Driver */
	final static public int MinorVersion = 1; 

	/** Singleton of this Driver */
	final static public DriverSep driver = new DriverSep(); 

	/** Switches ignoring the Case of Field and Table Names */
	final static public boolean ignoreIDCase = false;

	/** Switches ignoring the Case of Field and Table Names */
	final static public boolean useToUpper = true;

	/**
	 * Constructor, registers itself with the DriverManager on Class Load
	 */
	private DriverSep() { //throws SQLException {
		try {
			DriverManager.registerDriver(this);
		} catch (SQLException x){
			System.err.println("Could not register with the DriverManager!"); 
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Returns the fixed Major Version of this Driver.
	 * @see java.sql.Driver#getMajorVersion()
	 */
	public int getMajorVersion() {
		return MajorVersion;
	}

	/** Returns the fixed Minor Version of this Driver.
	 * @see java.sql.Driver#getMinorVersion()
	 */
	public int getMinorVersion() {
		return MinorVersion;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Tests whether the given URL starts with this Driver's {@link #PREFIX_SEP}.
	 * @see java.sql.Driver#acceptsURL(java.lang.String)
	 */
	public boolean acceptsURL(final String url) throws SQLException {
		return url.startsWith(PREFIX_SEP);
	}

	// TODO: LOGIC: calls acceptsURL(PREFIX_SEP) instead of acceptsURL(url), so this always
	// checks the Constant against itself (trivially true) rather than validating the actual
	// `url` Argument. Any URL - matching Prefix or not - falls through to
	// url.substring(PREFIX_SEP.length()), which throws StringIndexOutOfBoundsException for a
	// URL shorter than PREFIX_SEP instead of the intended SQLException("Unsupported jdbc-URL").
	/** Strips this Driver's Prefix from the URL and opens a {@link ConnectionSep} on the remainder.
	 * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
	 */
	public Connection connect(final String url, final Properties info)
		throws SQLException {
		if (!acceptsURL(PREFIX_SEP)) {
			throw new SQLException("Unsupported jdbc-URL:" + url);
		}
		return new ConnectionSep(url.substring(PREFIX_SEP.length()), info);
	}

	/** Returns the fixed set of Connection Properties this Driver understands.
	  * @see java.sql.Driver#getPropertyInfo(java.lang.String, java.util.Properties)	 */
	public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) { //throws SQLException {
		return AConnection.DRIVER_PROPS_INFO;
	}

	/**
	 * Only limited SQL Support (no Joins yet)
	 * @see java.sql.Driver#jdbcCompliant()
	 */
	public boolean jdbcCompliant() {
		return false;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws SQLException {
		DriverFix.testIt(INFIX_SEP, ConnectionSep.SUFFIX_TAB);
	}

}
