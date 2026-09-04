package Function;

/**Implementation of an unary Function (without Arguments) working on arg.
 * This abstract Function can be concatenated with other AFunctions
 * by giving these in the Constructor or setting 'inner' later.
 * The Function Method recursively calls the 'Funktion' Methods of the Parents!
 *
 * If this Class was defined in Package Function,
 * several Optimization in simplify would not be possible.
 * These would require Identity, Cat, Const and CCountAble/CMeasurAble
 * In order not to fragment the Design too much, Cat has been moved
 * into Package Derive and CatDerive into Package RingFuncs.
 *
 * This Class makes it very clear how to concatenate Mappings
 * by implementing the Mapping and singling out the specific Function,
 * for the cost of one additional Call to myFunction().
 * This corresponds to Filters and other recursively calling Objects.
 * It makes it possible to dynamically assemble Functions for fast Evaluations
 * and to use it for analytical Operations like Derivation.  */

/**Operator Class for the Cat of unary Functions.
 * Instead of deriving all Functions from n
 * and performing the Mapping internally,
 * by handing over the inner Function in the Constructor
 * we rather create this new Class to explicitly concatenate two Functions.
 * This makes it more transparent, keeps Inheritance low,
 * is faster, doesn't require special Constructors
 * and is more similar to the other Operator Classes like Sum and Prod.
 * It reduces Complexity by eliminating inner and outer Functions
 * of Identity and null.	 */
public class Cat
extends AInvertAble {

	/**Reference to the inner of the concatenated Functions	 */
	protected Function inner;

	/**Reference to the outer of the concatenated Functions	 */
	protected Function outer;

	/**Cache for the Inverse, could also be defined in AInvertAble	 */
	protected InvertAble _Inverse;

	/**Reference to the inner of the concatenated Functions	 */
	public Function inner() { return inner; }

	/**Reference to the outer of the concatenated Functions	 */
	public Function outer() { return outer; }

	/**Constructor for a concatenated Function, the Inverse is optional	 */
	public Cat(Function outer, Function inner) {
		if  (inner == null) { inner = outer; outer = null; }
		this.inner = inner;
		this.outer = outer;
	}

	/**Returns the Inverse Function to this one:
	 * (f(g))^-1 = g^-1(f^-1)
	 * Since it is not always possible and potentially complicated,
	 * the Result is only calculated on Demand and cached.	 */
	public InvertAble Inverse() {
		if (_Inverse == null)
			_Inverse = new Cat(((InvertAble) inner).Inverse(),
							   ((InvertAble) outer).Inverse());
		return _Inverse; }

	/**This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	 */
	public final Object Map(Object arg) {
//		if (arg.getClass().isArray()) return super.Function ((Object[]) arg);
		Object tmp = inner.Map(arg); if (outer == null) return tmp;
		return       outer.Map(tmp); }

	/**@return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString() {
		String Return = inner.toString();
		if (outer != null) Return = "[" + outer.toString() + "]@(" + Return + ")";
		return Return; }

	/**Compares two Objects for equality.
	 * <p>
	 * The <code>equals</code> method implements an equivalence relation:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any reference Value <code>x</code>,
	 * <code>x.equals(x)</code> should return <code>true</code>.
	 * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and
	 * <code>y</code>, <code>x.equals(y)</code> should return
	 * <code>true</code> if and only if <code>y.equals(x)</code> returns
	 * <code>true</code>.
	 * <li>It is <i>transitive</i>: for any reference values <code>x</code>,
	 * <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
	 * returns  <code>true</code> and <code>y.equals(z)</code> returns
	 * <code>true</code>, then <code>x.equals(z)</code> should return
	 * <code>true</code>.
	 * <li>It is <i>consistent</i>: for any reference values <code>x</code>
	 * and <code>y</code>, multiple invocations of <code>x.equals(y)</code>
	 * consistently return <code>true</code> or consistently return
	 * <code>false</code>.
	 * <li>For any reference Value <code>x</code>, <code>x.equals(null)</code>
	 * should return <code>false</code>.
	 * </ul>
	 * <p>
	 * The equals method for class <code>Object</code> implements the most
	 * discriminating possible equivalence relation on objects; that is,
	 * for any reference values <code>x</code> and <code>y</code>, this
	 * method returns <code>true</code> if and only if <code>x</code> and
	 * <code>y</code> refer to the same object (<code>x==y</code> has the
	 * Value <code>true</code>).
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return  <code>true</code> if this object is the same as the obj
	 * argument; <code>false</code> otherwise.
	 * @see     java.lang.Boolean#hashCode()
	 * @see     java.util.Hashtable
	 * @since   JDK1.0 	 */
	public final boolean equals  (Object arg) {
		if (!(arg instanceof Cat)) return false;
		Cat arg_ = (Cat) arg;
		return ((inner == arg_.inner) || inner.equals(arg_.inner)) &&
			   ((outer == arg_.outer) || outer.equals(arg_.outer));	}

	/**Returns an alternative Representation that is easier to simplify
	 * Rules implemented here:
	 * Const(f(x)) == Const(x) == Const
	 * f(f^-1) = id		including --a = a and  1 / (1 / a) = a and others.   */
	public Function simplify() {	//The first Operation also eliminates Algebras, since they are only Containers
		outer  = outer.simplify();
		inner  = inner.simplify();
		if (outer instanceof InvertAble	) {	//test for f(f^-1) and f^-1(f)
			if (inner instanceof Cat)
			if (((InvertAble)outer).Inverse() ==
					   ((Cat)inner).outer)
				return ((Cat)inner).inner; }
		if (outer.canProcess(inner))	//used minimally in Negative, Inverse etc.
			return (Function) outer.Map(inner);
		return this; }

}
