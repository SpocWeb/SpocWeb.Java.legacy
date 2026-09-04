/*
 * Created on 19.03.2005
 *
 * Interface for a Tester to implement a RowFilter, 
 * typically applied to a Cross Product (Join) of two ResultSets. 
 */
package streamIO.integer.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.RowSet;

import tester.ITester;

/**
 * @author heuerm
 *
 * Interface for a Tester to implement a RowFilter, 
 * typically applied to a Cross Product (Join) of two ResultSets. 
 * @see javax.sql.rowset.Predicate#evaluate(RowSet rs) 
 * 	that controls the rowset cursor moving from row to the next.
 *  It can also be used to check Integrity Constraints when inserting Rows. 
 */
public interface IRowCondition 
extends ITester {

	/** 
	 * returns true when the Test on  
	 * @param rs ResultSet to test 
	 * @return true when the Test is fulfilled on the current Row
	 * @throws SQLException
	 */
	boolean equals(final ResultSet rs) throws SQLException; 
	
}
