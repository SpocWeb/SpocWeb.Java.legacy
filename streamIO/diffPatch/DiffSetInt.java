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
 * Concrete Implementation of a DiffSet for generic int Lists or Streams. 
 * These DiffSetInts can be cascaded to form Diff Trees. 
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
 * @see streamIO.diffPatch.DifferInt creates DiffSetInts  
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:23:17Z
 * digest: 9d763b7c778d311ae89262bceede83659e69dcde1dedf13632de02ab33bb90d8
 * stale: false
 * tags: [code/diff_collection, code/merge_algorithm]
 * concepts: [Diffing, Merging]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public class DiffSetInt 
extends DiffSet {
	
	/** empty Constructor for new empty Instances	 */
	public DiffSetInt() { values = null; }
	
	/** Constructor to transport the ID */
	public DiffSetInt(final int[] _ID) { super(_ID); }
	
	/** added to be able to hand back the Result, 
	 * the Version and the Conflicts in one Structure 
	 * e.g. on Updates. 
	 */
	public int[] values; 
	
	/** Creates a new int-Diff Version without a cached Result Array; see {@link DiffSet#DiffSet(DiffBase[], int[][], int, DiffSet, String)}.
	 * @param _diffVals
	 * @param _same
	 * @param _numDeletions
	 * @param _parent
	 * @throws VersionException when you add a Child in the middle of a Branch
	 */
	public DiffSetInt(final DiffInt[] _diffVals, final int[][] _same,
			final int _numDeletions, final DiffSet _parent, final String branch)
		throws VersionException {
		this(_diffVals, _same, _numDeletions, _parent, branch, null);
	}

	/** Creates a new int-Diff Version, optionally caching the resulting int[] Values of this Version.
	 * @param _diffVals
	 * @param _same
	 * @param _numDeletions
	 * @param _parent
	 * @throws VersionException when you add a Child in the middle of a Branch
	 */
	public DiffSetInt(final DiffInt[] _diffVals, final int[][] _same,
			final int _numDeletions, final DiffSet _parent, final String branch, 
			final int[] _values)	throws VersionException {
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
	final public int[] merge(final int[] original) { 
		return merge(original, false); }
	
	/**
	 * merges the Changes described by the deleted and added Positions (and Values) 
	 * into the original Array.  
	 * 
	 * @param original the original Array
	 * @param inv Flag whether to perform the inverse Merge (Undo, unMerge) 
	 * @return the modified Array. 
	 */
	final public int[] merge(final int[] original, final boolean inv) {
		if (itemCount == 0)
			return original; 
		final int numDel = inv ? itemCount-numDeletions : numDeletions; 
		final int[] ret 
		= new int[(original.length-numDel)+(itemCount-numDel)]; 
		for (int i = 0, o = 0, d = 0, dpc, dp = diffVals[0].position; i < ret.length; ) {
			if (inv) dp = ~dp; dpc = ~dp; 
			if      (i == dp) //Addition
				ret[i++] = ((DiffInt) diffVals[d]).value; 
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
	public DiffSet newInstance() { return new DiffSetInt(); }

	/** abstract Method to create new instances of the same Type
	 * @see streamIO.diffPatch.DiffSet#newInstance(streamIO.diffPatch.DiffBase[], int[][], int, streamIO.diffPatch.DiffSet)
	 */
	protected DiffSet newInstance(final DiffBase[] _diffVals, final int[][] _same, final int _numDeletions, 
			final DiffSet _parent, final String branch) throws VersionException {
		return new DiffSetInt((DiffInt[]) _diffVals, _same, _numDeletions, _parent, branch); }
	
	/** Delegates entirely to the Superclass; this Subclass adds no own serialized Fields.
	 * @see streamIO.integer.AStreamAble#readField(java.lang.String, streamIO.integer.IStreamIn_Struct)	 */
	public Object readField(final String name, final IStreamIn_Struct stream) {
		return super.readField(name, stream);
	}
	
}
