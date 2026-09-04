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
