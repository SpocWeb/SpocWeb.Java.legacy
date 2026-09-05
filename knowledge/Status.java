package knowledge;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
  * A named, described row identified by an inherited ID - the lifecycle state of an
  * object, and the base class most persisted types in this package derive from.
  *
  * <p>Name and description are ordinary columns rather than attributes of the knowledge
  * model, so that every persisted object has them without a join; the setters mark the
  * object dirty, which is the only write tracking this class does. Marking it dirty is all
  * they do - nothing writes the object back on its own, so a caller holding a modified
  * object must persist it through {@link DBObjectFactory} before dropping it.
  *
  * <p>{@code Fields} deliberately starts from an empty array rather than the parent's:
  * {@link IdKey}'s single column is the key, and the key is written separately from the
  * data columns.
  *
  * Describes the List of available Statuses for most Objects in this Package.
  *  Also Base Class for most Objects in this Package.
  *
  * Subclasses:
  * @see knowledge.MetaType
  *
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T08:15:05Z
  * digest: bf90ced1313c9870118de6a98b06b50c66cfa314ddc0377a1dd863ba2d8975ae
  * stale: false
  * -->
  */
public class Status
extends IdKey 
implements PersistAble, IDescriptor { //, IPersistAble {
	
	////////////////////////////////////////////////////////////////////////////
	//  static Members  
	////////////////////////////////////////////////////////////////////////////
	
	/** String Constant for the Table Name */
	final static public String STR_TableName = "Status"; //Status.class.getName();// 
	
	/** String Constant for the Field Names */
//	final static public String STR_FieldNames = "Name, Description "; 
	
	/** Array of Field Names, selects only the non static, final or transient Fields
	  * The key is left out by Construction */
	final static public Field[] Fields = DBObjectFactory.conCat(new Field[0], Status.class.getDeclaredFields()); //getFields ()};
	
	/** String Constants for the Field Names */
	final static public String[] FieldNames = DBObjectFactory.getFieldNames(Fields);
	
	/** String Constants for the DBField Names */
	final static public String[] DBFieldNames = FieldNames;
	
	////////////////////////////////////////////////////////////////////////////
	//  static Methods  
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//  Members  
	////////////////////////////////////////////////////////////////////////////
	
	/** A descriptive Name for this Status, need not be unique.
	 *  Not implemented as an Attribute to have a least Common Denominator.
	 */
	protected String Name;
	
	/** A Description of this Status. 	 */
	protected String Description;
	
	/** Reference to the Statement used for executing SQL Commands. 	 */
	public Statement StateMt;
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessors  
	////////////////////////////////////////////////////////////////////////////
	
	/** A Name for this Status.
	 *  A descriptive Name for this Object, need not be unique.
	 *  Not implemented as an Attribute to have a least Common Denominator.
	 */
	public String getName() { return Name; }
	
	/** A Description of this Status. 	 */
	public String getDescription() { return Description; }
	
	/** A Name for this Status.
	 *  A descriptive Name for this Object, need not be unique.
	 *  Not implemented as an Attribute to have a least Common Denominator.
	 */
	public void setName(String Name) { dirty = true; this.Name = Name; }
	
	/** A Description of this Status. 	 */
	public void setDescription(String Description) { dirty = true; this.Description = Description; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors 
	////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor for Subclasses */
//	protected Status() {}
	
	/** Primary key Constructor */
	public Status(IPrimaryKey Key) { super(Key); }
	
	/** key Constructor for a Status, needs to be initialized from the DB */
	public Status(int ID) { super(ID); } 
	
	/** key Constructor for a Status, needs to be initialized from the DB */
	public Status(Integer ID) { super(ID); } 
	
	/** Full Constructor for a Status */
	public Status(int ID, String Name, String Description) {
		super(ID); 
		this.Name = Name; 
		this.Description = Description; 
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Methods  
	////////////////////////////////////////////////////////////////////////////
	
	//There is deliberately no finalize() here. One used to sit at this point, empty in both
	//branches, alongside a comment claiming that these objects update the database when they
	//are destroyed - which they never did. Writing to a database from a finalizer would not
	//be a fix either: it runs on the collector's thread, arbitrarily late, and possibly never.
	//A caller that wants a modified object persisted has to say so, through the factory's
	//insertObject or updateObject.

	/** Returns the foreign key Condition for the Table determined by the Factory, 
	  * i.e. the Condition for Objects holding a foreign key to this Object, 
	  * but only for standard Naming: i.e. "<TableName>ID", 
	  * which is not sufficient for e.g. parallel Relations. */
	public ArrayList relatedObjects(DBObjectFactory FactoryObject) throws SQLException {
		return FactoryObject.getObjects(ForeignKeyCondition()); }
	
	////////////////////////////////////////////////////////////////////////////
	//	Interface PersistAble
	////////////////////////////////////////////////////////////////////////////
	
	/** DB Table Name for this Class */
	public String TableName() { return STR_TableName; }
	
	/** Array of the Field Objects for this Class, including Parent Fields */
	public Field[] Fields() { return Fields; }
	
	/** String Constants for the Field Names */
	public String[] FieldNames() { return FieldNames; }
	
	/** String Constants for the DBField Names */
	public String[] DBFieldNames() { return DBFieldNames; }
	
	/** Returns the foreign key Condition, 
	  * i.e. the Condition for Objects holding a foreign key to this Object, 
	  * but only for standard Naming: i.e. "<TableName>ID", 
	  * which is not sufficient for e.g. parallel Relations. */
	public String ForeignKeyCondition() { 
		return DBObjectFactory.Condition(this, TableName()); } 
	
}
