package swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import streamIO.object.enumer.container.HashContainer;
import tester.ITester;

/** Helper Class to catch Keys pressed on a JTree and react accordingly:
  * 'R' changes the Root  of the Tree to the selected Node
  * 'F' changes the Focus of the Tree to the selected Node Representative
  */
public class KeyTreeNavigator
extends KeyAdapter {

	/** key for making the selected Node the Root of the Tree.  */
	final static public char Key4Root  = 'R';

	/** key for navigating to the Main Representative of the selected Node */
	final static public char Key4Focus = 'F';

	/** Reference to the Tree being watched	*/
	protected JTree tree;

	/** Reference to the Root of the Tree being watched	*/
//	protected TreeNode root;

	/** Reference to the Root Change Event Subscriber
	  * The ID of the selected Root is the Parameter of the Test() Method */
	protected ITester subScriber;

	/** Reference to the Set of tree Nodes.
	  * Sets are Necessary to put the Focus on the Node identified by the userObject.  */
	protected HashContainer treeMap;

	//Needs a Callback Method to notify others of the Root Change Event
	/** Initializing Constructor
	  * @param tree_    the JTree Component being watched. This creates a circular Relation
	  * @param treeMap_ the List of Tree Nodes
	  * @param rootID_  the ID of the Tree Root in the treeMap_  */
/*	public KeyTreeNavigator(JTree tree_, HashContainer treeMap_, ITester subscriber_) {
		this(tree_, treeMap_, subscriber_); }

	/** Initializing Constructor
	  * @param tree_    the JTree Component being watched. This creates a circular Relation
	  * @param treeMap_ the List of Tree Nodes
	  * @param root_    the current Root of the Tree */
	public KeyTreeNavigator(JTree tree_, HashContainer treeMap_, ITester subScriber_) {
		this.treeMap = treeMap_;
		this.subScriber = subScriber_;
		this.tree = tree_; }

	/** Actual Event Routine 	*/
	public void keyTyped(KeyEvent e) {
		char Key = Character.toUpperCase(e.getKeyChar());
		DefaultMutableTreeNode selNd = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent(); if (selNd == null) return;
		DefaultMutableTreeNode repNd = (DefaultMutableTreeNode) treeMap.findFirst(selNd.getUserObject());
		switch (Key) {
		case Key4Root: //don't use the Node as a root directly, switch to it's Representative!
			tree.setModel(new DefaultTreeModel(repNd)); //very rapid Change of Model! ;-) No need to reconstruct the Tree Structure!!!
			if (subScriber != null) {	//also the Representative has already been optimized and does not need another fix!
				subScriber.test(repNd.getUserObject()); }
			break; //set the new Root
		case Key4Focus:  //change the Focus
			TreeNode[] lst = null, objLst = repNd.getPath(); //possibly strip this List until the last common Root is found...
			Object root = tree.getModel().getRoot(); //rather get the Root dynamically than cacheing it!
			int len = objLst.length;
			while (--len >= 0) { //search for the current Root in the List...
				if (root == objLst[len]) {
					lst = new TreeNode[objLst.length - len];
					System.arraycopy(objLst, len, lst, 0, lst.length);
					break; }
			} //...and strip the Start, if found
			if (lst == null) return; //otherwise don't jump, because the Rep. is not in the currently displayed Subtree
			TreePath ndPath = new TreePath(lst); //convert the Array into a List
			tree.setSelectionPath(ndPath);
			tree.scrollPathToVisible(ndPath);
			break; //set the Focus to the main Representative
		default :
//			System.out.print(Key);
		}
	}
}
