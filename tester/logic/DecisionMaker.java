package tester.logic;

import tester.ITestAble;

/**
  * Title: DecisionMaker.java<p>
  * Description:
  * Self-reliant Evaluator for the ITester Functions and the Operations.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-04, 08;45;48<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class DecisionMaker
extends DecisionTool {

	/**Array of the Testers	 */
	protected ITestAble[] Testers;

	/**Local Storage for the Evaluations of the ITester Functions	 */
	protected boolean[] Values;

	/** Initializing Constructor taking
	  * A Matrix of Conditions
	  * A Matrix of Actions
	  * A List of ITester Objects
	  * A List of Operators
	  */
	public DecisionMaker(byte[][] Conditions_, boolean[][] Actions_,
						 ITestAble[] Testers_, Runnable[] Operators_) {
		super (Conditions_, Actions_, Operators_);
		Testers = Testers_;
	}

	/**Evaluates all the ITester Functions	 */
	public void Test() {
		int i = Values.length;
		while(--i >= 0) Values[i] = Testers[i].Test();
	}

	/**Processes the next Operations that match the Conditions for the Values.
	 * Initialize with negative Values.
	 * Call this Routine with Start = -1 and loop until Start = -1. 	 */
	public int evaluate(int Start)	{ return super.evaluate(Values, Start); }

	/**Processes the next Operations that match the Conditions for the Values.
	 * Initialize with negative Values.
	 * Call this Routine with Start = -1 and loop until Start = -1. 	 */
	public void evaluate() {
		int Start = -1; Test();
		do  Start = evaluate(Values, Start); while (Start >= 0);
	}

}
