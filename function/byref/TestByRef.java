package function.byref;

//import java.io.InputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.PrintStream;
import java.io.IOException;

/**Tests all Methods in the ByRef Package.  
 * Also contains Test Data for most Functions to save reading them from Files. 
 * (already in Java Context available to any Java Tester.
 */
public class TestByRef {

	/**Values of the Gaussian Error Function in pairs: {x, ErrFc(x)}	 */
	final static public float[][]
		ValuesErrFc = {{0.0f, 0.0000000f},
					   {0.1f, 0.1124629f},
					   {0.2f, 0.2227026f},
					   {0.3f, 0.3286268f},
					   {0.4f, 0.4283924f},
					   {0.5f, 0.5204999f},
					   {0.6f, 0.6038561f},
					   {0.7f, 0.6778012f},
					   {0.8f, 0.7421010f},
					   {0.9f, 0.7969082f},
					   {1.0f, 0.8427008f},
					   {1.1f, 0.8802051f},
					   {1.2f, 0.9103140f},
					   {1.3f, 0.9340079f},
					   {1.4f, 0.9522850f},
					   {1.5f, 0.9661051f},
					   {1.6f, 0.9763484f},
					   {1.7f, 0.9837905f},
					   {1.8f, 0.9890905f},
					   {1.9f, 0.9927904f}};

	/**Values of the Cosinus, Sinus, Exponential Integral: {x, CI(x), SI(x), EI(x)}	 */
	final static public float[][]
		ValuesCISIEI = {
			{ 0.1f, -1.727868f,	0.09994446f, -1.62281281396931f},
			{ 0.2f, -1.042206f,	0.1995561f, -0.822f},
			{ 0.5f, -0.177784078806617f,  0.493107418043065f, +0.454219904862013f},
			{ 0.6f, -2.227071E-02f,	0.5881288f, 0.77f},
			{ 0.7f,	0.1005147f,	0.6812222f, 1.065f},
			{ 1.1f,	+0.384873388424633f,  +1.02868521867395f, 2.16737828956006f},
			{ 1.8f,	0.4568111f,	1.505817f, 4.25f},
			{ 1.9f,	0.4419403f,	1.557775f, 4.594f},
			{ 2.0f,	0.4229808f,	1.605413f, 4.954f},
			{ 2.1f,	0.4005120f,	1.648699f, 5.333f},
			{ 2.2f,	0.3750746f,	1.687625f, 5.733f},

			{ 3.3f,	0.02467829f,	1.848081f,12.161f},
			{ 4.0f,	-0.141f,		1.758f,		 19.631f},
			{ 5.0f,	-0.19f,			1.55f,		 40.185f},
			{ 6.0f,	-0.068f,		1.425f,		 85.990f},
			{ 6.3f, -1.988822E-02f,  1.418174f, 108.916f},
			{ 6.4f, -4.181411E-03f,  1.419223f, 117.935f},
			{ 6.5f,  1.110152E-02f,  1.421794f, 127.747f},
			{ 6.6f,  2.582314E-02f,  1.425816f, 138.426f},
			{10.0f, -4.545644E-02f,  1.658348f, 2492.229f},
			{11.0f, -0.0895631354956395f,  +1.57830680694642f, 6071.40637383996f},
			{12.5f, -1.140835E-02f,  1.492337f, 23565.118f},
			{15.0f,  4.627868E-02f,  1.618194f, 234955.852f}};

	/**Compares the Performance of direct Field Access
	 * with public Methods
	 * It shows that the getValue() Method is optimized to direct Field Access by the JIT
	 * not the javac Compiler, but only because ByRefInt is declared final.
	 * Otherwise the Method Call is about 8 to 10 times slower!!!
	 */
	public static void testDirectAccess() throws IOException {
		int res = 0;
		ByRefInt A = new ByRefInt(3);
		ByRefInt B = new ByRefInt(2);
		System.in.read ();
		System.out.println ("Starting first Loop" +  new java.util.Date()); //it shows that getInt() is optimized an inlined to direct Access.
		int i =  10000; while (--i >= 0) { //real Operations are proportional to the # Operations
		int j = 100000; while (--j >= 0) { //+=,&= 75sec, pure Assignment is optimized away by the Compiler = 5sec
			res &= A.Value; // + B.Value; //=> getField
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
			res &= A.Value; // + B.Value;
			res &= B.Value; // + B.Value;
		}}
//		ICountAble C = (ICountAble) A;
//		ICountAble D = (ICountAble) B;
		ByRefInt C = A;
		ByRefInt D = B;
		System.out.println ("Finished first Loop " +  res + new java.util.Date());
			i =  10000; while (--i >= 0) {
		int j = 100000; while (--j >= 0) { //12 Minutes instead of 75 Seconds!!! Factor of 9,6 for the Call when using the Interface instead of the concrete Class
			res &= C.getInt (); // + D.getInt (); //=> invokevirtual()
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
			res &= C.getInt (); // + B.getInt ();
			res &= D.getInt (); // + B.getInt ();
		}}
		System.out.println ("Finished second Loop " +  res + new java.util.Date());
		System.in.read ();
		Exception e = new RuntimeException();
		e.fillInStackTrace();
	}

	/**What happens if an Exception happens in a finally clause?
	 * It is raised to the caller!
	 * So you should surround every Operation in a finally Clause with
	 * try{...}catch(Exception e){}
	 */
	public static void testExceptions() throws IOException{
		try{ throw new IOException();
		}catch (IOException e) {
//			throw e;
		}finally {
			//throw new IOException(); //Java 1.4 warns you about this! 
		}
	}

	/**Main Method, Entry Point for the Control	 */
	public static void main(String[] args)
	throws InstantiationException, ClassNotFoundException, IOException, IllegalAccessException, NoSuchFieldException  {
		System.out.println("Demonstrating that exceeding the Range does not raise an Exception in Java!");
		System.out.println((byte) 234526.23462);
		System.out.println((int) 22346234526.23462);
		System.out.println((short) 23456234626.23462);
		System.out.println((long) 234533534534526.23462);
		System.out.println("Hallo");
		testExceptions();
		testDirectAccess();
//		testCopyAt();
//		testSerial();
//		ByRefLong tmp = new ByRefLong(5);
//		ByteArrayOutputStream BS = new ByteArrayOutputStream();
//		test1 tst = new test3();
/*		ObjectOutputStream OS = new ObjectOutputStream(BS);
		OS.writeObject(tst);
		System.out.println(BS.toString());
*/		//StringBuffer SB = new StringBuffer();
//		StringBufferInputStream SB = new StringBufferInputStream();

//		Factorial.testIt();
//		DblFactorial.testIt();
//		Bernoulli.testIt();
	}

}
