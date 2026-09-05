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
 * Wraps another Test and swaps its Operand order when creating a new Instance - reuses
 * DbTestNegate's delegate/operator fields but does not negate the Result.
 * @author heuerm
 * <!-- docstate
 * tags: [code/predicate, code/predicate_delegate]
 * concepts: [Operand-Swapping Row Predicate Wrapper]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class DbTestSwapOperands
extends DbTestNegate {

	/** Creates a Test wrapping the given delegate with Operands swapped.
	 * @param delegate the wrapped Test
	 * @param operator the Operator String to expose
	 */
	public DbTestSwapOperands(final IDbTest delegate, final String operator) {
		super(delegate, operator);
	}

	/** Evaluates the delegate Test directly, without negating.
	 * @see streamIO.integer.jdbc.dbTest.IDbTest#test()	 */
	public boolean test() throws SQLException {
		return delegate.test();
	}

	/** Creates a new Instance with the delegate's Operands swapped (field2, field1).	 */
	public IDbTest newInstance(final DbColumn field1, final DbColumn field2) {
		return new DbTestSwapOperands(delegate.newInstance(field2, field1), operator);
	}
	
}
