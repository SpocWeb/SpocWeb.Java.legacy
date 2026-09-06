/*
 * Created on 11.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.enumer.container;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a lightweight, fuzzy RDF Value Triple 
 * which should be able to represent the whole World including Fuzziness. 
 * 
 * @author heuerm
 * 
 * <!-- docstate
 * tags: [code/container, code/hash_table, code/container_iteration]
 * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
 * facets: {layer: utility, status: legacy, complexity: high}
 * digest: 3e8d00e9cb04e522be2f2ef730e93b2287575433417a82840becd521c6ff4814
 * stale: false
 * -->
 */
public class TypedAssociation 
extends Edge {
	
	/** Creates a TypedAssociation with only a Key.
	 * @param _key
	 */
	public TypedAssociation(final Object _key) { super(_key); }

	/** Creates a TypedAssociation with a Key and Value.
	 * @param _key
	 * @param _value
	 */
	public TypedAssociation(final Object _key, final Object _value) { super(_key, _value); }

	/** Creates a TypedAssociation with a Key, Value and Weight.
	 * @param _key
	 * @param _value
	 * @param _weight
	 */
	public TypedAssociation(final Object _key, final Object _value, final double _weight) {
		super(_key, _value, _weight); }

	/** Creates a TypedAssociation with a Key, Value, Weight and relationship Type
	 * (the Predicate of the RDF Triple).
	 * @param _key
	 * @param _value
	 * @param _weight
	 * @param _type
	 */
	public TypedAssociation(final Object _key, final Object _value, final double _weight, final Object _type) {
		super(_key, _value, _weight); 
		this.typ = _type; 
	}
	
	/** reads all available Data from the ResultSet	 */
	public TypedAssociation(final ResultSet rs, final int[] cols) throws SQLException {
		super(rs.getObject(cols[0]), 
			  rs.getObject(cols[1])); 
		this.weight = (cols[2] >= 0 ? rs.getFloat (cols[2]) : 1); 
		this.typ    = (cols[3] >= 0 ? rs.getObject(cols[3]) : null); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * publicly accessible Type of the Relation / Edge / Association 
	 * Defines the Relationship Type
	 * This is the Predicate of the RDF Triple: 
	 * Key = Subject
	 * Typ = Predicate
	 * Val = Object	
	 * 
	 * Weight = (fuzzy) Strength of the Relation (normed to 1)
	 */
	public Object typ; 
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Object Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Renders this TypedAssociation as "key-type(weight)->value".
	 * @see java.lang.Object#toString()	 */
	public String toString() {
		return String.valueOf(key)+"-"+String.valueOf(typ)+"("+weight+")->"+String.valueOf(val);
	}
	
}
