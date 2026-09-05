package knowledge;

import java.lang.reflect.Field;
 

/**
 * Identifies one persistent record, both as the Java fields making up the key and as the
 * SQL condition selecting it.
 *
 * <p><b>Invariant:</b> an implementor must override {@code equals} and {@code hashCode},
 * because keys are used as map keys internally; a key that inherits identity semantics
 * silently defeats every lookup rather than failing loudly.
 *
 * <p>{@link #Keys()}, {@link #KeyNames()} and {@link #DBKeyNames()} are three parallel
 * views of the same key columns - reflected fields, Java names, database names - and are
 * expected to agree in order and length.
 *
 * IPrimaryKey.java
 *
 * Created on 8. Mai 2001, 23:41
 *
 * Interface for a Primary key Object.
 * this is e.g. returned by the primaryKey() Method of PersistAble. 
 * Since the primary key is a part of the Record, 
 * you can derive the Record from the key
 * 
 * Any Primary key must redefine the equals() and hashCode() Methods, 
 * because it is used for Organizing it internally too. 
 * 
 * @author  Matthias Heuer
 * @version
 * @see PersistAble the record type whose {@code primaryKey()} returns such a key
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:08:35Z
 * digest: fdda815f85185d9922e37b2cc1d6b205a355d68f33a5f153ff736e8a22d68573
 * stale: false
 * -->
 */
public interface IPrimaryKey {
	
	/** Array of Class Objects for the Constructor	 */
//	public Class[] KeyTypes(); //not necessary, use the PrimaryKey Constructor instead!
	
	/** Array of the Field Objects for this Class, including Parent Fields */
	Field[] Keys(); 
	
	/** String Constants for the Field Names */
	String[] KeyNames(); 
	
	/** String Constants for the DBField Names */
	String[] DBKeyNames(); 
	
	/** Returns a String representing the SQL Condition for this Primary key */ 
	String Condition();

	/**
	 * Unused placeholder left over from a modelling-tool template, referenced nowhere in the
	 * tree.
	 */
    int attribute1 = 0;
	
	/** Overloaded Constructor initializing by the current Record in the ResultSet	 */
//	public IPrimaryKey newInstance(ResultSet RS) throws SQLException; 
	
	/** Overloaded Constructor initializing by this Primary key. 
	  * This is to enforce a Constructor with the Primary key.	 */
//	public IPrimaryKey newInstance(IPrimaryKey Key); 
	
}
