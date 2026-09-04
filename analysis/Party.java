package analysis;

/**
  * Title: Party<p>
  * Description:
  * Defines the Interface for a Party (see Fowler: 'Analysis Patterns').
  * A Party is the Abstraction of a Person or an Organization.
  * Since a Party is usually also an Actor,
  * it is also known as 'Agent'
  *
  * A concrete Party Object implements the Composite Pattern
  * in that Parties can be Members of a different Party.
  *
  * But instead of modelling a single Hierarchy,
  * this Design can be generalized into a Responsibility.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-21-2002, 12:29 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface Party {

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'partyType' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds Reference to the Type of this Party. This is an Alternative to Subclassing that allows to dynamically change the Type   */
//protected PartyType partyType;

/** @return Reference to the Type of this Party. This is an Alternative to Subclassing that allows to dynamically change the Type  */
public PartyType getpartyType(); // {
//	return partyType; }

/** Sets Reference to the Type of this Party. This is an Alternative to Subclassing that allows to dynamically change the Type  */
public void setpartyType(PartyType partyType_); // {
//	this.partyType = partyType_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** This is equivalent to the Fact that it does/can not contain other Parties. 	 */
	public boolean isLeaf();

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

}

