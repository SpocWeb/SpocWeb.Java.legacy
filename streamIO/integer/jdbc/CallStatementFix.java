/*
 * File Name: CallStatementFix.java
 * Created on: 15.08.2003
 *
 */
package streamIO.integer.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Concrete fixed-length-table {@link ACallStatement}; does not override any of its inherited
 * stub {@code CallableStatement} methods, and its own {@link #getResultSet(File, String)}
 * factory is itself an unimplemented stub - see the {@code TODO: LOGIC} marker there.
 *
 * <h2>Collaborators</h2>
 *
 * | Type | Relationship |
 * |---|---|
 * | {@link ACallStatement} | Superclass supplying the (unimplemented) {@code CallableStatement} contract. |
 * | {@link ConnectionFix} | Connection type accepted by every constructor. |
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see ACallStatement the superclass
 * @see ConnectionFix
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:55:55Z
 * digest: e313ea4ca6838be7b7800937fb648b96d8c6e39116cedab9b9ab72ff5487224a
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class CallStatementFix
	extends ACallStatement {

	// TODO: LOGIC: getResultSet(File, String) is itself an unimplemented stub returning
	// null, and CallStatementFix overrides none of ACallStatement's stub get*/set*/
	// registerOutParameter methods either - every CallableStatement operation on this
	// concrete class silently returns null/false/0 instead of doing real work.
	/**
	 * Stub override; not implemented and always returns null.
	 * @see streamIO.integer.jdbc.APrepStatement#getResultSet(java.io.File, java.lang.String)
	 */
	protected ResultSet getResultSet(File table, String tableName) throws SQLException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Initializing constructor delegating to {@link ACallStatement}'s matching constructor.
	 * @param conn
	 * @param sql_
	 * @param resultSetType
	 * @param resultSetConcurrency
	 * @param resultSetHoldability
	 */
	public CallStatementFix(ConnectionFix conn, String sql_, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
		super(conn, sql_, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/**
	 * Initializing constructor using the default result-set holdability.
	 * @param conn
	 * @param sql
	 * @param resultSetType
	 * @param resultSetConcurrency
	 */
	public CallStatementFix(ConnectionFix conn, String sql, int resultSetType, int resultSetConcurrency) {
		super(conn, sql, resultSetType, resultSetConcurrency);
	}

	/**
	 * Initializing constructor using the default result-set type, concurrency and holdability.
	 * @param conn
	 * @param sql
	 */
	public CallStatementFix(ConnectionFix conn, String sql) {
		super(conn, sql);
	}

}
