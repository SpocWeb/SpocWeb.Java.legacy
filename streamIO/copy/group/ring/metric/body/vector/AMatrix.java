package streamIO.copy.group.ring.metric.body.vector;

import streamIO.copy.group.ring.metric.AMetricIRing;

/** Abstract Class officially introducing the Monoid Implementations
  * into the Ring Subtree.
  *
  * This has been done implicitly by having ATensor extend AAlgebra instead of
  * @see AMetricIRing
  * AMatrix  -> ATensor -> AAlgebra -> AMetricIRing
  * <!-- docstate
  * tags: [code/tensor, code/manifold_generation, code/interpolation]
  * concepts: [Vector/Matrix/Tensor and Manifold Interpolation]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public abstract class AMatrix
extends ATensor
implements IMatrix {

}
