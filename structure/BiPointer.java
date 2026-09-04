package structure; //

/**
  * Title: BiPointer<p>
  * Description:
  * Since you can build bidirectionally navigable Structures in Java and .NET
  * that are still Memory Managed, it is a very attractive Model
  * to embellish any Object structure using bidirectional References.
  *
  * The only Problem left is changing the Reference in an ACID Operation.
  * This has to be implemented over and over again.
  * This BiPointer unsuccessfully tries to define a bidirectional Pointer
  * that automatically maintains Consistency.
  * The Problem with these is that for one thing all Classes have to be PointAble
  * and have to have References to only PointAbles
  * and that unsetting the Reference requires even more Methods
  * resp. knowing the Connection Points.
  * For this Collections of Connection Points have to be created
  * even in the most simple Scenarios due to the asymmetric Access Scheme
  * of not knowing where to connect when establishing the Connection,
  * but having to know where to disconnect on changing the Connection.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-21-2002, 06:46 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class BiPointer
implements PointAble {

////////////////////////////////////////////////////////////////////////////////
//  Variables
//
//  Source and Target Object are used symmetrically,
//  still they are named separately for simplicity.
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Source Object 	 */
	protected PointAble source;

	/** Reference to the Target Object 	 */
	protected PointAble target;

////////////////////////////////////////////////////////////////////////////////
//  Interface PointAble: Implementation
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns the Reference that this Object points to 	 */
	public PointAble getRef(PointAble _source) {
		if (source == _source) return target;
		if (target == _source) return source;
		return null; }

	/** Sets the Reference that this Object points to to the new Target. 	 */
	public void setRef(PointAble _source, PointAble _target) {
		if (source == _source) {
			 target.setRef(this, null);
			 target = _target;
//			_target.setRef(this, oldRef ??? TODO
			return; }
		if (target == _source) {
			return; }
	}

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected BiPointer() { }

	/** Initializing Constructor	 */
	protected BiPointer(PointAble _source, PointAble _target) {
		this.source = _source;
		this.target = _target;
	}

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + BiPointer.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
