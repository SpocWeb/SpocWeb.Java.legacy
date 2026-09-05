package graphs;

/**
 * Edge.java
 * Value Class to transfer the Characteristics of an Edge 
 * identified by integer Values (e.g. Array Indices). 
 *
 * Created on 10. Februar 2002, 18:35
 *
 * Known SubClasses: @see graphs.TypedEdge
 *
 * Known Uses: 
 * @see graphs.SparseMatrix 
 * @see graphs.MatrixGraph
 * @see graphs.IGraph
 *
 * A typed Edge is necessary for Graphs 
 * with different Types of Edges, 
 * where you want to e.g. filter out certain Types.  
 *
 * Typed Edges are required to reflect generic Relations 
 * e.g. in the RDF Framework. 
 * 
 * Design Decisions: 
 * Alternatively if you don't want to specify the Type, 
 * instead of inheriting, you could also use the Decorator Pattern, 
 * but this is more complex and uses more Space and Indirections. 
 * It only applies if several independent Dimensions of Specialization 
 * have to be combined. 
 * Using a Default Type of 0 is OK. 
 * 
 * Similar Classes: 
 * @see graphs.SparseEdge could inherit from Edge, adding the nextItem Reference, 
 * but this would make this Class less lightweight 
 * and would introduce the redundant 'Key' into ListEdge.  
 * 
 * @author  mheuer
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:42:47Z
 * digest: c971612b7288d3d6a2bd3fd853bc31c62da385b0af85d30d259edd3e33c82533
 * stale: false
 * tags: [code/graph_edge]
 * concepts: [Graph Edge]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
final public class Edge
implements IPair {
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** publicly accessible key == originating Node == Row
	 * This is redundant for a Sparse Graph, 
	 * since the ListEdge appears only in the List of it's Key Index!	*/
	public int key;
	
	/** publicly accessible Value == targeted Node == Column	*/
	public int val;
	
	/**
	 * Type of this Edge; 
	 * int to be able to index an Array (e.g. of Objects or Strings)
	 */
	public int typ; 
	
	/** publicly accessible Weight of the Edge / Association	*/
	public float weight;
	
	/**
	 * returns true if this Edge is of the given Type. 
	 * @param _typ >= 0 checks, negative Values select all Edges
	 * @return true if this Edge is of the given Type. 
	 */
	public boolean isTyp(final int _typ) { return (_typ < 0) || (_typ == typ); } 
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** Accessor Method
	 * @return the key of the Pair  */
	public Object getKey() { return new Integer(key); }
	
	/** Accessor Method
	 * @return the Value of the Pair  */
	public Object getVal() { return new Integer(val); }
	
	/** Accessor Method
	 * @param sets the key of the Pair  */
	public void setKey(final Object key_) { 
		key = ((Integer) key_).intValue(); 
	}
	
	/** Accessor Method
	 * @param sets Value of the Pair  */
	public void setVal(final Object value_) { 
		val = ((Integer) value_).intValue(); 
	}
	
	/** Accessor Method
	 * @return the Type of this Edge, boxed  */
	public Object getTyp() {
		return new Integer(typ);
	}

	/** Accessor Method
	 * @param sets Value of the Pair  */
	public void setTyp(final Object type_) { 
		typ = ((Integer) type_).intValue(); }

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Creates an empty new instance of Edge */
	public Edge() { }

	/** Creates a new instance of Edge */
    public Edge(final int key_, final int value_) {
		this.key    = key_;
		this.val  = value_;
		this.weight = 1; }

	/** Creates a new instance of Edge */
    public Edge(final int key_, final int value_, final float weight_) {
		this.key    = key_;
		this.val    = value_;
		this.weight = weight_;
    }

	/** Creates a new instance of Edge */
    public Edge(final int key_, final int value_, final double weight_) {
		this.key    = key_;
		this.val    = value_;
		this.weight = (float) weight_;
    }

	/** Creates a new instance of Edge */
    public Edge(final Edge original) {
		this.key    = original.key;
		this.val    = original.val;
		this.typ    = original.typ;
		this.weight = original.weight;
    }

}
