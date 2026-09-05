package function;

/** Defines an Operation that returns it's Argument
  * which should be modified during the actual Operation.
  * Modifying arg and returning it cannot be enforced by the Interface,
  * but invalid Overloading can never be avoided completely.
  *
  * This is why the Result usually can be ignored,
  * making the previous Interface 'IOperator' obsolete.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:31:56Z
  * digest: c0ede52c4c002e696107ebd6f6346cd03c67fe48cd59e45bfebc9cd9781bb6e8
  * stale: false
  * tags: [code/function_contract, code/function_composition]
  * concepts: [Function/Relation Contract]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface IProcessor {

	/** Modifies {@code arg} in place and returns it.
	  * @return arg, mapped (in Place) by this Object: this.MapAt(arg) this=�arg
	  * @param  arg is being modified and returned in the Course of the Operation.
	  * This is the Function working on 'arg' defined by the implementing Class.
	  * The Class implementing this Method is the means of exchanging this Operation.
	  *
	  * The Method should be called getAt() to parallel the setAt() Method
	  * defined in IDynamicFunction.
	  */
	Object MapAt(final Object arg);

}
