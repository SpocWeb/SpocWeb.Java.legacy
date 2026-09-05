package streamIO.object.parser;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import streamIO.IIStreamIn;
import streamIO.object.enumer.container.Array;
import swing.HashTreeNode;

/**
  * Bridge Class to mediate between StreamIn and JTree
  *
  * Implements two Methods to fill Trees from Streams:
  * 1) using nested Streams to an arbitrary Level.
  * 	this can be used for Files or Memory Structures
  * 2) using relational Data by keeping a Hash Reference
  * 	to the Nodes already created and thus ensuring
  * 	the Integrity of the Tree, because only the Parent Node is given
  * 	by it's UserObject.
  * <!-- docstate
  * tags: [code/stream_parsing, code/parser]
  * concepts: [Separator-Driven Token Parsing and Stream Adapters]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class JTreeContainer {

	/**
	  * Fills the Tree from the given StreamIn Interface
	  * Filling is done according to the nested StreamIn Metaphor:
	  * adding an IStreamIn results in a new Subtree being added.
	  *
	  * The Control changes from the Streamer Routine to this Routine,
	  * although this requires programming the while Loop
	  * and the nested Streamer Routine seems much more elegant.
	  *
	  * Eventually no external Streamer Method will be necessary,
	  * because the add Method streams on it's own!
	  */
	final static public DefaultMutableTreeNode fillTreeFromStreamIn(IIStreamIn in) {
		DefaultMutableTreeNode ret = new DefaultMutableTreeNode();
		Object  currItem;
		while(((currItem  = in.nextItem()) != IIStreamIn.EOI) || in.isValid()) { //Stream of Attributes
			if (currItem == null) {
				currItem  = ""; }
			if (currItem  instanceof  IIStreamIn) { //Sub Stream, i.e. nested List)
				IIStreamIn currList = (IIStreamIn) currItem;
				while(((currItem =  currList.nextItem()) != IIStreamIn.EOI) || currList.isValid()) { //Stream of SubList
					ret.add(fillTreeFromStreamIn((IIStreamIn) currItem)); } //
			} else {
				ret.setUserObject(currItem); 	//add a Node with this Item interpreted as a String
			}
		}
		return ret;	}
	
	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		testFillTree();
		testConstructTree();
	}

	/** Tests constructing the Tree from a Collection of Links.
	  * The Nodes are created first from a Collection of Objects.
	  *
	  * Obviously also both Nodes and Links can be created concurrently
	  * as done in fillTree.
	  * Actually for the Hyperbolic Tree
	  * the full Tree structure stays invariant,
	  * only the Subtree changes with Navigation. */
	public static void testConstructTree() {
	}

	/** Tests filling the Tree from a single streamIO	 */
	public static void testFillTree() {
		HashTreeNode TN1 = new HashTreeNode();
		HashTreeNode TN2 = new HashTreeNode();
		System.out.println("TN1.equals(TN2)? " + TN1.equals(TN2));
		System.out.println("HashCode:" + TN2.hashCode()); TN2.setUserObject("Hallo");
		System.out.println("TN1 = " + TN1);
		System.out.println("TN2 = " + TN2);
		System.out.println("TN1.equals(TN2)? " + TN1.equals(TN2));
		System.out.println("HashCode:" + TN1.hashCode()); TN1.setUserObject("Hallo");
		System.out.println("TN1 = " + TN1);
		System.out.println("TN2 = " + TN2);
		System.out.println("TN1.equals(TN2)? " + TN1.equals(TN2));
		System.out.println("HashCode:" + TN1.hashCode()); TN1.setUserObject("xxx");
		System.out.println("HashCode:" + TN1.hashCode());
		Object[] objArr = {"Root", ""};//no other way...
		Object[][] nestArr1 = {{"Leaf1"}, {"Leaf2"}, {"Leaf3", ""}, {"Leaf4"}, {"Leaf5"}};  //
		Object[][] nestArr2 = {{"Leaf31"}, {"Leaf32"}, {"Leaf33"}};  //
		objArr[1] = nestArr1;
		nestArr1[2][1] = nestArr2;
		Array arr = new Array(objArr); //testing nested Constructor
		DefaultMutableTreeNode top = fillTreeFromStreamIn(arr);
		System.out.println(top);

		Hashtable ht = new Hashtable(); //Constructor not defined for HashMap
		String[] strArr = {"Brandon", "Bailey"};
		ht.put("Amy", strArr);
		Hashtable ht2 = new Hashtable();
		String[] strArr2 = {"Trent", "Garrett", "Paige"};
		ht2.put("Jodi", strArr2);
		ht.put("Donn", ht2);
		ht.put("Peter", "Fran");
		//Create a tree that allows one selection at a time.
		JTree tree = new JTree(ht);//objArr); //top);
//		tree.
		tree.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT); // LEFT_TO_RIGHT);
		tree.getSelectionModel().setSelectionMode
				(TreeSelectionModel.SINGLE_TREE_SELECTION);

		//Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);

		//Add the split pane to the frame.
		JFrame frame = new JFrame();
		frame.getContentPane().add(treeView, BorderLayout.CENTER);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.pack();
		frame.setVisible(true);
		try {
			Thread.sleep(5000);
		}catch(InterruptedException x) {}
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
