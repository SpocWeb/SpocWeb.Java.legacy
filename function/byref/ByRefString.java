package function.byref;

/**
  * Title: ByRefString<p>
  * Description:
  * This class is for transporting a String back from a Method Call.
  * It can also be used for generic Sorting Algorithms or as a Function,
  * since it implements OrderAble and ICountAble.
  *
  * You can also simply use String[] to return Values from Method Calls.
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
  * mtime: 2026-09-05T20:48:45Z
  * digest: 178ed43b17fdfbf4ebf461b0b82f1e6a5e86955816582054f2f602fa24846e78
  * stale: false
  * tags: [code/function_wrapper, code/mathematical_constants]
  * concepts: [By-Reference Primitive Wrapper]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
final public class ByRefString {

	/**Empty Constructor */
	public ByRefString(){}

	/**Initializing Constructor, just comfortable	 */
	public ByRefString(java.lang.String Value_){Value = Value_;}

	/**This is the Value of the String	 */
	public java.lang.String Value;

	/**Returns a string representation of the object. In general, the
	 * <code>toString</code> method returns a string that
	 * "textually represents" this object. The result should
	 * be a concise but informative representation that is easy for a
	 * person to read.
	 * It is recommendedthat all subclasses override this method.
	 * <p>
	 * The <code>toString</code> method for class <code>String</code>
	 * returns a string consisting of the name of the class of which the
	 * object is an instance, the at-sign character `<code>@</code>', and
	 * the unsigned hexadecimal representation of the hash code of the
	 * object.
	 *
	 * @return  a string representation of the object.
	 * @since   JDK1.0	 */
	public String toString() { return Value.toString();}

	/**Returns a hashcode for this Byte.	 */
	public int hashCode() { return Value.hashCode();}

	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1	 */
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return  true;
		return Value.equals(obj); }

	/**Compares two Strings characterwise.	 */
	public boolean less(Object arg) {
		if ((arg == null) ||
			(arg == this)) return false;
		if (arg instanceof ByRefString)
			return less(Value, ((ByRefString) arg).Value);
			return less(Value,  (     String) arg); }

	/**Loads a File into the String.
	 * Usually this is ineffective, since most Operations are already defined on Streams
	 * and can dynamically load Data just when they are needed!	 */
	public static String File2String(String FileName) {
		String str = null;
		//Copy the Implementation from the Applet
		return str; }

	/**Returns a String in which Characters i and j are swapped	 */
	public static String swapChar(String in, int i, int j) {
		if (i == j) return in;
		if (i >  j) {int t = i; i = j; j = t;}	//i is the smaller one
		return	in.substring(0	, i)+ in.charAt(j) +
				in.substring(i+1, j)+ in.charAt(i) +
				in.substring(j+1);
	}

	/**Compares two Strings characterwise.	 */
	public static boolean less(String a, String b) {
		int Length = a.length();
		if (Length > b.length())
			Length = b.length();
		int j = -1; //start from the beginning!
		while (++j < Length)
			if (a.charAt(j) >= b.charAt(j)) return false;
		return true; }

}
