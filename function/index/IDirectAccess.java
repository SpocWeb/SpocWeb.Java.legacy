/*
 * Created on 19.03.2005
 *
 * Defines the Interface for random Read/Write Access to a Container.  
 */
package function.index;

/**
 * Defines the Interface for random Read/Write Access to a Container.  
 * @author heuerm
 *
 */
public interface IDirectAccess 
extends IDirectRead {

	/**
	 * The Advantage of returning the Object is that 
	 * Access can be synchronized within the Container and not externally.  
	 * @param index the position to place the given Object 
	 * @return the Object which resided at the given Position previously. 
	 */
	public Object setAt(final int index, final Object value); 

}
