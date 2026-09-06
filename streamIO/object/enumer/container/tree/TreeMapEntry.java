/*
 * Created on 07.05.2005
 *
 */
package streamIO.object.enumer.container.tree;

import graphs.ILinkAble;
import graphs.ILinked;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import streamIO.object.enumer.container.IndexAssociation;
import tester.Discrete;
import tester.IEquivalence;

/**
 * Universal, lightweight, final and flexible Implementation for Top Performance 
 * usable for HashMaps, Trees, linked Lists, Graphs etc. 
 * 
 * No Overhead like IOrderAble etc.
 * @see streamIO.copy.monoid.Association
 * @see streamIO.object.enumer.container.TypedAssociation, a heaviweight Twin
 * @see function.index.IndexEntry
 * 
 * similar Classes: 
 * @see streamIO.object.enumer.container.HashEntry which also extends IndexAssociation
 * <!-- docstate
 * tags: [code/red_black_tree, code/iterator_pattern]
 * concepts: [Red-Black Tree Backed Sorted Map Implementation]
 * facets: {layer: utility, status: legacy, complexity: high}
 * digest: 974c5453e2ccf1df6fc3640a7d47bff57cb7dcf27adfefedef19185a7652759c
 * stale: false
 * -->
 */
final public class TreeMapEntry 
extends IndexAssociation //HashMapEntry //would increase the Footprint, reuses next for Linked List Implementation. 
implements Serializable, Cloneable, ILinkAble
{
	
	/**
	 * 
	 */
	/** Serialization version UID. */
	private static final long serialVersionUID = 1L;

	/** Flag whether the Node is red or black 	 */
	public boolean isRed; // = false; //not necessary

	/** Reference to the next Entry	 */
	public TreeMapEntry next;
	//protected HashMapEntry next;
	
	/** Reference to the previous Entry	 */
	public TreeMapEntry prev; // = null; //not necessary
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Parent Entry for a doubly linked Structure.
	 * since a Rotation in a Tree requires to update the Parent too!	 */
	public TreeMapEntry prnt;
	
	/** Returns this Node's Parent in the Tree.
	 * @see graphs.ILinked#getPrnt()	 */
	public ILinked getPrnt() { return prnt; }

	/** Sets this Node's Parent, cast from the generic ILinked Type.
	 * @see graphs.ILinkAble#setPrnt(graphs.ILinked)	 */
	public void setPrnt(final ILinked parent) { setPrnt((TreeMapEntry) parent); }

	/** Sets this Node's Parent directly.
	 * @see graphs.ILinkAble#setPrnt(graphs.ILinked)	 */
	public void setPrnt(final TreeMapEntry parent) { this.prnt = parent; }
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors 
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Make a new cell with given key, value, and parent, and with 
	 * <tt>null</tt> child links, and false color. 
	 */
	TreeMapEntry(final Object key, final Object value, final int index, final double _weight, final TreeMapEntry parent) {
		super(key); 
		this.val = value;
		this.ndx = index; 
		this.prnt = parent;
		this.weight = _weight; 
	}
	
	/** reads all available Data from the ResultSet	 */
	TreeMapEntry(final ResultSet rs, final int[] cols, final IEquivalence hashFn) throws SQLException {
		super(        rs.getObject(cols[0])); 
		this.val    = rs.getObject(cols[1]) ; 
		this.typ    = (cols[3] >= 0 ? rs.getObject(cols[3]) : null); 
		this.weight = (cols[2] >= 0 ? rs.getFloat (cols[2]) : 1); 
		this.ndx    = (cols[4] >= 0 ? rs.getInt   (cols[4]) : 1); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods
	///////////////////////////////////////////////////////////////////////////
	
	/** consistently set the next Node 	*/
	public void setNext(final TreeMapEntry _next) {
		this.next = _next; if (next != null) 
		next.prnt =  this; 
	}
	
	/** consistently set the next Node 	*/
	public void setPrev(final TreeMapEntry _prev) {
		this.prev = _prev; if (prev != null)
		prev.prnt =  this; 
	}
	
	/** 
	 * Returns the very first GrandChild of this Element.
	 * For the Root it returns the first Entry in the TreeMap 
	 * (according to the TreeMap's key-sort function).  
	 * Does not reduces the Height of the Tree,
	 * because there is no Method available to do that.
	 * For fast Performance uses direct Member access like in
	 * @see Discrete.Forest.IEquivalence	 
	 * @return null if the TreeMap is empty.
	 */
	public TreeMapEntry first() {
		TreeMapEntry curr = this;
		while(curr.prev != null)
			curr = curr.prev;
		return curr; }
	
	/**
	 * Returns the successor of the specified Entry (inOrder Traversal), or null if none exists.
	 * @return the successor of the specified Entry
	 */
	public TreeMapEntry succ() {
		if (this.next != null) 
			return this.next.first(); 
		return nextParent();
	}
	
	/**
	 * Climbs up the Tree from this Node while it is a right (next) Child.
	 * @return the next (InOrder) Parent of this Node
	 */
	public TreeMapEntry nextParent() {
		TreeMapEntry parnt, child = this;
		while (((parnt = child.prnt) != null) && (child == parnt.next))  //until a left Child or root is encountered...
			child = parnt; //...go up the Tree 
		return parnt;
	}
	
	/**
	 * Returns the predecessor of the specified Entry (inOrder Traversal), or null if none exists.
	 * @return the predecessor of the specified Entry
	 */
	public TreeMapEntry pred() {
		if (this.prev != null) 
			return this.prev.last(); 
		return prevParent();
	}
	
	/**
	 * Climbs up the Tree from this Node while it is a left (previous) Child.
	 * @return the previous (InOrder) Parent of this Node
	 */
	public TreeMapEntry prevParent() {
		TreeMapEntry parnt, child = this;
		while (((parnt = child.prnt) != null) && (child == parnt.prev)) //until a left Child or root is encountered...
			child = parnt; //...go up the Tree 
		return parnt;
	}
	
	/** 
	 * Returns the very last GrandChild of this Element.
	 * For the Root it returns the last Entry in the TreeMap 
	 * (according to the TreeMap's key-sort function).  
	 * Does not reduces the Height of the Tree,
	 * because there is no Method available to do that.
	 * For fast Performance uses direct Member access like in
	 * @see Discrete.Forest.IEquivalence	 
	 * @return null if the TreeMap is empty.
	 */
	public TreeMapEntry last() {
		TreeMapEntry curr = this;
		while(curr.next != null)
			curr = curr.next;
		return curr; }
	
	/** Returns the Root (very first GrandParent) of this Element.
	  * Does not reduces the Height of the Tree,
	  * because there is no Method available to do that.
	  * For fast Performance uses direct Member access like in
	  * @see Discrete.Forest.IEquivalence	 */
	public TreeMapEntry root() {
		TreeMapEntry curr = this;
		while(curr.prnt != null)
			curr = curr.prnt;
		return curr; }
	
	///////////////////////////////////////////////////////////////////////////
	/// Interface: ILinked
	///////////////////////////////////////////////////////////////////////////

	/** Delegates to {@link #root()}.
	 * @see graphs.ILinked#getRoot()	 */
	public ILinked getRoot() { return root(); }

	///////////////////////////////////////////////////////////////////////////
	/// Tree Balancing Operations
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Balancing operations.
	 *
	 * Implementations of rebalancings during insertion and deletion are
	 * slightly different than the CLR version.  
	 * Rather than using Dummy nilnodes / Sentinels, we use a set of accessors that deal properly with null.  
	 * They are used to avoid messiness surrounding nullness checks in the main algorithms.
	 */
	
	/** rotates SubTree 'root' left 
	 * @param root the current Root Node
	 * @return the new Root Node
	 */
	public TreeMapEntry rotateLeft(TreeMapEntry root) {
		final TreeMapEntry r = this.next; setNext(r.prev);  
		r.prnt = this.prnt;
		if (this.prnt == null)
			root = r;
		else if (this.prnt.prev == this)
			 this.prnt.prev = r;
		else this.prnt.next = r;
		r.setPrev(this); 
		return root; 
	}

	/** rotates SubTree 'root' right 
	 * @param root the current Root Node
	 * @return the new Root Node
	 */
	private TreeMapEntry rotateRight(TreeMapEntry root) {
		final TreeMapEntry l = this.prev; setPrev(l.next);
		l.prnt = this.prnt;
		if (this.prnt == null)
			root = l;
		else if (this.prnt.next == this)
			 this.prnt.next = l;
		else this.prnt.prev = l;
		l.setNext(this); 
		return root; 
	}
	
	/**
	 * Delete node p, and then rebalance the tree.
	 * The Order Relation is retained without ever checking it, 
	 * by employing only Order-preserving Operations. 
	 */
	public TreeMapEntry deleteEntry(TreeMapEntry root) {
		// If internal Node, copy successor's element to p 
		// and then recourse Deletion.
		if (this.prev != null && this.next != null) { // p has 2 children
			final TreeMapEntry s = this.succ(); //can either be any right child or even a right Parent! 
			/*
            this.key = s.key; //not allowed, Key must be constant!
            this.val = s.val; 
            */
			//Since the Key is immutable, swap all other Properties
			//this also allows to swap without caring for the other Fields, because they don't need to be copied. 
			TreeMapEntry swap = s.prnt; 
			if (prnt != null)
				if (prnt.next == this) {
					prnt.setNext(s); 
				} else {
					prnt.setPrev(s); 
				}
			if (swap != null)
				if (swap.next == s) {
					swap.setNext(this); 
				} else {
					swap.setPrev(this); 
				}
			swap = prev; setPrev(s.prev); s.setPrev(swap); 
			swap = next; setNext(s.next); s.setNext(swap); 
		} // p has at most 1 child
		return deleteEntryDirectly(root);
	}
	
	/**
	 * Delete node p, and then rebalance the tree.
	 * The Order Relation is retained without ever checking it, 
	 * by employing only Order-preserving Operations. 
	 */
	protected TreeMapEntry deleteEntryDirectly(TreeMapEntry root) {

		// Start fixup at replacement node, if it exists.
		final TreeMapEntry replacement = (this.prev != null ? this.prev : this.next);

		if (replacement != null) {
			// Link replacement to parent
			replacement.prnt = this.prnt;
			if (this.prnt == null)
				root = replacement;
			else if (this == this.prnt.prev)
				this.prnt.prev  = replacement;
			else
				this.prnt.next = replacement;

			// Null out links so they are OK to use by fixAfterDeletion.
			this.prev = this.next = this.prnt = null;
			
			// Fix replacement
			if (this.isRed == false)
				root = replacement.balanceAfterDeletion(root);
		} else if (this.prnt == null) { // return if we are the only node.
			root = null;
		} else { //  No children. Use self as phantom replacement and unlink.
			if (this.isRed == false)
				root = this.balanceAfterDeletion(root);

			if (this.prnt != null) {
				if (this == this.prnt.prev)
					this.prnt.prev = null;
				else if (this == this.prnt.next)
					this.prnt.next = null;
				this.prnt = null;
			}
		}
		return root; 
	}
	
	/**By balancing the Tree AFTER the Insertion, 
	 * instead of splitting 4-Nodes on the Search Path down, 
	 * a constant Insertion Time can be guaranteed. 
	 * Moved here from TreeMap **/
	final public TreeMapEntry balanceAfterInsertion(TreeMapEntry root) {
		TreeMapEntry x = this; 
		x.isRed = true;
		
		while ((x != null) && (x != root) && (x.prnt.isRed == true)) {
			if (PARENT_OF(x) == PREV_OF(PARENT_OF(PARENT_OF(x)))) {
				final TreeMapEntry y = NEXT_OF(PARENT_OF(PARENT_OF(x)));
				if (IS_RED(y) == true) {
					SET_RED(PARENT_OF(x), false);
					SET_RED(y, false);
					SET_RED(PARENT_OF(PARENT_OF(x)), true);
					x = PARENT_OF(PARENT_OF(x));
				} else {
					if (x == NEXT_OF(PARENT_OF(x))) {
						x = PARENT_OF(x);
						root = x.rotateLeft(root);
					}
					SET_RED(PARENT_OF(x), false);
					SET_RED(PARENT_OF(PARENT_OF(x)), true);
					if (PARENT_OF(PARENT_OF(x)) != null) 
						root = x.prnt.prnt.rotateRight(root);
				}
			} else {
				final TreeMapEntry y = PREV_OF(PARENT_OF(PARENT_OF(x)));
				if (IS_RED(y) == true) {
					SET_RED(PARENT_OF(x), false);
					SET_RED(y, false);
					SET_RED(PARENT_OF(PARENT_OF(x)), true);
					x = PARENT_OF(PARENT_OF(x));
				} else {
					if (x == PREV_OF(PARENT_OF(x))) {
						x = PARENT_OF(x);
						root = x.rotateRight(root);
					}
					SET_RED(PARENT_OF(x),  false);
					SET_RED(PARENT_OF(PARENT_OF(x)), true);
					if (PARENT_OF(PARENT_OF(x)) != null) 
						root = x.prnt.prnt.rotateLeft(root);
				}
			}
		}
		root.isRed = false;
		return root; 
	}
	
	/**By balancing the Tree AFTER the Insertion, 
	 * instead of splitting 4-Nodes on the Search Path down, 
	 * a constant Insertion Time can be guaranteed. 
	 * Moved here from TreeMap **/
	final public TreeMapEntry balanceAfterDeletion(TreeMapEntry root) {
		TreeMapEntry x = this; 
		while (x != root && IS_RED(x) == false) {
			if (x == PREV_OF(PARENT_OF(x))) {
				TreeMapEntry sib = NEXT_OF(PARENT_OF(x));

				if (IS_RED(sib) == true) {
					SET_RED(sib, false);
					SET_RED(PARENT_OF(x), true);
					root = x.prnt.rotateLeft(root);
					sib = NEXT_OF(PARENT_OF(x));
				}

				if (IS_RED(PREV_OF(sib))  == false && 
					IS_RED(NEXT_OF(sib)) == false) {
					SET_RED(sib,  true);
					x = PARENT_OF(x);
				} else {
					if (IS_RED(NEXT_OF(sib)) == false) {
						SET_RED(PREV_OF(sib), false);
						SET_RED(sib, true);
						root = sib.rotateRight(root);
						sib = NEXT_OF(PARENT_OF(x));
					}
					SET_RED(sib, IS_RED(PARENT_OF(x)));
					SET_RED(PARENT_OF(x), false);
					SET_RED(NEXT_OF(sib), false);
					root = x.prnt.rotateLeft(root);
					x = root;
				}
			} else { // symmetric
				TreeMapEntry sib = PREV_OF(PARENT_OF(x));

				if (IS_RED(sib) == true) {
					SET_RED(sib, false);
					SET_RED(PARENT_OF(x), true);
					root = x.prnt.rotateRight(root);
					sib = PREV_OF(PARENT_OF(x));
				}

				if (IS_RED(NEXT_OF(sib)) == false && 
					IS_RED(PREV_OF(sib)) == false) {
					SET_RED(sib,  true);
					x = PARENT_OF(x);
				} else {
					if (IS_RED(PREV_OF(sib)) == false) {
						SET_RED(NEXT_OF(sib), false);
						SET_RED(sib, true);
						root = sib.rotateLeft(root);
						sib = PREV_OF(PARENT_OF(x));
					}
					SET_RED(sib, IS_RED(PARENT_OF(x)));
					SET_RED(PARENT_OF(x), false);
					SET_RED(PREV_OF(sib), false);
					root = x.prnt.rotateRight(root);
					x = root;
				}
			}
		}

		SET_RED(x, false);
		return root; 
	}

	///////////////////////////////////////////////////////////////////////////
	/// Full Scan Search
	///////////////////////////////////////////////////////////////////////////
	
	/** recursive full Scan Search for null 	*/
	TreeMapEntry valueSearchNull() {
		// Check this node for the value
		if (this.val == null)
			return this;
		// Check left and right subtrees for value
		TreeMapEntry ret = null; 
		if (this.prev != null) 
			ret = this.prev.valueSearchNull(); 
		if (ret != null)
			return ret; 
		if (this.next != null) 
			ret = this.next.valueSearchNull();
		return ret;
	}
	
	/** recursive full Scan Search for a Value. 
	 * Though the duplicate Implementation is inelegant, 
	 * the initial Distinction pays off in the long Run!	
	 */
	public TreeMapEntry valueSearch(final Object value) {
		if (value == null)
			return valueSearchNull(); 
		return valueSearchNonNull(value); 
	}
	
	/** recursive full Scan Search for a Value 	*/
	TreeMapEntry valueSearchNonNull(final Object value) {
		// Check this node for the value
		if (value.equals(this.val))
			return this;
		// Check left and right subtrees for value
		TreeMapEntry ret = null; 
		if (this.prev != null) 
			ret = this.prev.valueSearchNonNull(value); 
		if (ret != null)
			return ret; 
		if (this.next != null) 
			ret = this.next.valueSearchNonNull(value);
		return ret;
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// static null-tolerant Methods
	///////////////////////////////////////////////////////////////////////////
	
	/** Reads the red/black color Flag, tolerating a null Node.
	 * @return false for a null Node, otherwise its red/black color Flag */
	final static public boolean IS_RED(final TreeMapEntry p) {
		return (p == null ? false : p.isRed);
	}

	/** Sets the red/black color Flag, silently ignoring a null Node. */
	final static public void SET_RED(final TreeMapEntry p, final boolean c) {
		if (p != null)  p.isRed = c;
	}

	/** Reads the Parent reference, tolerating a null Node.
	 * @return null for a null Node, otherwise its Parent */
	final static public TreeMapEntry  PARENT_OF(final TreeMapEntry p) {
		return (p == null ? null: p.prnt);
	}

	/** Sets the Parent, silently ignoring a null Node. */
	final static public void SET_PARENT(final TreeMapEntry n, final TreeMapEntry parent) {
		if (n != null)  n.prnt = parent;
	}

	/** Reads the previous (left) Child, tolerating a null Node.
	 * @return null for a null Node, otherwise its previous (left) Child */
	final static public TreeMapEntry  PREV_OF(final TreeMapEntry p) {
		return (p == null)? null : p.prev;
	}

	/** Reads the next (right) Child, tolerating a null Node.
	 * @return null for a null Node, otherwise its next (right) Child */
	final static public TreeMapEntry  NEXT_OF(final TreeMapEntry p) {
		return (p == null)? null : p.next;
	}

	/** Finds the last (rightmost) Descendant, tolerating a null Node.
	 * @return null for a null Node, otherwise its last (rightmost) Descendant */
	final static public TreeMapEntry  LAST_OF(final TreeMapEntry p) {
		return (p == null)? null : p.last();
	}

	/** Finds the first (leftmost) Descendant, tolerating a null Node.
	 * @return null for a null Node, otherwise its first (leftmost) Descendant */
	final static public TreeMapEntry  FIRST_OF(final TreeMapEntry p) {
		return (p == null)? null : p.first();
	}

	/**
	 * Returns the successor of the specified Entry, or null if no such.
	 */
	final static public TreeMapEntry SUCCESSOR(final TreeMapEntry t) {
		return  (t == null) ? null : t.succ(); 
	}

}