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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:54:11Z
 * digest: e10d26ad8769d0ecb3461eb97eb99503172480d06ddffe812fa174c1efcaa207
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface IDbStringValue {

	/** 
	 * should comply with the toString() Method. 
	 * @return the Value of this Field
	 * @throws SQLException
	 */
	public String getString() throws SQLException; 
	
	/**
	 * Sets this field's value.
	 * @param value the new Value
	 * @return the old Value of this Field
	 * @throws SQLException when this is not a DB Field
	 */
	public String setString(final String value) throws SQLException;
	
}
