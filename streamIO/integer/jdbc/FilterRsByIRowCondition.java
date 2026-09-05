/*
 * filters all rows out where the Condition is false 
 * Created on 13.03.2005
 *
 */
package streamIO.integer.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * filters all rows out where the Condition is false 
 * 
 * @author heuerm
 *
 * <!-- docstate
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class FilterRsByIRowCondition 
extends FilterResultSet {
	
	///////////////////////////////////////////////////////////////////////////
	/// Member Variables
	///////////////////////////////////////////////////////////////////////////
	
	/** Reference to the RowCondition to filter by 	 */
	protected final IRowCondition condition; 

	/**
	 * Initializing Constructor
	 * @param _rsIter
	 * @throws SQLException
	 */
	public FilterRsByIRowCondition(final ResultSet _rsIter, final IRowCondition _condition) throws SQLException {
		this(_rsIter, _condition, null);
	}
	
	/**
	 * Initializing Constructor
	 * @param _rsIter
	 * @throws SQLException
	 */
	public FilterRsByIRowCondition(final ResultSet _rsIter, final IRowCondition _condition, 
			final Statement _statement) throws SQLException {
		super(_rsIter);
		this.condition = _condition; 
	}
	
	/**filters all rows out where the Condition is false 
	 * @see streamIO.integer.jdbc.AResultSet#readNext()	 */
	public boolean next() throws SQLException {
		do 
			if (! rsIter.next())
				return false;
		while (!condition.test(this)); 
		return true; 
	}
	
	/** @see java.sql.ResultSet#relative(int)	 */
	//public boolean relative(int rows) throws SQLException {}
	
}
