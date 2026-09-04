package knowledge;

import java.sql.SQLException;

/**
 * This Class can aggregate Scalar Attributes and models 1:N Relations
 * using the Subject to group Attributes (e.g. for Time Series)
 * The MetaType of the Type for this Class must always be 'Attribute'(2).
 *
 * Design Decisions:
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
	
	/** Returns the Subject for this Object. */
	public int getSubjectID() { return SubjectID; }
	
	/** Returns the Subject for this Object. */
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
