package streamIO.object.backTrack;

import math.matrix.MatrixFloat;
import math.vector.VectorFloat;
import math.vector.VectorInt;
import streamIO.Assert;
import streamIO.Log;
import streamIO.object.IPipe;
import streamIO.object.enumer.container.DeQueueArr;
import streamIO.object.enumer.container.Heap;
import sun.security.util.PathList;
import tester.ITester;
import function.AFunction;
import function.AOrderAble;

/** Generator AND ITester Class for solving the Travelling Salesman Problem
 * using either the BackTracker Class or simulated Annealing.
 * The Problem is to find the shortest Roundtrip 
 * through a given Number of Cities with n-dim Coordinates. 
 * This Problem is trivial only for 1-dim. 
 * Since this Problem is discrete, you cannot use Methods relying on
 * Continuity or even higher Forms of Smoothness. 
 * 
 * On the other Hand, as soon as the Dimensionality is high enough, 
 * Pseudo-Continuity sets in and
 * local Minima become more and more improbable. 
 * 
 * This Class can solve the same Problem in two Ways:
 * 1) by simulated Annealing 
 * 2) using a Priority Search 
 * 
 * All Arrays start indexing with 1; 0 is not being used.
 * This makes it a bit ineffective, but an Index Change is hard to do!
 * 
 * @see math.minimizer.AnnealingMinimizer which applies Annealing to continuous Functions
 * instead of discrete Configuration Spaces. 
 * 
 */
public class TravelProblem
extends AFunction
implements ITester {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	protected static Log L = new Log(TravelProblem.class);
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	protected static int counter;
	protected static final double eps = 1e-12;	//double: 1e-16

	/**Does a partial Path Reversal, used by anneal() (10.9)
	 * Reverses the Order of Cities for the Travelling Salesman Problem	 
	 * Fuehrt die eigentliche Pfadumkehr aus:
	 * n[1] und n[2] sind der Anfangs- und Endpunkt	 */
	final static public int[] REVERT_PATH_SEGMENT(final int[] order, final int length, final int[] n) {
		final int[] jorder = (int[]) order.clone();
		final int nn=(1+(n[2]-n[1]+length) % length)>>1;	//#zu tauschender Staedte geteilt durch 2
		for (int j=nn; --j>=0; ) {
			final int k = (n[1]+j       ) % length;	//Faengt an den Enden des Abschnittes an...
			final int l = (n[2]-j+length) % length;	//und tauscht sich bis zur Mitte durch
			final int itmp=jorder[k]; jorder[k]=jorder[l]; jorder[l]=itmp;
		}
		return jorder;
	}

	/** do a Path transposition, used by anneal() (10.9)
	 * Takes a Sub-Path between n[1] and n[2]...
	 * ...and moves it between n[3] and n[4].   
	 * n[5] and n[6] are the previous enclosing Positions. 
	 * Helper Routine to calculate Cost of Travelling Salesman Problem
	 * Fuehrt den eigentlichen Tansport eines Abschnittes aus:
	 */
	final static public int[] TRANSPOSE(final int[] order, final int length, final int[] n) {
		final int[] jorder = new int[order.length];
		final int m1= (n[2]-n[1]+length) % length;	//#Staedte zwischen n(1) und n(2)
		final int m2= (n[5]-n[4]+length) % length;	//#Staedte zwischen n(4) und n(5)
		final int m3= (n[3]-n[6]+length) % length;	//#Staedte zwischen n(3) und n(6)
		int nn=-1;
		for (int j=0; j<=m1; ++j) {	//Kopie des Abschnittes zwischen n(1) und n(2)
			jorder[++nn]=order[(j+n[1]) % length];
		}
		for (int j=0; j<=m2; ++j) {	//Kopie des Abschnittes zwischen n(4) und n(5)
			jorder[++nn]=order[(j+n[4]) % length];
		}
		for (int j=0; j<=m3; ++j) {	//Kopie des Abschnittes zwischen n(3) und n(6)
			jorder[++nn]=order[(j+n[6]) % length];
		}
		return jorder; 
	}
	
	/** cost of a Path Reversal, used by anneal() (10.9)
	 * Calculates Cost of Reversing for the Travelling Salesman Problem,
	 * Liefert die Kosten-Funktion fuer eine vorgeschlagene Pfad-Umkehr.
	 * n gibt in den ersten beiden Koordinaten die Nummern der ersten
	 * und der letzten Stadt des Abschnittes an, der umgekehrt werden soll.
	 * Ergebnis sind die Netto- Kosten der Umkehr,
	 * die hier aber nicht durchgefuehrt wird.	 */
	final static public double COST_OF_REVERSAL(final float[][] x
	, final int[] order, final int length, final int[] n) {
		final float[][] xx = new float[5][2];
		n[3]= (n[1]-1+length) % length; //City before n[1]
		n[4]= (n[2]+1       ) % length; //City after n[2]
		for (int j=1;j<=4;j++) {
			xx[j]=x[order[n[j]]]; }
		double de;
		de  = VectorFloat.DIST(xx[1],xx[4]);	//Kosten um die neuen beiden
		de += VectorFloat.DIST(xx[2],xx[3]);	//Verbindungen zu schaffen
		de -= VectorFloat.DIST(xx[1],xx[3]);	//und die beiden alten
		de -= VectorFloat.DIST(xx[2],xx[4]);	//Verbindungen zu lösen
		return de; }
	
	/** cost of a Path transposition, used by anneal() (10.9)
	 * Helper Routine to calculate Cost of Travelling Salesman Problem
	 * Berechnet die Nettokosten fuer eine Pfadverlegung.
	 * n enthaelt in den ersten drei Elementen den Anfang und das Ende
	 * des Abschnittes, sowie das Element, an dem dieser eingesetzt werden soll.
	 * Errechnet werden die Netto-Kosten,
	 * die Verlegung an sich wird aber nicht durchgefuehrt.		
	 */
	final static public double COST_OF_TRANSPOSITION(final float[][] x
	, final int[] order, final int length, final int[] n) {
		float[][] xx = new float[7][2];

		n[4]= (n[3]+1       ) % length;	//Stadt nach n (3)
		n[5]= (n[1]-1+length) % length;	//Stadt vor  n (1)
		n[6]= (n[2]+1       ) % length;	//Stadt nach n (2)
		for (int j=1;j<=6;j++) {	//Koordinaten der benoetigten sechs Staedte ermitteln
			xx[j]=x[order[n[j]]]; }	//Kosten fuer...
		double de;
		de  = VectorFloat.DIST(xx[1],xx[3]);	//...den Aufbau  von n(1),n(3)
		de += VectorFloat.DIST(xx[2],xx[4]);	//...den Aufbau  von n(2),n(4)
		de += VectorFloat.DIST(xx[5],xx[6]);	//...den Aufbau  von n(5),n(6)
		de -= VectorFloat.DIST(xx[2],xx[6]);	//...das Oeffnen von n(2),n(6)
		de -= VectorFloat.DIST(xx[1],xx[5]);	//...das Oeffnen von n(1),n(5)
		de -= VectorFloat.DIST(xx[3],xx[4]);	//...das Oeffnen von n(3),n(4)
		return de; }
	
	/**Metropolis algorithm, used by anneal() (10.9)
	 * Determines Acceptance, based on the Temperature for Annealing 
	 * The starting Temperature could be determined from the first de, 
	 * so that e.g. the initial estimated Rejection Rate would be 50%, i.e. 
	 * .5 = Math.exp(-de0/t0) <=> -Ln(.5) = de0/t0 <=> t0 = -de0/Ln(.5) = 1.44*de0
	 */
	public static boolean ACCEPT_DELTA_ENERGY(final double de, final float t) {
		return (de < 0) || (Math.random() < Math.exp(-de/t));}
	
	/** 
	 * traveling salesman problem by simulated annealing (10.9) 
	 * Actually, annealing is not really necessary; 
	 * for high-dimensional Problems, local Minima become extremely rare, 
	 * so you can also use a simple 'high Watermark' Algorithm, 
	 * with which you force the System downhill 
	 * by not accepting any Energy Level above the Watermark. 
	 * 
	 * @param x
	 * @param order
	 * @param length
	 * @param path the starting Path Length, the value does actually not matter, 
	 * only the Changes, but for comparing different Results 
	 * the same Starting Value should be used. 
	 * When using the Criterion path >= 0 as a Health Check, 
	 * the Path should be the actual.  
	 */
	public static double ANNEAL(final float[][] x, int[] order, final int length, double path) {
		boolean transpose = false;
		int N = order.length;
		int nover=100*N;
		int nlimit=10*N;
		float t=0.5f;	//(initial) Temperature
		final int[] n = new int[7];	//using 1..6
		for (int j=1; j<=100; j++) { //for cooling the System 
			int numAccepted = 0;
			for (int k=1; k<=nover; k++) { //vary the Path
				++counter;
				int numOuter; 
				do {	//choose two random Cities
					n[1]=(int) ( N   *Math.random());	//Segment-Anfang und
					n[2]=(int) ((N-1)*Math.random());	//Segment-Ende bestimmen
					if (n[2] >= n[1]) {
						++n[2]; } 	//
					numOuter = (n[1]-n[2]+N) % N;
				} while (numOuter < 3);	//#Staedte ausserhalb kleiner als 3 => nochmal
				final double de;
				if (transpose = !transpose) //randomly switch between Reversion and Transport
				{	//choose another City for Transposition
					n[3]=(n[2]+1+(int) (Math.abs(numOuter-2)*Math.random())) % N;
					de = COST_OF_TRANSPOSITION(x,order,N,n); 
					if (ACCEPT_DELTA_ENERGY(de,t)) {
						++numAccepted; path += de; order = TRANSPOSE(order,N,n);
						//Assert.EQUALS(path, Cost(x, order));
					}
				} else {
					de = COST_OF_REVERSAL(x,order,N,n); 
					if (ACCEPT_DELTA_ENERGY(de,t)) {
						++numAccepted; path += de; order = REVERT_PATH_SEGMENT(order,N,n);
						//Assert.EQUALS(path, Cost(x, order));
					}
				}
				if (numAccepted >= nlimit) {
					break; } 
			}
			final double pathLength = MatrixFloat.PATH_LENGTH(x, true, order);
			L.n("T =").l(t).l("Path Length =").l(path).l("Recalc:").l(pathLength);
			L.l("Successful Moves: ").l(numAccepted).l("overall Moves: ").l(counter);
			Assert.EQUALS(pathLength, path);
			t *= 0.9f;
			if (numAccepted == 0) break;
		}
		L.n("*** System Frozen ***");
		L.n("Final path:");
		L.n("city").l("x").l("y");
		for (int i = x.length; --i > 0; ) {
			int ii = order[i];
			L.n().l(ii).l(x[ii]);
		}
		return path;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////

	/**Cache for the Size of the TravelProblem	 */
	final int numCities;

	/** Coordinates of the Cities	 */	
	final float[][] x;
	
	/**Sequence of the Cities	 */	
	int[] order;

	/**Start Path for the identical Permutation	 */
	protected double path;
	
	/**Initializing Constructor taking the Size of the TravelProblem
	 * creating an initial Identity Permutation and it's Path	 */
	public TravelProblem(final float[][] x_) { this(x_, x_.length); }
	
	/**Initializing Constructor taking the Size of the TravelProblem
	 * creating an initial Identity Permutation and it's Path	 */
	public TravelProblem(final float[][] x_, final int numCities_) {
		this.x = x_; 
		this.numCities = numCities_;
		order = new int[numCities_];
		int i = numCities_;
		while (--i > 0) {
			order[i] = i; }//start with an identical Permutation
		path = MatrixFloat.PATH_LENGTH(x, true, order);
		//add an optional Toll (also has to be removed when Connections are removed!)
		//if ((x[i1][0]>TravelProblem.border) != (x[i2][0]>TravelProblem.border)) {
		//	path += TravelProblem.toll; } 
		optimum = new TravelState(order, path);
	}
	
	/** Size of the Toll when crossing the Border.	 */
	static final double toll = 0;
	
	/** Position of the vertical Border	 */
	static final double border = 0.5;
	
	/**Current Optimum	 */
	protected TravelState optimum;
	
	/**Performs the Test for a complete Solution	 */
	public boolean test(Object arg) {
		final TravelState newState = (TravelState) arg;
		final boolean madeProgress = (optimum.cost - newState.cost) > optimum.cost*eps; //if (State.Cost < Optimum.Cost)
		if (madeProgress ) { //considerable Progress
			optimum = newState;
			L.n("Current Optimum: ").l(optimum.cost).l(MatrixFloat.PATH_LENGTH(x, true, optimum.sequence)).l("overall Moves: ").l(counter);
		}
		return madeProgress; 
	}	//State.Contents.equals(Solution);
	
	/**
	 * Returns new possible Solutions generated from the old one.
	 * You have to avoid cycles in general, but that can only be done
	 * by tracking trough all the Solutions already considered
	 * e.g. using their HashCode in a HashTable. 
	 * @param a previous Solution to start from. 
	 * @return new possible Solutions generated from the given old one.
	 */
	public Object Map(final Object arg) {
		return Map((TravelState) arg); 
	}	//
	
	/**
	 * Returns new possible Solutions generated from the old one.
	 * You have to avoid cycles in general, but that can only be done
	 * by tracking trough all the Solutions already considered
	 * e.g. using their HashCode in a HashTable. 
	 * @param a previous Solution to start from. 
	 * @return new possible Solutions generated from the given old one.
	 */
	public TravelState[] Map(final TravelState parent) {
		int nn, i = 10;
		int[] n = new int[7];
		boolean idec = false;
		final TravelState[] children = new TravelState[i];	//at most 10 Children, maybe less, indicated by null!
		while (--i >= 0) {	//5 Solutions using Transposal
			++counter;
			do {	//choose two random Cities
				n[1]= (int) ( numCities   *Math.random());
				n[2]= (int) ((numCities-1)*Math.random());
				if (n[2] >= n[1]) ++n[2];
				nn = (n[1]-n[2]+numCities-1) % numCities;
			} while (nn<3);
			double de;	//Faster to calculate the Difference than the whole Path
			if (idec = !idec) //randomly switch between Reversion and Transport
			{	//choose another City
				n[3]=(n[2]+1+(int) (Math.abs(nn-2)*Math.random())) % numCities;
				//n[3]=(n[3]-1) % numCities;
				de = COST_OF_TRANSPOSITION(x,parent.sequence,numCities,n);
				order = TRANSPOSE(parent.sequence,numCities,n);
			} else {
				de = COST_OF_REVERSAL(x,parent.sequence,numCities,n);
				order = REVERT_PATH_SEGMENT(parent.sequence,numCities,n);
			}
			children[i] = new TravelState(order, parent.cost + de);
			final double pathLength = MatrixFloat.PATH_LENGTH(x, true, order);
			Assert.EQUALS(pathLength, parent.cost + de);
//			L.n(Children[i].Cost + "\t" + Cost (Children[i].Sequence, x, y, N));
		}
		return children; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Testing and main Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	final static public float[][] testCities = {
	   {0.37874702f, 0.26794747f}, 
	   {0.26471500f, 0.39708003f}, 
	   {0.32754510f, 0.25209540f}, 
	   {0.05736414f, 0.39974090f}, 
	   {0.08760463f, 0.44607142f}, 
	   {0.12023141f, 0.93506030f}, 
	   {0.58845250f, 0.69867194f}, 
	   {0.60636870f, 0.15311596f}, 
	   {0.35213616f, 0.33958760f}, 
	   {0.73950700f, 0.15285437f}
	};

	
	static void testReversion() {
		final int[] order = VectorInt.IDENTITY(testCities.length); 
		final double cost = MatrixFloat.PATH_LENGTH(testCities, true, order);
		final int[] rev = {0, 3, 7, 8, 9, 2, 8}; 
		final double dCost = COST_OF_REVERSAL(testCities, order, order.length, rev); 
		final int[] newOrder = REVERT_PATH_SEGMENT(order, order.length, rev);
		final double newCost = cost+dCost; 
		Assert.EQUALS(newCost, MatrixFloat.PATH_LENGTH(testCities, true, newOrder)); 
	}
	
	static void testTransfer() {
		final int[] order = VectorInt.IDENTITY(testCities.length); 
		final double cost = MatrixFloat.PATH_LENGTH(testCities, true, order);
		final int[] trans = {0, 3, 7, 8, 9, 2, 8}; 
		final double dCost = COST_OF_TRANSPOSITION(testCities, order, order.length, trans); 
		final int[] newOrder = TRANSPOSE(order, order.length, trans);
		final double newCost = MatrixFloat.PATH_LENGTH(testCities, true, newOrder); 
		Assert.EQUALS(newCost, cost+dCost); 
	}
	
	/**Tests all Methods of this Class	 */
	public static void testIt() {
		L.n ("Testing TravelProblem:");
		testTransfer(); 
		testReversion(); 
		IPipe store;	//avoid reusing existing Elements
		TravelProblem Problem = new TravelProblem(testCities);
		L.n("Initial Path Length: ").l(Problem.path);
		double minPath = ANNEAL(Problem.x, Problem.order, testCities.length, Problem.path);
		//optimum Solution: 2.4808136420336053
		BackTracker tracker;
		counter = 0;
		
		//the Priority Queue takes 20 Tries (Priority-first)
		store = new Heap(100, false);
		store.addItem(new TravelState(Problem.order, Problem.path));
		tracker = new BackTracker(store, null, Problem, Problem, true);	//avoiding Duplicates
		for(int i= 20; --i >= 0;){
			Object nextItem = tracker.nextItem();
			TravelState state = (TravelState) nextItem;
			L.n(state.cost);
			if (state.cost <= minPath) {
				break; 
			}
		} 
		//Queue does a blind Search (Breadth-first, LIFO)
		Problem = new TravelProblem(testCities);
		store = new DeQueueArr(100, IPipe.ORDER_QUEUE);
		store.addItem(new TravelState(Problem.order, Problem.path));
		tracker = new BackTracker(store, null, Problem, Problem, true);	//avoiding Duplicates
		L.n (tracker.nextItem());
		
		//Stack does a blind Search (Depth-first, FIFO)
		store = new DeQueueArr(100, IPipe.ORDER_STACK);
		store.addItem(new TravelState(Problem.order, Problem.path));
		tracker = new BackTracker(store, null, null, Problem, true);	//avoiding Duplicates
		L.n (tracker.nextItem());
		
		//Queue also takes 20 Tries (Breadth-first, LIFO)
		store = new DeQueueArr(100, IPipe.ORDER_QUEUE);
		store.addItem(new TravelState(Problem.order, Problem.path));
		tracker = new BackTracker(store, null, null, Problem, false);	//not avoiding Duplicates
		L.n (tracker.nextItem());
		
		//Stack also takes 50 Tries (Depth-first, FIFO)
		store = new DeQueueArr(100, IPipe.ORDER_STACK);
		store.addItem(new TravelState(Problem.order, Problem.path));
		tracker = new BackTracker(store, null, null, Problem, false);	//not avoiding Duplicates
		L.n (tracker.nextItem());
		
	}
	
	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}

/**This Representation of a State for the Travel Problem is quite redundant.
 * 'Parent' and 'Solution' are redundant to 'Contents'
 * as well as the 'Position' of the Space,
 * which can be derived from the Contents.
 * To save reconstructing the Solution we track the whole Path
 * to the current Solution.	 */
class TravelState
extends AOrderAble { //OrderAble, to compare the Quality of Solutions

	/**The current State of the TravelProblem	 */
	protected int[] sequence;

	/**Cost of this State, redundant to the Contents.	 */
	protected double cost;

	/**less: '<' Returns True, when 'Self' < arg
	 * The Threshold must be negative.
	 * If it reaches 0 you have a classical greedy Algorithm.
	 * Of course it should scale with the Size of the Cost.
	 */
	public boolean isLessThan (Object arg) {
		return  cost > ((TravelState) arg).cost;
//		return (Cost - ((TravelState) arg).Cost) > Threshold;
	}

	/**returning fewer 'true' in both less and grtr
	 * makes the Heap work more and more like a Stack.
	 */

	/**Initializing Constructor	 */
	public TravelState(int[] sequence_, double cost_) {
		super(null); self = this; //workaround for AOrderAble
		sequence = sequence_;
		cost = cost_;
	}

	/**Constructor used on creating new Solutions.
	 * Constructs a new TravelState from the Parent
	 * with the Item at SwapRel changed. 	 */
/*	public TravelState(TravelState Parent_, int SwapType)
	{
	}
*/
	/**Tests if the Argument Object is equivalent to this one.
	 * Default Implementation tests for binary Equivalence.	 */
	public boolean equals(Object arg) {
		TravelState TravelState = (TravelState) arg;
		return sequence.equals(TravelState.sequence);
	}

}
