package knowledge;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.WeakHashMap;

/**
  * A {@link DBObjectFactory} that returns the same instance for the same primary key for
  * as long as anything still holds it, by caching loaded objects weakly.
  *
  * <p>The cache is a {@link WeakHashMap} keyed by the object's own primary key, holding a
  * {@link WeakReference} to the object. Both halves are deliberate: these objects are
  * usually their own key, so a strong key would pin them, and a strong value would defeat
  * the point of unloading unused rows.
  *
  * <p><b>Identity, not just speed:</b> because the same key yields the same instance, a
  * caller may compare loaded objects by reference and may mutate one knowing every holder
  * sees the change. That guarantee lasts only while a strong reference exists somewhere.
  *
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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T08:15:05Z
  * digest: f67c870a8da4593e7b6730db628bb74fb020aa5596bca60c4fe8a15f25bfbaad
  * stale: false
  * -->
  */
public class DbCachedFactory 
extends DBObjectFactory {

	//////////////////////
	//  static Members  //
	//////////////////////
	
	//////////////////////
	//  static Methods  //
	//////////////////////
	
	/**
	 * Installs a cached factory over the given connection for each of the eight object
	 * kinds this package persists.
	 *
	 * <p>Only the first four - status, object, meta-type and type - become caching
	 * factories; the four primitive attribute kinds stay plain {@link DBObjectFactory}
	 * instances, because an attribute is identified by its type/subject/status triple
	 * rather than by a stable ID and so gains nothing from an identity cache.
	 *
	 * @param C the connection every installed factory will read through
	 * @throws SQLException when a factory cannot be prepared against the connection
	 */
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
		// TODO: LOGIC: the failure is swallowed whole, leaving MaxID at 0; a caller cannot tell
		// a table whose largest ID really is 0 from one whose max query failed, and the
		// 0-means-insert convention documented on MaxID then applies to every object.
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