package graphs;

/**
  * Title: IValue<p>
  * Description:
  * Defines the Interface for a stateful Objet that can return its Value.
  * This Interface is related to IStreamIn nextItem(),
  * which returns a different Item each Time it is called.
  *
  * Known SubInterfaces: IFuture, ICPair
  *
  * Known Implementors: Pair, Association
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-31-2002, 10:01 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IValue
extends ICValue, IValueSetter {

}

