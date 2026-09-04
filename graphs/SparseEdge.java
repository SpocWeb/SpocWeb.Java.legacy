package graphs;

/**
 * Container Record Class for the Adjacency List. 
 * Allows to concatenate Edges. 
 * 
 * Subclasses: 
 * 
 * Analogous to: 
 * @see graphs.Edge a lightweight Value Object with the similar Fields, except for next
 * but with additional key Field. 
 * @see streamIO.Object.Enumerator.ListItem
 * could also be used as an untyped Class,
 * but that creates some Overhead.
 * Using a separate List Implementation 
 * would make some things more transparent.
 * 
 * used in: 
 * @see function.derive.neuron.KohonenGraph 
 * @see graphs.SparseMatrix which uses these Elements 
 * to build the List of connected Nodes.
 * 
 * This Implementation using a linked List is most efficient, 
 * unlike the direct Implementation using Object Maps, 
 * because Search Operations for Objects can be of O(1), but not inexpensive! 
 * 
 * Arrays of primitive Types are most efficient, 
 * but should be used only for simple Cases. 
 * As soon as different Types (int, float, Object etc.) 
 * need to be mixed, a typed Record Object should be used, 
 * for groupig related Data and adding Functionality (Objects), 
 * although creating a Data/Record/Value Object is more expensive 
 * than maintaining the Array Values.
 * 
 * TODO: maybe derive this Class from graph.Edge 
 * but that would make it non-final and add the Key Value 
 */
public class SparseEdge {
	
	/**Link to the next Edge in the linked List	 */
	public SparseEdge next;
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Source Node of the Edge, not necessary, since a ListEdge always resides in the Key's Linked List	
	 * @see SparseEdgeStream compensates for this by currItem.key   */
	//public int key;
	
	/**Target Node of the Edge	 */
	public int val;
	
	/**Weight of the Connection
	 * Has to be ordered and an additive Group,
	 * because used in calculating the minimum Path etc. 
	 * Unweighted Graphs could be stored in a simple int[] Array
	 */
	public float weight;
	
	///////////////////////////////////////////////////////////////////////////
	
	/** optional, Typization of the Edges to extend it to a full RDF Representation, 
	 * to be able to distinguish them, especially in graphical Representations.	
	 */
	public int typ;
	
	/**
	 * returns true if this Edge is of the given Type. 
	 * @param _typ >= 0 checks, negative Values select all Edges
	 * @return true if this Edge is of the given Type. 
	 */
	final public boolean isTyp(final int _typ) { return (_typ < 0) || (_typ == typ); } 
	
	///////////////////////////////////////////////////////////////////////////
	/// These Mappings can be done by a single Array and not by any Edge! 
	
	/**Source Node of the Edge, to save mapping back from Index to Object 
	 * optional Reference to Object represented by the Source Node. 
	 * This Object could be used to extend the Edge with additional Properties 
	 * or just to better identify it. 
	 * The Objects can be independently maintained in an Index
	 * to be able to quickly find them, 
	 * but for small Sets a linear Search through the Root Items would be sufficient.
	 * A re-sorting is not easy, since all Lists have to be re-indexed. 
	 */
	//public Object key;
	
	/**Target Node of the Edge, to save mapping back from Index to Object	 */
	//public Object val;
	
	/**Edge / Relation Type, to save mapping back from Index to Object	 */
	//public Object typ;
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/**Initializing Constructor	 */
	public SparseEdge(final int node, final float weight_) {
		this.val = node; this.weight = weight_; //this.next = null; this.typ = type_; 
	}
	
	/**Initializing Constructor	 */
	public SparseEdge(final int node, final float weight_, final int type_) {
		this.val = node; this.weight = weight_; //this.next = null; 
		this.typ = type_; 
	}
	
	/**Initializing Constructor	 */
	public SparseEdge(final int node, final SparseEdge next) {
		this.val = node; this.next = next; //this.typ = 0; this.weight = 1;
	}
	
	/**Initializing Constructor	 */
	public SparseEdge(final int node_, final SparseEdge next_, final float weight_) {
		this.val = node_; this.next = next_; this.weight = weight_; //this.typ = 0; 
	}
	
	/**Initializing Constructor	
	 * 
	 * @param node_
	 * @param next_ Reference to the next Node in this linked List
	 * @param weight_ Weight of the Edge represented by this Node
	 * @param type_ not used 
	 */
	public SparseEdge(final int node_, final SparseEdge next_, final float weight_, final int type_) {
		this.val = node_; this.next = next_; this.weight = weight_; 
		this.typ = type_; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Object Methods
	///////////////////////////////////////////////////////////////////////////
	
	/**Returns a String Representation of this Subtree	 */
	public String toString() {
		StringBuffer S = new StringBuffer();
		SparseEdge x = this;
//		do S.append ((char) (x.Node + 'A')).append('(').append(x.Weight).append("),"); while ((x = x.Next) != null); //use Characters for Output
		do S
		.append (Integer.toString(x.val)).append('(')//.append(x.type)
		.append(',').append(x.weight).append("),"); while ((x = x.next) != null); //use Numbers for Output
		return S.toString(); }
	
	/** 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object obj) {
		if (!(obj instanceof SparseEdge)) 
			return false; 
		return equals((SparseEdge) obj); 
	}
	
	/**
	 * Checks for Equality 
	 * can handle null for both arg1 and arg2! 
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	public static boolean EQUALS(final SparseEdge arg1, final SparseEdge arg2) {
		//quick Check
		if (arg1 == arg2) 
			return true;
		//make sure arg1 != null
		if (arg1 == null) 
			return arg2 == null; 
		return arg1.equals(arg2); 
	}
	
	/** 
	 * Since ListEdge is a pure Value Object, 
	 * Equivalence is totally defined by its Values 
	 * and the Values of its Successors. 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final SparseEdge arg) {
		//make sure arg != null
		if (arg == null) 
			return false; 
		//Check the Members
		if (arg.typ != typ) //!isTyp(arg.typ)) 
			return false; 
		if (arg.val != val) 
			return false; 
		if (arg.weight != weight) 
			return false; 
		//recursively check the Object Members
		return EQUALS(next, arg.next);
	}

}
