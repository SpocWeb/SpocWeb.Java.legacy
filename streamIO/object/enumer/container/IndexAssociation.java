/*
 * Created on 14.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.enumer.container;

import java.sql.ResultSet;
import java.sql.SQLException;

import function.index.IIndexAble;

/**
 * Common Base Class for HashEntry and TreeEntry
 * @see streamIO.object.enumer.container.HashEntry 
 * @see streamIO.object.enumer.container.tree.TreeMapEntry
 * @author heuerm
 * <!-- docstate
 * tags: [code/container, code/hash_table, code/container_iteration]
 * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
 * facets: {layer: utility, status: legacy, complexity: high}
 * digest: fe93e1a03f99d4cca4179a3b04e774bc6a9e33daac100e6b7a2146efdd667104
 * stale: false
 * -->
 */
public class IndexAssociation 
extends TypedAssociation 
implements IIndexAble {
	
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Reference to the integer Value mapped to the Key (to stay type-safe).
	 * Stores the Index / Value of the Key, to save creting a ByRefInt for val. 
	 * Also used to count the Number of Instances in a Bag
	 */
	public int ndx; 
	
	/** Sets the integer Index/Value mapped to this Association's Key.
	 * @see function.index.IIndexAble#setNdx(int)	 */
	public void setNdx(final int _index) { this.ndx = _index; }

	/** Returns the integer Index/Value mapped to this Association's Key.
	 * @return the integer Index/Value mapped to this Association's Key
	 * @see function.index.IIndexAble#setNdx(int)	 */
	public int getNdx() { return ndx; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Creates an IndexAssociation with only a Key.
	 * @param _key
	 */
	public IndexAssociation(final Object _key) {
		super(_key);
	}

	/** Creates an IndexAssociation with a Key and Value.
	 * @param _key
	 * @param _value
	 */
	public IndexAssociation(final Object _key, final Object _value) {
		super(_key);
		this.val = _value;
	}

	/** Creates an IndexAssociation with a Key, Value and Weight.
	 * @param _key
	 * @param _value
	 * @param _weight
	 */
	public IndexAssociation(final Object _key, final Object _value, final float _weight) {
		super(_key);
		this.val = _value;
		this.weight = _weight;
	}

	/** Creates an IndexAssociation with a Key, Value, Weight and integer Index.
	 * @param _key
	 * @param _value
	 * @param _weight
	 * @param _index
	 */
	public IndexAssociation(final Object _key, final Object _value, final float _weight, final int _index) {
		super(_key); 
		this.val = _value;
		this.weight = _weight;
		this.ndx = _index; 
	}
	
	/** reads all available Data from the ResultSet	 */
	public IndexAssociation(final ResultSet rs, final int[] cols) throws SQLException {
		super(                                             rs.getObject(cols[0])); 
		if (cols.length > 1) this.val    =                 rs.getObject(cols[1]) ; 
		if (cols.length > 3) this.typ    = (cols[3] >= 0 ? rs.getObject(cols[3]) : null); 
		if (cols.length > 2) this.weight = (cols[2] >= 0 ? rs.getFloat (cols[2]) : 1); 
		if (cols.length > 4) this.ndx    = (cols[4] >= 0 ? rs.getInt   (cols[4]) : 1); 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Object Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Renders this IndexAssociation as "key-type(weight)->ndx/value".
	 * @see java.lang.Object#toString()	 */
	public String toString() {
		return String.valueOf(key)+"-"+String.valueOf(typ)+"("+weight+")->"+ndx+"/"+String.valueOf(val);
	}
	
}
