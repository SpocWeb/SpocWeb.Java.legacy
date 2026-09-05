package analysis;

/**
  * Defines the Interface for a directed Parent/Child Association between two PartyTypes,
  * i.e. an allowed Responsibility at the Knowledge Level.
  * @see PartyAssociation the analogous Association between two Parties.
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
  * mtime: 2026-09-05T09:41:53Z
  * digest: f0e4687069327c454a1fd566d7de614559ef76762181c1e75c9056d455bb97c1
  * stale: false
  * tags: [code/domain_model, code/type_system]
  * concepts: [Domain Model, Relationship Modelling]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public interface PartyTypeAssociation {

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns the Parent PartyType of this Association.
	  * @return the Parent Element of this Association 	 */
	public PartyType getParent();

	/** Returns the Child PartyType of this Association.
	  * @return the Child Element of this Association 	 */
	public PartyType getChild();

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

}

