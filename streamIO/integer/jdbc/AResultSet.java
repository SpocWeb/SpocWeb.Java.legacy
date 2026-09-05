package streamIO.integer.jdbc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import math.vector.VectorString;
import function.byref.ByRefDouble;
import function.byref.ByRefLong;

/**
  * Abstract Bridge Class implementing the ResultSet Interface
  * for using a Container or a File as Backing Storage.
  * This is also the Prototype for writing custom JDBC ResultSet Classes
  * that are synchronized with Data Files in plain ASCII. 
  * 
  * The last Row returned is the insertRow 
  * and can also be used as the Match in the Outer Join! 
  * 
  * Also some static Methods to...
  * ...fill Objects from a ResultSet
  * ...print a ResultSet Row or completely
  * ...fill  a StringBuffer from a  byte[] Array
  * ...fill  a Collection from an Object[] Array
  *
  * Most Methods throw SQLException on Errors, because they are usually connected to
  * I/O Processes which can throw IOExceptions that are wrapped in SQL Exceptions.
  *
  * The Formatting of Standard Datatypes should adhere to the XML Schema Definition.
  * Timestamp: yyyy-mm-ddThh:mm[-hh:mm] where the optional second Number indicates
  * the local Time Offset to GMT.
  * Numbers: fixed Comma Syntax is required to allow numerical sorting
  * by alphabetical sorting so parsing the Numbers can be skipped.
  *
  * Column Information:
  * Data about the Columns that is shared between the Items of a Column are:
  * -Data Type
  * -Maximum Length
  * -AllowNull resp. Default Value (Default Value = Null <=> AllowNull = true)
  * -Column Name (the Data can be accessed by Index, which can be derived from the Collection of all Columns)
  * -Ordinal/Index in the Collection of Columns
  * -ReadOnly for certain Columns only
  * -Uniqueness
  * -AutoIncrement (Seed & Step)
  * -Sort Order for binary and Hunt Search Support
  * 
  * Other JDBC Implementations for plain ASCII Text Databases (Fix & Sep): 
  * HXTT (www.hxtt.com) von Hongxin Technology (China) 
  * hat vollen Tx-Support, Thread-safe, schreibt in ZIP/TAR files, verschl�sselt etc.    
  * Other JDBC Implementations: 
  * HSQLDB HSQLDB.org bzw. HSQLDB.sourceforge.net also used in OpenOffice 
  * als Fortsetzung von Thomas M�llers Hypersonic SQL Projekt.  
  * 
  * Subclasses:
  * @see streamIO.Byte.ResultSetFix
  * @see streamIO.object.parser.ResultSetSep
  * 
  * related Classes: 
  * @see streamIO.Byte.RSMetaData
  * @see ResultSetXmlAttribute
  * @see ResultSetXmlElement
  * Converting XML into ResultSets is not necessary, 
  * because they can be XSLTd into separated Files. 
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:47:22Z
  * digest: 8b3c200aa071ee9b7570a389504776fd449da30bb53de3fb650a66aa25773382
  * stale: false
  * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
  * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public abstract class AResultSet
extends AResultSetBase
implements ResultSet 
{

	////////////////////////////////////////////////////////////////////////////
	//  static Constants, Variables, Defaults
	////////////////////////////////////////////////////////////////////////////

	/**
	  * Error Message for Read Only ResultSet
	  */
	final static public String STR_READ_ONLY = "Read only ResultSet";

	//////////////////////////////////////////////////////////////////////////////////////////

	/** Constant for the first ("Operation") Field 
	 * indicating that the Record contains the Field Lengths 
	 * if parsed by the following Character which is the Separaor
	 */
	//protected static final byte CHR_SEPARATION = 'S';

	/** Constant for the first ("Operation") Field 
	 * indicating that the Record contains the Field Names
	 * By Default the first Row contains the Field Names, 
	 * this is just an Override
	 */
	protected static final byte CHR_FIELD_LABELS = 'L';

	/** Constant for the first ("Operation") Field 
	 * indicating that the Record contains the Field Names
	 * By Default the first Row contains the Field Names, 
	 * this is just an Override
	 */
	protected static final byte CHR_FIELD_NAMES = 'F';

	/** Constant for the first ("Operation") Field 
	 * indicating that the Record contains the Default Values
	 */
	protected static final byte CHR_FIELD_DEFAULTS = 'D';

	/** Constant for the first ("Operation") Field 
	 * indicating that the Record contains the Not Null Values
	 */
	protected static final byte CHR_FIELD_NULLS = 'N';

	/** Constant for the first ("Operation") Field 
	 * indicating that the Record contains the Comment Values, 
	 * although in Fixed Size Format it is limited by the Field Size! 
	 */
	protected static final byte CHR_FIELD_COMMENT = 'C';

	/** Constant for the first ("Operation") Field 
	 * indicating that the Record contains the Read Only Values for Columns
	 */
	protected static final byte CHR_FIELD_WRITABLE = 'W';

	//////////////////////////////////////////////////////////////////////////////////////////

	/** Constant for the first ("Operation") Field 
	 * indicating that the Record contains the Type Values
	 */
	protected static final byte CHR_FIELD_TYPES = 'T';
	
	/** Constant indicating the Field Type Integer */
	protected static final byte CHR_TYPE_INTEGER = 'I';
	
	/** Constant indicating the designated column is automatically numbered, thus read-only. */
	protected static final byte CHR_TYPE_COUNTER = 'C';
	
	/** Constant indicating the Field Type Float */
	protected static final byte CHR_TYPE_FLOAT = 'F';
	
	/** Constant indicating the Field Type String */
	protected static final byte CHR_TYPE_STRING = 'S';
	
	/** Constant indicating the Field Type Date in Format yyyy-MM-dd */
	protected static final byte CHR_TYPE_DATE = 'D';
	
	/** Constant indicating the Field Type Time in Format hh:mm:ss.mmm*/
	protected static final byte CHR_TYPE_TIME = 'T';
	
	/** Constant indicating the Field Type TimeStamp in Format yyyy-MM-dd'T'hh:mm:ss.mmm*/
	protected static final byte CHR_TYPE_TIMESTAMP = 'P';
	
	/** Constant indicating the undefined Field Type  */
	protected static final byte CHR_TYPE_NULL = 0;
	
	/** Constant indicating the Field Type Boolean */
	protected static final byte CHR_TYPE_BOOLEAN = 'B';
	
	//////////////////////////////////////////////////////////////////////////////////////////
	/// Ternary Logic 
	//////////////////////////////////////////////////////////////////////////////////////////

	/** Constant indicating the undefined Field Type  */
	protected static final byte CHR_BOOLEAN_NULL = 0; 

	/** Constant indicating the Boolean Field Value 'false' */
	protected static final byte CHR_BOOLEAN_FALSE = '0';

	/** Constant indicating the Boolean Field Value 'true' */
	protected static final byte CHR_BOOLEAN_TRUE = '1';

	//////////////////////////////////////////////////////////////////////////////////////////
	/// Status Column Values
	//////////////////////////////////////////////////////////////////////////////////////////

	/** Constant for the Trimming Character */
	protected static final byte CHR_SPACE = ' ';

	/** Constant for the first ("Operation") Field indicating that the Record is deleted */
	protected static final byte CHR_OP_DELETED = '-';

	/** Constant for the first ("Operation") Field indicating that the Record is deleted */
	protected static final byte CHR_OP_CONTINUED = '&';

	/** Constant for the first ("Operation") Field indicating that the Record is inserted */
	protected static final byte CHR_OP_INSERTED = '+';

	/** Constant for the first ("Operation") Field indicating that the Record is updated */
	protected static final byte CHR_OP_UPDATED = '~'; //STR_UPDATED.charAt(0);

	/** Constant for the first ("Operation") Field indicating that the Record is just read */
	protected static final byte CHR_OP_NEUTRAL = CHR_SPACE; //(byte) STR_NEUTRAL.charAt(0);

	//////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Maps a column type code to its representing Java class.
	 * @param type the TypeID to identify the Type
	 * @return the Class associated with the given TypeID
	 * @throws SQLException if the TypeID is unknown.
	 */
	final static public Class GET_CLASS_FROM_TYPE(final byte type) { //throws SQLException {
		switch (type) {
		case CHR_TYPE_COUNTER   : return Long.class; 
		case CHR_TYPE_BOOLEAN   : return Boolean.class; 
		case CHR_TYPE_DATE      : return Date.class; 
		case CHR_TYPE_FLOAT     : return Double.class; 
		case CHR_TYPE_INTEGER   : return Long.class; 
		case CHR_TYPE_STRING    : return String.class; 
		case CHR_TYPE_TIME      : return Time.class; 
		case CHR_TYPE_TIMESTAMP : return Timestamp.class; 
		case CHR_TYPE_NULL      : return String.class;
		//case CHR_TYPE_ : return .class;
		default :
			throw new ArrayIndexOutOfBoundsException("Unexpected Type Flag:'"+type+"'");
		}
	}

	////////////////////////////////////////////////////////////////////////////
	//  static Constants for Tokens in the File Header.
	////////////////////////////////////////////////////////////////////////////

	/** Value of the Token that indicates the Start of the Data	 */
	final static public String TOKEN_COMMENT = "Comment";

	/** Value of the Token that indicates the Column the Data is sorted by
	  * Sorting by multiple Columns is not supported,
	  * although it would be possible by just appending the Contents
	  * or nesting the Sort Test, but Types are not well supported anyway.
	  */
	final static public String TOKEN_SORTED_COL = "SortedBy";

	/** Value of the Token that indicates the Number of Data Items at Level x (Rows)	 */
	final static public String TOKEN_NUM_ITEMS = "NumItems";

	/** Value of the Token that indicates the Existence of Names for the Fields (Columns)	 */
	//final static public String TOKEN_FIELD_NAMES = "FieldNames";

	/** Value of the Token that indicates the Existence of Defaults for the Fields (Columns)	 */
	//final static public String TOKEN_FIELD_DEFAULTS = "FieldDefaults";

	/** Value of the Token that indicates the Support of a Delete of Data Items	 */
	final static public String TOKEN_DELETE_FLAG = "DeleteFlag";

	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Flag that controls parsing of Numbers
	 * if true, no SqlException is thrown. 
	 * Instead a NaN or MIN_VALUE is returned on parsing Double or Int.
	 */
	public boolean tolerantNumberParsing = true;
	
	/** Flag that controls reading deleted Rows in the move Operations
	  * This is an Optimization that speeds up skipping logically deleted Rows. */
	protected boolean readDeleted = false;
	
	/** Flag indicating whether this RS is read only
	  * this also prevents reacting to the first Column,
	  * resulting in not skipping deleted Lines
	  *
	  * no Flags => readOnly, but not vice versa!
	  */
	protected boolean readOnly; // = false;

	/** Constant for the Position of the first ("Operation") Field  */
	//	protected int OP_FIELD = 0; //hard coded in many Places and (unfortunately!) Assumptions...

	/** Flag for the Status Row: N,I,U,D or _,+,*,-
	 * contains the first Character indicating whether the Row was changed.
	 * Has to be set after each read Operation
	 * and to be set by each Write Operation.
	 * Making this an abstract Method forces direct Subclasses to implement it,
	 * but futher Subclasses are not that safe.
	 */
	protected byte operationFlag = CHR_OP_NEUTRAL;
	//	protected abstract byte getOperationFlag();
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Cache for the FetchSize, i.e. the Number of Rows retrieved ahead by the Container	*/
	protected int fetchSize;

	/**
	  * Returns the fetch size last set via {@link #setFetchSize(int)}.
	  * @see java.sql.ResultSet#getFetchSize()
	  */
	public int getFetchSize() { return fetchSize; }

	/**
	  * Gives the JDBC driver a hint as to the number of rows
	  * that should be fetched from the database
	  * when more rows are needed for this ResultSet object.
	  * @see java.sql.ResultSet#setFetchSize(int)
	  */
	public void setFetchSize(final int rows) { fetchSize = rows; }
	
	///////////////////////////////////////////////////////////////////////////

	/** The physical Pointer to the insert Row Number for the Cursor.
	  * For Fixed Size Formats this is the Row Number
	  * For delimited variable Size Formats this is the absolute Byte Position in the File.
	  * It is initialized on opening the File to the last Row / Position in the File.
	  */
	//protected int insertPosition = 0;
	
	/** Cache for the current absolute physical Position (not Row Number, this is stored in Position) 
	 * when inserting Rows while moving through a ResultSet
	 * @see moveToCurrentRow() uses this Variable to restore the Position
	 * @see moveToInsertRow() uses this Variable to store the current Position
	 */
	//protected int cachedRow = -3;
	
	/**
	  * Moves the cursor to the remembered cursor position, usually the current row.
	  * This is only necessary if the Cursor was moved to the InsertRow before.
	  * After Inserting you can move back to the current Row using this Method.
	  * @see moveToInsertRow()
	  * @see insertRow()
	  * @see java.sql.ResultSet#moveToCurrentRow()
	  */
	public abstract void moveToCurrentRow() throws SQLException;  /* {
		if (cachedRow > -1) {
			setPosition(cachedRow);
			cachedRow = -1;
		}
	}
	*/
	/**
	  * Moves the cursor to the insert row,
	  * usually at the End of the File.
	  * Remembers the current Row to be able to go back there.
	  * This is the Preparation for inserting new Rows.
	  *
	  * After Inserting you can move back to the current Row using...
	  * @see moveToCurrentRow()
	  * @see insertRow()
	  * @see java.sql.ResultSet#moveToInsertRow()
	  */
	public abstract void moveToInsertRow() throws SQLException; /* {
		if (readOnly)
			throw new SQLException(STR_READ_ONLY);
		if (this.cachedRow < 0) 
			this.cachedRow = getPointer();
		setPointer(this.insertPosition);
	}
	*/
	
	/**
	  * Refreshes the current row with its most recent value in the database.
	  * Either goes back to the Database
	  * or at least reverts to the original Values if these have been modified!
	  * To do this, a Copy is required.
	  *
	  * This method cannot be called when the cursor is on the insert row.
	  * The refreshRow method provides a way for an application
	  * to explicitly tell the JDBC driver to refetch a row(s) from the database.
	  * An application may want to call refreshRow when caching
	  * or prefetching is being done by the JDBC driver to fetch the latest value
	  * of a row from the database.
	  * The JDBC driver may actually refresh multiple rows at once
	  * if the fetch size is greater than one.
	  *
	  * All values are refetched subject to the transaction isolation level and cursor sensitivity.
	  * If refreshRow is called after calling an updateXXX method,
	  * but before calling the method updateRow, then the updates made to the row are lost.
	  * Calling the method refreshRow frequently will likely slow performance.
	  *
	  * @throws SQLException - if a database access error occurs
	  * 	or if this method is called when the cursor is on the insert row
	  * @since 1.2
	  * @see What Is in the JDBC 2.0 API
	  * @see java.sql.ResultSet#refreshRow()
	  */
	public abstract void refreshRow() throws SQLException; // { } //ignored, but not in the Subclasses! 
	
	///////////////////////////////////////////////////////////////////////////

	/** Flag for the current Row. */
	protected boolean isNull;

	/**
	  * Reports whether the last column read had a value of SQL NULL.
	  * Note that you must first call one of the getXXX methods on a column
	  * to try to read its value and then call the method wasNull
	  * to see if the value read was SQL NULL.
	  * This is important for primitive Values, because these are not changed
	  * when a null Value occurs, unlike the Object Types.
	  * For Object Types null is returned
	  * Empty Strings and Null Values are treated identical.
	  *
	  * @return true if the last column value read was SQL NULL and false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSet#wasNull()
	  */
	public boolean wasNull() { return (isNull); }

	////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////

	/** Number of Rows    */
	//protected int numRows = -1; 

	/**rarely used and not determined on Streams... 
	 * @return the Number of Rows or -1 if that Number is not vailable  */
	//public int getNumRows() { return numRows; }

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor, has to call init() to initialize the Object!  */
	protected AResultSet(final String _cursorName, final Statement _statement) {
		super(_cursorName, _statement);
	}

	/** Initializing Constructor
	 * @param numCols the (maximum) Number of Columns of this ResultSet
	 * @param FieldNames_ the Names of the Columns of this ResultSet, can be null
	 * @param FieldDefaults_ Default Values for new Rows of this ResultSet, can be null
	 */
	protected AResultSet(final String[] _fieldNames, final int _numCols, final String _cursorName, final Statement _statement) {
		super(_fieldNames, _numCols, _cursorName, _statement); 
	}

	/** Initializing Constructor
	 * @param numCols the (maximum) Number of Columns of this ResultSet
	 * @param FieldNames_ the Names of the Columns of this ResultSet, can be null
	 * @param FieldDefaults_ Default Values for new Rows of this ResultSet, can be null
	 */
	protected AResultSet(final String[] _fieldNames, final String _cursorName, final Statement _statement) {
		super(_fieldNames, _cursorName, _statement); 
	}

	/** Initializing Constructor
	 * @param _cols the Column Objects of this ResultSet
	 * @param _fieldNames the Names of the Columns of this ResultSet, can be null
	 */
	protected AResultSet(final DbColumn[] _cols, final String _cursorName, final Statement _statement) {
		super(_cols, _cursorName, _statement); 
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Interface ResultSet
	////////////////////////////////////////////////////////////////////////////////

	/** The current logical Position in the streamIO.
	  * Starts with 0 because currItem is set to BOF first
	  * This is the Row Number: 0 for BOF, 1 for the first, 2 for the second etc.  
	  * Fixed Size File Formats can calculate the physical FilePointer from here, 
	  * Variable Size File Formats can only read this Variable. 
	  * Since deleted Rows can be skipped, this Position is of limited Value anyway. 
	  */
	protected int position; // = -1; // = 0; //Integer.MIN_VALUE;
	
	/**
	  * Returns the current row number.
	  * Could also return the current Row Pointer / Offset,
	  * but that would defy the Interface.
	  * @see java.sql.ResultSet#getRow()
	  */
	public int getRow() { return position; }
	
	/**
	  * Moves the cursor to the given row number in this ResultSet object.
	  * @see java.sql.ResultSet#absolute(int)
	  */
	public boolean absolute(final int row) throws SQLException {
		return relative(row - position); }
	
	/**
	  * Indicates whether the cursor is after the last row in this ResultSet object.
	  * @see java.sql.ResultSet#isAfterLast()
	  */
	public abstract boolean isAfterLast() throws SQLException; // { return (position >= insertPosition); }
	
	/**
	  * Indicates whether the cursor is before the first row in this ResultSet object.
	  * @see java.sql.ResultSet#isBeforeFirst()
	  */
	public boolean isBeforeFirst() { return position <= 0; }

	/**
	  * Moves the cursor to the front of this ResultSet object, just before the first row.
	  * BOI
	  * @see java.sql.ResultSet#beforeFirst()
	  */
	public void beforeFirst() throws SQLException { absolute(0); fillDefaults(); } //
	
	/** fills the ResultSet with the Defaults defined. 
	 * used in beforeFirst or moveToInsertRow()
	 */
	protected void fillDefaults(){}
	
	/**
	  * Moves the cursor to the first row in this ResultSet object.
	  * @see java.sql.ResultSet#first()
	  */
	public boolean first() throws SQLException {
		beforeFirst(); //move forwards in case the first Records are deleted
		return next();
	}
	
	/**
	  * Moves the cursor to the end of this ResultSet object, just after the last row.
	  * EOI
	  * @see java.sql.ResultSet#afterLast()
	  */
	public void afterLast() throws SQLException { fillDefaults(); moveToInsertRow(); }
	
	/**
	  * Moves the cursor to the last row in this ResultSet object.
	  * @return true, when the Cursor is on a valid Row,
	  *  i.e. the ResultSet contains at least one Row.
	  * @see java.sql.ResultSet#last()
	  */
	public boolean last() throws SQLException {
		afterLast(); //move backwards in case the last Records are deleted
		return previous(); }
	
	/**
	  * Indicates whether the cursor is on a valid row in this ResultSet object.
	  * I.e. not before the first or after the last Row 
	  * and not on a logically deleted Row 
	  * @see java.sql.ResultSet#isBeforeFirst()
	  */
	public boolean inValid() throws SQLException {
		return isBeforeFirst() || isAfterLast() || rowDeleted(); }
	
	/**
	  * Indicates whether the cursor is on the last row of this ResultSet object.
	  * @see java.sql.ResultSet#isLast()
	  */
	public boolean isLast() throws SQLException {
		if (inValid()) 
			return false;
		boolean ret = next();
		previous();
		return ret;
	}
	
	/**
	  * Indicates whether the cursor is on the first row of this ResultSet object.
	  * @see java.sql.ResultSet#isFirst()
	  */
	public boolean isFirst() throws SQLException {
		if (inValid()) 
			return false;
		boolean ret = previous();
		next();
		return ret;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	  * Cancels the updates made to the current row in this ResultSet object.
	  * This method may be called after calling an updateXXX method(s)
	  * and before calling the method updateRow to roll back the updates made to a row.
	  * If no updates have been made or updateRow has already been called,
	  * this method has no effect.
	  * @throws SQLException - if a database access error occurs
	  * 		or if this method is called when the cursor is on the insert row
	  * @since 1.2
	  * @see What Is in the JDBC 2.0 API
	  * @see java.sql.ResultSet#cancelRowUpdates()
	  */
	public void cancelRowUpdates() throws SQLException { refreshRow(); } //

	/**
	  * Clears all warnings reported on this ResultSet object.
	  * @see java.sql.ResultSet#clearWarnings()
	  */
	public void clearWarnings() { } //ignored

	/**
	  * Returns the concurrency mode of this ResultSet object.
	  * @see java.sql.ResultSet#getConcurrency()
	  */
	public int getConcurrency() { return ResultSet.CONCUR_UPDATABLE; }

	/**
	  * Returns the fetch direction for this ResultSet object.
	  * @see java.sql.ResultSet#getFetchDirection()
	  */
	public int getFetchDirection() { return ResultSet.FETCH_FORWARD; } // FETCH_UNKNOWN;	}

	/**
	  * Gives a hint as to the direction
	  * in which the rows in this ResultSet object will be processed.
	  * @see java.sql.ResultSet#setFetchDirection(int)
	  */
	public void setFetchDirection(final int direction) throws SQLException {
		if (direction != ResultSet.FETCH_FORWARD)
			throw new SQLException("Forward Only ResultSet");
	}

	/**
	  * Always returns {@link ResultSet#TYPE_SCROLL_INSENSITIVE}.
	  * @see java.sql.ResultSet#getType()
	  */
	public int getType() { return ResultSet.TYPE_SCROLL_INSENSITIVE; }

	/**
	  * Returns the {@link Statement} that produced this result set.
	  * @see java.sql.ResultSet#getStatement()
	  */
	public Statement getStatement() { return statement; }

	/**
	  * Returns the first warning reported by calls on this ResultSet object.
	  * @see java.sql.ResultSet#getWarnings()
	  */
	public SQLWarning getWarnings() { return null; 
	}

	/**
	  * Maps the given ResultSet column name to its ResultSet column index.
	  * Uses a simple linear Search, because the Number of Attributes
	  * in (relational) Tables is typically less than a dozen.
	  * @see java.sql.ResultSet#findColumn(String)
	  */
	public int findColumn(String columnName) {
		if (capitalizing)
			columnName = columnName.toUpperCase().trim(); 
		for (int i = columns.length; --i >= 0; ) {
			if (columns[i].name.equals(columnName))
				return i; 
		}
		return -1; }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as an Object in the Java programming language.
	  * @see java.sql.ResultSet#getObject(int)
	  */
	public Object getObject(final int columnIndex) {
		return getString(columnIndex); }

	/**
	  * Updates the designated column with a String value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateString(String, String)
	  */
	public void checkReadOnly() throws SQLException {
		if (readOnly) 
			throw new SQLException("Read-Only ResultSet! Updates are not supported/allowed!"); 
	}

	/**
	  * Updates the designated column with a String value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateString(String, String)
	  */
	public void updateString(final String columnName, final String x) throws SQLException {
		//checkReadOnly(); //also done in the Routine called!  
		updateString(findColumn(columnName), x);
	}

	/**
	  * Updates the designated column with an Object value.
	  * @see java.sql.ResultSet#updateObject(int, Object)
	  */
	public void updateObject(final int columnIndex, final Object x) throws SQLException {
		if (x instanceof String) {
			updateString(columnIndex, (String) x);
		} else {
			updateString(columnIndex, x.toString());
		}
	}

	/**
	  * Updates the designated column with an Object value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateObject(String, Object)
	  */
	public void updateObject(final String columnName, final Object x) throws SQLException {
		if (x instanceof String) {
			updateString(columnName, (String) x);
		} else {
			updateString(columnName, x.toString());
		}
	}

	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX/isXXX/makeXXX)
	////////////////////////////////////////////////////////////////////////////

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object
	  * as an Array object in the Java programming language.
	  * @see java.sql.ResultSet#getArray(int)
	  */
	public Array getArray(final int i) { // throws SQLException {
		return null; }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a stream of ASCII characters.
	  * @see java.sql.ResultSet#getAsciiStream(int)
	  */
	public InputStream getAsciiStream(final int columnIndex) { // throws SQLException {
		return new ByteArrayInputStream(getString(columnIndex).getBytes()); }
	//return new StringBufferInputStream(getString(columnIndex)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a java.math.BigDecimal with full precision.
	  * @see java.sql.ResultSet#getBigDecimal(int)
	  */
	public BigDecimal getBigDecimal(final int columnIndex) {
		return new BigDecimal(getString(columnIndex)); }

	/**
	  * Gets the column value as a {@link BigDecimal} parsed from its string representation;
	  * {@code scale} is not applied.
	  * @see java.sql.ResultSet#getBigDecimal(int, int)
	  * @deprecated
	  */
	public BigDecimal getBigDecimal(final int columnIndex, final int scale) {
		return new BigDecimal(getString(columnIndex)); } //, scale); }

	/**
	  * Gets the value of the designated column in the current row
	  * of this ResultSet object as a binary stream of uninterpreted bytes.
	  * @see java.sql.ResultSet#getBinaryStream(int)
	  */
	public InputStream getBinaryStream(final int columnIndex) {
		return new ByteArrayInputStream(getString(columnIndex).getBytes()); }

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object
	  * as a Blob object in the Java programming language.
	  * @see java.sql.ResultSet#getBlob(int)
	  */
	public Blob getBlob(final int i) { return null; }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a boolean in the Java programming language.
	  * @see java.sql.ResultSet#getBoolean(int)
	  */
	public boolean getBoolean(final int columnIndex) {
		return Boolean.valueOf(getString(columnIndex)).booleanValue(); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a byte in the Java programming language.
	  * @throws NumberFormatException for invalid Characters or exceeding the Range
	  * @see java.sql.ResultSet#getByte(int)
	  */
	public byte getByte(final int columnIndex) throws SQLException {
		return (byte) getLong(columnIndex); } //Byte.parseByte(getString(columnIndex).trim()); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a byte array in the Java programming language.
	  * @see java.sql.ResultSet#getBytes(int)
	  */
	public byte[] getBytes(final int columnIndex) {
		return getString(columnIndex).getBytes(); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a java.io.Reader object.
	  * @see java.sql.ResultSet#getCharacterStream(int)
	  */
	public Reader getCharacterStream(final int columnIndex) {
		return new StringReader(getString(columnIndex));
	}

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object
	  * as a Clob object in the Java programming language.
	  * @see java.sql.ResultSet#getClob(int)
	  */
	public Clob getClob(final int i) { return null; }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a java.sql.Date object in the Java programming language.
	  * @see java.sql.ResultSet#getDate(int)
	  */
	public Date getDate(final int columnIndex) { 
		return Date.valueOf(getString(columnIndex)); }

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object
	  * as a java.sql.Date object in the Java programming language.
	  * @see java.sql.ResultSet#getDate(int, Calendar)
	  */
	public Date getDate(final int columnIndex, final Calendar cal) { 
		return Date.valueOf(getString(columnIndex)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a double in the Java programming language.
	  * @see java.sql.ResultSet#getDouble(int)
	  */
	public double getDouble(final int columnIndex) throws SQLException { 
		final String value = getString(columnIndex).trim(); 
		if (tolerantNumberParsing)
			return ByRefDouble.TRY_PARSE(value); //faster and Error tolerant 
		try {
			return Double.parseDouble(value);
		} catch (final NumberFormatException x) {
			if (tolerantNumberParsing)
				return Double.NaN; 
			throw new SQLException("Column #"+columnIndex+"='"+value+"' is no Number!"+x.toString()); 
		}
	}

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a float in the Java programming language.
	  * @see java.sql.ResultSet#getFloat(int)
	  */
	public float getFloat(final int columnIndex) throws SQLException {
		return (float) getDouble(columnIndex); } // Float.parseFloat(getString(columnIndex)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as an int in the Java programming language.
	  * @throws NumberFormatException for invalid Characters or exceeding the Range
	  * @see java.sql.ResultSet#getInt(int)
	  */
	public int getInt(final int columnIndex) throws SQLException {
		return (int) getLong(columnIndex); } // Integer.parseInt(getString(columnIndex).trim()); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as a long in the Java programming language.
	  * @throws NumberFormatException for invalid Characters or exceeding the Range
	  * @see java.sql.ResultSet#getLong(int)
	  */
	public long getLong(final int columnIndex) throws SQLException {
		final String value = getString(columnIndex).trim(); 
		if (tolerantNumberParsing)
			return ByRefLong.TRY_PARSE(value); //faster and Error tolerant 
		try {
			return Long.parseLong(value);
		} catch (final NumberFormatException x) {
			if (tolerantNumberParsing)
				return Long.MIN_VALUE; 
			throw new SQLException("Column #"+columnIndex+"='"+value+"' is no Number!"+x.toString()); 
		}
	}

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object
	  * as an Object in the Java programming language.
	  * This method uses the given Map object for the custom mapping of the SQL structured or distinct type that is being retrieved.
	  * @param i - the first column is 1, the second is 2, ...
	  * @param map - a java.util.Map object that contains the mapping from SQL type names to classes in the Java programming language
	  * @return an Object in the Java programming language representing the SQL value
	  * @since 1.2
	  * @see What Is in the JDBC 2.0 API
	  * @see java.sql.ResultSet#getObject(int, Map)
	  */
	public Object getObject(final int i, final Map map) {
		return null;
	}

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object
	  * as a Ref object in the Java programming language.
	  * @see java.sql.ResultSet#getRef(int)
	  */
	public Ref getRef(final int columnIndex) {
		return null;
	}

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object as a short in the Java programming language.
	  * @throws NumberFormatException for invalid Characters or exceeding the Range
	  * @see java.sql.ResultSet#getShort(int)
	  */
	public short getShort(final int columnIndex) throws SQLException {
		return (short) getLong(columnIndex); } // Short.parseShort(getString(columnIndex).trim()); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object as a java.sql.Time object in the Java programming language.
	  * @see java.sql.ResultSet#getTime(int)
	  */
	public Time getTime(final int columnIndex) {
		return Time.valueOf(getString(columnIndex)); }

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object as a java.sql.Time object in the Java programming language.
	  * @see java.sql.ResultSet#getTime(int, Calendar)
	  */
	public Time getTime(final int columnIndex, final Calendar cal) {
		return Time.valueOf(getString(columnIndex)); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object as a java.sql.Timestamp object in the Java programming language.
	  * @see java.sql.ResultSet#getTimestamp(int)
	  */
	public Timestamp getTimestamp(final int columnIndex) {
		return Timestamp.valueOf(getString(columnIndex)); }

	/**
	  * Returns the value of the designated column in the current row of this ResultSet object as a java.sql.Timestamp object in the Java programming language.
	  * @see java.sql.ResultSet#getTimestamp(int, Calendar)
	  */
	public Timestamp getTimestamp(final int columnIndex, final Calendar cal) {
		return Timestamp.valueOf(getString(columnIndex)); }

	/**
	  * Deprecated. Use getCharacterStream in place of getUnicodeStream
	  * @see java.sql.ResultSet#getUnicodeStream(int)
	  * @deprecated
	  */
	public InputStream getUnicodeStream(final int columnIndex) {
		return null;
	}

	/**
	  * Deprecated. Use getCharacterStream in place of getUnicodeStream
	  * @see java.sql.ResultSet#getURL(int)
	  */
	public java.net.URL getURL(final int columnIndex) {
		return null;
	}

	/** Exception Message for not supported Operations */
	final static public String STR_NOT_SUPPORTED =
		"Operation not supported by this Driver!";

	/**
	  * Updates the designated column with an Array value.
	  * @since JDK1.4
	  * @see java.sql.ResultSet#updateArray(int, Array)
	  */
	public void updateArray(final int columnIndex, final Array arr) throws SQLException {
		throw new SQLException(STR_NOT_SUPPORTED);
	}

	/**
	  * Updates the designated column with an ascii stream value.
	  * @see java.sql.ResultSet#updateAsciiStream(int, InputStream, int)
	  */
	public void updateAsciiStream(final int columnIndex, final InputStream x, final int length)
		throws SQLException {
		throw new SQLException(STR_NOT_SUPPORTED);
	}

	/**
	  * Updates the designated column with a java.math.BigDecimal value.
	  * @see java.sql.ResultSet#updateBigDecimal(int, BigDecimal)
	  */
	public void updateBigDecimal(final int columnIndex, final BigDecimal x)
		throws SQLException {
		updateString(columnIndex, x.toString());
	}

	/**
	  * Updates the designated column with a binary stream value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateBinaryStream(int, InputStream, int)
	  */
	public void updateBinaryStream(final int columnIndex, final InputStream x, final int length)
		throws SQLException {
		throw new SQLException(STR_NOT_SUPPORTED);
	}

	/**
	  * Updates the designated column with an Array value.
	  * @since JDK1.4
	  * @see java.sql.ResultSet#updateBlob(int, Blob)
	  */
	public void updateBlob(final int columnIndex, final Blob arr) throws SQLException {
		throw new SQLException(STR_NOT_SUPPORTED);
	}

	/**
	  * Updates the designated column with a boolean value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateBoolean(int, boolean)
	  */
	public void updateBoolean(final int columnIndex, final boolean x) throws SQLException {
		updateString(columnIndex, "" + x);
	}

	/**
	  * Updates the designated column with a byte value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateByte(int, byte)
	  */
	public void updateByte(final int columnIndex, final byte x) throws SQLException {
		updateString(columnIndex, Byte.toString(x));
	}

	/**
	  * Updates the designated column with a byte array value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateBytes(int, byte[])
	  */
	public void updateBytes(final int columnIndex, final byte[] x) throws SQLException {
		updateString(
			columnIndex,
			VectorString.toString(x, new StringBuffer()).toString());
	}

	/**
	  * Updates the designated column with a character stream value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateCharacterStream(int, Reader, int)
	  */
	public void updateCharacterStream(final int columnIndex, final Reader x, final int length)
		throws SQLException {
		throw new SQLException(STR_NOT_SUPPORTED);
	}

	/**
	  * Updates the designated column with an Array value.
	  * @since JDK1.4
	  * @see java.sql.ResultSet#updateClob(int, Clob)
	  */
	public void updateClob(final int columnIndex, final Clob arr) throws SQLException {
		throw new SQLException(STR_NOT_SUPPORTED);
	}

	/**
	  * Updates the designated column with a java.sql.Date value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateDate(int, Date)
	  */
	public void updateDate(final int columnIndex, final Date x) throws SQLException {
		updateString(columnIndex, x.toString());
	}

	/**
	  * Updates the designated column with a double value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateDouble(int, double)
	  */
	public void updateDouble(final int columnIndex, final double x) throws SQLException {
		updateString(columnIndex, Double.toString(x));
	}

	/**
	  * Updates the designated column with a float value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateFloat(int, float)
	  */
	public void updateFloat(final int columnIndex, final float x) throws SQLException {
		updateString(columnIndex, Float.toString(x));
	}

	/**
	  * Updates the designated column with an int value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateInt(int, int)
	  */
	public void updateInt(final int columnIndex, final int x) throws SQLException {
		updateString(columnIndex, Integer.toString(x));
	}

	/**
	  * Updates the designated column with a long value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateLong(int, long)
	  */
	public void updateLong(final int columnIndex, final long x) throws SQLException {
		updateString(columnIndex, Long.toString(x));
	}

	/**
	  * Gives a nullable column a null value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateNull(int)
	  */
	public void updateNull(final int columnIndex) throws SQLException {
		updateString(columnIndex, "");
	}

	/**
	  * Updates the designated column with an Object value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateObject(int, Object, int)
	  */
	public void updateObject(final int columnIndex, final Object x, final int scale)
		throws SQLException {
		updateString(columnIndex, x.toString());
	}

	/**
	  * Updates the designated column with an Array value.
	  * @since JDK1.4
	  * @see java.sql.ResultSet#updateRef(int, Ref)
	  */
	public void updateRef(final int columnIndex, final Ref ref) throws SQLException {
		throw new SQLException(STR_NOT_SUPPORTED);
	}

	/**
	  * Updates the designated column with a short value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateShort(int, short)
	  */
	public void updateShort(final int columnIndex, final short x) throws SQLException {
		updateString(columnIndex, Short.toString(x));
	}

	/**
	  * Updates the designated column with a java.sql.Time value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateTime(int, Time)
	  */
	public void updateTime(final int columnIndex, final Time x) throws SQLException {
		updateString(columnIndex, x.toString());
	}

	/**
	  * Updates the designated column with a java.sql.Timestamp value.
	  * @throws SQLException when the ResultSet is read only
	  * @see java.sql.ResultSet#updateTimestamp(int, Timestamp)
	  */
	public void updateTimestamp(final int columnIndex, final Timestamp x)
		throws SQLException {
		updateString(columnIndex, x.toString());
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	  * Indicates whether a row has been deleted.
	  * A deleted row may leave a visible "hole" in a result set.
	  * This method can be used to detect holes in a result set.
	  * The value returned depends on whether or not this ResultSet object can detect deletions.
	  * @return true if a row was deleted and deletions are detected; false otherwise
	  * @throws SQLException - if a database access error occurs
	  *
	  * Every Row has as the first Column an Indicator that displays whether the row is
	  * -inserted
	  * -deleted
	  * -ready for archive
	  * -etc.
	  * @see java.sql.ResultSet#rowDeleted()
	  */
	public boolean rowDeleted() {
		if (readOnly)
			return false;
		return operationFlag == CHR_OP_DELETED;
	}

	/**
	  * Indicates whether the current row has had an insertion.
	  * The value returned depends on whether or not this ResultSet object can detect visible inserts.
	  * @return true if a row was inserted and insertions are detected; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSet#rowInserted()
	  */
	public boolean rowInserted() {
		if (readOnly)
			return false;
		return operationFlag == CHR_OP_INSERTED;
	}

	/**
	  * Indicates whether the current row has been updated.
	  * The value returned depends on whether or not the result set can detect updates.
	  * @return true if a row was updated and updates are detected; false otherwise
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSet#rowUpdated()
	  */
	public boolean rowUpdated() {
		if (readOnly)
			return false;
		return operationFlag == CHR_OP_UPDATED;
	}
	//		return changed; }

	/**
	  * Deletes the current row from this ResultSet object
	  * and from the underlying database.
	  * The first Column of the File contains a Marker D,I or U
	  * @see java.sql.ResultSet#deleteRow()
	  */
	public void deleteRow() throws SQLException {
		if (readOnly || !operationSupported) 
			throw new SQLException(STR_READ_ONLY);
		operationFlag = CHR_OP_DELETED;
		updateRow();
	}

	/**
	  * Moves the cursor down one row from its current position.
	  * A ResultSet cursor is initially positioned before the first row;
	  * the first call to the method next makes the first row the current row;
	  * the second call makes the second row the current row, and so on.
	  *
	  * If an input stream is open for the current row,
	  * a call to the method next will implicitly close it.
	  * A ResultSet object's warning chain is cleared when a new row is read.
	  *
	  * @return true if the new current row is valid;
	  *  false if there are no more rows
	  * @throws SQLException - if a database access error occurs
	  * @see java.sql.ResultSet#next()
	  */
	public boolean next() throws SQLException {
		if ((operationFlag != CHR_OP_DELETED) && (operationFlag != CHR_OP_NEUTRAL)) 
			updateRow(); //the quick Check here potentially saves the call Overhead!
		do { //
			if (!readNext())  //did reading fail?
				return false;
		} while ((!readDeleted) && (operationFlag == CHR_OP_DELETED));
		return true;
	}

	/**
	  * Moves the cursor to the previous row in this ResultSet object.
	  * This is not supported in separated ResultSets
	  * This could be amended by memorizing the Positions in the File
	  * into an open ended Stack using e.g. Integer Objects,
	  * but instead you could as well read everything into a Collection.
	  * @see java.sql.ResultSet#previous()
	  */
	public boolean previous() throws SQLException {
		throw new SQLException(
			"Class '" + getClass().getName() + "' cannot go backwards! ");
	} //

	/**
	 * fills a List of boolean Flags to indicate
	 * writable Columns 
	 * non-null Columns or other MetaData
	 */
	// TODO: LOGIC: loop condition is "--i > 0", so index 0 is never visited -
	// flags[0] keeps its default (false) regardless of the actual first column's flag.
	// Every other reverse loop in this codebase uses "--i >= 0"; this one is inconsistent
	// and silently under-fills the first element of the caller-supplied array.
	public void fillFlags(final boolean[] flags) { //final byte opFlag) {
		for (int i = flags.length; --i > 0;)
			flags[i] = (CHR_BOOLEAN_TRUE == getFirstChar(i));
	} //

	/**
	 * Determines whether the current row holds data as opposed to metadata or a comment.
	 * @return true if the current Row has Data, false for MetaData or Comments
	 * @throws SQLException when an invalid Operation Character is encountered.
	 */
	public boolean isDataRow() { //final byte opFlag) {
		//throws SQLException {
		if (!operationSupported)  
			return true; 
		byte opFlag =  (byte) getFirstChar(0);
		if  (opFlag == (byte) Separator.charAt(0)) //replace because...  
			 opFlag = CHR_FIELD_COMMENT; // DEFAULTS; //...'case' must be constant!
		operationFlag = opFlag; 
		//with the Assumption that the Metadata is only at the Beginning of the File, 
		//this sqitch Statement can be placed here instead of 'previous()' and 'next()'
		switch (operationFlag) {
			//case CHR_:	break;
		
			//MetaData
			//case CHR_SEPARATION : break; //should only appear in the first Row
			case CHR_FIELD_COMMENT : return false; //List of Column Comments, ignored
			case CHR_FIELD_DEFAULTS : //List of Column Defaults
				final String[] fieldDefaults = getAllFields();
				for (int i = fieldDefaults.length; --i >= 0; )
					columns[i].defaultValue = fieldDefaults[i]; 
				return false;
			case CHR_FIELD_NAMES : //List of Column Names
				final String[] fieldNames = getAllFields(); //
				if (capitalizing) {
					VectorString.TRIM_CAPITALIZE_AT(fieldNames, false, true); }
				if (columns == null) //
					columns = new DbColumn[fieldNames.length]; 
				for (int i = fieldNames.length; --i >= 0; ) //overwrite possibly existing Field Names
					columns[i] = new DbColumn(this, fieldNames[i], i); 
				return false;
			case CHR_FIELD_LABELS : //List of Column Names
				final String[] fieldLabels = getAllFields(); //
				for (int i = fieldLabels.length; --i >= 0; )
					columns[i].alias = fieldLabels[i]; 
				return false;
			case CHR_FIELD_NULLS    : 
				//fillFlags(metaData.fieldIsNullable);
				for (int i = columns.length; --i > 0;)
					columns[i].isNullable = (CHR_BOOLEAN_TRUE == getFirstChar(i)); 
				return false;
			case CHR_FIELD_WRITABLE : 
				//fillFlags(metaData.fieldIsWritable); 
				for (int i = columns.length; --i > 0;)
					columns[i].isWritable = (CHR_BOOLEAN_TRUE == getFirstChar(i)); 
				return false;
			case CHR_FIELD_TYPES :
				for (int i = columns.length; --i > 0;) 
					columns[i].colClass = GET_CLASS_FROM_TYPE(getFirstChar(i));
				return false;
				
			//Data Row Starters
			case CHR_OP_CONTINUED : //fill the Buffers and read the next Row
				return false;
			case CHR_OP_UPDATED : //fall-through is intended!
			case CHR_OP_DELETED : //encountered a logically deleted Row. 
				return readDeleted; //skip updated or deleted Rows
			case CHR_OP_INSERTED: operationFlag = CHR_OP_NEUTRAL; 
			case CHR_OP_NEUTRAL : //fall through is intended!
				return true;	
			default : //compressing invalidates the rowOffsets Field! 
				throw new ArrayIndexOutOfBoundsException("Unexpected Operation Flag:'"+operationFlag+"'");
				//return true;
		}
	}
	
	/**
	 * Optimization to save constructing a String in ResultSetFix
	 * @return the first Character of the given Field
	 */
	protected byte getFirstChar(final int fieldNo) {
		final String str = getString(fieldNo); //is trimmed!!!
		if ((str == null) || (str.length() == 0)) 
			return 0;
		return (byte) str.charAt(0); 
	}
	
	/**
	  * Gets the Default Value of the designated column 
	  * as an untrimmed String in the Java programming language.
	  */
	public String getDefault(final int columnIndex) {
		return columns[columnIndex].defaultValue; }
	
	/**
	  * Gets the Default Values of the ResultSet 
	  * as an untrimmed String in the Java programming language.
	  */
	//public String[] getDefaults() { return fieldDefaults; }
	
	/**
	 * Returns all fields of the current row in a newly allocated array.
	 * @return a new Array containing all Fields of the current ResultSet Row
	 */
	public String[] getAllFields() {
		return getAllFields(null, 0); 
	}
	
	/**
	 * copies all current Fields into the given Array
	 * @param target the Array to copy into
	 * @return an Array containing all Fields of the current ResultSet Row up to the second 
	 */
	public String[] getAllFields(final String[] target) {
		return getAllFields(target, 1); 
	}
	
	/**
	 * copies all current Fields into the given Array
	 * @param target the Array to copy into
	 * @return an Array containing all Fields of the current ResultSet Row down to the given Indes
	 */
	public String[] getAllFields(String[] target, final int minIndex) {
		int i = getNumFields(); 
		if (target == null) 
			target =  new String[i];
		for (; --i >= minIndex;) 
			target[i] = getString(i); //creates a String from the Buffer
		return target; 
	}

	/**
	 * Returns the number of columns backing this result set.
	 * @return the Number of currently available Fields
	 */
	public int getNumFields() { return columns.length; }

	////////////////////////////////////////////////////////////////////////////////
	//	abstract Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	  * Gets the value of the designated column 
	  * in the current row of this ResultSet object 
	  * as an untrimmed String in the Java programming language.
	  * @see java.sql.ResultSet#getString(int)
	  */
	public abstract String getString(final int columnIndex);
	
	/**
	  * Moves the cursor a relative number of rows, either positive or negative.
	  * @see java.sql.ResultSet#relative(int)
	  */
	public abstract boolean relative(final int rows) throws SQLException;
	
	/**
	  * Inserts the contents of the insert row into this ResultSet object and into the database.
	  * @see java.sql.ResultSet#insertRow()
	  */
	public abstract void insertRow() throws SQLException; // { }
	
	/**
	  * Updates the underlying database with the new contents of the current row of this ResultSet object.
	  * @see java.sql.ResultSet#updateRow()
	  */
	public abstract void updateRow() throws SQLException; // { }
	
	/**
	  * Moves the cursor down one row from its current position.
	  * A ResultSet cursor is initially positioned BEFORE the first row;
	  * the first call to the method 'next()' makes the first row the current row;
	  * the second call makes the second row the current row, and so on.
	  *
	  * If an input stream is open for the current row,
	  * a call to the method 'next()' will implicitly close it.
	  * A ResultSet object's warning chain is cleared when a new row is read.
	  * 
	  * @return true if the new current row is valid; false if there are no more rows 
	  * and you are implicitly on the insert Row. 
	  * @throws SQLException - if a database access error occurs
	  */
	protected abstract boolean readNext() throws SQLException; // { }

	/**
	  * Updates the designated column with a String value.
	  * @see java.sql.ResultSet#updateString(int, String)
	  */
	public abstract void updateString(final int columnIndex, final String x) throws SQLException; // { }

	/**
	  * Releases this ResultSet object's database and JDBC resources immediately
	  * instead of waiting for this to happen when it is automatically closed.
	  * @see java.sql.ResultSet#close()
	  */
	public abstract void close() throws SQLException; // { } //ignored
	
}
