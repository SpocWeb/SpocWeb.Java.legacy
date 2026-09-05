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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:18Z
  * digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
  * stale: false
  * tags: [code/graph_element]
  * concepts: [Value Interface]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
public interface IValue
extends ICValue, IValueSetter {

}

