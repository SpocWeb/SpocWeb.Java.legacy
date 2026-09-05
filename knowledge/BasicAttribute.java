package knowledge;

import java.lang.reflect.Field;
import java.sql.SQLException;
 

/**
 * Base class for a primitive attribute value hanging off a subject {@link Objekt} in a 1:N
 * relation, typed by a {@link Type} and stamped with a {@link Status}.
 *
 * <p>It deliberately does not extend {@link AttributeObject}: an attribute needs no
 * identity or description of its own, so the triple (type, subject, status) is both its
 * data and its primary key - which is why the class implements {@link IPrimaryKey} and
 * {@link PersistAble} and returns {@code this} from {@link #primaryKey()}.
 *
 * <p>The three object references are resolved lazily through {@link DBObjectFactory} and
 * cached in place, so the first accessor call may hit the database and later ones do not.
 * Subclasses add the actual value and its table name; this class reports an empty
 * {@link #TableName()} because it is never persisted on its own.
 *
 * This Class is the Base Class for primitive Attributes.
 * It references an Object, has a Type and a Status
 *
 * Design Decisions:
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:12:43Z
 * digest: 33b966511689c9b9caed04d6f16b44d712582f3fef5f9ca6fcf4b2b07ad75b8a
 * stale: false
 * -->
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
            // TODO: LOGIC: keyed by StatusID, not TypeID, so this resolves and caches the
            // Status row as this attribute's Type whenever the two IDs differ - which is the
            // normal case; getStatus() one method above uses the same key correctly.
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
