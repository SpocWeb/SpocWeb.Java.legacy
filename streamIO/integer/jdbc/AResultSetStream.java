/*
 * Created on 16.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import streamIO.integer.IStreamByteRandom;
import streamIO.integer.IStreamIn_Byte;

/**
 * Abstract {@link AResultSet} specialization backed by a byte stream file, using an
 * {@link IStreamByteRandom} random-access stream when available to support inserts and
 * repositioning, and a plain {@link IStreamIn_Byte} otherwise for forward-only reading.
 *
 * <h2>Collaborators</h2>
 *
 * | Type | Relationship |
 * |---|---|
 * | {@link AResultSet} | Superclass providing the shared {@link java.sql.ResultSet} machinery. |
 * | {@link IStreamIn_Byte} | The underlying byte stream every constructor requires. |
 * | {@link IStreamByteRandom} | Optional random-access capability, detected via {@code instanceof}. |
 *
 * @author heuerm
 * @see AResultSet the superclass
 * @see IStreamIn_Byte the underlying stream
 * @see IStreamByteRandom the optional random-access capability
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:49:32Z
 * digest: 1541f36404bc07c16072d9b3106cee5d2f89170d320ee705004866d1e2f17389
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public abstract class AResultSetStream
extends AResultSet {
	
	/**
	 * The Column Position of the Insert, Update, Delete Flag.
	 * For easy Deletion this is assumed to be the first Character in the Row.
	 * Unfortunately the CR/LF Ambiguity could result in the LF Character
	 * to be prepended to the first Character.
	 */
	//final static public int FlagPosition = 0;

	/** Reference to the Input streamIO	  */
	//	protected FileInputStream in;

	/** Reference to the Output streamIO	  */
	//	protected FileOutputStream out;

	/** Reference to the File Object, needed only for Compression	  */
	protected File fileObj;
	
	/** Reference to the random Positioning File, can be null for read-only and forward-only Streams.
	  * Choosing a Random Access Input streamIO
	  * allows for randomly positioning the Cursor.
	  * Instead of using a potentially unsynchronized Pair
	  * of Input and Output Streams, better use this Random Access streamIO
	  * TODO: file now has all Methods to navigate directly!!! 
	  */
	protected IStreamByteRandom rndFile;
	
	/** Reference to simple read-only and forward-only Streams 	 */
	protected IStreamIn_Byte file;
	
	/**
	 * Initializing constructor deriving columns from field names only.
	 * @param _cursorName
	 * @param _statement
	 */
	public AResultSetStream(final IStreamIn_Byte _file, final String _cursorName, final Statement _statement) {
		super(_cursorName, _statement);
		this.file = _file; 
		if (_file instanceof IStreamByteRandom)
			this.rndFile  = (IStreamByteRandom) _file; 
	}
	
	/**
	 * Initializing constructor with an explicit column count.
	 * @param _fieldNames
	 * @param _numCols
	 * @param _cursorName
	 * @param _statement
	 */
	public AResultSetStream(final IStreamIn_Byte _file, final String[] _fieldNames, final int _numCols,
			final String _cursorName, final Statement _statement) {
		super(_fieldNames, _numCols, _cursorName, _statement);
		this.file = _file; 
		if (_file instanceof IStreamByteRandom)
			this.rndFile  = (IStreamByteRandom) _file; 
	}

	/**
	 * Initializing constructor deriving the column count from {@code _fieldNames}.
	 * @param _fieldNames
	 * @param _cursorName
	 * @param _statement
	 */
	public AResultSetStream(final IStreamIn_Byte _file, final String[] _fieldNames, final String _cursorName,
			final Statement _statement) {
		super(_fieldNames, _cursorName, _statement);
		this.file = _file; 
		if (_file instanceof IStreamByteRandom)
			this.rndFile  = (IStreamByteRandom) _file; 
	}
	
	/**
	 * Initializing constructor accepting pre-built {@link DbColumn} descriptors.
	 * @param _cols
	 * @param _cursorName
	 * @param _statement
	 */
	public AResultSetStream(final IStreamIn_Byte _file, final DbColumn[] _cols, final String _cursorName,
			final Statement _statement) {
		super(_cols, _cursorName, _statement);
		this.file = _file; 
		if (_file instanceof IStreamByteRandom)
			this.rndFile  = (IStreamByteRandom) _file; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Members
	///////////////////////////////////////////////////////////////////////////

	/** cache for the current Reading position 	 */
	protected long currPointer = IStreamIn_Byte.EOF; 
	
	/**
	 * Seeks the random-access file back to the position remembered by
	 * {@link #moveToInsertRow()}, undoing the temporary move to the insert row.
	 * @see java.sql.ResultSet#moveToCurrentRow()
	 */
	public void moveToCurrentRow() throws SQLException {
		try {
			rndFile.seek(currPointer);
			currPointer = IStreamIn_Byte.EOF;
		} catch (final IOException x) {
			throw new SQLException(x.toString());
		}
	}

	/**
	 * Reports whether the cursor is currently parked at the insert row.
	 */
	public boolean isInInsertRow() { return (currPointer != IStreamIn_Byte.EOF); }
	
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
	public void moveToInsertRow() throws SQLException {
		if (isInInsertRow())
			return; //already moved to InsertRow 
		if (readOnly || (rndFile == null)) 
			throw new SQLException(STR_READ_ONLY); 
		try {
			if ((operationFlag != CHR_OP_DELETED) &&
				(operationFlag != CHR_OP_NEUTRAL)) { //check potentially saves call Overhead!
					updateRow(); } //relocating Cursor here, because a read only Cursor is most probable.
			currPointer = rndFile.getFilePointer(); 
			rndFile.seek(rndFile.length());
			fillDefaults(); 
		} catch (final IOException x) {
			throw new SQLException(x.toString()); 
		}
	}
	
	/** fills the current Row with the Default Values	 */
	public abstract void fillDefaults(); 
	
	/**
	 * Reports whether the underlying stream has no more bytes available to read.
	 * @see java.sql.ResultSet#isAfterLast()	 */
	public boolean isAfterLast() throws SQLException {
		try { return file.available() < 0;
		} catch (final IOException x) {
			throw new SQLException(x.toString()); 
		}
	}
	
	/** 
	 * Releases this ResultSet object's database and JDBC resources immediately
	 * instead of waiting for this to happen when it is automatically closed.
	 * @see java.sql.ResultSet#close()	 
	 */
	public void close() throws SQLException {
		try { file.close(); 
		} catch (final IOException x) {
			throw new SQLException(x.toString()); 
		}
	}

	/** throws SQLException when this ResultSet is Read-Only 
	 * @throws SQLException when this ResultSet is Read-Only
	 */
	public void checkReadOnly() throws SQLException {
		if (readOnly || (rndFile == null)) 
			throw new SQLException(STR_READ_ONLY);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//	abstract Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Re-reads the current row's data from the stream; left to the concrete subclass.
	 * @see java.sql.ResultSet#refreshRow()
	 */
	abstract public void refreshRow() throws SQLException;

	/**
	 * Gets the designated column's value as a {@code String}; left to the concrete subclass.
	 * @see java.sql.ResultSet#getString(int)
	 */
	abstract public String getString(final int columnIndex);

	/**
	 * Moves the cursor a relative number of rows; left to the concrete subclass.
	 * @see java.sql.ResultSet#relative(int)
	 */
	abstract public boolean relative(int rows) throws SQLException;

	/**
	 * Inserts the contents of the insert row into the stream; left to the concrete subclass.
	 * @see java.sql.ResultSet#insertRow()
	 */
	abstract public void insertRow() throws SQLException;

	/**
	 * Writes the current row's pending changes back to the stream; left to the concrete
	 * subclass.
	 * @see java.sql.ResultSet#updateRow()
	 */
	abstract public void updateRow() throws SQLException;

	/**
	 * Reads and returns whether a further row is available; left to the concrete subclass.
	 * @see streamIO.integer.jdbc.AResultSet#readNext()
	 */
	abstract protected boolean readNext() throws SQLException;

	/** @see java.sql.ResultSet#updateString(int, java.lang.String)	 */
	//final public void updateString(final int columnIndex, final String x) throws SQLException { ; }

	/**
	 * Updates the designated column of the current or insert row; left to the concrete
	 * subclass.
	 * @see java.sql.ResultSet#updateString(int, java.lang.String)
	 */
	abstract public void updateString(int columnIndex, String x) throws SQLException;

	/**
	 * Always returns {@link Long#MAX_VALUE}; a byte stream imposes no mark-size limit.
	 * @see streamIO.IMarkAble#getMaxMarkSize()
	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }
}
