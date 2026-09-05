package structure; //

/**
  * Defines a bidirectional Pointer between an arbitrary source {@link Object} and a target
  * {@link BiRef} that automatically maintains Consistency and Reference-counts the source.
  *
  * Since you can build bidirectionally navigable Structures in Java and .NET
  * that are still Memory Managed, it is a very attractive Model
  * to embellish any Object structure using bidirectional References.
  *
  * The only Problem left is changing the Reference in an ACID Operation.
  * This has to be implemented over and over again.
  *
  * Unfortunately each Pointer Member has to be replaced by an Instance of BiRef.
  * Additionally, when setting a Reference to an Object, it has to be specified,
  * to which Property this Reference has to be set. ("Docking Point")
  * If this is a List Property, a new Reference Instance has to be created dynamically.
  * All this could be done by creating a new Pointer Object like the RefCount Pointer.
  * This is a very symmetric Handling of the Reference.
  * What is really different is that each Object has to know it's Docking Point.
  * This should be implemented by explicit Docking Methods like addItem(BiRef Item)
  * or setParent(BiRef Child), also in the Constructor.
  * Undocking happens automatically then.
  *
  * This can even be used for Memory Management:
  * If the last Reference to an Object is lost (set to null),
  * the Object can be garbage collected. The Reference Counter can be used successfully!
  * Unfortunately, like Reference Counting, this Strategy doesn't work for Cycles.
  * On the other hand no Cycles are necessary anymore
  * since any Navigation is bidirectional in principle !
  *
  * The Alternative generic Design works with (a) Collection(s) of Bidirectional Pointers
  * that are indexed in both ways, so any Relation can be traced back.
  * The Relations have to be typed to allow for several Types of Relations
  * between the same Objects. This is the relational Approach to Pointers.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-21-2002, 06:46 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:12:43Z
  * digest: 6563cfc954b2b4671f436c228792a379c8995761effb4407fb72f3f183138a9f
  * stale: false
  * tags: [code/reference_counting]
  * concepts: [Bidirectional Reference]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class BiRef
implements RefCounter {

////////////////////////////////////////////////////////////////////////////////
//  Variables
//
//  Source and Target Object are not used symmetrically!
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Source Object 	 */
	protected Object source;

	/** Reference to the Target bidirectional Reference 	 */
	protected BiRef target;

////////////////////////////////////////////////////////////////////////////////
//  Interface RefCounter: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** increases the Reference Counter 	 */
	public void incRef() { if (source instanceof RefCounter) ((RefCounter) source).incRef(); }

	/** decreases the Reference Counter 	 */
	public void decRef() { if (source instanceof RefCounter) ((RefCounter) source).decRef(); }

////////////////////////////////////////////////////////////////////////////////
//  Interface PointAble: Implementation
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns the source Object this Reference points from.
	  * @return the Source Object	*/
	public Object getSource() {
		return source; }

	/** Returns the source Object of the target BiRef this Reference points to.
	  * @return the Target Object	*/
	public Object getTarget() {
		return target.getSource(); }

	/** Sets the Reference that this Object points to to the new Target.
	  * This Operation is guaranteed to be dynamic and can not lead to DeadLocks directly! 	 */
	public synchronized void setTarget(BiRef _target) {
		if (target != null) { target.setTarget(null); if (target instanceof RefCounter) ((RefCounter) target).decRef(); }
		target = _target;
		if (target != null) { target.setTarget(this); if (target instanceof RefCounter) ((RefCounter) target).incRef(); }
	}

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected BiRef(Object _source) {
		this.source = _source; }

	/** Initializing Constructor	 */
	protected BiRef(Object _source, BiRef _target) {
		this.source = _source;
		this.target = _target;
		if  (target != null) { target.setTarget(this); if (target instanceof RefCounter) ((RefCounter) target).incRef(); }
	}

// TODO: in the Destructor / Finalizer the Target Reference has to be cleared!

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + BiRef.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
