package structure; //

/**
  * Binds an Invoker to a {@link Runnable} Receiver by delegating {@link #run()} to it, the
  * minimal realization of the Command Pattern with no undo support.
  *
  * Defines the Interface for the Command Pattern.
  * This Design Pattern
  *
  * Intent:
  * * Provide a Binding between Invoker and Receiver:
  * Invoker => Command.execute() => Receiver.action()
  *
  * * Encapsulate a request as an object, thereby letting you parameterize clients
  * with different requests, queue or log requests, and support undoable operations.
  *
  * Also Known As:
  * Action, Transaction
  *
  * Applicability:
  * * parameterize objects by an action to perform, like MenuItem objects.
  * You can express such parameterization in a procedural language with a callback function,
  * that is, a function that's registered somewhere to be called at a later point.
  * Commands are an object-oriented replacement for callbacks.
  *
  * * specify, queue, and execute requests at different times.
  * A Command object can have a lifetime independent of the original request.
  * If the receiver of a request can be represented in an address space-independent way,
  * then you can transfer a command object for the request to a different process
  * and fulfill the request there.
  *
  * * support undo. The Command's Execute operation can store state
  * for reversing its effects in the command itself.
  * The Command interface must have an added Unexecute operation
  * that reverses the effects of a previous call to Execute.
  * Executed commands are stored in a history list.
  * Unlimited-level undo and redo is achieved by traversing this list
  * backwards and forwards calling Unexecute and Execute, respectively.
  * This can be done using a NavStack!
  *
  * * support logging changes so that they can be reapplied in case of a system crash.
  * By augmenting the Command interface with load and store operations,
  * you can keep a persistent log of changes.
  * Recovering from a crash involves reloading logged commands from disk
  * and reexecuting them with the Execute operation.
  *
  * * structure a system around high-level operations built on primitives operations.
  * Such a structure is common in information systems that support transactions.
  * A transaction encapsulates a set of changes to data.
  * The Command pattern offers a way to model transactions.
  * Commands have a common interface, letting you invoke all transactions the same way.
  * The pattern also makes it easy to extend the system with new transactions.
  *
  * Participants:
  * * Command resp. TemplateMethod:
  * - declares an interface for executing an operation.
  *
  * * ConcreteCommand:
  * - implements the Command Interface
  * - defines a binding between a Receiver object and an action.
  * - implements Execute by invoking the corresponding operation(s) on Receiver.
  *
  * * Client (Application):
  * - creates a ConcreteCommand object and sets its receiver.
  *
  * * Invoker (MenuItem)
  * - asks the command to carry out the request.
  *
  * * Receiver (Document, Application):
  * - knows how to perform the operations associated with carrying out a request.
  * - Any class may serve as a Receiver,
  *   but for Consistency the Command Interface should be implemented.
  * - to notify several Receivers a MultiCaster should be used,
  *   where several Clients can (be) register(ed).
  *
  * Consequences:
  * * decouples the object that invokes the operation from the one that knows how to perform it.
  * * Commands can be manipulated and extended like any other object.
  * * You can assemble commands into a composite command.
  * * It's easy to add new Commands, because you don't have to change existing classes.
  *   (Object Pattern)
  *
  * Collaborations:
  * * Client creates a ConcreteCommand object and specifies its receiver.
  * * Invoker object stores the ConcreteCommand object.
  * * Invoker sets the Parameters and issues a request by calling Execute on the command.
  *   When commands are undoable, ConcreteCommand stores state for undoing the command
  *   prior to invoking Execute.
  * * The ConcreteCommand object invokes operations on its receiver to carry out the request.
  *
  * Related Patterns:
  * * queued RequestObject of the ActiveObject Pattern
  * * Delegate is an Alternative to Inheritance, but Code intensive,
  *   because not supported by Languages (unlike OO inheritance which is supported!)
  * * Composite (163) can be used to implement MacroCommands.
  * * Memento (283) can keep state the command requires to undo its effect.
  * * A command that must be copied before being placed on the history list
  *   acts as a Prototype (117).
  * * Template Method is one Extreme of the Command Pattern doing everything without a Receiver.
  *   The other Extreme is a Command providing merely the Binding between Invoker and Receiver.
  *   The Receiver is in fact the Difference between Abstract Method and Command!
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-11-2002, 09:18 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:13:42Z
  * digest: 55a6572503cedb832b59b23b9756589ebdbaf1e0f46ff76da8b0b5ca27cf99f5
  * stale: false
  * tags: [code/command_pattern]
  * concepts: [Command Pattern Base]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class Command
//extends
implements Runnable
{

////////////////////////////////////////////////////////////////////////////////
//  static Constants
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Receiver Object (Delegate) 	 */
	private Runnable receiver;

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns the Receiver this Command delegates {@link #run()} to.
	  * @return the Receiver Object 	 */
	public Runnable getReceiver() {
		return receiver; }

	/** Sets the Receiver this Command delegates {@link #run()} to.
	  * @param _receiver the Receiver Object  	 */
	public void setReceiver(Runnable _receiver) {
		receiver = _receiver; }

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** This is the single Method of the Command
	  * It delegates to the Receiver Object. 	 */
	public void  run() {
		receiver.run(); }

}
