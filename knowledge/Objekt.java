package knowledge;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A named, typed entity in the knowledge model - the plain object that attributes and
 * relations are hung off, and the root of both.
 *
 * <p>{@link AttributeObject} and {@link Relation} extend it rather than replace it, and
 * what tells the three apart at runtime is the {@link MetaType} of their {@link Type}, not
 * their Java class: the same row can be read back as whichever of them its meta-type says
 * it is. That is why the subject and object columns are declared here, on the base class,
 * even though a plain object uses neither.
 *
 * <p><b>Invariant,</b> not enforced here: the meta-type of this object's type must read
 * {@code Object} (1).
 *
 * This Class can aggregate Scalar Attributes and models Objects only
 * The MetaType of the Type for this Class must always be 'Object'(1).
 * Derived Classes are:
 * AttributeObject: to model 1:N Relations (with Attributes)
 * Relation:  to model N:M Relations(with Attributes)
 * These Classes are distinguished using the MetaType of their Type.
 *
 * Design Decisions:
 * An Object has both a fixed Length Name to easily identify it in Queries
 * and an arbitrary Length Description for any Remarks that cannot (yet) be quantified.
 * This is a Consequence of persisting it and two Forces:
 * Speed of Retrieval: use the Name
 * Arbitrary Length and avoiding unnecessary Memory Consumption: use the Description
 *
 * It is possible to implement a generic Transformation of any Object into this structure
 * and from this structure into and from a Database.
 * This is different from classical DB Modeling,
 * where a relational Table is mapped to a specific Object manually or by Name.
 * OODBs work like this and they can exploit the Reflection API of Languages like Java.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:17:51Z
 * digest: 1dbe6ed790a568df2a522e7a0b71c6e616d0efe002b1b33bf42594c25d5e7ae4
 * stale: false
 * -->
 */
public class Objekt
extends Status
implements IObject {
	
	////////////////////////////////////////////////////////////////////////////
	//  static Members  
	////////////////////////////////////////////////////////////////////////////
	
	/** Array of Field Names *///DBObjectFactory
	final static public Field[] Fields = DBObjectFactory.conCat (knowledge.Status.Fields, Objekt.class.getDeclaredFields()); //getFields ()};
	
	/** String Constants for the Field Names */
	final static public String[] FieldNames = DBObjectFactory.getFieldNames(Fields);
	
	/** String Constants for the DBField Names */
	final static public String[] DBFieldNames = FieldNames;
	
	/** String Constant for the Table Name */
	final static public String STR_TableName = "Object";
	
	/** String Constant  */
//	final static public String STR_StatusID = "StatusID"; 
	
	/** String Constant  */
//	final static public String STR_TypeID = "TypeID"; 
	
	/** String Constant  */
	final static public String STR_SubjectID = "SubjectID"; 
	
	/** String Constant  */
//	final static public String STR_ObjectID = "ObjectID"; 
	
	/** String Constant for the Field Names */
//	final static public String STR_FieldNames = Knowledge.Status.STR_FieldNames + "," + STR_TypeID + "," + STR_StatusID + "," + STR_SubjectID + "," + STR_ObjectID; 
	
	////////////////////////////////////////////////////////////////////////////
	//  static Methods  
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//  Members  
	////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the TypeID of the Object for Categorization,
	 *  usable only for one Dimensional Categories.
	 *  The rest must be done by Attributes of certain Types. */
	protected int TypeID;
	
	/** Reference to the Type of the Object for Categorization,
	 *  usable only for one Dimensional Categories.
	 *  The rest must be done by Attributes of certain Types. */
	protected Type Type;
	
	/** Reference to the StatusID of the Object,
	 *  usable only for one Dimension.
	 *  The rest must be done by Attributes of a certain Kind. */
	protected int StatusID;
	
	/** Reference to the Status of the Object,
	 *  usable only for one Dimension.
	 *  The rest must be done by Attributes of a certain Kind. */
	protected Status Status;
	
	/** Reference to the SubjectID of the Object,
	 *  Already defined here to save the Modifications to the SQL Operations.	 */
	protected int SubjectID;
	
	/** Reference to the ObjectID of the Object,
	 *  Already defined here to save the Modifications to the SQL Operations.	 */
	protected int ObjectID;
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessors  
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns the Status for this Object */
	public Status getStatus() throws SQLException { 
		if (Status == null)
            Status = (Status) DBObjectFactory.FactoryStatus.getObject(new IdKey(StatusID)); 
		return Status; }
	
	/** Returns the Type for this Object */
	public Type getType() throws SQLException {
		if (Type == null)
            Type = (Type) DBObjectFactory.FactoryType.getObject(new IdKey(TypeID));
		return Type; }

	/** Returns the ID of the Status for this Object, without resolving it. */
	public int getStatusID() { return StatusID; }

	/** Returns the ID of the Type for this Object, without resolving it. */
	public int getTypeID() { return TypeID; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors  
	////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor for Subclasses */
//	protected Objekt() {}
	
	/** key Constructor for a simple Object */
	public Objekt(int ID) { super(ID); } 
	
	/** key Constructor for a simple Object */
	public Objekt(Integer ID) { super(ID); } 
	
	/** key Constructor for a simple Object */
	public Objekt(IPrimaryKey ID) { super(ID); } 
	
	/** Full Constructor for a simple Object */
	public Objekt(int ID, String Name, String Description, int TypeID, int StatusID) {
		super(ID, Name, Description);
		this.StatusID = StatusID;
		this.TypeID = TypeID;
		this.SubjectID = ID; 
		this.ObjectID = ID; 
	}
	
	/** Full Constructor for a simple Object */
	public Objekt(int ID, String Name, String Description, Type Type, Status Status) {
		this(ID, Name, Description, Type.getID (), Status.getID());
		this.Status = Status;
		this.Type = Type;
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Methods  
	////////////////////////////////////////////////////////////////////////////

	/** Returns the Relations and Attributes for which this is the Subject */
	public ArrayList relatedSubjects() throws SQLException {
		return DBObjectFactory.FactoryObject.getObjects (DBObjectFactory.STR_WHERE_ + STR_SubjectID + "=" + ID); }
	
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
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface IPersistAble
	////////////////////////////////////////////////////////////////////////////
	
	/** Sets the Fields of this Object from the current Row in the ResultSet	 */
/*	public void setFields(ResultSet rs) throws SQLException { 
		SubjectID = rs.getInt(STR_SubjectID); 
		ObjectID = rs.getInt(STR_ObjectID); 
		StatusID = rs.getInt(STR_StatusID); 
		TypeID = rs.getInt(STR_TypeID); 
		super.setFields(rs); }
	
	/** Save (Insert or Update) this Object into the DB
	 * Returns true when inserted, false when updated  */
/*	public boolean insertIntoDB () throws SQLException { 
		String SQL = DBObjectFactory.STR_INSERT_INTO_ + STR_TableName; 
		SQL  = SQL + "(ID, " + STR_FieldNames + ") "; 
		SQL  = SQL + DBObjectFactory.STR_VALUES_ + "(" + ID + "," + Name + "," + Description + "," + TypeID + "," + StatusID + "," + SubjectID + "," + ObjectID + ")"; 
		return (StateMt.executeUpdate(SQL) == 1); }
	
	/** Returns the Where Clause of this Table (Primary Key Condition)  */
//	protected String getUpdate() { return super.getUpdate() + "," + STR_TypeID + " = " + TypeID + "," + STR_StatusID + " = " + StatusID + "," + STR_SubjectID + "=" + SubjectID + "," + STR_ObjectID +  "=" + ObjectID ; } 
	
}
