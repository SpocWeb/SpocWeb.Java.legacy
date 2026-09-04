/*
 * File Name: CallStatementSep.java
 * Created on: 15.08.2003
 *
 */
package streamIO.object.parser.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import streamIO.integer.jdbc.ACallStatement;

/**
 * Title: CallStatementSep<p>
 * Description:
 * Purpose:
 * CallableStatement Implementation for Separated Files. 
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
 */
public class CallStatementSep
	extends  ACallStatement {

	/** @see streamIO.integer.jdbc.APrepStatement#getResultSet(java.io.File, java.lang.String)	 */
	protected ResultSet getResultSet(File table, String tableName) throws SQLException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @param conn
	 * @param sql_
	 * @param resultSetType
	 * @param resultSetConcurrency
	 * @param resultSetHoldability
	 */
	public CallStatementSep(ConnectionSep conn, String sql_, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
		super(conn, sql_, resultSetType, resultSetConcurrency, resultSetHoldability);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param conn
	 * @param sql
	 * @param resultSetType
	 * @param resultSetConcurrency
	 */
	public CallStatementSep(ConnectionSep conn, String sql, int resultSetType, int resultSetConcurrency) {
		super(conn, sql, resultSetType, resultSetConcurrency);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param conn
	 * @param sql
	 */
	public CallStatementSep(ConnectionSep conn, String sql) {
		super(conn, sql);
		// TODO Auto-generated constructor stub
	}

}
