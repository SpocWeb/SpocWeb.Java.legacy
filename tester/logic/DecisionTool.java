package tester.logic;

/**
  * Title: DecisionTool.java<p>
  * Description:
  * TODO: Describes the Purpose / Responsibilities of this Class, not it's Implementation.
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-04, 08;44;32<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class DecisionTool
extends DecisionTable {

	/**Array of the Operators	 */
	protected Runnable[] Operators;

	/**Initializing Constructor	 */
	public DecisionTool(byte[][] Conditions_, boolean[][] Actions_, Runnable[] Operators_) {
		super (Conditions_, Actions_);
		Operators = Operators_;
	}

	/** Processes the next Operations that match the Conditions for the Values.
	  * Initialize with negative Values.
	  * Call this Routine with Start = -1 and loop until Start = -1. 	 */
	public int evaluate(boolean[] Values, int Start) {
		if ((Start = super.evaluate(Values, Start)) >= 0) {
			int j = Operators.length;
			while (--j >= 0)
				Operators[j].run();
		}
		return Start; }

	/**Processes the next Operations that match the Conditions for the Values.
	 * Initialize with negative Values.
	 * Call this Routine with Start = -1 and loop until Start = -1. 	 */
	public void evaluate(boolean[] Values) {
		int Start = -1;
		do Start = evaluate(Values, Start);	while (Start >= 0);
	}

}
