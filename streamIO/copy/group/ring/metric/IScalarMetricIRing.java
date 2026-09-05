package streamIO.copy.group.ring.metric;

/**SMIntRing.java
 * It is useful to define all the Operations elementwise on Vectors
 * to prepare Manifold Operations and thus allow for bulk Operations.
 * Even positive() and negative() can be defined,
 * but Algorithms should then NOT assume that negative = !positive
 *
 * Created on 30. Dezember 2000, 14:26
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface IScalarMetricIRing
extends IMetricIRing, IScalarMetric {
}
