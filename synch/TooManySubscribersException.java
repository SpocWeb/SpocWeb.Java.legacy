package synch;

/** Thrown by {@link IPublisher#addSubscriber} (or {@link IConstrained#addValidator})
  * when a caster that only supports a single Subscriber/Validator is asked to add
  * another one instead of being transparently upgraded to a MultiCaster/MultiValidator.
  * Carries the Number of Subscribers already registered at the time of the failed add.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:42:55Z
  * digest: d7172b68c4b4ceb5903597017ee074065a56f967e0d468e4baffeae41b18d88b
  * stale: false
  * tags: [code/validation]
  * concepts: [Custom Exception Type]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
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

/** Returns how many Subscribers were already registered when the add was rejected.
 * @return the Number of Subscribers already registered when the add was rejected   */
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
