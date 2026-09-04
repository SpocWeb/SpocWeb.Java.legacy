/*
 * Created on 27.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tester;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * defines a (scalar) Metric between Objects, i.e. a Function O*O->R+
 * The Metric is a binary, symmetric and positive definite Function: 
 * m(a,b) = m(b,a) >= 0
 * m(a,a) = 0
 * If it is not positive definite, it must be antisymmetric 
 * and describes a scalar Metric consistent with an Order Relation: 
 * m(a,b) =-m(b,a)
 * m(a,a) = 0 
 * 
 * This Interface defines a continuous Topology,
 * because the Return Type of 'double' allows infinite "Closeness"
 * 
 * It also defines the discrete Topologies of the Superclasses.
 * 
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
 *
 * Known SubClasses: 
 * @see tester.IScalarMetric which adds a consistent Order Relation
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
public interface IMetric {
	
	/**
	 * defines a scalar Metric between Objects, i.e. a Function O*O->R
	 * which fulfills the Triangle Inequation:
	 * m(a,c) >= m(a,b) + m(b,c)
	 * with m(a,a) = 0 for all Objects a
	 * 
	 * It must either be symmetric and positive definite:
	 * m(a,b) = m(b,a) >= 0
	 * or antisymmetric:
	 * m(a,b) =-m(b,a)
	 * @return the Distance between both Objects
	 */
	public double dist(final Object a, final Object b);
	
}
