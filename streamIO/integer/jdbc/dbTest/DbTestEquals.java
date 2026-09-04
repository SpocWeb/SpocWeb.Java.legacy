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
 */
public class DbTestEquals 
implements IDbTest {
	
	/** Logger for this Class	 */
	private Log L = new Log(DbTestEquals.class, 1); 
	
	final static public String OPERATOR = "="; 
	
	/** the left Field of the Relation	*/
	final public DbColumn field0; 
	
	/** the right Field of the Relation	*/
	final public DbColumn field1; 
	
	/**returns a String Represenation of this Object
	 * @return a String Represenation of this Object
	 */
	public String toString() { return field0+getOperator()+field1; }
	
	/**
	 * @param field1
	 * @param field2
	 */
	public DbTestEquals(final DbColumn field0, final DbColumn field1) {
		this.field0 = field0;
		this.field1 = field1;
	}

	/** @see streamIO.integer.jdbc.dbTest.IDbTest#getOperator()	 */
	public String getOperator() { return OPERATOR; } 
	
	/** @see streamIO.integer.jdbc.dbTest.IDbTest#test()	
	 * @return true when the Column Values match
	 * @throws SQLException
	 */
	public boolean test() throws SQLException {
		final String param = field0.getString(); 
		final String value = field1.getString(); 
		L.n("'").l(param).l("'?='").l(value).l("'");
		return ValidationRule.EQUALS(param, value); 
	}
	
	public IDbTest newInstance(final DbColumn field0, final DbColumn field1) {
		return new DbTestEquals(field0, field1); 
	}

	/** @see streamIO.integer.jdbc.dbTest.IDbTest#getOperand0()	 */
	public DbColumn getOperand0() { return field0; }

	/** @see streamIO.integer.jdbc.dbTest.IDbTest#getOperand1()	 */
	public DbColumn getOperand1() { return field1; }
	
}
