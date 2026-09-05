package knowledge;

import java.sql.SQLException;

/**
 * An {@link IObject} that qualifies exactly one subject {@link Objekt}.
 *
 * <p>The inherited type decides what kind of qualification it is: a type whose
 * {@link MetaType} is scalar makes this a plain attribute, while the {@code Relation}
 * meta-type makes it an {@link IRelation}, which adds an object side and so becomes N:M.
 * The subject side is the half every attribute has, which is why it lives here.
 *
 * @see IObject the status and type contract this extends
 * @see IRelation the specialisation that adds the object side
 * @see Objekt the entity a subject reference points at
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:07:14Z
 * digest: 4f4123369d7d12f341250849fbd47e9e18a25d43795079d28a169d1b03eb0f89
 * stale: false
 * -->
 */
public interface IAttribute
extends IObject {

	///////////////
	//  Methods  //
	///////////////

	/** Returns the Subject for this Object. */
	Objekt getSubject() throws SQLException;

}
