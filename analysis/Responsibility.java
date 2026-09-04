package analysis;

/**
  * Title: Responsibility<p>
  * Description:
  * Defines the Interface for a Responsibility Type.
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

/** @return Reference to the Type of this Responsibility. Alternative to Subclassing that allows to dynamically change the Type.  */
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

