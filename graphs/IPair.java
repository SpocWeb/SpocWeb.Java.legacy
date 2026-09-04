package graphs;

/**
  * Title: IPair<p>
  * Description:
  * Defines the Interface for a read / write Object that links two Objects
  * This is more modular and standalone than using the
  * @see ILinked which also allows to link Objects, but only with itself.
  * On the other hand, unlike in an ILinked structure
  * none of the paired Processors know anything about their Use in a linked structure, .
  *
  * This is a standalone Construction that can be used in several Contexts:
  * ordered Pairs for Set Products
  * Operators like Add, Subt, Mul, Div, Cat
  * ListItem
  *
  *
  * Known SubInterfaces:
  * @see ILinked
  *
  * Known Implementors:
  * @see streamIO.Object.Pair
  * @see streamIO.Copy.IMonoid.Pair
  * @see streamIO.Copy.IMonoid.Association
  * @see streamIO.Object.Enumerator.ListItem
  *
  * Similar Interfaces:
  * @see ILinked
  * @see ILinkAble
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-07-20, 03;28;30<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IPair
extends ICPair, IValue {

	////////////////////////////////////////////////////////////////////////////////
	//  static Constants
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Accessor Method 
	 * For an Association it should not be allowed to change the Key
	 * @param _key the new key of the Pair */
	public void setKey(final Object _key);
	
	////////////////////////////////////////////////////////////////////////////////
	//  public Methods
	////////////////////////////////////////////////////////////////////////////////
	
}
