package knowledge;

/**
 * Holds the boolean modification flag that {@link IDirtyFlag} describes, and nothing else.
 *
 * <p><b>Invariant, not enforced:</b> {@code dirty} is {@code protected} rather than
 * private precisely so subclasses can both set and clear it, which means correctness
 * depends on every mutating path remembering to. The class comment below already says as
 * much, and it is the reason this base class is so small.
 *
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:10:26Z
 * digest: 946778b2895cc75af25c1b608b1736f2397fe6825b5c144fc1d3c2292303e331
 * stale: false
 * -->
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

	/**
	 * Reports the stored modification flag as it was last set.
	 *
	 * @return true, when this Object has been modified, false otherwise
	 */
	public boolean isDirty() { return dirty; }

	/** (re-)sets the Dirty Flag 	 */
	public void setDirty(boolean dirty_) { this.dirty = dirty_; }

	////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////

}
