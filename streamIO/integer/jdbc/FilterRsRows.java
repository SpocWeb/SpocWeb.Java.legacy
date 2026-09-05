/*
 * filters all rows out where the Condition is false 
 * Created on 13.03.2005
 *
 */
package streamIO.integer.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import math.vector.VectorString;

/**
 * filters all rows out where the Condition is false 
 * @see streamIO.integer.jdbc.IRowCondition is used to define the Criteria 
 * @see streamIO.integer.jdbc.dbTest.FilterRsRows 
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:57:30Z
 * digest: e43780cbf31249bd33c1c3739a15d751fd428ab2f73d227433b7e4c072a2a971
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class FilterRsRows 
extends FilterResultSet {
	
	/**
	 * Unused entry point; performs no action.
	 */
	public static void main(final String[] args) {
	}

	///////////////////////////////////////////////////////////////////////////

	/** Equality operator recognized by {@link #parseWhereClause(String)}. */
	final static public String STR_EQUALS = "=";

	/** Clause separator recognized by {@link #parseWhereClause(String)}. */
	final static public String STR_AND = " AND ";
	
	/**
	 * parses the Where Clause of a Query for exact Matches between Fields. 
	 * Other Operators (e.g. '>' or 'like') are not implemented yet.
	 * Also other Boolean Operators ('NOT', 'OR') are not supported yet.   
	 * @param where
	 * @return
	 */
	final static public String[][] parseWhereClause(final String where) {
		return VectorString.PARSE_2D(where.toUpperCase(), STR_AND, STR_EQUALS, true);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Reference to the RowCondition to filter by 	 */
	protected final IRowCondition condition; 
	
	/**
	 * Initializing Constructor
	 * @param _rsIter
	 * @throws SQLException
	 */
	public FilterRsRows(final ResultSet _rsIter, final IRowCondition _condition) throws SQLException {
		super(_rsIter);
		this.condition = _condition; 
	}
	
	/**filters all rows out where the Condition is false 
	 * @see streamIO.integer.jdbc.AResultSet#readNext()	 */
	protected boolean readNext() throws SQLException {
		do 
			if (! rsIter.next())
				return false;
		while (!condition.test(this)); 
		return true; 
	}
	
	/** @see java.sql.ResultSet#relative(int)	 */
	//public boolean relative(int rows) throws SQLException {}
	
}
