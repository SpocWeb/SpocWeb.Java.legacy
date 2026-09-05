/*
 * Created on 25.03.2005
 *
 * Encapsulates a Test for a Relation between two Fields. 
 */
package streamIO.integer.jdbc.dbTest;

import java.sql.SQLException;

import streamIO.Log;
import streamIO.integer.jdbc.DbColumn;
import synch.ValidationRule;

/**
 * Encapsulates a Test for a Relation between two Fields. 
 * @author heuerm
 *
 * <!-- docstate
 * tags: [code/predicate, code/predicate_evaluation]
 * concepts: [Row-Level Equality Test between Two DbColumn Fields]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class DbTestEquals 
implements IDbTest {
	
	/** Logger for this Class	 */
	private Log L = new Log(DbTestEquals.class, 1); 
	
	/** The Operator Symbol for an Equals Test. */
	final static public String OPERATOR = "=";
	
	/** the left Field of the Relation	*/
	final public DbColumn field0; 
	
	/** the right Field of the Relation	*/
	final public DbColumn field1; 
	
	/**returns a String Represenation of this Object
	 * @return a String Represenation of this Object
	 */
	public String toString() { return field0+getOperator()+field1; }
	
	/** Creates a Test for Equality between the given two Fields.
	 * @param field0 the left Operand
	 * @param field1 the right Operand
	 */
	public DbTestEquals(final DbColumn field0, final DbColumn field1) {
		this.field0 = field0;
		this.field1 = field1;
	}

	/** Returns the Equals Operator Symbol.
	 * @see streamIO.integer.jdbc.dbTest.IDbTest#getOperator()	 */
	public String getOperator() { return OPERATOR; }
	
	/** Evaluates whether both Fields' String Values are equal.
	 * @see streamIO.integer.jdbc.dbTest.IDbTest#test()
	 * @return true when the Column Values match
	 * @throws SQLException
	 */
	public boolean test() throws SQLException {
		final String param = field0.getString(); 
		final String value = field1.getString(); 
		L.n("'").l(param).l("'?='").l(value).l("'");
		return ValidationRule.EQUALS(param, value); 
	}
	
	/** Creates a new DbTestEquals over the given Fields.	 */
	public IDbTest newInstance(final DbColumn field0, final DbColumn field1) {
		return new DbTestEquals(field0, field1);
	}

	/** Returns the left Operand Field.
	 * @see streamIO.integer.jdbc.dbTest.IDbTest#getOperand0()	 */
	public DbColumn getOperand0() { return field0; }

	/** Returns the right Operand Field.
	 * @see streamIO.integer.jdbc.dbTest.IDbTest#getOperand1()	 */
	public DbColumn getOperand1() { return field1; }
	
}
