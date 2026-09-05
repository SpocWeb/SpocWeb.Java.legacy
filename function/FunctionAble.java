package function;

/** Example Interface for an unary Function with 'this' as the only argument.
  * This interface is not very useful, because the Operation Name 'FunctionAble'
  * carries no Semantics so it is hard to read and understand.
  * Rather use specific Operations, give them an understandable Name
  * and define a new Interface for them, modeled after this one.
  *
  * Design Decisions:
  * Functions are more expensive, but also more elegant than Operations,
  * because they allow for piping the result into the next Function,
  * similar to Pipes and Filters, but they have to transport the Result back,
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:12:24Z
  * digest: c0435d21959a50ccd7f03472f948b0f4e21c72e5c3642e5292635015e420734d
  * stale: false
  * tags: [code/function_contract, code/function_composition]
  * concepts: [Function/Relation Contract]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  * so they are more expensive, especially on WANs.	 */
public interface FunctionAble {

	/**This is the Template for an undefined Function working on an Object.
	 * The Object implementing this Method is the means of exchanging this Operation.	 */
	Object Function();

}
