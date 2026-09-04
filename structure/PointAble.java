package structure; //

/**
  * Title: PointAble<p>
  * Description:
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
