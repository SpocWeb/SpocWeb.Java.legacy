package streamIO.copy.group.ring;

import streamIO.copy.boole.Boole;

/**
  * Title: ABoolRing.java<p>
  * Description:
  * Unifies the independent Functionalities of Rings and Boolean Groups
  * used by Containers and Streams like StreamSet.
  * It allows arithmetic AND Set Operations on thus ICountAble discrete Sets.
  *
  * Known SubClasses:
  * @see StreamSet
  * @see Container
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-12, 12;14;36<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface BoolRing
extends IRing, Boole { // ALattice //ARing
}
