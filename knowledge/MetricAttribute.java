package knowledge;

import java.lang.reflect.Field;
 

/**
 * A {@link BasicAttribute} holding one {@code double} measurement.
 *
 * <p>The value is a public field with no validation or unit, so the {@link Type} is the only
 * record of what is being measured and in what unit.
 *
 * <p>The class is a thin specialisation: it adds the value, its table name and its own
 * field arrays, and inherits the type/subject/status key from {@link BasicAttribute}.
 *
 * This Class can aggregate Scalar Metric Attributes and models 1:N Relations
 * using the Subject to group Attributes (e.g. for Time Series)
 * The MetaType of the Type for this Class must always be 'Metric'.
 *
 * Design Decisions:
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:13:09Z
 * digest: d34aaab096aea167707de4730b370adfc353208e29c07c119af2597ea131fe74
 * stale: false
 * -->
 */
public class MetricAttribute
extends BasicAttribute {

	//////////////////////
	//  static Members  //
	//////////////////////

	/** String Constant for the Table Name */
	final static public String STR_TableName = "MetricAttribute"; //
	
	/** Array of Field Names, selects only the non static, final or transient Fields	 */
	final static public Field[] Fields = DBObjectFactory.conCat(BasicAttribute.Keys, MetricAttribute.class.getDeclaredFields()); //getFields ()};
	
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
	public double Value; 

	////////////////////
	//  Constructors  //
	////////////////////

	/** Empty Constructor for Subclasses */
//	protected MetricAttribute() {}
	
	/** Primary key Constructor */
	public MetricAttribute (IPrimaryKey Key) { super(Key); } 
	
	/** Full Constructor for an MetricAttribute Object */
	public MetricAttribute(int TypeID, int SubjectID, int StatusID) {
		super(TypeID, SubjectID, StatusID); } 
	
	/** Full Constructor for an MetricAttribute Object */
	public MetricAttribute(int TypeID, int SubjectID, int StatusID, double Value) {
		super(TypeID, SubjectID, StatusID); 
		this.Value = Value; } 
	
	/** Constructor for an MetricAttribute Object */
	public MetricAttribute(Type Type, Objekt Subject, Status Status) { 
		super(Type, Subject, Status); } 
	
	/** Constructor for an Attribute Object */
	public MetricAttribute(Type Type, Objekt Subject, Status Status, double Value) {
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
