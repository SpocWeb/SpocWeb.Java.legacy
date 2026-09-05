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
 * Wraps another Test and inverts its Result.
 * @author heuerm
 * <!-- docstate
 * tags: [code/predicate, code/predicate_delegate]
 * concepts: [Negating Row Predicate Wrapper]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class DbTestNegate
implements IDbTest {

	/** Reference to the inner Tester	 */
	final public IDbTest delegate; 
	
	/** the Operator String to use 	*/
	final public String operator; 
	
	/** initializing Constructor 	 */
	public DbTestNegate(final IDbTest delegate, final String operator) {
		this.delegate = delegate; 
		this.operator = operator; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Interface IDbTest
	///////////////////////////////////////////////////////////////////////////

	/** Returns this Negation's own Operator String.
	 * @see streamIO.integer.jdbc.dbTest.IDbTest#getOperator()	 */
	public String getOperator() { return operator; }

	/** Evaluates the delegate Test and inverts its Result.
	 * @see streamIO.integer.jdbc.dbTest.IDbTest#test()	 */
	public boolean test() throws SQLException {
		return !delegate.test();
	}
	
	/** creates a new Instance of this Class	 */
	public IDbTest newInstance(final DbColumn field1, final DbColumn field2) {
		return new DbTestNegate(delegate.newInstance(field1, field2), operator); 
	}
	
	/** Delegates to the wrapped Test's left Operand.
	 * @see streamIO.integer.jdbc.dbTest.IDbTest#getOperand0()	 */
	public DbColumn getOperand0() { return delegate.getOperand0(); }

	/** Delegates to the wrapped Test's right Operand.
	 * @see streamIO.integer.jdbc.dbTest.IDbTest#getOperand1()	 */
	public DbColumn getOperand1() { return delegate.getOperand1(); }
	
}
