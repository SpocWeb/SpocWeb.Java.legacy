package knowledge;

import java.lang.reflect.Field;
 

/**
 * A {@link BasicAttribute} holding one {@link String} value.
 *
 * <p>The value is a public field with no length bound of its own; whatever the
 * {@code StringAttribute} column allows is the only limit.
 *
 * <p>The class is a thin specialisation: it adds the value, its table name and its own
 * field arrays, and inherits the type/subject/status key from {@link BasicAttribute}.
 *
 * This Class can aggregate Scalar String Attributes and models 1:N Relations
 * using the Subject to group Attributes (e.g. for Time Series)
 * The MetaType of the Type for this Class must always be 'String'.
 *
 * Design Decisions:
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:13:09Z
 * digest: 80c2dcf5a634d69ce6ac0a8cf0cd2ca0bcf029e75d9906684bf04828e2f9f638
 * stale: false
 * -->
 */
public class StringAttribute
extends BasicAttribute {

	//////////////////////
	//  static Members  //
	//////////////////////

	/** String Constant for the Table Name */
	final static public String STR_TableName = "StringAttribute"; //
	
	/** Array of Field Names, selects only the non static, final or transient Fields	 */
	// TODO: LOGIC: seeded with an empty array instead of BasicAttribute.Keys, so Fields()
	// returns only this class's own Value column despite its 'including Parent Fields'
	// contract; the inherited TypeID/SubjectID/StatusID key columns are missing from every
	// generic insert, update and select DBObjectFactory builds from it. MetaType, which
	// concatenates Status.Fields, shows the intended form.
	final static public Field[] Fields = DBObjectFactory.conCat(new Field[0], StringAttribute.class.getDeclaredFields()); //getFields ()};
	
	/** String Constants for the Field Names */
	final static public String[] FieldNames = DBObjectFactory.getFieldNames(Fields);
	
	/** String Constants for the DBField Names */
	final static public String[] DBFieldNames = FieldNames;
	
	//////////////////////
	//  static Methods  //
	//////////////////////
	
	///////////////
	//  Members  //
	///////////////
	
	/** The Value of this Attribute.
	 *  Can be freely modified, so it is defined public  */
	public String Value;
	
	////////////////////
	//  Constructors  //
	////////////////////
	
	/** Empty Constructor for Subclasses */
//	protected StringAttribute() {}
	
	/** Primary key Constructor */
	public StringAttribute (IPrimaryKey Key) { super(Key); } 
	
	/** Full Constructor for a BasicAttribute Object */
	public StringAttribute(int TypeID, int SubjectID, int StatusID) {
		super(TypeID, SubjectID, StatusID); } 
	
	/** Full Constructor for a BasicAttribute Object */
	public StringAttribute(int TypeID, int SubjectID, int StatusID, String Value) {
		super(TypeID, SubjectID, StatusID); 
		this.Value = Value; } 
	
	/** Constructor for an Attribute Object */
	public StringAttribute(Type Type, Objekt Subject, Status Status) { 
		super(Type, Subject, Status); } 
	
	/** Constructor for an Attribute Object */
	public StringAttribute(Type Type, Objekt Subject, Status Status, String Value) { 
		super(Type, Subject, Status);
		this.Value = Value; } 
	
	///////////////
	//  Methods  //
	///////////////
	
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
