package function.index;

/**
 * 
 * This Interface specifies that the Index of this Object in a Storage
 * can be stored within the Object to be able to directly access it.
 * Indexed[] is a universal structure to map Objects to Integers and vice versa.
 * This structure cannot be made fail safe though,
 * because the Index of Objects must be changeable,
 * all Interface Methods must be public and the Scope cannot be reduced. 
 * 
 * Design Decisions: 
 * to minimize the Number of Interfaces, ICountAble has been reused, 
 * instead of defining a new Interface with the Methods
 * int getIndex() and 
 * void setIndex(int)
 * This is a Change of Semantics for getInt() 
 * and can potentially lead to a Conflict when an Object wants implement both
 * ICountAble and IIndexAble 
 * 
 * @see IIn
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 51f4b68f3a6b50b8072b75def9482f2f5940fda732e5ee9b24d332bc80668ae1
 * stale: false
 * tags: [code/indexing]
 * concepts: [Indexed Collection Access]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IIndexAble 
//extends ICountAble //implies IMeasurAble and IOrderAble
{

	/** Sets the Index of this Object	 */
	public void setNdx(final int index);

	/** returns the Index of this Object	 */
	public int getNdx();

}
