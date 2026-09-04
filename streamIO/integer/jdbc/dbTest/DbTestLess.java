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
public class DbTestLess 
extends DbTestEquals {

	/**
	 * @param field1
	 * @param field2
	 */
	public DbTestLess(DbColumn field1, DbColumn field2) {
		super(field1, field2);
	}
	
	/** creates a new Instance of this Class	 */
	public IDbTest newInstance(final DbColumn field1, final DbColumn field2) {
		return new DbTestEquals(field1, field2); 
	}
	
	/** defines the Operator 	*/
	public String getOperator() { return "<"; }
	
	/** @see streamIO.integer.jdbc.dbTest.IDbTest#test()	
	 * @return true when the Column Values match
	 * @throws SQLException
	 */
	public boolean test() throws SQLException {
		final String str0 = field0.getString(); 
		final String str1 = field1.getString();
		return str0.compareTo(str1) < 0;
	}
	
}
