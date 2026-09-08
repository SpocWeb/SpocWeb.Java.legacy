/*
 * Interface to test the Foreign Key Relation between two ResultSet Rows
 * Created on 14.03.2005
 *
 */
package streamIO.integer.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import tester.IEquivalence;

/**
 * Contract for testing whether a foreign-key relation holds between the current rows of
 * two {@link ResultSet}s, extending {@link IEquivalence} with the two-argument row form.
 *
 * @author heuerm
 *
 * <!-- docstate
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: 4}
 * -->
 */
public interface IJoinCondition // 
extends IEquivalence {

	/** 
	 * returns true when the Primary/Foreign Key Relation between rs1 and rs2 is fulfilled 
	 * @param rs1 first ResultSet 
	 * @param rs2 second ResultSet
	 * @return true when the Primary/Foreign Key Relation between rs1 and rs2 is fulfilled on the current Rows of both
	 * @throws SQLException
	 */
	boolean equals(final ResultSet rs1, final ResultSet rs2) throws SQLException; 
	
}
