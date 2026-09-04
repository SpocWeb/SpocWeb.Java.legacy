package knowledge;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
 

/**
 * IPersistAble.java
 * Functional Interface for persisting Data (into a DB)
 * The Mechanism (Stored Procedure, Prepared Statement or simple Execute) 
 * is left open, even the Storage Format, 
 * so this could be used for something else but relational DBs.
 *
 * This is a higher Level Interface than in PersistAble!
 * It throws SQLExceptions because these can carry Links to other Exceptions. 
 * 
 * @see PersistAble implementing a generic Approach for loading and saving Objects
 * into a relational Table. 
 *
 * Created on 6. Mai 2001, 22:58
 *
 * @author  Matthias Heuer
 * @version 
 */
public interface IPersistAble { 
	
	/** Overloaded empty Constructor. 
	  * Since the primary key cannot be set, it cannot be saved to the DB! 	 */
//	public Object newInstance(); 
	
	/** DB Table Name for this Class */
	String TableName(); 
	
	/** Sets the Fields of this Object from the current Row in the ResultSet	 */
	void setFields(ResultSet rs) throws SQLException; 
	
	/** Returns the Primary key Where Condition of this Table  */
	String primaryKeyCondition(); 
	
	/** Retrieve this Object from the DB.
	 *  The primary key must be set for this,
	 *  otherwise an IllegalStateException is thrown
	 *  Returns null when not found.  */
	Vector selectFromDB (String Condition) throws SQLException; 
	
	/** Retrieve this Object from the DB. 
	  * The primary key must be set for this, 
	  * otherwise an IllegalStateException is thrown
	  * Returns null when not found. */
	IPersistAble selectFromDB() throws SQLException; 
	
	/** Save (Insert or Update) this Object into the DB
	  * Returns true when inserted, false when updated */
	boolean saveIntoDB() throws SQLException; 
	
	/** Insert this Object into the DB
	  * Returns true when inserted, false when updated */
	boolean insertIntoDB() throws SQLException; 
	
	/** Updates this Object in the DB
	  * Returns true when inserted, false when updated */
	boolean updateInDB() throws SQLException; 
	
	/** Delete this Object from the DB
	  * Returns true when it could be deleted.  */
	boolean deleteFromDB() throws SQLException;
	
}
