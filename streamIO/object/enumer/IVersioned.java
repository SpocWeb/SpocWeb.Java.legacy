/*
 * Created on 26.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.enumer;

/**
 * Interface for versioned Objects or Classes. 
 * Major and Minor Version are to distinguish between 
 * compatible Changes (only different Minor Version) and 
 * incompatible Changes (different in Major Version). 
 * 
 * Additions (Objects, Columns, Fields, Rows, Tables etc.) are typical compatible Changes, 
 * whereas Deletions and "Changes" (which can be seen as Combination of Deletion and Addition) 
 * pose a Problem, because important Fields and Relations cannot be reconstructed, 
 * whereas new Objects, Rows and Fields are simply not considered 
 * (wouldn't anyway if the Evaluation was performed earlier). 
 * 
 * This is related to the 'null' Problem in DB and Programming: 
 * In SQL and most Languages, a non-existing Object or Value can be represented as 'null' 
 * which additionally requires the Programmer to consider this Case every time, 
 * but frees him otherwise from structural Considerations. 
 * 
 * This does not capture the Fact of Upward- or Downward (Data-)Compatibility 
 * (resp. Downward or Upward Code-Compatibility). 
 * Downward Data-Incompatibility is a typical Result of added Features 
 * and typically a global one-way Data-Up-Migration is being offered. 
 * Upward Data-Incompatibility should be rarer, since the Application 
 * just needs to ignore the new Fields. 
 * Unfortunately the new Fields are not being maintained, so a mixed 
 * Version State can result, which requires a granular Data-Up-Migration. 
 * @author heuerm
 *
 * <!-- docstate
 * tags: [code/enumerator, code/iterator_adapter]
 * concepts: [Custom Streaming Enumerator and Iterator Bridge Layer for Object Collections]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public interface IVersioned {
	
	/** Returns the current major Version of the Container to support fast-fail Enumerators
	  * Should be incremented on each structural change of the Container
	  * and checked for the same Value on each Call of nextItem() or currItem()
	  * to warn the User (Client) of the Enumerator.
	  * Using int should be relatively safe,
	  * because Containers will at most contain about |int| Elements.
	  * Calling this Method additionally to nextItem() is quite expensive,
	  * so the Enumerator should try to access the Field directly.
	  */
	public int getMajor();
	
	/** Returns the current minor Version of the Container to support fast-fail Enumerators
	 * Should be incremented on each change of the Container's Content 
	 * and checked for the same Value on each Call of nextItem() or currItem()
	 * to warn the User (Client) of the Enumerator.
	 * Using int should be relatively safe,
	 * because Containers will at most contain about |int| Elements.
	 * Calling this Method additionally to nextItem() is quite expensive,
	 * so the Enumerator should try to access the Field directly.
	 */
	public int getMinor();
	
	/**
	 * Changes in the Revision merely indicate no functional Change, 
	 * only a Change in Implementation
	 * @return
	 */
	//public int getRevision();
	
	/**
	 * Changes in the Build merely indicate a different Runtime, 
	 * but no Change in Structure or Contents
	 * @return
	 */
	//public int getBuild();
	
}
