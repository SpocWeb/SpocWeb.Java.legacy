package streamIO.copy.group.ring.metric.body;

import streamIO.copy.ACopyAble;
import streamIO.copy.group.ring.metric.TestMetric;

/**Tests all methods in this package.
 *
 * <!-- docstate
 * tags: [code/rational_numbers, code/interval_arithmetic]
 * concepts: [Rational Numbers and Interval Arithmetic]
 * facets: {layer: domain, status: legacy, complexity: high}
 * digest: a32f6471d3d5bb15d4d766c6c05b666248b80f41d1ac54a3286cc6661af5543a
 * stale: false
 * -->
 */
public class TestBody {

	/**The main entry point for the application.
	 * @param args Array of command line parameters passed to the application
	 * After testing this Package, calling the Parent Packages.	 */
	public static void main (String[] args) throws Exception {
		//concrete Testing Object can be defined now! Using BodyDouble!
		MetricBody BD = new BodyDouble(5);
		MetricBody e_1 = BD.expM1(); 
		ACopyAble.testInstance =  new BodyDouble(5);
		AMetricBody.testIt();
		Object BD2;
		long Z = 1;
//		Z = 1000000; while (--Z >= 0)
			BD2 = BD.add(BD); 	//150 = 6*25 times longer than direct call
		System.out.println("Testing Package Body with "+Z+" Iterations:"+BD2);
//		Z = 10000000; while (--Z >= 0)
			BD.addAt(BD); 	//150 = 6*25 times longer than direct call
		System.out.println("Testing Package Body");
		BodyDouble.testIt();	//sets testInstance = testIt(new BodyDouble());
//		System.in.read();
		AMetricBody.testIt();
		Fraction.testIt();
		TestMetric.main(args);
	}
}
