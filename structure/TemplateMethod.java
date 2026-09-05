package structure; //

/**
  * Declares the single parameterless {@link #run()} Operation of the Template Method
  * Pattern's Extreme, queued or scheduled rather than invoked with immediate Data.
  *
  * Defines the Interface for ...TODO: Describes the Purpose / Responsibilities
  * of this Interface, not it's Implementation.
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  * All interface Operations are implicitly public and abstract.
  * All interface Attributes are implicitly public, final and static.
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-11-2002, 09:40 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:17:53Z
  * digest: 3b49c9d11c1f721bdf63aebbc8635ae682e34e7d9b6943434dfdd08889e2683a
  * stale: false
  * tags: [code/design_patterns]
  * concepts: [Template Method Pattern]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface TemplateMethod
//extends
{

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** The Method(s) which are called by the Client
	  * Doesn't return Data or take Parameters,
	  * because it should be queued or scheduled for later Use 	 */
	void run();

}
