package analysis;

/**
  * Defines the Interface for a Responsibility, a generalized Parent/Child Association
  * between two Parties, typed by a {@link ResponsibilityType}.
  *
  * Responsibilities are Generalizations of hierarchical Relations
  * and thus can be used to model different and concurrent
  * hierarchical Relationships like Parent, Organization Membership etc.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-21-2002, 12:56 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:42:02Z
  * digest: d7b8fc30157dbe4a7bad405a5a8549f7a8c4e7a7ce60cb8f78966f6ee6328a3a
  * stale: false
  * tags: [code/domain_model, code/type_system]
  * concepts: [Domain Model, Relationship Modelling]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public interface Responsibility
extends PartyAssociation {

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'responsibilityType' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

	/** @return the Type of this Responsibility 	 */
//	public int getResponsibilityType();

/** holds Reference to the Type of this Responsibility. Alternative to Subclassing that allows to dynamically change the Type.   */
//protected ResponsibilityType responsibilityType;

/** Returns this Responsibility's dynamically assignable Type.
  * @return Reference to the Type of this Responsibility. Alternative to Subclassing that allows to dynamically change the Type.  */
public ResponsibilityType getresponsibilityType(); // {
//	return responsibilityType; }

/** Sets Reference to the Type of this Responsibility. Alternative to Subclassing that allows to dynamically change the Type.  */
public void setresponsibilityType(ResponsibilityType responsibilityType_); // {
//	this.responsibilityType = responsibilityType_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

}

