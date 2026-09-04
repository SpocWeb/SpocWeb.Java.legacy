package knowledge;

import java.lang.reflect.Field;
import java.sql.SQLException;
 

/**
 * This Class is the Base Class for primitive Attributes.
 * It references an Object, has a Type and a Status
 *
 * Design Decisions:
 */
public class BasicAttribute
//extends AttributeObject	//this Class already brings ID and Description with it!
implements IObject, IPrimaryKey, PersistAble {
	
	//////////////////////
	//  static Members  //
	//////////////////////
	
	/** Array of Field Names, selects only the non static, final or transient Fields	 */
	final static public Field[] Keys = DBObjectFactory.conCat(new Field[0], BasicAttribute.class.getDeclaredFields()); //
	
	/** String Constants for the Field Names */
	final static public String[] KeyNames = DBObjectFactory.getFieldNames(Keys);
	
	/** Array of Class Objects for the Constructor	 */
	final static public Class[] KeyTypes = DBObjectFactory.getFieldTypes(Keys); 
	
	/** String Constants for the DBField Names */
	final static public String[] DBKeyNames = KeyNames;
	
	//////////////////////
	//  static Methods  //
	//////////////////////
	
	///////////////
	//  Members  //
	///////////////
	
	/** Reference to the TypeID of the Object for Categorization,
	 *  usable only for one Dimensional Categories.
	 *  The rest must be done by Attributes of certain Types. */
	protected int TypeID;
	
	/** Reference to the Type of the Object for Categorization,
	 *  usable only for one Dimensional Categories.
	 *  The rest must be done by Attributes of certain Types. */
	protected Type Type;
	
	/** Reference to the SubjectID, the Object this Attribute belongs to (1:N Relation).
	 *  The Subject can be all: Attribute, Objects or Relation.
	 */
	protected int SubjectID;
	
	/** Reference to the Subject, the Object this Attribute belongs to (1:N Relation).
	 *  The Subject can be all: Attribute, Objects or Relation.
	 */
	protected Objekt Subject;
	
	/** Reference to the Status of the Type. */
	protected int StatusID;
	
	/** Reference to the Status of the Type. */
	protected Status Status;
	
	/////////////////
	//  Accessors  //
	/////////////////
	
	/** Returns the Status for this Object */
	public Status getStatus() throws SQLException { 
		if (Status == null)
            Status = (Status) DBObjectFactory.FactoryStatus.getObject(new IdKey(StatusID)); 
		return Status; }
	
	/** Returns the Type for this Object */
	public Type getType() throws SQLException { 
		if (Type == null)
            Type = (Type) DBObjectFactory.FactoryStatus.getObject(new IdKey(StatusID)); 
		return Type; }
	
	/** Returns the Subject for this Object. */
	public Objekt getSubject() throws SQLException  { 
		if (Subject == null)
            Subject = (Objekt) DBObjectFactory.FactoryObject.getObject(new IdKey(SubjectID)); 
		return Subject; }
	
	/** Returns the StatusID for this Object */
	public int getStatusID() { return StatusID; }
	
	/** Returns the TypeID for this Object */
	public int getTypeID() { return TypeID; }
	
	/** Returns the SubjectID for this Object. */
	public int getSubjectID() { return SubjectID; }
	
	////////////////////
	//  Constructors  //
	////////////////////
	
	/** Empty Constructor for Subclasses */
//	protected BasicAttribute() {}
	
	/** Overloaded Constructor initializing by the primary key 	  */
	public BasicAttribute(IPrimaryKey Key) { 
		if (Key == null) return; 
		BasicAttribute key = (BasicAttribute) Key; 
		this.SubjectID = key.SubjectID; 
		this.StatusID = key.StatusID; 
		this.TypeID = key.TypeID; 
	}
	
	/** Full Constructor for a BasicAttribute Object */
	public BasicAttribute(int TypeID, int SubjectID, int StatusID) {
		this.SubjectID = SubjectID;
		this.StatusID = StatusID;
		this.TypeID = TypeID;
	}
	
	/** Full Constructor for a BasicAttribute Object */
	public BasicAttribute(Type Type, Objekt Subject, Status Status) {
		this(Type.getID(), Subject.getID(), Status.getID()); 
		this.Subject = Subject;
		this.Status = Status;
		this.Type = Type;
	}
	
	///////////////
	//  Methods  //
	///////////////
	
	////////////////////////////////////////////////////////////////////////////
	//	Interface IPrimaryKey
	////////////////////////////////////////////////////////////////////////////
	
	/** Array of the Field Objects for this Class, including Parent Fields */
	public Field[] Keys() { return Keys; }
	
	/** String Constants for the Field Names */
	public String[] KeyNames() { return KeyNames; }
	
	/** String Constants for the DBField Names */
	public String[] DBKeyNames() { return DBKeyNames; }
	
	/** Returns a String representing the SQL Condition for this Primary key
	  * This generic Implementation can be overridden by faster hardcoded ones. */
	public String Condition () { return DBObjectFactory.Condition(this, ""); } 
	
	////////////////////////////////////////////////////////////////////////////
	//	Interface IPrimaryKey
	////////////////////////////////////////////////////////////////////////////
	
	/** Overloaded Constructor initializing by the primary key 	  */
	public PersistAble newInstance (IPrimaryKey Key) { 
		return DBObjectFactory.newInstance(this, Key); }
	
	/** Array of Class Objects for the Constructor	  */
	public Class[] KeyTypes() { return KeyTypes; } 
	
	////////////////////////////////////////////////////////////////////////////
	//	Interface IPrimaryKey
	////////////////////////////////////////////////////////////////////////////
	
	/** DB Table Name for this Class  */
	public String TableName () { return ""; } 
	
	/** Array of the Field Objects for this Class, including Parent Fields  */
	public Field[] Fields () { return Keys; } 
	
	/** String Constants for the Field Names  */
	public String[] FieldNames () { return KeyNames; }
	
	/** String Constants for the DBField Names  */
	public String[] DBFieldNames () { return DBKeyNames; }
	
	/** Returns an Object representing the Primary key
	 * Not really necessary: usually the Object itself represents it's primary key  */
	public IPrimaryKey primaryKey () { return this; }
	
	/** Returns the foreign key Condition,
	 * i.e. the Condition for Objects holding a foreign key to this Object,
	 * but only for standard Naming: i.e. "<TableName>ID",
	 * which is not sufficient for e.g. parallel Relations.  */
	public String ForeignKeyCondition () { return null; }
	
}
