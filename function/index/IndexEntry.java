/*
 * Created on 15.03.2005
 *
 */
package function.index;

import function.AOrderAble;
import function.ICountAble;
import function.IIOrderAble;
import function.IMeasurAble;
import function.byref.ByRefInt;
import graphs.IPair;
import graphs.IValue;

/**
 * Stores an Object Reference together with an Index. 
 * The natural Order is derived from the Key, not the Index/Value!!!
 * Can be used in a Container like HashSet as well as in a (sorted) Array or a SortedSet. 
 * Alternatively and even faster the Objects to index can be derived directly from this Class. 
 * <br/>
 * Since an Index defines a fully ordered Set, it is natural to assume
 * that the Values implement IOrderAble, IMeasurAble or Comparable. 
 * <br/>
 * An alternative (Memory-minimalistic) Approach would be to 
 * maintain only an int[] Array and use a dynamic (but cached e.g. in another Array) 
 * Retrieval of the Values for Comparison.
 * <br/>
 * An Array of 'IndexElement' Objects provides a changeable bijective Mapping
 * between the Objects and the Index,
 * although a HashTable is apted to handle arbitrary Objects, since
 * all Objects implement the 'hashCode()' Method, but usually not 'Indexed'.
 * After an Array of 'Indexed' Objects has been 'index()'ed,
 * any Sequence of Transformations can be undone or evaluated
 * by calling 'unMap()' or 'getInt()' (which returns the Inverse).
 * On Demand, the Array can be re-Indexed()
 *
 * @see java.util.SortedSet implemented by TreeSet
 * @see streamIO.copy.monoid.Association
 * @see streamIO.copy.monoid.Association which allows only an untyped Access to the Key / Index. 
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:41:40Z
 * digest: 67d73bb3aed59ac84546d912deba46de725f87c9887f2f99467afa783225a34b
 * stale: false
 * tags: [code/indexing]
 * concepts: [Indexed Collection Access]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class IndexEntry 
extends AOrderAble
implements IMeasurAble, Comparable, ICountAble, IIndexAble, IValue, IPair //would restrict Usability, rather use a Comparator
{
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * (Re-)Indexes through the given Array to set all the Indices.
	 * Any Change in Order requires this Re-Indexing.
	 * The Indices are a Permutation of the Sequence then.
	 */
	public static void index(final IIndexAble[] arr) {
		IIndexAble obj;
		for (int i = arr.length; --i >= 0; )
			if ((obj = arr[i]) != null)
				obj.setNdx (i);
	}
	
	/**Returns a new Array of the given Size,
	 * filled with new Objects of the given Type.
	 * The Type has to have an empty Constructor for this to work. */
	final static public Object[] CREATE_FILLED_ARRAY(int Length, Class Type) throws
		InstantiationException, IllegalAccessException {
		Object[] ret = new Object[Length];
		while (--Length >= 0)
			ret[Length] = Type.newInstance();
		return ret; }

	/**
	 * Returns an Index to the List of Objects in the given Array in Place.
	 * This is a Permutation of the Sequence and the Inverse to unMap().
	 */
	final static public int[] GET_INDEX_AT(final ICountAble[] arr, final int[] Index) {
		ICountAble obj;
		//if ((i = arr.length) != Index.length) throw new
		for (int i = arr.length; --i >= 0; )
			if ((obj = arr[i]) != null)
				Index[obj.getInt()] = i;
		return Index; }

	/**
	 * Returns an Index to the List of Objects in the given Array.
	 * The is a Permutation of the Sequence and the Inverse to unMap().
	 */
	final static public int[] GET_INDEX(final ICountAble[] arr) { 
		return GET_INDEX_AT(arr, new int[arr.length]); }

	/**
	 * Returns the List of all Positions of the Objects in the given Array in Place.
	 * This returns the Permutation that arr has undergone after calling index()
	 * which is the Inverse Permutation to getIndex().
	 * It corresponds to the unMap Method of the Monoid,
	 * but requires indexing the Array beforehand
	 */
	final static public int[] UN_MAP_AT(final ICountAble[] arr, final int[] Position) {
		ICountAble obj;
		int i = arr.length; //if ((i = arr.length) != Index.length) throw new
		while (--i >= 0)
			if ((obj = arr[i]) != null)
				Position[i] = obj.getInt();
		return Position; }

	/**
	 * Returns the List of all Positions of the Objects in the given Array in Place.
	 * This returns the Permutation that arr has undergone after calling index()
	 * which is the Inverse Permutation to getIndex().
	 * It corresponds to the unMap Method of the Monoid,
	 * but requires indexing the Array beforehand
	 */
	final static public int[] UN_MAP(final ICountAble[] arr) { 
		return UN_MAP_AT(arr, new int[arr.length]); }
	
	///////////////////////////////////////////////////////////////////////////

	/** The Index of the Value	 */
	public int index;

	/** The Index of the Value	 */
	public int getInt() { return index; }
	
	/** The Index of the Value	 */
	public int getNdx() { return index; }
	
	/** The Index of the Value	 */
	public void setNdx(final int _index) { this.index = _index; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** The Value at the index Position	 */
	public Object key; 

	/** The Value at the index Position	 */
	public Object getKey() { return key; }

	/** The Value at the index Position	 */
	public void setKey(final Object _value) { this.key = _value; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	public IndexEntry() { }
	
	/** Constructor for Subclasses being directly indexed. 	 */
	public IndexEntry(final int _index) { 
		this.key = this; 
		this.index = _index; }
	
	/** Initializing Constructor	 */
	public IndexEntry(final Object _Value, final int _index) {
		this.index = _index; 
		this.key = _Value; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Interface IPair Implementation
	///////////////////////////////////////////////////////////////////////////
	
	/** Sets {@link #index} by converting {@code _val} to an {@code int}.
	 * @see graphs.IPair#setKey(java.lang.Object)	 */
	public void setVal(final Object _val) { index = ByRefInt.TO_INT(_val); }

	/** Returns {@link #index} boxed as an {@link Integer}.
	 * @see graphs.ICPair#getKey()	 */
	public Object getVal() { return new Integer(index); }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface ICountAble Implementation
	////////////////////////////////////////////////////////////////////////////////

	/** Returns {@link #index} narrowed to a {@code byte}.
	 * @see function.ICountAble#getByte()	 */
	public byte getByte() { return (byte) index; }

	/** Returns {@link #index} widened to a {@code long}.
	 * @see function.ICountAble#getLong()	 */
	public long getLong() { return index; }

	/** Returns {@link #index} narrowed to a {@code short}.
	 * @see function.ICountAble#getShort()	 */
	public short getShort() { return (short) index; }

	///////////////////////////////////////////////////////////////////////////
	/// Interface IMeasurAble Implementation
	///////////////////////////////////////////////////////////////////////////

	/** Delegates to {@link #key}, which must implement {@link IMeasurAble}.
	 * @see function.IMeasurAble#getDouble()	 */
	public double getDouble() { return ((IMeasurAble) key).getDouble(); }

	/** Delegates to {@link #key}, which must implement {@link IMeasurAble}.
	 * @see function.IMeasurAble#getFloat()	 */
	public float getFloat() { return ((IMeasurAble) key).getFloat(); }

	///////////////////////////////////////////////////////////////////////////
	/// Interface IOrderAble Implementation
	///////////////////////////////////////////////////////////////////////////

	/** Delegates to {@link #key}, which must implement {@link IIOrderAble}.
	 * @see function.IIOrderAble#isLessThan(java.lang.Object)	 */
	public boolean isLessThan(final Object arg) {
		return ((IIOrderAble) key).isLessThan(arg);
	}

	/** Compares this entry's {@link #key} to {@code arg}'s key.
	 * @see function.IIOrderAble#isLessThan(java.lang.Object)	 */
	public int compareTo(final Object arg) {
		return ((Comparable) key).compareTo(((IndexEntry)arg).key);
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  java.lang.Object: Implementation
	////////////////////////////////////////////////////////////////////////////

	/**Returns a hash code Value for the object.
	 * With the Association the HashCode is exactly the key's HashCode!
	 * This has to be redefined if the Association is used to (recursively)
	 * cluster two Arguments which is done in String.DynTransByFunction.
	 *
	 * This method is supported for the benefit of hashtables
	 * such as those provided by <code>java.util.Hashtable</code>.
	 * <p>
	 * The general contract of <code>hashCode</code> is:
	 * <ul>
	 * <li>Whenever it is invoked on the same object more than once during
	 * an execution of a Java application, the <code>hashCode</code> method
	 * must consistently return the same integer. This integer need not
	 * remain consistent from one execution of an application to another
	 * execution of the same application.
	 * <li>If two objects are equal according to the <code>equals</code>
	 * method, then calling the <code>hashCode</code> method on each of the
	 * two objects must produce the same integer result.
	 * </ul>
	 *
	 * @return  a hash code Value for this object.
	 * @see java.lang.Object#hashCode()
	 * @see     java.util.Hashtable
	 * @since   JDK1.0	 */
	public int hashCode(){ 
		if (key == null) 
			return 0; 
		return key.hashCode(); 
	}

	/** Compares two Objects for equality.
	 * <p>
	 * The <code>equals</code> method implements an equivalence relation:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any reference Value <code>x</code>,
	 * <code>x.equals(x)</code> should return <code>true</code>.
	 * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and
	 * <code>y</code>, <code>x.equals(y)</code> should return
	 * <code>true</code> if and only if <code>y.equals(x)</code> returns
	 * <code>true</code>.
	 * <li>It is <i>transitive</i>: for any reference values <code>x</code>,
	 * <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
	 * returns  <code>true</code> and <code>y.equals(z)</code> returns
	 * <code>true</code>, then <code>x.equals(z)</code> should return
	 * <code>true</code>.
	 * <li>It is <i>consistent</i>: for any reference values <code>x</code>
	 * and <code>y</code>, multiple invocations of <code>x.equals(y)</code>
	 * consistently return <code>true</code> or consistently return
	 * <code>false</code>.
	 * <li>For any reference Value <code>x</code>, <code>x.equals(null)</code>
	 * should return <code>false</code>.
	 * </ul>
	 * <p>
	 * The equals method for class <code>Object</code> implements the most
	 * discriminating possible equivalence relation on objects; that is,
	 * for any reference values <code>x</code> and <code>y</code>, this
	 * method returns <code>true</code> if and only if <code>x</code> and
	 * <code>y</code> refer to the same object (<code>x==y</code> has the
	 * Value <code>true</code>).
	 * @param arg The Object to be checked for Equality
	 * @return <code>true</code> if this object is the same as the obj
	 * argument; <code>false</code> otherwise.
	 * @see java.lang.Object#equals(java.lang.Object)	 
	 * @see java.util.Hashtable#
	 */
	public boolean equals  (final Object arg) {
		if (arg == null ) 
			return false; //(self == null);
		if (arg == this ) 
			return true; 
		if (arg == key) 
			return true; 
		if (arg instanceof IndexEntry)
			return equals((IndexEntry) arg); 
		return key.equals(arg); 
	}
	
	/** Compares this entry to another by their {@link #key} values. */
	public boolean equals(final IndexEntry arg) {
		return key.equals(arg.key);
	}

	/** Returns the key and index in the form {@code (key@index)}.
	 * @see java.lang.Object#toString()	 */
	public String toString() {
		return "("+key+"@"+index+")";
	}
	
}
