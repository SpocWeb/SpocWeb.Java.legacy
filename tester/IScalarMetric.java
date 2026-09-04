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
 */
public interface IScalarMetric
extends IComparator, IMetric 
{
}
