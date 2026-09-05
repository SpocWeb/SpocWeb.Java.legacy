package knowledge;

import java.sql.SQLException;

/**
 * An {@link IAttribute} that also names an object, modelling an N:M relation between two
 * {@link Objekt}s.
 *
 * <p>Subject and object together are the relation; because a relation is itself an
 * {@link IObject}, scalar attributes can be hung off it, which is how a relation carries
 * data of its own rather than being a bare link table.
 *
 * <p><b>Invariant:</b> the {@link MetaType} of this object's {@link Type} must always be
 * {@code Relation} (3). Nothing in this interface enforces it - it is a constraint on the
 * type data, not on the Java type.
 *
 * @see IAttribute the subject-only half this extends
 * @see MetaType the classification that must read Relation for such an object
 * @see Objekt the entity on either end of the relation
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:07:36Z
 * digest: 1966f21371d872d66df711088938453447ec7416b3ee3d116fee3cd704663924
 * stale: false
 * -->
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
