package knowledge;

import java.lang.reflect.Field;

/**
  * Describes a class to {@link DBObjectFactory} well enough for it to be loaded and saved
  * generically: its table, its fields, and how those fields are named in the database.
  *
  * <p>The object stays passive - it issues no SQL of its own, which is the whole
  * difference from {@link IPersistAble} - and no method here throws, because nothing here
  * touches a connection.
  *
  * <p><b>Invariant:</b> {@link #Fields()}, {@link #FieldNames()} and {@link #DBFieldNames()}
  * are three parallel views of the same columns and must agree in order and length;
  * {@link #ForeignKeyCondition()} additionally assumes the {@code <TableName>ID} naming
  * convention and is wrong for anything that departs from it, such as parallel relations.
  *
  * PersistAble
  * Interface for Classes that can be generically persisted into a relational DataBase
  * by knowing their Table and Field Names.
  *
  * @see DBObjectFactory uses this Interface to load and save Objects through a DBConnection
  * @see IPersistAble which is an alternative Approach for loading and saving Objects
  *
  *
  * Created on 6. Mai 2001, 19:04
  *
  * @author  Matthias Heuer
  * @version
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T08:11:04Z
  * digest: 1d1e78689fda5fe65676aee605f05e04244529374a40435dae01f5cb9a9855f1
  * stale: false
  * -->
  */
public interface PersistAble { 
//extends IPrimaryKey { //don't rely on that!

	/** Overloaded Constructor initializing by the current Record in the ResultSet	 */
//	public PersistAble newInstance(ResultSet RS) throws SQLException; 
	
	/** Overloaded Constructor initializing by the primary key
	  * This has to be complemented by the corresponding Constructor! */
	PersistAble newInstance(IPrimaryKey Key); 
	
	/** DB Table Name for this Class */
	String TableName(); 
	
	/** Array of the Field Objects for this Class, including Parent Fields */
	Field[] Fields(); 
	
	/** String Constants for the Field Names */
	String[] FieldNames(); 
	
	/** String Constants for the DBField Names */
	String[] DBFieldNames(); 
	
	/** Returns an Object representing the Primary key
	  * Not really necessary: usually the Object itself represents it's primary key */
	IPrimaryKey primaryKey(); 
	
	/** Returns the foreign key Condition, 
	  * i.e. the Condition for Objects holding a foreign key to this Object, 
	  * but only for standard Naming: i.e. "<TableName>ID", 
	  * which is not sufficient for e.g. parallel Relations. */
	String ForeignKeyCondition(); 

	/**
	 * Unused placeholder left over from a modelling-tool template, referenced nowhere in the
	 * tree.
	 */
    int attribute1 = 0;
}
