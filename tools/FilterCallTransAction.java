package tools;

import streamIO.copy.ICopyAble;

/**
 * TransAction
 * Wraps a Callable Object with Transaction Methods
 * as well as Before and After Operations (e.g. for (un-)Locking).
 * The Subject is copied before the Call
 * and the Result either replaces it (Commit) or not (Rollback).
 *
 * Created on 29. Januar 2001, 16:34
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2006-04-14T08:30:40Z
 * digest: ef8fa9819a8b11f24e5bb6304a3ebee0f62e782b84d9c7d626a15495e8d3866e
 * stale: false
 * -->
 */
public class FilterCallTransAction
implements CallAble {

	/**Local Reference to the Delegate for the call() Method   */
	protected CallAble BeforeOp;

	/**Local Reference to the Delegate for the call() Method   */
	protected CallAble AfterOp;

	/** The Subject is subjected to the Calls handed over in call() */
	protected ICopyAble Subject;

	/** The Depth determines to what Depth a Copy is performed. */
	protected int Depth;

	/** The Substitute Copy on which the Operations are performed. */
	protected ICopyAble Subst;

	/**Starts a Transaction by creating a Copy */
	public void startTrans() {
		if (Subst != null) throw new IllegalStateException();
		Subst = Subject.copy (Depth);
	}

	/**Commits a Transaction by substituting the Original
	 * This is atomic even if it is not synchronized,
	 * because only a single Object has to be swapped. */
	public void commitTrans() { Subject = Subst; Subst = null; }

	/**Starts a Transaction by creating a Copy */
	public void rollBackTrans() {
		if (Subst == null) throw new IllegalStateException();
		Subst = null;
	}

	/**Creates new TransAction
	 * The Subject is subjected to the Calls handed over in call()
	 * The Depth determines to what Depth a Copy is performed.
	 */
	public FilterCallTransAction(ICopyAble Subject, int Depth) {
		this.Subject = Subject;
		this.Depth = Depth;
	}

	/**Here the Semantics are reversed:
	 * The callAble Function is handed over and this Class provides the Subject.
	 *
	 * This is most generic, since any Number of Arguments and Return Values
	 * can be encapsulated into a single (Container) Argument
	 * and any type of Exception is derived from this Class.
	 * Even 'Operation's that return no Value are defined
	 * by this Method returning simply 'null'.
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
