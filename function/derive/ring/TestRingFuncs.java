package function.derive.ring;

//import Stream.Copy.*;
import function.IFunction;
import function.IInvertAble;
import function.derive.CCountAble;
import function.derive.IDeriveAble;

/**Tests the Methods of the Package BodyFuncs
 * This class can take a variable number of parameters on the command
 * line. Program execution begins with the main() method. The class
 * constructor is not invoked unless an object of type 'Class1'
 * created in the main() method. */
public class TestRingFuncs {

	public static void testInstantiation() {
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws java.io.IOException {
		FuncParser.testIt();
		//Testing Zero, One and Two
		Algebra[] a = new Algebra[2];
		a[0] = new Algebra(CCountAble.Zero);
		a[1] = new Algebra(CCountAble.One);
		System.out.println(CCountAble.Two.getFloat());

		IFunction f;
		int i = -1;
		while(++i <= 1) {
			System.out.println("a[i] =		"	+ a[i].getFloat());
			System.out.println("a[i] = 0?	"	+ a[i].isZero());
			System.out.println("a[i] = 1?	"	+ a[i].isOne ());
			int j = -1;
			while(++j <= 1) {
				f = new Sum (a[i], a[j]);System.out.println("f = " + f);
				f = f.simplify();		 System.out.println("f = " + f);
				f = new Prod(a[i], a[j]);System.out.println("f = " + f);
				f = f.simplify();		 System.out.println("f = " + f);
				f = new Diff(a[i], a[j]);System.out.println("f = " + f);
				f = f.simplify();		 System.out.println("f = " + f);
				f = new Quot
					        (a[i], a[j]);System.out.println("f = " + f);
				f = f.simplify();		 System.out.println("f = " + f);
			}
		}

		//Test the Inversion capabilities.
		IInvertAble invrt;
		invrt = new MulAt(CCountAble.Two);	System.out.println("invrt = " + invrt);
//TODO		invrt = new MulAt(new Body.BodyDouble(2));	System.out.println("invrt = " + invrt);
//		invrt = Identity.getSingleton();
		invrt = invrt.getInverse();		System.out.println("invrt = " + invrt);
		invrt = new CatDerive(Succ  .SUCC,
							  Square.SQUARE);System.out.println("f (x) = " + invrt);
		invrt = invrt.getInverse();		System.out.println("invrt = " + invrt);

		//Test the Derivation capabilities.
		invrt = ((IDeriveAble) invrt).getDerivative();
										System.out.println("f'(x) = " + invrt);
		Algebra A;
//		A = new Algebra(new CatDerive(new fInverse(), new Sinus()));	System.out.println("A = " + A);
		A = new Algebra(Inv  .INV  );	System.out.println("A  = " + A);
//		A = new Algebra(Sinus.Sinus);	System.out.println("A  = " + A);
		A = (Algebra) A.getDerivative();System.out.println("A' = " + A);
		A = (Algebra) A.getDerivative();System.out.println("A''= " + A);
		A.simplify();					System.out.println("A''= " + A);
		A.simplify();					System.out.println("A''= " + A);
		A = (Algebra) A.getDerivative();System.out.println("A' = " + A);
		A.simplify();					System.out.println("A' = " + A);
		A = (Algebra) A.getDerivative();System.out.println("A' = " + A);
		A.simplify();					System.out.println("A' = " + A);
		A = (Algebra) A.getDerivative();System.out.println("A' = " + A);
		//Test the Parsing capabilities

		//Test the Integration capabilities.

	}
}
