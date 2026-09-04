package streamIO.copy.group.ring.metric;

import streamIO.copy.group.ring.AStepper;
import streamIO.copy.group.ring.IIntRing;
import function.IOrderAble;

/**Performs several Steps for ODE Integration with a variable Step Size
 * up to the Target. This Code is separated from the Steppers,
 * because it cannot be implemented in the base Class 	 */
public abstract class AStepperQ
extends AStepper {

	/**Performs several Steps with a variable Step Size until past the Target	 */
	public static void runVariable (AStepper self, IIntRing target) {
		//Instead of the exact Distance, use the Metric
		boolean neg = ((IOrderAble) target).isLessThan(self.x);
		if (neg != ((IScalarMetric) self.stepSize).negative()) {
			self.stepSize.negAt();} 	//first check, whether the Step Size has the correct Sign
		while (neg != ((IOrderAble) self.x).isLessThan(target)) {
			self.step(self.stepSize);} 	//Target is not stepped over (in Reach should not be used with variable Step Size)
	}

	/**Performs several Steps with a variable Step Size exactly up to the Target	 */
	public static IIntRing runVariableX (AStepper self, IIntRing target) {
		runVariable(self, target);
		return self.step((IIntRing) target.sub(self.x));	//take a last Step
	}	//to exactly land on the Target

	/**Performs several Steps with a variable Step Size until past the Target	 */
	public static void runVariable (final AStepper self, double target) {
		//Instead of the exact Distance, use the Metric
		boolean neg = target < self.xd;
		if (neg != (self.stepSizeDbl < 0)) {
			self.stepSizeDbl = -self.stepSizeDbl;} 	//first check, whether the Step Size has the correct Sign
		while (neg != (self.xd < target)) {
			self.step(self.stepSizeDbl); }	//Target is not stepped over (in Reach should not be used with variable Step Size)
	}

	/**Performs several Steps with a variable Step Size exactly up to the Target	 */
	public static double runVariableX (final AStepper self, double target) {
		runVariable(self, target);
		return self.step(target - self.xd);	//take a last Step
	}	//to exactly land on the Target

}
