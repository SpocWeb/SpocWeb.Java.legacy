package tester;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Defines a scalar Metric between Objects, i.e. a Function O*O->R
 * It is not positive definite, it must be antisymmetric 
 * and describes a scalar Metric consistent with an Order Relation: 
 * m(a,b) =-m(b,a)
 * m(a,a) = 0 
 * 
 * This Interface defines a continuous Topology,
 * because the Return Type of 'float' allows infinite "Closeness"
 * 
 * It also defines the discrete Topologies of the Superclasses.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:33Z
 * digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
 * stale: false
 * tags: [code/metric_interface]
 * concepts: [Scalar Metric Interface]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IScalarMetric
extends IComparator, IMetric 
{
}
