/*
 * Created on 04.02.2006
 *
 * The Structure to manage a Tree of tagged Versions. 
 */
package streamIO.diffPatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import streamIO.integer.AStreamAble;
import streamIO.integer.IStreamIn_Struct;
import streamIO.integer.IStreamOutStruct;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Model Structure to manage a Tree of tagged Versions. 
 * The unique Thing about a Tree Structure is 
 * that there is always only a single Path between any two Nodes. 
 * Additionally the Notion of a Branch was introduced 
 * to allow for downward Navigation, although this is not necessary, 
 * since the HEAD Version of each Branch is tagged anyway. 
 * 
 * Purpose / Responsibilities of this Class:
 * Owns the Map of named Versions (Tags, Branch Heads and IDs) and the generic Tree-Navigation
 * Operations (move up/down, move to a named Version, find the common Ancestor, merge).
 * Subclasses supply the concrete Value Type by implementing {@link #move(DiffSet, DiffSet, DiffSet)},
 * {@link #calcDiff(DiffSet, DiffSet)} and {@link #merge(DiffSet, DiffSet, DiffSet)}.
 *
 * Design Decisions / Implementation Details:
 * {@link VersionedObjects} is currently the only Subclass, specializing this Tree to Object[] Streams;
 * an analogous int[]-based Subclass would follow the same Pattern as {@link DiffSetInt} does for {@link DiffSetObject}.
 *
 * Known SubClasses: {@link VersionedObjects}
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
 * mtime: 2026-09-05T10:25:05Z
 * digest: 4c9b7a5181a752753eaa4721513128d2ffe85cfebc6929e88102534de886d133
 * stale: false
 * tags: [code/version_tree, code/version_control]
 * concepts: [Versioning, Branching]
 * facets: {layer: domain, status: broken, complexity: high}
 * -->
 */
abstract public class VersionTree 
extends AStreamAble {
	
	/** Reference to the Name currently retrieved Version	 */
	protected DiffSet currDiff;
	
	/**returns the Name of the currently chosen Branch.
	 * @return the currently chosen Version 
	 */
	public String getVersionID() { return currDiff.getID(); }

	/** List of named Versions. 
	 * HEAD and Branch Names are always stored. 
	 * Tag Names can be used for branching at any time.  
	 * Since we need it anyway, we can as well store all Revisions in it
	 * and thus make Navigation easier, instead of . 
	 */
	protected final HashMap	namedVersions	= new HashMap();
	
	/** empty Constructor 	 */
	public VersionTree() {
		super(null); 
		addVersionSafe(new DiffSetObject()); // 
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// De-/Serialization 
	////////////////////////////////////////////////////////////////////////////
	
	/** Field/Struct Name used to (de)serialize a single named (ID- or Branch-tagged) Version.	 */
	public static final String STR_NAMED_VERSION = "namedVersion";
	/** Struct Name wrapping all {@link #STR_NAMED_VERSION} Entries.	 */
	public static final String STR_NAMED_VERSIONS = STR_NAMED_VERSION+"s";

	/** Struct Name used to (de)serialize a single (non-Branch) Tag.	 */
	public static final String STR_TAG = "tag";
	/** Struct Name wrapping all {@link #STR_TAG} Entries.	 */
	public static final String STR_TAGS = STR_TAG+"s";

	/** Field Name used to (de)serialize a Tag's Name.	 */
	public static final String STR_NAME = "name";
	/** Field Name used to (de)serialize a Tag's Version ID.	 */
	public static final String STR_ID = "id";

	/** Compares two Version-ID Arrays lexicographically by common Prefix Length, missing trailing Elements counting as 0.
	 * @return true if arr1 denotes an earlier (or equal-length Prefix but shorter) Version than arr2.	 */
	public static final boolean LESS(final int[] arr1, final int[] arr2) {
		boolean ret = false; 
		int len = arr2.length;
		if (len > arr1.length) {
			len = arr1.length; 
			ret = true; 
		}
		// TODO: LOGIC: this loop never returns false on a differing Element, only early-returns on arr1[i] < arr2[i];
		// when an earlier Index has arr1[i] > arr2[i] (arr1 lexicographically greater) but a later Index has
		// arr1[i] < arr2[i], this incorrectly returns true instead of false. Needs `else if (arr1[i] > arr2[i]) return false;`.
		for(int i = -1; ++i < len;)
			if (arr1[i] < arr2[i])
				return true;
		return ret; }
	
	/** for DeSerialization only 	*/
	transient String tagName; 
	
	/** for DeSerialization only 	*/
	transient String tagID; 
	
	/** Reads named Versions and Tags by Name from the Stream, reconstructing the {@link #namedVersions} Map.
	 * @see streamIO.integer.AStreamAble#readField(java.lang.String, streamIO.integer.IStreamIn_Struct)	 */
	public Object readField(final String name, final IStreamIn_Struct stream) {
		if (STR_NAMED_VERSION.equals(name)) {
			final Object item = stream.nextItem(); 
			if (! (item instanceof DiffSet))
				return null; //not initialized
			final DiffSet diff = (DiffSet) item; //not initialized yet! 
			namedVersions.put(diff.getID(), diff); //must be read on the closeStruct() Method!
			//since the Branch Tags are not stored (to reduce Redundancies!), they have to be added
			final DiffSet branch = (DiffSet) namedVersions.get(diff.getBranch()); 
			if ((branch == null) || LESS
				(branch.ID, diff.ID))
				namedVersions.put(diff.getBranch(), diff); /** @see streamIO.object.enumer.container.HashSet would make this Replacement much easier and faster! */
			return diff;
		} 
		if (STR_NAME.equals(name)) 
			return tagName = stream.nextString(); 
		if (STR_ID.equals(name)) 
			return tagID = stream.nextString(); 
		if (STR_TAG.equals(name)) {
			return namedVersions.put(tagName, namedVersions.get(tagID)); 
		} else if (STR_TAGS.equals(name)) {
		} else if (STR_NAMED_VERSIONS.equals(name)) {
		} else if (STR_NAMED_VERSIONS.equals(name)) {
		} else 
			return super.readField(name, stream); 
		return null; }
	
	/** appends all characteristic Members of this Object to the Stream.
	 * @param stream the PrintStream to write to.  
	 * @return a new PrintStreamOut writing into a StringBuffer. 
	 */
	public void writeTo(final IStreamOutStruct stream) {
		//super.writeTo(stream); 
		stream.open_Struct(STR_NAMED_VERSIONS); //the outer Xml must be well-formed 
		int numTags = 0; 
		String[][] tags = new String[10][]; 
		for(final Iterator iter = namedVersions.entrySet().iterator(); iter.hasNext();){
			final Map.Entry entry = (Map.Entry) iter.next();
			final Object key = entry.getKey(); 
			if (key == null)
				continue; 
			final String keyStr = key.toString(); 
			final DiffSet diff = (DiffSet) entry.getValue(); 
			if (diff == null)
				continue; 
			final String id = diff.getID(); 
			if (keyStr.equals(id)) { //choose the ID Tags
				diff.writeTo(stream, STR_NAMED_VERSION); 
			} else if (keyStr.equals(diff.getBranch())) { //sort out the Branch Tags, they are redundant! 
				//(but setting them to the latest Revision is not fast!!!
			} else { // the real (non-Branch) Tags
				// TODO: LOGIC: the resize check is off-by-one (`>` instead of `>=` before the write) and, more
				// importantly, the enlarged `tmp` Array is never assigned back to `tags` - so once more than 10
				// real Tags exist, the next iteration writes past the end of the original 10-Element Array and
				// throws ArrayIndexOutOfBoundsException, corrupting Serialization of Trees with >10 Tags.
				tags[numTags] = new String[] {keyStr, diff.getID()};
				if (++numTags > tags.length) {
					final String[][] tmp = new String[tags.length << 1][];
					System.arraycopy(tags, 0, tmp, 0, tags.length);
				}
			}
		}
		stream.closeStruct();
		stream.open_Struct(STR_TAGS);
		for(int i = numTags; --i >= 0;) {
			final String[] tag = tags[i]; 
			stream.open_Struct(STR_TAG);
			stream.writeNameValuePair(STR_NAME, tag[0]);
			stream.writeNameValuePair(STR_ID  , tag[1]);
		}
		final DiffSet head = (DiffSet) namedVersions.get(DiffSet.DEFAULT_BRANCH);
		//a full Treewalk is not possible, sind the Tree consists only of upward References.
		//you could give out the HashMap of Revision Numbers as a first Try. 
		//actually a Patricia-Trie would be better to associate the Version Numbers with the Diffs. 
		stream.closeStruct();
		stream.closeStruct();
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// abstract Methods
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * moves the current Position to the next (Child) Version 
	 * as determined by the given DiffSet
	 * Not usable in a Server Environment. 
	 */
	abstract protected DiffSet move(final DiffSet _goal, final DiffSet diff, DiffSet conflicts); 
	
	/**
	 * determines the Changes from the given currVersion to the given diffVersion
	 * based on the currVersion. 
	 * @return the DiffSetObject between the given Versions 
	 * together with the currVersion Contents
	 */
	abstract protected DiffSet calcDiff(
			final DiffSet _diffVersion, 
			final DiffSet _currVersion); 
	
	/**
	 * merges the Changes from the given version into the current Version
	 * and returns the Values and Conflicts.
	 * @param  currVersion the Version to merge into 
	 * @param  lastVersion the  last Version of the Range to be merged
	 * @param startVersion the first Version of the Range to be merged
	 * @return a DiffSet containing the Conflicts and the Values of the merged Version. 
	 * The Alternative can be obtained by applying the returned DiffSet to the contained Values. 
	 */
	abstract public DiffSet merge(final DiffSet currVersion, 
			final DiffSet startVersion, final DiffSet lastVersion);
	
	///////////////////////////////////////////////////////////////////////////
	/// concrete Implementations 
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * merges the Changes from startVersion to lastVersion into the given current Version 
	 * and returns the Values and Conflicts.
	 * @param  currVersion the Version to merge into 
	 * @param  lastVersion the  last Version of the Range to be merged
	 * @param startVersion the first Version of the Range to be merged
	 * @return a DiffSet containing the Conflicts and the Values of the merged Version. 
	 * The Alternative can be obtained by applying the returned DiffSet to the contained Values. 
	 */
	public DiffSet merge(
			final String currVersion, 
			final String startVersion, 
			final String lastVersion) {
		return merge(
				getVersion( currVersion), 
				getVersion(startVersion), 
				getVersion(lastVersion)); 
	}
	
	/**
	 * merges the Changes from the common Ancestor of the current Version 
	 * and the given version into the current Version 
	 * and returns the Values.
	 * Not usable for Server Operation! 
	 * @param version 
	 */
	public DiffSet merge(final String _lastVersion) {
		final DiffSet lastVersion = getVersion(_lastVersion); 
		final DiffSet curr = currDiff; //prevent accidental Modification! 
		return merge(curr, commonAncestor(lastVersion), lastVersion); }
	
	/**
	 * merges the Changes from 
	 * the common Ancestor of the given startVersion and the given lastVersion 
	 * to the lastVersion 
	 * into the startVersion 
	 * and returns the Values.
	 * @param version 
	 */
	public DiffSet merge(final String startVersion, final String lastVersion) {
		final DiffSet start = getVersion(startVersion);  
		return merge(start, start, 
				getVersion(lastVersion)); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// concrete Implementations 
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * determines the Changes from the given currVersion to the given diffVersion
	 * based on the currVersion. 
	 * Instead of accumulating all Differences along the way, 
	 * rather calculate a new Diff between the End Versions, 
	 * since this is typically smaller. 
	 * @return the DiffSetObject between the given Versions 
	 * together with the currVersion Contents
	 */
	public DiffSet diff(final String _diffVersion, final String _currVersion) {
		return diff(getVersion(_diffVersion), getVersion(_currVersion)); }
	
	/**
	 * determines the Changes from the given currVersion to the given diffVersion
	 * based on the currVersion. 
	 * Instead of accumulating all Differences along the way, 
	 * rather calculate a new Diff between the End Versions, 
	 * since this is typically smaller. 
	 * @return the DiffSetObject between the given Versions 
	 * together with the currVersion Contents
	 */
	protected DiffSet diff (
			final DiffSet diffVersion, 
			final DiffSet currVersion) {
		if (diffVersion.parent == currVersion)
			return diffVersion; 
		if (currVersion.parent == diffVersion)
			return currVersion.inv(); 
		return calcDiff(diffVersion, currVersion); }
	
	/**
	 * moves the current Position to the next (Child) Version 
	 * as determined by the given DiffSet
	 * Not usable in a Server Environment. 
	 */
	protected DiffSet move(final DiffSet _goal) {
		return move(_goal, null, null); }
	
	/**
	 * moves the current Position to the previous (Parent) Version. 
	 * Not usable in a Server Environment. 
	 */
	public DiffSet moveUp(final DiffSet diff, final DiffSet conflicts) { 
		return move(currDiff.parent, diff, conflicts); } 
	
	/**
	 * moves the current Position to the previous (Parent) Version. 
	 * Not usable in a Server Environment. 
	 */
	public DiffSet moveUp() { return move(true, null, null); } 
	
	/**
	 * moves the current Position to the previous (Parent) Version. 
	 * Not usable in a Server Environment. 
	 */
	public DiffSet moveDown() { return move(false, null,null);	}
	
	/** moves to the last Child Version	 */
	public void moveToLeaf() { while(moveDown() != null); }
	
	/** moves to the very first (Root-) Version	 */
	public void moveToRoot() { while(moveUp() != null); }
	
	/**
	 * moves the current Position to the next (Child) Version 
	 * as determined by the given DiffSet
	 * Not usable in a Server Environment. 
	 */
	protected DiffSet move(final boolean up, final DiffSet diff, final DiffSet conflicts) {
		return move(up ? currDiff.parent : currDiff.directChild, diff, conflicts); }
	
	/**
	 * moves the current Position to the next (Child) Version 
	 * as determined by the given DiffSet
	 * Not usable in a Server Environment. 
	 */
	protected DiffSet moveDown(final DiffSet child, final DiffSet diff, final DiffSet conflicts) { 
		return move(child, diff, conflicts); }
	
	/**
	 * moves 1 Step down the current Branch and merge the given Difference with it. 
	 * @param diff optional DiffSet to be merged along the way. 
	 * @param conflicts optional DiffSet to collect the Conflict Rows. Newly created if null and diff != null. 
	 * @return the Conflicts from merging the DiffSet or null if the End was reached. 
	 */
	protected DiffSet mergeDown(final DiffSet diff, DiffSet conflicts) {
		if (currDiff.directChild == null) 
			return null;
		move(currDiff.directChild, diff, conflicts); 
		return conflicts; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** adds the given DiffSet to the Tree, after checking whether it is allowed  */
	/** Adds the given DiffSet to the Tree, after checking that its Branch (if new) does not already exist.	 */
	final protected void addVersion(final DiffSet diff) throws VersionException {
		// TODO: LOGIC: the second clause is an exact duplicate of the first (`currDiff.getBranch() != diff.getBranch()`
		// twice), almost certainly a copy-paste mistake for a `.equals()` fast/slow-path check like the one in
		// DiffSet's own constructor (compare `_branch != parent.branch` followed by `!_branch.equals(parent.branch)`).
		// As written this relies solely on String reference identity: two equal but distinct Branch-Name String
		// instances (e.g. one read back via deserialization) are wrongly treated as "different Branch", which can
		// throw a spurious "This Branch already exists" VersionException for a legitimate same-Branch append.
		if ((currDiff != null) &&
			(currDiff.getBranch() != diff.getBranch()) &&
			(currDiff.getBranch() != diff.getBranch())) {
			if (namedVersions.containsKey(diff.getBranch())) {
				//the diff has already been added to the Parent Diff
				--currDiff.numBranches; 
				throw new VersionException("This Branch already exists:"+diff.getBranch());
			} 
		}
		addVersionSafe(diff); 
	}
	
	/** moves this Tree to the given new DiffSet 	*/
	protected void addVersionSafe(final DiffSet diff) {
		currDiff = diff; // 
		namedVersions.put(currDiff.getID(), currDiff); 
		namedVersions.put(currDiff.getBranch(), currDiff); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * retrieve the DiffSet for the given Name 
	 * @param version the Tag Name, Branch Name or VersionID of the desired Version
	 * @return retrieve the DiffSet associated with the given Name. 
	 */
	protected DiffSet getVersion(final String version) {
		return (DiffSet) namedVersions.get(version); } 		//
	
	/** Assigns a new Name to an existing Version, Tag or Branch, so it can be looked up by that Name later.
	 * @param version the Version or Name to tag
	 * @param name the (new) Tag Name.
	 * @return the DiffSet associated with this Name previously, e.g. to retrieve its Version.
	 */
	public DiffSet tag(final String version, final String name) {
		return (DiffSet) namedVersions.put(name, getVersion(version)); }
		
	/** 
	 * moves this Object to the given Revision 
	 * Not usable in a Server Environment. 
	 * @param version the Version Name to move to
	 */
	public DiffSet moveToVersion(final String version, 
			final DiffSet diff, final DiffSet conflicts) {
		return moveToVersion(getVersion(version), diff, conflicts); } 		//
	
	/** 
	 * moves this Object to the given Revision 
	 * Not usable in a Server Environment. 
	 * @param version the Version Name to move to
	 */
	public DiffSet moveToVersion(final String version) {
		return moveToVersion(getVersion(version), null, null); } 		//
	
	/** 
	 * The only and most generic Operation is to take a Difference Object 
	 * and move it up and then down the Tree to be applied to a different Version (Merging). 
	 * The other Operation is to take a concrete Object[] 
	 * and move it up and then down the Tree. 
	 * 
	 * moves this Object to the given Revision 
	 * Not usable in a Server Environment. 
	 * @param name
	 * @return the List of Conflicts encountered. 
	 */
	protected DiffSet moveToVersion(final DiffSet goal) {
		return moveToVersion(goal, null, null); }
	
	/** 
	 * The only and most generic Operation is to take a Difference Object 
	 * and move it up and then down the Tree to be applied to a different Version (Merging). 
	 * The other Operation is to take a concrete Object[] 
	 * and move it up and then down the Tree. 
	 * 
	 * moves this Object to the given Revision 
	 * Not usable in a Server Environment. 
	 * @param name
	 * @param diff optional DiffSet to which the Changes are applied. 
	 * @param conflicts optional DiffSet to collect the Conflicts on applying the Changes. 
	 * @return the Conflicts when diff != null, the common Ancestor otherwise. 
	 */
	protected DiffSet moveToVersion(final DiffSet goal, final DiffSet diff, DiffSet conflicts) {
		//find the latest common Ancestor of the current DiffSet and the chosen one
		//(this is only well-defined in DAGs)
		final ArrayList goalPath = new ArrayList(); //could analytically calculate the required Length, but too tedious. 
		conflicts = moveThroughAncestor(goal, diff, conflicts, goalPath); 
		//and down again to the desired Version.
		for(int i = goalPath.size(); --i >= 0;) 
			conflicts = moveDown((DiffSet) goalPath.get(i), diff, conflicts);
		return conflicts; 
	}

	/**
	 * @param goal 
	 * @return the common Ancestor between the current and the given DiffSet. 
	 */
	protected DiffSet commonAncestor(final DiffSet goal) {
		return moveThroughAncestor(goal, null, null, null); 
	}
	
	/**
	 * @param goal
	 * @param diff
	 * @param conflicts
	 * @param goalPath
	 * @return the Conflicts when diff != null, the common Ancestor otherwise. 
	 */
	protected DiffSet moveThroughAncestor(final DiffSet goal, final DiffSet diff, DiffSet conflicts, final ArrayList goalPath) {
		DiffSet commonAncestor = goal; 
		for(int com, cur, i = 0; 
		(i < commonAncestor.ID.length) || 
		(i < currDiff      .ID.length) ; ++i) { //the Version IDs are helpful in determining the common Ancestor. 
			com = (i < commonAncestor.ID.length) ? commonAncestor.ID[i] : 0; 
			cur = (i < currDiff.ID.length) ? currDiff.ID[i] : 0; 
			while (com > cur) { 
				if (goalPath != null)
					goalPath.add(commonAncestor); 
				commonAncestor = commonAncestor.parent; 
				com = (i < commonAncestor.ID.length) ? commonAncestor.ID[i] : 0; 
			}
			while (com < cur) {
				conflicts = moveUp(diff, conflicts); 		//move the current List up the List to the common Ancestor
				cur = (i < currDiff.ID.length) ? currDiff.ID[i] : 0; 
			}
		}
		if (conflicts != null)
			return conflicts;
		return commonAncestor; 
	}
	
}
