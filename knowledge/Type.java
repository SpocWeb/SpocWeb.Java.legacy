package knowledge;

import java.lang.reflect.Field;

/**
 * Type of the Object for Categorization,
 * usable only for one Dimensional Categories.
 * The rest must be done by Attributes of certain Types.
 * Contains the Reference to the MetaType for categorizing Objects into
 * Simple-, Attribute- and Relation-Objects.
 *
 * Design Decisions:
 * No Hierarchy or even Network structure imposed on these Types (apart from the MetaType).
 * Possibly with increasing Model Size these Types have to be organized in their own way!
 */
public class Type
extends Relation {

	//////////////////////
	//  static Members  //
	//////////////////////

	/** String Constant for the Table Name */
	final static public String STR_TableName = "Type";
	
	/** Array of Field Names *///DBObjectFactory
	final static public Field[] Fields = DBObjectFactory.conCat (Relation.Fields, knowledge.Type.class.getDeclaredFields()); //getFields ()};
	
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

	/** Reference to the second SuperTypeID of this Type.
	 *  Used to introduce a Network into this Class
	 */
	protected int Type2ID;
	
	/** Reference to the second SuperType of this Type.
	 *  Used to introduce a Network into this Class
	 */
	protected Type Type2;
	
	/** Reference to the MetaTypeID of this Type.
	 *  Used to Categorize Objects into these fundamental Classes:
	 * 1) Objects
	 * 2) 1:N Relations: using the Subject to group Attributes (e.g. for Time Series)
	 * 3) N:M Relations(with Attributes) using Subject and Object
	 * 4) primitive Metric Attributes
	 * 5) primitive Time   Attributes
	 * 6) primitive Enum   Attributes
	 * 7) primitive String Attributes
	 * 8) Value for   Enum Attributes
	 */
	protected int MetaTypeID;
	
	/** Reference to the MetaType of this Type.
	 *  Used to Categorize Objects into these fundamental Classes:
	 * 1) Objects
	 * 2) 1:N Relations: using the Subject to group Attributes (e.g. for Time Series)
	 * 3) N:M Relations(with Attributes) using Subject and Object
	 * 4) primitive Metric Attributes
	 * 5) primitive Time   Attributes
	 * 6) primitive Enum   Attributes
	 * 7) primitive String Attributes
	 * 8) Value for   Enum Attributes
	 */
	protected MetaType MetaType;
	
	////////////////////
	//  Constructors  //
	////////////////////
	
	/** Empty Constructor for a Type */
//	protected Type () { }
	
	/** key Constructor for a Type */
	public Type (int ID) { super(ID); }
		
	/** key Constructor for a Type */
	public Type (Integer ID) { super(ID); }
		
	/** key Constructor for a Type */
	public Type (IPrimaryKey ID) { super(ID); }
		
	/** Full Constructor for a Type */
	public Type (int ID, String Name, String Description, Status Status, Type type, Type Type2, MetaType MetaType, Type Subject, Type Object) {
		super(ID, Name, Description, type, Status, Subject, Object);
		this.MetaType = MetaType;
		this.Type2 = Type2; 
	}
	
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
	
}
