/*
 * Filters and translates the Field Indices. 
 * Created on 13.03.2005
 *
 */
package streamIO.integer.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import math.vector.VectorString;


/**
 * Filters and translates the field indices of a {@link ResultSet}, presenting a chosen
 * subset of columns - optionally renamed, and with {@code *} expanded to every column -
 * as its own {@link DbColumn} array.
 *
 * @author heuerm
 * @see DbColumn
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T22:01:10Z
 * digest: 4ac35525d6dfe29f24f80fc610670ac1b3853bbc01f274c580cf81d9cbf0e601
 * stale: false
 * tags: [code/jdbc_adapter, code/database_access, code/database_driver]
 * concepts: [Filesystem-Backed JDBC Driver Framework with Fixed-Length and Separator-Delimited Table Storage]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class FilterRsCols
extends FilterResultSet {

	/**
	 * Unused entry point; performs no action.
	 */
	public static void main(final String[] args) {
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** String to parse by */
	final static public String STR_AS = " AS "; 
	
	/** String to parse by */
	final static public String STR_SEP = ","; 
	
	/**
	 * pases the Field List
	 * @param fields
	 * @return two Arrays of the same Size 
	 * the first  containing the Field Names
	 * the second containing the Aliases 
	 */
	final static public String[][] parseFieldAndAliasList(final String fieldList) {
		return VectorString.PARSE_2D(fieldList, STR_SEP, STR_AS, true); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	// Member Variables 
	///////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////
	// Constructors & Initializers 
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializing constructor selecting a subset of columns by index.
	 * @param _rsIter the Base ResultSet with all Fields
	 * @param _fields the Subset of Fields to return
	 * @throws SQLException
	 */
	public FilterRsCols(final ResultSet _rsIter, final int[] _fields) throws SQLException {
		super(_rsIter); 
		final ResultSetMetaData rsMdIter = rsIter.getMetaData(); 
		//super.init(_fields.length, fieldNames);
		this.columns = new DbColumn[_fields.length];
		for (int i = _fields.length; --i >= 0; ) { //creating new Columns because of new Alias
			final int pos = _fields[i]; 
			columns[i] = new DbColumn(_rsIter, rsMdIter.getColumnName(pos), rsMdIter.getColumnLabel(pos), pos); 
		}
	}

	/**
	 * Initializing constructor selecting columns by name, using each name as its own alias.
	 * @param _rsIter the Base ResultSet with all Fields
	 * @param _fieldNames the List of selected Field-Names
	 * @throws SQLException
	 */
	public FilterRsCols(final ResultSet _rsIter, final String[] _fieldNames) throws SQLException {
		this(_rsIter, _fieldNames, _fieldNames);
	}

	/**
	 * Initializing constructor selecting columns by name with distinct aliases.
	 * @param _rsIter the Base ResultSet with all Fields
	 * @param _fieldNames the List of selected Field-Names
	 * @param _aliasNames the List of new Field-Names (must be equally long!)
	 * @throws SQLException
	 */
	public FilterRsCols(final ResultSet _rsIter, final String[] _fieldNames, final String[] _aliasNames) throws SQLException {
		super(_rsIter);
		this.columns = new DbColumn[_fieldNames.length];
		for (int i = _fieldNames.length; --i >= 0; ) //creating new Columns because of new Alias
			columns[i] = new DbColumn(_rsIter, _fieldNames[i], _aliasNames[i], _rsIter.findColumn(_fieldNames[i])); 
	}

	/**
	 * expands all Fields named '*' to the full List of Fields. 
	 * Using '*' is a bad Practice for Programming, but quite convenient for interactive use. 
	 * @param _rsIter the Base ResultSet with all Fields 
	 * @param _fieldAliases the List of selected Field-Names 
	 * @param _aliasNames the List of new Field-Names (must be equally long!)
	 * @throws SQLException
	 */
	public FilterRsCols(final ResultSet _rsIter, final DbColumn[] _fieldAliases) throws SQLException {
		super(_rsIter);
		//expand '*' Fields to all Columns
		final ArrayList list = new ArrayList(_fieldAliases.length); 
		for (int i = -1; ++i < _fieldAliases.length; ) {
			final DbColumn fieldAlias = _fieldAliases[i]; 
			if ("*".equals(fieldAlias.name)) {
				final ResultSet table = (fieldAlias.table == null) ? _rsIter : fieldAlias.table; 
				final ResultSetMetaData rsMd = table.getMetaData(); 
				for (int j = rsMd.getColumnCount(); --j >= 0; ) //creating new Columns because of new Alias
					list.add(new DbColumn(table, rsMd.getColumnName(j), rsMd.getColumnLabel(j), j)); 
			} else 
				list.add(fieldAlias); 
		}
		if (list.size() == _fieldAliases.length) 
			this.columns = _fieldAliases; 
		else {
			this.columns = new DbColumn[list.size()]; 
			for (int i = columns.length; --i >= 0; )
				columns[i] = (DbColumn) list.get(i); 
		}
	}
	
	/**redirects to the original Fields 
	 * @see java.sql.ResultSet#getString(int)	 */
	public String getString(final int columnIndex) { //throws SQLException {
		try {
			return columns[columnIndex].getString();
		} catch (final SQLException x) {
			throw new RuntimeException(x); 
		}
	}
	
}
