package knowledge;

import java.lang.reflect.Field;

/**
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

    int attribute1 = 0;	
}
