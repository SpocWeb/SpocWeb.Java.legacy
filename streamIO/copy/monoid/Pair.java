package streamIO.copy.monoid;

/**
  * Pair.java
  * Redefines the hashCode() and equals() Methods of of 'Association' 
  * to reflect the Role of both Partners.
  * Pairs have an Identity, i.e. they should be reused between Containers.
  * This Class Name is reused as a lightweight Class in
  * @see streamIO.Object.Pair to allow for quickly creating new Instances.
  * The original Reason for this was to avoid a circular Reference.
  *
  * Tripels and higher n-Tupels can be built up recursively by nesting Pairs,
  * but you should better use List.ListItem for that, because it's Interface is typed.
  *
  * Created on 26. Mai 2001, 15:30
  *
  * similar Classes:
  * @see streamIO.Object.ILinked
  * @see streamIO.Object.IPair
  * @see streamIO.Object.Pair
  * @see streamIO.Object.Enumerator.ListItem
  * @see streamIO.Copy.IMonoid.Pair
  * @see streamIO.Copy.IMonoid.Association
  *
  * @author  Matthias Heuer
  * @version
  */
final public class Pair
extends Association {

	////////////////////////////////////////////////////////////////////////////
	//	Member Variables
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Empty Constructor for (De-)Serialization	 */
	public Pair () { super(); }

	/** Constructor with the full Information for a Pair
	  * Pairs have an Identity
	  * @param key   The key for this Pair.
	  * @param value The Target Object of this Pair
	  */
	public Pair(Object key, Object value) { super(key, value); }

	////////////////////////////////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////////////////////////////////

	/**Returns a hash code Value for the object.
	 * With the Association the HashCode is exactly the key's HashCode!
	 * This has to be redefined if the Association is used to (recursively)
	 * cluster two Arguments which is done in String.DynTransByFunction.
	 *
	 * This method is supported for the benefit of hashtables
	 * such as those provided by <code>java.util.Hashtable</code>.
	 * <p>
	 * The general contract of <code>hashCode</code> is:
	 * <ul>
	 * <li>Whenever it is invoked on the same object more than once during
	 * an execution of a Java application, the <code>hashCode</code> method
	 * must consistently return the same integer. This integer need not
	 * remain consistent from one execution of an application to another
	 * execution of the same application.
	 * <li>If two objects are equal according to the <code>equals</code>
	 * method, then calling the <code>hashCode</code> method on each of the
	 * two objects must produce the same integer result.
	 * </ul>
	 *
	 * @return  a hash code Value for this object.
	 * @see     Object#equals(Object)
	 * @see     java.util.Hashtable
	 * @since   JDK1.0	 */
	public int hashCode(){
		int HC = 0;
		if (key != null) HC  = key.hashCode();
		if (val != null) HC ^= val.hashCode();
		return HC; }

	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1	 */
	public boolean equals(final Object arg) { return super.test(arg); }

	////////////////////////////////////////////////////////////////////////////
	//	Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class */
	public static void testIt() {
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String args[]) {
	}

}
