package tester.logic;

/**
  * Title: DecisionTable.java<p>
  * Description:
  *Implements a Decision Table.
  * This helps to structure complicated nested If Then Else Structures,
  * because the tabular Notation of the Conditions helps people to understand it.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-04, 08;45;26<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class DecisionTable
extends ConditionTable {

	/**List of Actions corresponding to the Conditions	 */
	protected boolean[][] Actions;

	/**String Constant for the Error Messages	 */
	final public String strActions = "Actions";

	/**Initializing Constructor	 */
	public DecisionTable(byte[][] Conditions_, boolean[][] Actions_) {
		super (Conditions_);
		int i = Conditions.length;
		if (i!= Actions   .length) throw new AbstractMethodError("Number of Rules in " + strConditions + " and " + strActions + "different");
		int ActLength = Actions   [0].length;
		while (--i > 0)
			if (ActLength != Actions   [i].length) throw new AbstractMethodError(strError + strActions    + strInconsistent);
		Actions = Actions_;
	}

	/**Empty Constructor for reading the Table from a streamIO	 */
//	public DecisionTable(){}

}
