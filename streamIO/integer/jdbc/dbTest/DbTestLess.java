/*
 * Created on 25.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.jdbc.dbTest;

import java.sql.SQLException;

import streamIO.integer.jdbc.DbColumn;

/**
 * Tests that the left Field's String Value sorts strictly before the right Field's.
 * @author heuerm
 * <!-- docstate
 * tags: [code/predicate, code/predicate_evaluation]
 * concepts: [Less-Than Row Predicate]
 * facets: {layer: domain, status: broken, complexity: low}
 * -->
 */
public class DbTestLess
extends DbTestEquals {

	/** Creates a Less-Than Test over the given two Fields.
	 * @param field1 the left Operand
	 * @param field2 the right Operand
	 */
	public DbTestLess(DbColumn field1, DbColumn field2) {
		super(field1, field2);
	}

	// TODO: LOGIC: returns a plain DbTestEquals instead of a new DbTestLess, so a caller
	// invoking newInstance() on this Test loses the Less-Than semantics and silently gets
	// an Equals Test instead.
	/** Creates a new Instance of this Class	 */
	public IDbTest newInstance(final DbColumn field1, final DbColumn field2) {
		return new DbTestEquals(field1, field2);
	}

	/** Returns the Less-Than Operator Symbol.	 */
	public String getOperator() { return "<"; }

	/** Evaluates whether the left Field's String Value sorts before the right Field's.
	 * @see streamIO.integer.jdbc.dbTest.IDbTest#test()
	 * @return true when the Column Values match
	 * @throws SQLException
	 */
	public boolean test() throws SQLException {
		// TODO: LOGIC: neither str0 nor str1 is checked for null before compareTo() below;
		// getString() can return null for a SQL NULL value, throwing NullPointerException
		// (same class of bug as EqualCondition.equals()).
		final String str0 = field0.getString();
		final String str1 = field1.getString();
		return str0.compareTo(str1) < 0;
	}
	
}
