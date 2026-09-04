package knowledge;

import java.lang.reflect.Field;
 

/**
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

    int attribute1 = 0;
	
	/** Overloaded Constructor initializing by the current Record in the ResultSet	 */
//	public IPrimaryKey newInstance(ResultSet RS) throws SQLException; 
	
	/** Overloaded Constructor initializing by this Primary key. 
	  * This is to enforce a Constructor with the Primary key.	 */
//	public IPrimaryKey newInstance(IPrimaryKey Key); 
	
}
