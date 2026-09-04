package graphs;

/** Minimum Interface to describe a copyAble Object
  * leaving out all of the changing Operations
  * This Method could be implemented by making the internal clone() Method public
  * and catching all the possible Exceptions
  * All "Value Classes" should implement this Interface,
  * because it allows for a fast shallow Copy without compromising it's Contents.
  *
  * Maybe this Interface can be used to implement the "most common" or "fastest"
  * Copy Operations e.g. for Containers.
  *
  * Design Decisions:
  * @see streamIO.Copy.ICopyAble
  * The Method is named differently to CopyAble.copy(),
  * because the Return Type is different and could not be overwritten otherwise.
  */
public interface ICopy
extends Cloneable {

	/** @return A shallow Copy of this Object
	  * The Default Implementation is to delegate to the clone() Method.
	  * The same is done in the Methods Iterator() etc. of most Container Iterators
	  * The Copy() Operation can implement a deep Copy if needed!
	  */
	ICopy Copy();

}
