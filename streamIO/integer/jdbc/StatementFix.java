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
 * Concrete fixed-length-table {@link AStatement}; its {@link #getResultSet(File, String)}
 * factory builds a {@link ResultSetFix} over the given table, like the sibling
 * {@code CallStatementFix} and {@code PrepStatementFix}.
 *
 * <h2>Collaborators</h2>
 *
 * | Type | Relationship |
 * |---|---|
 * | {@link AStatement} | Superclass supplying the SQL parser/evaluator this statement runs on. |
 * | {@link ConnectionFix} | Connection type accepted by every constructor. |
 * | {@link ResultSetFix} | Result set implementation built by {@link #getResultSet(File, String)}. |
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see streamIO.object.parser.jdbc.StatementSep
 * @see AStatement the superclass
 * @see ConnectionFix
 * @see ResultSetFix the ResultSet implementation used by this Statement
 * <!-- docstate
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: 4}
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
