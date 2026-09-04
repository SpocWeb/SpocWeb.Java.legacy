package tools.mementos; //

/**
  * Marker Interface identifying an opaque Snapshot of an {@link Originator}'s internal State.
  *
  * <p>This is only a flagging Interface to indicate the Purpose of the Classes implementing it.
  * Effectively also {@code Object} could have been used,
  * but this Interface adds Clarity, some Compile Time Checking
  * and the Ability to amend it with Helper Methods for the Caretaker.
  * Unfortunately {@link Originator} and Memento cannot be implemented in the same Java Source File.
  *
  * <h2>Invariants</h2>
  *
  * <p>A Memento:
  * <ul>
  * <li>stores internal state of the Originator object and is thus quite passive ('struct')!
  *   The memento may store as much or as little of the originator's internal state
  *   as necessary at its originator's discretion.
  * <li>protects against Access by Objects other than the Originator.
  *   Mementos have effectively two Interfaces.
  *   The Caretaker sees this narrow Interface to the Memento.
  *   It can only pass the Memento to other Objects for Storage.
  *   Originator, in contrast, sees a wide Interface,
  *   one that lets it access all the Data necessary to restore itself to its previous State.
  *   Ideally, only the originator Class that produced the Memento
  *   would be permitted to access the Memento's internal State
  *   using friend/package Access.
  * <li>Since the Memento is only an empty Interface,
  *   no other Class then the Instantiating can use the Memento
  *   which is thus practically protected against Spoofing!
  * </ul>
  *
  * <h2>Collaborators</h2>
  *
  * <table>
  * <caption>Types this Interface works with</caption>
  * <tr><th>Type</th><th>Relationship</th></tr>
  * <tr><td>{@link Originator}</td>
  *     <td>Sole legitimate Producer and Consumer of a Memento; sees the wide Interface.</td></tr>
  * </table>
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-22-2002, 07:49 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  *
  * @see Memento for an empty Interface only indicating a Memento.
  * @see Originator creating and consuming Mementos
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-04T16:35:47Z
  * digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
  * stale: false
  * tags: [code/marker_interface, code/state_snapshot]
  * concepts: [Memento Pattern]
  * facets: {layer: infrastructure, status: stable, complexity: low}
  * -->
  */
public interface Memento { }

