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
 */
public interface IScalarMetricIRing
extends IMetricIRing, IScalarMetric {
}
