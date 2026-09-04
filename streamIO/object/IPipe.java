package streamIO.object;

import streamIO.IStreamOut;

/** This Class models a Pipe which implements both Interfaces: StreamIn and StreamOut.
  * It also defines the minimum Interface for a Store,
  * allowing for adding and removing Items.
  *
  * This allows flexible Communication between two Objects and Threads.
  * To facilitate continuous Operation on both Sides you have to add caching.
  * To avoid Memory Overload, you should introduce a MaxCapacity.
  * Synchronization between Sender and Receiver can be done by blocking.
  * To avoid DeadLocks, introduce a Timeout for the Blocking.
  *
  * To communicate Changes in structure, Major should be incremented with each Item.
  * This should be done automatically by addItem() and additionally,
  * when a contained Object changes it's State.
  *
  * Since the getOrder() Method has moved to the 'streamIO' Interface, replacing isMonotonous(),
  * this Interface is essentially empty and only merges StreamIn and StreamOut.
  *
  * @see Operation.Process.StreamProcessor which is an active Component taking Input and Output streamIO
  * 	in Contrast this is a passive Component implementing both Interfaces.
  * 	By feeding the Output back to the Input, the BackTracker Component solves Problems.
  */
public interface IPipe //Store
extends IStreamOut, IStreamIn { //ModStreamIn {

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

/** Minor counts the Modification in Contents, whereas
  * Major counts the Modification in structure	 */
//public int getMajor();

/** Minor counts the Modification in Contents, whereas
  * Major counts the Modification in structure	 */
//public int incMajor();

}
