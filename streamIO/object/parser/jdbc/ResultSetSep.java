package streamIO.object.parser.jdbc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import math.vector.VectorLong;
import math.vector.VectorString;
import streamIO.Log;
import streamIO.integer.AStreamOutByte;
import streamIO.integer.IStreamByteRandom;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.file.FileStreamByte;
import streamIO.integer.file.FileStreamIn_Byte;
import streamIO.integer.jdbc.AResultSet;
import streamIO.integer.jdbc.AResultSetStream;
import streamIO.integer.jdbc.DbColumn;
import streamIO.object.parser.InputStream2StreamIn;
import tools.IOError;

/**
  * Implements the Interface java.sql.ResultSet
  * for a file of fixed Length 2D Data.
  * 
  * Extends Class AResultSet to reuse the Default Behaviors. 
  * Implements the Standard JDBC Interface java.sql.ResultSet
  * Uses Class InputStream2StreamIn to perform Parsing.
  *
  * Needs only an ISTreamIn_Byte to be able to work as a read only, forward only ResultSet! 
  * Needs an IStreamOutByte to be able to insert or update Data by appending it. 
  * On Deletion and Updating the deleted or updated Row is marked, 
  * so it is skipped on Reading and Truncation can be done in one Sweep. �
  * Files without an Operation Flag can be updated or deleted
  * by overwriting the Contents of the overwritten or deleted Row with empty Strings, 
  * except for the last (hopefully non-key) Column 
  * which contains only Spaces that are also truncated to an empty String. 
  * This corresponds to the Fact that Update = Delete & Insert 
  * 
  * A ResultSet is thus used both as a Log and a Database! 
  * With only logical Deletes and Updates the Timestamp (or Position in the File) 
  * determines whether a Row is valid. 
  * Truncation for deleted or updated Rows can happen as a Batch Operation! 
  * 
  * Apart from handing over all Parameters in the Constructor,  
  * the *.sep File Format is self-describing enough to determine:
  * The Column Separator(s), which is the very first Character
  * The Escape Character, which is the next Character 
  * The Row Separator which is the Charactor before the next Escape Character
  * A Set of MetaData Pairs also in separated Format:
  * Boolean FixedFormat
  * Boolean DeleteFlag
  * Boolean FieldNames
  * String  Comment
  * 
  * Conventions are: 
  * Row Separator is CR, LF or both 
  * Column Separator can be given or defaults to TAB 
  * Field Names are given in the first Row
  * 
  * The following Rows can either contain Data or Metadata, 
  * depending on the Contents of the first Column: 
  * 
  * When there is no first Column for MetaData, 
  * Data can be updated up to the current Length 
  * and Deletion can be simulated by clearing all Fields. (but this also clears Identity!) 
  *
  * first Column for MetaData Rows: 
  * 'F' Field Name  (Overrides, esp. for brief Fields e.g. 1 Character)
  * 'L' Field Label or Alias (for Display or Print Output)
  * 'T' Type Information, one of 'I'(Integer), 'F'(Float), 'S'(String), 'D'(Date), 'T'(Time), 'P'(TimeStamP) or 'B'(Boolean)
  * 'N' Nullability of a Field 
  * 'W' Writability of a Field
  * 'C' Comment Row (skipped)
  * 
  * First column for Data Rows: 
  * ' '  changed Row (indicates actuality; there should be only one Row with this Key) 
  * '+' inserted Row (not really necessary)
  * '-'  deleted Row (can be read or skipped or inserted into by the ResultSet)
  * '~'  updated Row (is overwritten by a later Row marked by ' ')
  * '&' Row Continuation is not necessary for separated DataSets, 
  * 	since the Fields can become arbitrarily long
  * 
  * Known SubClasses: <none>
  * 
  * @see streamIO.integer.jdbc.AResultSet
  * @see streamIO.integer.jdbc.ResultSetFix
  * @see streamIO.integer.jdbc.ResultSetFix2
  * 
  * TODO: Support Files without Operation Indicator (+,-,U,D) as read (or insert-) only DataSet!
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	1999-12-31, 12;38;24<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * tags: [code/jdbc_adapter, code/sax_event_generation]
  * concepts: [Minimal JDBC Driver over Separated-Format Flat Files]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public class ResultSetSep
extends AResultSetStream {
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Array of empty Default Value to avoid reading the Defaults from the File   */
	protected static final String[] EMPTY_DEFAULTS = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
	
	/** Default Value for Double Parameters     */
	public static double ParameterDefaultDouble = Double.MIN_VALUE;
	
	/** Default Value for Long Parameters     */
	public static long ParameterDefaultLong = Long.MIN_VALUE;
	
	/** Default Value for boolean Parameters     */
	public static boolean ParameterDefaultBool = false;
	
	/** Default Value for the Capitalization of the Parameter Keys     */
	//final static public boolean capitalizeDefault = true;
	
	/** Estimates an upper Bound on remaining Rows, assuming every Field stays empty.
	  * @return the Maximum Number of Rows left from the current Position.
	 * This is only reached with only empty Fields!
	 * Divide this Number by the average Number of Characters in each Field	 */
	public long getMaxNumRowsLeft() throws IOException { //Separators without Escape Character
		return (rndFile.length() - rndFile.getFilePointer())/(Separator.length()-1); }

	/** Estimates the remaining Rows using the average observed Row Size so far.
	  * @return the Maximum Number of Rows left from the current Position.
	 * This is only reached with only empty Fields!
	 * Divide this Number by the average Number of Characters in each Field	 */
	public long getAvgNumRowsLeft() throws IOException { //Separators without Escape Character
		return (rndFile.length() - rndFile.getFilePointer())/getAvgRowSize(); }

	/** Computes the average Row Size seen so far, from the File Position and current Row Count.
	  * @return the Average Number of Rows left from the current Position
	 * estimated by the average Length of the previous Rows.	 */
	public long getAvgRowSize() throws IOException {
		return (rndFile.getFilePointer() - rowOffsets.getLongAt(firstPos))/position; }
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Parser Object	  */
	protected final InputStream2StreamIn scanner;
	
	/** Type-safe Field String Buffer	defaulted to a maximum Size speeds up Access,
	  * but would make it necessary to add Code to manage the Array Size... 
	  */
	protected final VectorString fields = new VectorString();
	
	/** The Position of the first Line of real Data,
	  * dependent on the Flag ColNamesInFirstRow.
	  * Needed to reset the Data Set to the Start. 
	  * @see #rowOffsets Now tracks ALL Positions. 
	  * Was then used to indicate the Position Offset of the first Row of real Data, 
	  * but using this Offset to correct the position() Function was too tedious, 
	  * and since the Data above is not maintained anyway...
	  * @see AResultSet#insertPosition
	  */
	protected static final int firstPos = 1;
	//protected final long firstLine;
	
	/** used to maintain a mapping between Row Numbers and File Offsets, 
	 * to be able to jump to a Position directly 
	 */
	protected final VectorLong rowOffsets = new VectorLong(); 
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializing Constructor
	 * reading the Separators from the File itself
	 */
	public ResultSetSep(final String _filePath, final Statement statement) throws FileNotFoundException, IOException {
		this(new File(_filePath), null, null, false, statement); }
	
	/**
	 * Initializing Constructor
	 * reading the Separators from the File itself
	 */
	public ResultSetSep(final String _filePath, final boolean useCRLF, final Statement statement) throws FileNotFoundException, IOException {
		this(new File(_filePath), null, null, useCRLF, statement); }
	
	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted or updated Rows.
	  */
	public ResultSetSep(final String _filePath, final char fieldSep_, final Statement statement) throws FileNotFoundException, IOException {
		this(new File(_filePath), new String(new char[]{ fieldSep_, '\n'}), null, true, statement); }
	
	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted or updated Rows.
	  */
	public ResultSetSep(final String _filePath, final String _separators, final Statement statement) throws FileNotFoundException, IOException {
		this(new File(_filePath), _separators, null, true, statement); }
	
	/**
	 * Initializing Constructor
	 * reading the Separators from the File itself
	 */
	public ResultSetSep(final File _file, final Statement statement) throws FileNotFoundException, IOException {
		this(_file, null, null, false, statement); }
	
	/**
	 * Initializing Constructor
	 * reading the Separators from the File itself
	 */
	public ResultSetSep(final File _file, final boolean useCRLF, final Statement statement) throws FileNotFoundException, IOException {
		this(_file, null, null, useCRLF, statement); }
	
	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted or updated Rows.
	  */
	public ResultSetSep(final File _file, final String _separators, 
		final Statement statement) throws FileNotFoundException, IOException {
		this(_file, _separators, null, true, statement); }

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted or updated Rows.
	  */
	public ResultSetSep(final File _file, final String _separators, 
		final Statement statement, final String _tableName) throws FileNotFoundException, IOException {
		this(_file, _separators, null, true, statement, _tableName); }

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted or updated Rows.
	  */
	public ResultSetSep(final File _file, final String _separators, 
	final boolean firstRowNames, final Statement statement) throws FileNotFoundException, IOException {
		this(_file, _separators, firstRowNames ? null : EMPTY_DEFAULTS, true, statement); }

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted or updated Rows.
	  */
	public ResultSetSep(final String _filePath, final String _separators, 
		final String[] _fieldNames, final Statement statement) throws FileNotFoundException, IOException {
		this(new File(_filePath), _separators, _fieldNames, true, statement); }

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted or updated Rows.
	  */
	public ResultSetSep(final File file_, final String _separators, 
	final String[] _fieldNames, final Statement statement) throws FileNotFoundException, IOException {
		this(file_, _separators, _fieldNames, true, statement); }

	/** Initializing Constructor
	  * To avoid forgetting to call the super.init() Method,
	  * one ore more static Factory Methods could be used.
	  * @param FieldNames List of Column Names. If null, the first Row is used!
	  * @param Separators String of which the first two Characters are used as Separators.
	  */
	protected ResultSetSep(final File file_, final String _separators, 
		final String[] _fieldNames, final boolean useCRLF, final Statement _statement) throws FileNotFoundException, IOException {
		this(new FileStreamByte(file_, "rw"), _separators, _fieldNames, useCRLF, _statement, file_.getName());
		fileObj = file_; //alternatively you could use Factory Methods to find out the Number of Columns before the Constructor.
	}

	/**
	 * Initializing Constructor
	 * The Row Separator is defaulted to CRLF
	 * The Column Separator is read from the File itself
	 */
	public ResultSetSep(final String _filePath) throws FileNotFoundException, IOException {
		this(new File(_filePath), null, null, true, null); }
		
	/**
	 * Initializing Constructor
	 * reading the Separators from the File itself
	 */
	public ResultSetSep(final String _filePath, final boolean useCRLF) throws FileNotFoundException, IOException {
		this(new File(_filePath), null, null, useCRLF, null); }
		
	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted or updated Rows.
	  */
	public ResultSetSep(final String _filePath, final char fieldSep_) throws FileNotFoundException, IOException {
		this(new File(_filePath), new String(new char[]{fieldSep_, '\n'}), null, true, null); }
		
	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted or updated Rows.
	  */
	public ResultSetSep(final String _filePath, final String _separators) throws FileNotFoundException, IOException {
		this(new File(_filePath), _separators, null, true, null); }
		
	/**
	 * Initializing Constructor
	 * reading the Separators from the File itself
	 */
	public ResultSetSep(final File file_) throws FileNotFoundException, IOException {
		this(file_, null, null, false, null); }
		
	/**
	 * Initializing Constructor
	 * reading the Separators from the File itself
	 */
	public ResultSetSep(final File file_, final boolean useCRLF) throws FileNotFoundException, IOException {
		this(file_, null, null, useCRLF, null); }
		
	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted or updated Rows.
	  */
	public ResultSetSep(final File file_, final String _separators 
		) throws FileNotFoundException, IOException {
		this(file_, _separators, null, true, null); }
	
	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted or updated Rows.
	  */
	public ResultSetSep(final File file_, final String _separators, 
	final boolean firstRowNames) throws FileNotFoundException, IOException {
		this(file_, _separators, null //firstRowNames ? null : EMPTY_DEFAULTS
				, true, null); }
	
	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted or updated Rows.
	  */
	public ResultSetSep(final String _filePath, final String _separators, 
		final String[] _fieldNames) throws FileNotFoundException, IOException {
		this(new File(_filePath), _separators, _fieldNames, true, null); }
	
	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted or updated Rows.
	  */
	public ResultSetSep(final File file_, final String _separators, 
	final String[] _fieldNames) throws FileNotFoundException, IOException {
		this(file_, _separators, _fieldNames, true, null); }
	
	/** Initializing Constructor
	  * To avoid forgetting to call the super.init() Method,
	  * one ore more static Factory Methods could be used.
	  * @param FieldNames List of Column Names. If null, the first Row is used!
	  * @param Separators String of which the first two Characters are used as Separators.
	  */
	protected ResultSetSep(final File _file, final String _separators, 
		final String[] _fieldNames, final boolean useCRLF) throws FileNotFoundException, IOException {
		this(new FileStreamByte(_file, "rw"), _separators, _fieldNames, useCRLF, null, _file.getName());
		this.fileObj = _file; //alternatively you could use Factory Methods to find out the Number of Columns before the Constructor.
	}
	
	/** Initializing Constructor
	 * 
	 * @param _file the File to open
	 * @param _separators the Separators to use, can be null
	 * @param _fieldNames the Field Names, can be null
	 * @param _useCRLF flag whether to use CR/LF as Row Separator
	 * @param _statement the Statement creating this ResultSet, can be null
	 * @param _cursorName the Cursor or Table Name, can be null or ""
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private ResultSetSep(final File _file, final String _separators, final String[] _fieldNames, 
		final boolean _useCRLF, final Statement _statement, final String _cursorName) throws FileNotFoundException, IOException {
		this(new FileStreamByte(_file, "rw"), _separators, _fieldNames, _useCRLF, _statement, _cursorName); 
	}
	
	/** Initializing Constructor
	  * To avoid forgetting to call the super.init() Method,
	  * one ore more static Factory Methods could be used.
	  *
	  * By using a pure IStreamIn_Byte additional Filters,
	  * also irreversible can be plugged into the streamIO!
	  * @param FieldNames List of Column Names. If null, the first Row is used!
	  * @param Separators String of which the first two Characters are used as Separators.
	  * If null, the Separators are derived from the first two Characters in the streamIO
	  * @param useCRLF switch to CRLF as a second Separator instead of reading it from the File. 
	  */
	private ResultSetSep(final IStreamIn_Byte _file, String _separators, String[] _fieldNames, 
		final boolean useCRLF, final Statement _statement, final String _cursorName) throws FileNotFoundException, IOException {
		super(_file, _cursorName, _statement); //initialize FieldNames and FieldDefaults later (see below...)
		//file.seek(0); //not necessary
		//set up the Parser: //=> \n\r\t //all Separators > 0 are treated the same!
		scanner = new InputStream2StreamIn(file, _separators); //read the Separators and Escape Characters from the File
		this.Separator = scanner.getSeparators(); //
		scanner.clearOnNext = true; //don't append Strings accross several Fields!
		if (_fieldNames == null) { 	//Read the Field Names
			scanner.doEscape = false; //for reading the Field Names
			readNext(false); //read at least the first Row for Field #, Names, Defaults etc., even if it is a Data Row!
			_fieldNames = this.getAllFields();
			scanner.doEscape = true; 
		} 
		position = -1; 
		if (_separators == null) //skip the next Comment Row
			readNext(false); 
		//this overwrites the Information on the previous rows, but makes it easier to navigate later...
		//this.firstPos = 1; //...since the position() Function returns the inner position Variable. 
		super.init(_fieldNames.length, _fieldNames); 
		operationSupported = "OpFlag".equals(_fieldNames[0]);
		this.getMetaData(); //force Creation for direct Usage! 
	}
	
	/**
	 * fills an Array with all next Items of the streamIO and returns it
	 */
	public String[] getArray() {
		fillArray(); //toArray() doesn't work! Runtime Error! due to Conversion (no Covariance)
		int i = fields.getInt();
		String[] ret = new String[i];
		while (--i >= 0) {
			ret[i] = (String) fields.getStringAt(i); }
		return ret; }

	/**
	 * Central Routine to fill the internal ArrayList with the next Items of the streamIO. 
	 * Helper Routine used by the Constructor (Field Names) and readNext()
	 * SideEffect: moves the IOPointer to the Beginning of the next Row.
	 */
	protected void fillArray() {
		fields.setSize(0); //very fast clearing of Eval Flags!
		try {
			for (int sep; ;) {
				sep = scanner.nextToken().Value; 
	//			if (sep > 1) continue; //either ignore all minor Separators
				fields.addItem(scanner.currItem().toString()); //or treat all minor Seps like major ones...
				if ((sep < 0) || //EOF 
					(sep == Separator.length()-1)) { //last Separator
					if (this.metaData != null) //when someone requests it...
						this.metaData.columnCount = fields.getInt(); //allow to read variable-length RSs
					return; } //break on ALL major, i.e. small Separators 
			}  //
		} catch(final IOException x) {
			throw new IOError(x); 
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//	abstract Methods of abstract Class AResultSet
	////////////////////////////////////////////////////////////////////////////////
	
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
	  * @return true if the new current row is valid; false if there are no more rows
	  * @throws SQLException - if a database access error occurs
	  */
	protected final boolean readNext() throws SQLException { 
		try { return readNext(true);
		} catch (final IOException x) {
			throw new SQLException(x.toString());  
		}
	}
	
	/**
	  * Reads the Contents and moves the cursor down one row from its current Position.
	  * When this Row is deleted and skipDeleted is set, the Contents is not read.
	  *
	  * @return true if the new current row is valid; false if there are no more rows
	  * @throws SQLException - if a database access error occurs
	  */
	protected boolean readNext(final boolean skipMetaData) throws IOException {
		do { //
			file.mark(); //Integer.MAX_VALUE); //remember Position for later update of the Flag or Clearing of Row
			rowOffsets.setAt(++position, file.getPosition()); //
			fillArray(); //Have to read until the End
			if (fields.getInt() <= 1)
				return false; 
		} while (!isDataRow() && skipMetaData); //buffer[0])); 
		if ((!operationSupported) || 
			(CHR_OP_DELETED != (operationFlag = (byte)(fields.getStringAt(0)).charAt(0)))) {
			operationFlag =  CHR_OP_NEUTRAL; } //clear any updates from previous Operations
		//Fields.set(FlagPosition, STR_NEUTRAL); //initialize to "not changed"
		return true; //always return true, except something goes very wrong.
	}

	/**
	  * Refreshes the current row with its most recent value in the database.
	  * Loses the current Changes in the Record.
	  * Either goes back to the Database
	  * or at least reverts to the original Values if these have been modified!
	  * To do this, a Copy is required.
	  */
	public void refreshRow() throws SQLException {
		//try {
			file.reSet();
			readNext(); //go back to the Database File
//		java.util.Arrays.fill(currRow, null); //very fast clearing of Eval Flags!
/*		try {  //since all Values are still cached in the Buffers, clearing the Strings is sufficient!
			--Position;
			file.skipBytes((int) -RecordSize);// .seek(--Position*RecordSize);
			readNext();
		} catch (final IOException x) {
			throw new SQLException(x.toString()); }
*/	}

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object as a String in the Java programming language.
	  * Strings can be filled with Spaces (and trimmed later) or terminated with 0
	  */
	public String getString(final int columnIndex) {
		if (columnIndex < fields.getInt()) {
			final Object ret = fields.getStringAt(columnIndex);
			if (ret != null) { //the trimmed first Character makes Problems
				return ret.toString(); } //.trim(); }
		}
		return ""; } //FieldDefaults[columnIndex]; }

	/** Returns how many Fields the current Row actually holds.
	  * @return the Number of currently available Fields	 */
	public int getNumFields() { return fields.getInt(); }
	
	/**
	  * Updates the designated column with a String value.
	  * Updating consists of... 
	  * marking the current Row as updated/deleted (and thus invalid) and 
	  * inserting the updated Row at the End. 
	  * This way you even gain a Log of all Proceedings in this DB! 
	  */
	public void updateString(final int columnIndex, String x) throws SQLException {
		checkReadOnly(); 
		if (x == null) 
			x =  ""; 
		String str = "";
		try {  str = fields.getStringAt(columnIndex);
		} catch (final IndexOutOfBoundsException y) { 
			//Log.N(y); //it is allowed to have fewer Columns in separated Files!
		}
		if  ( (x   ==   str) ||
			  (x.equals(str))) //
			return; 
		operationFlag = CHR_OP_UPDATED;
		fields.setAt(columnIndex, x);
	}

	/**
	  * Moves the cursor a relative number of rows, either positive or negative.
	  * @return true, when the Cursor is at a valid Record, false otherwise
	  */
	public boolean relative(final int rows) throws SQLException {
		return absolute(position + rows); } //

	/**
	  * Moves the cursor to the given row number in this ResultSet object.
	  * @return true, when the Cursor is at a valid Record, false otherwise
	  */
	public boolean absolute(final int row) throws SQLException {
		//throw new SQLException("Class '" + getClass().getName() + "': too expensive to move absolutely! "); } //
		final long offset = rowOffsets.getLongAt(row-1+firstPos); 
		if ((offset == 0) && (row > 0)) { //not there yet... move forward
			while(position < row)
				if (!next())
					return false; 
			return true; 
		}
		file.reSet(offset-rowOffsets.getLongAt(position)); //then go relative
		position = row-1; 
		return readNext(); }
/*		if ((Fields[0][0] != CHR_DELETED) &&
			(Fields[0][0] != CHR_NEUTRAL)) { //check potentially saves call Overhead!
			updateRow(); }
		boolean ret = true;
		if (row <= -1) {
			row  = 0; ret = false; }
		Position = row-1; //compensate for the Increment in readNext()
		if (row >= insertPosition) {
			row  = Position = insertPosition; ret = false; }
		try { file.seek(row*RecordSize);
		} catch (IOException x) {
			throw new SQLException(x.toString()); }
		if (ret) {
			this.readNext();
		} else {
//			java.util.Arrays.fill(currRow, ""); //no clearing of Eval Flags!
			System.arraycopy(FieldDefaults, 0, currRow, 0, currRow.length);
		} return ret;	}
	}
*/
	/** fills the current Row with the Default Values	 */
	public void fillDefaults() { DbColumn.FILL_DEFAULTS(columns, fields); }
	
	/** Returns the current Row's Fields, as a whole.
	 * @return the current Object.
	 * In this Case this is the complete Row Object.
	 */
	public Object currItem() { return fields; } //

	/////////////////////////////////////////////////////////////////////////////////////
	/// Write Access to the Stream
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	  * Inserts the contents of the insert row into this ResultSet object
	  * and into the database.
	  * The Procedure is as follows:
	  * @see moveToInsertRow() first
	  * then perform the update...() Statements on the Fields...
	  * @see insertRow() last to write the Data to the Store
	  *
	  * After Inserting you can either insert the next Record
	  * or move back to the current Row using the Method...
	  * @see moveToCurrentRow()
	  */
	public void insertRow() throws SQLException {
		if (readOnly) 
			throw new SQLException(STR_READ_ONLY); 
		if (!this.isInInsertRow()) {
			throw new SQLException("Call 'moveToInsertRow()' before inserting a Row! "); }
		operationFlag = CHR_OP_INSERTED;
		updateRow(); 
	}

	/**
	  * Updates the underlying database
	  * with the new (INSERT), DELETEd or UPDATEd contents of the current row of this ResultSet object.
	  * Writes the Contents and moves the cursor down one row from its current position.
	  *
	  * @return true if the current row was modified and saved; false otherwise
	  * @throws SQLException - if a database access error occurs
	  */
	public void updateRow() throws SQLException {
		if (operationFlag  == CHR_OP_NEUTRAL) //relocating Cursor late on update,  
			return; //because a read only Cursor is most probable.
		if (isBeforeFirst() || isAfterLast())
		 	return;
		if (readOnly || !operationSupported || !(file instanceof IStreamOutByte)) {
			throw new SQLException(STR_READ_ONLY); }//should never come here in the first place!
		final IStreamOutByte fileOut = (IStreamOutByte) this.file;
		final byte op = operationFlag; operationFlag = CHR_OP_NEUTRAL; //prevent further Operations, keeping reporting correct is not so important!
		try { //INSERT, UPDATE or DELETE
			long currPos = ((IStreamByteRandom)file).getFilePointer(); //remember the current Position
			if (op != CHR_OP_INSERTED) { //UPDATE or DELETE: move to the Beginning of the current Row,
				file.reSet(); //use the file's mark() Mechanism for this.
				fileOut.write(op); } //and mark the current Row, because an Update is a Delete plus an Insert!
			if (op != CHR_OP_DELETED) {	//don't need to write the Row on STR_DELETED!
				if (op != CHR_OP_INSERTED) { //UPDATE: go to the End...
					rndFile.seek(rndFile.length()); } //not necessary on INSERT, because already at the End!
				fileOut.write((op == CHR_OP_INSERTED) ? op : CHR_OP_NEUTRAL); //INSERT or UPDATE); 
				String str;	//save the previous Buffer, if it was changed!
				final char escChr = Separator.charAt(0); 
				final char colSep = Separator.charAt(1); 
				for (int i = 0, Len = fields.getInt(); ++i < Len;) {
					str = fields.getStringAt(i);
					if (str == null)  //never set or set to null
						str = ""; 
					if ((i == Len-1) && (str.length() == 0))
						break; //skip the last empty Column
					fileOut.write(colSep); //
					AStreamOutByte.ESCAPE_UNSAFE(fileOut, str, Separator); 
				}//due to the convenient writeBytes Method,
				AStreamOutByte.WRITE(fileOut, Separator, 2, Separator.length()-2);   //prevent adding a superfluous Separator to the End!
			}
			if  (op == CHR_OP_INSERTED) { //prefill the List e.g. to allow random Updates on the Collection. 
				DbColumn.FILL_DEFAULTS(columns, fields); 
			} else { //DELETE or UPDATE: move back to the original Position 
				rndFile.seek(currPos); //(the End of the Row being deleted or updated)
			} 
		} catch (final IOException x) {
			throw new SQLException(x.toString()); }
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	new Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Compresses the current ResultSet by eliminating deleted Rows
	  * This should be done when closing the ResultSet.
	  */
	public synchronized void compress() throws SQLException {
		if ((operationFlag != CHR_OP_DELETED) &&
			(operationFlag != CHR_OP_NEUTRAL)) { //check potentially saves call Overhead!
			updateRow(); }
		//Create a new, temporary File Object
		final File fileIn = new File(fileObj.getParentFile(), "ResultSetFix.bak");
		try { compress(fileIn);
		} catch (IOException x) {
			throw new SQLException(x.toString()); }
	}

	/** 
	 * compresses the current Data File
	 * @param fileBackup the Backup File
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws SQLException
	 */
	private void compress(final File fileBackup) throws IOException, FileNotFoundException, SQLException {
		file.close();
		fileBackup.delete(); //try to delete the old Backup File
		fileObj.renameTo(fileBackup); //rename the current File to a Backup File
		FileStreamIn_Byte in  = new FileStreamIn_Byte(fileBackup); //open the renamed File for reading only.
		FileStreamByte    out = new FileStreamByte(fileObj, "rw"); //create a new File
		final byte[] buf = new byte[(int) rowOffsets.getLongAt(firstPos)];
		in .read (buf); 			//copy the Start in a Bulk
		out.write(buf);
		//copy the Rows....
		//int numRows = 0; //, pos = 1;
		int val; 
		//final StringBuffer SB = new StringBuffer();
		final char rowSep = Separator.charAt(Separator.length()-1);
loop: 	do { //no need to use another Scanner here!?!
			for(val = in.read(); (val == CHR_OP_DELETED) || (val == CHR_OP_UPDATED); val = in.read()) { //skip this row...
				while ((val =  in.read()) != rowSep) { //skipRow
					if (val == IStreamIn_Byte.EOF) 
						break loop;  
				} 
			} //
			out.write(val);
			//++numRows;
			do {
				if ((val = in.read()) == IStreamIn_Byte.EOF) //shouldn't happen!
					break; //would result in incomplete Rows! 
				out.write(val); //since reading Byte-Wise, a Buffered Reader / Writer is recommended. 
			} while (val != rowSep); //reads the whole ResultSet Row
		} while (val != IStreamIn_Byte.EOF);
		in.close();
		//scanner = new InputStream2StreamIn(file = out, scanner.getSeparators(), scanner.EscapeChar);
		scanner.setStreamIn(file = out);	//set up the Parser with it
		file.reSet(rowOffsets.getLongAt(firstPos)); //then go relative
		position = 0; //absolute(0); //beforeFirst(); 
	}

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Logger for this Class	 */
	private static final Log L = new Log(ResultSetSep.class); 
	
	/** Testing Method	 
	 * @param RSF
	 * @throws SQLException
	 */
	protected static void printfirstRows(final ResultSet RSF) throws SQLException {
		L.n("Start: ").l(" next Op ").l(RSF.next()); PRINT_RS_ROW(RSF, System.out, 5);
		L.n("Start: ").l(" next Op ").l(RSF.next()); PRINT_RS_ROW(RSF, System.out, 5);
		L.n("Start: ").l(" next Op ").l(RSF.next()); PRINT_RS_ROW(RSF, System.out, 5);
		L.n("Start: ").l(" next Op ").l(RSF.next()); PRINT_RS_ROW(RSF, System.out, 5);
	}

	/** File Name for the static Testing Methods	*/
	final static public String TEST_FILE_PATH = "../../Databases/MusicCollection/";
	
	/** File Name for the static Testing Methods	*/
	final static public String TEST_FILE_NAME = "Artists.tab";
	
	/** File Name for the static Testing Methods	*/
	final static public String TEST_FILE = TEST_FILE_PATH + TEST_FILE_NAME; 
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws java.io.IOException, SQLException {
		L.n("Testing ").l(ResultSetSep.class.getName()).n();
		//
		main(new String[] { TEST_FILE } );
		final ResultSetSep RsS = new ResultSetSep(FileStreamByte.COPY_TMP_FILE(TEST_FILE, "tmp.tab"), true, null);
		PRINT_RS(RsS, L); RsS.beforeFirst();
		printfirstRows(RsS);
		RsS.deleteRow(); //delete this Row
		L.n("Start: ").l(" next Op ").l(RsS.next()); PRINT_RS_ROW(RsS, L, 5);
		RsS.updateString("Name", "Idiot"); //update the next Row
		RsS.moveToInsertRow(); RsS.updateString(4, "***newly added***");
		RsS.insertRow(); //add two new empty Rows
		RsS.insertRow();
		RsS.beforeFirst();
		L.n(RsS.getString(1)); // RSF.updateString(4, "***Updated back***");
		L.n("Row deleted? ").l(RsS.rowDeleted());
		L.n("Start: ").l(" next Op ").l(RsS.next()); PRINT_RS_ROW(RsS, L, 5);
		printfirstRows(RsS); 
		PRINT_RS(RsS, L); 
		RsS.compress(); 
		RsS.beforeFirst(); 
		printfirstRows(RsS); 
		PRINT_RS(RsS, L); 
		RsS.close(); 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws java.io.IOException, SQLException {
		ResultSetSep rs = null;
		switch (args.length) {
		case 1: rs = new ResultSetSep(args[0]); break;
		case 2: rs = new ResultSetSep(args[0], args[1]); break;
		default:testIt(args); break;
		}
		if (rs != null) {
			PRINT_RS(rs);
		}
			
	}

}
