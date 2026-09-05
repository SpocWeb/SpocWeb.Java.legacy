package structure; //

/**
  * Declares the get/set Reference Contract a bidirectionally navigable Object must implement
  * so a {@link BiPointer} can point to it consistently.
  *
  * Since you can build bidirectionally navigable Structures in Java and .NET
  * that are still Memory Managed, it is a very attractive Model
  * to embellish any Object structure using bidirectional References.
  *
  * The only Problem left is changing the Reference in an ACID Operation.
  * This has to be implemented over and over again.
  * The Class BiPointer tries to define a bidirectional Pointer
  * that automatically maintains the Consistency.
  *
  * This Interface has to be implemented by BiPointers and all Objects
  * that are to be bidirectionally navigated to.
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  * @see BiPointer
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-21-2002, 06:47 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:17:11Z
  * digest: ff9fb26746ae09b22535340bf88fac99d3622cb31de8e817efcf79b2a65aee41
  * stale: false
  * tags: [code/reference_counting]
  * concepts: [Pointer-Like Reference Holder]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface PointAble {

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns the Reference that this Object points to 	 */
	PointAble getRef(PointAble source);

	/** Sets the Reference that this Object points to to the new Target. 	 */
	void setRef(PointAble source, PointAble target);

}
