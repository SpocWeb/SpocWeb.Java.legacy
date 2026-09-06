/*
 * Created on 25.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.jdbc.dbTest;

import java.sql.SQLException;

import streamIO.integer.jdbc.DbColumn;
import synch.ValidationRule;

/**
 * Left Outer Join variant of the Equals Test: treats the left Field being null as a Match,
 * as long as it has not already matched a different Row.
 * @author heuerm
 * <!-- docstate
 * tags: [code/predicate, code/predicate_evaluation]
 * concepts: [Left Outer Join Row Predicate]
 * facets: {layer: domain, status: broken, complexity: low}
 * -->
 */
public class DbTestOuter
extends DbTestEquals {

	/** Creates a Left Outer Equals Test over the given two Fields.
	 * @param field1 the left Operand
	 * @param field2 the right Operand
	 */
	public DbTestOuter(DbColumn field1, DbColumn field2) {
		super(field1, field2);
	}

	/** Creates a new Instance of this Class	 */
	public IDbTest newInstance(final DbColumn field1, final DbColumn field2) {
		return new DbTestOuter(field1, field2);
	}

	/** Returns the Left Outer Join Operator Symbol.	 */
	public String getOperator() { return "=*"; }
	
	//the Flags have to be reset with the ResultSet! 	
	
	/** Flag whether a Match has already been found 	 */
	boolean foundMatch = false; 
	
	/** Evaluates the Left Outer Equals Test, treating a null left Field as a Match unless it
	 * already matched a different Row.
	 * @see streamIO.integer.jdbc.dbTest.IDbTest#test()
	 * @return true when the Column Values match
	 * @throws SQLException
	 */
	public boolean test() throws SQLException {
		final String str0 = field0.getString(); 
		if ((str0 == null) && !foundMatch)
			return true; 
		final String str1 = field1.getString();
		return ValidationRule.EQUALS(str0, str1);
	}
	
}
