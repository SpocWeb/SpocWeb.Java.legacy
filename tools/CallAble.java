package tools;

/**
 * Encapsulates an arbitrary Operation as a single Object-to-Object Call that may throw anything.
 *
 * <p>Name adopted from the Book "Concurrent Java Programming 2nd Ed" by Doug Lea.
 * This is the most generic Call Signature available: any Number of Arguments and Return
 * Values fits into one Container Argument, and every Exception and Error derives from
 * {@link Throwable}, so no Implementation ever has to widen the Interface.
 *
 * <h2>Collaborators</h2>
 *
 * <table>
 * <caption>Types this Interface works with</caption>
 * <tr><th>Type</th><th>Relationship</th></tr>
 * <tr><td>{@link FilterCallTransAction}</td>
 *     <td>Decorates a CallAble with Transaction and before/after Behaviour.</td></tr>
 * <tr><td>{@link ErrorHandler}</td>
 *     <td>Wraps a CallAble to turn its Throwable into handled Error Reporting.</td></tr>
 * </table>
 *
 * Created on 7. Januar 2001, 18:10
 * @see also: Function.Function which defines the same Interface,
 *			except for declaring the Exception.
 *			In principle you can replace 'CallAble' by 'Function'.
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-04T16:35:46Z
 * digest: be38cf2148451f456f3f524d2431d414c0f12d2e4b0f830ac0ace189e10386f2
 * stale: false
 * -->
 */
public interface CallAble {
	//Set the System Error Stream
	//System.setErr (err

	/**Performs the encapsulated Operation on the given Argument.
	 *
	 * <p>Any Method Call that takes an Object Argument, returns an Object
	 * and possibly throws any Exception ('Error's are not checked).
	 *
	 * <p>This is most generic, since ANY Number of Arguments and Return Values
	 * can be encapsulated into a single (Container) Argument
	 * and any type of Exception or Error is derived from 'Throwable'.
	 * It is necessary to define Exception as a CoverAll for any thrown Exception.
	 *
	 * @param arg the single (Container) Argument carrying every Input the Operation needs
	 * @return the Operation's Result, or {@code null} for an Operation that returns no Value
	 * @throws Throwable whatever the encapsulated Operation itself throws
	 */
	public Object call(Object arg) throws Throwable;

}
