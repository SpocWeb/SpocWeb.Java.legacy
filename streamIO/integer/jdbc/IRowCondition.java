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
 * Interface for a tester implementing a row filter, typically applied to a cross product
 * (join) of two result sets; can also check integrity constraints when inserting rows.
 *
 * @author heuerm
 * @see javax.sql.rowset.Predicate#evaluate(RowSet rs)
 * 	that controls the rowset cursor moving from row to the next.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:54:31Z
 * digest: 777f9d040fe051e70071e5545ea58b5f5fae0dd1699192b1844f7014f9b5bc15
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface IRowCondition
extends ITester {

	/**
	 * Evaluates this condition against the current row of {@code rs}.
	 * @param rs ResultSet to test
	 * @return true when the Test is fulfilled on the current Row
	 * @throws SQLException
	 */
	boolean equals(final ResultSet rs) throws SQLException;
	
}
