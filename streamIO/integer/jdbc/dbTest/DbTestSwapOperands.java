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
 * @author heuerm
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DbTestSwapOperands 
extends DbTestNegate {

	/**
	 * @param delegate
	 * @param operator
	 */
	public DbTestSwapOperands(final IDbTest delegate, final String operator) {
		super(delegate, operator);
	}

	/** @see streamIO.integer.jdbc.dbTest.IDbTest#test()	 */
	public boolean test() throws SQLException {
		return delegate.test(); 
	}
	
	/** creates a new Instance of this Class	 */
	public IDbTest newInstance(final DbColumn field1, final DbColumn field2) {
		return new DbTestSwapOperands(delegate.newInstance(field2, field1), operator); 
	}
	
}
