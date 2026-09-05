package function;

/**
 * Interface for an unary Function (single Argument) working on arg
 * without throwing any Exceptions. An even more generic Interface is 'callAble'.
 *
 * Design Decisions:
 * Any additional Parameters for 'Funktion' are left out, to keep the Interface clean.
 * They have to be set globally with the concrete Class or put into a Container
 * like Association, Array or an Instance of a generic Container.
 * The Interface String.ITransition makes a second Parameter explicit,
 * so you don't have to wrap and unwrap it and the Interface is more explicit.
 *
 * Since most Functions don't have a State, they also don't have local Variables
 * and could as well be implemented using static Methods.
 * Even those static Methods could be parameterized using static Variables,
 * but unfortunately you cannot declare Interface Methods to be static,
 * native, syncronized or final!
 * The only Information in some Function is lying in 'this' resp. it's VMT
 * which points to different Functions.
 *
 * All Operations are the same Inverse as in Monoid
 * They are renamed to resolve uncompilable Ambiguities,
 * because they are designed to operate on Objects, instead of Monoids,
 * that is why they return Objects instead of Monoids again!
 * Object Map  (Object) <-> SemiMonoid map  (SemiMonoid)
 * Object MapAt(Object) <-> SemiMonoid MapAt(SemiMonoid)
 *
 * The Interface IInvertAble.getInverse() and Monoid.invert() are kept separate
 * because the first denotes a Function, whereas the Seconde denotes an Operation!
 *
 * Functions are more expensive, but also more elegant than Operations,
 * because they allow for chaning the result into the next Function,
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:31:51Z
 * digest: 3ebc816c3741327d15c1634f3aebab3ff8ad65bde4d82e7a5ff71f7e03292a47
 * stale: false
 * tags: [code/function_contract, code/function_composition]
 * concepts: [Function/Relation Contract]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 * similar to Pipes and Filters, but have to transport the Result back. */
public interface IFunction
extends IProcessor {
	
	/** Maps {@code arg} to its result according to the implementing class.
	  * @return arg mapped by this Object: this.Map(arg) == this�arg
	  * This is the Function working on 'arg' defined by the implementing Class.
	  * The Class implementing this Method is the means of exchanging this Operation.	 */
	Object Map (final Object arg);
	
	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative for the Algebra
	 * to delegate the Operation to the inner Function,
	 * but to operate on the Functions and operate the Results on evaluation.	 */
	boolean canProcess(final Object arg);
	
	/**Returns the 'simplified' Concatenation of this Function and arg.
	 * Originally intended for Resolving f(f^-1),
	 * which is now handled by Catenation.simplify()
	 * This is now responsible for the other Operations, e.g. -(Group)	 */
//	public IFunction simplify(IFunction arg);	//no longer needed
	
	/**Returns an alternative Representation that is 'simplified'	 */
	IFunction simplify();
	
}
