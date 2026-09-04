package synch;

/** This Exception Type is thrown when
  *
  *
  * Used in
  *
  * Design Decisions:
  */
public class TooManySubscribersException
extends Exception {

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'NumSubscribers' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/** holds The Number of Subscribers    */
protected int NumSubscribers;

/** @return The Number of Subscribers   */
public int getNumSubscribers() {
	return NumSubscribers; }

	/** Initializing Constructor
	  * @param Msg The Message to be displayed
	  */
	public TooManySubscribersException(int NumSubscribers_, String Msg) {
		super(Msg);
		this.NumSubscribers = NumSubscribers_; }

	/** Initializing Constructor  */
	public TooManySubscribersException(int NumSubscribers_) {
		super();
		this.NumSubscribers = NumSubscribers_; }

}
