package structure; //

/**
  * Enhances a chained {@link Decorator}'s {@link #doSomething} by wrapping the call, without
  * changing its Interface.
  *
  * A Decorator cannot be defined generically,
  * because it can use any existing Interface!
  *
  * Enhances the Implementation of another object retaining its Interface.
  * (e.g. adding Logging or Filtering of Streams!)
  * This allows Chaining of Decorators.
  * Subclasses can 'decorate' the Functions BEFORE or AFTER calling the Delegate Methods!
  * Just like Bridge it reduces the Number of Classes and allows mixing Class Hierarchies!
  *
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-22-2002, 08:33 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:14:22Z
  * digest: be5580fb98950de37e5a051226a1bead89d5d7085f7e622a7a2bbac670615929
  * stale: false
  * tags: [code/decorator_pattern]
  * concepts: [Decorator Pattern]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class Decorator
{

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** Object delegated to ...	 */
	protected Decorator delegate;

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected Decorator() { }

	/** Initializing Constructor taking the Delegate Object	 */
	public Decorator(Decorator _delegate) {
		this.delegate = _delegate; }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/** This is only a Representative for any Operation of the Decorator
	  */
	public Object doSomething(Object Params) {
		//possibly do something BEFORE delegating
		//possibly also change the Parameters
		Object ret = delegate.doSomething(Params);
		//possibly do something AFTER delegating
		//possibly also change the Return Value
		return ret; }

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Decorator.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
