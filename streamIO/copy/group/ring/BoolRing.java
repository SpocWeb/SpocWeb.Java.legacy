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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:24Z
  * digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
  * stale: false
  * tags: [code/ring_theory, code/ode_solver]
  * concepts: [Ring Algebra and ODE Solvers]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public interface BoolRing
extends IRing, Boole { // ALattice //ARing
}
