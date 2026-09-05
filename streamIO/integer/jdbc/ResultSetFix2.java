package streamIO.integer.jdbc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.file.FileStreamByte;

/**
 * Extends ResultSetFix with Methods for higher Performance 
 * 
 * @see streamIO.integer.jdbc.ResultSetFix is the Parent Class
 * The Difference is that in the Parent Implementation always the full Row is read or written into a Buffer,
 * so no optimization takes place that possibly skips single Columns (not implemented) or whole deleted Rows
 * (after reading their first Indicator).
 * That means that always the full Buffer is filled with Strings,
 * which should make the Parent Implementation considerably I/O faster 
 * than this one, but only if most of the Record is written / read 
 * (Sum of Fields Sizes vs. Record Size)
 * AND/OR the ResultSet is not very fragmented (deleted Records). 
 * The Implementation could be switched using a "Strategy" Pattern.
 *
 * TODO: Support Files without I,U,D Indicator as read only DataSet!
 * Have to copy fields[0][0] into the operationFlag and back!
 * 
 * which always writes the full Row and does not skip deleted Rows.
 * 
 * @see streamIO.object.parser.jdbc.ResultSetSep
 * @see streamIO.integer.jdbc.ResultSetFix
 * <!-- docstate
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class ResultSetFix2 
extends ResultSetFix {
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** List of the Field Buffers with exact Field Sizes	  */
	protected byte[][] fields;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	protected ResultSetFix2(final String _FilePath)
		throws FileNotFoundException, IOException, SQLException {
		this(new File(_FilePath));
	}

	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	protected ResultSetFix2(final File _file)
		throws FileNotFoundException, IOException, SQLException {
		this(_file, IStreamIn_Byte.BYTE_LINE_FEED, CHR_SPACE, null, _file.getName());
	}

	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	protected ResultSetFix2(final String _FilePath, final byte rowSep, final byte colSep)
		throws FileNotFoundException, IOException, SQLException {
		this(new File(_FilePath), rowSep, colSep);
	}

	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	protected ResultSetFix2(final File _file, final byte rowSep, final byte colSep)
		throws FileNotFoundException, IOException, SQLException {
		this(_file, rowSep, colSep, null, _file.getName());
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	protected ResultSetFix2(final String FileName, final int[] FieldSizes)
		throws FileNotFoundException, IOException {
		this(new File(FileName), FieldSizes);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	protected ResultSetFix2(final File _File, final int[] _FieldSizes)
		throws FileNotFoundException, IOException {
		this(_File, _FieldSizes, 0, null, null, null, _File.getName());
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	protected ResultSetFix2(final String _FilePath, final int[] _FieldSizes, final String[] _FieldNames)
		throws FileNotFoundException, IOException {
		this(new File(_FilePath), _FieldSizes, _FieldNames);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	protected ResultSetFix2(final File _File, final int[] _FieldSizes, final String[] _FieldNames)
		throws FileNotFoundException, IOException {
		this(_File, _FieldSizes, 0, _FieldNames, null, null, _File.getName());
	}

	/** Initializing Constructor
	  * The Field Sizes reflect the Existence of the initial Column
	  * that indicates deleted Rows.
	  *
	  */
	protected ResultSetFix2(
		final File _File,
		final int[] _FieldSizes,
		final int _DataOffset,
		final String[] _FieldNames,
		final String[] _FieldDefaults)
		throws FileNotFoundException, IOException {
		this(_File, _FieldSizes, _DataOffset, _FieldNames, _FieldDefaults, null, "");
	}

	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	protected ResultSetFix2(final String _FilePath, final Statement _statement, final String _cursorName)
		throws FileNotFoundException, IOException, SQLException {
		this(new File(_FilePath), IStreamIn_Byte.BYTE_LINE_FEED, CHR_SPACE, _statement, _cursorName);
	}

	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	protected ResultSetFix2(final File _file, final Statement _statement, final String _cursorName)
		throws FileNotFoundException, IOException, SQLException {
		this(_file, IStreamIn_Byte.BYTE_LINE_FEED, CHR_SPACE, _statement, _cursorName);
	}

	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	protected ResultSetFix2(
		final String _FilePath,
		final byte _rowSep,
		final byte _colSep,
		final Statement _statement, final String _cursorName)
		throws FileNotFoundException, IOException, SQLException {
		this(new File(_FilePath), _rowSep, _colSep, _statement, _cursorName);
	}

	/** Initializing Constructor
	  * The initial Column has to act as a Separator to detect the Field Sizes.
	  */
	protected ResultSetFix2(
		final File _file,
		final byte _rowSep,
		final byte _colSep,
		final Statement _statement,
		final String _cursorName)
		throws FileNotFoundException, IOException, SQLException {
		super(_file, _rowSep, _colSep, _statement, _cursorName);
		init();
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	protected ResultSetFix2(final String _FilePath, final int[] _FieldSizes, 
			final Statement _statement, final String _cursorName)
		throws FileNotFoundException, IOException {
		this(new File(_FilePath), _FieldSizes, 0, null, null, _statement, _cursorName);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	protected ResultSetFix2(final File File_, final int[] FieldSizes_
			, final Statement _statement, final String _cursorName)
	throws FileNotFoundException, IOException {
		this(File_, FieldSizes_, 0, null, null, _statement, _cursorName);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	protected ResultSetFix2(
			final String _FilePath,
			final int[] _FieldSizes,
			final String[] _FieldNames,
			final Statement _statement, final String _cursorName)
	throws FileNotFoundException, IOException {
		this(new File(_FilePath), _FieldSizes, 0, _FieldNames, null, _statement, _cursorName);
	}

	/** Initializing Constructor
	  * The Field Sizes have to reflect the Existence of the initial Column
	  * that indicates deleted Rows. */
	protected ResultSetFix2(
			final File _File,
			final int[] _FieldSizes,
			final String[] _FieldNames,
			final Statement _statement, final String _cursorName)
	throws FileNotFoundException, IOException {
		this(_File, _FieldSizes, 0, _FieldNames, null, _statement, _cursorName);
	}

	/** Initializing Constructor
	  * The Field Sizes reflect the Existence of the initial Column
	  * that indicates deleted Rows.
	  *
	  */
	protected ResultSetFix2(
			final File _File,
			final int[] _FieldSizes,
			final int _DataOffset,
			final String[] _FieldNames,
			final String[] _FieldDefaults,
			final Statement _statement, final String _cursorName)
	throws FileNotFoundException, IOException {
		super(_File, _FieldSizes, _DataOffset, _FieldNames, _FieldDefaults, _statement, _cursorName);
		init();
	}

	////////////////////////////////////////////////////////////////////////////

	/** finishes Initialization of this Instance */
	protected void init() {
		int len;
		fields = new byte[len = columns.length][];
		while (--len >= 0) 
			fields[len] = new byte[columns[len].size];
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
		if (operationFlag == CHR_OP_NEUTRAL)
			return; //relocating Cursor here, because a read only Cursor is most probable.
		if (isBeforeFirst() || isAfterLast())
			return;
		if (readOnly || (rndFile == null)) {
			throw new SQLException(STR_READ_ONLY);
		} //should never come here in the first place!
		final byte fl = operationFlag;
		operationFlag = CHR_OP_NEUTRAL;
		//prevent further Operations, keeping reporting correct is not so important!
		try {
			if (fl != CHR_OP_INSERTED) {
				//file.skipBytes((int) -RecordSize); //skipping works only in the positive Direction!
				rndFile.seek(dataOffset + position * recordSize);
			} //thus we have to use seek()! write the current Row!
			rndFile.write(fl); //write the Marker
			if (fl == CHR_OP_DELETED) {
				rndFile.jump((int) recordSize - 1); //move to the end of the Row
				return;
			} //don't need to write the whole Row!
			int Len, MaxLen, i = 0;
			String currField; //save the previous Buffer, if it was changed!
			while (++i < fields.length) {
				byte[] field = fields[i];
				MaxLen = field.length; // FieldSizes[i]; //
				if (null == (currField = newRow[i])) { //no new Value
					if (rndFile.jump(MaxLen) == MaxLen) { //never read or modified or set to null
						continue;
					} //the old Value in the Buffer is still valid
					currField = getString(i);
				} //skipBytes doesn't work, when adding new Rows.
				byte[] bytes = currField.getBytes();
				if ((Len = bytes.length) > MaxLen) {
					Len = MaxLen;
					System.arraycopy(bytes, 0, field, 0, MaxLen);
				} else if (bytes.length < MaxLen) {
					java.util.Arrays.fill(field, Len, field.length, CHR_SPACE);
					//prefill it with Spaces, alternatively fill it with 0s
					System.arraycopy(bytes, 0, field, 0, bytes.length);
				} else {
					field = fields[i] = bytes;
				}
				//str.getBytes(0, Len, field, 0); //deprecated! Use a Reader with the Default Encoding instead!
				rndFile.write(field);
				/*file.writeBytes(str); //due to the convenient writeBytes Method of the String Class
				while (++Len <= MaxLen) { //write the last Characters individually
					file.writeByte(CHR_SPACE); } //this can be expensive due to frequent Low Level Calls.
				//if    (++Len < MaxLen) { //terminate the String by a 0 Byte
				//	file.writeByte(0); }
				*/
			}
		} catch (IOException x) {
			throw new SQLException(x.toString());
		}
		//		return ret;
	}

	/**
	 * Old Implementation: 
	 * Reads the Contents Field-wise 
	 * and moves the cursor down one row from its current position.
	 * When this Row is deleted and readDeleted is not set, the Contents is not read.
	 *
	 * @return true if the new current row is valid; false if there are no more rows
	 * @throws SQLException - if a database access error occurs
	 */
	protected boolean readOld(final boolean forward) throws IOException {
		byte[] field;
		++position;
		Arrays.fill(newRow, null); //very fast clearing of Eval Flags!
		Arrays.fill(oldRow, null); //very fast clearing of Eval Flags!
		//read the next Row.
		for (int i = -1; ++i < columns.length;) { //read all fields individually
			if (file.read(field = fields[i]) != field.length) {
				return false;
			}
			if (readDeleted || !operationSupported)
				continue; //go on, read all Fields deleted Rows...
			if (fields[0][0] != CHR_OP_DELETED)
				continue; //Speed Optimization
			operationFlag = CHR_OP_DELETED;
			rndFile.jump((int) recordSize - 1); //skip the deleted Row! don't read it
			return true; //valid Row, but deleted!
		} //set the Flag to support the Methods in the Super Classes
		//if((operationFlag = fields[0][0]) != CHR_DELETED) { //don't keep track of updated Rows!
		operationFlag = CHR_OP_NEUTRAL; //}
		return true; //valid Row, not deleted!
	}

	/** Loops over the Lines until it finds a valid one 
	 * 
	 * @param forward Flag whether to move forward or backward
	 * @return true when the Row at the current Position could be read completely
	 * @throws IOException
	 * @throws SQLException
	 */
	protected boolean read(final boolean forward) throws IOException {
		int counter = -1; //needed outside the Loop
		Arrays.fill(newRow, null); //very fast clearing of Eval Flags!
		byte[] field;
		for (;;) { //repeats...
			++counter;
			Arrays.fill(oldRow, null); //need to iteratively clear this!
			//no clearing of Eval Flags!
			//System.arraycopy(FieldDefaults, 0, currRow, 0, currRow.length);
			if (forward) {
				++position;
				if (file.available() <= 0) {
					DbColumn.FILL_DEFAULTS(columns, newRow); 
					DbColumn.FILL_DEFAULTS(columns, oldRow); 
					return false;
				}
			} else {
				if (--position <= 0) {
					position = 0;
					Arrays.fill(buffer, (byte) 0);
					rndFile.seek(dataOffset);
					return false;
				}
				//if (file.skipBytes((int) -RecordSize << 1) != (RecordSize << 1))
				rndFile.seek(dataOffset + (position - 1) * recordSize);
			}
			boolean skipped = false;
			for (int i = -1; ++i < columns.length;) { //read all fields individually
				if (file.read(field = fields[i]) != field.length) 
					return false;
				if (readDeleted || !operationSupported)
					continue; //go on, read all Fields of deleted Rows...
				if (fields[0][0] == CHR_OP_DELETED) {
					rndFile.jump((int) recordSize - 1); //skip the deleted Row! don't read it
					skipped = true; 
					break;
				}
			} //set the Flag to support the Methods in the Super Classes
			if (!skipped) 
				file.read(); //rndFile.skipBytes(1); //skip Row Separator / last Character
			if (!operationSupported) 
				break; //use any Row 
			if (isDataRow()) 
				break; //Check the Operation: 
		}
		if (position < 1) { //define the first Row
			position = 1;
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
		return fields[fieldNo][0];
	}

	/**
	  * Gets the old or updated Value of the designated column in the current row as a String.
	  * Strings can be filled with Spaces (and trimmed later) or terminated with 0
	  */
	public String getString(int columnIndex) {
		String ret;
		if ((ret = newRow[columnIndex]) != null) {
			return ret;
		} //
		if ((ret = oldRow[columnIndex]) != null) {
			return ret;
		} //
		byte[] field = fields[columnIndex];
		int trim = field.length;
		while (--trim >= 0) { //so that the Strings are returned exactly as they were saved.
			if (field[trim] != CHR_SPACE) { //(assuming no String should contain 0 Characters)
				break;
			}
		}
		return oldRow[columnIndex] = new String(field, 0, trim + 1);
		//deprecated! //defaults High Byte to 0
		//new String(field, 0); //deprecated! //defaults High Byte to 0
		//toString(fields[columnIndex], FieldBufs[columnIndex]).toString(); //slower
	}

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt() throws java.io.IOException, SQLException {
		testRSF(new ResultSetFix2(FileStreamByte.COPY_TMP_FILE(testFileName, "tmp.fix"))); //, testFieldSizes, Defaults));
	}

	/** Tests whether local Loop Variables make a Loop slower
	  * than Variables outside the Loop
	  * Result: the Speed is absolutely the same! No Stack Operations happen!
	  */
	public static void testLocalLoopVariables() {
		long startTime = System.currentTimeMillis();
		String[] arrStr = new String[10000000];
		//String currStr;
		for (int j = 50; --j >= 0;) {
			for (int i = arrStr.length; --i >= 0;) {
				final String currStr = arrStr[i];
				if (currStr != null) {
					System.out.println(arrStr[i]);
				}
			}
		}
		System.out.println(System.currentTimeMillis() - startTime);
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
