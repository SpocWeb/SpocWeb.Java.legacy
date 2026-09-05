/*
 * Created on 01.02.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.diffPatch;

import streamIO.IInstantiAble;
import streamIO.StringBufferOutputStream;
import streamIO.integer.AStreamAble;
import streamIO.integer.IStreamIn_Struct;
import streamIO.integer.IStreamOutStruct;
import streamIO.integer.IStreamReadAble;
import streamIO.integer.StreamOutStruct;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * This Class collects all Differences between two consecutive Versions 
 * and can merge Versions up and down. 
 * DiffSets form a Hierarchy with the initial (or empty) Version at the Top 
 * and the Leaf Versions at the Leafs. 
 * Typically the Tree needs to be upward navigable only 
 * and thus only the Leaf Versions need to be stored. 
 * To save Space and Redundancy, the Leaf Versions need not be stored directly, 
 * since they can be restored from the initial (or empty) Version. 
 * 
 * DiffSets can be concatenated, forming larger, but most complex DiffSets, 
 * an Operation that forms a non-commutable Group. 
 * 
 * DiffSets can also be applied to each other, again resulting in DiffSets,  
 * an Operation that corresponds to merging a certain Difference into a Branch. 
 * When no Conflicts occur (Addition or Deletion of the same Row(s)), 
 * this Operation forms a non-commutable Group. 
 * 
 * 
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:23:06Z
 * digest: 74ee0c002aded97f6288925088661769ee09952d12af6a18d3321735da03470b
 * stale: false
 * tags: [code/diff_collection, code/diff_application, code/merge_algorithm]
 * concepts: [Diffing, Merging]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
abstract public class DiffSet 
extends AStreamAble 
implements IInstantiAble, IStreamReadAble {
	
	/** The Default Branch Name if none is given 	*/
	final static public String DEFAULT_BRANCH = "HEAD"; 
	
	///////////////////////////////////////////////////////////////////////////
	/// Member Variables & Accessors
	///////////////////////////////////////////////////////////////////////////
	
	/** The Number of Deletions in the Difference Set
	 * this is redundant and could be derived 
	 * by counting the Number of negative Positions in the Array.  
	 */
	protected DiffBase[] diffVals; 
	
	/** The Number of valid Differences in this Set, typically = diffVals.length	 */
	protected int itemCount; 
	
	/**returns the Number of valid Differences in this Set
	 * @return the Number of valid Differences in this Set 
	 */
	public int getInt() { return itemCount; }
	
	/** The Number of Deletions in the Difference Set
	 * this is redundant and could be derived 
	 * by counting the Number of negative Positions in the Array.  
	 */
	protected int numDeletions; 
	
	/** optional List of Arrays describing the Positions of the same Elements.	 */
	final public int[][] same; 
	
	/** optional Reference to the parent Difference Set to be able to navigate upward the Tree 
	 * upward navigable Trees are easier to manage. 	 */
	final public DiffSet parent; 
	
	/** The Name of the (latest) Branch this Version is in. 
	 * This Name is always well-defined! and it is necessary for the update() Command!	*/
	private String branch; //	final public

	/** Returns the Name of the (latest) Branch this Version is in.
	 * @return the Name of the (latest) Branch this Version is in.	 */
	public String getBranch() { return branch; }
	
	/** The Number of Branches from this Version.	 */
	protected int numBranches; 
	
	/** Optional List of Child Branches from this Node, necessary for downward Navigation.	 */
	//protected final DiffSet[] branches; 
	
	/** 
	 * optional Reference to the direct Child Difference Set 
	 * to be able to navigate down along the "current Branch", 
	 * a Concept that only exists as named Branch. 
	 * There must be only one direct Child, all others are Branches, 
	 * so this also serves as a Flag.  	 
	 * In Pricinple the direct Child could also be identified and retrieved by its ID, 
	 * but that is slower and more fragile. 
	 * @see VersionTree#mergeDown(DiffSet, DiffSet) uses this Property solely. 
	 */
	protected DiffSet directChild; 
	
	/** The unique ID of this Version.
	 * Used for faster Retrieval of the common Ancestor and for generating a unique ID.	 */
	protected int[] ID; 
	
	/** return the ID of this DiffSet as a String
	 * @return the ID of this DiffSet as a String
	 */
	public String getID() { 
		final StreamOutStruct stream = new StreamOutStruct(new StringBufferOutputStream()); 
		stream.addInts(ID); 
		return stream.toString(); } 
	
	/** The Description / Comment for this Difference	 */
	public Object description = ""; 
	
	////////////////////////////////////////////////////////////////////////////
	/// De-/Serialization 
	////////////////////////////////////////////////////////////////////////////
	
	/** Field Name used to (de)serialize the {@link #ID}.	 */
	final public static String STR_ID = "ID";

	/** Field Name used to (de)serialize the {@link #getBranch() Branch Name}.	 */
	final public static String STR_BRANCH = "branch";

	/** Field Name used to (de)serialize the {@link #description}.	 */
	public static final String STR_DESCRIPTION = "description";

	/** Field Name used to (de)serialize each individual {@link DiffBase} in {@link #diffVals}.	 */
	public static final String STR_DIFF = "diff";

	/** Reads the ID, Branch, Description and individual Diff Fields by Name from the Stream, appending each Diff via {@link #addDiff(DiffBase)}.
	 * @see streamIO.integer.AStreamAble#readField(java.lang.String, streamIO.integer.IStreamIn_Struct)	 */
	public Object readField(final String name, final IStreamIn_Struct stream) {
		if (STR_ID.equals(name)) 
			return ID = stream.nextInts(); 
		if (STR_BRANCH.equals(name)) 
			return branch = stream.nextString(); 
		if (STR_DESCRIPTION.equals(name)) 
			return description = stream.nextString(); 
		if (STR_DIFF.equals(name)) {
			final Object obj = stream.nextItem(); 
			if (obj instanceof DiffBase)
				addDiff((DiffBase) obj); 
			return obj;  
		} else
			return super.readField(name, stream);
	}
	
	/** Default Separators for the String Representation in one Line	 */
	//final static public String DEFAULT_SEPS = ".//[,]\n"; 
	
	/** appends all characteristic Members of this Object to the Stream.
	 * @see streamIO.integer.IStreamWriteAble#writeTo(streamIO.integer.IStreamOutStruct)
	 * @param stream the PrintStream to write to.  
	 * @return a new PrintStreamOut writing into a StringBuffer. 
	 */
	public void writeTo(final IStreamOutStruct stream) {
		//super.writeTo(stream); 
		stream.writeNameValuePair(STR_BRANCH, branch); 
		stream.writeNameValuePair(STR_DESCRIPTION, description.toString()); 
		stream.open_Struct(STR_ID).addInts(ID).closeStruct(); 
		for(int i = -1; ++i < itemCount;) {
			diffVals[i].writeTo(stream, STR_DIFF); 
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/** empty Constructor for creating Conflict Containers		 */
	protected DiffSet() { this(new int[1]); }
	
	/** empty Constructor for new empty Instances	 */
	public DiffSet(final int[] _ID) { 
		super(null); 
		this.ID = _ID; 
		this.numDeletions = 0;  
		this.itemCount = 0;  
		this.diffVals = new DiffBase[9]; 
		this.parent = null; 
		this.same = new int[2][];
		this.branch = DEFAULT_BRANCH;
	}
	
	/** Creates a new DiffSet from the given Differences, optionally attaching it below a Parent Version (as its direct Child or as a new Branch).
	 * @param _diffVals the Differences making up this Version
	 * @param _same optional Arrays of Indices of the unchanged Elements
	 * @param _numDeletions the Number of Deletions among _diffVals
	 * @param _parent the optional Parent Version, or null for the Root
	 * @param _branch the optional Name of a new Branch to start at this Version
	 * @throws VersionException when you add a Child in the middle of a Branch
	 */
	public DiffSet(
			final DiffBase[] _diffVals,
			final int[][] _same,
			final int _numDeletions, 
			final DiffSet _parent, 
			final String _branch) throws VersionException {
		super(null); 
		this.numDeletions = _numDeletions;  
		this.itemCount = _diffVals.length;  
		this.diffVals = _diffVals; 
		this.parent = _parent; 
		this.same = _same; 
		if (parent == null) {
			this.ID = new int[1]; 
			this.branch = DEFAULT_BRANCH;
		} else {
			if (( _branch != null) && 
				( _branch != parent.branch) && 
				(!_branch.equals(parent.branch))) {
				this.branch = _branch; 
				this.ID = new int[parent.ID.length+2]; ID[ID.length-2] = ++parent.numBranches; 
			} else { 
				this.branch = parent.branch; 
				this.ID = new int[parent.ID.length]; 
				if (parent.directChild != null) 
					throw new VersionException("The Parent Version " + parent.getID() + " already has a direct Child! You would have to branch!"); 
				parent.directChild = this; 
			}
			System.arraycopy(parent.ID, 0, ID, 0, parent.ID.length); 
			++ID[ID.length-1]; 
		}
	}
	
	/** abstract Method to create new instances of the same Type	 */
	public IInstantiAble NewInstance() { return newInstance(); } 
	
	/** abstract Method to create new instances of the same Type	 */
	public abstract DiffSet newInstance(); 
	
	/** abstract Method to create new instances of the same Type	 */
	protected abstract DiffSet newInstance( 
			final DiffBase[] _diffVals, 
			final int[][] _same,
			final int _numDeletions, 
			final DiffSet _parent, 
			final String branch) throws VersionException; 
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods
	///////////////////////////////////////////////////////////////////////////
	
	/** Appends a single Difference to this Set, growing the backing Array as needed and updating the Deletion Count.
	 * @param item the Difference to add
	 * @return the number of Items in this DiffSet.
	 */
	public int addDiff(final DiffBase item) {
		if (itemCount >= diffVals.length) {
			final DiffBase[] tmp = new DiffBase[itemCount+itemCount]; 
			System.arraycopy(diffVals, 0, tmp, 0, diffVals.length); 
			diffVals = tmp; 
		}
		diffVals[itemCount] = item; 
		if (item.position < 0)
			++numDeletions; 
		return ++itemCount; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Group Methods
	///////////////////////////////////////////////////////////////////////////
	
	/**returns the inverse Difference Set for moving up the Version Hierarchy.
	 * @return the inverse Difference Set for moving up the Version Hierarchy. 
	 */
	public DiffSet inv() { 
		try {
			return inv(null); 
		} catch (VersionException x) {
			throw new RuntimeException("Should never happen!", x); 
		}
	}
	
	/** Returns the inverse Difference Set for moving up the Version Hierarchy, with each contained Difference inverted.
	 * @param child Reference to the Child DiffSet of this one,
	 * to be able to establish a reverse Parent Relation for the Inverse.
	 * @return the inverse Difference Set for moving up the Version Hierarchy.
	 */
	public DiffSet inv(final DiffSet child) throws VersionException {
		final DiffBase[] inv = new DiffBase[diffVals.length]; 
		for (int i = diffVals.length; --i >= 0;) 
			inv[i] = diffVals[i].inv(); 
		final int[][] swap = {same[1], same[0]}; 
		return newInstance(inv, swap, diffVals.length-numDeletions, child, null); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Merging Methods
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * applies diff2 to the first diff given. 
	 * @param diffSet List of Positions for this Diff to be modified/moved.
	 */
	final public DiffSet applyDiffs(final DiffSet diffSet) {
		return applyDiffs(diffSet, null); }
	
	/**
	 * applies diff2 to the first diff given. 
	 * @param diffSet List of Positions for this Diff to be modified/moved.
	 */
	final public DiffSet applyDiffs(final DiffSet diffSet, final boolean inv) {
		return applyDiffs(diffSet, inv, null); }
	
	/**
	 * applies diff2 to the first diff given. 
	 * @param diffSet List of Positions for this Diff to be modified/moved.
	 * @param conflicts dynamically filled DiffSet Container for Conflict Positions and Values 
	 */
	final public DiffSet applyDiffs(final DiffSet diffSet, final DiffSet conflicts) {
		return applyDiffs(diffSet, false, conflicts); }
	
	/**
	 * applies diff2 to the first diff given. 
	 * @param diffSet List of Positions for this Diff to be modified/moved.
	 * @param inv Flag whether to apply the inverse DiffSet when moving up the Tree. 
	 * @param conflicts dynamically filled DiffSet Container for Conflict Positions and Values 
	 */
	final public DiffSet applyDiffs(final DiffSet diffSet, final boolean inv, DiffSet conflicts) {
		if (diffSet == null)
			return conflicts; 
		final int conflictCount; //only update the already existing Conflicts. 
		if (conflicts == null) {
			conflicts = newInstance(); 
			conflictCount = 0; 
		} else
			conflictCount = conflicts.getInt(); 
		for(int i = diffSet.diffVals.length; --i >= 0;) {
			final DiffBase diff = diffSet.diffVals[i];
			int delta; 
			final int pos; 
			if(diff.position < 0) {
				delta = -1;
				pos = ~diff.position; 
			} else {
				delta = +1; 
				pos = diff.position; 
			}
			if (inv)
				delta = -delta; 
			this     .applyDiff(diff,     itemCount, conflicts, pos, delta);
			conflicts.applyDiff(diff, conflictCount, null     , pos, delta);
		} //also move the Conflict Items, but only the old Ones. Use null for fail-fast Op.
		return conflicts; 
	}
	/** Moves all Diffs in this Object by delta when above pos. 
	 * 
	 * @param diff
	 * @param diffLen
	 * @param conflicts
	 * @param pos
	 * @param delta
	 */
	protected void applyDiff(final DiffBase diff, final int diffLen, 
			final DiffSet conflicts, final int pos, final int delta) {
		boolean added = false; 
		for(int j = diffLen; --j >= 0;) { //when fully ordered, 
			int dpi = diffVals[j].position; //you can stop after reaching smaller Indices. 
			int dta = delta; 
			if (dpi < 0) {
				dpi = ~dpi; 
				dta = -dta; 
			}
			if (dpi > pos)
				diffVals[j].position+=dta; 
			else if (dpi == pos) 
				if (!diff.equals(diffVals[j])) //only conflict, when the Value differs.  
					if(!added) {
						added = true; 
						conflicts.addDiff(diff);
					}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// static Testing and Main Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** tests all Methods of this Class	 */
	public static void main(final String[] args) throws Exception {
	}
	
}
