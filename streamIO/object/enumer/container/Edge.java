package streamIO.object.enumer.container;

import java.sql.ResultSet;
import java.sql.SQLException;

import graphs.KeyValuePair;

/**Title:        Edge<p>
 * Description:  Lightweight Edge Class for defining weighted Graphs/Relations. <p>
 * Copyright:    Copyright (c) Matthias Heuer<p>
 * Company:      personal<p>
 * 
 * similar Classes: 
 * @see Pair
 * Used only passively in: 
 * @see streamIO.object.enumer.container.Function
 * @see streamIO.object.enumer.container.Relation 
 * 
 * @author Matthias Heuer
 * @version 1.0
 */
public class Edge 
extends KeyValuePair // Association { //Pair
//implements IMeasurAble  
{  
	
	/** The Weight of this Edge. 
	 * Indicates either the Strength of the Association (fuzziness, normed to 1)
	 * or the Distance between the Nodes     */
	public double weight;
	
	/** @return the Weight of this Edge     */
	public double getWeight() { return weight; }

	/** @return the Weight of this Edge     */
	public void setWeight(final double _weight) {
		//final float ret = Weight;
		this.weight = _weight; 
		//return Weight; //brief Implementation fosters fast Inlining
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param _key
	 */
	public Edge(final Object _key) {
		super(_key);
	}
	
	/**
	 * @param _key
	 * @param _value
	 */
	public Edge(final Object _key, final Object _value) {
		super(_key); 
		this.val = _value; 
	}
	
	/**Empty Constructor     */
	//public Edge(final Object _key) { super(_key); }

	/** Constructor with the full Information for an Association
	 * @param _key key for this Association.
	 * It's hashcode is used
	 * as the hashcode for the whole Association
	 * @param _value The Target Object of this Association
	 */
	public Edge(final Object _key, final Object _value, final double _weight) {
		super(_key); 
		this.val = _value; 
		this.weight = _weight; 
	}
	
	/** reads all available Data from the ResultSet	 */
	public Edge(final ResultSet rs, final int[] cols) throws SQLException {
		super(        rs.getObject(cols[0])); 
		this.val    = rs.getObject(cols[1]) ; 
		this.weight = (cols[2] >= 0 ? rs.getFloat (cols[2]) : 1); 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Object Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** @see java.lang.Object#toString()	 */
	public String toString() { 
		return String.valueOf(key)+"-"+weight+"->"+String.valueOf(val); 
	}
	
}
