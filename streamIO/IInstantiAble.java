package streamIO;

/**
  * Title: InstantiAble<p>
  * Description:
  * This Interface takes Account for the frequent Need,
  * especially in recursive Operations, to create new Instances of an Object,
  * especially to be filled with Contents like in ICopyAble or in IStreamOut.
  *
  * It is used in the Prototype and the Builder Design Patterns
  *
  * This Interface is very weak, because it defines only a single Operation
  * and the Return Type has to be cast to be of any Use.
  * A slower Alternative is to use class.newInstance(),
  * but that has Disadvantages:
  * no Instance specific Information can be used to initialize the new Instance.
  * (e.g. Capacity for Containers or Defaults)
  * The Name of this Method starts with a capital Letter
  * to distinguish it from the similar Method in @see ICopyAble !
  *
  * Known SubClasses:
  * @see streamIO.Copy.IICopyAble is a SubClass of this Interface
  *
  * Known Implementors:
  *
  * Design Decisions:
  * @see streamIO.Copy.ICopyAble
  * The Method is named differently to CopyAble.newInstance(),
  * because the Return Type is different and could not be overwritten otherwise.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-01-08, 08;42;31<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IInstantiAble {

	/** Creates an uninitalized new Instance of it's class.
	  * When overriding, use newInstance on all Components.	 */
	public IInstantiAble NewInstance();

}
