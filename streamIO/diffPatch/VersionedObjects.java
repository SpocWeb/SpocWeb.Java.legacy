/*
 * Created on 04.02.2006
 *
 * This Object represents a versioned Object Stream or List. 
 */
package streamIO.diffPatch;

import math.vector.VectorInt;
import streamIO.Assert;
import streamIO.Log;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * This Object represents a single versioned Object Stream or List. 
 * For modelling multiple Object Streams or Lists 
 * (e.g. 'Folders' of versioned Files, i.e. Lists of Lines), 
 * use multiple Instances of this Class. 
 * 
 * It holds a copy of the current(last calculated) Version 
 * and a History of all Changes between all Versions. 
 * 
 * It allows to merge Changes from one Branch into other Branches. 
 * It allows to name Versions (also multiply) to be able to retrieve them by Name. 
 * 
 * So far this Object is used for single-threaded Development. 
 * I.e. it does not check whether the Client actually references the same Version 
 * as the Server, which is necessary to be able to merge his Changes. 
 * Additional Revision Counting is necessary for this. 
 * 
 * Key Ideas: 
 * Although it is sufficient to simply apply the Diffs to the Object[] List, 
 * to determine the actual Values at any Position in the Tree, 
 * this is not valid when merging ('moving') a changed List. 
 * Although not intuitive, 
 * it is necessary to determine the Diff to the underlying Version 
 * and then apply all intermediate Diffs to this Diff, 
 * instead of just applying them to the changed List. 
 * This is due to the Fact that only calculating the Diff 
 * (a quite expensive Operation) establishes a Relationship 
 * between the unchanged and the changed List.   
 * 
 * Design Decisions / Implementation Details:
 * Although it is possible to aggregate Differences and apply them 
 * to each other iteratively, it is easier (but slower) and has fewer Conflicts 
 * to determine the direct Difference and apply it only once by the other direct Difference. 
 * On the other Hand, a faster but less thorough Matching Algorithm 
 * could be supported in matching by smaller Differences. 
 * 
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
 * mtime: 2026-09-05T10:25:10Z
 * digest: 56479849697aad9b204d2944558e57c49bf117008e8d0e3dfdb1f42f7e61d2af
 * stale: false
 * tags: [code/version_tree, code/version_control]
 * concepts: [Versioning, Merging]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class VersionedObjects 
extends VersionTree {
	
	/** The Logger for this Class	 */
	private static final Log L = new Log(VersionedObjects.class, 0); 
	
	/** Flag whether to cache the calculated Values in the DiffSetObjects 
	 * This speeds up Navigation extremely, but can be costly, 
	 * if the whole Tree is navigated or the Data is large with only few Changes. 
	 */
	public boolean cacheValues; 
	
	/** Copy of the currently retrieved Version	 */
	protected Object[] currValues; //ArrayList
	
	/**returns the Values of the currently chosen Version
	 * Not usable in a Server Environment. 
	 * @return the Values of the currently chosen Version 
	 */
	public Object[] getValues() { return currValues; }
	
	/** Initializing Constructor
	 * @param initialValues the initial Values for the Root Version
	 */
	public VersionedObjects(final Object[] initialValues) {
		currValues = initialValues; 
	}
	
	/** Empty Constructor for wrapping and late Initialization. 	 */
	public VersionedObjects() {}
	
	///////////////////////////////////////////////////////////////////////////
	/// Implementation of abstract Methods
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * moves the current Position by 1 Position to the previous Parent or next Child 
	 * (as determined by the given DiffSet) Version 
	 * Not usable in a Server Environment. 
	 */
	protected DiffSet move(final DiffSet _goal, final DiffSet diff, DiffSet conflicts) {
		final boolean up =(_goal == currDiff.parent); 
		final DiffSetObject goal = (DiffSetObject) _goal; 
		if ((goal == null) || 
			(goal == currDiff))
			return conflicts; 
		final DiffSetObject modifyingDiff = up ? (DiffSetObject) currDiff : goal; 
		if (goal.values != null)
			currValues = goal.values; //no need to merge
		else {
			currValues = modifyingDiff.merge(currValues, up);
			if (cacheValues)
				goal.values = currValues; 
		}
		if (diff != null) {
			conflicts = diff.applyDiffs(modifyingDiff, up, conflicts); 
			++conflicts.ID[conflicts.ID.length-1]; //count down the Version, but only when moving down the direct Path.
			//when not moving straight, the ID has to be assigned new on Addition to the Parent! 
		}
		currDiff = goal; 
		return conflicts; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods
	///////////////////////////////////////////////////////////////////////////
	
	/**returns the Values of the currently chosen Version
	 * Not usable in a Server Environment. 
	 * @return the Values of the currently chosen Version 
	 */
	public Object[] getValues(final String version) { 
		return getValues((DiffSetObject) getVersion(version)); }
	
	/**returns the Values of the currently chosen Version
	 * Not usable in a Server Environment. 
	 * @return the Values of the currently chosen Version 
	 */
	protected Object[] getValues(final DiffSetObject version) { 
		if (version.values != null)
			return version.values; 
		moveToVersion(version); 
		return currValues; }
	
	/**
	 * determines the Changes from the given currVersion to the given diffVersion
	 * Both Versions can be anywhere in the Tree, 
	 * since there is always a unique Path between them. 
	 * Instead of accumulating all Differences along the way, 
	 * rather calculate a new Diff between the End Versions, 
	 * since this is typically smaller. 
	 * @return the DiffSetObject between the given Versions 
	 * together with the currVersion 
	 */
	protected DiffSet calcDiff(final DiffSet _diffVersion, final DiffSet _currVersion) { //return simple Differences directly. 
		return diff(
				(DiffSetObject) _diffVersion, 
				(DiffSetObject) _currVersion); }
	
	/**
	 * determines the Changes from the given currVersion to the given diffVersion
	 * Both Versions can be anywhere in the Tree, 
	 * since there is always a unique Path between them. 
	 * Instead of accumulating all Differences along the way, 
	 * rather calculate a new Diff between the End Versions, 
	 * since this is typically smaller. 
	 * @return the DiffSetObject between the given Versions 
	 * together with the currVersion 
	 */
	protected DiffSetObject diff(
			final DiffSetObject _diffVersion, 
			final DiffSetObject _currVersion) { //return simple Differences directly. 
		if (_diffVersion.parent == _currVersion)
			return _diffVersion; 
		if (_currVersion.parent == _diffVersion)
			return (DiffSetObject) _currVersion.inv(); 
		final Object[] diffValues = getValues(_diffVersion); //retrieve the second Version
		final Object[] currValues = getValues(_currVersion); //retrieve the first Version
		//create the Difference directly, since this is typically smaller! 
		//(instead of concatenating the individual Differences, which would result in longer Diffs!) 
		return DifferObject.DIFF(diffValues, currValues); 
	}
	
	/**
	 * merges the Changes between the given versions into the current Version
	 * and returns the Values and Conflicts.
	 * Instead of accumulating all Differences and getting a lot of Conflicts, 
	 * better calculate the Diffs directly and merge them. 
	 * @param  currVersion the Version to merge into 
	 * @param  lastVersion the  last Version of the Range to be merged
	 * @param startVersion the first Version of the Range to be merged
	 * @return a DiffSet containing the Conflicts and the Values of the merged Version. 
	 * The Alternative can be obtained by applying the returned DiffSet to the contained Values. 
	 */
	public DiffSet merge(final DiffSet currVersion, 
			final DiffSet startVersion, final DiffSet lastVersion) {
		return merge(
				(DiffSetObject) currVersion, 
				(DiffSetObject) startVersion, 
				(DiffSetObject) lastVersion); 
	}
	
	/**
	 * merges the Changes between the given versions into the current Version
	 * and returns the Values and Conflicts.
	 * Instead of accumulating all Differences and getting a lot of Conflicts, 
	 * better calculate the Diffs directly and merge them. 
	 * @param  currVersion the Version to merge into 
	 * @param  lastVersion the  last Version of the Range to be merged
	 * @param startVersion the first Version of the Range to be merged
	 * @return a DiffSet containing the Conflicts and the Values of the merged Version. 
	 * The Alternative can be obtained by applying the returned DiffSet to the contained Values. 
	 */
	public DiffSetObject merge(final DiffSetObject currVersion, 
			final DiffSetObject startVersion, final DiffSetObject lastVersion) {
		final DiffSetObject diffLast = diff(lastVersion, startVersion); 
		final DiffSetObject diffCurr = diff(currVersion, startVersion); 
		final DiffSetObject conflicts = (DiffSetObject) diffLast.applyDiffs(currVersion); 
		conflicts.values = diffLast.merge(getValues(currVersion)); 
		//retrieve the associated Diff 
		//determine the common ancestor
		//walk up the first Path to the common Ancestor and collect the Diffs. 
		//walk up the 2nd   Path to the common Ancestor and collect the Diffs. 
		//for each Diff on Path1...
		//merge with all Differences along Path2 
		//move to the current Version.
		//merge the Diff and return it. 
		return conflicts; 
	}
	
	/** updates the given Object to the given Version or Leaf Version if it's a Branch. 
	 * The Client has to track, which version the given Values are based upon.
	 * @param currVersion the versionID of the Version these Changes are based upon.  
	 * @return the Conflict Difference Set, accumulated during the Merge up to the Leaf, 
	 * decorated with the ID and the updated Values of the new Leaf in this Branch.  
	 * @throws VersionException when you add a Child in the middle of a Branch
	 */
	public DiffSetObject update(final Object[] changedValues, final String currVersionID) 
	throws VersionException {
		moveToVersion(currVersionID); 
		//cannot just apply all Diffs to the given Values. 
		//instead: identify the Differences 
		final DiffSetObject diff = DifferObject.DIFF(changedValues, currValues, currDiff, "temp"); //create a Temp Branch
		--currDiff.numBranches; //don't really add the Diff Node.  
		//move Diff to the End of the Branch. 
		final DiffSetObject conflicts = new DiffSetObject(VectorInt.COPY(currDiff.ID)); //based on currDiff now...
		//this works only if it's a Branch!!! 
		while(null != mergeDown(diff, conflicts)); //merge all the way down the Branch 
		conflicts.values = diff.merge(currValues); 
		return conflicts; 
	}
	
	/** adds the given Values as a new Version to the current Branch 
	 * does NOT move to the new Version! 
	 * @param values the Values to add after the given Version  
	 * @param version the Version the new Values are based upon. 
	 * @param branch the Name of the new Branch to create. 
	 * @return the new Diff Object appended to the last Version of this Branch. 
	 * @throws VersionException when there is a direct Child and no new branch Name was given. 
	 */
	public DiffSetObject addVersion(final Object[] values, final String version) throws VersionException {
		return addVersion(values, version, null); }
	
	/** adds the given Values as a new Version to the current Branch 
	 * does NOT move to the new Version! 
	 * @param _values the Values to add after the given Version  
	 * @param _version the Version the new Values are based upon. 
	 * @param _branch the Name of the new Branch to create. 
	 * @return the new Diff Object appended to the last Version of this Branch. 
	 * @throws VersionException when there is a direct Child and no new branch Name was given. 
	 */
	public DiffSetObject addVersion(final Object[] _values, final String _version
			, final String _branch) throws VersionException {
		final DiffSet version = getVersion(_version);
		if ((version.directChild != null) && (
			(_branch == null) || 
			(_branch == currDiff.getBranch()) ||   
			(_branch.equals(currDiff.getBranch())))) //no new Branch...
			throw new VersionException("Cannot add a Leaf without a Branch. \n" +
					"Either give a Branch Name or update to the Leaf of the current Branch!"); 
		moveToVersion(version); 
		final DiffSetObject diff = DifferObject.DIFF(_values, currValues, currDiff, _branch);
		addVersion(diff); 
		currValues = diff.values = _values;  
		return diff; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// static Testing and main Methods
	///////////////////////////////////////////////////////////////////////////
	
	/** Runs the self-Test of this Class.	 */
	public static void main(final String[] args) throws Exception {
		testIt();
	}

	/** Self-Test exercising Branching, Merging and Conflict Detection on a small Object[] Tree.	 */
	public static VersionedObjects testIt() throws VersionException {
		final Object[] x  = {"A", "B", "C", "D", "E"}; 
		final Object[] x1 = {"A", "B", "X", "D", "E"}; 
		final Object[] x2 = {"A", "B", "C", "Y", "E"}; //Mergeable with x1 
		final Object[] x3 = {"A", "B", "C", "Z", "E"}; //Conflict with x2
		final String branch1 = "Branch1"; 
		final String branch2 = "Branch2"; 
		final VersionedObjects tree = new VersionedObjects(x); 
		Assert.EQUALS(x, tree.getValues());
		final String root = tree.getVersionID(); 
		DiffSetObject diff;
		//add 1 Version and 2 Branches, retrieve them again. 
		diff = tree.addVersion(x1, root); 			Assert.EQUALS(x1, tree.getValues()); Assert.EQUALS(2, diff.getInt()); L.n(diff); 
		tree.moveUp(); 								Assert.EQUALS(x , tree.getValues());
		diff = tree.addVersion(x2, root, branch1); 	Assert.EQUALS(x2, tree.getValues()); Assert.EQUALS(2, diff.getInt()); 
		tree.moveUp(); 								Assert.EQUALS(x , tree.getValues());
		try {  tree.addVersion(x3, root, branch1); 	Assert.FAIL("Starting a second branch with the same Name should cause an Exception!");
		} catch (final VersionException e) {}
		tree.moveToVersion(DiffSet.DEFAULT_BRANCH); Assert.EQUALS(x1, tree.getValues());
		diff = tree.addVersion(x3, "0", branch2);   Assert.EQUALS(x3, tree.getValues()); Assert.EQUALS(2, diff.getInt()); 
		tree.moveToVersion(branch1); 				Assert.EQUALS(x2, tree.getValues());
		tree.moveToVersion(branch2); 				Assert.EQUALS(x3, tree.getValues());
		tree.moveToVersion(DiffSet.DEFAULT_BRANCH); Assert.EQUALS(x1, tree.getValues());
		tree.merge(branch1); 
		//try to add a Version based on a deprecated Version... 
		try{  diff = tree.addVersion(x2, root);  	Assert.FAIL("Trying to add within a History without Branching should be detected!");
		} catch (final VersionException e) {}
		//add 1 Version based on a previous Version without Conflicts. 
		DiffSetObject conflicts; //first update the Changes to the current Leaf Version, and THEN add it. 
		conflicts = tree.update(x2, root); diff = tree.addVersion(conflicts.values, conflicts.getID()); 
		Assert.EQUALS(new Object[] {"A","B","X","Y","E"}, conflicts.values); 
		//add 1 Version based on a previous Version with Conflicts. 
		conflicts = tree.update(x3, root); diff = tree.addVersion(conflicts.values, conflicts.getID());
		tree.tag("HEAD", "currHead"); 
		Assert.EQUALS(new Object[] {"A","B","X","Z","E"}, conflicts.values); 
		diff = (DiffSetObject) tree.diff("0", "HEAD"); 
		L.n(tree); 
		//final VersionedObjects tree2 = new VersionedObjects(); 
		//tree.writeTo(tree2); 
		return tree; 
	}
	
}
