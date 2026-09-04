package tools;

/**
 * callAble
 * Name adopted from the Book "Concurrent Java Programming 2nd Ed" by Doug Lea
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
 * mtime: 2003-01-25T23:39:20Z
 * digest: 29ce792e0f170f4a5861918f3f9a3f6abe30ac207369d0519646c395d5402ca4
 * stale: false
 * -->
 */
public interface CallAble {
	//Set the System Error Stream
	//System.setErr (err

	/**Any Method Call that takes an Object Argument, returns an Object
	 * and possibly throws any Exception ('Error's are not checked).
	 *
	 * This is most generic, since ANY Number of Arguments and Return Values
	 * can be encapsulated into a single (Container) Argument
	 * and any type of Exception or Error is derived from 'Throwable'.
	 * Even 'Operation's that return no Value are defined
	 * by this Method returning simply 'null'.
	 * It is necessary to define Exception as a CoverAll for any thrown Exception.
	 */
	public Object call(Object arg) throws Throwable;

}
