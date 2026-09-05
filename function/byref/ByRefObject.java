package function.byref;

/**
  * Title: ByRefObject<p>
  * Description:
  * This class is for transporting an Object back from a Method Call.
  *
  * You can also simply use Object[] to return Values from Method Calls.
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-12-12, 01;52;04<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T20:48:33Z
  * digest: e8afd207f65348b163a962aaf8f2bdfaefd1eef9285784a2e22549f4b8734304
  * stale: false
  * tags: [code/function_wrapper, code/mathematical_constants]
  * concepts: [By-Reference Primitive Wrapper]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
final public class ByRefObject {
//extends Const { //in Java cannot make protected Variable 'Value' public! Only add a setValue() Method.

	/**This is the Value of the Object	 */
	public Object Value;

	/**Empty Constructor */
	public ByRefObject(){}

	/**Initializing Constructor, just comfortable	 */
	public ByRefObject(Object Value_) { Value = Value_; }

	/**Returns a string representation of the object. In general, the
	 * <code>toString</code> method returns a string that
	 * "textually represents" this object. The result should
	 * be a concise but informative representation that is easy for a
	 * person to read.
	 * It is recommendedthat all subclasses override this method.
	 * <p>
	 * The <code>toString</code> method for class <code>Object</code>
	 * returns a string consisting of the name of the class of which the
	 * object is an instance, the at-sign character `<code>@</code>', and
	 * the unsigned hexadecimal representation of the hash code of the
	 * object.
	 *
	 * @return  a string representation of the object.
	 * @since   JDK1.0
	 */
	public String toString() { return Value.toString(); }

	/**Returns a hashcode for this Byte.	 */
	public int hashCode() { return Value.hashCode(); }

	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1
	 */
	public boolean equals(Object obj) {
		return (Value   ==   obj) ||
				Value.equals(obj); }

}
