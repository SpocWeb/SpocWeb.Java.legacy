package swing;

import graphs.ICPair;
import graphs.KeyValuePair;
import graphs.PairVal;
import graphs.SparseMatrix;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import streamIO.IIStreamIn;
import streamIO.object.enumer.container.HashContainer;
import streamIO.object.enumer.container.Relation;
import streamIO.object.parser.jdbc.ResultSetSep;

/**
  * HashTreeNode.java
  *
  * Helper TreeNode Class for swing.
  * Overrides equals() and hashCode() Methods
  * to allow for being hashed by it's Contents i.e. UserObject.
  *
  * The ideal Model for a TreeNode can be seen from it's Data structure:
  * a UserObject and the single Parent, which guarantees a Hierarchy!
  * The Children Vector of the Tree is automatically maintained by setting the Parent Property
  * and builds the corresponding Adjacency List Representation.
  *
  * This can be stored in a relational Table with 2 or more Columns
  * It can also be stored in a Collection of Associations (HashMap!),
  * where the unique key is the UserObject and the Value is the Parent.
  * This enforces Tree Consistency, since the key is unique
  * and can thus only have a single Parent!!!
  *
  * The Hashtable Implementation of the Tree, where the Value represents a List of Children
  * cannot guarantee Uniqueness of Parent since the same Child can appear multiple Times.
  * This is equivalent to the Adjacency List Representation of a Graph.
  *
  * This Implementation delegates the equals() and hashCode() Methods to the UserObject
  * and is thus customary for Container Objects like the TreeNode should be!
  * It is stored in a HashContainer to allow for fast retrieval.
  * Instead of a HashMap, a HashSet would be sufficient,
  * if it would return the Element, which only HashContainer does.
  * @see graphs.ICPair where the same is required for Pairs and Associations.
  *
  * For a general Graph where each Node may have several Parents,
  * Dummy Nodes have to be created.
  * These Dummy Nodes will have the same Label as their Main Representative,
  * but no Child Nodes, since these are only added to the Main Representative.
  * This is made Standard Behavior here, so also general Graphs can be loaded into a JTree.
  */
public class HashTreeNode
	extends DefaultMutableTreeNode {

////////////////////////////////////////////////////////////////////////////////
//  static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** HashMap containing all Tree Sets by their Tree Names	  */
	final static public Relation treeList = new Relation();

	/** Determines whether Diamonds are possible in Graphs	*/
	public static boolean showRedundant = true;

	/** String used to indicate possible Subnodes of replicated Nodes
	  * in Graphs with Diamonds (only there a Node may appear as a duplicate Child)	*/
	public static String strDiamond = "...";

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the HashMap by their Map Name
	  * If the hashMap does not exist yet, it is being created and added.
	  * but the latter requires ALWAYS to instante a new HashContainer.
	  * This is moved to the Relation Interface.
	  */
	public static HashContainer getTree(final Object TreeID_) {
		try {
			return (HashContainer) treeList.getAt(TreeID_, HashContainer.class);
		} catch (InstantiationException x) { //should never happen!
			return null; }
	}

	/** adds the given Node to the given Tree by it's Names. 	  */
	public static void addNode(Object TreeID_, HashTreeNode Node_) {
		getTree(TreeID_).addAt(Node_); }

	/** HashMap containing all HashMaps by their Map Name
	  * This allows for maintaining an arbitrary Number of Node Collections,
	  * one for each Tree.
	  */
	public static void removeNode(Object TreeID_, Object NodeID_) {
		getTree(TreeID_).removeItem(NodeID_); }

	/** @return the given Node of the given Tree by their Names. 	  */
	public static HashTreeNode getNode(Object TreeID_, Object NodeID_) {
		return (HashTreeNode) getTree(TreeID_).findFirst(NodeID_); }

	/** Loads the Adjacency List into a Tree Model.	 */
	public static void loadAdjacencyList2Tree(SparseMatrix AL, Object TreeID_, Object InvTreeID_) {
		PairVal currEdge;
		IIStreamIn iter = AL.Iterator();
		while ((currEdge = (PairVal) iter.nextItem()) != null) {
			if (   TreeID_ != null) { new HashTreeNode(currEdge.val,    TreeID_, currEdge.Key); }
			if (InvTreeID_ != null) { new HashTreeNode(currEdge.Key, InvTreeID_, currEdge.val); }
		}
	}

	/** Loads the Adjacency Matrix into a Tree Model.	 */
/*	public static void loadAdjacencyMatrix2Tree(MatrixGraph AM, Object TreeID_, Object InvTreeID_) {
		IStreamIn iter = AM.;
		while (iter.nextItem() != null) {
		}
	}
*/
	/** Loads the DB into a Tree Model.	 */
	public static void loadDb2Tree(ResultSet rs, int child, int parent, Object TreeID_, Object InvTreeID_) throws SQLException {
		String strChild;
		String strParent;
		while (rs.next()) {
			strChild  = rs.getString(child ).trim();
			strParent = rs.getString(parent).trim();
//			System.out.println(strChild) +  ", " + strParent));
			if (   TreeID_ != null) { new HashTreeNode(strChild ,    TreeID_, strParent); }
			if (InvTreeID_ != null) { new HashTreeNode(strParent, InvTreeID_, strChild ); }
		}
//		return (HashMap) treeList.get(TreeID_);
	}

	/** Recursively rearranges the Nodes so that all Representative Nodes
	  * are moved to the subTree starting at the given root.
	  * This is necessary to give a satisfactory View Experience
	  * on the JHyperTree etc.
	  */
	public static void fixTreeModel(HashContainer nodes, Object rootID) {
		fixTreeModel((DefaultMutableTreeNode) nodes.findFirst(rootID), nodes); }

	/** Recursively swaps the Nodes so that all Representative Nodes
	  * are moved to the subTree starting at the given root.
	  * This is necessary to give a satisfactory View Experience on the JHyperTree.
	  */
	public static void fixTreeModel(final DefaultMutableTreeNode root, final HashContainer nodes) {
/*		Enumeration enm = root.children(); //enm is Invalidated, create a local Copy!
		while (enm.hasMoreElements()) {	//do a recursive In Order Traversal
			child = (DefaultMutableTreeNode) enm.nextElement();
*/		int len2, len1;
		DefaultMutableTreeNode[] children = new DefaultMutableTreeNode[len1 = len2 = root.getChildCount()];
		while (--len1 >= 0) {
			children[len1] = (DefaultMutableTreeNode) root.getChildAt(len1); }
		while (--len2 >= 0) {
			DefaultMutableTreeNode child = children[len2];
			if  (child.getChildCount() > 0) {
				child = fixDiamond(root, nodes, child);
			}	//but there is no easier Alternative!
			fixTreeModel(child, nodes); //Recursion!
		}
	}

	/** @see #fixTreeModel(DefaultMutableTreeNode, HashContainer) uses this Method exclusively 	 */
	private static DefaultMutableTreeNode fixDiamond(
		final DefaultMutableTreeNode root,
		final HashContainer nodes,
		DefaultMutableTreeNode child) {
		final Object usr = ((DefaultMutableTreeNode) child.getChildAt(0)).getUserObject();
		if((usr instanceof KeyValuePair) && ((KeyValuePair) usr).val == strDiamond) { //if encountering a Diamond Node...
			final DefaultMutableTreeNode repNd = (DefaultMutableTreeNode) nodes.findFirst(child.getUserObject());
			final DefaultMutableTreeNode parNd = (DefaultMutableTreeNode) repNd.getParent(); //identify the Main Representative and it's Parent
			if (!root.isNodeAncestor(repNd)) {
					root .add(repNd);
				if (parNd != null) { //switch Diamond Node and Main Representative in their Parents.
					parNd.add(child); //add() automatically calls remove() from the other Parent!
				} else {
					root .remove(child); }
				child = repNd; //prepare fixing recursive Children!
			} //this possibly leads to some superfluous Switches,
		}	//when the same Nodes appear multiple Times in this Subtree
		return child;
	}

	/** @return an Array of HashTreeNodes wrapping the given Array of User Objects
	  * The Nodes are also added to the given Tree
	  * After creating the Nodes, their Parents can be set separately.
	  */
	public static HashTreeNode[] wrapObjects(Object[] obj, Object TreeID_) {
		int l;
		HashTreeNode[] ret = new HashTreeNode[l = obj.length];
		while (--l >= 0) {
			ret[l] = new HashTreeNode(obj[l], TreeID_); }
		return ret; }

	/** creates a Tree with the given TreeID_
	  * and adds all Associations to it.
	  */
//	private static void createTree(Container Associations, Object TreeID_) { }

	//A Tree can be created from a List of Pairs: Child-Parent
	//with unique Children this becomes a Tree or Forest, otherwise a Graph
	//
	//A Tree can also be created from a List of Collections: Parent-Children
	//with unique / disjoint Children this becomes a Tree or Forest, otherwise Diamonds and Cycles
	//

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor, leaving UserObject and Parent to null	*/
	public HashTreeNode() { }

	/** Constructor taking the UserObject,
	  * and the Tree this Node should be added to.
	  * This Node has no Parent.
	  */
	public HashTreeNode(Object userObject_, Object TreeID_) {
		this(userObject_, TreeID_, null); }

	/** Constructor taking the UserObject and the ParentID
	  * as key and Value in the Association
	  * (the latter must be equal to the UserObject of the Parent Node)
	  * as well as the Parent this Node should be added to, in the given Tree.
	  * If the Tree does not exist, it is automatically created!
	  * The Parent Node has to exist in the Tree, so the Sequence of Creation
	  * has to proceed Top Down, which is guaranteed by most Tree Walks.
	  */
	public HashTreeNode(ICPair userObject_Parent, Object TreeID_) {
		this(
			userObject_Parent.getKey(), TreeID_,
			userObject_Parent.getVal()); }

	/** Constructor taking the UserObject,
	  * the Tree this Node should be added to,
	  * and the ParentID, which must be equal to the UserObject of the Parent Node
	  * in the given Tree.
	  * If the Tree does not exist, it is automatically created!
	  * The Parent Node has to exist in the Tree, so the Sequence of Creation
	  * has to proceed Top Down, which is guaranteed by most Tree Walks.
	  * The Number of Children is not available (yet) and cannot be appended to the Display
	  * nor can it help to determine whether the ... are necessary!
	  */
	public HashTreeNode(Object userObject_, Object TreeID_, Object parentID_) {
		super(userObject_);	//set the User Object
		HashTreeNode rep;
		HashContainer tree = getTree(TreeID_);
		//check whether the Node already exists
		if((rep = (HashTreeNode) tree.findFirst(userObject_)) == null) { //doesn't exist yet...
			rep = this;	         tree.addItem(this);	//this could be further optimized, by reusing the Position from find(), but not done yet...
		} else if (rep.getParent() == null) { //a preliminary Node resulting from unsorted adding of Relations exists,
			//don't use this Node but the existing one as the rep...
		} else if (showRedundant) {	//Main Representative already exists
			rep = this;	//don't add this to the tree Map.
			//If numChild > 0  //if the Node has Children, add '...' //not available
			if (!(userObject_ instanceof KeyValuePair) || (((KeyValuePair) userObject_).val != strDiamond)) { //prevent Recursion!
				this.add(new HashTreeNode((Object) new KeyValuePair(userObject_ , strDiamond), TreeID_)); //add the dotted Node to indicate the Substitute and facilitate Navigation
			}
		} else { //throw an Exception if a Diamond Scheme exists...
		}
		if (parentID_ == null) return;
		HashTreeNode par = (HashTreeNode) tree.findFirst(parentID_);
		if (par == null) { //create a Dummy Parent Node to be filled later?!?
			par =  new HashTreeNode(parentID_, TreeID_);
		}
		try { par.add(rep);  //only add() ensures Maintenance of the Parent's Children List!
		} catch (IllegalArgumentException x) {
			System.out.println("Cycle: Parent '" + parentID_ + "' has Child '" + userObject_ + "' already as an Ancestor !"); }
	}

	//Recursively fills the Tree Nodes from the RAM Model (Collection of Collections == Adjacency List)
	//The Structure of the Collection is the same as one of the resulting Tree.
	//Where does the Node Name come from?

	public HashTreeNode(Object userObject_) {
		super(userObject_); }

	public HashTreeNode(Object userObject_, HashTreeNode parent_) {
		super(userObject_);
//		parent = parent_;
//		setParent(parent_); //this also does NOT enter the Object in the Parent's Children List!
		parent_.add(this); }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	  * @return a HashCode based on the UserObject, instead of the TreeNode.
	  * since the userObject cannot be protected, the Value cannot be cached.
	  */
	public int hashCode() {
		if (userObject == null) return 0;
		return userObject.hashCode(); }

	/**
	  * @return true, if the Argument arg equals the UserObject.
	  */
	public boolean equals(Object arg) {
		if (arg == this) return true; //Optimization
		if (arg == userObject) return true; //Optimization
		if (arg instanceof DefaultMutableTreeNode) {
			arg = ((DefaultMutableTreeNode) arg).getUserObject();
			if (arg == userObject) return true; //Optimization
			if (arg == null) return false; //
			return arg.equals(userObject); //
		} else {
			return userObject.equals(arg) || arg.equals(userObject); }
	}

////////////////////////////////////////////////////////////////////////////
//	static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/** Tests the Hashing of this Class	 */
	public static void testHash() {
		String strHallo = "Hallo";
		HashTreeNode htn = new HashTreeNode(strHallo);
/*		HashMap test1 = new HashMap();
		test1.put(htn, htn); //"Hallo"
		System.out.println(test1.get(strHallo));
*/		Relation test = new Relation();
		test.addItem(htn, htn); //"Hallo"
		System.out.println(test.getAt(strHallo));
	}

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException, FileNotFoundException, SQLException {
		System.out.println("Testing " + HashTreeNode.class.getName());
		testHash();
		String FileName = "E:/Personal/Databases/CycOnto/IsA_.txt";
		ResultSetSep rs = new ResultSetSep(FileName, "\n\t");
		loadDb2Tree(rs, 0, 1, "Tree", null);//"InvTree");
		HashContainer    treeMap = getTree(   "Tree");
//		HashContainer invTreeMap = getTree("InvTree");
		TreeNode root; // = (TreeNode) treeMap.get("1662");
/*		root = (TreeNode) treeMap.get("ScriptType");
		Enumeration enumChild = root.children();
		while (enumChild.hasMoreElements()) { //proof that only Children with later Names are added to their Parent!
			System.out.println(enumChild.nextElement()); }
*/		root = (TreeNode) treeMap.findFirst("Collection");
		DefaultTreeModel treeModel = new DefaultTreeModel(root);
		JTree tree = new JTree(treeModel); //
		tree.addKeyListener(new KeyTreeNavigator(tree, treeMap, null));
		tree.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT); // LEFT_TO_RIGHT);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		//Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);
		JFrame frame = new JFrame();
		frame.getContentPane().add(treeView, BorderLayout.CENTER);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.pack();
		frame.setVisible(true);
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException, FileNotFoundException, SQLException {
		testIt(args); }

}
