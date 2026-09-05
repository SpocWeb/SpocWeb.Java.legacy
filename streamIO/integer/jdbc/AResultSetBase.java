/*
 * Created on 10.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.jdbc;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import streamIO.exception.BaseException;
import streamIO.object.AStreamIn;
import streamIO.object.IStreamIn;

/**
 * Abstract base implementing the operations shared by {@link AResultSet} and
 * {@link FilterResultSet}: column/metadata bookkeeping, the {@link IStreamIn} adapter methods
 * ({@link #currItem()}, {@link #nextItem()}, {@link #availAble()}), and the {@code String}-column
 * overloads of the {@link ResultSet} interface that all resolve to an index via
 * {@link #findColumnOrFail(String)} and delegate to their {@code int}-column counterpart.
 *
 * <h2>Collaborators</h2>
 *
 * | Type | Relationship |
 * |---|---|
 * | {@link DbColumn} | Column descriptors backing {@link #getColumns()} and {@link #getNumCols()}. |
 * | {@link RSMetaData} | Metadata view lazily created by {@link #getMetaData()}. |
 * | {@link AResultSet} | Concrete file-backed subclass. |
 * | {@link FilterResultSet} | Concrete filtering subclass. |
 *
 * @author heuerm
 * @see DbColumn
 * @see RSMetaData
 * @see AResultSet a concrete subclass
 * @see FilterResultSet a concrete subclass
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:44:37Z
 * digest: 708285f3459d7c62fd1aecc56b29a4bf87dc85ba4d5cd91c1051ff9434d7398c
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public abstract class AResultSetBase
extends AStreamIn
implements ResultSet
{

	////////////////////////////////////////////////////////////////////////////
	//  static Methods for creating and filling Objects from a ResultSet
	////////////////////////////////////////////////////////////////////////////

	/** Retrieve Objects of the given Type class from the DB.
	  * The Class has to have an empty Constructor.
	  * The primary key must be set for this,
	  * otherwise an IllegalStateException is thrown
	  * Returns null when not found.  */
	final static public ArrayList GET_OBJECTS(final Class cls, final Field[] Fields, 
			final ResultSet rs) throws SQLException {
		final ArrayList ret = new ArrayList();
		//ArrayList is not synchronized and thus faster
		try {
			while (rs.next()) {
				ret.add(FILL_OBJECT_FROM_RS(cls.newInstance(), Fields, rs));
			}
		} catch (IllegalAccessException x) {
			throw new SQLException(x.toString());
		} catch (InstantiationException x) {
			throw new SQLException(x.toString());
		}
		return ret;
	}

	/** Fills a single Object from the ResultSet
	  * by filling it into the Fields Collection of the Target Object
	  * Prerequisite is the Resultset being filled
	  * and the Fields Array Order to match the Array of Resultset Fields,
	  * i.e. not wanted Fields are null in the Fields Array
	  * This generic Implementation is quite slow due to Reflection,
	  * and should be replaced by an optimized type sensitive one,
	  * to speed up, especially when creating large Sets of Objects. */
	protected static final Object FILL_OBJECT_FROM_RS(
			final Object ret, final Field[] Fields, final ResultSet rs)
		throws SQLException {
		try { //IllegalAccessException should never happen!
			Field fld;
			int i = Fields.length;
			while (--i >= 0) {
				if ((fld = Fields[i]) != null) {
					fld.set(ret, rs.getObject(i));
				}
			} //rs.getObject(DBFieldNames[i]));
		} catch (IllegalAccessException x) {
			throw new SQLException(x.toString());
		}
		return ret;
	} //this prevents Memory to be overfilled when no longer used!

	////////////////////////////////////////////////////////////////////////////
	/// static Methods for Debug- Printing a ResultSet back and forth
	/// made static, because they should be applicable also to other JDBC RS!
	////////////////////////////////////////////////////////////////////////////
	
	/** Prints all rows of the given ResultSet to the given PrintStream	 */
	final static public void PRINT_RS(final ResultSet RS)
		throws SQLException { PRINT_RS(RS, System.out); }
	
	/** Prints all rows of the given ResultSet to the given PrintStream	
	 * @param RS the ResultSet to print 
	 * @param PS the Stream to print to
	 * @return the Number of Rows printed 
	 */
	final static public int PRINT_RS(final ResultSet RS, final PrintStream PS)
		throws SQLException {
		final ResultSetMetaData RSMD = RS.getMetaData();
		final int numCols = RSMD.getColumnCount();
		boolean validRow = true; 
		while (RS.isBeforeFirst()) 
			validRow = RS.next();  
		int ret = 0; 
		if (validRow) {
			do { ++ret; 
				PRINT_RS_ROW(RS, PS, numCols);
			} while (RS.next());
		}
		PS.println("End of ResultSet\n");
		return ret; 
	}

	/** Prints the given ResultSet backwards to System.out	 */
	final static public void PRINT_RS_BACK(final ResultSet RS)
		throws SQLException { PRINT_RS_BACK(RS, System.out); }

	/** Prints the given ResultSet backwards to the given PrintStream	 */
	final static public void PRINT_RS_BACK(final ResultSet RS, final PrintStream PS)
		throws SQLException {
		final ResultSetMetaData RSMD = RS.getMetaData();
		final int numCols = RSMD.getColumnCount();
		while (RS.previous()) {
			PRINT_RS_ROW(RS, PS, numCols);
		}
		PS.println("Start of ResultSet\n");
	}

	/** Prints the current Row of the ResultSet  */
	final static public void PRINT_RS_ROW(
		final ResultSet RS,
		final int numCols)
		throws SQLException {
		PRINT_RS_ROW(RS, System.out, numCols);
	}

	/** Prints the current Row of the ResultSet  */
	final static public void PRINT_RS_ROW(
		final ResultSet RS,
		final PrintStream PS,
		final int numCols)
		throws SQLException {
		for(int i = -1; ++i < numCols;) {
			PS.print(RS.getString(i) + ", ");
		}
		PS.println();
	}

	////////////////////////////////////////////////////////////////////////////
	/// static Methods for Copying a ResultSet
	/// made static, because they should be applicable also to other JDBC RS!
	////////////////////////////////////////////////////////////////////////////

	/** Copies all Rows of the Source ResultSet as new Rows into the dest RS.  */
	public void copyFromRS(final ResultSet source, final int[] srcCols)
		throws SQLException {
		COPY_RS(source, this, srcCols);
	}

	/** Copies all Rows of the Source ResultSet as new Rows into the dest RS.  */
	final static public void COPY_RS(
			final ResultSet source,
			final ResultSet dest,
			final int[] srcCols) throws SQLException {
		int numCols;
		if (srcCols != null) {
			numCols = srcCols.length;
		} else {
			ResultSetMetaData RSMD = source.getMetaData();
			numCols = RSMD.getColumnCount();
		}
		while (source.next()) {
			dest.insertRow(); //also updates the last Row!
			COPY_RS_ROW(source, dest, srcCols, numCols);
		}
		//		dest.updateRow(); //done on closing!
	}

	/** Copies the current Row of the source ResultSet into the dest RS */
	final static public void COPY_RS_ROW(
		ResultSet source,
		ResultSet dest,
		int[] srcCols)
	throws SQLException {
		COPY_RS_ROW(source, dest, srcCols, srcCols.length);
	}

	/** Copies the current Row of the source ResultSet into the dest RS */
	final public void copyRsRow(ResultSet source, int[] srcCols)
	throws SQLException {
		COPY_RS_ROW(source, this, srcCols, srcCols.length);
	}

	/** Copies the current Row of the source ResultSet into the dest RS */
	final static public void COPY_RS_ROW(
		ResultSet source,
		ResultSet dest,
		int[] srcCols,
		int numCols)
		throws SQLException {
		for (int i = numCols, srcCol; --i >= 0;) {
			if (0 > (srcCol = srcCols[i])) {
				continue;
			}
			dest.updateString(i, source.getString(srcCol));
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor, has to call init() to initialize the Object!  */
	protected AResultSetBase(final String _cursorName, final Statement _statement) {
		this.cursorName = _cursorName;
		this.statement  = _statement;
	}

	/** Initializing Constructor
	 * @param _numCols the (maximum) Number of Columns of this ResultSet
	 * @param _fieldNames the Names of the Columns of this ResultSet, can be null
	 */
	protected AResultSetBase( final String[] _fieldNames, final int _numCols, 
			final String _cursorName, final Statement _statement) {
		this(_cursorName, _statement); 
		init(_numCols, _fieldNames);
	}

	/** Initializing Constructor
	 * @param _fieldNames the Names of the Columns of this ResultSet, can be null
	 */
	protected AResultSetBase(final String[] _fieldNames, final String _cursorName, final Statement _statement) {
		this(_cursorName, _statement); 
		init(_fieldNames.length, _fieldNames);
	}

	/** late Initialization
	 * @param _cols the Column Objects of this ResultSet
	 * @param _fieldNames the Names of the Columns of this ResultSet, can be null
	 */
	protected AResultSetBase(final DbColumn[] _cols, final String _cursorName, final Statement _statement) {
		this(_cursorName, _statement); 
		init(_cols);
	}

	/** late Initialization
	 * @param _numCols the (maximum) Number of Columns of this ResultSet
	 * @param _fieldNames the Names of the Columns of this ResultSet, can be null
	 */
	protected void init(final DbColumn[] _cols) {
		if (columns != null)
			throw new RuntimeException("Already initialized!"); 
		this.columns = _cols; 
		this.currRow = new Object[_cols.length]; 
	}
	
	/** late Initialization
	 * the fieldNames are defaulted to their Position Numbers
	 * @param _numCols the (maximum) Number of Columns of this ResultSet
	 */
	protected void init(final int _numCols) {
		init(_numCols, null);
	}
	
	/** late Initialization
	 * @param _fieldNames the Names of the Columns of this ResultSet, can be null
	 */
	protected void init(final String[] _fieldNames) {
		init(_fieldNames.length, _fieldNames); 
	}
	
	/** late Initialization
	 * @param _numCols the (maximum) Number of Columns of this ResultSet
	 * @param _fieldNames the Names of the Columns of this ResultSet, can be null
	 */
	public void init(final int _numCols, String[] _fieldNames) {
		init(new DbColumn[_numCols]);  
		if (_fieldNames == null) 
			_fieldNames =  new String[0]; 
		for (int i = _numCols; --i >= _fieldNames.length; )
			columns[i] = new DbColumn(this, Integer.toString(i), i);
		for (int i = _fieldNames.length; --i >= 0; ) {
			final String fieldName = capitalizing ? _fieldNames[i].toUpperCase() : _fieldNames[i];
				columns[i] = new DbColumn(this,  fieldName, i); 
		} //length[0] == 0 when the Separator is determined from the Stream directly! 
		//So this is no suitable Criterion! 
		if ((_fieldNames   .length == 0) ||
			(_fieldNames[0].length() > 1))
			operationSupported = false; //rather use an Object or int to have three Values
	}

	////////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Gets the name of the SQL cursor (or Table) used by this ResultSet object.	 */
	protected String cursorName; 
	
	/**
	  * Gets the name of the SQL cursor (or Table or it's Alias) used by this ResultSet object.
	  * @see java.sql.ResultSet#getCursorName()
	  */
	public String getCursorName() { return cursorName; }
	
	/** Reference to the Statement Object that produced this ResultSet */
	final Statement statement;
	
	/**
	  * Flag indicating whether the Operation Flag is supported
	  * If yes, and the RS is not read only, all Operations are possible (IUD),
	  * if not, Delete is never possible and Update only in Fixed Length Formats.
	  */
	protected boolean operationSupported = false;
	
	/** Reference to the MetaData Object. 
	 * This is redundant to FieldNames
	 */
	protected RSMetaData metaData;
	
	/** List of the Column Objects containing Names, Aliases or Labels, Field Default Strings and Types  */
	protected DbColumn[] columns;
	
	/**
	 * Returns the number of columns in this result set.
	 * @return the Number of Columns      */
	final public int getNumCols() {
		return columns.length; //metaData.getColumnCount();
	}

	/**
	 * Returns a shallow copy of this result set's column descriptors.
	 * @return a clone of the {@link DbColumn} array backing this result set      */
	final public DbColumn[] getColumns() {
		return  (DbColumn[])columns.clone(); //no deep Clone necessary
	}
	
	/**
	  * Retrieves the number, types and properties of this ResultSet object's columns.
	  * overwritten, because it also contains Field Sizes now...
	  * @see java.sql.ResultSet#getMetaData()
	  */
	public ResultSetMetaData getMetaData() {
		if (metaData == null)
			metaData = new RSMetaData(columns);
		return metaData;
	}
	
	/** return a readable Representation of this Object
	 * @return a readable Representation of this Object
	 */
	public String toString() {
		return this.cursorName+":"+statement; //return the Fields and possibly the current Values
		//for(int i = currRow.length; --i >= 0;) //would trigger too much Action
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variable 'Capitalizing' with Accessor Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** holds Flag whether Column Names and Properties are made case insensitive by Capitalizing   */
	protected boolean capitalizing = true;

	/**
	 * Reports whether column names and properties are compared case-insensitively.
	 * @return Flag whether Column Names and Properties are made case insensitive by Capitalizing  */
	public boolean getCapitalizing() { return capitalizing; }

	/** Sets Flag whether Column Names and Properties are made case insensitive by Capitalizing  */
	//public void setCapitalizing(boolean Capitalizing_) { this.Capitalizing = Capitalizing_; }
	
	////////////////////////////////////////////////////////////////////////////
	// Interface StreamIn: Implementation of abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/** Return Object for StreamIn Operations
	 * made private, since it cannot be made final, 
	 * due to late Initialization  	 */
	protected Object[] currRow; // = new String[columns.length];
	
	/**
	 * Returns the current row, materialized as an {@code Object[]}.
	 * @see streamIO.object.IStreamIn#currItem()
	 * @return the current Object.
	 * In this Case this is the complete Row Object.
	 */
	public Object currItem() { return currRow; }

	/**
	 * Reports {@link IStreamIn#ORDER_NONE}; result set rows carry no defined iteration order.
	 * @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() { return IStreamIn.ORDER_NONE; }
	
	/**
	 * Reports whether at least one more row can be read, without committing to an exact count.
	 * @return the minimum Number of Items left (in the Buffer).
	 * The actual Number may be higher, so available() should be called again
	 * at the End of this Number.
	 *
	 * Nearly equivalent is currItem != null
	 * (when the Container does not contain null Entries, like e.g. HashTables)
	 * @see streamIO.IAvailAble#availAble()
	 */
	public long availAble() {
		try {
		//	if (this.isFirst      ()) return  0; 
			if (this.isLast       ()) return  0; 
			if (this.isAfterLast  ()) return -1; 
		//	if (this.isBeforeFirst()) return -1; 
			return 1;
		} catch (final SQLException x) {
			return -1; 
		}
	}
	
	/**
	 * Returns the current row number, wrapping a checked {@link SQLException} into
	 * an unchecked {@link BaseException}.
	 * @see streamIO.IAvailAble#getPosition()
	 */
	final public long getPosition() {
		try { return getRow();
		} catch (final SQLException x) {
			throw new BaseException(x);
		}
	}

	/**
	 * Advances to and returns the next row as an {@code Object[]}, or {@link #EOI} at the end.
	 * @see streamIO.IFactory#nextItem()
	 * @return  the next (Parent) Object of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster because Exception Handling can be extremely slow.
	 */
	public Object nextItem() {
		try {
			if (!next())
				return EOI; 
			for (int i = currRow.length; --i >= 0;) 
				currRow[i] = this.getObject(i);
			return currRow;
		} catch (final SQLException x) {
			throw new RuntimeException(x); 
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Interface java.sql.RsultSet
	///////////////////////////////////////////////////////////////////////////

	/**
	  * Maps the given ResultSet column name to its ResultSet column index.
	  * Uses a simple linear Search, because the Number of Attributes
	  * in (relational) Tables is typically less than a dozen.
	  * @see java.sql.ResultSet#findColumn(String)
	  */
	public int findColumnOrFail(final String columnName) throws SQLException {
		final int ret = findColumn(columnName);
		if (ret < 0)
			throw new SQLException("Column-Name '"+columnName+"' not found in Query '"+this.cursorName+"'");
		return ret; 
	}
	
	/**
	 * Resolves {@code columnName} to its index and delegates to {@link #updateString(int, String)}.
	 * @see java.sql.ResultSet#updateString(java.lang.String, final java.lang.String)  */
	public void updateString(final String columnName, final String x)
	throws SQLException { updateString(findColumnOrFail(columnName), x); }

	/**
	 * Resolves {@code columnName} to its index and delegates to {@link #updateObject(int, Object)}.
	 * @see java.sql.ResultSet#updateObject(java.lang.String, final java.lang.Object)  */
	public void updateObject(final String columnName, final Object x)
	throws SQLException { updateObject(findColumnOrFail(columnName), x); }
	
	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as an Object in the Java programming language.
	  * @see java.sql.ResultSet#getObject(String)
	  */
	public Object getObject(final String columnName) throws SQLException {
		return getObject(findColumnOrFail(columnName)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object as a String in the Java programming language.
	  * @see java.sql.ResultSet#getString(String)
	  */
	public String getString(final String columnName) throws SQLException {
		return getString(findColumnOrFail(columnName)); }

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object
	  * as an Array object in the Java programming language.
	  * @see java.sql.ResultSet#getArray(String)
	  */
	public Array getArray(final String columnName) throws SQLException {
		return getArray(findColumnOrFail(columnName)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a stream of ASCII characters.
	  * @see java.sql.ResultSet#getAsciiStream(String)
	  */
	public InputStream getAsciiStream(final String columnName) 
	throws SQLException { return getAsciiStream(findColumnOrFail(columnName)); }

	/**
	  * Resolves {@code columnName} to its index and delegates to {@link #getBigDecimal(int, int)}.
	  * @see java.sql.ResultSet#getBigDecimal(String, int)
	  * @deprecated
	  */
	public BigDecimal getBigDecimal(final String columnName, final int scale)
	throws SQLException { return getBigDecimal(findColumnOrFail(columnName), scale); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet
	  * object as a java.math.BigDecimal with full precision.
	  * @see java.sql.ResultSet#getBigDecimal(String)
	  */
	public BigDecimal getBigDecimal(final String columnName) 
	throws SQLException { return getBigDecimal(findColumnOrFail(columnName)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a stream of uninterpreted bytes.
	  * @see java.sql.ResultSet#getBinaryStream(String)
	  */
	public InputStream getBinaryStream(final String columnName) 
	throws SQLException { return getBinaryStream(findColumnOrFail(columnName)); }

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object as a Blob object in the Java programming language.
	  * @see java.sql.ResultSet#getBlob(String)
	  */
	public Blob getBlob(final String colName) throws SQLException { 
		return getBlob(findColumnOrFail(colName)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object as a boolean in the Java programming language.
	  * @see java.sql.ResultSet#getBoolean(String)
	  */
	public boolean getBoolean(final String columnName) throws SQLException {
		return getBoolean(findColumnOrFail(columnName)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a byte in the Java programming language.
	  * @throws NumberFormatException for invalid Characters or exceeding the Range
	  * @see java.sql.ResultSet#getByte(String)
	  */
	public byte getByte(final String columnName) throws SQLException {
		return getByte(findColumnOrFail(columnName)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a byte array in the Java programming language.
	  * @see java.sql.ResultSet#getBytes(String)
	  */
	public byte[] getBytes(final String columnName) throws SQLException {
		return getBytes(findColumnOrFail(columnName)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a java.io.Reader object.
	  * @see java.sql.ResultSet#getCharacterStream(String)
	  */
	public Reader getCharacterStream(final String columnName) throws SQLException {
		return getCharacterStream(findColumnOrFail(columnName)); }

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object
	  * as a Clob object in the Java programming language.
	  * @see java.sql.ResultSet#getClob(String)
	  */
	public Clob getClob(final String colName) throws SQLException { 
		return getClob(findColumnOrFail(colName)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a java.sql.Date object in the Java programming language.
	  * @see java.sql.ResultSet#getDate(String)
	  */
	public Date getDate(final String columnName) throws SQLException { 
		return getDate(findColumnOrFail(columnName)); }

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object
	  * as a java.sql.Date object in the Java programming language.
	  * @see java.sql.ResultSet#getDate(String, Calendar)
	  */
	public Date getDate(final String columnName, final Calendar cal) 
	throws SQLException { return getDate(findColumnOrFail(columnName), cal); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a double in the Java programming language.
	  * @see java.sql.ResultSet#getDouble(String)
	  */
	public double getDouble(final String columnName) throws SQLException { 
		return getDouble(findColumnOrFail(columnName)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a float in the Java programming language.
	  * @see java.sql.ResultSet#getFloat(String)
	  */
	public float getFloat(final String columnName) throws SQLException {
		return getFloat(findColumnOrFail(columnName)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as an int in the Java programming language.
	  * @throws NumberFormatException for invalid Characters or exceeding the Range
	  * @see java.sql.ResultSet#getInt(String)
	  */
	public int getInt(final String columnName) throws SQLException {
		return getInt(findColumnOrFail(columnName)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a long in the Java programming language.
	  * @throws NumberFormatException for invalid Characters or exceeding the Range
	  * @see java.sql.ResultSet#getLong(String)
	  */
	public long getLong(final String columnName) throws SQLException {
		return getLong(findColumnOrFail(columnName)); }

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object
	  * as an Object in the Java programming language.
	  * This method uses the given Map object for the custom mapping of the SQL structured or distinct type that is being retrieved.
	  * @param i - the first column is 1, the second is 2, ...
	  * @param map - a java.util.Map object that contains the mapping from SQL type names to classes in the Java programming language
	  * @return an Object in the Java programming language representing the SQL value
	  * @since 1.2
	  * @see What Is in the JDBC 2.0 API
	  * @see java.sql.ResultSet#getObject(String, Map)
	  */
	public Object getObject(final String colName, final Map map) 
	throws SQLException { return getObject(findColumnOrFail(colName), map); }

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object
	  * as a Ref object in the Java programming language.
	  * @see java.sql.ResultSet#getRef(String)
	  */
	public Ref getRef(final String colName) throws SQLException {
		return getRef(findColumnOrFail(colName)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object as a short in the Java programming language.
	  * @throws NumberFormatException for invalid Characters or exceeding the Range
	  * @see java.sql.ResultSet#getShort(String)
	  */
	public short getShort(final String columnName) throws SQLException {
		return getShort(findColumnOrFail(columnName)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object as a java.sql.Time object in the Java programming language.
	  * @see java.sql.ResultSet#getTime(String)
	  */
	public Time getTime(final String columnName) throws SQLException {
		return getTime(findColumnOrFail(columnName)); }

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object as a java.sql.Time object in the Java programming language.
	  * @see java.sql.ResultSet#getTime(String, Calendar)
	  */
	public Time getTime(final String columnName, final Calendar cal) 
	throws SQLException { return getTime(findColumnOrFail(columnName), cal); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object as a java.sql.Timestamp object.
	  * @see java.sql.ResultSet#getTimestamp(String)
	  */
	public Timestamp getTimestamp(final String columnName) throws SQLException {
		return getTimestamp(findColumnOrFail(columnName)); }

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object as a java.sql.Timestamp object in the Java programming language.
	  * @see java.sql.ResultSet#getTimestamp(String, Calendar)
	  */
	public Timestamp getTimestamp(final String columnName, final Calendar cal) 
	throws SQLException { return getTimestamp(findColumnOrFail(columnName), cal); }

	/**
	  * Deprecated. Use getCharacterStream in place of getUnicodeStream
	  * @see java.sql.ResultSet#getUnicodeStream(String)
	  * @deprecated
	  */
	public InputStream getUnicodeStream(final String columnName) 
	throws SQLException { return getUnicodeStream(findColumnOrFail(columnName)); }

	/**
	  * Deprecated. Use getCharacterStream in place of getUnicodeStream
	  * @see java.sql.ResultSet#getURL(String)
	  */
	public java.net.URL getURL(final String columnName) throws SQLException {
		return getURL(findColumnOrFail(columnName)); }

	/**
	  * Updates the designated column with an ascii stream value.
	  * @since JDK1.4
	  * @see java.sql.ResultSet#updateArray(String, Array)
	  */
	public void updateArray(final String columnName, final Array arr) 
	throws SQLException { updateArray(findColumnOrFail(columnName), arr);	}

	/**
	  * Updates the designated column with an ascii stream value.
	  * @see java.sql.ResultSet#updateAsciiStream(String, InputStream, int)
	  */
	public void updateAsciiStream(final String columnName, final InputStream x, final int length) 
	throws SQLException { updateAsciiStream(findColumnOrFail(columnName), x, length); }

	/**
	  * Updates the designated column with a java.sql.BigDecimal value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateBigDecimal(String, BigDecimal)
	  */
	public void updateBigDecimal(final String columnName, final BigDecimal x) 
	throws SQLException { updateBigDecimal(findColumnOrFail(columnName), x); }

	/**
	  * Updates the designated column with a binary stream value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateBinaryStream(String, InputStream, int)
	  */
	public void updateBinaryStream(final String columnName, final InputStream x, final int length) 
	throws SQLException { updateBinaryStream(findColumnOrFail(columnName), x, length); }

	/**
	  * Updates the designated column with an ascii stream value.
	  * @since JDK1.4
	  * @see java.sql.ResultSet#updateBlob(String, Blob)
	  */
	public void updateBlob(final String columnName, final Blob arr) 
	throws SQLException { updateBlob(findColumnOrFail(columnName), arr); }

	/**
	  * Updates the designated column with a boolean value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateBoolean(String, boolean)
	  */
	public void updateBoolean(final String columnName, final boolean x) 
	throws SQLException { updateBoolean(findColumnOrFail(columnName), x); }

	/**
	  * Updates the designated column with a byte value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateByte(String, byte)
	  */
	public void updateByte(final String columnName, final byte x) 
	throws SQLException { updateByte(findColumnOrFail(columnName), x); }

	/**
	  * Updates the designated column with a boolean value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateBytes(String, byte[])
	  */
	public void updateBytes(final String columnName, final byte[] x) 
	throws SQLException { updateBytes(findColumnOrFail(columnName), x); }

	/**
	  * Updates the designated column with a character stream value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateCharacterStream(String, Reader, int)
	  */
	public void updateCharacterStream(final String columnName, final Reader reader, final int length) 
	throws SQLException { updateCharacterStream(findColumnOrFail(columnName), reader, length); }

	/**
	  * Updates the designated column with an ascii stream value.
	  * @since JDK1.4
	  * @see java.sql.ResultSet#updateClob(String, Clob)
	  */
	public void updateClob(final String columnName, final Clob arr) 
	throws SQLException { updateClob(findColumnOrFail(columnName), arr);	}

	/**
	  * Updates the designated column with a java.sql.Date value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateDate(String, Date)
	  */
	public void updateDate(final String columnName, final Date x) 
	throws SQLException { updateDate(findColumnOrFail(columnName), x); }

	/**
	  * Updates the designated column with a double value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateDouble(String, double)
	  */
	public void updateDouble(final String columnName, final double x) 
	throws SQLException { updateDouble(findColumnOrFail(columnName), x); }

	/**
	  * Updates the designated column with a float value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateFloat(String, float)
	  */
	public void updateFloat(final String columnName, final float x) 
	throws SQLException { updateFloat(findColumnOrFail(columnName), x); }

	/**
	  * Updates the designated column with an int value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateInt(String, int)
	  */
	public void updateInt(final String columnName, final int x) 
	throws SQLException { updateInt(findColumnOrFail(columnName), x); }

	/**
	  * Updates the designated column with a long value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateLong(String, long)
	  */
	public void updateLong(final String columnName, final long x) 
	throws SQLException { updateLong(findColumnOrFail(columnName), x); }

	/**
	  * Updates the designated column with a null value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateNull(String)
	  */
	public void updateNull(final String columnName) throws SQLException {
		updateNull(findColumnOrFail(columnName)); }

	/**
	  * Updates the designated column with an Object value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateObject(String, Object, int)
	  */
	public void updateObject(final String columnName, final Object x, final int scale) 
	throws SQLException { updateObject(findColumnOrFail(columnName), x, scale); }

	/**
	  * Updates the designated column with an ascii stream value.
	  * @since JDK1.4
	  * @see java.sql.ResultSet#updateRef(int, Ref)
	  */
	public void updateRef(final String columnName, final Ref ref) 
	throws SQLException { updateRef(findColumnOrFail(columnName), ref); }

	/**
	  * Updates the designated column with a short value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateShort(String, short)
	  */
	public void updateShort(final String columnName, final short x) 
	throws SQLException { updateShort(findColumnOrFail(columnName), x); }

	/**
	  * Updates the designated column with a java.sql.Time value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateTime(String, Time)
	  */
	public void updateTime(final String columnName, final Time x) 
	throws SQLException { updateTime(findColumnOrFail(columnName), x); }

	/**
	  * Updates the designated column with a java.sql.Timestamp value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateTimestamp(String, Timestamp)
	  */
	public void updateTimestamp(final String columnName, final Timestamp x) 
	throws SQLException { updateTimestamp(findColumnOrFail(columnName), x); }

	/**
	 * Stub override of {@link java.lang.Object#clone}; not implemented and always returns super.clone().
	 *
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#getHoldability}; not implemented and always returns 0.
	 *
	 * @see java.sql.ResultSet#getHoldability()
	 */
	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#getNCharacterStream}; not implemented and always returns null.
	 *
	 * @see java.sql.ResultSet#getNCharacterStream(int)
	 */
	public Reader getNCharacterStream(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#getNCharacterStream}; not implemented and always returns null.
	 *
	 * @see java.sql.ResultSet#getNCharacterStream(java.lang.String)
	 */
	public Reader getNCharacterStream(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#getNClob}; not implemented and always returns null.
	 *
	 * @see java.sql.ResultSet#getNClob(int)
	 */
	public NClob getNClob(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#getNClob}; not implemented and always returns null.
	 *
	 * @see java.sql.ResultSet#getNClob(java.lang.String)
	 */
	public NClob getNClob(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#getNString}; not implemented and always returns null.
	 *
	 * @see java.sql.ResultSet#getNString(int)
	 */
	public String getNString(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#getNString}; not implemented and always returns null.
	 *
	 * @see java.sql.ResultSet#getNString(java.lang.String)
	 */
	public String getNString(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#getRowId}; not implemented and always returns null.
	 *
	 * @see java.sql.ResultSet#getRowId(int)
	 */
	public RowId getRowId(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#getRowId}; not implemented and always returns null.
	 *
	 * @see java.sql.ResultSet#getRowId(java.lang.String)
	 */
	public RowId getRowId(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#getSQLXML}; not implemented and always returns null.
	 *
	 * @see java.sql.ResultSet#getSQLXML(int)
	 */
	public SQLXML getSQLXML(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#getSQLXML}; not implemented and always returns null.
	 *
	 * @see java.sql.ResultSet#getSQLXML(java.lang.String)
	 */
	public SQLXML getSQLXML(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#isClosed}; not implemented and always returns false.
	 *
	 * @see java.sql.ResultSet#isClosed()
	 */
	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateAsciiStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, long)
	 */
	public void updateAsciiStream(int arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateAsciiStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream)
	 */
	public void updateAsciiStream(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateAsciiStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, long)
	 */
	public void updateAsciiStream(String arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateAsciiStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream)
	 */
	public void updateAsciiStream(String arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateBinaryStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, long)
	 */
	public void updateBinaryStream(int arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateBinaryStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream)
	 */
	public void updateBinaryStream(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateBinaryStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, long)
	 */
	public void updateBinaryStream(String arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateBinaryStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream)
	 */
	public void updateBinaryStream(String arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateBlob}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateBlob(int, java.io.InputStream, long)
	 */
	public void updateBlob(int arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateBlob}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateBlob(int, java.io.InputStream)
	 */
	public void updateBlob(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateBlob}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.io.InputStream, long)
	 */
	public void updateBlob(String arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateBlob}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.io.InputStream)
	 */
	public void updateBlob(String arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateCharacterStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, long)
	 */
	public void updateCharacterStream(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateCharacterStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader)
	 */
	public void updateCharacterStream(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateCharacterStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	public void updateCharacterStream(String arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateCharacterStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader)
	 */
	public void updateCharacterStream(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateClob}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateClob(int, java.io.Reader, long)
	 */
	public void updateClob(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateClob}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateClob(int, java.io.Reader)
	 */
	public void updateClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateClob}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader, long)
	 */
	public void updateClob(String arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateClob}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader)
	 */
	public void updateClob(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateNCharacterStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateNCharacterStream(int, java.io.Reader, long)
	 */
	public void updateNCharacterStream(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateNCharacterStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateNCharacterStream(int, java.io.Reader)
	 */
	public void updateNCharacterStream(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateNCharacterStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateNCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	public void updateNCharacterStream(String arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateNCharacterStream}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateNCharacterStream(java.lang.String, java.io.Reader)
	 */
	public void updateNCharacterStream(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateNClob}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateNClob(int, java.sql.NClob)
	 */
	public void updateNClob(int arg0, NClob arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateNClob}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateNClob(int, java.io.Reader, long)
	 */
	public void updateNClob(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateNClob}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateNClob(int, java.io.Reader)
	 */
	public void updateNClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateNClob}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateNClob(java.lang.String, java.sql.NClob)
	 */
	public void updateNClob(String arg0, NClob arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateNClob}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateNClob(java.lang.String, java.io.Reader, long)
	 */
	public void updateNClob(String arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateNClob}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateNClob(java.lang.String, java.io.Reader)
	 */
	public void updateNClob(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateNString}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateNString(int, java.lang.String)
	 */
	public void updateNString(int arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateNString}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateNString(java.lang.String, java.lang.String)
	 */
	public void updateNString(String arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateRowId}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateRowId(int, java.sql.RowId)
	 */
	public void updateRowId(int arg0, RowId arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateRowId}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateRowId(java.lang.String, java.sql.RowId)
	 */
	public void updateRowId(String arg0, RowId arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateSQLXML}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateSQLXML(int, java.sql.SQLXML)
	 */
	public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stub override of {@link java.sql.ResultSet#updateSQLXML}; not implemented and performs no action.
	 *
	 * @see java.sql.ResultSet#updateSQLXML(java.lang.String, java.sql.SQLXML)
	 */
	public void updateSQLXML(String arg0, SQLXML arg1) throws SQLException {
		// TODO Auto-generated method stub
		
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

}
