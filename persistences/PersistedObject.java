package persistences;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import streamIO.copy.monoid.Association;

/**
  * Base Class for persistent Objects, identified and hashed by a non-semantic String ID.
  *
  * But to support Persistence and dynamic Relations
  * these are built using Associations by ID
  * rather then by direct Pointers.
  * Pointers will be used only within Routines to speed up Processing and Data Access.
  * 
  * Overrides equals() and hashCode() Methods
  * to allow for being hashed by it's Contents.
  *
  * This is customary for Container Objects like the TreeNode should be!
  * @see Association
  *
  * Subclasses:
  *
  * Similar Classes:
  * @see knowledge.IdKey defines the Root Class for IPersistAble Objects with ID
  * @see knowledge.DirtyFlag defines a Flag indicating whether Data has been changed.
  *
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:12:58Z
  * digest: 8c74a4945e1684e9b6224126a3dfd0dde5638e956448c64311cc1e1a997e7df7
  * stale: false
  * tags: [code/entity_model, code/registry_pattern]
  * concepts: [Record Identity, Persistence]
  * facets: {layer: persistence, status: broken, complexity: low}
  * -->
  */
public class PersistedObject
extends knowledge.DirtyFlag {

////////////////////////////////////////////////////////////////////////////////
//  static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the HashMap containing all active Persisted Objects
	  * Newly created Objects are automatically added to this Collection.
	  */
	private static HashMap objects = new HashMap();

////////////////////////////////////////////////////////////////////////////////
//  static Methods 
////////////////////////////////////////////////////////////////////////////////

	/** Returns a loaded Object from it's ID or null, if the Object does not exist.
	  * Newly created Objects are automatically added to this Container.
	  * There is no Possibility that this Method returns a null
	  * that has been explicitly added!
	  */
	public static PersistedObject getObject(String ID) {
		return (PersistedObject) objects.get(ID); } //

////////////////////////////////////////////////////////////////////////////////
//  Variables 
////////////////////////////////////////////////////////////////////////////////

	/**
	  * The non semantic ID is used to retain Relations between Objects
	  * in the persisted Format.
	  * The ID is analogous to the Address of the Object,
	  * a Reference or Pointer, but permanent and Platform and Media independent.
	  *
	  * It is most important that the ID is not semantic
	  * and only used for Reference Purposes.
	  * That is why the ID is not made public and no Accessor Methods exist.
	  * @see java.lang.Integer is identical to this Class but final!
	  *
	  * A universal HashMap is necessary as a Substitute for the Address System.
	  * This HashMap can also be used to control Cacheing of the persisted Objects.
	  *
	  * For hashing, an Object is necessary and sufficient
	  * but by using int as an ID a Decision is made against e.g. URLs as IDs
	  * A String is probably the best Compromise, although this requires
	  * a Call Overhead and is not as fast in Comparisons.
	  * The most generic Alternative is to use an Object as the PrimaryKey Class
	  *
	  * The IDCode is the cached hashValue of the ID key given in the Constructor.
	  * This is an Optimization used to speed up Comparisons.
	  */
//	private int IDCode;

	/**
	  * The non semantic ID is used to retain Relations between Objects
	  * in the persisted Format.
	  * The ID is analogous to the Address of the Object,
	  * a Reference or Pointer, but permanent and Platform and Media independent.
	  *
	  * It is most important that the ID is not semantic
	  * and only used for Reference Purposes.
	  * That is why the ID is not made public and no Accessor Methods exist.
	  * @see java.lang.Integer is identical to this Class but final!
	  *
	  * A universal HashMap is necessary as a Substitute for the Address System.
	  * This HashMap can also be used to control Cacheing of the persisted Objects.
	  *
	  * For hashing, an Object is necessary and sufficient
	  * but by using int as an ID a Decision is made against e.g. URLs as IDs
	  * A String is probably the best Compromise, although this requires
	  * a Call Overhead and is not as fast in Comparisons.
	  * The most generic Alternative is to use an Object as the PrimaryKey Class
	  */
	private String ID;

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns the ID of this Object
	  * This is necessary for establishing and storing a Relation from other Objects.
	  * The ID can even be a serialized concatenated key
	  * What to do with concatenated Keys of non primary Objects?
	  * N:M Relations consist of concatenated Keys,
	  * but they are not used individually.
	  */
	public String getId() { return ID; }

	/** Returns the ID of this Object
	  * This is necessary for establishing and storing a Relation from other Objects.
	  * The ID can even be a serialized concatenated key
	  * What to do with concatenated Keys of non primary Objects?
	  * N:M Relations consist of concatenated Keys,
	  * but they are not used individually.
	  */
	public void setId(String ID_) {
		if (ID_ == null) return; //allows for multiple tries
		if (ID  != null) { //if already initialized: throw Error!
			throw new IllegalAccessError(PersistedObject.class.getName() + ".setId"); }
		this.ID = ID_;
//		this.IDCode = ID_.hashCode();
		objects.put(ID_, this); }

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super() 
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor for later initialization e.g. from the DB	 */
	public PersistedObject() { } //this(null); } //the latter is equivalent!

	/** Initializing Constructor
	  * The ID is expected in the Column named "ID"
	  * The ResultSet can not be moved to the next Row,
	  * because the Result of next() should be tested in the outer Loop
	  * and the super(rs) call has to happen first thing!
	  */
	public PersistedObject(ResultSet rs) throws SQLException {
		this(rs.getString("ID")); }

	/** Initializing Constructor
	  * The ID is used for hashing and also as the Result of the toString Method.
	  * The Object has no Identity except for the given ID.
	  */
	public PersistedObject(String ID_) {
		if (ID_ != null) {
			setId(ID_); }
	}

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/** Returns this Object's ID.
	  * @return the ID as the Description of the Object.
	  */
	public String toString() {
		return ID; }

	/** Hashes this Object by its ID.
	  * @return a HashCode based on the ID of the Object
	  */
	final public int hashCode() {
//		return IDCode; }
		return ID.hashCode(); }

	/** Compares by ID, not by reference identity alone.
	  * @return true, if the Argument arg equals the UserObject.
	  */
	final public boolean equals(Object arg) {
		if (arg == this) return true; //Optimization
		if (arg instanceof PersistedObject) {
			PersistedObject arg_ = (PersistedObject) arg;
//			if (IDCode != arg_.IDCode) return false; //definitely different
			if (ID == arg_.ID) return true; //definitely identical
			return ID.equals(arg_.ID);
		} else {
			return false; }
	}


}
