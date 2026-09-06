package knowledge;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import streamIO.AStreamOut;

/**
  * Loads, saves and deletes any {@link PersistAble} object over one JDBC connection, by
  * reading the table and column names the object reports about itself and reflecting over
  * its fields.
  *
  * <p>One instance binds a connection to a single table: the prototype passed to the
  * constructor supplies the table name and the field arrays, from which the SELECT, INSERT,
  * UPDATE and DELETE prefixes are built once and reused. The eight {@code Factory*} statics
  * are the per-table instances the rest of the package resolves its references through, and
  * {@link #initFactories(Connection)} is what installs them.
  *
  * <p><b>Invariant:</b> the prototype's {@code Fields()} and {@code DBFieldNames()} must
  * agree in order and length, and the result set a row is read from must present the key
  * columns first and the data columns after, in that same order - the reader addresses
  * columns by position, not by name, so a mismatch mis-assigns fields silently rather than
  * failing.
  *
  * <p>Values are rendered into the SQL text by {@link #literal(Object)} rather than bound as
  * parameters, because {@link IPrimaryKey#Condition()} is a String by contract across three
  * interfaces. That method is therefore the single point at which a value crosses into SQL
  * syntax, and the only place to change if this is ever reworked onto prepared statements.
  *
  * Title: DBObjectFactory.java<p>
  * Description:
  * Implements all the Behavior necessary to load / cache / save an Object implementing
  * @see PersistAble from a DB Table
  * @see streamIO.Object.Parser for Serialization into an XML String
  * Also maintains a Sequence for the ID of the Table (faster than from the DB)
  * (must be synchronized and a Singleton, if not implemented by the DB itself).
  *
  * This is essentially an enriched Connection for Objects that implement the Interface
  * @see PersistAble and thus know their DB Table and Field Names
  *
  * It takes the DB and Object Field Names and Conditions from the PersistAble Interface,
  * builds the Query, executes it and fills the Object Fields from the DB Fields.
  * It can process individual Objects as well as create and return
  * Resultsets from conditional Where Clauses.
  *
  * Design Decisions:
  * The previous Version way was expecting the key Fields to be separated
  * into a distinct Set from the Rest of the Fields.
  * Although this is efficient for the Update Statement,
  * since the Keys are never updated,
  * it is more generic to leave all Fields in the Fields Collection,
  * also the key Fields.
  *
  * TODO:
  * separate Creation / Filling of Objects
  * from the Construction of the ResultSet.
  * Because the ResultSet Interface is already implemented by myself!
  *
  * Subclasses:
  * @see knowledge.DBCachedFactory which delays Loading and Saving
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2006-03-11T02:04:43Z
  * digest: c6778f9add1714047042fa2d49d57af51b65ef265f604e6604795322be6a2be4
  * stale: false
  * -->
  */
public class DBObjectFactory
extends Object {

	//////////////////////
	//  static Constants
	//////////////////////

	/** String Constants for the Primary Keys */
//	final static public String STR_ID = "ID";

	/** String Constants for the Equals Relation */
//	final static public String STR__ = " = ";

	/** String Constant for the Table Name */
//	final static public String STR_ID__ = STR_ID + STR__;

	/** String Constant for the Select Statement
	  * Full Syntax: SELECT <Field1>,<Field2>,... FROM <TableName> WHERE <Condition1> AND/OR <Condition2> 	 */
	final static public String STR_SELECT_ = "SELECT ";

	/** String Constant for the Select Statement
	  * Full Syntax: INSERT INTO <TableName> (<Field1>,<Field2>,...) VALUES (<Value1>,<Value2>,...)	 */
	final static public String STR_INSERT_INTO_ = "INSERT INTO ";

	/** String Constant for the Select Statement
	  * Full Syntax: UPDATE <TableName> SET <Field1>=<Value1>, <Field2>=<Value2>,... 	 */
	final static public String STR_UPDATE_ = "UPDATE ";

	/** String Constant for the Select Statement
	  * Full Syntax: DELETE FROM <TableName> WHERE <Condition1> AND/OR <Condition2>... 	 */
	final static public String STR_DELETE_FROM_ = "DELETE FROM ";

	/** String Constant for the Select Statement */
	final static public String STR_VALUES_ = ") VALUES (";

	/** String Constant for the Select Statement */
	final static public String STR_FROM_ = " FROM ";

	/** String Constant for the Select Statement */
	final static public String STR_SET_ = " SET ";

	/** String Constant for the Table Name */
	final static public String STR_WHERE_ = " WHERE ";

	/** String Constant for the Select Statement */
	final static public String STR_Select_Max = "Select Max(ID) FROM ";

	//////////////////////
	//  static Variables
	//////////////////////

	/** Central (Singleton) Factory for Status Objects */
	public static DBObjectFactory FactoryStatus;

	/** Central (Singleton) Factory for MetaType Objects */
	public static DBObjectFactory FactoryMetaType;

	/** Central (Singleton) Factory for Objects, Attributes and Relations */
	public static DBObjectFactory FactoryObject;

	/** Central (Singleton) Factory for Type Objects */
	public static DBObjectFactory FactoryType;

	/** Central (Singleton) Factory for StringAttribute Objects */
	public static DBObjectFactory FactoryString;

	/** Central (Singleton) Factory for MetricAttribute Objects */
	public static DBObjectFactory FactoryMetric;

	/** Central (Singleton) Factory for EnumAttribute Objects */
	public static DBObjectFactory FactoryEnum;

	/** Central (Singleton) Factory for TimeAttribute Objects */
	public static DBObjectFactory FactoryTime;

	//////////////////////
	//  static Methods  //
	//////////////////////

	//These Methods should be in the DBObjectFactory

	/** Test Method:
	  * Initializes the Basic Object Factories of the Knowledge DB
	  * using the given Connection */
	public static void initFactories(Connection C) throws SQLException {
		FactoryStatus   = new DBObjectFactory(C, new Status  (0));
		FactoryObject   = new DBObjectFactory(C, new Objekt  (0));
		FactoryMetaType = new DBObjectFactory(C, new MetaType(0));
		FactoryType     = new DBObjectFactory(C, new Type    (0));
		FactoryString   = new DBObjectFactory(C, new StringAttribute(0, 0, 0, "" ));
		FactoryMetric   = new DBObjectFactory(C, new MetricAttribute(0, 0, 0, 0.0));
		FactoryEnum     = new DBObjectFactory(C, new   EnumAttribute(0, 0, 0, 0  ));
		FactoryTime     = new DBObjectFactory(C, new   TimeAttribute(0, 0, 0, new java.util.Date(0)));
	}

	/**
	  * Helper Method:
	  * @return a new Array of Fields that is a concatenation of
	  * @param Arr and
	  * @param Suf
	  * but filters out the final, transient, static, volatile
	  * and non Primitive Fields (except for Strings).	 */
	public static Field[] conCat(Field[] Arr, Field[] Suf) {
		Field fld;
		Field[] ret = new Field[Arr.length + Suf.length];
		System.arraycopy(Arr, 0, ret, 0, Arr.length);
		int i = Suf.length; int j = Arr.length-1;
		while (--i >= 0) {
			fld = Suf[i];
			Class Type = fld.getType();
			int mod = fld.getModifiers();
			if (Modifier.isFinal	(mod) ||
				Modifier.isTransient(mod) ||
				Modifier.isStatic	(mod) ||
				Modifier.isVolatile	(mod) ||
				!((Type == String .class) ||
				  (Type == java.util.Date.class) ||
				   Type.isPrimitive()) //only primitive Types and Strings in the DB
			) continue;
			ret[++j] = fld;
		}
		Field[] ret2 = new Field[++j];
		System.arraycopy(ret, 0, ret2, 0, j);
		return ret2; }

	/**
	 * Returns the declared name of each of the given fields, in the same order.
	 *
	 * @return an Array of Field Names extracted from the Array of the Fields,
	 *   Since ususally the Field Name is the same as the Class Name.
	 */
	final static public String[] getFieldNames(Field[] Fields) {
		int i = Fields.length;
		String[] fieldNames = new String[i];
		while (--i >= 0)
			fieldNames[i] = Fields[i].getName();
		return fieldNames; }

	/**
	 * Returns the declared type of each of the given fields, in the same order.
	 *
	 * @return an Array of Field Types extracted from the Array of the Fields
	 */
	final static public Class[] getFieldTypes(Field[] Fields) {
		int i = Fields.length;
		Class[] FieldTypes = new Class[i];
		while (--i >= 0)
			FieldTypes[i] = Fields[i].getType();
		return FieldTypes; }

	/**
	 * Builds the WHERE clause selecting exactly the row the given key identifies, ANDing one
	 * equality per key column.
	 *
	 * @param Prefix prepended to each column name, for qualifying columns by table
	 * @return a String representing the SQL Condition for this Primary key.
	 *   This generic Implementation can be overridden by faster hardcoded ones.
	 * @throws IllegalAccessError when a key field cannot be read reflectively
	 */
	public static String Condition (IPrimaryKey obj, String Prefix) {
		StringBuffer ret = new StringBuffer(DBObjectFactory.STR_WHERE_);
		String[] dBKeyNames = obj.DBKeyNames();
		Field [] keys = obj.Keys();
		int i = dBKeyNames.length;
		while (--i >= 0) try {
			ret.append("(").append(Prefix).append(dBKeyNames[i]).append("=").append(literal(keys[i].get(obj))).append(") AND ");
		} catch (IllegalAccessException e) { throw new IllegalAccessError(e.toString()); }
		ret.setLength(ret.length() - 4); //Cut off the last "AND " Operator
		return ret.toString (); }

	/**
	 * Renders a value as an SQL literal: numbers bare, everything else single-quoted with
	 * embedded quotes doubled.
	 *
	 * <p>Every statement this class builds is assembled as text, so this is the single point
	 * at which a value crosses from Java into SQL syntax. Without it a string containing an
	 * apostrophe produced invalid SQL, and a hostile one could close the literal and append
	 * clauses of its own.
	 *
	 * <p>Doubling the quote is the SQL standard escape. It is not a substitute for bound
	 * parameters, which remain the better answer if this class is ever reworked to hand back
	 * statements rather than strings.
	 *
	 * @param value the value to render, may be null
	 * @return the SQL literal denoting that value, never null
	 */
	public static String literal(Object value) {
		if (value == null) return "NULL";
		if (value instanceof Number || value instanceof Boolean) return value.toString();
		return "'" + value.toString().replace("'", "''") + "'"; }

	/** Signature of the key Constructor as Array of Parameter Types
	  * suitable for calling the Constructor dynamically.  */
	final static public Class[] KeyTypes = { IPrimaryKey.class };

	/** Overloaded PersistAble Constructor initializing by the primary key 	  */
	public static PersistAble newInstance (PersistAble ths, IPrimaryKey Key) { //
		Object[] Params = {Key}; //
		try { //this Implementation is slow, but generic!
			Constructor C = ths.getClass().getConstructor(KeyTypes);
			return (PersistAble) C.newInstance(Params); }
//			return (PersistAble) this.getClass().getConstructor(KeyTypes).newInstance(Params); }
		catch (InstantiationException x) { throw new InstantiationError(x.toString()); }
		catch (IllegalAccessException x) { throw new IllegalAccessError(x.toString()); }
		catch (NoSuchMethodException  x) { throw new NoSuchMethodError (x.toString()); }
		catch (InvocationTargetException x) { throw new IllegalAccessError(x.toString()); }
	}

	///////////////
	//  Member Variables
	///////////////

	/** Reference to the Class of Objects to be created and cached	 */
	protected PersistAble Factory;

	/** String Constant for the Table Name */
	protected final String TableName;

	//List of Object Fields, their Names, DB Names and concatenated List.

	/** Array of Field Names */
	protected final Field[] Fields; //getFields ()};

	/** String Constants for the Field Names */
	protected final String[] FieldNames;

	/** String Constants for the DBField Names */
	protected final String[] DBFieldNames;

	/** String Constant for the DBField Names concatenated by ',' */
	protected final String strDBFieldNames;

	//the same for the Keys only

	/** Array of Field Names */
	protected final Field[] Keys; //getKeys ()};

	/** String Constants for the key Names */
	protected final String[] KeyNames;

	/** String Constants for the DB key Names */
	protected final String[] DBKeyNames;

	/** String Constant for the key Names concatenated by ',' */
	protected final String strDBKeyNames;

	/** String Constant for the first Part of the UPDATE Statement */
	protected final String strUpdate;

	/** String Constant for the first Part of the INSERT Statement */
	protected final String strInsert;

	/** String Constant for the first Part of the DELETE Statement */
	protected final String strDelete;

	/** String Constant for the first Part of the SELECT Statement,
	  * containing only the non key Fields  */
//	protected final String strSelect;

	/** String Constant for the first Part of the SELECT Statement inclusive Keys */
	protected final String strSelAll;

	/** Reference to the DB Connection */
	protected Connection Conn;

//	private int attribute1;

	/** Prepared Statement to load a single Object by ID from the DB
	  * Statements and Prepared Statements cannot be reused! */
//	protected Statement StateMt; //
//	protected PreparedStatement StateMt; //

	////////////////////
	//  Constructors  //
	////////////////////

    /** Creates new DBObjectFactory
	 *  Initialize the Prepared Statement and the MaxID  */
    public DBObjectFactory (Connection conn, PersistAble Factory) {
		this.Factory = Factory;
		TableName = Factory.TableName();
		DBFieldNames = Factory.DBFieldNames();
		FieldNames = Factory.FieldNames();
		Fields = Factory.Fields();
		IPrimaryKey Key = Factory.primaryKey();
		DBKeyNames = Key.DBKeyNames();
		KeyNames = Key.KeyNames();
		Keys = Key.Keys();
		String str 		= AStreamOut.ARRAY_TO_STRING(DBFieldNames, ",");
		strDBKeyNames   = AStreamOut.ARRAY_TO_STRING(DBKeyNames  , ",");
		strDBFieldNames = str.isEmpty() ? str : str.substring(0, str.length()-1);
		strUpdate = STR_UPDATE_ + Factory.TableName() + STR_SET_;
		strInsert = STR_INSERT_INTO_ + Factory.TableName() + '(' + strDBKeyNames + strDBFieldNames + STR_VALUES_;
		strDelete = STR_DELETE_FROM_ + Factory.TableName(); // + STR_WHERE_;
		strSelAll = STR_SELECT_ + strDBKeyNames + strDBFieldNames + STR_FROM_ + Factory.TableName(); // + STR_WHERE_;
//		strSelect = STR_SELECT_ + strDBFieldNames + STR_FROM_ + Factory.TableName(); // + STR_WHERE_;
		this.Conn = conn;
    }

	///////////////
	//  Methods  //
	///////////////

	/**
	 * Deletes the row the object's primary key identifies.
	 *
	 * @return true when exactly one row was deleted
	 * @throws SQLException when the statement fails
	 */
	public boolean deleteObject(PersistAble obj) throws SQLException {
		StringBuffer SB = new StringBuffer(strDelete).append(obj.primaryKey().Condition());
		return (1 == Conn.createStatement().executeUpdate(SB.toString())); } //create a new Statement for each Query!

	/**
	 * Inserts the object as a new row.
	 *
	 * @return true when exactly one row was inserted
	 * @throws SQLException when the statement fails
	 * @throws IllegalAccessError when a field cannot be read reflectively
	 */
	public boolean insertObject(PersistAble obj) throws SQLException {
//		IPrimaryKey Key = obj.primaryKey();
		StringBuffer SB = new StringBuffer(strInsert);
		int i;
		//the column list is the keys first and then the fields, each in array order, so the
		//values have to be appended in that same order or they land in the wrong columns.
		try { //should never happen!
			i = -1; while (++i < Keys  .length) SB.append(literal(Keys  [i].get(obj))).append(",");
			i = -1; while (++i < Fields.length) SB.append(literal(Fields[i].get(obj))).append(",");
		} catch (IllegalAccessException x) { throw new IllegalAccessError(x.toString()); }
		SB.setCharAt(SB.length()-1, ')');
		return (1 == Conn.createStatement().executeUpdate(SB.toString())); } //create a new Statement for each Query!

	/** Update this single Object in the DB.
	  * Doesn't change the Primary key of course, since this is the key to the Row.
	  * Returns true when updated.  */
	public boolean updateObject(PersistAble obj) throws SQLException {
//		IPrimaryKey Key = obj.primaryKey();
		StringBuffer SB = new StringBuffer(strUpdate);
		int i = Fields.length;
		while (--i >= 0) try { //for all Fields... IllegalAccessException should never happen!
			SB.append(DBFieldNames[i]).append("=").append(literal(Fields[i].get(obj))).append(","); //rs.getObject(DBFieldNames[i]));
		} catch (IllegalAccessException x) { throw new IllegalAccessError(x.toString()); }
		SB.setCharAt(SB.length ()-1, ' ');
		SB.append(obj.primaryKey().Condition()); //Condition() already opens with WHERE
		return (1 == Conn.createStatement().executeUpdate(SB.toString())); } //create a new Statement for each Query!

	/** Retrieve this Object from the DB.
	 *  The primary key must be set for this,
	 *  otherwise an IllegalStateException is thrown
	 *  Returns null when not found.  */
	public ArrayList getObjects(String condition) throws SQLException {
		ArrayList ret = new ArrayList(); //ArrayList is not synchronized
		String SQL = strSelAll;
		if (condition != null) SQL += condition;
		ResultSet rs = Conn.createStatement().executeQuery(SQL); //create a new Statement for each Query!
		while (rs.next ())
			ret.add(getObject(rs));
		return ret; }

	/** Reads a single (new) Object from the ResultSet
	  * and fills it into the Fields Collection of the Target Object
	  * Prerequisite is the Resultset being filled with the correct Order of Fields */
//	protected static PersistAble getObject(Field[] Fields, Field[] Keys, ResultSet rs) throws SQLException {
	protected PersistAble getObject(ResultSet rs) throws SQLException {
		PersistAble ret = Factory.newInstance((IPrimaryKey)null); //
		try { //should never happen!
			int i = Fields.length + Keys.length+1; //the Fields also contain the Key!
			int j = Fields.length; while (--j >= 0) Fields[j].set(ret, rs.getObject(--i)); //rs.getObject(DBFieldNames[i]));
			int k =   Keys.length; while (--k >= 0)   Keys[k].set(ret, rs.getObject(--i)); //rs.getObject(DBFieldNames[i]));
		} catch (IllegalAccessException x) { throw new IllegalAccessError(x.toString()); }
		return ret; } //this prevents Memory to be overfilled when no longer used!

	/** Loads a new Object from the Cache or the DB
	 *  Working with weak References to allow for unloading these cached Objects
	 *  when they are not used.
	 */
	public PersistAble getObject(IPrimaryKey Key) throws SQLException { //, InstantiationException, IllegalAccessException {
		PersistAble ret = null;
//		PreparedStatement pStateMt = conn.prepareStatement (Status.STR_Select_ + STR_TableName + STR_Where + "?");
//		pStateMt.setLong (1, ID); //only for PreparedStatement; not implemented for generic ODBC Bridge or ACCESS Driver
		String SQL = strSelAll + Key.Condition();
		ResultSet rs = Conn.createStatement().executeQuery(SQL); //create a new Statement for each Query!
//		Result.first(); //not supported in Default RS; forwardOnly!
		while (rs.next ())
			ret = getObject(rs);
		return ret; } //this prevents Memory to be overfilled when no longer used!

	////////////////////////////////////////////////////////////////////////////
	//	Test Methods
	////////////////////////////////////////////////////////////////////////////

	/** Main Method: Tests all Methods of this Class	*/
	public static void main(String[] args)
	throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		testIt(args); }

	/** Tests all Methods of this Class	*/
	public static void testIt(String[] args)
	throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver"); //loads the Driver, which registers with the DriverManager
//		Enumeration Drivers = DriverManager.getDrivers ();
		Connection conn = DriverManager.getConnection ("jdbc:odbc:TypedKnowledge");
//		Statement StateMt = conn.createStatement();
		IdKey Key1 = new IdKey(1);
		IdKey Key2 = new IdKey(2);
		DbCachedFactory.initFactories (conn);
//		StringAttribute SA = DbCachedFactory.FactoryString.getObject (new BasicAttribute(
/*		ArrayList allString = DBObjectFactory.FactoryString.getObjects ("");
		ArrayList allMetric = DBObjectFactory.FactoryMetric.getObjects ("");
		ArrayList allEnums  = DBObjectFactory.FactoryEnum  .getObjects ("");
		ArrayList allTimes  = DBObjectFactory.FactoryTime  .getObjects ("");
*/		ArrayList allStatus = DBObjectFactory.FactoryStatus.getObjects ("");
		AStreamOut.ARRAY_TO_STREAM(System.out, allStatus.toArray(), ",");
		Status st = (Status) DBObjectFactory.FactoryStatus.getObject(Key1); //MetaTypes don't have a Status, they are permanent!
		ArrayList tmp = st.relatedObjects(DBObjectFactory.FactoryType);
		AStreamOut.ARRAY_TO_STREAM(System.out, tmp.toArray(), ",");
		System.out.println(st.getDescription());
		MetaType MT = (MetaType) DBObjectFactory.FactoryMetaType.getObject(Key1);
		MT = (MetaType) DBObjectFactory.FactoryMetaType.getObject(Key1);
		System.out.println(MT.getDescription());
		Objekt Ob = (Objekt) DBObjectFactory.FactoryObject.getObject(Key2);
		System.out.println(Ob.getDescription());
		ArrayList relatedObjects = Ob.relatedObjects(DBObjectFactory.FactoryObject);
		AStreamOut.ARRAY_TO_STREAM(System.out, relatedObjects.toArray(), ",");
		relatedObjects = Ob.relatedSubjects();
		Object[] arr = relatedObjects.toArray();
		AStreamOut.ARRAY_TO_STREAM(System.out, arr, ",");
		int i = arr.length;
		while (--i >= 0) System.out.println(arr[i].getClass().getName());
		Type Ty = (Type) DBObjectFactory.FactoryType.getObject(Key1);
		System.out.println(Ty.getDescription());
	}

}