package streamIO.object.backTrack;

import streamIO.object.IPipe;
import streamIO.object.enumer.container.DeQueueArr;
import function.FunctionByHash;

/**
  * Title: noname2<p>
  * Description:
  * TODO: Describes the Purpose / Responsibilities of this Class, not it's Implementation.
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
/** Generates candidate sentence phenotypes from grammar-rule genotypes for use as a
  * {@link BackTracker} generator.
  * <p>
  * Generator and ITester Class for the Generation of Sentence Phenotypes
  * from Grammar Genotypes.
  * The Function could actually be a FunctionByHash
  * that returns an Array of possible Result Objects
  * for a given Input Object.
  * The Purpose of this Class is just to assemble the Result of all Mappings
  * to provide a Backlog. 
  * Could also be split up into two Classes.
  * This saves handing over the Solution to each Item.
  * On the other Hand the Solution must be known to the Generator for it
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T20:45:01Z
  * digest: b1afe77d3a240312290ba924f9a504f4a24be63a680fe16c723264357fecf6b8
  * stale: false
  * tags: [code/backtracking, code/algorithm]
  * concepts: [Backtracking Search]
  * facets: {layer: utility, status: broken, complexity: medium}
  * -->
  * to generate good Candidates.	 */
public class Grammar
//extends AFunction
//implements ITester
{

	/**Initializing Constructor taking the Size of the AchterProblem
	 * and the Solution	 */
	public Grammar(Object Start, Object[]Starts, Object[][]Productions) {
	}

	/**Tests all Methods of this Class	 */
	public static void testIt()	{
		System.out.println ("Testing GrammarState:");
		IPipe Store;	//avoid reusing existing Elements

		//Queue with avoiding Duplicates, takes 20 Tries (Breadth-first, LIFO)
		Store = new DeQueueArr(100, IPipe.ORDER_QUEUE);
		Store.addItem(new GrammarState("C0 Start"));
		FunctionByHash Fn = new FunctionByHash();
		Object[] tmp;
		String[] str;
		int j, i = GrammarState.States.length;
		while (--i >= 0) {
			str = GrammarState.Productions[i]; j = str.length;
			tmp = new Object[j];
			while (--j >= 0) {
				tmp[j] = new GrammarState(str[j]); }
			Fn.setAt(new GrammarState(GrammarState.States[i]), tmp); }
		BackTracker BT;
		BT = new BackTracker(Store, null, null, Fn, true);
		System.out.println (Fn.Map(new GrammarState("C0")));
		System.out.println (((GrammarState)BT.nextItem()).Contents);

	}

}

/** This Representation of a State for a Problem is quite redundant.
  * 'Parent' and 'Solution' are redundant to 'Contents'
  * as well as the 'Position' of the Space,
  * which can be derived from the Contents.
  * To save reconstructing the Solution we track the whole Path
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T20:45:01Z
  * digest: 7f4bf02c74c98246b83eb2a4629a5b6fd8df20065ef888015de2f00fffdbe651
  * stale: false
  * tags: [code/backtracking, code/algorithm]
  * concepts: [Backtracking Search]
  * facets: {layer: utility, status: broken, complexity: medium}
  * -->
  * to the current Solution.	 */
class GrammarState {

	/** Short mnemonic codes naming each recognized state (index-aligned with {@link #Productions}). */
	final static public String[] States = {
//		"C0 an ZK", "C0 an LS", "C0 an ber. LS", "LSC0", "D0", "E1", "E2", "E3", "E4", "E5", "E6 an LS", "E6 an ZK", "E7", "E8", "E>8", "", "", "", "", "", "", "", ""
		"C0", "D0", "E1", "E2", //"E3", "E4",
			"E5", "E6", //"E7",
			"E8", "E>8"
	};

	/** For each state in {@link #States}, the textual alternatives it can produce. */
	final static public String[][] Productions = {
		{"(keine �nderung)", "C0 reformatiert", "C0 Intersystemdublette", "E6 IntrasystemDublette", "E2 Pflichtfelder bei Neuanlage", //"E3 Pflichtfelder bei �nderung", "E4 Pflichtfelder bei �nderung",
			"E>8 QS Fehler", "(Akzeptiert, nicht berechtigt)", "C0 Felder reformatiert", "E8 Ablehnung der �nderung"},
		{"(L�schung durchgef�hrt)", "E5 unbekannte ZKDBNr","E7 Ablehung der L�schung"}, //D0
		{"Log, an Administrator"}, //E1
		{"(ignoriert)","C0 erg�nzte Daten"}, //E2
//		{"E3": "","","","","","","",}, //E3
//		{"E4": "","","","","","","",}, //E4
		{"(nur loggen)"}, //E5
		{"(ignorieren)","(loggen)","C0 Merge von IntrasystemDublette","D0 L�schung von IntrasystemDublette"}, //E6
//		{"E7": "","","","","","","",},
		{"(loggen, Clearing oder Reporting"}, //E8
		{"(loggen, Clearing oder Reporting"} //E>8
	};

	/** The Solution of the current State = all the last Moves
	  * with the last one at the end.	 */
	public String Remark;

	/** The Solution of the current State = all the last Moves
	  * with the last one at the end.	 */
	public Object Contents;

	/** Reference to the Parent State of this Result
	  * Used for tracking the Path from the Origin to this Result
	  */
	public GrammarState parent;

	/** Minimal Constructor, constructing from a Start String	 */
	public GrammarState(String Contents)	{
		this.Contents	= Contents;
		this.Remark		= Contents; //Remark doubles as the hash/equals key; see hashCode()
	}

	/** Derives a hash from the first two characters of {@link #Remark}.
	 * @return a HashCode for this Object
	  * restricted to 2 Characters */
	public int hashCode() {
		return Remark.charAt(0) + Remark.charAt(1); }

	/** Compares the first two characters of each object's {@link #Remark}.
	 * Tests if the Argument Object is equivalent to this one.
	  * Default Implementation tests for binary Equivalence in the first Character.	 */
	public boolean equals(Object arg) {
		GrammarState State = (GrammarState) arg;
//		System.out.println(	State.Contents);
		return  (State.Remark.charAt(0) == Remark.charAt(0)) &&
				(State.Remark.charAt(1) == Remark.charAt(1)); }
//		return State.Contents.equals(Contents); }	//cannot use ==, because Strings are not guaranteed to be unique!

	/**
	 * Returns this state's remark text.
	 */
	public String toString() { return Remark; }

}
