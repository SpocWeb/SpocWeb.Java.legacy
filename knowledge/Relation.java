package knowledge;

import java.sql.SQLException;
 

/**
 * An {@link AttributeObject} that also points at an object, making it the N:M side of
 * the model.
 *
 * <p>Because a relation is itself an object, its own object end may be another relation,
 * which is how relations of three or more members are expressed without a separate
 * construct. The object end must never be a plain attribute.
 *
 * <p><b>Invariant,</b> not enforced here: the {@link MetaType} of this object's
 * {@link Type} must read {@code Relation} (3).
 *
 * This Class can aggregate Scalar Attributes and models
 * N:M Relations(with Attributes) using Subject and Objekt
 * The MetaType of the Type for this Class must always be 'Relation'(3).
 *
 * Design Decisions:
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:13:26Z
 * digest: 6432581a9ca8b74efce8dfdabc89261c11c19b4cf274dee7693f4bce7723c4cc
 * stale: false
 * -->
 */
public class Relation
extends AttributeObject
implements IRelation {
	
	////////////////////////////////////////////////////////////////////////////
	//  static Members  
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//  static Methods  
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//  Members  
	////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Object of this Relation.
	 *  Used to model N:M Relations.
	 *  The Object must never be an Attribute, only Objects an Relations are allowed.
	 *  When the Object is a Relation itself, a 3 Member Relation is created etc.
	 */
	protected Objekt Object;
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessors  
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns the ObjectID for this Relation. */
	public int getObjectID() { return ObjectID; }
	
	/** Returns the Object for this Relation. */
	public Objekt getObject() throws SQLException { 
	if (Object == null)
        Object = (Objekt) DBObjectFactory.FactoryObject.getObject(new IdKey(ObjectID)); //new Objekt(SubjectID)).selectFromDB(); 
		return Object; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors  
	////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor for Subclasses */
//	protected Relation() {}
	
	/** key Constructor for a Relation Object */
	public Relation(int ID) { super(ID); }
	
	/** key Constructor for a Relation Object */
	public Relation(Integer ID) { super(ID); }
	
	/** key Constructor for a Relation Object */
	public Relation(IPrimaryKey ID) { super(ID); }
	
	/** Full Constructor for a Relation Object */
	public Relation(int ID_, String Name_, String Description_, int TypeID_, int StatusID_, int SubjectID_, int ObjectID_) {
		super(ID_, Name_, Description_, TypeID_, StatusID_, SubjectID_); 
		this.ObjectID = ObjectID_; 
	}
	
	/** Full Constructor for a Relation Object */
	public Relation(int ID, String Name, String Description, Type Type, Status Status, Objekt Subject, Objekt Object) {
		this(ID, Name, Description, Type.getID (), Status.getID(), Subject.getID (), Object.getID ()); 
		this.Subject = Subject; 
		this.Object = Object; 
		this.Status = Status; 
		this.Type = Type; 
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	Interface PersistAble
	////////////////////////////////////////////////////////////////////////////
	
	/** Overloaded empty Constructor, used for selectFromDB() 	 */
//	public Object newInstance() { return new Relation(); } 
	
	////////////////////////////////////////////////////////////////////////////
	//  Methods  
	////////////////////////////////////////////////////////////////////////////

}
