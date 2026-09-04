package knowledge;

import java.lang.reflect.Field;
 

/**
 * This Class can aggregate Scalar enumerated Attributes and models 1:N Relations
 * using the Subject to group Attributes (e.g. for Time Series)
 * The MetaType of the Type for this Class must always be 'Enum'.
 * 
 * Design Decisions:
 * Boolean Values have been made an Enum Type, 
 * because they may be substituted for a multivalued 
 * or even a continuous Attribute. 
 */
public class EnumAttribute
extends BasicAttribute {

	//////////////////////
	//  static Members  //
	//////////////////////

	/** String Constant for the Table Name */
	final static public String STR_TableName = "EnumAttribute"; //
	
	/** Array of Field Names, selects only the non static, final or transient Fields	 */
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
