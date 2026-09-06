package tester.logic;

/**
  * Title: ConditionTable.java<p>
  * Description:
  * TODO: Describes the Purpose / Responsibilities of this Class, not it's Implementation.
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-04, 08;45;09<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:12:21Z
  * digest: cbce57b7a6e1915143f07f462ed22113818318c3676d8fb9f927149bea77f730
  * stale: false
  * tags: [code/decision_tree, code/rule_engine]
  * concepts: [Condition Table]
  * facets: {layer: utility, status: broken, complexity: medium}
  * -->
  */
public class ConditionTable {
	/**List of Conditions	 */
	protected byte[][] Conditions;

	/**String Constant for the Error Messages	 */
	final public String strConditions = "Conditions";

	/**First Part of the Errror Message	 */
	final public String strError = "Number of Values in the ";

	/**First Part of the Errror Message	 */
	final public String strInconsistent = " inconsistent";

	/**Initializing Constructor	 */
	public ConditionTable(byte[][] Conditions_) {
		int i = Conditions_.length;
		int CndLength = Conditions_[0].length;
		while (--i > 0)
			if (CndLength != Conditions_[i].length) throw new AbstractMethodError(strError + strConditions + strInconsistent);
		Conditions = Conditions_;
	}

	/**Empty Constructor for reading the Table from a streamIO	 */
//	public DecisionTable(){}

	/**Returns the first Condition that matches the Values.
	 * Initialize with negative Values.
	 * Call this Routine with Start = -1 and loop until Start = -1. 	 */
	public int evaluate(boolean[] Values, int Start) {
//		if (Values.length != Actions   .length) throw new AbstractMethodError(strError + strActions    + strInconsistent);
//		if (Values.length != Conditions.length) throw new AbstractMethodError(strError + strConditions + strInconsistent);
		if (Start < 0) Start = Conditions.length;
		while (--Start >= 0) {
			byte[] Condition = Conditions[Start];
			int j = Condition.length;
			while (--j >= 0) {
				if ((Condition[j] == 0) &&  Values[j]) break;
				if ((Condition[j] == 1) && !Values[j]) break;
			}	//other Values are ignored
			if (j < 0) return Start;
		}
		return Start; }

}
