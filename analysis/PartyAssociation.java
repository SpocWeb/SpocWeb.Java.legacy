package analysis;

/**
  * Defines the Interface for a directed Parent/Child Association between two Parties.
  * @see PartyTypeAssociation the analogous Association between two PartyTypes.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-21-2002, 10:41 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:41:43Z
  * digest: 686dcc6cf09668487e5badc26701d89cc694d12fa0d8919781d0be6d9c80fdcc
  * stale: false
  * tags: [code/domain_model, code/type_system]
  * concepts: [Domain Model, Relationship Modelling]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public interface PartyAssociation {

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns the Parent Party of this Association.
	  * @return the Parent Element of this Association 	 */
	public Party getParent();

	/** Returns the Child Party of this Association.
	  * @return the Child Element of this Association 	 */
	public Party getChild();

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

}

