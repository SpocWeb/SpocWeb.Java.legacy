package function;

/** Defines an Operation that returns it's Argument
  * which should be modified during the actual Operation.
  * Modifying arg and returning it cannot be enforced by the Interface,
  * but invalid Overloading can never be avoided completely.
  *
  * This is why the Result usually can be ignored,
  * making the previous Interface 'IOperator' obsolete.
  */
public interface IProcessor {

	/** @return arg, mapped (in Place) by this Object: this.MapAt(arg) this=°arg
	  * @param  arg is being modified and returned in the Course of the Operation.
	  * This is the Function working on 'arg' defined by the implementing Class.
	  * The Class implementing this Method is the means of exchanging this Operation.
	  *
	  * The Method should be called getAt() to parallel the setAt() Method
	  * defined in IDynamicFunction.
	  */
	Object MapAt(final Object arg);

}
