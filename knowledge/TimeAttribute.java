package knowledge;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * A {@link BasicAttribute} holding one {@link java.util.Date} value.
 *
 * <p>{@link java.util.Date} is mutable, and the value is a public field handed out by
 * reference, so a caller can change a stored timestamp in place without the owning object
 * noticing.
 *
 * <p>The class is a thin specialisation: it adds the value, its table name and its own
 * field arrays, and inherits the type/subject/status key from {@link BasicAttribute}.
 *
 * This Class can aggregate Scalar DateTime Attributes and models 1:N Relations
 * using the SubjectID to group Attributes (e.g. for Time Series)
 * The MetaType of the Type for these Objects must always be 'Time'.
 *
 * Design Decisions:
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:13:09Z
 * digest: 15e5a1d110d57d6036802c17986ec89ab670763329b5468f7cf8de6661754d34
 * stale: false
 * -->
 */
public class TimeAttribute
extends BasicAttribute {

	//////////////////////
	//  static Members  //
	//////////////////////

	/** String Constant for the Table Name */
	final static public String STR_TableName = "TimeAttribute"; //
	
	/** Array of Field Names, selects only the non static, final or transient Fields	 */
	final static public Field[] Fields = DBObjectFactory.conCat(BasicAttribute.Keys, TimeAttribute.class.getDeclaredFields()); //getFields ()};
	
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

	/** The Date and Time Value of this Attribute.
	 *  Can be freely modified, so it is defined public  */
	public Date Value;

	////////////////////
	//  Constructors  //
	////////////////////

	/** Empty Constructor for Subclasses */
//	protected TimeAttribute() {}

	/** Primary key Constructor */
	public TimeAttribute (IPrimaryKey Key) { super(Key); } 
	
	/** Full Constructor for a BasicAttribute Object */
	public TimeAttribute(int TypeID, int SubjectID, int StatusID) {
		super(TypeID, SubjectID, StatusID); } 
	
	/** Full Constructor for a BasicAttribute Object */
	public TimeAttribute(int TypeID, int SubjectID, int StatusID, Date Value) {
		super(TypeID, SubjectID, StatusID); 
		this.Value = Value; } 
	
	/** Constructor for an Attribute Object */
	public TimeAttribute(Type Type, Objekt Subject, Status Status) { 
		super(Type, Subject, Status); } 
	
	/** Constructor for an Attribute Object */
	public TimeAttribute(Type Type, Objekt Subject, Status Status, Date Value) {
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
