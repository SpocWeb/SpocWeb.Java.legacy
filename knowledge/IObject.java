package knowledge;

import java.sql.SQLException;

/**
 * Base interface for most objects in this package, giving each one a {@link Status} and a
 * {@link Type}.
 *
 * <p>Both are exposed twice: as the resolved object, which may hit the database and so
 * throws, and as the raw integer ID, which is whatever the record already holds and never
 * throws. Callers that only need to compare or persist an object should prefer the ID
 * accessors, because resolving is the expensive half.
 *
 * @see Status the lifecycle state an object is in
 * @see Type the classification an object belongs to
 * @see IAttribute the sub-interface for objects qualifying another object
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:07:26Z
 * digest: 4855d745126326a3a5dfef45e7fef31c9f50798a59a098db19119792967a942d
 * stale: false
 * -->
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

	/**
	 * Unused placeholder left over from a modelling-tool template, referenced nowhere in the
	 * tree.
	 */
    int attribute1 = 0;
}
