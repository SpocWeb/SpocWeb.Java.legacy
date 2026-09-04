package streamIO.object.backTrack;

import streamIO.object.IPipe;
import streamIO.object.enumer.container.DeQueueArr;
import tester.ITester;
import function.AFunction;
import function.byref.ByRefString;

/**Generator and ITester Class for the Achter Problem.
 * Could also be split up into two Classes.
 * This saves handing over the Solution to each Item.
 * On the other Hand the Solution must be known to the Generator for it
 * to generate good Candidates.	 */
public class AchterProblem
extends AFunction
implements ITester {

	/**Cache for the Size of the AchterProblem	 */
	protected int N;

	/**Cache for the square Size of the AchterProblem	 */
	protected int N2;

	/**Solution searched for...	 */
	protected String Solution;

	/**Initializing Constructor taking the Size of the AchterProblem
	 * and the Solution	 */
	public AchterProblem(int N, String Solution) {
		this.N = N;
		this.N2 = N*N;
		this.Solution = Solution;
	}

	/**Performs the Test for a complete Solution	 */
	public boolean test(Object arg) {
		AchterState State = (AchterState) arg;
		System.out.println(	State.Contents);
		return				State.Contents.equals(Solution); }
//		return AchterState.Contents == Solution; }	//cannot use ==, because Strings are not guaranteed to be unique!

	public static char chrUp	= '^';
	public static char chrDown	= 'v';
	public static char chrLeft	= '<';
	public static char chrRight	= '>';
	public static char chrSpace = '_';

	/**Returns new possible Solutions generated from the old one.
	 * The Way back to the old one is not taken.
	 * This avoids at least 1/4 of the new Solutions.
	 * Of course you also have to avoid cycles in general,
	 * but that can only be done on tracking trough all the Solutions already considered
	 * e.g. using their HashCode in a HashTable.	 */
	public Object Map(Object arg) {
		int i = -1;
		AchterState Parent = (AchterState) arg;
		char Solution = chrSpace;
		AchterState[] Child = new AchterState[3];	//at most 3 Children, maybe less, indicated by null!
		if (Parent.Solution != "")
			Solution = Parent.Solution.charAt(Parent.Solution.length()-1);
		if ((Solution != chrUp		) && (Parent.Position		>  N  ))Child[++i] = new AchterState(Parent, -N, chrDown);
		if ((Solution != chrDown	) && (Parent.Position + N	<  N2 ))Child[++i] = new AchterState(Parent, +N, chrUp);
		if ((Solution != chrRight	) && (Parent.Position % N	!= 0  ))Child[++i] = new AchterState(Parent, -1, chrLeft);
		if ((Solution != chrLeft	) && (Parent.Position % N	!= N-1))Child[++i] = new AchterState(Parent, +1, chrRight);
		return Child; }

	/**Tests all Methods of this Class	 */
	public static void testIt()	{
		System.out.println ("Testing AchterState:");
		String StartPosition = "_12345678";
		String Stop_Position = "14237568_";
		IPipe Store;	//avoid reusing existing Elements
		//Queue with avoiding Duplicates, takes 20 Tries (Breadth-first, LIFO)
		Store = new DeQueueArr(100, IPipe.ORDER_STACK);
		Store.addItem(new AchterState(StartPosition));
		AchterProblem Problem = new AchterProblem(3, Stop_Position);
		BackTracker BT; 
		BT = new BackTracker(Store, null, Problem, Problem, true);
		System.out.println (((AchterState)BT.nextItem()).Solution);

		//Stack with avoiding Duplicates, takes 50 Tries (Depth-first, FIFO)
		Store = new DeQueueArr(100, IPipe.ORDER_QUEUE);
		Store.addItem(new AchterState(StartPosition));
		BT = new BackTracker(	Store, null, Problem, Problem, true);
		System.out.println (((AchterState)BT.nextItem()).Solution);

		//Queue without avoiding Duplicates, also takes 20 Tries (Breadth-first, LIFO)
		Store = new DeQueueArr(100, IPipe.ORDER_QUEUE);
		Store.addItem(new AchterState(StartPosition));
		BT = new BackTracker(	Store, null, Problem, Problem, false);
		System.out.println (((AchterState)BT.nextItem()).Solution);

		//Stack without avoiding Duplicates, also takes 50 Tries (Depth-first, FIFO)
		Store = new DeQueueArr(100, IPipe.ORDER_STACK);
		Store.addItem(new AchterState(StartPosition));
		BT = new BackTracker(	Store, null, Problem, Problem, false);
		System.out.println (((AchterState)BT.nextItem()).Solution);

		//Test the linear List, which acts like a Queue,
		//should work like a DeQueue(false)
/*		Store = new List.List();
		Store.addItem("abcde");
		BackTracker.BackTracker(Store, new finish(), new Generator(), false);
		Store.clear();
*/
	}
	
	public static void main(String[] args) {
		testIt(); 
	}

}

/**This Representation of a State for the Achter Problem is quite redundant.
 * 'Parent' and 'Solution' are redundant to 'Contents'
 * as well as the 'Position' of the Space,
 * which can be derived from the Contents.
 * To save reconstructing the Solution we track the whole Path
 * to the current Solution.	 */
class AchterState {
	/**The current State of the AchterProblem	 */
	protected String Contents;

	/**The Parent was formerly used to reconstruct the Solution	 */
	protected AchterState Parent;

	/**The Solution of the current State = all the last Moves
	 * with the last one at the end.	 */
	protected String Solution;

	/**Position of the Space, indicated by an Underscore '_'
	 * Redundant to the Contents.	 */
	protected int Position;

	/**Minimal Constructor, constructing from a Start String	 */
	public AchterState(String Contents)	{
//		this.Parent		= null;
		this.Solution	= "";
		this.Contents	= Contents;
		this.Position	= Contents.indexOf(AchterProblem.chrSpace);
	}

	/**Initializing Constructor	 */
	public AchterState(String Contents, AchterState Parent, String Solution, int Position) {
		this.Contents	= Contents;
		this.Parent		= Parent;
		this.Solution	= Solution;
		this.Position	= Position;
	}

	/**Constructor used on creating new Solutions.
	 * Constructs a new AchterState from the Parent
	 * with the Item at SwapRel changed. 	 */
	public AchterState(AchterState Parent_, int SwapRel, char Move)	{
		Parent	= Parent_;
		Position	= Parent.Position + SwapRel;	//Position to swap with.
		Contents= ByRefString.swapChar(Parent.Contents, Parent.Position, Position);
		Solution= Parent.Solution + Move;	//append the last Move
/*		if ( einzig ) {
			Ausgabe (Hilf);
			INC (belegt);
			NEW (Nachfolger);
			Suchfeld [belegt] = Nachfolger;
			Nachfolger.Contents = Hilf;
			Nachfolger.Parentn = Parent;
			Nachfolger.Herkunft = Herkunft;
			Nachfolger.Position = Parent.Position+Tausch;
		}
*/	}

	/**Tests if the Argument Object is equivalent to this one.
	 * Default Implementation tests for binary Equivalence.	 */
	public boolean equals(Object arg) {
		AchterState AchterState = (AchterState) arg;
		return Contents.equals(AchterState.Contents); }

}
