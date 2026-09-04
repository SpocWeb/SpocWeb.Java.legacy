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
 * Title: PrepStatementSep<p>
 * Description:
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
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
 *
 */
public class PrepStatementSep
	extends APrepStatement //Sep
	implements PreparedStatement {

	public PrepStatementSep(ConnectionSep conn, String sql_, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
		super(conn, sql_, resultSetType, resultSetConcurrency, resultSetHoldability);
		// TODO Auto-generated constructor stub
	}

	public PrepStatementSep(ConnectionSep conn, String sql, int resultSetType, int resultSetConcurrency) {
		super(conn, sql, resultSetType, resultSetConcurrency);
		// TODO Auto-generated constructor stub
	}

	public PrepStatementSep(ConnectionSep conn, String sql) {
		super(conn, sql);
		// TODO Auto-generated constructor stub
	}

	/** @see streamIO.integer.jdbc.AStatement#getResultSet(java.io.File, java.lang.String)	 */
	protected ResultSet getResultSet(File table, String tableName) throws SQLException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
