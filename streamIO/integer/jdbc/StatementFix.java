/*
 * File Name: StatementFix.java
 * Created on: 15.08.2003
 *
 */
package streamIO.integer.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Title: StatementFix<p>
 * Description:
 * Purpose:
 * Provides a Statement Implementation for the jdbc 1.0 Framework
 * defaults all Interface Implementations to the Classes of this Package.  
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see streamIO.object.parser.jdbc.StatementSep
 * <!-- docstate
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class StatementFix 
extends AStatement {
    
	static final int resultSetTypeDefault = ResultSet.TYPE_SCROLL_SENSITIVE; // FORWARD_ONLY;
	static final int resultSetConcurrencyDefault = ResultSet.CONCUR_UPDATABLE; // READ_ONLY; 
	static final int resultSetHoldabilityDefault = ResultSet.HOLD_CURSORS_OVER_COMMIT; // CLOSE_CURSORS_AT_COMMIT;
	
	/** Constructor	 */
	public StatementFix(final ConnectionFix conn_) {
		this(conn_, resultSetTypeDefault, resultSetConcurrencyDefault, resultSetHoldabilityDefault);
	}

	/** Constructor	 */
	public StatementFix(final ConnectionFix conn_, final int resultSetType, final int resultSetConcurrency) {
		this(conn_, resultSetType, resultSetConcurrency, resultSetHoldabilityDefault);
	}

	/** Constructor	 */
	public StatementFix(final ConnectionFix _conn, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) {
		super(_conn); 
	}

	/**This is, in Fact just a Factory Method for it's Parent Implementation.  
	 * @see streamIO.integer.jdbc.AStatement#getResultSet(java.io.File, String)	 */
	protected ResultSet getResultSet(final File table, final String tableName) throws IOException, SQLException {
		return new ResultSetFix(table, this, tableName);
	}

}
