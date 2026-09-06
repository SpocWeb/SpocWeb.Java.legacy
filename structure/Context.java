package structure; //

/**
  * Models a TCP/IP-like Connection whose Requests are all delegated to the current
  * {@link State}, letting the State itself decide the Connection's Behavior and Successor
  * State.
  *
  * This Class maintains the Context for the State Subclasses it references.
  * All State dependent Methods are delegated to the State Object.
  * The State Objects change the Behavior of this Context Class.
  * This saves repeated if or switch case Statements throughout the Code.
  *
  * Distributed and complex Decisions are replaced by central State Objects.
  * This makes the available States more obvious too.
  *
  * Letting the State determine its Successor State is more flexible
  * than to code it into the Context,
  * especially when the more States can be defined later.
  * One Drawback is that States have to know about each other.
  *
  * An Alternative is a Decision Table,
  * but the Behavior for a State has to be dispatched.
  *
  * When the Interface of the Context is the same as for the State
  * this is essentially a Null Decorator with changing Delegates.
  *
  * Here the Context and it's Methods are modeled after a TCP/IP Connection,
  * where the Behavior to Requests depends very much on the State.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-22-2002, 10:52 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:13:56Z
  * digest: 9378a3afb375f35066c59a3b8039c965c9869fe3f4682418494c8f5e3f0c28a9
  * stale: false
  * tags: [code/state_pattern]
  * concepts: [State Pattern Context]
  * facets: {layer: utility, status: broken, complexity: low}
  * -->
  */
public class Context {

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the current State	 */
	protected State currState; // = StateClosed.StateClosed; //Use Singletons for stateless States

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	public Context() { }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/** Opens up a Connection actively */
	public void activeOpen() {
		currState.activeOpen(this); }

	/** listens for a Connection Request passively */
	public void passiveOpen() {
		currState.passiveOpen(this); }

	/** closes a Connection (passive or active) */
	public void close() {
		currState.close(this); }

	/** Sends the collected Data */
	public void send() {
		currState.send(this); }

	/** Acknowledge Request */
	public void acknowledge() {
		currState.acknowledge(this); }

	/** Synchronize */
	public void synchronize() {
		currState.synchronize(this); }

	/** Adds another Byte to this streamIO  */
	public void processStream(java.io.OutputStream stream) {
		currState.transmit(this, stream); }

	/** Allows the State Object to change the State of this Context
	  * due to package Level Access */
	void changeState(State state) {
		currState = state; }

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Context.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
