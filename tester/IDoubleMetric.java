package tester;

/**
  * defines a scalar Metric between Scalars, i.e. a Function R*R->R
  *
  * This Interface defines a continuous Topology,
  * because the Return Type of 'float' allows infinite "Closeness"
  *
  * It also defines the discrete Topologies of the Superclasses.
  *
  * @see IScalarMetric which defines a Mapping O*O->R
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:32Z
  * digest: 4419daa6f591811f27ec954b15c9ed2a16b528946acdad47923cc2e43411ddc0
  * stale: false
  * tags: [code/metric_interface]
  * concepts: [Double Metric Interface]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface IDoubleMetric {

	/** defines a scalar Metric between scalar Values, i.e. a Function R*R->R
	  * @return the Distance between both Objects
	  * which may NOT be positive definite in 1D Spaces, to be able to define an Order!
	  */
	public double dist(double a, double b);

	/** defines a scalar Metric between scalar Values, i.e. a Function R*R->R
	  * @return the Distance between both Objects
	  * which may NOT be positive definite in 1D Spaces, to be able to define an Order!
	  */
	public float dist(float a, float b);

}
