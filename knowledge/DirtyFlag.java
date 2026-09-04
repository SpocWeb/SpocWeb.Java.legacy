package knowledge;

/**
 * DirtyFlag
 *
 * Created on 9. Mai 2001, 00:06
 *
 * Responsibilities:
 * <Basic Functionality>
 * Provides the Storage and Part of the Logic of a Dirty Flag.
 * An Implementation with an Object as the Value
 * can be found in
 * @see Future
 * @see ACachedProperty
 *
 * Collaborations/Patterns:
 * <Role in Patterns>
 *
 * Implementation Characteristics:
 * <Similarities and Differences to other Classes>
 *
 * Subclasses:
 * @see knowledge.IdKey
 * @see CachedValue
 *
 * @author  Matthias Heuer
 * @version
 */
public class DirtyFlag
implements IDirtyFlag {

	////////////////////////////////////////////////////////////////////////////
	//  static Members
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//  Members
	////////////////////////////////////////////////////////////////////////////

	/** Dirty Flag indicating the Modification of this Object. False by Default!
	  * Since it has to be set and reset, it is made protected and not private.
	  * The correct use can unfortunately not be enforced thus. */
	protected boolean dirty;

	////////////////////////////////////////////////////////////////////////////
	//  Accessors
	////////////////////////////////////////////////////////////////////////////

	/** @return true, when this Object has been modified, false otherwise */
	public boolean isDirty() { return dirty; }

	/** (re-)sets the Dirty Flag 	 */
	public void setDirty(boolean dirty_) { this.dirty = dirty_; }

	////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////

}
