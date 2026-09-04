package tools;

import streamIO.copy.ICopyAble;

/**
 * Applies a {@link CallAble} to a copied Subject, keeping the Copy only if the Call succeeds.
 *
 * <p>Wraps a Callable Object with Transaction Methods
 * as well as Before and After Operations (e.g. for (un-)Locking).
 * The Subject is copied before the Call
 * and the Result either replaces it (Commit) or not (Rollback).
 *
 * <p>The Semantics of {@link #call(Object)} are reversed relative to {@link CallAble}: the
 * Operation is the Argument and the Subject belongs to this Instance, which is what makes
 * a single Instance reusable across different Operations on the same Subject.
 *
 * <h2>Invariants</h2>
 *
 * <p>At most one Transaction is open at a time: {@link #startTrans()} rejects a second one
 * while a Substitute exists, and both {@link #commitTrans()} and {@link #rollBackTrans()}
 * clear it again. A null Substitute therefore means no Transaction is in progress.
 *
 * <h2>Collaborators</h2>
 *
 * <table>
 * <caption>Types this Class works with</caption>
 * <tr><th>Type</th><th>Relationship</th></tr>
 * <tr><td>{@link CallAble}</td>
 *     <td>Implemented, and also the Type of the Operation passed in as the Argument.</td></tr>
 * <tr><td>{@link streamIO.copy.ICopyAble}</td>
 *     <td>Contract the Subject must satisfy so a Copy can be taken for the Transaction.</td></tr>
 * <tr><td>{@link ErrorHandler}</td>
 *     <td>Supplies the askAbort/askRetry/askIgnore Constants this Class reuses.</td></tr>
 * </table>
 *
 * Created on 29. Januar 2001, 16:34
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-04T16:35:47Z
 * digest: 40ed294b938ad0fa8b822f7f158d112a17acae3d1f35c8235823f726db5d228d
 * stale: false
 * tags: [code/transaction, code/copy_on_write, code/decorator]
 * concepts: [Transaction Semantics]
 * facets: {layer: infrastructure, status: experimental, complexity: medium}
 * -->
 */
public class FilterCallTransAction
implements CallAble {

	/**Optional Operation run on the Subject before the Transaction, e.g. Mutex.acquire().   */
	// TODO: LOGIC: never assigned - no Constructor Parameter and no Setter reaches this
	// field or AfterOp, so the documented (un-)Locking Bracket is unreachable and both
	// null Checks in call() are always false.
	protected CallAble BeforeOp;

	/**Optional Operation run on the Subject after the Transaction, e.g. Mutex.release().   */
	protected CallAble AfterOp;

	/** The Subject is subjected to the Calls handed over in call() */
	protected ICopyAble Subject;

	/** The Depth determines to what Depth a Copy is performed. */
	protected int Depth;

	/** The Substitute Copy on which the Operations are performed. */
	protected ICopyAble Subst;

	/**Opens a Transaction by taking a Copy of the Subject to work on.
	 *
	 * @throws IllegalStateException when a Transaction is already open
	 */
	public void startTrans() {
		if (Subst != null) throw new IllegalStateException();
		Subst = Subject.copy (Depth);
	}

	/**Commits the open Transaction by promoting the Substitute Copy to be the Subject.
	 *
	 * <p>Commits a Transaction by substituting the Original
	 * This is atomic even if it is not synchronized,
	 * because only a single Object has to be swapped. */
	// TODO: LOGIC: the atomicity claim above does not hold - two non-volatile fields are
	// written unsynchronized, so a concurrent Reader can observe the new Subject while
	// Subst still points at it, or either write out of order. Only safe single-threaded.
	public void commitTrans() { Subject = Subst; Subst = null; }

	/**Abandons the open Transaction, discarding the Substitute Copy and leaving the
	 * Subject untouched.
	 *
	 * @throws IllegalStateException when no Transaction is open
	 */
	public void rollBackTrans() {
		if (Subst == null) throw new IllegalStateException();
		Subst = null;
	}

	/**Binds this Instance to the Subject it will copy, and to the Depth of that Copy.
	 *
	 * <p>The Subject is subjected to the Calls handed over in call().
	 *
	 * @param Subject the Object copied at the Start of every Transaction
	 * @param Depth how deep a Copy {@link streamIO.copy.ICopyAble#copy(int)} should take
	 */
	public FilterCallTransAction(ICopyAble Subject, int Depth) {
		this.Subject = Subject;
		this.Depth = Depth;
	}

	/**Runs the given Operation against a Copy of the Subject, committing it only on Success.
	 *
	 * <p>Here the Semantics are reversed:
	 * The callAble Function is handed over and this Class provides the Subject.
	 *
	 * <p>This is most generic, since any Number of Arguments and Return Values
	 * can be encapsulated into a single (Container) Argument
	 * and any type of Exception is derived from this Class.
	 *
	 * @param Call the Operation to apply; must itself be a {@link CallAble}
	 * @return whatever the Operation returned on the Substitute Copy, or {@code null} when
	 *         it threw
	 * @throws Throwable the Operation's own Exception, rethrown after the Rollback, or a
	 *         {@link ClassCastException} when the Argument is not a CallAble
	 */
	public Object call (Object Call) throws Throwable {
		Object ret = null;
		int ask = 0;
		do
//		BeforeOp.call (Subject); //should be guaranteed fail safe, e.g. Mutex.acquire()
		try {
			if (BeforeOp != null) {
				BeforeOp.call (Subject); }//should be guaranteed fail safe, e.g. Mutex.acquire()
			startTrans ();
			CallAble Delegate = (CallAble) Call;
//			BeforeOp.call (Subst);
//			return Delegate.call(Subst);
			ret = Delegate.call(Subst);
//			AfterOp.call (Subst);
			commitTrans ();
		}
		catch (Exception e) {
			rollBackTrans ();
			switch (ask) { //ask the User
			case ErrorHandler.askRetry : //see below
			case ErrorHandler.askIgnore:
			case ErrorHandler.askAbort : //throw the same Exception, embellished by more Information
//				ByteArrayOutputStream OS = new ByteArrayOutputStream();
//				e.printStackTrace(new PrintStream(OS));
//				IOException n = new IOException( "\n" + OS.toString ());
//				n.fillInStackTrace(); //is this necessary?
				throw e;  //shows how to propagate Exceptions with full Trace Information.
			}
		} finally { 			//never return anything from here!?!
			if (AfterOp != null) {
				AfterOp.call (Subject); } //should be guaranteed fail safe, e.g. Mutex.release()
			//return ret;
		} while (ask == ErrorHandler.askRetry);
//		AfterOp.call (Subject); //should be guaranteed fail safe, e.g. Mutex.release()
		return ret; }
}
