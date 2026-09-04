package knowledge;

import java.lang.reflect.Field;

/** MetaType of the Type.
 *  Used to Categorize Objects into these fundamental Classes:
 * 1) Objects
 * 2) 1:N Relations: using the Subject to group Attributes (e.g. for Time Series)
 * 3) N:M Relations(with Attributes) using Subject and Object
 */
public class MetaType
extends Status {

	////////////////////////////////////////////////////////////////////////////
	//  static Members  
	////////////////////////////////////////////////////////////////////////////
	
	/** String Constant for the Table Name */
	final static public String STR_TableName = "MetaType";
	
	/** Array of Field Names *///DBObjectFactory
	final static public Field[] Fields = DBObjectFactory.conCat (Status.Fields, MetaType.class.getDeclaredFields()); //getFields ()};
	
	/** String Constants for the Field Names */
	final static public String[] FieldNames = DBObjectFactory.getFieldNames(Fields);
	
	/** String Constants for the DBField Names */
	final static public String[] DBFieldNames = FieldNames;
	
	////////////////////////////////////////////////////////////////////////////
	//  static Methods  
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//  Members  
	////////////////////////////////////////////////////////////////////////////
	
	/** Determines whether this MetaType is primitive or an Object.  */
	protected boolean primitive;
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors  
	////////////////////////////////////////////////////////////////////////////
	
	/** protected Empty Constructor for a MetaType */
//	protected MetaType () { }
	
	/** key Constructor for a MetaType */
	public MetaType (int ID) { super(ID); }
	
	/** key Constructor for a MetaType */
	public MetaType (Integer ID) { super(ID); }
	
	/** key Constructor for a MetaType */
	public MetaType (IPrimaryKey ID) { super(ID); }
	
	/** Full Constructor for a MetaType */
	public MetaType (int ID, String Name, String Description, boolean primitive) {
		super(ID, Name, Description);
		this.primitive = primitive; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Methods  
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//	Interface PersistAble
	////////////////////////////////////////////////////////////////////////////
	
	/** Overloaded empty Constructor, used for selectFromDB() 	 */
//	public Object newInstance() { return new MetaType(); } 
	
	/** DB Table Name for this Class */
	public String TableName() { return STR_TableName; }
	
	/** Array of the Field Objects for this Class, including Parent Fields */
	public Field[] Fields() { return Fields; }
	
	/** String Constants for the Field Names */
	public String[] FieldNames() { return FieldNames; }
	
	/** String Constants for the DBField Names */
	public String[] DBFieldNames() { return DBFieldNames; }
	
}
