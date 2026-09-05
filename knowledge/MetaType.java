package knowledge;

import java.lang.reflect.Field;

/**
 * The coarsest classification a {@link Type} carries, saying whether an object is a plain
 * object, a relation, or a primitive attribute value.
 *
 * <p>It is a {@link Status} row of its own, persisted through {@link PersistAble}; its
 * field arrays are built once at class-initialisation time by concatenating
 * {@link Status}'s fields with this class's own, so {@code DBFieldNames} and
 * {@code FieldNames} are deliberately the same array - this table's column names match its
 * Java field names.
 *
 * MetaType of the Type.
 *  Used to Categorize Objects into these fundamental Classes:
 * 1) Objects
 * 2) 1:N Relations: using the Subject to group Attributes (e.g. for Time Series)
 * 3) N:M Relations(with Attributes) using Subject and Object
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:11:11Z
 * digest: 8dce032d90db5813b6457eda5da2ecf05643b4cc41faff06e5054e866a228722
 * stale: false
 * -->
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
