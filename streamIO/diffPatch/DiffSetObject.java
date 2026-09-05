/*
 * Created on 01.02.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.diffPatch;

import streamIO.integer.IStreamIn_Struct;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Concrete Implementation of a DiffSet for generic Object Lists or Streams. 
 * These DiffSetObjects can be cascaded to form Diff Trees. 
 * At the Root of such a Tree is either the empty List 
 * or the first Version (since the Difference to the empty List is trivial and large). 
 * At the Leafs of such a Tree are the latest Versions of the File. 
 * All Leafs, intermediate Versions and the root Version can be calculated 
 * from any other Version by travelling up the Tree 
 * to the last common Difference Element 
 * and travelling down again to the desired Version. 
 * Thus it is a good Strategy to use the latest main Trunc Version 
 * as the Reference and derive all other Versions from it. 
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 * @see streamIO.diffPatch.DifferObject creates DiffSetObjects  
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:23:26Z
 * digest: da5a858a8d9d9c644a449912adb53c3719f9d8522ffd5cb17cfc3830d48289f9
 * stale: false
 * tags: [code/diff_collection, code/merge_algorithm]
 * concepts: [Diffing, Merging]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public class DiffSetObject 
extends DiffSet {
	
	/** empty Constructor for new empty Instances	 */
	public DiffSetObject() { values = null; }
	
	/** Constructor to transport the ID */
	public DiffSetObject(final int[] _ID) { super(_ID); }
	
	/** Optionally filled Value of this Revision. 
	 * Calculated only on Demand. 
	 * Added to this Object to be able to hand back the Result, 
	 * the Version and the Conflicts in one Structure 
	 * e.g. on Updates. 
	 */
	public Object[] values; 
	
	/** Delegates entirely to the Superclass; the optional cached {@link #values} are not (yet) (de)serialized.
	 * @see streamIO.integer.AStreamAble#readField(java.lang.String, streamIO.integer.IStreamIn_Struct)	 */
	public Object readField(final String name, final IStreamIn_Struct stream) {
		//if (STR_VALUES.equals(name))
		//	values = stream.nextInts();
		return super.readField(name, stream);
	}

	/** Creates a new Object-Diff Version without a cached Result Array; see {@link DiffSet#DiffSet(DiffBase[], int[][], int, DiffSet, String)}.
	 * @param _diffVals
	 * @param _same
	 * @param _numDeletions
	 * @param _parent
	 * @throws VersionException when you add a Child in the middle of a Branch
	 */
	public DiffSetObject(final DiffObject[] _diffVals, final int[][] _same,
			final int _numDeletions, final DiffSet _parent, final String branch)
		throws VersionException {
		this(_diffVals, _same, _numDeletions, _parent, branch, null);
	}

	/** Creates a new Object-Diff Version, optionally caching the resulting Object[] Values of this Version.
	 * @param _diffVals
	 * @param _same
	 * @param _numDeletions
	 * @param _parent
	 * @throws VersionException when you add a Child in the middle of a Branch
	 */
	public DiffSetObject(final DiffObject[] _diffVals, final int[][] _same,
			final int _numDeletions, final DiffSet _parent, final String branch, 
			final Object[] _values)	throws VersionException {
		super(_diffVals, _same, _numDeletions, _parent, branch);
		values = _values;
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Merging Methods
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * merges the Changes described by the deleted and added Positions (and Values) 
	 * into the original Array.  
	 * 
	 * @param original the original Array
	 * @return the modified Array. 
	 */
	final public Object[] merge(final Object[] original) { 
		return merge(original, false); }
	
	/**
	 * merges the Changes described by the deleted and added Positions (and Values) 
	 * into the original Array.  
	 * 
	 * @param original the original Array
	 * @param inv Flag whether to perform the inverse Merge (Undo, unMerge) 
	 * @return the modified Array. 
	 */
	final public Object[] merge(final Object[] original, final boolean inv) {
		if (itemCount == 0)
			return original; 
		final int numDel = inv ? itemCount-numDeletions : numDeletions; 
		final Object[] ret 
		= new Object[(original.length-numDel)+(itemCount-numDel)]; 
		for (int i = 0, o = 0, d = 0, dpc, dp = diffVals[0].position; i < ret.length; ) {
			if (inv) dp = ~dp; dpc = ~dp; 
			if      (i == dp) //Addition
				ret[i++] = ((DiffObject) diffVals[d]).value; 
			else if (o == dpc) //Deletion 
				++o; 
			else { //same
				ret[i++] = original[o++];
				if (inv) dp = ~dp; //restore, since flipped twice otherwise...
				continue; 
			} 
			if (++d < itemCount) {
				dp = diffVals[d].position; 
			} else 
				dp = dpc = Integer.MAX_VALUE; 
		}
		return ret; 
	}
	
	/** abstract Method to create new instances of the same Type
	 * @see streamIO.diffPatch.DiffSet#newInstance()
	 */
	public DiffSet newInstance() { return new DiffSetObject(); }

	/** abstract Method to create new instances of the same Type
	 * @see streamIO.diffPatch.DiffSet#newInstance(streamIO.diffPatch.DiffBase[], int[][], int, streamIO.diffPatch.DiffSet)
	 */
	protected DiffSet newInstance(final DiffBase[] _diffVals, final int[][] _same, final int _numDeletions, 
			final DiffSet _parent, final String branch) throws VersionException {
		return new DiffSetObject((DiffObject[]) _diffVals, _same, _numDeletions, _parent, branch); }
	
}
