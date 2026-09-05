package function;

/**
  * Marks an {@link IFunction} as having a computable inverse, exposed via {@code getInverse()}
  * and the {@code UnMap}/{@code UnMapAt} operations.
  *
  * Title: IInvertAble.java<p>
  * Description:
  * Interface indicating that a Function is IInvertAble
  *
  * @see also Monoid.Monoid which defines the same Operations.
  *
  * Design Decisions:
  * All Operations are the same Inverse as in Monoid
  * They are renamed to resolve uncompilable Ambiguities,
  * because they are designed to operate on Objects, instead of Monoids,
  * that is why they return Objects instead of Monoids again!
  * Monoid UnMap  (Monoid) <-> UnMap  ()
  * Monoid UnMapAt(Monoid) <-> UnMapAt()
  * Monoid getInverse() <-> IInvertAble invert ()
  *
  * Known SubClasses: Monoid, IDeriveAble
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-03, 11;34;18<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:32:24Z
  * digest: b0c24348bee0052f928515fac4415ed9d67c0c99d99878e61c7657cc8558ba18
  * stale: false
  * tags: [code/function_contract, code/function_composition]
  * concepts: [Function/Relation Contract]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface IInvertAble
extends IFunction {

	/**Returns the Inverse Function to this one: !this
	 * i.e. the Function that returns the identical Mapping,
	 * if Mapped / concatenated with this Function (at least locally)
	 * This is the same Inverse as returned from Monoid.invert()	 */
	IInvertAble getInverse();

	/** Sets the Inverse from outside.
	 * This can be done only once, after that an IllegalStateException is thrown.	 */
	void setInverse(IInvertAble inverse);

	/**Returns arg Mapped by the Inverse of this Object: !this�arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	 */
	Object UnMap (Object arg);

	/**Returns arg Mapped in Place by the Inverse of this Object: !this=�arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	 */
	Object UnMapAt(Object arg);

}
