/*
 * Created on 25.03.2005
 *
 * Interface to read and Write String Values in Databases
 */
package streamIO.integer.jdbc;

import java.sql.SQLException;

/**
 * Interface to read and Write String Values in Databases
 * @author heuerm
 * 
 */
public interface IDbStringValue {

	/** 
	 * should comply with the toString() Method. 
	 * @return the Value of this Field
	 * @throws SQLException
	 */
	public String getString() throws SQLException; 
	
	/** 
	 * @param value the new Value 
	 * @return the old Value of this Field
	 * @throws SQLException when this is not a DB Field 
	 */
	public String setString(final String value) throws SQLException; 
	
}
