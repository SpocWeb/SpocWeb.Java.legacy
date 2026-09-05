package analysis;

/**
  * Defines the Interface for a Responsibility Type.
  * This Interface defines polymorph Operations
  * that are common to all Subtypes of Responsibility
  * along a certain Dimension of Refinement.
  *
  * For other (independent) Dimensions of Refinement
  * define a different Type.
  *
  * Instances of this Interface define the States in a State Pattern.
  * These Instances belong to the Knowledge Level
  * that defines the Rules for the operational Level.
  *
  * In Contrast to the operational Layer there is not only a single
  * Association between Parent and Child,
  * but a whole Set of Associations (a Relation).
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-21-2002, 01:04 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:42:10Z
  * digest: 3b1c35081a451e31a0dc51c25c9e6c084be890ee61770154afab91da89fccb20
  * stale: false
  * tags: [code/domain_model, code/type_system]
  * concepts: [Domain Model, Relationship Modelling]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public interface ResponsibilityType {

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the allowed Associations between Parent and Child PartyTypes.
	 * @return the allowed Associations between Parent and Child Parties
	 * represented as an Array of Party Pairs
	 * each Pair describing an allowed Responsibility
	 */
	public PartyType[][] getAllowedAssociationsAsArray();
	/** Returns the allowed Associations between Parent and Child PartyTypes. */
	public PartyTypeAssociation[] getAllowedAssociations();

	/** Allows the given Association between Parent and Child PartyTypes. */
	public void addAllowedAssociation(PartyTypeAssociation assoc);
	/** Allows an Association between the given Parent and Child PartyTypes. */
	public void addAllowedAssociation(PartyType parent, PartyType child);

	/** Disallows the given Association between Parent and Child PartyTypes. */
	public void delAllowedAssociation(PartyTypeAssociation assoc);
	/** Disallows the Association between the given Parent and Child PartyTypes. */
	public void delAllowedAssociation(PartyType parent, PartyType child);

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

}

