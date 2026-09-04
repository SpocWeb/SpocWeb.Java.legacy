package asynch;

import knowledge.IReadyFlag;

/**
  * Title: ReadyToRun<p>
  * Description:
  * Defines the Interface for a Runnable Object,
  * that checks a Condition before being run().
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-31-2002, 07:34 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface ReadyToRun
extends Runnable, IReadyFlag {

}

