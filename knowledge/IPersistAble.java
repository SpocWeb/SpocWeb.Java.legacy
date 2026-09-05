package knowledge;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
 

/**
 * Lets an object load, save and delete itself, leaving the storage mechanism entirely to
 * the implementor.
 *
 * <p>This is the active counterpart to {@link PersistAble}: there, an object merely
 * describes its table and fields and {@link DBObjectFactory} does the work; here the
 * object performs its own SQL. The two are alternatives, not layers, and a class picks
 * one.
 *
 * <p><b>Invariant:</b> every method except {@link #TableName()} requires the primary key
 * to be set, and the read methods return {@code null} rather than throwing when nothing
 * matches - so a null result means "not found", not "not attempted".
 *
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:10:52Z
 * digest: 9e9b4bb3b0c2bb592006bbca3e036b6c4fbeecab5a7cb185450dc9775c37865c
 * stale: false
 * -->
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
	  * Returns true when a row was updated, false when none matched */
	boolean updateInDB() throws SQLException;
	
	/** Delete this Object from the DB
	  * Returns true when it could be deleted.  */
	boolean deleteFromDB() throws SQLException;
	
}
