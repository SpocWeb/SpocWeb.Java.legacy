/*
 * filters all rows out where the Condition is false 
 * Created on 13.03.2005
 *
 */
package streamIO.integer.jdbc.dbTest;

import java.sql.ResultSet;
import java.sql.SQLException;

import streamIO.integer.jdbc.AStatement;
import streamIO.integer.jdbc.FilterResultSet;
import streamIO.integer.jdbc.ResultSetCrossJoin;

/**
 * filters all rows out where the Condition is false 
 * @see streamIO.integer.jdbc.dbTest.IDbTest defines the Condition in this Class. 
 * @see streamIO.integer.jdbc.FilterRsByIRowCondition defines the Condition using IRowCondition. 
 * @author heuerm
 *
 */
public class FilterRsRows 
extends FilterResultSet {
	
	///////////////////////////////////////////////////////////////////////////
	/// Member Variables
	///////////////////////////////////////////////////////////////////////////
	
	/** Reference to the RowConditions to filter by
	 * All Conditions are connected usind OR! 
	 * To connect Conditions using AND, just nest FilterRsRows within each other
	 */
	protected final IDbTest[] conditions; 
	
	/** the full Cross-Join ResultSet to iterate over (and filter out the non-matching Rows)	 */
	protected final ResultSetCrossJoin rscj; 
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializing Constructor for a single Condition. 
	 * @param _rsIter
	 * @throws SQLException
	 */
	public FilterRsRows(final ResultSet _rsIter, final IDbTest _condition) throws SQLException {
		this(_rsIter, new IDbTest[] {_condition}); 
	}
	
	/**
	 * Initializing Constructor
	 * @param _rsIter
	 * @param _conditions the List of Conditions that should be OR combined 
	 * @throws SQLException
	 */
	public FilterRsRows(final ResultSet _rsIter, final IDbTest[] _conditions) throws SQLException {
		super(_rsIter);
		this.conditions = _conditions; 
		this.rscj = (_rsIter instanceof ResultSetCrossJoin) ? (ResultSetCrossJoin) _rsIter : null; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods
	///////////////////////////////////////////////////////////////////////////
	
	/**filters all rows out where the Condition is false 
	 * @see streamIO.integer.jdbc.AResultSet#readNext()	 */
	public boolean next() throws SQLException {
		int  counter = 0; 
		do{++counter; //local Counters and Timers are better than centralized ones... 
			if (! rsIter.next()) //...because they implicitly consider Scope! 
				return false;
		}while (!AStatement.TEST_OR(conditions));
		if (rscj != null) //rsIter instanceof ResultSetCrossJoin)
			rscj.matchFound = true; //found a Match, suppress the outer Join
		return true; 
	}
	
	/** @see java.sql.ResultSet#relative(int)	 */
	//public boolean relative(int rows) throws SQLException {}
	
}
