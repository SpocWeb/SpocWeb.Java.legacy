/*
 * Created on 25.03.2005
 *
 */
package streamIO.integer.jdbc.dbTest;

import java.sql.SQLException;

import streamIO.integer.jdbc.DbColumn;

/**
 * Encapsulates the Test for a (crisp) Relation between two Fields
 * @author heuerm
 *
 */
public interface IDbTest {

	/** 
	 * @return true when the Test succeeds. 
	 * @throws SQLException
	 */
	public boolean test() throws SQLException;
	
	/** defines the Operator, one of: 
	 * @see streamIO.integer.jdbc.AStatement#DB_TEST_EQUALS 
	 * @see streamIO.integer.jdbc.AStatement#DB_TEST_GREATER
	 * @see streamIO.integer.jdbc.AStatement#DB_TEST_GREATER_EQ
	 * @see streamIO.integer.jdbc.AStatement#DB_TEST_LESS
	 * @see streamIO.integer.jdbc.AStatement#DB_TEST_LESS_EQ
	 * @see streamIO.integer.jdbc.AStatement#DB_TEST_OUTER
	 */
	public String getOperator(); 
	
	/** @return the left Operand 	*/
	public DbColumn getOperand0(); 
	
	/** @return the right Operand 	*/
	public DbColumn getOperand1(); 
	
	/** creates a new Instance of this Class	 */
	public IDbTest newInstance(final DbColumn field0, final DbColumn field1); 

}
