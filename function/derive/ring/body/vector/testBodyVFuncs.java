package function.derive.ring.body.vector;

import streamIO.copy.group.ring.metric.body.BodyDouble;
import function.ICountAble;
import function.IFunction;
import function.derive.ring.Algebra;
import function.derive.ring.Diff;
import function.derive.ring.Inv;
import function.derive.ring.Prod;
import function.derive.ring.Quot;
import function.derive.ring.Sum;
import function.derive.ring.body.Sinus;

/**Tests the Methods of the Package BodyFuncs
 * This class can take a variable number of parameters on the command
 * line. Program execution begins with the main() method. The class
 * constructor is not invoked unless an object of type 'Class1'
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: a32f6471d3d5bb15d4d766c6c05b666248b80f41d1ac54a3286cc6661af5543a
 * stale: false
 * tags: [code/entry_point_code/console_output_code/test]
 * concepts: [Test Harness]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * created in the main() method. */
public class testBodyVFuncs {

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * <!-- docstate
	 * tags: [code/testing]
	 * concepts: [Demo Entry Point]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 * via the command line.	 */
	public static void main(String[] args) {	//Testing Zero, One and Two
		Algebra[] a = new Algebra[2];
		a[0] = new Algebra(new BodyDouble(ICountAble.ZERO));
		a[1] = new Algebra(new BodyDouble(ICountAble.ONE ));
		System.out.println(new Algebra(new BodyDouble(ICountAble.TWO)).getFloat());
		IFunction f;
		int i = -1;
		while(++i <= 1)	{
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
/*		IInvertAble invrt;
		invrt = new MulAt(new Body.BodyDouble(2));	System.out.println("invrt = " + invrt);
//		invrt = Identity.getSingleton();
		invrt = invrt.getInverse();				System.out.println("invrt = " + invrt);
		invrt = new CatDerive(Succ  .Succ,
								 Square.Square);System.out.println("f (x) = " + invrt);
		invrt = invrt.getInverse();				System.out.println("invrt = " + invrt);
*/

		//Test the Derivation capabilities.
		//now returns deriveAble instead of IPartialDerive,
		//becauce the Derivatives of Dimension don't implement IPartialDerive
		//but that is not necessary,
		//because Dimension.Derivative(deriveAble f, int Dim) can be used.
//		IPartialDerive P = new CatPartial(Exponential.Exponential,
		IPartialDerive P = new CatPartial(Sinus.SINUS,
						  new ProdPartial(new Dimension(0),
										  new Dimension(1)));	//P = x*y;
		System.out.println (P);
		System.out.println (P.getDerivative(0));
		System.out.println (P.getDerivative(1));

		Algebra A;
//		A = new Algebra(new CatDerive(new fInverse(), new Sinus()));	System.out.println("A = " + A);
		A = new Algebra(Inv  .INV  );	System.out.println("A  = " + A);
//		A = new Algebra(Sinus.Sinus);	System.out.println("A  = " + A);
		A = (Algebra) A.getDerivative();	System.out.println("A' = " + A);
		A = (Algebra) A.getDerivative();	System.out.println("A''= " + A);
		A.simplify();						System.out.println("A''= " + A);
		A.simplify();						System.out.println("A''= " + A);
		A = (Algebra) A.getDerivative();	System.out.println("A' = " + A);
		A.simplify();						System.out.println("A' = " + A);
		A = (Algebra) A.getDerivative();	System.out.println("A' = " + A);
		A.simplify();						System.out.println("A' = " + A);
		A = (Algebra) A.getDerivative();	System.out.println("A' = " + A);
		//Test the Parsing capabilities

		//Test the Integration capabilities.
		//TODO: Create Bridge ...
		//		from Sqr SqRt and 2* and 1/2* and Identity to Polynom
		//		from Succ and Pred to AddAt
		//		Concatenate and multiply Polynoms
	}

}
