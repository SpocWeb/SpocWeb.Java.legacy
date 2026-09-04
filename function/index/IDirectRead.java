/*
 * Created on 19.03.2005
 *
 * Defines the Interface for random Read Access to a Container.  
 */
package function.index;

/**
 * Defines the Interface for random Read Access to a Container.
 * @see java.sql.ResultSet Could implement this Interface to access a specific Row. 
 * @author heuerm
 *
 */
public interface IDirectRead {

	/** 
	 * returns the Object at the given Index / Position
	 * 
	 * @param index the Position of the Object to retrieve 
	 * @return the Object at the given Index / Position
	 */
	public Object getAt(final int index);
	
}
