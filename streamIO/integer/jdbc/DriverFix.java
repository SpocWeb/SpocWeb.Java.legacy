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
 * Title: DriverFix<p>
 * Description:
 * Purpose:
 * Provides a Driver Implementation for the jdbc 2.0 Framework
 * defaults all Interface Implementations to the Classes of this Package.  
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 * @see streamIO.object.parser.jdbc.DriverSep
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
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

	/** @see java.sql.Driver#getMajorVersion()	 */
	public int getMajorVersion() { return MajorVersion; }

	/** Minor Version of this Driver */
	final static public int MinorVersion = 1; 

	/** @see java.sql.Driver#getMinorVersion()	 */
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
	 * @see java.sql.Driver#acceptsURL(java.lang.String)
	 */
	public boolean acceptsURL(final String url) throws SQLException {
		return url.startsWith(PREFIX_FIX);
	}

	/**
	 * Strips the Driver specific Part from the URL
	 * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
	 */
	public Connection connect(final String url, final Properties info)
		throws SQLException {
		if (!acceptsURL(PREFIX_FIX)) {
			throw new SQLException("Unsupported jdbc-URL:" + url);
		}
		return new ConnectionFix(url.substring(PREFIX_FIX.length()), info);
	}

	/** @see java.sql.Driver#getPropertyInfo(java.lang.String, java.util.Properties)	 */
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
