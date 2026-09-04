package knowledge;

import java.sql.SQLException;

/** Describes the List of available Statuses for most Objects in this Package.
 *  Also Base Class for most Objects in this Package.
 */
public interface IObject {

	////////////////////////////////////////////////////////////////////////////
	//  static Members  
	////////////////////////////////////////////////////////////////////////////
	
	///////////////
	//  Methods  //
	///////////////

	/** Returns the Status for this Object */
	Status getStatus() throws SQLException; 
	
	/** Returns the Type for this Object */
	Type getType() throws SQLException; 
	
	/** Returns the Status for this Object */
	int getStatusID(); 
	
	/** Returns the Type for this Object */
	int getTypeID();

    int attribute1 = 0;	
}
