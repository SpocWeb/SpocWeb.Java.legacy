package streamIO.integer.jdbc;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import math.vector.VectorString;
import streamIO.Log;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.file.FileStreamByte;
import streamIO.integer.file.FileStreamIn_Byte;
import streamIO.object.parser.InputStream2StreamIn;

/**
  * Title: ResultSetFix<p>
  * 
  * Description:
  * Implements the Interface java.sql.ResultSet
  * for a file of fixed Length 2D Data.
  * Fixed Length Sets emphasize the relational structure,
  * because they don't allow the Inclusion of 1:N Relations in the same Row,
  * as separated Formats do (see Polygon Formats...)
  * Using the same Structure from the Start without a (variable Length) Prefix 
  * allows to use MS Access or other simple Parsers to handle the Data too.  
  * 
  * Extends Class AResultSet to reuse the Default Behavior
  * Implements the Standard JDBC Interface java.sql.ResultSet
  * 
  * @see streamIO.integer.jdbc.AResultSet
  * @see streamIO.object.parser.ResultSetSep
  * 
  * File Format: 
  * Optionally carries its own Formatting 
  * by using the first Character as the Separator for its first Row (Column Names), 
  * just like ResultSetSep. 
  * The Separator Character is the very first Character in the File. 
  * The Row Separator is usually defaulted to the LF Character, 
  * so it can be used for both Unix and DOS Files 
  * (the latter with CR at the last Character or Column dep. on trailing Separator).
  * 
  * The following Rows can either contain Data or Metadata, 
  * depending on the Contents of the first Column: 
  * 
  * When there is no first Column for MetaData, 
  * Data can still be updated and Deletion can be simulated 
  * by filling all Fields with empty Strings. 
  * This corresponds to the Fact that Update = Delete & Insert 
  * 
  * first Column for MetaData Rows: 
  * 'F' Field Name  (Overrides, esp. for brief Fields e.g. 1 Character)
  * 'L' Field Label (for Display or Print Output)
  * 'T' Type Information, one of 'I'(Integer), 'F'(Float), 'S'(String), 'D'(Date), 'T'(Time), 'P'(TimeStamP) or 'B'(Boolean)
  * 'N' Nullability of a Field 
  * 'W' Writability of a Field
  * 'C' Comment Row (skipped)
  * 
  * First column for Data Rows: 
  * ' ' unchanged Row 
  * '+' inserted Row (not really necessary)
  * '-'  deleted Row (can be read or skipped or inserted into by the ResultSet)
  * '~'  updated Row (not used for Fixed Size ResultSets)
  * '&' Row is continued in the next Row (which also determines the Type!
  *     the Data should be postpended, instead of prepended
  *     so the most significant Part stays in the typed Row and not the continued Row.  
  *     If you use LL(1), you can read on, knowing about the Continuation
  *     this also solves the Problem with reading the MetaData
  *     without starting the first Row, but on the other Hand 
  *     you need to update the first Column and this is more complex when reading the last! 
  * 
  * Design Decisions:
  * using StringBuffer and String to read the ResultSet from a Byte streamIO
  * This is deprecated, but sufficient for the LATIN-1 CharSet.
  * 
  * Data Formats: 
  * Strings are left aligned. 
  * Dates or Times are stored in XML Format: yyyy-mm-ddThh:mm:ss.mmm
  * Numbers are stored either in fixed Format: ddddd.dddddd
  * or in Exponential Format with Leading Exponent: XXemmmmmmmmm
  * Unfortunately sorting works only for Data of the same Sign!
  * 
  * Known SubClasses:
  * @see ResultSetFix2 which does not read the full Row and skips deleted Rows.
  * This could result in Performance Gains for 'sparse' ResultSets! 
  *
  * TODO:
  * Sorting is possible in Place.
  * Since Navigation in a File is not too fast,
  * Sorting should take place in RAM first and then on Disk.
  * This Strategy can be used even for separated File Formats.
  * Instead of sorting on Disk, an Index could be used. 
  * Even several Indices can be maintained. 
  * Since Virtual Memory is faster than Disk Access and has no Side-Effects, 
  * small to medium-sized Databases should be kept in Memory! 
  * For fast Updatability, also on Disk, the Index should be a Tree (preferably a B-Tree)
  * The Index is sorted according to a certain Schema and can be used
  * for binary Search, accessing the original Data (to prevent copying).
  * A typed numeric Type (w. padding) would be nice to allow for numeric sorting!
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	1999-12-31, 12;38;24<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * 
  * @see streamIO.object.parser.jdbc.ResultSetSep
  * @see streamIO.integer.jdbc.ResultSetFix2
  */
public class ResultSetFix 
extends AResultSetStream {

	/** Logger for this Class 	 */
	private static final Log L = new Log(ResultSetFix.class); 
	
	/** @return true when the File ends with the Fixed ResultSet Suffix  */
	final static public boolean HAS_FORMAT(String filePathOrName) {
		return VectorString.ENDS_WITH(filePathOrName, ConnectionFix.SUFFIX_FIX);
	}

	/** @return true when the File ends with the Fixed ResultSet Suffix  */
	final static public boolean HAS_FIX_FORMAT(File file) {
		return VectorString.ENDS_WITH(file, ConnectionFix.SUFFIX_FIX);
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Variable
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * The Offset of the Data in this File.
	 * This allows an arbitrary Amount of Comment etc. in the Data File.
	 * TODO: But actually this is redundant to firstLine. 
	 */
	protected int dataOffset;

	/**
	 * Cache for the updated Values of the current Row
	 * to speed up the Check on performing update().
	 * The two Caching Operations would compete with each other.
	 * Because the Problem is that only the single operationFlag
	 * does not reflect which Columns actually have changed,
	 * requiring a new Check for Optimization on performing update()
	 */
	protected String[] newRow;

	/**
	 * Cache for the calculated Values of the current Row
	 * The two Caching Operations would compete with each other.
	 * avoiding repeated calculation of the Value in the File speeds up processing.
	 * comparing the Value in the File with the new Value avoiding updates
	 * although the latter Scenario is quite improbable.
	 * The single operationFlag does not reflect which Columns actually have changed,
	 * requiring a new Check for Optimization on performing update()
	 */
	protected String[] oldRow;

	/** List of the Field Sizes	  */
	//protected int[] fieldSizes;

	/** List of the Field Offsets by Record#	  */
	protected int[] fieldOffsets;
	
	/** Buffer used for reading and writing a whole Row	*/
	protected byte[] buffer; // = new byte[(int)RecordSize];

	/** second Buffer filled with the Default Values; used for initializing a new Row	*/
	//	protected byte [] bufferDefault; //

	/** Record Size = Sum(FieldSizes)
	  * Made long to save explicit casting to long! */
	protected long recordSize;

	/** In C you can declare a structure to be at the same Position
	  * as a byte[] Array so filling the Array concurrently fills the structure.
	  * But to declare a dynamic structure this doesn't help anyway.
	  */

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	public ResultSetFix(final String _FileName) throws FileNotFoundException, IOException {
		this(new File(_FileName), IStreamIn_Byte.BYTE_LINE_FEED, CHR_SPACE, null, null);
	}

	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	public ResultSetFix(final File _file) throws FileNotFoundException, IOException {
		this(_file, IStreamIn_Byte.BYTE_LINE_FEED, CHR_SPACE, null, null);
	}

	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	public ResultSetFix(final String _FileName, final byte _rowSep)
		throws FileNotFoundException, IOException {
		this(new File(_FileName), _rowSep, CHR_SPACE, null, null);
	}

	/** Initializing Constructor determining the Field Sizes by parsing the first Row. 
	 * The initial Column has to act as a Separator to detect the Field Sizes.
	 * @param _File File Object to read from 
	 * @param rowSeparator Row Separator for parsing the Length
	 * @param columnSeparator Column Separator for parsing the Lengths, 
	 * 			if not positive, the first Character in the File is used!  
	 * @throws FileNotFoundException when the File does not exist 
	 * @throws IOException on any other I/O Error 
	 */
	public ResultSetFix(final File _File, final byte _rowSeparator, final byte _columnSeparator)
		throws FileNotFoundException, IOException {
		this(_File, _rowSeparator, _columnSeparator, null, null);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	public ResultSetFix(final String FileName, final int[] FieldSizes)
		throws FileNotFoundException, IOException {
		this(new File(FileName), FieldSizes, 0, null, null, null);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	public ResultSetFix(final File _File, final int[] _FieldSizes)
		throws FileNotFoundException, IOException {
		this(_File, _FieldSizes, 0, null, null, null);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	public ResultSetFix(final String FileName, final int[] _FieldSizes, final String[] _FieldNames)
		throws FileNotFoundException, IOException {
		this(new File(FileName), _FieldSizes, 0, _FieldNames, null, null);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	public ResultSetFix(final File _File, final int[] _FieldSizes, final String[] _FieldNames)
		throws FileNotFoundException, IOException {
		this(_File, _FieldSizes, 0, _FieldNames, null, null);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	public ResultSetFix(
		final String FileName,
		final int[] _FieldSizes,
		final String[] _FieldNames,
		final int _DataOffset)
		throws FileNotFoundException, IOException {
		this(new File(FileName), _FieldSizes, _DataOffset, _FieldNames, null, null);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	public ResultSetFix(
		final File _File,
		final int[] _FieldSizes,
		final String[] _FieldNames,
		final int _DataOffset)
		throws FileNotFoundException, IOException {
		this(_File, _FieldSizes, _DataOffset, _FieldNames, null, null);
	}

	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	public ResultSetFix(final String FileName, final Statement statement)
		throws FileNotFoundException, IOException {
		this(new File(FileName), IStreamIn_Byte.BYTE_LINE_FEED, CHR_SPACE, statement, null);
	}

	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	public ResultSetFix(final File _file, final Statement _statement)
		throws FileNotFoundException, IOException {
		this(_file, IStreamIn_Byte.BYTE_LINE_FEED, CHR_SPACE, _statement, null);
	}
	
	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	public ResultSetFix(final File _file, final Statement _statement, final String _TableName)
		throws FileNotFoundException, IOException {
		this(_file, IStreamIn_Byte.BYTE_LINE_FEED, CHR_SPACE, _statement, _TableName);
	}

	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	public ResultSetFix(final String _FileName, final byte _rowSep, final Statement _statement)
		throws FileNotFoundException, IOException {
		this(new File(_FileName), _rowSep, CHR_SPACE, _statement, null);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	public ResultSetFix(final String _FileName, final int[] _FieldSizes, final Statement _statement)
		throws FileNotFoundException, IOException {
		this(new File(_FileName), _FieldSizes, 0, null, null, _statement, null);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	public ResultSetFix(final File _File, final int[] _FieldSizes, final Statement _statement)
		throws FileNotFoundException, IOException {
		this(_File, _FieldSizes, 0, null, null, _statement, null);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	public ResultSetFix(
		final String _FileName,
		final int[] _FieldSizes,
		final String[] _FieldNames,
		final Statement _statement)
		throws FileNotFoundException, IOException {
		this(new File(_FileName), _FieldSizes, 0, _FieldNames, null, _statement, null);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	public ResultSetFix(
		final File _File,
		final int[] _FieldSizes,
		final String[] _FieldNames,
		final Statement _statement)
		throws FileNotFoundException, IOException {
		this(_File, _FieldSizes, 0, _FieldNames, null, _statement, null);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	public ResultSetFix(
		final String _FilePath,
		final int[] _FieldSizes,
		final String[] _FieldNames,
		final int _DataOffset,
		final Statement _statement)
		throws FileNotFoundException, IOException {
		this(new File(_FilePath), _FieldSizes, _DataOffset, _FieldNames, null, _statement, null);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	public ResultSetFix(
		final File _File,
		final int[] _FieldSizes,
		final String[] _FieldNames,
		final int _DataOffset,
		final Statement _statement)
		throws FileNotFoundException, IOException {
		this(_File, _FieldSizes, _DataOffset, _FieldNames, null, _statement, null);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted (updated, inserted, unchanged) Rows.
	  *
	  * TODO: parse the first row to find out the Number and Names of the Columns
	  */
	protected ResultSetFix(
		final File _file,
		final int[] _FieldSizes,
		final int _DataOffset,
		final String[] _FieldNames,
		final String[] _FieldDefaults, 
		final Statement _statement)
		throws FileNotFoundException, IOException {
		this(_file, _FieldSizes, _DataOffset, _FieldNames, _FieldDefaults, _statement, _file.getName());
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted (updated, inserted, unchanged) Rows.
	  *
	  * TODO: parse the first row to find out the Number and Names of the Columns
	  */
	protected ResultSetFix(
		final File _file,
		final int[] _FieldSizes,
		final int _DataOffset,
		final String[] _FieldNames,
		final String[] _FieldDefaults,
		final Statement _statement, 
		final String _cursorName)
		throws FileNotFoundException, IOException {
		super(new FileStreamByte(_file, "rw"), _cursorName, _statement);
		init(
			_file,
			_FieldSizes,
			_DataOffset,
			_FieldNames,
			_FieldDefaults);
	}

	/** Initializing Constructor determining the Field Sizes by parsing the first Row. 
	 * The initial Column has to act as a Separator to detect the Field Sizes.
	 * @param _File File Object to read from 
	 * @param rowSeparator Row Separator for parsing the Length
	 * @param columnSeparator Column Separator for parsing the Lengths, 
	 * 			if not positive, the first Character in the File is used!  
	 * @throws FileNotFoundException when the File does not exist 
	 * @throws IOException on any other I/O Error 
	 */
	public ResultSetFix(
		final File _File,
		final byte _rowSeparator,
		final byte _columnSeparator,
		final Statement _statement, 
		final String _cursorName)
		throws FileNotFoundException, IOException {
		super(new FileStreamByte(_File, "rw"), _cursorName, _statement);
		if (!_File.exists()) 
			throw new FileNotFoundException(_File.getAbsolutePath());
		parseLengthsAndNames(_File, _columnSeparator, _rowSeparator);
	}

	/////////////////////////////////////////////////////////////////////////////

	/**
	 * called by the Constructor
	 * The initial Column has to act as a Separator to detect the Field Sizes.
	 * The Field Names in the first Line can only be shorter by one Character. 
	 * This is especially problematic for 1 Character Fields. 
	 * For these you must supply an extra Row with the Field Names! 
	 * 
	 * @param _File
	 * @param _fileStream
	 * @param fieldSep Column Separator for parsing the Lengths, 
	 * 			if not positive, the first Character in the File is used 
	 * 			and Field Names are assumed to START with the Separator, 
	 * 			otherwise Field Names are finishing with the Separator(s, typically Spaces).
	 * @param rowSep
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void parseLengthsAndNames(final File _File, 
		final byte fieldSep, final byte rowSep) throws FileNotFoundException, IOException {
		//parse the first Row by the first Character to deduct the Field Names and the Field Sizes.
		//Use a StringTokenizer for that to avoid Dependency to Stream.Object.Parser.InputStream2StreamIn!
		final Collection coll;
		if (fieldSep > 0) {
			coll = InputStream2StreamIn.PARSE_FIELDS_LAST(file, fieldSep, rowSep, false);
		} else {
			coll = InputStream2StreamIn.PARSE_FIELDS     (file, (byte)file.read(), rowSep, false);
		}
		final String[] _FieldNames = new String[coll.size()];
		final int[] _FieldSizes = new int[_FieldNames.length];
		int i = -1;
		final Iterator iter = coll.iterator();
		while (iter.hasNext()) { //also includes the Flag Field
			final String FieldName = (String) iter.next();
			_FieldNames[++i] = FieldName.trim(); //prelim. Names
			_FieldSizes[i] = FieldName.length() + 1; //+1 for the Sep. Char
			if (fieldSep > 0) 
				--_FieldSizes[i];
		}
		final int _DataOffset = rndFile == null ? 0 : (int) rndFile.getFilePointer();
		init(_File, _FieldSizes, _DataOffset, _FieldNames, null);
		//has to be post-initialized further!
		//readNext(); //reads the first Record to Default Values
		//this.FieldDefaults = VectorString.COPY(fillAllFields());
		//this.Position = 0; //Position is unknown yet!
	}

	/** Initializing all inner Fields, called by the Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted (updated, inserted, unchanged) Rows.
	  *
	  * The Parameters can either be parsed from the File (a posteriori) 
	  * or given a priori. 
	  */
	protected void init(
		final File _file,
		final int[] _fieldSizes,
		final int _dataOffset,
		final String[] _fieldNames,
		final String[] _fieldDefaults)
		throws FileNotFoundException, IOException {
		super.init(_fieldSizes.length, _fieldNames);
		operationSupported =
			(_fieldSizes[0] == 1) && (_fieldNames != null) && (_fieldNames[0].length() == 0);
		this.dataOffset = _dataOffset;
		this.fileObj = _file;
		//this.FieldBufs = new StringBuffer[_FieldSizes.length];
		this.newRow = new String[_fieldSizes.length];
		this.oldRow = new String[_fieldSizes.length];
		this.fieldOffsets = new int[_fieldSizes.length + 1];

		fieldOffsets[0] = 0;
		recordSize = 0;
		for (int i = 0; i < _fieldSizes.length;) {
			recordSize += (columns[i].size = _fieldSizes[i]);
			fieldOffsets[++i] = (int) recordSize;
		}
		++recordSize; //for the final LF Separator Character
		buffer = new byte[(int) recordSize];
		/*if (rndFile == null) {
			int length = (int) rndFile.length() + 2; //for possibly missing CR/LF
			numRows = (int) (length/recordSize); 
		}*/
	}

	////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Compresses the current ResultSet by eliminating deleted Rows
	  * This should be done when closing the ResultSet.
	  */
	public synchronized void compress() throws IOException {
		if ((operationFlag != CHR_OP_DELETED) && (operationFlag != CHR_OP_NEUTRAL)) {
			//check potentially saves call Overhead!
			updateInternal();
		}
		File fileIn = new File(fileObj.getParentFile().getAbsoluteFile(), "ResultSetFix.temp");
		file.close();
		if (!fileObj.renameTo(fileIn)) //rename the new File
			throw new IOException("Could not rename the DB File named:'" + fileObj + "'");
		FileStreamIn_Byte in = new FileStreamIn_Byte(fileIn);
		file = rndFile = new FileStreamByte(fileObj, "rw");
		//Transfer the Header
		byte[] header = new byte[dataOffset];
		in.read(header);
		rndFile.write(header);
		//Transfer the Data
		while (in.read(buffer) == recordSize) {
			if (buffer[0] == CHR_OP_DELETED) {
				continue;
			} //
			buffer[0] = CHR_OP_NEUTRAL;
			//writes the whole Record in one call!
			//this is faster, because it starts right at the next Iteration
			//instead of jumping to the End and then to the next Iteration.
			rndFile.write(buffer);
		}
		in.close();
		fileIn.delete(); //delete the old File
		rndFile.seek(dataOffset);
		position = 0; //absolute(0);
	}

	////////////////////////////////////////////////////////////////////////////////
	//	abstract Methods of abstract Class AResultSet
	////////////////////////////////////////////////////////////////////////////////

	/** fills the current Row with the Default Values	 */
	public void fillDefaults() { DbColumn.FILL_DEFAULTS(columns, this.newRow); }
	
	/**
	  * Inserts the contents of the insert row into this ResultSet object
	  * and into the database.
	  * The Procedure is as follows:
	  * @see moveToInsertRow()
	  * perform the Update Statements ...
	  * @see insertRow()
	  *
	  * After Inserting you can either insert the next Record
	  * or move back to the current Row using the Method...
	  * @see moveToCurrentRow()
	  */
	public void insertRow() throws SQLException {
		if (readOnly)
			throw new SQLException(STR_READ_ONLY);
		if (!isInInsertRow()) // 
			throw new SQLException("Call 'moveToInsertRow()' before inserting a Row! ");
		//position = ++insertPosition;
		operationFlag = CHR_OP_INSERTED;
		updateRow();
		fillDefaults(); 
	}

	/**
	  * Updates the underlying database
	  * with the new contents of the current row of this ResultSet object.
	  * Writes the Contents and moves the cursor down one row from its current position.
	  *
	  * Design Decisions:
	  * replaced the deprecated String.getBytes() Method.
	  * Filling a Buffer Byte Array completely instead of writing each individual Field.
	  *
	  * @return true if the current row was modified and saved; false otherwise
	  * @throws SQLException - if a database access error occurs
	  */
	protected void updateInternal() throws IOException {
		if (operationFlag == CHR_OP_NEUTRAL) //relocating Cursor late, 
			return; //because a read only Cursor is most probable.
		if (isBeforeFirst() || (file.available() < 0)) //;isAfterLast())
			return;
		if (readOnly || (rndFile == null)) //should never come here in the first place!
			throw new IOException(STR_READ_ONLY);
		final byte op = operationFlag; operationFlag = CHR_OP_NEUTRAL;
		if (op != CHR_OP_INSERTED) { //move back to the Beginning of the current Row
			//file.skipBytes((int) -RecordSize); //skipping works only in the positive Direction!
			rndFile.seek(dataOffset + (position - 1) * recordSize);
		} //thus we have to use seek()! write the current Row!
		if (op == CHR_OP_DELETED) {
			rndFile.write(op); //write the Marker //FileStreamByte has some more Methods...
			rndFile.jump((int) recordSize - 1); //seek(rndFile.getFilePointer()+recordSize-1); //
			//move to the end of the Row, no need to write the Contents on Deletions!
			return;
		} //don't need to write the whole Row!
		buffer[0] = (op == CHR_OP_INSERTED) ? op : CHR_OP_NEUTRAL; //INSERT or UPDATE
		if (!transferToBuffer()) 
			return; //skip I/O writing, if Changes were undone (quite unlikely though)
		rndFile.write(buffer);
	}

	/**
	  * Updates the underlying database
	  * with the new contents of the current row of this ResultSet object.
	  * Writes the Contents and moves the cursor down one row from its current position.
	  *
	  * Design Decisions:
	  * replaced the deprecated String.getBytes() Method.
	  * Filling a Buffer Byte Array completely instead of writing each individual Field.
	  *
	  * @return true if the current row was modified and saved; false otherwise
	  * @throws SQLException - if a database access error occurs
	  */
	public void updateRow() throws SQLException {
		//prevent further Operations, keeping reporting correct is not so important!
		try { //INSERT, UPDATE or DELETE
			updateInternal(); 
		} catch (final IOException x) {
			throw new SQLException(x.toString());
		}
		//		return ret;
	}

	/**
	 * also transfers the Data from the Strings to the Buffer 
	 * @return true when the Data was actually changed
	 */
	protected boolean transferToBuffer() {
		int size, space, toCopy;
		String currField;
		boolean changed = false;
		for (int offset = 1, i = 1; i < columns.length; i++) {
			space = columns[i].size;
			if (null == (currField = newRow[i])) { //no new Value
				offset += space;
				continue;
			} //the old Value in the Buffer is still valid
			//transfer the Data from the Strings to the Buffer
			changed = true;
			size = currField.length();
			toCopy = (size <= space) ? size : space; //choose the smaller
			System.arraycopy(currField.getBytes(), 0, buffer, offset, toCopy);
			//newData[i].getBytes(0, toCopy, buffer, offset);  //replaced deprecated Method here
			if (size < space) {
				Arrays.fill(buffer, offset + toCopy, offset + space, CHR_SPACE);
			}
			offset += space;
		}
		return changed;
	}

	/**
	 * protected Method, called by previous() and next(). 
	 * Thus no positioning can take place! 
	 * Reads the Contents and moves the cursor down one row from its current position.
	 * When this Row is deleted and readDeleted is not set, the Contents is not read. 
	 * At the End of the File the Insert Row is implicitly reached. 
	 *
	 * @return true if the new current row is valid; false if there are no more rows
	 * @throws SQLException - if a database access error occurs
	 */
	protected boolean readNext() throws SQLException {
		return readNext(true);
	}

	/**
	 * public Method, called by previous() and next(). 
	 * Thus no positioning can take place! 
	 * Reads the Contents and moves the cursor down one row from its current position.
	 * When this Row is deleted and readDeleted is not set, the Contents is not read.
	 * At the End of the File the Insert Row is implicitly reached. 
	 *
	 * @return true if the new current row is valid; false if there are no more rows
	 * @throws SQLException - if a database access error occurs
	 */
	public boolean readNext(final boolean forward) throws SQLException {
		return readNext(forward, position);
	}

	/**
	 * inner Method called by previous() and next(). 
	 * Thus no positioning can take place! 
	 * Reads the Contents and moves the cursor down one row from its current position.
	 * When this Row is deleted and readDeleted is not set, the Contents is not read.
	 *
	 * @return true if the new current row is valid; false if there are no more rows
	 * @throws SQLException - if a database access error occurs
	 */
	public boolean readNext(final boolean forward, final int nextPos) throws SQLException {
		if ((operationFlag != CHR_OP_DELETED) && (operationFlag != CHR_OP_NEUTRAL)) {
			updateRow(); //the Check here potentially saves call Overhead!
		}
		try {
			position = nextPos;
			return read(forward);
		} catch (final EOFException x) {
			return false;
		} catch (final IOException x) {
			throw new SQLException(x.toString());
		}
	}

	/** Loops backwards or forwards over the Rows until it finds a valid one 
	 * 
	 * @param forward Flag whether to move forward or backward
	 * @return true when the Row at the current Position is a valid, saved Row and could be read completely
	 * @throws IOException
	 * @throws SQLException
	 */
	protected boolean read(final boolean forward) throws IOException, SQLException {
		int counter = -1; 
		Arrays.fill(newRow, null); //very fast clearing of Eval Flags!
		String[] prevFields = null;
		for (;;) { //search for a valid row...
			++counter;
			Arrays.fill(oldRow, null); //need to iteratively clear this!
			//no clearing of Eval Flags!
			//System.arraycopy(FieldDefaults, 0, currRow, 0, currRow.length);
			if (forward) {
				++position;
			} else { //backward
				if (--position <= 0) {
					position = 0;
					Arrays.fill(buffer, (byte) 0);
					rndFile.seek(dataOffset);
					return false;
				}
				//if (file.skipBytes((int) -RecordSize << 1) != (RecordSize << 1)) //works only forward!
				rndFile.seek(dataOffset + (position - 1) * recordSize);
			}
			//file.readFully(buffer);  //blocks, if the Buffer is not filled yet!
			//read the whole Row in one single Operation! very efficient for small Field Sizes!
			if (file.read(buffer) != recordSize) { //moved to InsertRow
				DbColumn.FILL_DEFAULTS(columns, newRow); 
				DbColumn.FILL_DEFAULTS(columns, oldRow); 
				return false;
			}
			if (!operationSupported) 
				break; //then use any Row 
			if (prevFields != null) {
				getAllFields(oldRow, 1);
				VectorString.CONCAT(prevFields, prevFields, oldRow);
			}
			if (buffer[0] == CHR_OP_CONTINUED) {
				//read next Row and append all Fields (on reading forward; reading backward is another Problem!)
				if (prevFields == null) {
					prevFields = getAllFields();
				} else { //already concatenated above...
				}
			} else {
				if (prevFields != null) {
					VectorString.COPY_AT(oldRow, prevFields);
					prevFields = null;
				}
			}
			if (isDataRow()) 
				break; //Check the Operation: 
		}
		if (position < 1) { //not initialized yet...
			position = 1; //define the first Row
			if (rndFile != null)
				dataOffset = (int) (rndFile.getFilePointer() - recordSize);
		}
		return true;
	}

	/**
	 * Optimization to save constructing a String in ResultSetFix
	 * @return the first Character of the given Field
	 */
	public byte getFirstChar(final int fieldNo) { 
		//return super.getFirstChar(fieldNo)
		return buffer[fieldOffsets[fieldNo]];
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
		return readNext(true);
	}

	/**
	  * Moves the cursor to the previous row in this ResultSet object.
	  * Only in Fixed Length Formats, not in separated Formats. 
	  */
	public boolean previous() throws SQLException {
		return readNext(false); //
	}

	/**
	  * Refreshes the current row with its most recent value in the database.
	  * Loses the current Changes in the Record.
	  * Either goes back to the Database
	  * or at least reverts to the original Values if these have been modified!
	  * To do this, a Copy is required.
	  */
	public void refreshRow() { // throws SQLException {
		java.util.Arrays.fill(newRow, null);
		//very fast clearing of Eval Flags!
		/*		try {  //since all Values are still cached in the Buffers, clearing the Strings is sufficient!
					--Position;
					file.skipBytes((int) -RecordSize);// .seek(--Position*RecordSize + DataOffset);
					readNext();
				} catch (IOException x) {
					throw new SQLException(x.toString()); }
		*/
	}

	/**
	  * Gets the value of the designated column in the current row 
	  * but here as a trimmed String! 
	  * So leading or trailing Spaces should be enclosed e.g. in Quotes
	  * or other nonspace Characters.
	  */
	public String getString(int columnIndex) {
		//return currRow[columnIndex]; } //only on pre-creating all Strings
		String ret;
		if ((ret = newRow[columnIndex]) != null) {
			return ret; //updated Value
		} //
		if ((ret = oldRow[columnIndex]) != null) {
			return ret; //original Value
		} //delayed Reading from Buffer
		int offset = fieldOffsets[columnIndex];
		int trim = fieldOffsets[columnIndex + 1];
		//trim the trailing Space Characters here, this is faster than calling trim() later!
		while (--trim >= offset) { //so that the Strings are returned exactly as they were saved.
			if (buffer[trim] != CHR_SPACE) { //(assuming no String should contain 0 Characters)
				break;
			}
		} //using Default Encoding here!
		return oldRow[columnIndex] = new String(buffer, offset, trim - offset + 1);
	}

	/**
	  * Updates the designated column with a String value.
	  * Optimization: when the String is the same,
	  * the Update Flag is not set!
	  */
	public void updateString(int columnIndex, String x) throws SQLException {
		if (readOnly)
			throw new SQLException(STR_READ_ONLY);
		String str;
		if ((str = oldRow[columnIndex]) == null) {
			str = getString(columnIndex);
		}
		if ((str == x) || (str.equals(x))) { //
			newRow[columnIndex] = null; //mark it as not updated
			return;
		}
		newRow[columnIndex] = x;
		operationFlag = CHR_OP_UPDATED; //CHR_INSERTED, CHR_NEUTRAL
	}

	/**
	  * Moves the cursor a relative number of rows, either positive or negative.
	  * @return true, when the Cursor is at a valid Record, false otherwise
	  */
	public boolean relative(final int rows) throws SQLException {
		return absolute(position + rows);
	} //

	/**
	  * Moves the cursor to the given row number in this ResultSet object.
	  * 1 corresponds to first() -1 to last()
	  * @see #setPosition(int)
	  * @return true, when the Cursor is at a valid Record, false otherwise
	  */
	public boolean absolute(final int row) throws SQLException {
		return readNext(false, row + 1);
	}

	////////////////////////////////////////////////////////////////////////////
	//	new Methods
	////////////////////////////////////////////////////////////////////////////

	/** Sets the current Position
	 * @see #absolute(int) does the same
	 * @return false when the Position is out of the Range for this DB. 
	 */
	public void setPosition(int Position_) throws SQLException {
		absolute(Position_);
	}

	/**
	 * fills and returns all Fields, i.e. the whole Row.
	 * @return the current Object.
	 * In this Case this is the complete Row Object.
	 * The Row is not filled yet.
	 * @see #fillAllFields() has to be called for this 
	 */
	public Object currItem() {
		int trim, offset = fieldOffsets[oldRow.length];
		for (int i = oldRow.length; --i >= 0;) {
			trim = offset;
			offset = fieldOffsets[i];
			if (oldRow[i] == null) {
				oldRow[i] = new String(buffer, offset, trim - offset);
			}
		}
		return oldRow;
	} //

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** File Name for the static Testing Methods	*/
	public static String testFileName = "../../Databases/MusicCollection/Artists.fix";

	/** Default Separators for TAB separated Files     */
	final static public String TAB_SEPARATORS = "\\\t\r\n";

	/** Field Sizes for the static Testing Methods	*/
	//	public static int[] testFieldSizes = {1,11,20,30,30,2}; //the last two are for the CR/LF

	/** Field Defaults for the static Testing Methods	*/
	//	public static String[] Defaults = {null, "0", "testVorname", "testNachName", "testKommentar", "\r\n"};

	/** Singled out of testIt() for reuse on testing this Class	*/
	protected static void printNextRow(final ResultSet RSF, final int numCols)
		throws SQLException {
		L.l("next:" + RSF.next());
		PRINT_RS_ROW(RSF, numCols);
	}

	/** Singled out of testIt() for reuse on testing this Class	*/
	protected static void printPrevRow(final ResultSet RSF, final int numCols)
		throws SQLException {
		RSF.getRow();
		L.l("prev:" + RSF.previous());
		PRINT_RS_ROW(RSF, numCols);
	}

	/** Singled out of testIt() for reuse on testing this Class	*/
	protected static void printCurrRow(final ResultSet RSF, final int numCols)
		throws SQLException {
		L.l("pos:" + RSF.getRow()); //getPosition());
		PRINT_RS_ROW(RSF, System.out, numCols);
	}

	/** Singled out of testIt() for reuse on testing this Class	*/
	protected static void printfirstRows(final ResultSet RSF, final int numCols)
		throws SQLException {
		printNextRow(RSF, numCols);
		printNextRow(RSF, numCols);
		printNextRow(RSF, numCols);
		printNextRow(RSF, numCols);
		printNextRow(RSF, numCols);
		printNextRow(RSF, numCols);
		printPrevRow(RSF, numCols);
		printPrevRow(RSF, numCols);
		printPrevRow(RSF, numCols);
		printPrevRow(RSF, numCols);
		printPrevRow(RSF, numCols);
		printPrevRow(RSF, numCols);
		printPrevRow(RSF, numCols);
	}
	
	/** Tests all Methods of this Class or any Subclass	 */
	static final void testRSF(final ResultSetFix RSF)
		throws java.io.IOException, SQLException {
		L.n("Testing " + RSF.getClass().getName());
		RSF.readDeleted = true;
		final ResultSetMetaData rsMd = RSF.getMetaData();
		final int numCols = rsMd.getColumnCount();
		printfirstRows(RSF, numCols);
		RSF.absolute(3);
		printCurrRow(RSF, numCols);
		// RSF.updateString(4, "***Updated***"); //undoes Deletion!
		RSF.deleteRow();
		//why is this row not reported as being deleted, below???
		printNextRow(RSF, numCols);
		RSF.moveToInsertRow();
		RSF.updateString(4, "***newly added***");
		RSF.insertRow();
		RSF.insertRow();
		RSF.beforeFirst();
		printNextRow(RSF, numCols);
		PRINT_RS_ROW(RSF, System.out, numCols); //RSF.getNumCols());
		RSF.absolute(3); //moving to a deleted Row
		L.n("Row deleted? " + RSF.rowDeleted());
		//L.n(RSF.getString(1)); //reading a deleted row is optimized away
		//RSF.updateString(4, "***Updated back***");
		//updating a deleted Row (#3) doesn't work, because it is not read properly!
		L.n("Row deleted? " + RSF.rowDeleted());
		printPrevRow(RSF, numCols);
		printfirstRows(RSF, numCols);
		PRINT_RS(RSF, System.out);
		PRINT_RS_BACK(RSF, System.out);
		RSF.compress();
		printfirstRows(RSF, numCols);
		PRINT_RS(RSF, System.out);
		PRINT_RS_BACK(RSF, System.out);
		RSF.close();
	}

	/** Tests all Methods of this Class	 */
	public static void testIt() throws java.io.IOException, SQLException {
		testRSF(new ResultSetFix(FileStreamByte.COPY_TMP_FILE(testFileName, "tmp.fix")));
		//testRSF(new ResultSetFix(FileStreamByte.COPY_TMP_FILE(testFileName, "tmp.fix"), (byte)'\n', (byte)-1));
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws java.io.IOException, SQLException {
		if (args.length <= 0) {
			testIt(); return; }
	}

}
