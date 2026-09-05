package knowledge;

import java.lang.reflect.Field;
 

/**
 * A {@link BasicAttribute} holding one enumerated value, stored as the {@code long} ID of
 * the {@link Type} representing that value.
 *
 * <p>Booleans are modelled here rather than as their own attribute kind, so that a yes/no
 * attribute can later be widened to a multi-valued or continuous one without changing its
 * storage.
 *
 * <p>The class is a thin specialisation: it adds the value, its table name and its own
 * field arrays, and inherits the type/subject/status key from {@link BasicAttribute}.
 *
 * This Class can aggregate Scalar enumerated Attributes and models 1:N Relations
 * using the Subject to group Attributes (e.g. for Time Series)
 * The MetaType of the Type for this Class must always be 'Enum'.
 * 
 * Design Decisions:
 * Boolean Values have been made an Enum Type, 
 * because they may be substituted for a multivalued 
 * or even a continuous Attribute. 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:13:09Z
 * digest: 7ac9f2da8972260a9befa646c7fcb1831c1220bb8d43db75e5c45dc7039c8cbd
 * stale: false
 * -->
 */
public class EnumAttribute
extends BasicAttribute {

	//////////////////////
	//  static Members  //
	//////////////////////

	/** String Constant for the Table Name */
	final static public String STR_TableName = "EnumAttribute"; //
	
	/** Array of Field Names, selects only the non static, final or transient Fields	 */
	// TODO: LOGIC: seeded with an empty array instead of BasicAttribute.Keys, so Fields()
	// returns only this class's own Value column despite its 'including Parent Fields'
	// contract; the inherited TypeID/SubjectID/StatusID key columns are missing from every
	// generic insert, update and select DBObjectFactory builds from it. MetaType, which
	// concatenates Status.Fields, shows the intended form.
	final static public Field[] Fields = DBObjectFactory.conCat(new Field[0], EnumAttribute.class.getDeclaredFields()); //getFields ()};
	
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
	public long Value;

	////////////////////
	//  Constructors  //
	////////////////////

	/** Empty Constructor for Subclasses */
//	protected EnumAttribute() {}

	/** Primary key Constructor */
	public EnumAttribute (IPrimaryKey Key) { super(Key); } 
	
	/** Full Constructor for an EnumAttribute Object */
	public EnumAttribute(int TypeID, int SubjectID, int StatusID) {
		super(TypeID, SubjectID, StatusID); } 
	
	/** Full Constructor for an EnumAttribute Object */
	public EnumAttribute(int TypeID, int SubjectID, int StatusID, int Value) {
		super(TypeID, SubjectID, StatusID); 
		this.Value = Value; } 
	
	/** Constructor for an EnumAttribute Object */
	public EnumAttribute(Type Type, Objekt Subject, Status Status) { 
		super(Type, Subject, Status); } 
	
	/** Constructor for an EnumAttribute Object */
	public EnumAttribute(Type Type, Objekt Subject, Status Status, long Value) {
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
