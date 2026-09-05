/*
 * Created on 14.03.2005
 *
 * ResultSet which optimizes the left Join 
 */
package streamIO.integer.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import streamIO.Log;
import streamIO.integer.jdbc.dbTest.DbTestEquals;
import streamIO.integer.jdbc.dbTest.IDbTest;
import streamIO.object.parser.jdbc.ResultSetSep;
import stringOp.PatriciaNode;
import function.index.AIndexer;
import function.index.IIndexer;

/**
 * ResultSet which optimizes the left Join 
 * by reading the (Positions) of the second ResultSet 
 * into an Index (HashMap, sorted Array or sorted Tree)  
 * based on the given Column(s). 
 * HashMap Access is direct and faster than a binary Search on an Index, 
 * but it costs more Memory. 
 * An Array can be even faster in Retrieval due to Interpolation, 
 * when the Keys are equally distributed! 
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T22:02:44Z
 * digest: 2a7b60c76e6a32ce2b941bc61e99ff84bf1f9bc911c87c07436fe8a16be87524
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class ResultSetLeftJoin 
extends ResultSetCrossJoin {
	
	private static final Log L = new Log(ResultSetLeftJoin.class); 
	
	/**
	 * creates an Index on the given ResultSet
	 * Using a PatriciaNode is faster (esp. for long Keys) AND 
	 * it also sorts the Items. 
	 * @param _rsFind the ResultSet to index 
	 * @param _colFind the columns to index by 
	 * @return the Number of Rows indexed 
	 * @throws SQLException when accessing the ResultSet fails
	 */
	final static public IIndexer CREATE_INDEX(final ResultSet rsFind, final int[] colFind) throws SQLException {
		final AIndexer index = new PatriciaNode(); //Indexer(Math.max(rsFind.getFetchSize(), 25)); 
		index.update(rsFind, colFind); 
		return index; 
	}
	
	/**
	 * returns a Matrix of 2 Lists of Field Positions in the ResultSet
	 * @param _rsFind the ResultSet for the second List 
	 * @param conditions Array of DB Conditions 
	 * @return a Matrix of 2 Lists of Field Positions in the ResultSet
	 * @throws SQLException when the Conditions contain other than "=" Tests. 
	 */
	final static public int[][] CONDITION_POSITION(final ResultSet _rsFind, final IDbTest[] conditions) 
	throws SQLException {
		final int[][] fieldPositions = new int[2][conditions.length]; 
		for(int i = conditions.length; --i >= 0;) {
			final IDbTest condition = conditions[i]; 
			if (condition.getOperator() != DbTestEquals.OPERATOR)
				throw new SQLException("Only EquiJoins allowed!"); 
			final DbColumn col0 = condition.getOperand0(); 
			final DbColumn col1 = condition.getOperand1();
			int offset = (col0.table == _rsFind ? 1 : 0); 
			fieldPositions[  offset][i] = col0.position; 
			fieldPositions[1-offset][i] = col1.position; 
		}
		return fieldPositions; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Member Variables
	///////////////////////////////////////////////////////////////////////////
	
	/** Flag whether to use an outer Join	 */
	final public boolean outer; 
	
	/** Columns used to match ResultSets 	 */
	protected final int[] colIter; 
	
	/** Columns used to match ResultSets 	 */
	protected final int[] colFind; 
	
	/** the Index, used to look up the Rows 	 */
	final IIndexer index; 
	
	/** made a Member Variable to avoid Instatiation 	 */
	final transient StringBuffer buf = new StringBuffer(); 
	/** to allow for a Full Outer Join, a BitSet could check whether any Rows of the right Table are not matched */
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializing constructor performing an inner join.
	 * @param _rsIter the ResultSet to iterate over, typically the Many-Side of a Relation
	 * @param _rsFind the ResultSet to find Matches in, typically the Lookup/One-Side of a Relation
	 * @param _colIter the Colums to match in _rsIter, typically the Set of Foreign Keys
	 * @param _colFind the Colums to match in _rsFind, typically the Set of Primary Keys
	 * @throws SQLException when the Condition contains other than '=' Criteria or the Number of Keys does not match.
	 */
	public ResultSetLeftJoin(final ResultSet _rsIter,
			final ResultSet _rsFind, final int[] _colIter, final int[] _colFind)
	throws SQLException {
		this(_rsIter, _rsFind, _colIter, _colFind, false);
	}
	
	/**
	 * Initializing constructor letting the caller choose inner or outer join.
	 * @param _rsIter the ResultSet to iterate over, typically the Many-Side of a Relation
	 * @param _rsFind the ResultSet to find Matches in, typically the Lookup/One-Side of a Relation
	 * @param _colIter the Colums to match in _rsIter, typically the Set of Foreign Keys
	 * @param _colFind the Colums to match in _rsFind, typically the Set of Primary Keys
	 * @param _outer Flag whether to use an outer Join
	 * @throws SQLException when the Condition contains other than '=' Criteria or the Number of Keys does not match.
	 */
	public ResultSetLeftJoin(final ResultSet _rsIter,
			final ResultSet _rsFind, final int[] _colIter, final int[] _colFind
			, final boolean outer)
	throws SQLException {
		this(_rsIter, _rsFind, _colIter, _colFind, outer, false); 
	}
	
	/**
	 * Initializing constructor with an explicit right-join flag, no statement reference.
	 * @param _rsIter the ResultSet to iterate over, typically the Many-Side of a Relation
	 * @param _rsFind the ResultSet to find Matches in, typically the Lookup/One-Side of a Relation
	 * @param _colIter the Colums to match in _rsIter, typically the Set of Foreign Keys
	 * @param _colFind the Colums to match in _rsFind, typically the Set of Primary Keys
	 * @param _outer Flag whether to use an outer Join
	 * @param _right Flag for a right outer Join; swaps the Sequence of the ResultSet Column Sets.
	 * @throws SQLException when the Condition contains other than '=' Criteria or the Number of Keys does not match.
	 */
	public ResultSetLeftJoin(final ResultSet _rsIter,
			final ResultSet _rsFind, final int[] _colIter, final int[] _colFind
			, final boolean outer, final boolean right)
	throws SQLException {
		this(_rsIter, _rsFind, _colIter, _colFind, outer, right, null); 
	}
	
	/**
	 * Initializing constructor deriving the match columns from parsed {@code '='} conditions.
	 * @param _rsIter the ResultSet to iterate over, typically the Many-Side of a Relation
	 * @param _rsFind the ResultSet to find Matches in, typically the Lookup/One-Side of a Relation
	 * @param _conditions the List of '=' Condition in to match,
	 * typically the Set of Foreign Keys against the Set of Primary Keys.
	 * @param _outer Flag whether to use an outer Join
	 * @param _right Flag for a right outer Join; swaps the Sequence of the ResultSet Column Sets.
	 * @param _statement Reference to the Statement that created this ResultSet.
	 * @throws SQLException when the Condition contains other than '=' Criteria or the Number of Keys does not match.
	 */
	public ResultSetLeftJoin(final ResultSet _rsIter, final ResultSet _rsFind,
			final IDbTest[] _conditions,
			final boolean _outer, final boolean _right, 
			final Statement _statement) 
	throws SQLException {
		this(new ResultSet[] {_rsIter, _rsFind}, 
				CONDITION_POSITION(_rsFind, _conditions), _outer, _right, _statement); 
	}
	
	/**
	 * Initializing constructor with an explicit statement reference; delegates to the
	 * array-based constructor and logs at debug level.
	 * @param _rsIter the ResultSet to iterate over, typically the Many-Side of a Relation
	 * @param _rsFind the ResultSet to find Matches in, typically the Lookup/One-Side of a Relation
	 * @param _colIter the Colums to match in _rsIter, typically the Set of Foreign Keys
	 * @param _colFind the Colums to match in _rsFind, typically the Set of Primary Keys
	 * @param _outer Flag whether to use an outer Join
	 * @param _right Flag for a right outer Join; swaps the Sequence of the ResultSet Column Sets.
	 * @param _statement Reference to the Statement that created this ResultSet.
	 * @throws SQLException when the Condition contains other than '=' Criteria or the Number of Keys does not match.
	 */
	public ResultSetLeftJoin(final ResultSet _rsIter, final ResultSet _rsFind,
			final int[] _colIter, final int[] _colFind,
			final boolean _outer, final boolean _right, 
			final Statement _statement) 
	throws SQLException {
		this(new ResultSet[] {_rsIter, _rsFind}, 
			 new int[][] {_colIter, _colFind}, _outer, _right, _statement); 
		L.debug("Empty Constructor"); 
	}
	
	/**
	 * Root initializing constructor: builds the {@link IIndexer} over the lookup side's
	 * match columns and swaps sides when {@code _right} is set.
	 * @param _rsIter the ResultSet to iterate over
	 * @param _rsFind the ResultSet to find Matches in
	 * @param _colIter the Colums to match in _rsIter
	 * @param _colFind the Colums to match in _rsFind
	 * @param _outer Flag whether to use an outer Join
	 * @param _right Flag for a right outer Join; swaps the ResultSets.
	 * @param _statement Reference to the Statement that created this ResultSet.
	 * @throws SQLException
	 */
	public ResultSetLeftJoin(final ResultSet[] _rs, final int[][] _cols,
			final boolean _outer, final boolean _right, 
			final Statement _statement) 
	throws SQLException {
		super(_rs[_right ? 1 : 0], _rs[_right ? 0 :1], _right, _statement); 
		if(_cols[0].length != _cols[1].length) 
			throw new SQLException("Match Criteria Field List Lengths don't match:");
		this.outer = _outer; 
		this.colIter = _cols[_right ? 1 : 0]; 
		this.colFind = _cols[_right ? 0 : 1]; 
		index = CREATE_INDEX(rsFind, colFind); 
		//L.n(index.toString()); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Advances {@code rsIter} and positions {@code rsFind} on the matching row found via
	 * {@link #index}, or on the null record when no match exists and this is an outer join.
	 * @see streamIO.integer.jdbc.AResultSet#readNext()
	 */
	public boolean next() throws SQLException {
		for(;rsIter.next();) {
			buf.setLength(0); 
			for (int i =-1; ++i < colIter.length; ) 
				buf.append(rsIter.getString(colIter[i])); //Sequence is Precedence in sorting
			final int ndx = index.getIndexOf(buf); 
			//Find the exact matching Row in rsFind
			if (ndx >= 0) {
				if (rsFind.absolute(ndx)) 
					return true; //
			} else if (outer) {
				rsFind.beforeFirst(); //set rsFind to a null Record
				return true; //
			}
		}
		return false;
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Manual smoke test joining two {@link ResultSetSep} test files and printing the result.
	 */
	public static void main(final String[] args) throws Exception {
		try {
		final ResultSetSep rsIter = new ResultSetSep(ResultSetSep.TEST_FILE_PATH+"CDs.tab"); 
		final ResultSetSep rsFind = new ResultSetSep(ResultSetSep.TEST_FILE_PATH+"Artists.tab"); 
		final ResultSetLeftJoin rslj = new ResultSetLeftJoin(rsIter, rsFind, new int[]{6}, new int[]{1}, true, true); 
		PRINT_RS(rslj);
		} finally {
		}
	}
	
}
