package synch;

import function.byref.ByRefLong;

/**Matrix Representation of a finite State Machine.
 * The States are not modeled directly as Objects,
 * but mapped to Integer Numbers 0..V-1.
 * The Value of the Coefficient a[i,j] represents the next State,
 * coming from an Input Element i and a State j.
 *
 * This Representation is apted best for full Graphs.
 * By Convention 0 is the End State for all Operations.
 * So the Machine is initialized to 0 for all Operations.
 *
 * The Publisher knows all it's Subscribers, but the Subscribers typically
 * also know the Publisher (e.g. to request more Information)
 * The Pipes only know their successors, which are defined at Construction
 * A versatile Element would allow Subscri
 *
 * Since the State Machine is driven by the step() Method,
 * it does not process on it's own but must be driven by an active Process
 * at the Source.
 * By publishing Events it can also indirectly drive a following Pipe,
 * although this can easily be done by the controlling Process.
 *
 * @see tester.Process.StateMachine for a Machine working with Objects.
 *
 */
public class StateMachine {

	/**Current State to be published...	 */
	protected ByRefLong EventState = new ByRefLong();

	/**Matrix containing the Transition Function.	 */
	protected int [][] a;

	/**Current State	 */
	protected int State = 0;

	/**For enabling Debug and Trace Info to System.out	 */
	public static boolean debug = false;

	/**Constructor sets the number of Vertices
	 * and allocates the Space for the Matrix
	 * The individual Connections have to be set later using setEdge()
	 * The Matrix is initialized by giving 0 the State for each following Operation.	 */
	public StateMachine(int numInputs, int numStates) {
		a	= new int	[numInputs][numStates];
		int i = numInputs; while(--i >= 0) { int[] I = a[i];	//initialize the whole Matrix, O(V^2)
		int j = numStates; while(--j >= 0) I[j] = 0; }
	}

	/**Constructor using the number of Vertices and an Adjacency List
	 * to initialize itself.	 */
/*	public StateMachine(int numInputs, int numStates, SparseMatrix AL) {
		this(numInputs, numStates);
		AdjListIterator It = (AdjListIterator) AL.Iterator();
		Node curr;
		ByRefLong available = new ByRefLong();
		do
			if ((curr = (Node) It.nextItem(available)) != null)
				setEdge(It.currentLine(), curr.v, false, curr.w);
		while (available.Value > 0);
	}

	/**Constructor sets the number of Vertices
	 * and allocates the Space for the Matrix generated from the Vectors.
	 * Also calculates all Distances. 	 */
	public StateMachine(int[][] States) {
		a = new int[States.length][];
		int i = States.length; //Copy two Levels deep.
		//Use clone() on the lowest Level instead of ArrayCopy(),
		//because that saves allocating the Memory
		while(--i >= 0) a[i] = (int[]) States[i].clone();	//copy the whole Matrix, O(I*Q)
	}

	/**Dynamically add/remove an Edge to/from the Graph.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge */
	public void setTransition(int Input, int Start, int End) {
		a[Input][Start] = End; }

	/**sets the current State
	 * This should normally not be done directly except on Initialization. 	 */
	public void setState(int newState) { State = newState; }

	/**returns the current State	 */
	public int getState() { return State; }

	/**Perform the next Step with the given Input.
	 * @return the current State
	 * The Mapping to the Output Value has to happen in a concatenated Mapping.
	 */
	public int step(int Input) { return State = a[Input][State]; }

	/**Returns a String Representation of this Object	 */
	public String toString() {
		StringBuffer S = new StringBuffer();
		int i = -1;
		while (++i < a.length) {
			int j = -1;
			while (++j < a.length)
				S.append(a[i][j]).append(','); //",\t");
			S.append('\n');
		}
		return S.toString(); }

	/**Tests all Methods of this Class	 */
	public static void testIt() {
		System.out.println("Testing StateMachine:");
		char[][] Edges ={{'A','G', (char) 4},
						 {'A','B', (char) 1},
						 {'C','A', (char) 1},
						 {'L','M', (char) 1},
						 {'J','M', (char) 2},
						 {'J','L', (char) 3},
						 {'J','K', (char) 1},
						 {'E','D', (char) 2},
						 {'D','F', (char) 1},
						 {'H','I', (char) 1},
						 {'F','E', (char) 2},
						 {'A','F', (char) 2},
						 {'G','E', (char) 1}};
		char[][] Edges2={{'G','C', (char) 1},	//neu für zweifachen Zusammenhang
						 {'H','G', (char) 3},
						 {'G','J', (char) 1},
						 {'L','G', (char) 5}};
		char[][] Edges5={{'I','H', (char) 1},	//neu für gerichtete Graphen
						 {'M','L', (char) 1}};
		char[][] Edges3={{'G','C', (char) 1},	//neu für kürzeste Wege und Spannbaum
						 {'A','C', (char) 1},	//Werte löschen einfach den schon gesetzten Weg!
						 {'A','G', (char) 6},		//Entfernung ändern!
						 {'H','I', (char) 2},		//Entfernung ändern!

						 {'B','D', (char) 2},		//hier kommen die neuen Verbingungen
						 {'B','E', (char) 4},		//für den ungerichteten gewichteten Graphen
						 {'B','C', (char) 1},
						 {'C','E', (char) 4},
						 {'E','L', (char) 4},
						 {'F','L', (char) 2},
						 {'I','K', (char) 1}};
		int i;
		StateMachine AM;
		AM = new StateMachine(13,13);	//for   directed Graphs
		i = -1; while (++i < Edges .length) AM.setTransition(	Edges [i][0]-'A',
																Edges [i][1]-'A', Edges[i][2]);
		i = -1; while (++i < Edges2.length) AM.setTransition(	Edges2[i][0]-'A',
																Edges2[i][1]-'A', Edges2[i][2]);
		i = -1; while (++i < Edges5.length) AM.setTransition(	Edges5[i][0]-'A',
																Edges5[i][1]-'A', Edges5[i][2]);
		System.out.println(AM);
		System.out.println(Edges3);
	}

}
