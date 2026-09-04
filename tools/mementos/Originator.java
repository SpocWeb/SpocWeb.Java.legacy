package tools.mementos; //

/**
  * Defines the Interface for capturing and restoring an Object's own State via a {@link Memento}.
  *
  * <p>Since the Memento is only an empty Interface,
  * no other Class then the Instantiating can use the Memento
  * which is thus practically protected against Spoofing!
  * Instead of Memento also {@code Object} could have been used!
  *
  * <h2>Collaborators</h2>
  *
  * <table>
  * <caption>Types this Interface works with</caption>
  * <tr><th>Type</th><th>Relationship</th></tr>
  * <tr><td>{@link Memento}</td>
  *     <td>Opaque Snapshot this Interface produces and consumes; only the Producer can read it.</td></tr>
  * </table>
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-22-2002, 07:51 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  *
  * @see Memento for an empty Interface only indicating a Memento.
  * @see Originator creating and consuming Mementos
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-04T16:35:47Z
  * digest: c02e6618cf7aea70cfe5b8ccaa564438d0dfa633b2e08d3540be19c46ec8ea77
  * stale: false
  * tags: [code/state_snapshot, code/interface_contract]
  * concepts: [Memento Pattern]
  * facets: {layer: infrastructure, status: stable, complexity: low}
  * -->
  */
public interface Originator {

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Captures this Originator's current internal State into a freshly created Memento.
	 *
	 * @return the current State of this Originator stored in the new Memento
	 */
	Memento getState();

	/**
	 * Restores this Originator to the State captured in the given Memento.
	 *
	 * @param state a Memento this same Originator produced earlier via {@link #getState()}
	 */
	void setState(Memento state);

}
