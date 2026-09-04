package swing;

import java.awt.ComponentOrientation;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import streamIO.object.enumer.container.HashContainer;
import streamIO.object.parser.jdbc.ResultSetSep;
import tester.ITester;

/**
  * JHyperTree.java
  *
  * Created on 16. Dezember 2001, 03:23
  *
  * A Tree Implementation to display Networks as HyperGraphs 
  * with the current Node as the common Root of two mirroring TreeViews. 
  * 
  * @author  mheuer
  */
public class JHyperTree
extends javax.swing.JPanel
implements ITester {

	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** left Tree Component 	*/
	private javax.swing.JTree TreeLeft;

	/** right Tree Component 	*/
	private javax.swing.JTree TreeRight;

	/** left Tree Nodes 	*/
	private HashContainer LeftNodes;

	/** right Tree Nodes 	*/
	private HashContainer RightNodes;

	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX/isXXX/makeXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Getter for property treeLeft.
	  * @return Value of property treeLeft.
	  */
    public JTree getTreeLeft() { return TreeLeft; }

    /** Getter for property treeRight.
	  * @return Value of property treeRight.
	  */
    public JTree getTreeRight() { return TreeRight; }

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Creates new JHyperTree Component */
	public JHyperTree() { this (null, null, null); }

	/** Creates new JHyperTree Component
	  * with Knowledge of the left and right Graph Models */
	public JHyperTree(HashContainer LeftNodes_, HashContainer RightNodes_, Object rootID) {
		this.RightNodes = RightNodes_;
		this. LeftNodes =  LeftNodes_;
		initComponents(rootID);
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////////
	
	/** This method is called from within the constructor to
	  * initialize the form.
	  */
	private void initComponents(Object rootID) {//
		setLayout(new  java.awt.BorderLayout()); //Panel has FlowLayout as Default!
		TreeLeft  = new javax.swing.JTree();
		TreeRight = new javax.swing.JTree();
		add(TreeLeft,  java.awt.BorderLayout.WEST);
		add(TreeRight, java.awt.BorderLayout.CENTER);
		TreeLeft .setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		TreeRight.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		TreeLeft .getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		TreeRight.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		TreeLeft .setShowsRootHandles(true);
		TreeRight.setShowsRootHandles(true);
		if ((RightNodes != null) && (LeftNodes != null)) {
			TreeLeft .addKeyListener(new KeyTreeNavigator(TreeLeft , LeftNodes , this));
			TreeRight.addKeyListener(new KeyTreeNavigator(TreeRight, RightNodes, this));
			if (rootID != null) { setRoot(rootID); }
		}
	}//

	/** Sets the Attribute on both Trees	*/
	public void setAutoscrolls(boolean scrolls) {
		TreeRight.setAutoscrolls(scrolls);
		TreeLeft .setAutoscrolls(scrolls); }

	/** Sets the Models of both Trees	*/
	public void setModel(TreeModel modelLeft, TreeModel modelRight) {
		TreeRight.setModel(modelRight);
		TreeLeft .setModel(modelLeft ); }

	/** Sets the Models of both Trees based on the root Nodes.	*/
	public void setRoots(TreeNode rootLeft, TreeNode rootRight) {
		TreeRight.setModel(new DefaultTreeModel(rootRight));
		TreeLeft .setModel(new DefaultTreeModel(rootLeft )); }

	/** Sets the Models of both Trees based on the common root Node.
	  * Also tries to optimize the Trees based on the Lists of Nodes.  */
	public void setRoot(Object rootID) {
		DefaultMutableTreeNode root;
		if (((DefaultMutableTreeNode) TreeRight.getModel().getRoot()).getUserObject() != rootID) {
			root = (DefaultMutableTreeNode) RightNodes.findFirst(rootID);
			HashTreeNode.fixTreeModel(root, RightNodes);
			TreeRight.setModel(new DefaultTreeModel(root)); } //
		if (((DefaultMutableTreeNode) TreeLeft .getModel().getRoot()).getUserObject() != rootID) {
			root = (DefaultMutableTreeNode) LeftNodes.findFirst(rootID);
			HashTreeNode.fixTreeModel(root, LeftNodes);
			TreeLeft .setModel(new DefaultTreeModel(root)); } //
	}

	/** Sets the Attribute on both Trees	*/
	public void setRootVisible(boolean visible) {
		TreeRight.setRootVisible(visible);
		TreeLeft .setRootVisible(visible); }

	/** Sets the Attribute on both Trees	*/
	public void setScrollsOnExpand(boolean scrolls) {
		TreeRight.setScrollsOnExpand(scrolls);
		TreeLeft .setScrollsOnExpand(scrolls); }

	/** Sets the Attribute on both Trees	*/
	public void setShowsRootHandles(boolean show) {
		TreeRight.setShowsRootHandles(show);
		TreeLeft .setShowsRootHandles(show); }

	////////////////////////////////////////////////////////////////////////////
	//  Event Interface ITester: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** Event Callback for the Change of Roots
	  * The same Callback is used for both Trees
	  */
	public boolean test(Object arg) {
		setRoot(arg);
		return false; }

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + JHyperTree.class.getName());
		String FileName = "../../Databases/CycOnto/IsA_.txt";
		ResultSetSep rs = new ResultSetSep(FileName, "\n\t");
		try { HashTreeNode.loadDb2Tree(rs, 0, 1, "Tree", "InvTree");
		} catch (SQLException x) { }
		HashContainer    treeMap = HashTreeNode.getTree(   "Tree");
		HashContainer invTreeMap = HashTreeNode.getTree("InvTree");
		//Create the scroll pane and add the tree to it.
		String rootID = "Collection"; //RadiansPerSecond";  //
//		HashTreeNode.fixTreeModel(invTreeMap, rootID);
		JHyperTree hyperTree = new JHyperTree(invTreeMap, treeMap, rootID);
		JScrollPane scrollPane = new JScrollPane(hyperTree);
		JFrame frame = new JFrame();
		frame.getContentPane().add(scrollPane);
		frame.addWindowListener(new WindowAdapter() { //end the App
			public void windowClosing(WindowEvent e) { //when closing this Window
				System.exit(0);	} } );
		frame.pack();
		frame.setVisible(true);
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
