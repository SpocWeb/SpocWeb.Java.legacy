package math.algorithm;

/**
  * Calculates the optimum Solution for any Knapsack Problem
  * with Sizes less than 'Capacity' and only Integer Costs and Values.
  * As soon as the Capacity, the Costs or the Values are fractional
  * you cannot use this Algorithm, except if you can approximate Integers
  * by multiplying all Values by the same common Denominator.
  *
  * When you can choose continuous Amounts the Problem actually becomes trivial:
  * Just calculate the specific SpecVal[j] = Value[j]/Size[j]
  * and take as much as there is of the Stuff with maximum specific Value,
  * after this, fill it up with the next valuable Stuff.
  *
  * The Calculation of the Solution is O(Capacity*Types)
  * For large Capacities, the Problem can be considered as quasi continuous
  * if you need only an approximate Maximum.
  * For large Number of Types the Calculation becomes quite long.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:18Z
  * digest: 344ebeeda1c993232334c8c6732e3a0989c1da0f599306a7e2ed9bc84f85d04b
  * stale: false
  * tags: [code/knapsack_problem, code/dynamic_programming]
  * concepts: [Knapsack Problem Solver]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class KnapSack {

	/**Fill[j] is the maximum Filling that can be reached
	 * using only the first j of the given Types.
	 * You don't need to store the previous Results,
	 * so the 1-dim Array is sufficient.
	 */
	private int[] Fill;

	/** best[i] is the Item last added to reach this filling for i Items.	 */
	private int[] best;

	/**Local Cache for the Values,
	 * they are not protected from being changed,
	 * but at least not directly modifyable
	 */
	private int[] Value;

	/**Local Cache for the Sizes,
	 * they are not protected from being changed,
	 * but at least not directly modifyable
	 */
	private int[] Size;

	/**
	 * Initializing Constructor taking all Parameters:
	 * @param Capacity The total Capacity of the Sack
	 * @param Sizes    The Sizes  of the Items in the Sack
	 * @param Values   The Values of the Items in the Sack
	 */
	public KnapSack(int Capacity, int[] Sizes, int[] Values) {
		int N = Values.length;
		if (N != Sizes.length) throw new AbstractMethodError();
		Size = Sizes;
		Value = Values;
		best = new int[Capacity+1];
		Fill = new int[Capacity+1];
		int newVal;
		for (int j = 1; j < N; ++j)	//iterate through increasing Number of Types
			for (int i = 1; i <= Capacity; ++i)	//iterate through the different bag Sizes
				if (i >= Size[j])	//if the Size of the Bag fits for the new Type...
					if (Fill[i] < (newVal = Fill[i-Size[j]] + Value[j])) {	//and gives a better Solution for the new Type ...
						Fill[i] = newVal;	//choose it!
						best[i] = j;
					}	//this creates the
	}

	/** The Result can be calculated using 'best'
	  * for any chosen Capacity less than given in the Constructor.
	  * The last Item added is Item = best[Capacity].
	  * If you take this Item away, the Capacity reduces itself to
	  * CapNew = Capacity - Size[Item].
	  * For the next Item you choose Item = best[CapNew] and so on...
	  */
	public int[] getItems(int Capacity) {
		int Item;
		int j = -1;
		int[] Items = new int[Capacity];
		while (Capacity > 0) {
			Item  = Items[++j] = (Capacity > 0) ? best[Capacity] : 0;	//the next Element is determined by the reduced Capacity...
			Capacity -= Size[Item];	//the Size of the Bag reduces itself on choosing best
		}
		return Items; }

	/** The Result can be calculated using 'best'
	  * for any chosen Capacity less than before.
	  * The last Element added is best[Capacity].
	  * If you take this Item away, the Capacity reduces itself to
	  * CapNew = Capacity - Size[best[Capacity]].
	  * For the next Item you choose best[CapNew].
	  */
	public int getValue(int[] Items) {
		int Sum = 0;
		int Item, i = -1;
		do Sum += Value[Item = Items[++i]];
		while (Item > 0);
		return Sum; }

	/**The Results for the optimum Fillings doesn't need to be calculated using getValue(),
	 * because they are already in the Fill Array.
	 */
	public int[] getValues() {
		return Fill;}

////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

/** Tests all Methods of this Class	 */
public static void testIt(String[] args) { //throws java.io.IOException {
	System.out.println("Testing " + KnapSack.class.getName());
	//The first Items have to be Dummies!
	int[] Value = {0, 4, 5, 10, 11, 13};
	int[] Size  = {0, 3, 4, 7, 8, 9};
	KnapSack KS = new KnapSack(17, Size, Value);
	int[] Result = KS.getItems(17);
	System.out.println("KnapSack: " + KS.getValue(Result));
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * via the command line.	 */
public static void main (String[] args) { //throws java.io.IOException {
	testIt(args); }

}
