/*
 * Created on 14.03.2005
 *
 * Implements a Row Filter Condition that tests for the Equality of two Fields. 
 */
package streamIO.integer.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implements a Row Filter Condition that tests for the Equality of two Fields. 
 * Full Flexibility could be achieved by handing over the Field Comparator, 
 * but then you could simply use ITester.  
 * @author heuerm
 *
 */
public class EqualCondition 
implements IJoinCondition {

	final int col1;
	final int col2; 
	final boolean ignoreCase; 
	final boolean trim; 
	
	/**
	 * initializing Constructor
	 * @param _col1
	 * @param _col2
	 */
	public EqualCondition(final int _col1, final int _col2) {
		this(_col1, _col2, false, false); 
	}

	/**
	 * initializing Constructor
	 * @param _col1
	 * @param _col2
	 * @param _ignoreCase
	 */
	public EqualCondition(final int _col1, final int _col2, final boolean _ignoreCase) {
		this(_col1, _col2, _ignoreCase, false); 
	}

	/**
	 * initializing Constructor
	 * @param _col1
	 * @param _col2
	 * @param _ignoreCase
	 * @param _trim
	 */
	public EqualCondition(final int _col1, final int _col2, final boolean _ignoreCase, final boolean _trim) {
		super();
		this.ignoreCase = _ignoreCase; 
		this.trim = _trim; 
		this.col1 = _col1; 
		this.col2 = _col2; 
	}

	/** 
	 * @return true when two Colums of the ResultSet are equal.  
	 * @see streamIO.integer.jdbc.IJoinCondition#equals(java.sql.ResultSet, java.sql.ResultSet)	 
	 */
	public boolean equals(final ResultSet rs1, final ResultSet rs2) throws SQLException {
		String str1 = rs1.getString(col1); //use the Strings...
		String str2 = rs2.getString(col2); 
		if (ignoreCase) {
			str1 = str1.toUpperCase(); 
			str2 = str2.toUpperCase(); 
		}
		if (trim) {
			str1 = str1.trim(); 
			str2 = str2.trim(); 
		}
		return str1.equals(str2); 
	}
	
	/** @see tester.IEquivalence#equals(java.lang.Object, java.lang.Object)	 */
	public boolean equals(final Object A, final Object B) {
		try {
			return equals((ResultSet) A, (ResultSet) B);
		} catch (final SQLException x) {
			throw new RuntimeException(x); 
		}
	}
	
	/** @see tester.IEquivalence#HashCode(java.lang.Object)	 */
	public int HashCode(final Object A) {
		throw new RuntimeException("Not well defined!"); 
	}
	
}
