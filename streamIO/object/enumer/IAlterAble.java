package streamIO.object.enumer;

 //.Enumerator;

//import Stream.Object.ChangeAble;

/**
  * Title: AlterAble.java<p>
  * Description:
  * This Interface is implemented by Containers supporting the addItem() and removeItem() Method
  * and / or an Enumerator Iterator.
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-05, 10;25;27<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * tags: [code/enumerator, code/iterator_adapter]
  * concepts: [Custom Streaming Enumerator and Iterator Bridge Layer for Object Collections]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public interface IAlterAble
extends IChangeAble, IVersioned {

	/** Returns a new Intstance of an Enumerator at the same Position,
	  * which allows for changing the Data and structure concurrently. */
//	public Enumerator Enumerator();

	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * which allows for changing the Data and structure concurrently. */
	Enumerator Enumerator();

	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/**Increments and returns the current Major Version of the Container
	 * to indicate Modification to fast-fail Iterators.
	 * The Version should be incremented on each structural change of the Container
	 * and checked for the same Value on each Call of nextItem() or currItem()
	 * to warn the User (Client) of the Iterator.
	 * Using int should be large enough,
	 * because Containers will at most contain about |int| Elements.
	 */
	public int incMajor();

}
