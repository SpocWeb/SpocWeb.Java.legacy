package knowledge;

import java.sql.SQLException;

/** Describes the List of available Statuses for most Objects in this Package.
 *  Also Base Class for most Objects in this Package.
 */
public interface IAttribute
extends IObject {

	///////////////
	//  Methods  //
	///////////////

	/** Returns the Subject for this Object. */
	Objekt getSubject() throws SQLException;

}
