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
 * @author heuerm
 *
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
	
	/** @see java.sql.ResultSet#moveToCurrentRow()	 */
	public void moveToCurrentRow() throws SQLException {
		try {
			rndFile.seek(currPointer);
			currPointer = IStreamIn_Byte.EOF; 
		} catch (final IOException x) {
			throw new SQLException(x.toString()); 
		}
	}
	
	/** @see java.sql.ResultSet#moveToInsertRow()	 */
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
	
	/**can already be applied to  
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

	/** @see java.sql.ResultSet#refreshRow()	 */
	abstract public void refreshRow() throws SQLException; 

	/** @see java.sql.ResultSet#getString(int)	 */
	abstract public String getString(final int columnIndex);

	/** @see java.sql.ResultSet#relative(int)	 */
	abstract public boolean relative(int rows) throws SQLException;

	/** @see java.sql.ResultSet#insertRow()	 */
	abstract public void insertRow() throws SQLException; 

	/** @see java.sql.ResultSet#updateRow()	 */
	abstract public void updateRow() throws SQLException; 

	/** @see streamIO.integer.jdbc.AResultSet#readNext()	 */
	abstract protected boolean readNext() throws SQLException; 
	
	/** @see java.sql.ResultSet#updateString(int, java.lang.String)	 */
	//final public void updateString(final int columnIndex, final String x) throws SQLException { ; } 
	
	/** @see java.sql.ResultSet#updateString(int, java.lang.String)	 */
	abstract public void updateString(int columnIndex, String x) throws SQLException; 
	
	/** @see streamIO.IMarkAble#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }
}
