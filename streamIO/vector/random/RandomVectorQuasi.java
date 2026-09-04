package streamIO.vector.random;

import streamIO.Assert;
import streamIO.IReSetAble;
import streamIO.Log;
import streamIO.integer.random.ARandomInt;

/** Generates a Sub-Random Sequence that equally fills up
  * any given Space Seed up to MaxDim Dimensions using a binary Distribution. 
  * Due to it's even Distribution, Evaluation can be stopped any Time 
  * with an average Error of only 1/N. 
  * (Sobol's quasi-random sequence 7.7)
  * This is most effective and can be very well implemented in Assembler too.
  *
  * The Vectors generated here can very well be used for Monte Carlo Integration.
  * In Advantage over random Numbers where the relative Error falls like 1/SqRt(N),
  * the Error falls like (ln(N)^N)/N, which is nearly like 1/N for SMOOTH Functions
  * For discontinuous Functions or hard Borders the Error is proportional
  * to the Number of Points on the Border, i.e. in d Dimensions: N^((d-1)/d)
  * The Variance of this is the Square Root and the resulting relative Error
  * is this divided by N: relErr = N^((d-1)/2d - 1) = 1/N^((d+1)/2d)
  * e.g. relErr ~ 1/N^(2/3) with d = 3
  * the Advantage of Sub-Random Sequences gets lost for hard Borders:
  * with N => Infinity the relative Error approximates 1/N^0.5
  *
  * Speed: fast(2)
  * @see streamIO.vector.random.RandomVectorPseudoSequential cannot be stopped at any Time 
  * @see streamIO.vector.random.RandomVectorPseudo is not quite evenly distributed 
  * @see streamIO.integer.random.RandomPseudoBinary generates the same 1-Dim binary Halton Sequence. 
  * @see streamIO.integer.random.RandomPseudoGAdic generates a 1-Dim g-Adic Halton Sequence. 
  */
public class RandomVectorQuasi
extends ARandomInt {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(RandomVectorQuasi.class, 1);
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the static 1-dim Random Number Generator, shared between Clients	 */ 
	final static public RandomVectorQuasi RANDOM = new RandomVectorQuasi(); 
	
	/**Random double Precision Number from the static Random Number Generator	 */
	final static public double NEXT_DOUBLE() { return RANDOM.nextDouble(); }
	
	/**Random double Precision Number from the static Random Number Generator	 */
	final static public double NEXT_DOUBLE(final double MaxDouble) { return RANDOM.nextDouble(MaxDouble); }
	
	/**Random double Precision Number from the static Random Number Generator	 */
	final static public float NEXT_FLOAT() { return RANDOM.nextFloat(); }
	
	/**Random double Precision Number from the static Random Number Generator	 */
	final static public float NEXT_FLOAT(final float MaxFloat) { return RANDOM.nextFloat(MaxFloat); }
	
	/** Random integer Number from the static Random Number Generator	 */
	final static public int NEXT_INT() { return RANDOM.nextInt(); }
	
	/** Random integer Number from the static Random Number Generator	 */
	final static public int NEXT_INT(final int MaxInt) { return RANDOM.nextInt(MaxInt); }
	
	/** Random long Number from the static Random Number Generator	 */
	final static public long NEXT_LONG() { return RANDOM.nextLong(); }
	
	/** Random long Number from the static Random Number Generator	 */
	final static public long NEXT_LONG(final long MaxLong) { return RANDOM.nextLong(MaxLong); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Maximum Dimension to fill out with RandomQuasi Vectors	 */
	final static public int MAX_DIM =  6;
	
	/** Maximum Number of Bits in the random Vector	 */
	final static public int MAX_BIT = 30;
	
	/** Maximum Value of the Generator, used for Norming	 */
	final static public int MAX_VALUE = 1 << MAX_BIT;
	
	/** Norming Factor of the Generator, pre-calculated	 */
	final static public float NORM = 1f / MAX_VALUE;

	/** Initial Value of the Generator	 */
	final static public int DEFAULT_SEED = 0;
	
	/** Result List of Integers from the Recursion	 */
	protected static final int[][] iu = new int [MAX_BIT][MAX_DIM];
	
	/** List of the Primitive Polynomials with their Degrees[0] and
	  * encoded as decimal Numbers[1] with the Bits set
	  * except for the highest and lowest Bit	 */
	protected static final int[][] PP; /* ={{1,0},
										{2,1},
										{3,1},{3,2},
										{4,1},{4,4},
										{5,2},{5,4},{5,7},{5,11},{5,13},{5,14},
										{6,1},{6,13},{6,16},{6,19},{6,22},{6,25},
										{7,1},{7,4},{7,7},{7,8},{7,14},{7,19},{7,21},{7,28},{7,31},{7,32},{7,37},{7,41},{7,42},{7,50},{7,55},{7,56},{7,59},{7,62},
										{8,14},{8,21},{8,22},{8,38},{8,47},{8,49},{8,50},{8,52},{8,56},{8,67},{8,70},{8,84},{8,97},{8,103},{8,115},{8,122},
										{9,8},{9,13},{9,16},{9,22},{9,25},{9,44},{9,47},{9,52},{9,55},{9,59},{9,62},{9,67},{9,74},{9,81},{9,82},{9,87},{9,91},{9,94},{9,103},{9,104},{9,109},{9,122},{9,124},{9,137},{9,138},{9,143},{9,145},{9,152},{9,157},{9,167},{9,173},{9,176},{9,181},{9,182},{9,185},{9,191},{9,194},{9,199},{9,218},{9,220},{9,227},{9,229},{9,230},{9,234},{9,236},{9,241},{9,244},{9,253},
										{10,4},{10,13},{10,19},{10,22},{10,50},{10,55},{10,64},{10,69},{10,98},{10,107},{10,115},{10,121},{10,127},{10,134},{10,140},{10,145},{10,152},{10,158},{10,161},{10,171},{10,181},{10,194},{10,199},{10,203},{10,208},{10,227},{10,242},{10,251},{10,253},{10,265},{10,266},{10,274},{10,283},{10,289},{10,295},{10,301},{10,316},{10,319},{10,324},{10,346},{10,352},{10,361},{10,367},{10,382},{10,395},{10,398},{10,400},{10,412},{10,419},{10,422},{10,426},{10,428},{10,433},{10,446},{10,454},{10,457},{10,472},{10,493},{10,505},{10,508}}; */ 
	
	/** List of the Primitive Polynomials by their Degree 
	  * encoded as decimal Numbers with the Bits set for the Coefficients
	  * except for the highest and lowest Bit (always 1)	 */
	protected static final int[][] PRIMITIVE_POLYNOMIALS ={
			{}, //0
			{0}, //x+1
			{1}, //x²+x+1
			{1,2}, //x³+x+1 and x²+x²+1
			{1,4}, //4,4
			{2,4,7,11,13,14},
			{1,13,16,19,22,25},
			{1,4,7,8,14,19,21,28,31,32,37,41,42,50,55,56,59,62},
			{14,21,22,38,47,49,50,52,56,67,70,84,97,103,115,122},
			{8,13,16,22,25,44,47,52,55,59,62,67,74,81,82,87,91,94,103,104,109,122,124,137,138,143,145,152,157,167,173,176,181,182,185,191,194,199,218,220,227,229,230,234,236,241,244,253},
			{4,13,19,22,50,55,64,69,98,107,115,121,127,134,140,145,152,158,161,171,181,194,199,203,208,227,242,251,253,265,266,274,283,289,295,301,316,319,324,346,352,361,367,382,395,398,400,412,419,422,426,428,433,446,454,457,472,493,505,508}
			};

	/** Initial Values for the following Recursion:
	  * M[i] = (2*a[1]M[i-1])^(2*2*a[2]M[i-2])^...^(2*...*2*a[i-q]M[i-q])
	  * Starting Values can be arbitrary odd integers less than
	  * The Rest of the Array is initialized in the Constructor	 */
	protected static final int[][] IV ={{ 1, 1, 1, 1, 1, 1},
										{ 3, 1, 3, 3, 1, 1},
										{ 5, 7, 7, 3, 3, 5},
										{15,11, 5,15,13, 9}};

	/** Static Initializer	 */
	static {
		int len = 0; 
		for(int i = PRIMITIVE_POLYNOMIALS.length; --i >= 0;)
			len+=PRIMITIVE_POLYNOMIALS[i].length;
		PP = new int[len][];
		//Assert.EQUALS(PP.length, len); 
		for(int i = PRIMITIVE_POLYNOMIALS.length; --i >= 0;) {
			final int[] currPoly = PRIMITIVE_POLYNOMIALS[i]; 
			for(int j = currPoly.length; --j >= 0;) {
				//Assert.EQUALS(PP[--len][0],i); 
				//Assert.EQUALS(PP[  len][1],currPoly[j]); 
				PP[--len]= new int[] {i, currPoly[j]};
			}
		}
		for(int j = IV.length; --j >= 0;) {
			iu[j] = IV[j]; } 
		for(int k = 0; k < MAX_DIM; k++) {	
			for(int j = 0; j < PP[k][0]; j++) //stored Values... 
				iu[j][k] <<= (MAX_BIT-j-1);  //...only require Normalization
			for(int j = PP[k][0]; j < MAX_BIT; j++) { //Use the Recurrence...
				int ipp = PP[k][1]; //...to calculate the Rest
				int i = iu[j-PP[k][0]][k];
				i ^= (i >> PP[k][0]);
				for (int l = PP[k][0]-1; l >= 1; l--) {
					if ((ipp & 1) != 0) { 
						i ^= iu[j-l][k]; } 
					ipp >>= 1;
				}
				iu[j][k]=i;
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Local Storage for the current State of the Generator	 */
	protected int value; // = 0;	//not necessary in Java
	
	/** Local State of the Vector, in which the Bits are reversed	 */
	protected final int[] ix;
	
	/** Copy of the Local State of the Vector to protect the State	 */
	final public int[] xi;
	
	/** Vector to be returned by RandomFloat	 */
	final public float[] x;
	
	/** Initializing Constructor for a 1-dim. Result	 */
	public RandomVectorQuasi(){ this(1); }
	
	/** Initializing Constructor for a Result Vector of Length dim
	 * 
	 * @param dim the Number of Dimensions of the resulting Vector
	 */
	public RandomVectorQuasi(final int dim) {
		super(MAX_VALUE);
		if (dim > MAX_DIM) {
			throw new AbstractMethodError("Maximum Dimension exceeded in RandomQuasi"); }
		 x = new float[dim];
		ix = new int  [dim];
		xi = new int  [dim];
//		int k = n; while (--k >= 0) ix[k]=0;}	//not necessary in Java
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { //throws IOException {
		reSet(DEFAULT_SEED); return this;  
	}
	
	/**New Semantic: instead of returning to the indicated Position, 
	 * this Method sets the Seed Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public long reSet(final long _seed) { //throws IOException {
		return this.value = (int) _seed; 
	}
	
	/**Generates a sub-random Integer.	 */
	protected long nextLongInternal() { return randomInt()[0]; }
	
	/**Random Integer Number from 0 to MaxInt-1	 */
	public int nextInt(final int MaxInt) {
		return	(int) (((long)nextInt()* MaxInt)/MAX_VALUE); }
	
	/**Generates a sub-random Integer Vector.	 */
	public int[] randomInt() {
		int im = value++;	//The Seed must be incremented on each Call.
		int j;
		for (j = 0; j < MAX_BIT; j++) { //Search for the rightmost nonzero Bit
			if ((im & 1) == 0)  
				break; 
			im >>= 1; }
		if (j > MAX_BIT) {
			throw new AbstractMethodError("MaxBit too small in RandomQuasi"); } 
		final int[] v = iu[j]; //Select the 
		for (int k = x.length; --k >= 0;) {
			xi[k] = ix[k] ^= v[k]; }	//XOR the found Bit in each Dimension
		return ix;
	}
	
	/**Generates a sub-random float Vector.
	 * The Seed must be incremented on each Call.	 */
	public float[] randomFloat() {
		randomInt();
		for(int k = x.length; --k >= 0;) {
			x[k] = xi[k] * NORM; } 
		return x; }
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Tests all Methods of this Class	 */
	public static void testRandomQuasi1D() {
		L.n("Testing RandomQuasi in 1 Dimension:");
		L.n("It displays graphically the Frequency of filling the Bins in [0,1]");
		L.n("The Filling of the bins should always be nearly balanced!");
		final int N = 64;
		RandomVectorQuasi SR = new RandomVectorQuasi();
		StringBuffer SB = new StringBuffer(N); SB.setLength(N);
		for (int i = N; --i >= 0; ) {
			SB.setCharAt(i, '0'); } 
		for (int j = N; --j > 0; ) {
			final int i = SR.nextInt(N);
			SB.setCharAt(i, (char) (SB.charAt(i) + 1)); //increment the Counters
			L.n(SB);
		}
		for (int j = N; --j > 0; ) 
			Assert.EQUALS(1, SB.charAt(j)-'0');
	}

	/**Tests all Methods of this Class	 */
	public static void testRandomQuasi2D() {
		L.n("Testing RandomQuasi in 2 Dimensions:");
		final int maxNum = 3; 
		final int N = 64;
		final int[][] sb = new int[N][N]; 
		final RandomVectorQuasi ran = new RandomVectorQuasi(2);
		int x=0,y=0;
		for (int i = maxNum; --i >= 0; ) {
			for (int k = N; --k >= 0; ) {
				for (int j = N; --j >= 0; ) {
					final float[] rnd = ran.randomFloat();
					rnd[0]*=N; x = (int)rnd[0]; //rounding doesn't work!
					rnd[1]*=N; y = (int)rnd[1]; //truncating!
					++sb[x][y];
				}
			}
		}
		--sb[x][y];	
		++sb[0][0];	
		L.n();
		for (int k = N; --k >= 0; ) {
			for (int j = N; --j >= 0; ) {
				L.l(sb[k][j]);
				Assert.EQUALS(maxNum, sb[k][j]);
			}
			L.n();
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) { //throws java.io.IOException {
		L.enter().n();
		testRandomQuasi2D();
		testRandomQuasi1D();
		final RandomVectorQuasi SR = new RandomVectorQuasi(2); 
		for (int i=0; i<32; i++) {
			final int[] x = SR.randomInt();
			L.n().l(i).l(x[0]).l(x[1]); //.l(x[2]);
		}
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}
