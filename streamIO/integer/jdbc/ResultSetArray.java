/*
 * Created on 09.04.2005
 *
 */
package streamIO.integer.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import math.matrix.MatrixObject;
import streamIO.Log;
import streamIO.integer.file.FileStreamByte;
import streamIO.integer.jdbc.dbTest.DbTestEquals;
import streamIO.integer.jdbc.dbTest.FilterRsRows;
import streamIO.object.parser.jdbc.ConnectionSep;
import streamIO.object.parser.jdbc.ResultSetSep;

/**
 * Title: ArrayResultSet<p>
 * Description:
 * Purpose:
 * An Object[][] Table-backed ResultSet. 
 * The Table Cols represent the ResultSet Cols directly, so they may be typed, 
 * as long as they are not primitive.  
 * There is no Row Concept in this Implementation.  
 * 
 * ResultSet based on a dynamic Array of Object Arrays (fixed Size)
 * Used for providing Arrays in the Form of ResultSets, which allows for relational Operations, 
 *  and for caching ResultSets in RAM, resulting in vast Performance Improvements! 
 * 
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 *
 * Design Decisions: 
 * reusing a dynamic Object Matrix to stay typesafe, 
 * but still separate the Storage Funktionality from the Iteration. 
 *   
 * @see streamIO.object.enumer.ArrayEnum is very similar
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class ResultSetArray 
extends AResultSetContainer {
	
	///////////////////////////////////////////////////////////////////////////
	/// static Variables & Constants
	///////////////////////////////////////////////////////////////////////////
	
	/**The default initial Capacity on instantiating an Array	 */
	public static int DEFAULT_ROW_INIT = 10;

	/**The default Capacity Increment on instantiating an Array	 */
	public static int DEFAULT_ROW_INCR = -1;

	/** Logger for this Class 	 */
	private static final Log L = new Log(ResultSetArray.class); 
	
	///////////////////////////////////////////////////////////////////////////
	/// static Methods
	///////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////
	/// Member Variables
	///////////////////////////////////////////////////////////////////////////
	
	/** holds the Values for this ResultSet */	
	protected final MatrixObject table;

	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param _values
	 * @param _rowIncrement
	 * @param _statement, optional
	 */
	public ResultSetArray(final Object[][] _values, final String[] _fieldNames, final int _numCols, final int _rowIncrement, final String _cursorName, final Statement _statement) {
		super(_fieldNames, _numCols, _cursorName, _statement); //TODO: initialize the FieldNames and FieldNumber
		this.table = new MatrixObject(_values, _rowIncrement); 
		position = -1; 
	}

	/**
	 * 
	 * @param _values
	 * @param _rowIncrement
	 */
	public ResultSetArray(final Object[][] _values, final String[] _fieldNames, final int _rowIncrement) {
		this(_values, _fieldNames, _fieldNames.length, _rowIncrement, "", null);
	}

	/**
	 * 
	 * @param _values
	 * @param _rowIncrement
	 */
	public ResultSetArray(final Object[][] _values, final String[] _fieldNames) {
		this(_values, _fieldNames, _fieldNames.length, DEFAULT_ROW_INCR, "", null);
	}

	/**
	 * 
	 * @param _values
	 * @param _rowIncrement
	 */
	public ResultSetArray(final Object[][] _values) {
		this(_values, null, _values[0].length, DEFAULT_ROW_INCR, "", null);
	}

	/**
	 * @param numCols
	 * @param _fieldNames
	 * @param _statement, optional
	 */
	public ResultSetArray(final String[] _fieldNames, final int _numCols, 
			final String _cursorName, final Statement _statement) {
		this(_fieldNames, _numCols, DEFAULT_ROW_INIT, DEFAULT_ROW_INCR, _cursorName, _statement);
	}

	/**
	 * @param _fieldNames
	 * @param _statement
	 */
	public ResultSetArray(final String[] _fieldNames, final String _cursorName, final Statement _statement) {
		this(_fieldNames, _fieldNames.length, _cursorName, _statement);
	}

	/**
	 * Copies the ResultSet into RAM for fast Retrieval. 
	 * @param _source the ResultSet to be copied 
	 * @throws SQLException only on Exception from _Source
	 */
	public ResultSetArray(final ResultSet _source) throws SQLException {
		super(_source.getCursorName(), _source.getStatement()); 
		final ResultSetMetaData rsMeta = _source.getMetaData();
		final String[] fieldNames = new String[rsMeta.getColumnCount()];
		for (int i = fieldNames.length; --i >= 0; )
			fieldNames[i] = rsMeta.getColumnName(i); 
		table = new MatrixObject(_source.getFetchSize(), DEFAULT_ROW_INCR); 
		init(fieldNames); 
		while(_source.next()) {
			final Object[] newRow = new Object[fieldNames.length]; 
			for (int i = fieldNames.length; --i >= 0; )
				newRow[i] = _source.getObject(i); 
			table.addItem(newRow); //counted both here and in table...
		}
		position = -1; 
	}

	/**
	 * 
	 * @param numRows initial Number of Rows to reserve
	 * @param numCols 
	 * @param rowIncrement
	 * @param _fieldNames
	 * @param _statement
	 */
	public ResultSetArray(final String[] _fieldNames, final int _numCols,
			int _numRows, final int _rowIncrement,
			final String _cursorName, final Statement _statement) {
		super(_fieldNames, _numCols, _cursorName, _statement);
		if (_numRows <= 0)
			_numRows = DEFAULT_ROW_INIT; 
		table = new MatrixObject(new Object[_numRows][], _rowIncrement); //numCols]);
	}

	///////////////////////////////////////////////////////////////////////////
	/// Methods
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * returns the current Number of Rows in this ResultSet
	 * @return the current Number of Rows in this ResultSet 
	 */
	public int getNumRows() { return table.getInt(); }
	
	/** @see streamIO.IMarkAble#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return table.getInt(); } 
	
	/** @see java.sql.ResultSet#getString(int)	 */
	public String getString(final int columnIndex) {
		final Object field = currRow[columnIndex];
		if (field == null)
			return   null; 
		return field.toString();
	}
	
	/** @see java.sql.ResultSet#relative(int)	 */
	public boolean relative(final int rows) throws SQLException {
		final boolean ret = (position+=rows) >= table.getInt(); 
		if (ret)
			position = table.getInt(); 
		currRow = table.getVectorAt(position);
		return !ret;
	}

	/** @see java.sql.ResultSet#insertRow()	 */
	public void insertRow() throws SQLException {
		currRow = new Object[position = columns.length]; 
		table.addItem(currRow); 
		//DbColumn.FILL_DEFAULTS(columns, currRow); 
	}

	/** @see streamIO.integer.jdbc.AResultSet#readNext()	 */
	protected boolean readNext() throws SQLException { return relative(1); }

	/** @see java.sql.ResultSet#updateString(int, java.lang.String)	 */
	public void updateString(final int columnIndex, final String x) throws SQLException {
		currRow[columnIndex] = x; 
	}
	
	/** @see streamIO.integer.jdbc.AResultSet#isAfterLast()	 */
	public boolean isAfterLast() throws SQLException {
		return position > table.getInt(); }
	
	///////////////////////////////////////////////////////////////////////////
	/// Main & testing Methods
	///////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws java.io.IOException, SQLException {
		final ResultSetFix rsf = new ResultSetFix(FileStreamByte.COPY_TMP_FILE(ResultSetFix.testFileName, "tmp.fix"));
		final ResultSetArray rsa = new ResultSetArray(rsf); 
		//PRINT_RS(rsa, L); 
		final ResultSetSep rsArtists = new ResultSetSep(ConnectionSep.TEST_DB_PATH+'/'+"Artists.tab"); 
		final ResultSetArray rsArtAr = new ResultSetArray(rsArtists); 
		final ResultSetSep rsCDs     = new ResultSetSep(ConnectionSep.TEST_DB_PATH+'/'+"CDs.tab"); 
		//vast Performance Improvement by cacheing the Lookup ResultSet in RAM, despite full Cross Join!
		final AResultSet rsArtist    = rsArtAr; //rsArtists; //
		rsArtist.beforeFirst(); //reSet(); //
		final ResultSetCrossJoin rsj = new ResultSetCrossJoin(rsCDs, rsArtist);
		final DbColumn FK   = rsCDs   .getColumns()[rsCDs   .findColumn("ArtistID")]; 
		final DbColumn PK   = rsArtist.getColumns()[rsArtist.findColumn("ID")]; 
		final DbColumn Name = rsArtist.getColumns()[rsArtist.findColumn("Name")];
		final DbColumn Value= new DbColumn(null, "SEAL"); 
		FilterRsRows rsFilter;
		rsFilter = new FilterRsRows(rsj, new DbTestEquals(PK, FK)); //use all available Columns for Filtering
		rsFilter = new FilterRsRows(rsj, new DbTestEquals(Name, Value)); //use all available Columns for Filtering
		PRINT_RS(rsFilter, L); 
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
