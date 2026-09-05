package analysis;

/**
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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:41:32Z
  * digest: c52a48c5aec8a9a545656f88051af1b3a7a5b0e7cd1f502d52ce6a9b54f8f00f
  * stale: false
  * tags: [code/domain_model, code/type_system]
  * concepts: [Domain Model, Relationship Modelling]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
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

/** Returns the Super Type this Type derives its Rules and Definitions from, if any.
  * @return Reference to the Super Type of this Type to be able to reuse Rules and Definitions  */
public PartyType getsuperPartyType(); // {
//	return superPartyType; }

/** Sets Reference to the Super Type of this Type to be able to reuse Rules and Definitions  */
public void setsuperPartyType(PartyType superPartyType_); // {
//	this.superPartyType = superPartyType_; }

}

