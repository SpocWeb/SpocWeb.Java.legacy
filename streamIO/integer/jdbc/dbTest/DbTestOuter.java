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
 * @author heuerm
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DbTestOuter 
extends DbTestEquals {

	/**
	 * @param field1
	 * @param field2
	 */
	public DbTestOuter(DbColumn field1, DbColumn field2) {
		super(field1, field2);
	}

	/** creates a new Instance of this Class	 */
	public IDbTest newInstance(final DbColumn field1, final DbColumn field2) {
		return new DbTestEquals(field1, field2); 
	}
	
	/** defines the Operator 	*/
	public String getOperator() { return "=*"; }
	
	//the Flags have to be reset with the ResultSet! 	
	
	/** Flag whether a Match has already been found 	 */
	boolean foundMatch = false; 
	
	/** @see streamIO.integer.jdbc.dbTest.IDbTest#test()	
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
