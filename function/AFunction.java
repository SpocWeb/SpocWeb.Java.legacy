package function;

import java.io.File;

/**Implementation of an unary Function (without Arguments) working on arg.
 * This abstract Function can be concatenated with other AFunctions
 * by giving these in the Constructor or setting inner later.
 * The Function Method recursively calls the Funktion Methods of the Parents!
 * For this to happen the super.Function has to be called each time!
 *
 * This Class makes it very clear how to concatenate Mappings
 * by implementing the Mapping and singling out the specific Function,
 * for the cost of one additional Call to myFunction().
 * This corresponds to Filters and other recursively calling Objects.
 * It makes it possible to dynamically assemble Functions for fast Evaluations
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:31:33Z
 * digest: 71d0e17bc3e162b495201fcddb1937b05e4d51056f458b7d66dbe5bd448b1fda
 * stale: false
 * tags: [code/function_contract, code/function_composition]
 * concepts: [Function/Relation Contract]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 * and to use it for analytical Operations like Derivation.  */
public abstract class AFunction
implements IFunction {

    /** Default Value for incoming null Values	 */
    final static public String NULL_MAPPING = null; 
    
    ///////////////////////////////////////////////////////////////////////////
    /// Stateless and parameterless Functions are declared here as static Flyweights
    ///////////////////////////////////////////////////////////////////////////
    
    /** Maps a File Object to it's File Path String     */
    final static public AFunction FILE_NAME_FUNCTION = new AFunction() {
        
        /** @see function.IFunction#Map(java.lang.Object)     */
        public Object Map(final Object arg) {
            if (arg == null)
                return NULL_MAPPING; 
            return ((File) arg).getName(); }
        
    };
    
    /** Maps a File Object to it's File Path String     */
    final static public AFunction TO_STRING_FUNCTION = new AFunction() {
        
        /** @see function.IFunction#Map(java.lang.Object)     */
        public Object Map(final Object arg) {
            if (arg == null)
                return NULL_MAPPING; 
            return arg.toString(); }
        
    };
    
    ///////////////////////////////////////////////////////////////////////////
    /// preliminary Member Implementations 
    ///////////////////////////////////////////////////////////////////////////
    
	/** Maps {@code arg} in place; the base implementation always throws and must be overridden.
	  * @return arg mapped in Place by this Object: this.MapAt(arg) this=�arg
	  * This is the Function working on 'arg' defined by the implementing Class.
	  * The Class implementing this Method is the means of exchanging this Operation.	 */
	public Object MapAt(final Object arg) {
		throw new AbstractMethodError(); }
	//  return arg; }
	
	/** This applies the Function to each Item of an Array of Objects	 */
	public Object[] Map(final Object[] arg) {
		int Length = arg.length;
		Object[] Return = new Object[Length];
		while (--Length >= 0)
			Return[Length] = Map(arg[Length]);
		return Return; }

	/**Class that can be processed by this Function
	 * If more than a single Class can be processed (apart from the Inverse),
	 * the Method 'canProcess()' has to be overwritten.
	 * This is a bit of a Memory Overhead, because this is an Instance Variable */
//	protected Class ProcessAble = java.lang.Object.class;

	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative
	 * not to operate on the Constants,
	 * but to operate on the Functions and operate the Results on evaluation.
	 * Returns false by Default,
	 * because most simple Functions are not even Algebras.	 */
	public boolean canProcess(final Object arg) { return false; } //ProcessAble.isInstance(arg); }

	/**Alternative Representation that is 'simplify()'ed
	 * or can be simplified more easily.
	 * This is a bit of a Memory Overhead, because this is an Instance Variable */
//	protected IFunction simple =  this;

	/**Returns an alternative Representation that is 'simplified'	 */
	public IFunction simplify() {
//		return simple;
		return this; }

	/**Returns the 'simplified' Concatenation of this Function and arg.	 */
//	public IFunction simplify(IFunction arg){return null;}

	/** Returns the fully qualified class name of this function.
	 * @return  The string representation of the Function.
	 * @since   JDK1.0
	 * Uses getName() to get the fully qualified Name,
	 * which is needed for Restoration in the forName() Method.	 */
	public String toString()	{ return getClass().getName(); }// toString();}

}
