/*
 * Created on 13.03.2005
 *
 * ResultSet that is filled from the (inner or outer) Join of two ResultSets.  
 */
package streamIO.integer.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ResultSet that is filled from the (inner or outer) Join of two ResultSets. 
 * The Join is the Cross Product of both ResultSets, 
 * which results in a lot of Overhead and Looping. 
 * If no Index exists, or no Criterion is given, this Looping is justified, 
 * but mostly it isn't, so , as an Optimization, the rsFind should be indexed! 
 * 
 * @author heuerm
 *
 */
public class ResultSetCrossJoin 
extends FilterResultSet {
	
	///////////////////////////////////////////////////////////////////////////
	// Member Variables
	///////////////////////////////////////////////////////////////////////////
	
	/** Number of Columns of the Base ResultSet being iterated, 
	 * to distinguish the Cols from the 2nd ResultSet.	 */
	protected final int numIterColumns; 
	
	/** Number of Columns of the Base ResultSet being searched, 
	 * to distinguish the Cols from the 1st ResultSet.	 */
	protected final int numFindColumns; 
	
	/** ResultSet to join, is being searched and should have an Index!  	*/
	protected final ResultSet rsFind; 
	
	/** Flag for a right Join; swaps the ResultSet Fields.	 */
	final boolean swapFields; 
	
	///////////////////////////////////////////////////////////////////////////
	// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param statement_ Reference to the Statement creating the Join. 
	 * @param rsIter left  RS of the Cross Join 
	 * @param rsFind right RS of the Cross Join
	 */
	public ResultSetCrossJoin(final ResultSet rsIter, final ResultSet rsFind) throws SQLException {
		this(rsIter, rsFind, false);
	}
	
	/**
	 * @param statement_ Reference to the Statement creating the Join. 
	 * @param rsIter left  RS of the Cross Join 
	 * @param rsFind right RS of the Cross Join
	 */
	public ResultSetCrossJoin(final ResultSet rsIter, final ResultSet rsFind, 
			final boolean _swapFields) throws SQLException {
		this(rsIter, rsFind, _swapFields, null);
	}
	
	/**
	 * @param rsIter left (outer) RS of the left outer Join, the one we iterate over 
	 * @param rsFind right(inner) RS of the left outer Join, the one we find matching Fields in.
	 * @param _swapFields Flag whether to swap the Sequence of the Fields in the ResultSet 
	 * from rsIter, rsFind to rsFind, rsIter. 
	 * @param _statement Reference to the Statement creating the Join. 
	 */
	public ResultSetCrossJoin(final ResultSet rsIter,
			final ResultSet rsFind, final boolean _swapFields, final Statement _statement) throws SQLException {
		super(rsIter, false);
		this.swapFields = _swapFields; 
		this.rsFind = rsFind; 
		final ResultSetMetaData rsMdIter = rsIter.getMetaData();
		final ResultSetMetaData rsMdFind = rsFind.getMetaData();
		numIterColumns = rsMdIter.getColumnCount(); 
		numFindColumns = rsMdFind.getColumnCount(); 
		final int colsCount = numIterColumns+numFindColumns; 
		final String[] fieldNames = new String[colsCount]; 
		for (int i = numIterColumns; --i >= 0;) 
			fieldNames[i+(swapFields ? numFindColumns : 0)] = rsMdIter.getColumnName(i); //use the Alias instead! 
		for (int i = numFindColumns; --i >= 0;) 
			fieldNames[i+(swapFields ? 0 : numIterColumns)] = rsMdFind.getColumnName(i);
		super.init(colsCount, fieldNames); //create new Field Objects...
	} //...since they belong to a separate 'Meta-Table'
	
	///////////////////////////////////////////////////////////////////////////
	/// Read/Write on the current Row
	///////////////////////////////////////////////////////////////////////////
	
	/** @see java.sql.ResultSet#getString(int)	 */
	public String getString(final int columnIndex) {
		final int numIterColumns = (swapFields ? this.numFindColumns : this.numIterColumns);
		try {
			if (columnIndex < numIterColumns)
				return (swapFields ? rsFind : rsIter).getString(columnIndex); 
				return (swapFields ? rsIter : rsFind).getString(columnIndex-numIterColumns); 
		} catch (final SQLException x) {
			throw new RuntimeException(x); 
		}
	}
	
	/** @see java.sql.ResultSet#updateString(int, java.lang.String)	 */
	public void updateString(final int columnIndex, final String x) throws SQLException {
		final int numIterColumns = (swapFields ? this.numFindColumns : this.numIterColumns);
		if (columnIndex < numIterColumns)
			(swapFields ? rsFind : rsIter).updateString(columnIndex, x); 
		else
			(swapFields ? rsIter : rsFind).updateString(columnIndex-numIterColumns, x); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	// Navigation 
	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.object.IStreamIn#currItem()	 */
	public Object currItem() { return this; }
	
	/** late initialized with the Number of Rows in the right "Find" ResultSet	 */
	private int numRowsFind = -2; 
	
	/**
	 * returns the Number of Rows in the Find ResultSet
	 * to be able to skip the accoriding Number of Rows. 
	 * @return the Number of Rows in the Find ResultSet
	 * @throws SQLException
	 */
	protected int getNumRowsFind() throws SQLException {
		if (numRowsFind != -2) 
			return numRowsFind; 
		final int pos = rsFind.getRow(); //cache the previous Position
		rsFind.last(); numRowsFind = rsFind.getRow();
		rsFind.absolute(pos); //go back to the previous Position
		return numRowsFind; 
	}
	
	/** @see java.sql.ResultSet#relative(int)	 */
	public boolean relative(final int rows) throws SQLException {
		final int numRowsFind = getNumRowsFind(); 
		return false;
	}
	
	/** Flag to allow for reading outer Join Elements of the 'Find' ResultSet. 
	 * By setting it to 'true', a match of the current Iter Element is indicated!	 */
	public boolean matchFound = false; 
	
	/** Flag to allow for breaking the inner Loop after the first Match. 
	 * For full Table Scans and exact Matches on a Primary Key 
	 * the Speed can be doubled by breaking the Search on the first Match. 
	 */
	public boolean breakOnMatch = false; //true; 
	
	/** Flag to allow for reading outer Join Elements of the 'Iter' ResultSet 	 */
	//boolean outerIter = false; 
	
	/**Should return also the Insert Rows of all DataSets 
	 * For full Table Scans and exact Matches on a Primary Key 
	 * the Speed can be doubled by breaking the Search on the first Match. 
	 * @see streamIO.integer.jdbc.AResultSet#readNext()	 
	 */
	public boolean next() throws SQLException {
		while (rsIter.isBeforeFirst()) 
			rsIter.next();  
		//The DataSets should update themselves
		if (breakOnMatch && matchFound) {
			rsFind.first(); //assume only single Match on Primary Key
		} else {
			if (rsFind.next()) 
				return true; //still rows left in the 2nd RS
			if (matchFound)
				rsFind.first(); //no more rows, no Outer Join, reset the 2nd RS 
			else
				return matchFound = true; 
		}
		matchFound = false; 
		return rsIter.next(); //proceed on the 1st RS
	}

	final static public String STR_NOT_WELL_DEFINED = "Not well defined for Cross Joins!"; 
	
	/**The Question is whether to insert the Row into the Iter Table or the Find Table or both.... 
	 * @see java.sql.ResultSet#insertRow()	 */
	public void insertRow() throws SQLException {
		throw new SQLException(STR_NOT_WELL_DEFINED); 
	}

	/** The Question is should the left DataSet Row be deleted, or the right, or both?
	 * @see java.sql.ResultSet#deleteRow()	 */
	public void deleteRow() throws SQLException {
		throw new SQLException(STR_NOT_WELL_DEFINED); 
	}

	/** @see java.sql.ResultSet#rowUpdated()	 */
	public boolean rowUpdated() throws SQLException {
		return rsIter.rowUpdated() || rsFind.rowUpdated(); }

	/** @see java.sql.ResultSet#rowInserted()	 */
	public boolean rowInserted() throws SQLException {
		return rsIter.rowInserted() || rsFind.rowInserted(); }

	/** @see java.sql.ResultSet#rowDeleted()	 */
	public boolean rowDeleted() throws SQLException {
		return rsIter.rowDeleted() || rsFind.rowDeleted(); }

	/**Updates can be limited to the Dataset which Fields are updated, 
	 * unless the Keys are updated, which makes the Join absurd!  
	 * @see java.sql.ResultSet#updateRow()	 */
	public void updateRow() throws SQLException {
		//throw new SQLException(STR_NOT_WELL_DEFINED); 
		if (rsIter.rowUpdated())
			rsIter.updateRow(); 
		if (rsFind.rowUpdated())
			rsFind.updateRow(); 
	}

	/** @see java.sql.ResultSet#close()	 */
	public void close() throws SQLException {
		super.close(); 
		rsFind.close(); 
	}

}
