/*
 * File Name: AConnection.java
 * Created on: 15.08.2003
 *
 */
package streamIO.integer.jdbc;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.NClob;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Title: AConnection<p>
 * Description:
 * Purpose:
 *
 * Connection Object holding the Parameters for all Tables 
 * in a Directory. 
 *
 * Known SubClasses: 
 * @see streamIO.object.parser.jdbc.ConnectionSep
 * @see streamIO.integer.jdbc.ConnectionFix
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
public abstract class AConnection 
implements Connection {

	/** Factory Method for a fully initialized @see DriverPropertyInfo Instance */
	final static public DriverPropertyInfo GET_DRIVER_PROPERTY_INFO(final String name, 
		final String description, final boolean required, final String[] choices) {
		return GET_DRIVER_PROPERTY_INFO(name, description, required, choices, null); 
	}

	/** Factory Method for a fully initialized @see DriverPropertyInfo Instance */
	final static public DriverPropertyInfo GET_DRIVER_PROPERTY_INFO(final String name, 
		final String description, final boolean required, final String[] choices, final String value) {
		DriverPropertyInfo ret = new DriverPropertyInfo(name, value);
		ret.description = description; 
		ret.required = required; 
		ret.choices = choices;
		return ret; 
	}

	/** Constant for a Connection Parameter in a Properties Object	*/
	final static public String STR_PROP_SUFFIX = "Suffix";

	/** Constant for a Connection Parameter in a Properties Object	*/
	final static public String STR_PROP_SEPARATOR = "Separator";

	/** Constant for a Connection Parameter in a Properties Object	*/
	final static public String STR_PROP_IGNORE_ID_CASE = "ignoreIDCase";

	/** Default for a Connection Parameter */
	final static public boolean DEFAULT_IGNORE_ID_CASE = false;

	/** Constant for a Connection Parameter in a Properties Object	*/
	final static public String STR_PROP_USE_TO_UPPER = "useToUpper";

	/** Default for a Connection Parameter */
	final static public boolean DEFAULT_USE_TO_UPPER = true;

	/** Constant for a Connection Parameter in a Properties Object	*/
	final static public String STR_PROP_ROW_FIELD_NAMES = "rowFieldNames";

	/** Default for a Connection Parameter */
	final static public int DEFAULT_ROW_FIELD_NAMES = -1; //0;

	/** Constant for a Connection Parameter in a Properties Object	 */
	final static public String STR_PROP_ROW_FIELD_DEFAULTS = "rowFieldDefaults";
	
	/** Default for a Connection Parameter */
	final static public int DEFAULT_ROW_FIELD_DEFAULTS = -1; //1;

	/** Driver Properties for Separators or Fixed Format Databases	 */
	final static public DriverPropertyInfo[] DRIVER_PROPS_INFO = {
		GET_DRIVER_PROPERTY_INFO(STR_PROP_SUFFIX, "The Suffix of the Files to be used as Tables.", true, null),
		GET_DRIVER_PROPERTY_INFO(STR_PROP_SEPARATOR, "The Separator Characters to parse the Columns and Rows if not given as first Character in the File.", false, null),
		GET_DRIVER_PROPERTY_INFO(STR_PROP_ROW_FIELD_NAMES, "The Row Number for the Field Defaults", false, new String[]{"0"}),
		GET_DRIVER_PROPERTY_INFO(STR_PROP_ROW_FIELD_DEFAULTS, "The Row Number for the Field Defaults", false, new String[]{"1"}),
		GET_DRIVER_PROPERTY_INFO(STR_PROP_IGNORE_ID_CASE, "Flag to ignore the Case for Identifiers like Table or Column Names", false, null),
		GET_DRIVER_PROPERTY_INFO(STR_PROP_USE_TO_UPPER, "Flag to switch between converting to lower or upper Case when ignoring", false, null)
		//GET_DRIVER_PROPERTY_INFO(STR_PROP_, null),
	};

	/////////////////////////////////////////////////////////////////////////////////////////////
	
	/** return a readable Represenation of this Object
	 * @return a readable Represenation of this Object
	 */
	public String toString() { return urlDir+"/*"+suffix; }
	
	/** the Url is the Location of the Directory where the Files can be found */
	final public File urlDir;
	
	/** the Suffix to recognize the Table Files */
	final public String suffix;
	
	/** the String of Separators to parse by (when using parsing ResultSets) */
	final public String separators;
	
	/** @return the Instance of the Driver used	 */
	final public Driver driver; 
	
	/** Flag for ignoring the Case	 */
	final public boolean ignoreIDCase;
	
	/** Flag for converting the Field Names to Upper Case	 */
	final public boolean useToUpper; 
	
	/** if >= 0, the row Number to contain the Field Names */ 
	final public int rowFieldNames; 
	
	/** if >= 0, the row Number to contain the Field Defaults */ 
	final public int rowFieldDefaults;
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Initializing Constructor 
	 * 
	 * @param path URL or Path of the jdbc Connection
	 * @param driver_ Reference to the Driver creating this Connection 
	 * @param propSuffix Properties Object containing the Table Files Suffix (Default: ".fix")
	 * @param defaultSuffix Default Value, if the Properties Object does not contain a Suffix String
	 * @param defaultSep Default Value, if the Properties Object does not contain a Separator String
	 * @throws SQLException when the Directory does not exist or the Prefix does not match. 
	 */
	public AConnection(final String path, final Driver driver_, final Properties propSuffix, final String defaultSuffix, final String defaultSep)
		throws SQLException {
		this.driver = driver_; 
		suffix      = propSuffix.getProperty(STR_PROP_SUFFIX, defaultSuffix);
		separators  = propSuffix.getProperty(STR_PROP_SEPARATOR, defaultSep);
		ignoreIDCase= Boolean.valueOf(propSuffix.getProperty(STR_PROP_IGNORE_ID_CASE, Boolean.toString(DEFAULT_IGNORE_ID_CASE))).booleanValue();
		useToUpper  = Boolean.valueOf(propSuffix.getProperty(STR_PROP_USE_TO_UPPER  , Boolean.toString(DEFAULT_USE_TO_UPPER  ))).booleanValue();
		rowFieldNames    = Integer.valueOf(propSuffix.getProperty(STR_PROP_ROW_FIELD_NAMES   , Integer.toString(DEFAULT_ROW_FIELD_NAMES   ))).intValue();
		rowFieldDefaults = Integer.valueOf(propSuffix.getProperty(STR_PROP_ROW_FIELD_DEFAULTS, Integer.toString(DEFAULT_ROW_FIELD_DEFAULTS))).intValue();
		try {
			final URI uri = new URI(path);
			if (uri.isAbsolute()) {
				this.urlDir = new File(uri);
			} else {
				this.urlDir = new File(path);
			}
		} catch (URISyntaxException x) {
			throw new SQLException(x.toString());
		}
	}

	/**
	 * Initializing Constructor 
	 * 
	 * @param path URL or Path of the jdbc Connection
	 * @param _suffix the Table Files Suffix (Default: ".fix")
	 * @param _separator the Table Files Separator (Default: "\t"
	 * @throws SQLException when the Directory does not exist or the Prefix does not match. 
	 */
	public AConnection(final String _path, final String _suffix, final String _separator)
		throws SQLException {
		this(_path, null, _suffix, _separator); 
	}

	/**
	 * Initializing Constructor 
	 * 
	 * @param path URL or Path of the jdbc Connection
	 * @param driver_ the Driver Object creating this Connection 
	 * @param propSuffix Properties Object containing the Table Files Suffix (Default: ".fix")
	 * @param _suffix the Table Files Suffix (Default: ".fix")
	 * @param _separator the Table Files Separator (Default: "\t"
	 * @throws SQLException when the Directory does not exist or the Prefix does not match. 
	 */
	public AConnection(final String _path, final Driver _driver, final String _suffix, final String _separator)
		throws SQLException {
		this.driver = _driver; 
		this.suffix      = _suffix;
		this.separators  = _separator;
		this.ignoreIDCase= DEFAULT_IGNORE_ID_CASE;
		this.useToUpper  = DEFAULT_USE_TO_UPPER;
		this.rowFieldNames    = DEFAULT_ROW_FIELD_NAMES;
		this.rowFieldDefaults = DEFAULT_ROW_FIELD_DEFAULTS; 
		try {
			final URI uri = new URI(_path);
			if (uri.isAbsolute()) {
				this.urlDir = new File(uri);
			} else {
				this.urlDir = new File(_path);
			}
		} catch (final URISyntaxException x) {
			throw new SQLException(x.toString());
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/** Reference to the corresponding MetaData Object */
	protected DBMetaData dbMetaData;

	/** 
	 * not cached, since it is possibly dynamic (new Tables)
	 * @see java.sql.Connection#getMetaData()
	 */
	public DatabaseMetaData getMetaData() { //throws SQLException {
		if (dbMetaData == null)
			dbMetaData =  new DBMetaData(this);
		return dbMetaData; 
	}

	///////////////////////////////////////////////////////////////////////////

	/** Flag to indicate Closed ResultSets */
	private boolean closed = false;

	/**
	 * @see java.sql.Connection#close()
	 */
	public void close() { //do nothing...
		closed = true; //TODO: close all open ResultSets too! 
	}

	/**
	 * @see java.sql.Connection#isClosed()
	 */
	public boolean isClosed() { return closed; }

	///////////////////////////////////////////////////////////////////////////

	/** Flag to indicate Read-Only ResultSets */
	private boolean readOnly = false;

	/** 
	 * @see java.sql.Connection#setReadOnly(boolean)
	 */
	public void setReadOnly(final boolean readOnly_) { readOnly = readOnly_; }

	/**
	 * @see java.sql.Connection#isReadOnly()
	 */
	public boolean isReadOnly() { return readOnly; }

	///////////////////////////////////////////////////////////////////////////

	/** Catalog Name used for this Connection */
	private String catalog; 

	/**
	 * @see java.sql.Connection#setCatalog(java.lang.String)
	 */
	public void setCatalog(final String catalog_) { //throws SQLException {
		this.catalog = catalog_;
	}

	/**
	 * @see java.sql.Connection#getCatalog()
	 */
	public String getCatalog() { //throws SQLException {
		return catalog;
	}

	///////////////////////////////////////////////////////////////////////////

	/** Flag to indicate Read-Only ResultSets */
	final static public int isolationLevelDefault = TRANSACTION_NONE; 

	/** Flag to indicate Read-Only ResultSets */
	private int isolationLevel = TRANSACTION_NONE; 

	/** Helper Method to centralize Error Messaging	 */
	protected void throwTxNotSupported() throws SQLException {
		throw new SQLException("Transactions are not supported!"); 
	}

	/** @see java.sql.Connection#setTransactionIsolation(int)	 */
	public void setTransactionIsolation(final int level) throws SQLException {
		if (level != isolationLevelDefault) {
			throwTxNotSupported(); 
		}
		isolationLevel = level; 
	}

	/** @see java.sql.Connection#getTransactionIsolation()	 */
	public int getTransactionIsolation() { //throws SQLException {
		return isolationLevel;
	}

	///////////////////////////////////////////////////////////////////////////

	/** holds the Warnings, private for Type-Safety 	*/
	private final List warnings = new ArrayList();

	protected void addWarning(final SQLWarning warning) {
		warnings.add(warning);
	}

	/** @see java.sql.Connection#getWarnings()	 */
	public SQLWarning getWarnings() { //throws SQLException {
		return (SQLWarning) warnings.remove(warnings.size());
	}

	/** @see java.sql.Connection#clearWarnings()	 */
	public void clearWarnings() { //throws SQLException {
		warnings.clear();
	}
	
	///////////////////////////////////////////////////////////////////////////

	/** Member for the TypeMap used */
	protected Map typeMap;

	/** @see java.sql.Connection#getTypeMap()	 */
	public Map getTypeMap() { //throws SQLException {
		return typeMap;
	}

	/** @see java.sql.Connection#setTypeMap(java.util.Map)	 */
	public void setTypeMap(final Map map) { //throws SQLException {
		typeMap = map;
	}

	///////////////////////////////////////////////////////////////////////////

	protected int holdAbility;

	/** @see java.sql.Connection#setHoldability(int)	 */
	public void setHoldability(final int holdAbility_) { //throws SQLException {
		holdAbility = holdAbility_;
	}

	/** @see java.sql.Connection#getHoldability()	 */
	public int getHoldability() { //throws SQLException {
		return holdAbility; 
	}

	///////////////////////////////////////////////////////////////////////////

	/** @see java.sql.Connection#setSavepoint()	 */
	public Savepoint setSavepoint() { //throws SQLException {
		return null;
	}

	/** @see java.sql.Connection#setSavepoint(java.lang.String)	 */
	public Savepoint setSavepoint(final String name) { //throws SQLException {
		return null;
	}

	/** @see java.sql.Connection#rollback(java.sql.Savepoint)	 */
	public void rollback(final Savepoint savepoint) { //throws SQLException {
	}

	/** @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)	 */
	public void releaseSavepoint(final Savepoint savepoint) { //throws SQLException {
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see java.sql.Connection#nativeSQL(java.lang.String)
	 */
	public String nativeSQL(final String sql) { //throws SQLException {
		return sql;
	}

	/**
		 * @see java.sql.Connection#setAutoCommit(boolean)
		 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		if (!autoCommit) 
			throwTxNotSupported();
	}

	/** @see java.sql.Connection#getAutoCommit()	 */
	public boolean getAutoCommit() { //throws SQLException {
		return true;
	}

	/** @see java.sql.Connection#commit()	 */
	public void commit() throws SQLException {
		throwTxNotSupported();
	}

	/** @see java.sql.Connection#rollback()	 */
	public void rollback() throws SQLException {
		throwTxNotSupported();
	}
	
	/** @see java.sql.Connection#createArrayOf(java.lang.String, java.lang.Object[]) 	 */
	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.Connection#createBlob()	 */
	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.Connection#createClob()	 */
	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.Connection#createNClob()	 */
	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.Connection#createSQLXML()	 */
	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.Connection#createStruct(java.lang.String, java.lang.Object[])	 */
	public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.Connection#getClientInfo()	 */
	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.Connection#getClientInfo(java.lang.String)	 */
	public String getClientInfo(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see java.sql.Connection#isValid(int)	 */
	public boolean isValid(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/** @see java.sql.Connection#setClientInfo(java.util.Properties)	 */
	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.Connection#setClientInfo(java.lang.String, java.lang.String)	 */
	public void setClientInfo(String arg0, String arg1) throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	}

	/** @see java.sql.Wrapper#isWrapperFor(java.lang.Class)	 */
	public boolean isWrapperFor(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/** @see java.sql.Wrapper#unwrap(java.lang.Class)	 */
	public Object unwrap(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
