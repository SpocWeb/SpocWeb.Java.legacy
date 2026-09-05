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
 * Full Outer Join variant of the Equals Test: treats either Field being null as a Match,
 * as long as the other side has not already matched a different Row.
 * @author heuerm
 * <!-- docstate
 * tags: [code/predicate, code/predicate_evaluation]
 * concepts: [Full Outer Join Row Predicate]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class DbTestFullOuter
extends DbTestEquals {

	/** Creates a Full Outer Equals Test over the given two Fields.
	 * @param field1 the left Operand
	 * @param field2 the right Operand
	 */
	public DbTestFullOuter(DbColumn field1, DbColumn field2) {
		super(field1, field2);
	}

	/** Returns the Full Outer Join Operator Symbol.
	 * @see streamIO.integer.jdbc.dbTest.IDbTest#getOperator()	 */
	public String getOperator() { return "*=*"; }
	
	//the Flags have to be reset with the ResultSet! 	
	
	/** Flag whether a Match has already been found 	 */
	boolean foundMatch1 = false; 
	
	/** Flag whether a Match has already been found 	 */
	boolean foundMatch2 = false; 
	
	/** Evaluates the Full Outer Equals Test, treating a null Field as a Match unless the
	 * other side already matched a different Row.
	 * @see streamIO.integer.jdbc.dbTest.IDbTest#test()
	 * @return true when the Column Values match
	 * @throws SQLException
	 */
	public boolean test() throws SQLException {
		final String str0 = field0.getString(); 
		if ((str0 == null) && !foundMatch2)
			return true; 
		final String str1 = field1.getString();
		if ((str1 == null) && !foundMatch1)
			return true; 
		return ValidationRule.EQUALS(str0, str1);
	}
	
	/** creates a new Instance of this Class	 */
	public IDbTest newInstance(final DbColumn field1, final DbColumn field2) {
		return new DbTestFullOuter(field1, field2); 
	}
	
}
