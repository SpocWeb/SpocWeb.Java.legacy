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
 * {@link AResultSetContainer} backed by an in-memory {@link MatrixObject} of
 * {@code Object[]} rows rather than a file or another data source; columns may be
 * arbitrarily typed (as long as not primitive) since each cell is a plain {@code Object}.
 * Useful both for materializing rows directly (relational operations on hand-built data)
 * and for caching an existing {@link ResultSet} in RAM for a large performance gain,
 * see the {@link #ResultSetArray(ResultSet)} copy constructor.
 *
 * <h2>Collaborators</h2>
 *
 * | Type | Relationship |
 * |---|---|
 * | {@link MatrixObject} | Backing storage for the rows. |
 * | {@link AResultSetContainer} | Superclass supplying the shared field/cursor bookkeeping. |
 *
 * @see streamIO.object.enumer.ArrayEnum a similar, non-JDBC array-backed enumerator
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T22:08:00Z
 * digest: 08f0cd9c3ecf630b180815e1300cddb943b0115287ec57abeb7039e6ee86ffaf
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
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
	 * Root initializing constructor wrapping a pre-populated {@code Object[][]} directly
	 * (no copy) as this ResultSet's backing {@link MatrixObject}.
	 * @param _values the row data to wrap
	 * @param _fieldNames the column names
	 * @param _numCols the number of columns
	 * @param _rowIncrement growth increment passed to {@link MatrixObject}
	 * @param _cursorName the cursor name
	 * @param _statement the owning Statement, optional
	 */
	public ResultSetArray(final Object[][] _values, final String[] _fieldNames, final int _numCols, final int _rowIncrement, final String _cursorName, final Statement _statement) {
		super(_fieldNames, _numCols, _cursorName, _statement); //TODO: initialize the FieldNames and FieldNumber
		this.table = new MatrixObject(_values, _rowIncrement); 
		position = -1; 
	}

	/**
	 * Wraps {@code _values} with no cursor name or owning Statement.
	 * @param _values the row data to wrap
	 * @param _fieldNames the column names
	 * @param _rowIncrement growth increment passed to {@link MatrixObject}
	 */
	public ResultSetArray(final Object[][] _values, final String[] _fieldNames, final int _rowIncrement) {
		this(_values, _fieldNames, _fieldNames.length, _rowIncrement, "", null);
	}

	/**
	 * Wraps {@code _values} using {@link #DEFAULT_ROW_INCR} as the growth increment.
	 * @param _values the row data to wrap
	 * @param _fieldNames the column names
	 */
	public ResultSetArray(final Object[][] _values, final String[] _fieldNames) {
		this(_values, _fieldNames, _fieldNames.length, DEFAULT_ROW_INCR, "", null);
	}

	/**
	 * Wraps {@code _values} with no field names, sizing the column count from the first row.
	 * @param _values the row data to wrap
	 */
	public ResultSetArray(final Object[][] _values) {
		this(_values, null, _values[0].length, DEFAULT_ROW_INCR, "", null);
	}

	/**
	 * Creates an initially empty ResultSet with {@link #DEFAULT_ROW_INIT} reserved rows.
	 * @param _fieldNames the column names
	 * @param _numCols the number of columns
	 * @param _cursorName the cursor name
	 * @param _statement the owning Statement, optional
	 */
	public ResultSetArray(final String[] _fieldNames, final int _numCols,
			final String _cursorName, final Statement _statement) {
		this(_fieldNames, _numCols, DEFAULT_ROW_INIT, DEFAULT_ROW_INCR, _cursorName, _statement);
	}

	/**
	 * Creates an initially empty ResultSet, sizing the column count from {@code _fieldNames}.
	 * @param _fieldNames the column names
	 * @param _cursorName the cursor name
	 * @param _statement the owning Statement
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
	 * Root initializing constructor for an empty, growable ResultSet.
	 * @param _fieldNames the column names
	 * @param _numCols the number of columns
	 * @param _numRows initial Number of Rows to reserve; {@link #DEFAULT_ROW_INIT} is used if not positive
	 * @param _rowIncrement growth increment passed to {@link MatrixObject}
	 * @param _cursorName the cursor name
	 * @param _statement the owning Statement
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
	
	/**
	 * Returns the current number of rows, same as {@link #getNumRows()}.
	 * @see streamIO.IMarkAble#getMaxMarkSize()
	 */
	public long getMaxMarkSize() { return table.getInt(); }

	/**
	 * Returns {@code currRow[columnIndex]} converted via {@link Object#toString()},
	 * or {@code null} if the field itself is {@code null}.
	 * @see java.sql.ResultSet#getString(int)
	 */
	public String getString(final int columnIndex) {
		final Object field = currRow[columnIndex];
		if (field == null)
			return   null; 
		return field.toString();
	}
	
	/**
	 * Advances {@code position} by {@code rows} and loads {@code currRow} from the
	 * backing {@link #table}, clamping the position at the row count when it would run
	 * past the end.
	 * @see java.sql.ResultSet#relative(int)
	 */
	public boolean relative(final int rows) throws SQLException {
		final boolean ret = (position+=rows) >= table.getInt(); 
		if (ret)
			position = table.getInt(); 
		currRow = table.getVectorAt(position);
		return !ret;
	}

	/**
	 * Appends a new, empty row to {@link #table} and positions on it.
	 * @see java.sql.ResultSet#insertRow()
	 */
	public void insertRow() throws SQLException {
		currRow = new Object[position = columns.length]; 
		table.addItem(currRow); 
		//DbColumn.FILL_DEFAULTS(columns, currRow); 
	}

	/**
	 * Advances by one row via {@link #relative(int)}.
	 * @see streamIO.integer.jdbc.AResultSet#readNext()
	 */
	protected boolean readNext() throws SQLException { return relative(1); }

	/**
	 * Sets {@code currRow[columnIndex]} to {@code x} directly.
	 * @see java.sql.ResultSet#updateString(int, java.lang.String)
	 */
	public void updateString(final int columnIndex, final String x) throws SQLException {
		currRow[columnIndex] = x;
	}

	// TODO: LOGIC: relative(int) clamps `position` to exactly table.getInt() when it would
	// run past the end, so `position` never exceeds the row count - it only ever reaches it.
	// This comparison uses `>` where `>=` is needed, so isAfterLast() always returns false
	// even when relative()/readNext() has driven the cursor past the last row.
	/**
	 * Reports whether the cursor is positioned after the last row.
	 * @see streamIO.integer.jdbc.AResultSet#isAfterLast()
	 */
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
