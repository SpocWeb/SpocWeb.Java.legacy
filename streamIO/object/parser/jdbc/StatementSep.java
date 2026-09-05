/*
 * File Name: StatementSep.java
 * Created on: 15.08.2003
 *
 */
package streamIO.object.parser.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import streamIO.integer.jdbc.AStatement;

/**
 * Parses simple Queries without Fields and without Conditions.
 * Basically it resolves the Table Name based on the Settings in the Connection:
 * Path, Suffix and Separators.
 *
 * Provides a Statement Implementation for the jdbc 1.0 Framework
 * defaults all Interface Implementations to the Classes of this Package.
 *
 * Design Decisions / Implementation Details:
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
 *
 * <!-- docstate
 * tags: [code/jdbc_adapter, code/sax_event_generation]
 * concepts: [Minimal JDBC Driver over Separated-Format Flat Files]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class StatementSep 
extends AStatement {

	///////////////////////////////////////////////////////////////////////////
	// Properties of this Statement Type
	///////////////////////////////////////////////////////////////////////////
	
	/** Constructor	 */
	public StatementSep(final ConnectionSep _conn) { super(_conn); }

	/** Constructor	 */
	public StatementSep(final ConnectionSep _conn, final int resultSetType, final int resultSetConcurrency) {
		super(_conn, resultSetType, resultSetConcurrency);
	}

	/** Constructor	 */
	public StatementSep(final ConnectionSep _conn, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) {
		super(_conn, resultSetType, resultSetConcurrency, resultSetHoldability); 
	}

	/** @see streamIO.integer.jdbc.AStatement#getResultSet(java.io.File)	 */
	protected ResultSet getResultSet(final File table, final String tableName) throws IOException, SQLException {
		final ResultSetSep ret = new ResultSetSep(table, conn.separators, this, tableName); //
		if (conn.rowFieldNames == 0) {
			final String[] fieldNames    = ret.getAllFields();
			if (conn.rowFieldDefaults == 1) 
				ret.next();
			ret.init(fieldNames.length, fieldNames);
		}
		return ret; 
	}
	
}
