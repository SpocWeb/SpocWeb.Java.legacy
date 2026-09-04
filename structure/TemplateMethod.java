package structure; //

/**
  * Title: TemplateMethod<p>
  * Description:
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
