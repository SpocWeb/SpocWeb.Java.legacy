package knowledge;

import java.sql.SQLException;

/**
 * An {@link Objekt} that additionally points at a subject, modelling the 1:N side of the
 * model: many attributes grouped under one subject.
 *
 * <p>Unlike {@link BasicAttribute} it keeps its own identity, name and description, which
 * is what lets further attributes hang off it in turn - the mechanism behind grouping a
 * time series under a single carrier object.
 *
 * <p><b>Invariants,</b> neither enforced here: the {@link MetaType} of this object's
 * {@link Type} must read {@code Attribute} (2), and the subject must be a plain object,
 * never an attribute or a relation.
 *
 * This Class can aggregate Scalar Attributes and models 1:N Relations
 * using the Subject to group Attributes (e.g. for Time Series)
 * The MetaType of the Type for this Class must always be 'Attribute'(2).
 *
 * Design Decisions:
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:13:26Z
 * digest: 3ad6a09b894102a440ecb61db23b95c40f26b3ba7d4c0a3904553dbeebab54d8
 * stale: false
 * -->
 */
public class AttributeObject
extends Objekt
implements IAttribute {

	//////////////////////
	//  static Members  //
	//////////////////////

	//////////////////////
	//  static Methods  //
	//////////////////////

	///////////////
	//  Members  //
	///////////////

	/** Reference to the Subject of this Object or Relation.
	 *  Used to model 1:N Relations (for Bundling Data Attributes) and N:M Relations.
	 *  Meaning of the Relation is determined by the Type.
	 *  The Subject must always be an Object, not an Attribute
	 *  (no Attributes for Attributes) or even a Relation.
	 */
	protected Objekt Subject;
	
	/////////////////
	//  Accessors  //
	/////////////////
	
	/** Returns the ID of the Subject for this Object, without resolving it. */
	public int getSubjectID() { return SubjectID; }
	
	/** Returns the Subject for this Object, resolving and caching it on first call. */
	public Objekt getSubject() throws SQLException { 
		if (Subject == null)
            Subject = (Objekt) DBObjectFactory.FactoryObject.getObject(new IdKey(SubjectID)); 
		return Subject; }
	
	////////////////////
	//  Constructors  //
	////////////////////
	
	/** Empty Constructor for Subclasses */
//	protected AttributeObject() {}
	
	/** key Constructor for an Attribute Object */
	public AttributeObject(int ID) { super(ID); } 
	
	/** key Constructor for an Attribute Object */
	public AttributeObject(Integer ID) { super(ID); } 
	
	/** key Constructor for an Attribute Object */
	public AttributeObject(IPrimaryKey ID) { super(ID); } 
	
	/** Full Constructor for a simple Object */
	public AttributeObject(int ID, String Name, String Description, int TypeID, int StatusID, int SubjectID) {
		super(ID, Name, Description, TypeID, StatusID);
		this.SubjectID = SubjectID;
		this.StatusID = StatusID;
	}
	
	/** Full Constructor for an Attribute Object */
	public AttributeObject(int ID, String Name, String Description, Type Type, Status Status, Objekt Subject) {
		this(ID, Name, Description, Type.getID (), Status.getID(), Subject.getID ()); 
		this.Subject = Subject; 
		this.Status = Status;
		this.Type = Type;
	}
	
	///////////////
	//  Methods  //
	///////////////

	////////////////////////////////////////////////////////////////////////////
	//	Interface PersistAble
	////////////////////////////////////////////////////////////////////////////
	
	/** Overloaded empty Constructor, used for selectFromDB() 	 */
//	public Object newInstance() { return new AttributeObject(); } 
	
}
