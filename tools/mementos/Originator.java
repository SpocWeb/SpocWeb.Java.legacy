package tools.mementos; //

/**
  * Title: Originator<p>
  * Description:
  * Defines the Interface for storing its State into a @see Memento.
  * Since the Memento is only an empty Interface,
  * no other Class then the Instantiating can use the Memento
  * which is thus practically protected against Spoofing!
  * Instead of Memento also Object could have been used!
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
  * mtime: 2003-01-25T23:39:46Z
  * digest: d18bb5a1d272994612655baadde55d7c3b3809fb8787c5c87bbfd38fff314c89
  * stale: false
  * -->
  */
public interface Originator {

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** @return the current State of this Originator stored in the new Memento 	 */
	Memento getState();

	/** sets the internal State of this Originator to the one stored in the given Memento 	 */
	void setState(Memento state);

}
