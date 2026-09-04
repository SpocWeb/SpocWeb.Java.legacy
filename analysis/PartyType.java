package analysis;

/**
  * Title: PartyType<p>
  * Description:
  * Defines the Interface for a Party (i.e. Group of Agents) Type.
  * This Interface defines polymorph Operations
  * that are common to all Subtypes of Party
  * along a certain Dimension of Refinement.
  *
  * For other (independent) Dimensions of Refinement
  * define a different Type.
  *
  * Instances of this Interface define the States in a State Pattern.
  * These Instances belong to the Knowledge Level
  * that defines the Rules for the operational Level.
  *
  * To allow simplifying the Rules and to structure the Party Types,
  * a hierarchical Type System is introduced by allowing Party Types
  * to be 'derived from' other Party Types.
  * This Typing could / should be made multi valued,
  * if simple Inheritance is not sufficient.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-21-2002, 09:47 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface PartyType {

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'superPartyType' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds Reference to the Super Type of this Type to be able to reuse Rules and Definitions   */
//protected PartyType superPartyType;

/** @return Reference to the Super Type of this Type to be able to reuse Rules and Definitions  */
public PartyType getsuperPartyType(); // {
//	return superPartyType; }

/** Sets Reference to the Super Type of this Type to be able to reuse Rules and Definitions  */
public void setsuperPartyType(PartyType superPartyType_); // {
//	this.superPartyType = superPartyType_; }

}

