/*
 * File Name: DriverFix.java
 * Created on: 15.08.2003
 *
 */
package streamIO.integer.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * {@link Driver} implementation for {@code jdbc:fix:} URLs, defaulting every JDBC interface
 * to this package's fixed-length-table classes; a singleton instance registers itself with
 * {@link DriverManager} on class load.
 *
 * <h2>Collaborators</h2>
 *
 * | Type | Relationship |
 * |---|---|
 * | {@link ConnectionFix} | Concrete connection created by {@link #connect(String, Properties)}. |
 * | {@link AConnection} | Supplies the driver property descriptors returned by {@link #getPropertyInfo(String, Properties)}. |
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see streamIO.object.parser.jdbc.DriverSep
 * @see ConnectionFix
 * @see AConnection
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T22:00:16Z
 * digest: fb82ec9ec0130b9bb173c2ac716b99a136847abd8f3ac9e1c01115150507f588
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class DriverFix
implements Driver{

	/** URL Prefix for JDBC-Drivers	*/
	final static public String PREFIX_JDBC = "jdbc:"; 

	/** URL Infix of this Driver */
	final static public String INFIX_FIX = "fix:"; 

	/** URL Prefix of this Driver */
	final static public String PREFIX_FIX = PREFIX_JDBC+INFIX_FIX; 

	/** Major Version of this Driver */
	final static public int MajorVersion = 0; 

	/**
	 * Returns {@link #MajorVersion}.
	 * @see java.sql.Driver#getMajorVersion()
	 */
	public int getMajorVersion() { return MajorVersion; }

	/** Minor Version of this Driver */
	final static public int MinorVersion = 1;

	/**
	 * Returns {@link #MinorVersion}.
	 * @see java.sql.Driver#getMinorVersion()
	 */
	public int getMinorVersion() { return MinorVersion; }

	/** Singleton of this Driver */
	final static public Driver driver = new DriverFix();
	
	/**
	 * Constructor, registers itself with the DriverManager on Class Load
	 */
	private DriverFix() { //throws SQLException {
		try {
			DriverManager.registerDriver(this);
		} catch (SQLException x){
			System.err.println("Could not register with the DriverManager!"); 
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reports whether {@code url} starts with {@link #PREFIX_FIX}.
	 * @see java.sql.Driver#acceptsURL(java.lang.String)
	 */
	public boolean acceptsURL(final String url) throws SQLException {
		return url.startsWith(PREFIX_FIX);
	}

	/**
	 * Strips the Driver specific Part from the URL
	 * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
	 */
	// TODO: LOGIC: acceptsURL(PREFIX_FIX) checks the constant PREFIX_FIX against itself,
	// not the actual `url` parameter - this is always true, so the URL-prefix guard never
	// rejects anything. Any url shorter than PREFIX_FIX then throws
	// StringIndexOutOfBoundsException from url.substring(...) instead of the intended
	// SQLException, and any longer url not actually starting with PREFIX_FIX silently
	// mis-parses the directory path. Should be "acceptsURL(url)".
	public Connection connect(final String url, final Properties info)
		throws SQLException {
		if (!acceptsURL(PREFIX_FIX)) {
			throw new SQLException("Unsupported jdbc-URL:" + url);
		}
		return new ConnectionFix(url.substring(PREFIX_FIX.length()), info);
	}

	/**
	 * Returns {@link AConnection#DRIVER_PROPS_INFO}, ignoring both arguments.
	 * @see java.sql.Driver#getPropertyInfo(java.lang.String, java.util.Properties)
	 */
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) { //throws SQLException {
		return AConnection.DRIVER_PROPS_INFO;
	}

	/**
	 * Only limited SQL Support (no Joins yet)
	 * @see java.sql.Driver#jdbcCompliant()
	 */
	public boolean jdbcCompliant() { return false; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Directory Name for the static Testing Methods	*/
	final static public String testSchemaName = "../../Databases/";

	/** Schema Name for the static Testing Methods	*/
	final static public String strCatalog = "MusicCollection";

	/** Table Name for the static Testing Methods	*/
	final static public String strTable = "Artists";  
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String driverInFix, final String fileSuffix) throws SQLException {
		final Properties props = new Properties(); 
		props.setProperty(ConnectionFix.STR_PROP_SUFFIX, fileSuffix);
		props.setProperty(ConnectionFix.STR_PROP_SEPARATOR, ResultSetFix.TAB_SEPARATORS);
		props.setProperty(ConnectionFix.STR_PROP_ROW_FIELD_NAMES, "0");
		
		String url = PREFIX_JDBC+driverInFix+testSchemaName;
		final Connection conn = DriverManager.getConnection(url, props); 
		//final Connection conn = new ConnectionFix(url, props); 
		final DatabaseMetaData dbMeta = conn.getMetaData();
		
		System.out.println("all Catalogs for this Connection:");
		final ResultSet catalogs = dbMeta.getCatalogs();
		AResultSet.PRINT_RS(catalogs, System.out); 
		
		System.out.println("all Tables for Catalog "+strCatalog);
		final ResultSet tables = dbMeta.getTables(strCatalog, null, null, null);
		AResultSet.PRINT_RS(tables, System.out); 
		
		System.out.println("all Columns for the Table "+strTable);
		final ResultSet columns = dbMeta.getColumns(strCatalog, null, strTable, null);
		AResultSet.PRINT_RS(columns, System.out); 
		
		final Statement st = conn.createStatement();
		final ResultSet rs = st.executeQuery(AStatement.STR_SELECT_ALL+strCatalog+'/'+strTable); // 
		AResultSet.PRINT_RS(rs, System.out); 
	}
	
	/** static testing Method */
	public static void main(final String[] args) throws SQLException {
		testIt(INFIX_FIX, ConnectionFix.SUFFIX_FIX); 
	}
	
}
