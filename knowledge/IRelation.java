package knowledge;

import java.sql.SQLException;

/**
 * This Class can aggregate Scalar Attributes and models
 * N:M Relations(with Attributes) using Subject and Objekt
 * The MetaType of the Type for this Class must always be 'Relation'(3).
 *
 * Design Decisions:
 */
public interface IRelation
extends IAttribute {

	//////////////////////
	//  static Members  //
	//////////////////////

	///////////////
	//  Methods  //
	///////////////

	/** Returns the Object for this Object. */
	Objekt getObject() throws SQLException;

}
