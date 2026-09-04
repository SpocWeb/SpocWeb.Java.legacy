package streamIO;

/**
  * Title: IFactory<p>
  * Description:
  * Defines the Interface for abstract Factories without Parameters.
  * Factories can return an unlimited streamIO of Objects
  * or the same Object (Flyweight, e.g. Null Pattern)
  *
  * Usually different Factories have to be registered with an Application,
  * so the Application can determine when and how to create Objects.
  *
  * Since the Factory Method does not take any Parameters
  * only non initialized Instances will be returned.
  *
  * Related Interfaces:
  * @see graphs.ICValue which should always return the same Object using get()
  * With a Singleton the IFactory would more act like an ICValue!
  * @see structure.IRegistry which takes a Parameter to determine the Type of Object to return. 
  *
  * Known SubInterfaces:
  * @see streamIO.IIStreamIn
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-28-2002, 06:10 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IFactory {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the next (Parent) Object of this one.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	  * This is less explicit, but much faster because Exception Handling can be extremely slow.
	  * Alternatively this Method can block until new Data is available,
	  * but this should always have a TimeOut to avoid DeadLocks.
	  */
	public Object nextItem();

}
