package analysis;

/**
  * Title: ResponsibilityType<p>
  * Description:
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
  */
public interface ResponsibilityType {

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/**
	 * @return the allowed Associations between Parent and Child Parties
	 * represented as an Array of Party Pairs
	 * each Pair describing an allowed Responsibility
	 */
	public PartyType[][] getAllowedAssociationsAsArray();
	public PartyTypeAssociation[] getAllowedAssociations();

	public void addAllowedAssociation(PartyTypeAssociation assoc);
	public void addAllowedAssociation(PartyType parent, PartyType child);

	public void delAllowedAssociation(PartyTypeAssociation assoc);
	public void delAllowedAssociation(PartyType parent, PartyType child);

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

}

