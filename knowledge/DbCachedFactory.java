package knowledge;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.WeakHashMap;

/**
  * DbCachedFactory.java
  *
  * Maintains a Set of Objects weakly cached in a WeakHashTable
  * (i.e. Objects that can be Garbage Collected when not used)
  * read from the DB.
  *
  * Created on 10. Mai 2001, 22:26
  *
  * @author  Matthias Heuer
  * @version
  */
public class DbCachedFactory 
extends DBObjectFactory {

	//////////////////////
	//  static Members  //
	//////////////////////
	
	//////////////////////
	//  static Methods  //
	//////////////////////
	
	public static void initCachedFactories(Connection C) throws SQLException {
		FactoryStatus   = new DbCachedFactory(C, new Status  (0)); 
		FactoryObject   = new DbCachedFactory(C, new Objekt  (0)); 
		FactoryMetaType = new DbCachedFactory(C, new MetaType(0)); 
		FactoryType     = new DbCachedFactory(C, new Type    (0)); 
		FactoryString   = new DbCachedFactory(C, new StringAttribute(0, 0, 0, "")); 
		FactoryMetric   = new DBObjectFactory(C, new MetricAttribute(0, 0, 0, 0.0)); 
		FactoryEnum     = new DBObjectFactory(C, new   EnumAttribute(0, 0, 0, 0  )); 
		FactoryTime     = new DBObjectFactory(C, new   TimeAttribute(0, 0, 0, new java.util.Date(0))); 
	}
	
	///////////////
	//  Members  //
	///////////////
	
	/** Largest ID as primary key for the DB,
	  * can be used for all Classes or only a single one.
	  * Made private to prevent reuse in the Child Classes.
	  * Only Object that are stored in the same Table need to be indexed together
	  * for other Classes create a separate ObjectFactory!
	  * The only Problem about external Factories is that they need Access
	  * to the Fields of the Object.
	  *
	  * Convention:
	  *        0 means Insert (new Object)
	  * positive means Update
	  * negative means Delete
	  */
	protected long MaxID; //
	
	/**
	  * Flag to indicate whether the Objects are to be stored into the DB
	  * Should be set AFTER all unnecessary / temporary Objects are destroyed
	  * so the Rest of the Objects are guaranteed to be stored into the DB.
	  * Alternative the Method saveAll() could be called.  */
	public boolean storeInDB;
	
	/** Local static List to cache the already loaded Objects
	  * Need to use a WeakHashMap, since these Objects are usually their own Primary Keys. 
	  * On Destruction these Objects have to update the DB! */
	protected WeakHashMap LoadedObjects = new WeakHashMap();
	
	////////////////////
	//  Constructors  //
	////////////////////
	
    /** Creates new DbCachedFactory */
    public DbCachedFactory (Connection conn, PersistAble Factory) throws SQLException { 
		super(conn, Factory); 
		try { 
			ResultSet rs = conn.createStatement().executeQuery(STR_Select_Max + TableName);
			while (rs.next ())
				MaxID = rs.getLong(1);
		} catch(SQLException x) { }
	}

	///////////////
	//  Methods  //
	///////////////
	
	/** Reads a single (new) Object from the ResultSet	*/ 
	protected PersistAble getObject(ResultSet rs) throws SQLException {
		PersistAble ret2;  
		PersistAble ret = super.getObject(rs); //read it from the DB
		IPrimaryKey Key = ret.primaryKey(); //Try to find this Object in the current Set and return that one, if found. 
		if ((ret2 = getCachedObject(Key)) != null) return ret2; 
		LoadedObjects.put(Key, new WeakReference(ret)); //don't store a Strong Reference
		return ret; }
	
	/** Reads a cached Object. If the Object is not cached, 
		or has been Garbage Collected, returns null */
	public PersistAble getCachedObject(IPrimaryKey Key) { 
		WeakReference wr = (WeakReference) LoadedObjects.get(Key);
		if (wr == null) return null; 
		return (PersistAble) wr.get(); }	//rely on the Ref pointing to the correct Type!
	
	/** Loads a new Object from the Cache or the DB
	 *  Working with weak References to allow for unloading these cached Objects
	 *  when they are not used.
	 */
	public PersistAble getObject(IPrimaryKey Key) throws SQLException { //, InstantiationException, IllegalAccessException {
		PersistAble ret; 
		if ((ret = getCachedObject(Key)) != null) return ret; 
		return super.getObject(Key); }
	
}