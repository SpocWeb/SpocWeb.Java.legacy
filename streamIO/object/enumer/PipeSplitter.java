package streamIO.object.enumer;

import streamIO.IAvailAble;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;

/**
  * Title: PipeSplitter<p>
  * Description:
  * Purpose:
  * Splits a Pipe up into a StreamIn and a StreamOut Interface / joins both into a Pipe
  * The Reverse is not necessary, because a Pipe can be directly used
  * with both Interfaces.
  *
  * StreamIn, StreamOut => Pipe
  *
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-24-2002, 09:10 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class PipeSplitter
extends APipe {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Input streamIO:	 */
	protected IIStreamIn streamIn;

	/** Reference to the Output streamIO:	 */
	protected IIStreamOut streamOut;

	/** Reference to the last Object returned by the Input streamIO:	 */
	protected Object currItem;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	public PipeSplitter(IIStreamIn streamIn_, IIStreamOut streamOut_) {
		this.streamIn  = streamIn_;
		this.streamOut = streamOut_; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Parent APipe: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Parent APipe: Implementation / Overrides
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IStreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the (minimum) Number of Items left (in the Buffer),
	  * i.e. the minimum Number of times to call nextItem().
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number.
	  *
	  * Nearly equivalent is currItem != null
	  * (when the Container does not contain null Entries, like e.g. HashTables)
	  */
	public long availAble() { return ((IAvailAble)streamIn).availAble(); }

	/** @return the next (Parent) Object of this one.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	  * This is less explicit, but much faster because Exception Handling can be extremely slow.
	  * Alternatively this Method can block until new Data is available,
	  * but this should always have a TimeOut to avoid DeadLocks.
	  */
	public Object nextItem() { return currItem = streamIn.nextItem(); }

	/** @return the current Object of this one.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	  * This is less explicit, but much faster because Exception Handling can be extremely slow.
	  * Alternatively this Method can block until new Data is available,
	  * but this should always have a TimeOut to avoid DeadLocks.
	  */
	public Object currItem() { return currItem; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IStreamOut: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Adds this Item to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * The Position of the Item is undefined either.
	  * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	  * @return this StreamOut or a SubStreamOut to append more Items
	  */
	public IIStreamOut addItem(Object arg) {
		streamOut.addItem(arg);
		return this; }

	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return 0; }
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return -1; }
	
}
