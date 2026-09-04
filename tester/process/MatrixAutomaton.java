package tester.process;

/** Matrix Representation of a discrete Automaton: a[x][q] -> q
  * The States Q are mapped to the Integer Numbers 0..q.
  * The Inputs X are mapped to the Integer Numbers 0..x.
  * The Value of the Coefficients a[x,q] represent the next State
  * They represent the State Change Function Lambda.
  *		The Output Function Beta can either be based on the State  (Moore Automaton)
  *		or the State and the Input Value (Mealy Automaton)
  *
  * The interesting Thing about Automatons is that they are reCoupled,
  * i.e. their current State is an Input to the next State.
  *
  * Most Operations are of Order O(|Q|^2),
  * so this Representation is apted best for full Automatons.
  *
  * Like with Graphs, Automatons can be represented by Matrices or Lists.	 */
public class MatrixAutomaton {

	/**Matrix containing the Cost of the Connections or the next State	 */
	protected int [][] a;

	/**the current State of the Automaton	 */
	protected int State;

	/**Constructor, sets the number of States
	 * and allocates the Space for the Matrix	 */
	public MatrixAutomaton(int numInput, int numState) {
		a = new int[numInput][numState];
	}

	/** Constructor, sets all Productions at once,
	  * by handing over all States in a Matrix.	 */
	public MatrixAutomaton(int[][] Lambda) {
		a = Lambda;
	}

	/**Dynamically change a Production (Input, Start) -> End
	 * of the Automaton.	 */
	public void setProduction(int Input, int Start, int  End) {
		a[Input][Start] = End;}

	/**Returns a String Representation of this Automaton	 */
	public String toString() {
		java.text.NumberFormat form = new java.text.DecimalFormat("+00;-00");
		StringBuffer S = new StringBuffer();
		int i = -1;
		while (++i < a.length) {
			int j = -1;
			while (++j < a.length)
				S.append(form.format(a[i][j])).append(',');//",\t");
			S.append('\n');
		}
		return S.toString(); }

	/** Single Step Operation of this Automaton.
	  * Performs only State Transition and no Calculation of the Output Function. 	 */
	public int Operation(int InPut) {
		return State = a[InPut][State]; }

	/**Tests all Methods of this Class	 */
	public static void testIt() {
		System.out.println("Testing AutomatonM:");
	}

}
