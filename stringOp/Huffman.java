package stringOp;

import streamIO.Assert;
import streamIO.Log;
import function.byref.ByRefInt;

/** Creates the Huffman Encoding for a List of Character Counts.
  * To derive the Counts, loop over the Text or use an appropripate
  * Standard Frequency Table for the Language used.
  * 
  * Huffman Encoding can considerably compress Streams. 
  * Another Standard Application in Fax Transmission and GIF Pictures is 
  * to Huffman-encode Run-Length encoded Picture Information.  
  * 
  * Of course the Code also needs Memory and has to be transferred,
  * so the real Benefit only happens, when you have large Texts
  * or use a Standard Encoding based on the Frequency of Characters
  * in the Language.
  * 
  * The Huffman Code can compress a Character into up to a single Bit and thus
  * compress Data up to a Factor of the Number of Bits in the Character.
  * This is a Factor of 8 (12.5%) for Bytes (8 Bit) or 16 (6,25%) for Unicode Characters 
  * and will only be reached if the Text consists of only one Character.
  * With two Characters that appear equally frequent, the Factor
  * reduces to 5 (18.25%) for Bytes (8 Bit).
  * 
  * It doesn't save Space if the Frequencies of all Characters are alike
  * or e.g. the Frequency of the most likely Character is only double the one
  * of the most unlikely Character 
  * or e.g. nearly all Characters are used throughout the Document. 
  * 
  * The Notion of Entropy is important here, 
  * because the Entropy of a Frequency Table h[i] is defined as the Sum: 
  * H = sum(i, h[i]*log(h[i])) 
  * It becomes maximum when all Frequencies are the same: h[i] = 1/N
  * and minimum when only one Frequency is 1 and all others 0.  
  * Huffman-encoding directly returns a Result in the Size of the Stream's Entropy, 
  * it thus maximizes the Result's Entropy. 
  * @see math.vector.VectorInt#ENTROPY(int[]) 
  * 
  * Some Facts about Compression: 
  * Compression is not feasible for all Kinds of Documents. 
  * D <-> D'=Compress(D) must be a bijective Operation, because
  * a) ALL Documents D should be compressable 
  * b) D must be recoverable from D' 
  * Now the Number of possible Documents D is a Function of it's Length: N = b^L
  * If all Documents were compressible at least to Length L' less than L, 
  * only N'=b^L' compressed Documents would be possible, which would disallow Bijectivity.   
  * So there must be a Set of Documents, which become larger on Compression. 
  * 
  * The same Compression Algorithm can typically not be applied iteratively, 
  * otherwise it could compress Data infinitely. 
  * But the Combination of different Algorithms works fine e.g. for GIF Images. 
  * 
  * Apart from this Example of a fixed Word Size, variable Length Input Compressor 
  * also variable Length Input, fixed Output Size Code like Ziv-Lempel is used. 
  * 
  * Why Compression is applicable: 
  * Structured Data is typically NOT stored in compressed Format. 
  * Instead it is stored explicitly, often in a Format 
  * that makes it easier for the Processor to work on it. 
  * The Processor can be a Machine (Processor with 2, 16, 32 or 64 Bit Word Length) 
  * or a Human being (then it is typically written Language with Characters & Words). 
  * The Document represents a Network of Ideas or Commands 
  * and thus references these same Entities over and over again. 
  * This results in Redundancy at least of Object References which allows for Compression. 
  * 
  * The Nature of all-purpose Languages is it's Simplicity resulting from a Structure 
  * which allows the Composition of fewer, smaller Elements to larger Entities. 
  * (Bits to Characters, Characters to Words, Words to Sentences, Sentences to Paragraphs, 
  *  Paragraphs to Documents etc.) 
  * This Simplicity and the inner Network both result in Inefficiencies. 
  * The Difference is similar to that of a designed Machine and an evolved Organism. 
  * 
  * A Word with n Bits allows to represent up to 2^n States (which are typically not exhausted), 
  * but the Use of m Words typically does not result in the Usage of all 2^(m*n) Possibilities. 
  * The Number of Alternatives grows exponentially with the Length, an is thus never exhausted. 
  * 
  * Interestingly the higher the Information Density in a Document or State, 
  * the less useful it is for Generalization and Learning. 
  * If no Rule or Pattern can be found, you need the full Document to recover it, 
  * but these States are typically not very useful, since they are only Noise 
  * and in thermodynamic Equilibrium. 
  * 
  * Entropy is extensive / additive: 
  * For two independent random Variables their Entropies are defined as: 
  * Hp = Sum(i, p[i]*log(p[i])) 
  * Hq = Sum(j, q[j]*log(q[j])) 
  * The combined Entropy is: 
  * Hpq = Sum(i,j, p[i]*        q[j]* log(p[i] *    q[j])) 
  *     = Sum(i,   p[i]* Sum(j, q[j]*(log(p[i])+log(q[j]))))
  *     = Sum(i,   p[i]*(Sum(j, q[j]* log(p[i]))
  *                     +Sum(j, q[j]* log(q[j]))))
  *     = Sum(i,   p[i]*(log(p[i])*Sum(j, q[j]) + Hq))
  *     = Sum(i,   p[i]*(log(p[i])*1            + Hq))
  *     = Sum(i,   p[i]* log(p[i])
  *      +Sum(i,   p[i]* Hq))
  *     = Hp + Hq*Sum(i,   p[i])) = Hp+Hq*1 
  *     = Hp + Hq
  * 
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:32Z
  * digest: 06b651cdbe76e91d15a0e7de45fb83e6732b5821e5654cf14207917e4de6ee27
  * stale: false
  * tags: [code/compression]
  * concepts: [Huffman Coding]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class Huffman {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(Huffman.class, -0);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// static Methods	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Creates the Trie as an Array of outer (at most first Max_Char)
	 * and inner Nodes (after MaxChar)
	 * with each int pointing to the Index of it's parent.
	 * Negative Values indicate a right Child, positive Values a left Child,
	 * 0 is the Root of the Trie.
	 * It uses a greedy Algorithm to construct the Trie by creating a new Node
	 * with minimum Depth from the two Nodes with least Count.
	 * This Strategy of a local Minimum also leads to a global Minimum.
	 */
	final static public int[][] createTrie(final ByRefInt[] counts, final int maxChar) {
		final int dad [][] = new int [maxChar+maxChar][3]; //
		final HeapByIndex heap = new HeapByIndex(counts, maxChar);	//Use the full Array, saves copy step, but creates a longer Heap
		do{	//get the two smallest Items and replace them by their Sum as a Trie
			final int MN = maxChar + heap.length-2;
			final int x = heap.get();	//up to here you can use for both Implementations...
			final int y = heap.p[1];	//this one saves one heapify() Operation by combining an Insert with a Remove = Replace
			final int c =   ((ByRefInt)heap.a[x]).Value + //Count of the Parent
							((ByRefInt)heap.a[y]).Value;	// = Sum of counts of 2 Children
			if (c < 0)	//not necessary anymore...
			{	//only Nodes with Count > 0 are added to the Heap
				heap.a[MN] = counts[MN] = new ByRefInt(c);
				final int dx = dad[x][0] = heap.p[1] = MN;
				final int dy = dad[y][0] = -dx; //Signs to distinguish left and right Children!
				if (dx > 0)	{  dad[ dx][1] = x; dad[ dx][2] = y;
				} else {       dad[-dx][2] = x; dad[-dx][1] = y; } //now set the Children accordingly
				//L.l(dy);
				heap.downHeap(1);	//simulates an Insert combined with a Remove <=> Replace
				//heap.replace()
			}
			/*
			int y = heap.get();	//Implementation using a generic indirect Heap.
			heap.insert(counts[MN] = new ByRefInt
								  (((ByRefInt)heap.a[x]).Value +
								   ((ByRefInt)heap.a[y]).Value), MN);
			dad[y][0] = -(dad[x][0] = MN);	//negative Sign to distinguish left and right Children!
			*/
		} while (!heap.isZero());
		dad[maxChar][0] = 0;	//Now the Heap is no longer needed
		return dad;	
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Member Variables	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Defines the maximum Character of the Alphabet.	 */
	protected int maxChar = Character.MAX_VALUE;

	/**Defines the Offset of the Alphabet.	 */
	protected int offset = Character.MAX_VALUE;

    /**Trie stored as Links from the Child to the Parent Node AND vice Versa
     * The lower Part (0..Max_Char-1) is used as Heap,
     * the upper Part (Max_Char..2*Max_Char-1) stores the Trie
     * with trie[Max_Char] as the Root:
     * trie[x][0]   stores the Parent pointer
     * trie[x][1/2] store  the left and right Child pointer
     */
    int[][] trie;

	/**Codes of the Characters with Offset as binary Numbers.	 */
	int[] code; //= new int	 [Max_Char];

	/**Lengths of the Codes in Bits to make conding faster	 */
	int[] len;  //= new int	 [Max_Char];

	/////////////////////////////////////////////////////////////////////////////////////
	// Constructors	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Generates the Statistics of the Characters
	 * for Huffman Encoding of the given String	 */
	public Huffman(final String a, final int maxChar_, final int offset_) {
		this.offset = offset_;
		this.maxChar = maxChar_;
		code = new int [maxChar_];
		len  = new int [maxChar_];
		ByRefInt[] count = new ByRefInt[maxChar_+maxChar_]; //Frequencies
		int i;	//Initialize the Counter
		i = maxChar_;   while (--i >= 0)   count[i] = new ByRefInt(0); //0;	//not necessary in Java!
		i = a.length(); while (--i >= 0) --count[a.charAt(i)- offset_].Value;	//Count the Occurences, but negative, because the Heap looks for the Maximum.
		createCode(trie = createTrie(count, maxChar));
	}

	/**Generates a Huffman Code for the given Statistics
	 * To apply the Code to unknown Text, all Counts should be greater than 0,
	 * otherwise the Character is not encoded, but skipped.
	 * To achieve this, divide all Probabilites by the least one and round UP.	 */
	public Huffman(final ByRefInt[] counts, final int maxChar_, final int offset_) {
		this.offset = offset_;
		this.maxChar = maxChar_;
		createCode(createTrie(counts, maxChar));
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Methods	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Now comes the part where the Trie structure of the greedy Algorithm
	 * is evaluated...
	 * The Trie Representation guarantees that there are no two Codes
	 * where the Start of one is the same as the Code of the second,
	 * so there is no Ambiguity as how to interpret a Bit Series.
	 * Thus you save the Separator (Delimiter) Bits.
	 *
	 * The 'count' Parameter is not really necessary,
	 * it only allows for a faster, because smaller Heap in creating the Trie,
	 * by skipping empty Entries.	 */
	public void createCode(final int[][] dad) {
		for (int k = maxChar; --k >= 0; ) {//Character k is in dad[k]
			//you can also reuse dad[], if it is initialized with 0s
			//if (count[k].Value == 0) {code[k] = 0; len[k] = 0;} else
			int c = 0; int l = 0;
			for (int j = 1, d = k //walk up the Trie
			; 0 != (d = dad[d][0]); ){ //0 is the Root; uses binary Horner Scheme
				if (d < 0) {
					c+=j; d = -d; }	//adding a right Child (1)
				j += j; ++l;	//moving up a Level, i.e. adding a Bit.
			} //how do you walk down the Trie?
			code[k] = c; len[k] = l;	//Stores Length and actual Code
		}
	}

	/**Encodes the given String by the Huffman code generated from the Counts	 */
	public String enCode(final String a) {	//Giving out the Code.
		int c,l,j = -1;
		final StringBuffer B = new StringBuffer();
		while(++j < a.length())	{
			c = a.charAt(j)-offset;
			l = len[c];
			while (--l >= 0)
				B.append((char)('0' + ((code[c] >> l) & 1)));
		} return B.toString(); }

	/**Decodes the given String by the Huffman code generated from the Counts	 */
	public String deCode(final String a) {	//Giving out the Code.
		final StringBuffer B = new StringBuffer();
        for (int i = -1; i < a.length()-1; ) {
            int cp = maxChar;
            do { //read at least 1 Bit!
                cp = trie[cp][1 + a.charAt(++i) - '0'];
            } while (cp >= maxChar);
			B.append((char)(cp + offset));
		} 
		return B.toString(); }
		
	/////////////////////////////////////////////////////////////////////////////////////
	// Testing and main() Methods	
	/////////////////////////////////////////////////////////////////////////////////////
	
	static final String EXAMPLE = "A_SIMPLE_STRING_TO_BE_ENCODED_USING_A_MINIMAL_NUMBER_OF_BITS"; 
	static final String EXPECTED = "001011110000101010011101001011011111000011010110010110001111111011000111110001110111111011100100110100110000110100001111011110000101100011111110010111101001011000101010001010010111110010111101000011101101101110011100111111000101001101000"; 
	
	/**Tests all Methods of this Class	 */
	public static void testIt()	{
		L.n("Testing Huffman Encoding");
		testHuffman(EXAMPLE, 237, EXPECTED);
		testHuffman("ABAABBBA",12, null);	//only 2 Characters, should compress by a Factor of eight, i.e. to 1 Byte
		testHuffman("AAAAAAAA", 8, null);	//only 1 Character, can only compress by a Factor of eight, i.e. to 1 Byte
	}

	private static void testHuffman(final String test, final int numBytes, final String expected) {
		L.n("Test String: ").l(test);
		Huffman H = new Huffman(test, '_'-'A'+1, 'A');
		final String encoded = H.enCode(test   ); L.n("encoded: ").l(encoded);
		final String decoded = H.deCode(encoded); L.n("decoded: ").l(decoded);
		//cannot use a BitSet (no Conversion to int[]), nor an int (limited Size) 
		Assert.EQUALS(test, decoded); 
		Assert.EQUALS(numBytes, encoded.length(), "encoded.length"); 
		if (null != expected)
			Assert.EQUALS(encoded, expected); 
		L.n("Length before:").l(8*test.length()).l("	Length after:").l(encoded.length());
	}
	
	/**
	 *The main entry point for the application.
	 * @param args Array of parameters passed to the application via the command line.
	 */
	public static void main(final String[] args) { //throws java.io.IOException {
		testIt();
	}
    
}
