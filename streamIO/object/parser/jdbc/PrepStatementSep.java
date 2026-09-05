/*
 * File Name: PrepStatementSep.java
 * Created on: 15.08.2003
 *
 */
package streamIO.object.parser.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import streamIO.integer.jdbc.APrepStatement;

/**
 * PreparedStatement Implementation for Separated Files.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * tags: [code/jdbc_adapter, code/sax_event_generation]
 * concepts: [Minimal JDBC Driver over Separated-Format Flat Files]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class PrepStatementSep
	extends APrepStatement //Sep
	implements PreparedStatement {

	/** Initializing Constructor forwarding the full JDBC ResultSet Configuration to the base Class.
	  * @param conn the separated-Files Connection to run this Statement against
	  * @param sql_ the parameterized SQL Text
	  * @param resultSetType one of the ResultSet TYPE_* Constants
	  * @param resultSetConcurrency one of the ResultSet CONCUR_* Constants
	  * @param resultSetHoldability one of the ResultSet HOLD_CURSORS_OVER_COMMIT / CLOSE_CURSORS_AT_COMMIT Constants	 */
	public PrepStatementSep(ConnectionSep conn, String sql_, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
		super(conn, sql_, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/** Initializing Constructor defaulting the ResultSet Holdability to the base Class' Default.
	  * @param conn the separated-Files Connection to run this Statement against
	  * @param sql the parameterized SQL Text
	  * @param resultSetType one of the ResultSet TYPE_* Constants
	  * @param resultSetConcurrency one of the ResultSet CONCUR_* Constants	 */
	public PrepStatementSep(ConnectionSep conn, String sql, int resultSetType, int resultSetConcurrency) {
		super(conn, sql, resultSetType, resultSetConcurrency);
	}

	/** Initializing Constructor defaulting ResultSet Type, Concurrency and Holdability.
	  * @param conn the separated-Files Connection to run this Statement against
	  * @param sql the parameterized SQL Text	 */
	public PrepStatementSep(ConnectionSep conn, String sql) {
		super(conn, sql);
	}

	// TODO: LOGIC: unimplemented stub - always returns null instead of the requested Table's
	// ResultSet or throwing an explicit "not implemented" Exception.
	/** @see streamIO.integer.jdbc.AStatement#getResultSet(java.io.File, java.lang.String)	 */
	protected ResultSet getResultSet(File table, String tableName) throws SQLException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
